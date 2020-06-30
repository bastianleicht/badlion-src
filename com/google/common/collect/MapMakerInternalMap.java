package com.google.common.collect;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import com.google.common.collect.AbstractMapEntry;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ForwardingConcurrentMap;
import com.google.common.collect.GenericMapMaker;
import com.google.common.collect.Iterators;
import com.google.common.collect.MapMaker;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

class MapMakerInternalMap extends AbstractMap implements ConcurrentMap, Serializable {
   static final int MAXIMUM_CAPACITY = 1073741824;
   static final int MAX_SEGMENTS = 65536;
   static final int CONTAINS_VALUE_RETRIES = 3;
   static final int DRAIN_THRESHOLD = 63;
   static final int DRAIN_MAX = 16;
   static final long CLEANUP_EXECUTOR_DELAY_SECS = 60L;
   private static final Logger logger = Logger.getLogger(MapMakerInternalMap.class.getName());
   final transient int segmentMask;
   final transient int segmentShift;
   final transient MapMakerInternalMap.Segment[] segments;
   final int concurrencyLevel;
   final Equivalence keyEquivalence;
   final Equivalence valueEquivalence;
   final MapMakerInternalMap.Strength keyStrength;
   final MapMakerInternalMap.Strength valueStrength;
   final int maximumSize;
   final long expireAfterAccessNanos;
   final long expireAfterWriteNanos;
   final Queue removalNotificationQueue;
   final MapMaker.RemovalListener removalListener;
   final transient MapMakerInternalMap.EntryFactory entryFactory;
   final Ticker ticker;
   static final MapMakerInternalMap.ValueReference UNSET = new MapMakerInternalMap.ValueReference() {
      public Object get() {
         return null;
      }

      public MapMakerInternalMap.ReferenceEntry getEntry() {
         return null;
      }

      public MapMakerInternalMap.ValueReference copyFor(ReferenceQueue queue, @Nullable Object value, MapMakerInternalMap.ReferenceEntry entry) {
         return this;
      }

      public boolean isComputingReference() {
         return false;
      }

      public Object waitForValue() {
         return null;
      }

      public void clear(MapMakerInternalMap.ValueReference newValue) {
      }
   };
   static final Queue DISCARDING_QUEUE = new AbstractQueue() {
      public boolean offer(Object o) {
         return true;
      }

      public Object peek() {
         return null;
      }

      public Object poll() {
         return null;
      }

      public int size() {
         return 0;
      }

      public Iterator iterator() {
         return Iterators.emptyIterator();
      }
   };
   transient Set keySet;
   transient Collection values;
   transient Set entrySet;
   private static final long serialVersionUID = 5L;

   MapMakerInternalMap(MapMaker builder) {
      this.concurrencyLevel = Math.min(builder.getConcurrencyLevel(), 65536);
      this.keyStrength = builder.getKeyStrength();
      this.valueStrength = builder.getValueStrength();
      this.keyEquivalence = builder.getKeyEquivalence();
      this.valueEquivalence = this.valueStrength.defaultEquivalence();
      this.maximumSize = builder.maximumSize;
      this.expireAfterAccessNanos = builder.getExpireAfterAccessNanos();
      this.expireAfterWriteNanos = builder.getExpireAfterWriteNanos();
      this.entryFactory = MapMakerInternalMap.EntryFactory.getFactory(this.keyStrength, this.expires(), this.evictsBySize());
      this.ticker = builder.getTicker();
      this.removalListener = builder.getRemovalListener();
      this.removalNotificationQueue = (Queue)(this.removalListener == GenericMapMaker.NullListener.INSTANCE?discardingQueue():new ConcurrentLinkedQueue());
      int initialCapacity = Math.min(builder.getInitialCapacity(), 1073741824);
      if(this.evictsBySize()) {
         initialCapacity = Math.min(initialCapacity, this.maximumSize);
      }

      int segmentShift = 0;

      int segmentCount;
      for(segmentCount = 1; segmentCount < this.concurrencyLevel && (!this.evictsBySize() || segmentCount * 2 <= this.maximumSize); segmentCount <<= 1) {
         ++segmentShift;
      }

      this.segmentShift = 32 - segmentShift;
      this.segmentMask = segmentCount - 1;
      this.segments = this.newSegmentArray(segmentCount);
      int segmentCapacity = initialCapacity / segmentCount;
      if(segmentCapacity * segmentCount < initialCapacity) {
         ++segmentCapacity;
      }

      int segmentSize;
      for(segmentSize = 1; segmentSize < segmentCapacity; segmentSize <<= 1) {
         ;
      }

      if(this.evictsBySize()) {
         int maximumSegmentSize = this.maximumSize / segmentCount + 1;
         int remainder = this.maximumSize % segmentCount;

         for(int i = 0; i < this.segments.length; ++i) {
            if(i == remainder) {
               --maximumSegmentSize;
            }

            this.segments[i] = this.createSegment(segmentSize, maximumSegmentSize);
         }
      } else {
         for(int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = this.createSegment(segmentSize, -1);
         }
      }

   }

   boolean evictsBySize() {
      return this.maximumSize != -1;
   }

   boolean expires() {
      return this.expiresAfterWrite() || this.expiresAfterAccess();
   }

   boolean expiresAfterWrite() {
      return this.expireAfterWriteNanos > 0L;
   }

   boolean expiresAfterAccess() {
      return this.expireAfterAccessNanos > 0L;
   }

   boolean usesKeyReferences() {
      return this.keyStrength != MapMakerInternalMap.Strength.STRONG;
   }

   boolean usesValueReferences() {
      return this.valueStrength != MapMakerInternalMap.Strength.STRONG;
   }

   static MapMakerInternalMap.ValueReference unset() {
      return UNSET;
   }

   static MapMakerInternalMap.ReferenceEntry nullEntry() {
      return MapMakerInternalMap.NullEntry.INSTANCE;
   }

   static Queue discardingQueue() {
      return DISCARDING_QUEUE;
   }

   static int rehash(int h) {
      h = h + (h << 15 ^ -12931);
      h = h ^ h >>> 10;
      h = h + (h << 3);
      h = h ^ h >>> 6;
      h = h + (h << 2) + (h << 14);
      return h ^ h >>> 16;
   }

   @GuardedBy("Segment.this")
   @VisibleForTesting
   MapMakerInternalMap.ReferenceEntry newEntry(Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
      return this.segmentFor(hash).newEntry(key, hash, next);
   }

   @GuardedBy("Segment.this")
   @VisibleForTesting
   MapMakerInternalMap.ReferenceEntry copyEntry(MapMakerInternalMap.ReferenceEntry original, MapMakerInternalMap.ReferenceEntry newNext) {
      int hash = original.getHash();
      return this.segmentFor(hash).copyEntry(original, newNext);
   }

   @GuardedBy("Segment.this")
   @VisibleForTesting
   MapMakerInternalMap.ValueReference newValueReference(MapMakerInternalMap.ReferenceEntry entry, Object value) {
      int hash = entry.getHash();
      return this.valueStrength.referenceValue(this.segmentFor(hash), entry, value);
   }

   int hash(Object key) {
      int h = this.keyEquivalence.hash(key);
      return rehash(h);
   }

   void reclaimValue(MapMakerInternalMap.ValueReference valueReference) {
      MapMakerInternalMap.ReferenceEntry<K, V> entry = valueReference.getEntry();
      int hash = entry.getHash();
      this.segmentFor(hash).reclaimValue(entry.getKey(), hash, valueReference);
   }

   void reclaimKey(MapMakerInternalMap.ReferenceEntry entry) {
      int hash = entry.getHash();
      this.segmentFor(hash).reclaimKey(entry, hash);
   }

   @VisibleForTesting
   boolean isLive(MapMakerInternalMap.ReferenceEntry entry) {
      return this.segmentFor(entry.getHash()).getLiveValue(entry) != null;
   }

   MapMakerInternalMap.Segment segmentFor(int hash) {
      return this.segments[hash >>> this.segmentShift & this.segmentMask];
   }

   MapMakerInternalMap.Segment createSegment(int initialCapacity, int maxSegmentSize) {
      return new MapMakerInternalMap.Segment(this, initialCapacity, maxSegmentSize);
   }

   Object getLiveValue(MapMakerInternalMap.ReferenceEntry entry) {
      if(entry.getKey() == null) {
         return null;
      } else {
         V value = entry.getValueReference().get();
         return value == null?null:(this.expires() && this.isExpired(entry)?null:value);
      }
   }

   boolean isExpired(MapMakerInternalMap.ReferenceEntry entry) {
      return this.isExpired(entry, this.ticker.read());
   }

   boolean isExpired(MapMakerInternalMap.ReferenceEntry entry, long now) {
      return now - entry.getExpirationTime() > 0L;
   }

   @GuardedBy("Segment.this")
   static void connectExpirables(MapMakerInternalMap.ReferenceEntry previous, MapMakerInternalMap.ReferenceEntry next) {
      previous.setNextExpirable(next);
      next.setPreviousExpirable(previous);
   }

   @GuardedBy("Segment.this")
   static void nullifyExpirable(MapMakerInternalMap.ReferenceEntry nulled) {
      MapMakerInternalMap.ReferenceEntry<K, V> nullEntry = nullEntry();
      nulled.setNextExpirable(nullEntry);
      nulled.setPreviousExpirable(nullEntry);
   }

   void processPendingNotifications() {
      MapMaker.RemovalNotification<K, V> notification;
      while((notification = (MapMaker.RemovalNotification)this.removalNotificationQueue.poll()) != null) {
         try {
            this.removalListener.onRemoval(notification);
         } catch (Exception var3) {
            logger.log(Level.WARNING, "Exception thrown by removal listener", var3);
         }
      }

   }

   @GuardedBy("Segment.this")
   static void connectEvictables(MapMakerInternalMap.ReferenceEntry previous, MapMakerInternalMap.ReferenceEntry next) {
      previous.setNextEvictable(next);
      next.setPreviousEvictable(previous);
   }

   @GuardedBy("Segment.this")
   static void nullifyEvictable(MapMakerInternalMap.ReferenceEntry nulled) {
      MapMakerInternalMap.ReferenceEntry<K, V> nullEntry = nullEntry();
      nulled.setNextEvictable(nullEntry);
      nulled.setPreviousEvictable(nullEntry);
   }

   final MapMakerInternalMap.Segment[] newSegmentArray(int ssize) {
      return new MapMakerInternalMap.Segment[ssize];
   }

   public boolean isEmpty() {
      long sum = 0L;
      MapMakerInternalMap.Segment<K, V>[] segments = this.segments;

      for(int i = 0; i < segments.length; ++i) {
         if(segments[i].count != 0) {
            return false;
         }

         sum += (long)segments[i].modCount;
      }

      if(sum != 0L) {
         for(int i = 0; i < segments.length; ++i) {
            if(segments[i].count != 0) {
               return false;
            }

            sum -= (long)segments[i].modCount;
         }

         if(sum != 0L) {
            return false;
         }
      }

      return true;
   }

   public int size() {
      MapMakerInternalMap.Segment<K, V>[] segments = this.segments;
      long sum = 0L;

      for(int i = 0; i < segments.length; ++i) {
         sum += (long)segments[i].count;
      }

      return Ints.saturatedCast(sum);
   }

   public Object get(@Nullable Object key) {
      if(key == null) {
         return null;
      } else {
         int hash = this.hash(key);
         return this.segmentFor(hash).get(key, hash);
      }
   }

   MapMakerInternalMap.ReferenceEntry getEntry(@Nullable Object key) {
      if(key == null) {
         return null;
      } else {
         int hash = this.hash(key);
         return this.segmentFor(hash).getEntry(key, hash);
      }
   }

   public boolean containsKey(@Nullable Object key) {
      if(key == null) {
         return false;
      } else {
         int hash = this.hash(key);
         return this.segmentFor(hash).containsKey(key, hash);
      }
   }

   public boolean containsValue(@Nullable Object value) {
      if(value == null) {
         return false;
      } else {
         MapMakerInternalMap.Segment<K, V>[] segments = this.segments;
         long last = -1L;

         for(int i = 0; i < 3; ++i) {
            long sum = 0L;

            for(MapMakerInternalMap.Segment<K, V> segment : segments) {
               int c = segment.count;
               AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table = segment.table;

               for(int j = 0; j < table.length(); ++j) {
                  for(MapMakerInternalMap.ReferenceEntry<K, V> e = (MapMakerInternalMap.ReferenceEntry)table.get(j); e != null; e = e.getNext()) {
                     V v = segment.getLiveValue(e);
                     if(v != null && this.valueEquivalence.equivalent(value, v)) {
                        return true;
                     }
                  }
               }

               sum += (long)segment.modCount;
            }

            if(sum == last) {
               break;
            }

            last = sum;
         }

         return false;
      }
   }

   public Object put(Object key, Object value) {
      Preconditions.checkNotNull(key);
      Preconditions.checkNotNull(value);
      int hash = this.hash(key);
      return this.segmentFor(hash).put(key, hash, value, false);
   }

   public Object putIfAbsent(Object key, Object value) {
      Preconditions.checkNotNull(key);
      Preconditions.checkNotNull(value);
      int hash = this.hash(key);
      return this.segmentFor(hash).put(key, hash, value, true);
   }

   public void putAll(Map m) {
      for(Entry<? extends K, ? extends V> e : m.entrySet()) {
         this.put(e.getKey(), e.getValue());
      }

   }

   public Object remove(@Nullable Object key) {
      if(key == null) {
         return null;
      } else {
         int hash = this.hash(key);
         return this.segmentFor(hash).remove(key, hash);
      }
   }

   public boolean remove(@Nullable Object key, @Nullable Object value) {
      if(key != null && value != null) {
         int hash = this.hash(key);
         return this.segmentFor(hash).remove(key, hash, value);
      } else {
         return false;
      }
   }

   public boolean replace(Object key, @Nullable Object oldValue, Object newValue) {
      Preconditions.checkNotNull(key);
      Preconditions.checkNotNull(newValue);
      if(oldValue == null) {
         return false;
      } else {
         int hash = this.hash(key);
         return this.segmentFor(hash).replace(key, hash, oldValue, newValue);
      }
   }

   public Object replace(Object key, Object value) {
      Preconditions.checkNotNull(key);
      Preconditions.checkNotNull(value);
      int hash = this.hash(key);
      return this.segmentFor(hash).replace(key, hash, value);
   }

   public void clear() {
      for(MapMakerInternalMap.Segment<K, V> segment : this.segments) {
         segment.clear();
      }

   }

   public Set keySet() {
      Set<K> ks = this.keySet;
      return ks != null?ks:(this.keySet = new MapMakerInternalMap.KeySet());
   }

   public Collection values() {
      Collection<V> vs = this.values;
      return vs != null?vs:(this.values = new MapMakerInternalMap.Values());
   }

   public Set entrySet() {
      Set<Entry<K, V>> es = this.entrySet;
      return es != null?es:(this.entrySet = new MapMakerInternalMap.EntrySet());
   }

   Object writeReplace() {
      return new MapMakerInternalMap.SerializationProxy(this.keyStrength, this.valueStrength, this.keyEquivalence, this.valueEquivalence, this.expireAfterWriteNanos, this.expireAfterAccessNanos, this.maximumSize, this.concurrencyLevel, this.removalListener, this);
   }

   abstract static class AbstractReferenceEntry implements MapMakerInternalMap.ReferenceEntry {
      public MapMakerInternalMap.ValueReference getValueReference() {
         throw new UnsupportedOperationException();
      }

      public void setValueReference(MapMakerInternalMap.ValueReference valueReference) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getNext() {
         throw new UnsupportedOperationException();
      }

      public int getHash() {
         throw new UnsupportedOperationException();
      }

      public Object getKey() {
         throw new UnsupportedOperationException();
      }

      public long getExpirationTime() {
         throw new UnsupportedOperationException();
      }

      public void setExpirationTime(long time) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getNextExpirable() {
         throw new UnsupportedOperationException();
      }

      public void setNextExpirable(MapMakerInternalMap.ReferenceEntry next) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousExpirable() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry previous) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getNextEvictable() {
         throw new UnsupportedOperationException();
      }

      public void setNextEvictable(MapMakerInternalMap.ReferenceEntry next) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousEvictable() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry previous) {
         throw new UnsupportedOperationException();
      }
   }

   abstract static class AbstractSerializationProxy extends ForwardingConcurrentMap implements Serializable {
      private static final long serialVersionUID = 3L;
      final MapMakerInternalMap.Strength keyStrength;
      final MapMakerInternalMap.Strength valueStrength;
      final Equivalence keyEquivalence;
      final Equivalence valueEquivalence;
      final long expireAfterWriteNanos;
      final long expireAfterAccessNanos;
      final int maximumSize;
      final int concurrencyLevel;
      final MapMaker.RemovalListener removalListener;
      transient ConcurrentMap delegate;

      AbstractSerializationProxy(MapMakerInternalMap.Strength keyStrength, MapMakerInternalMap.Strength valueStrength, Equivalence keyEquivalence, Equivalence valueEquivalence, long expireAfterWriteNanos, long expireAfterAccessNanos, int maximumSize, int concurrencyLevel, MapMaker.RemovalListener removalListener, ConcurrentMap delegate) {
         this.keyStrength = keyStrength;
         this.valueStrength = valueStrength;
         this.keyEquivalence = keyEquivalence;
         this.valueEquivalence = valueEquivalence;
         this.expireAfterWriteNanos = expireAfterWriteNanos;
         this.expireAfterAccessNanos = expireAfterAccessNanos;
         this.maximumSize = maximumSize;
         this.concurrencyLevel = concurrencyLevel;
         this.removalListener = removalListener;
         this.delegate = delegate;
      }

      protected ConcurrentMap delegate() {
         return this.delegate;
      }

      void writeMapTo(ObjectOutputStream out) throws IOException {
         out.writeInt(this.delegate.size());

         for(Entry<K, V> entry : this.delegate.entrySet()) {
            out.writeObject(entry.getKey());
            out.writeObject(entry.getValue());
         }

         out.writeObject((Object)null);
      }

      MapMaker readMapMaker(ObjectInputStream in) throws IOException {
         int size = in.readInt();
         MapMaker mapMaker = (new MapMaker()).initialCapacity(size).setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).concurrencyLevel(this.concurrencyLevel);
         mapMaker.removalListener(this.removalListener);
         if(this.expireAfterWriteNanos > 0L) {
            mapMaker.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
         }

         if(this.expireAfterAccessNanos > 0L) {
            mapMaker.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
         }

         if(this.maximumSize != -1) {
            mapMaker.maximumSize(this.maximumSize);
         }

         return mapMaker;
      }

      void readEntries(ObjectInputStream in) throws IOException, ClassNotFoundException {
         while(true) {
            K key = in.readObject();
            if(key == null) {
               return;
            }

            V value = in.readObject();
            this.delegate.put(key, value);
         }
      }
   }

   static final class CleanupMapTask implements Runnable {
      final WeakReference mapReference;

      public CleanupMapTask(MapMakerInternalMap map) {
         this.mapReference = new WeakReference(map);
      }

      public void run() {
         MapMakerInternalMap<?, ?> map = (MapMakerInternalMap)this.mapReference.get();
         if(map == null) {
            throw new CancellationException();
         } else {
            for(MapMakerInternalMap.Segment<?, ?> segment : map.segments) {
               segment.runCleanup();
            }

         }
      }
   }

   static enum EntryFactory {
      STRONG {
         MapMakerInternalMap.ReferenceEntry newEntry(MapMakerInternalMap.Segment segment, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
            return new MapMakerInternalMap.StrongEntry(key, hash, next);
         }
      },
      STRONG_EXPIRABLE {
         MapMakerInternalMap.ReferenceEntry newEntry(MapMakerInternalMap.Segment segment, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
            return new MapMakerInternalMap.StrongExpirableEntry(key, hash, next);
         }

         MapMakerInternalMap.ReferenceEntry copyEntry(MapMakerInternalMap.Segment segment, MapMakerInternalMap.ReferenceEntry original, MapMakerInternalMap.ReferenceEntry newNext) {
            MapMakerInternalMap.ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
            this.copyExpirableEntry(original, newEntry);
            return newEntry;
         }
      },
      STRONG_EVICTABLE {
         MapMakerInternalMap.ReferenceEntry newEntry(MapMakerInternalMap.Segment segment, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
            return new MapMakerInternalMap.StrongEvictableEntry(key, hash, next);
         }

         MapMakerInternalMap.ReferenceEntry copyEntry(MapMakerInternalMap.Segment segment, MapMakerInternalMap.ReferenceEntry original, MapMakerInternalMap.ReferenceEntry newNext) {
            MapMakerInternalMap.ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
            this.copyEvictableEntry(original, newEntry);
            return newEntry;
         }
      },
      STRONG_EXPIRABLE_EVICTABLE {
         MapMakerInternalMap.ReferenceEntry newEntry(MapMakerInternalMap.Segment segment, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
            return new MapMakerInternalMap.StrongExpirableEvictableEntry(key, hash, next);
         }

         MapMakerInternalMap.ReferenceEntry copyEntry(MapMakerInternalMap.Segment segment, MapMakerInternalMap.ReferenceEntry original, MapMakerInternalMap.ReferenceEntry newNext) {
            MapMakerInternalMap.ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
            this.copyExpirableEntry(original, newEntry);
            this.copyEvictableEntry(original, newEntry);
            return newEntry;
         }
      },
      WEAK {
         MapMakerInternalMap.ReferenceEntry newEntry(MapMakerInternalMap.Segment segment, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
            return new MapMakerInternalMap.WeakEntry(segment.keyReferenceQueue, key, hash, next);
         }
      },
      WEAK_EXPIRABLE {
         MapMakerInternalMap.ReferenceEntry newEntry(MapMakerInternalMap.Segment segment, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
            return new MapMakerInternalMap.WeakExpirableEntry(segment.keyReferenceQueue, key, hash, next);
         }

         MapMakerInternalMap.ReferenceEntry copyEntry(MapMakerInternalMap.Segment segment, MapMakerInternalMap.ReferenceEntry original, MapMakerInternalMap.ReferenceEntry newNext) {
            MapMakerInternalMap.ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
            this.copyExpirableEntry(original, newEntry);
            return newEntry;
         }
      },
      WEAK_EVICTABLE {
         MapMakerInternalMap.ReferenceEntry newEntry(MapMakerInternalMap.Segment segment, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
            return new MapMakerInternalMap.WeakEvictableEntry(segment.keyReferenceQueue, key, hash, next);
         }

         MapMakerInternalMap.ReferenceEntry copyEntry(MapMakerInternalMap.Segment segment, MapMakerInternalMap.ReferenceEntry original, MapMakerInternalMap.ReferenceEntry newNext) {
            MapMakerInternalMap.ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
            this.copyEvictableEntry(original, newEntry);
            return newEntry;
         }
      },
      WEAK_EXPIRABLE_EVICTABLE {
         MapMakerInternalMap.ReferenceEntry newEntry(MapMakerInternalMap.Segment segment, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
            return new MapMakerInternalMap.WeakExpirableEvictableEntry(segment.keyReferenceQueue, key, hash, next);
         }

         MapMakerInternalMap.ReferenceEntry copyEntry(MapMakerInternalMap.Segment segment, MapMakerInternalMap.ReferenceEntry original, MapMakerInternalMap.ReferenceEntry newNext) {
            MapMakerInternalMap.ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
            this.copyExpirableEntry(original, newEntry);
            this.copyEvictableEntry(original, newEntry);
            return newEntry;
         }
      };

      static final int EXPIRABLE_MASK = 1;
      static final int EVICTABLE_MASK = 2;
      static final MapMakerInternalMap.EntryFactory[][] factories = new MapMakerInternalMap.EntryFactory[][]{{STRONG, STRONG_EXPIRABLE, STRONG_EVICTABLE, STRONG_EXPIRABLE_EVICTABLE}, new MapMakerInternalMap.EntryFactory[0], {WEAK, WEAK_EXPIRABLE, WEAK_EVICTABLE, WEAK_EXPIRABLE_EVICTABLE}};

      private EntryFactory() {
      }

      static MapMakerInternalMap.EntryFactory getFactory(MapMakerInternalMap.Strength keyStrength, boolean expireAfterWrite, boolean evictsBySize) {
         int flags = (expireAfterWrite?1:0) | (evictsBySize?2:0);
         return factories[keyStrength.ordinal()][flags];
      }

      abstract MapMakerInternalMap.ReferenceEntry newEntry(MapMakerInternalMap.Segment var1, Object var2, int var3, @Nullable MapMakerInternalMap.ReferenceEntry var4);

      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry copyEntry(MapMakerInternalMap.Segment segment, MapMakerInternalMap.ReferenceEntry original, MapMakerInternalMap.ReferenceEntry newNext) {
         return this.newEntry(segment, original.getKey(), original.getHash(), newNext);
      }

      @GuardedBy("Segment.this")
      void copyExpirableEntry(MapMakerInternalMap.ReferenceEntry original, MapMakerInternalMap.ReferenceEntry newEntry) {
         newEntry.setExpirationTime(original.getExpirationTime());
         MapMakerInternalMap.connectExpirables(original.getPreviousExpirable(), newEntry);
         MapMakerInternalMap.connectExpirables(newEntry, original.getNextExpirable());
         MapMakerInternalMap.nullifyExpirable(original);
      }

      @GuardedBy("Segment.this")
      void copyEvictableEntry(MapMakerInternalMap.ReferenceEntry original, MapMakerInternalMap.ReferenceEntry newEntry) {
         MapMakerInternalMap.connectEvictables(original.getPreviousEvictable(), newEntry);
         MapMakerInternalMap.connectEvictables(newEntry, original.getNextEvictable());
         MapMakerInternalMap.nullifyEvictable(original);
      }
   }

   final class EntryIterator extends MapMakerInternalMap.HashIterator {
      EntryIterator() {
         super();
      }

      public Entry next() {
         return this.nextEntry();
      }
   }

   final class EntrySet extends AbstractSet {
      public Iterator iterator() {
         return MapMakerInternalMap.this.new EntryIterator();
      }

      public boolean contains(Object o) {
         if(!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry)o;
            Object key = e.getKey();
            if(key == null) {
               return false;
            } else {
               V v = MapMakerInternalMap.this.get(key);
               return v != null && MapMakerInternalMap.this.valueEquivalence.equivalent(e.getValue(), v);
            }
         }
      }

      public boolean remove(Object o) {
         if(!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry)o;
            Object key = e.getKey();
            return key != null && MapMakerInternalMap.this.remove(key, e.getValue());
         }
      }

      public int size() {
         return MapMakerInternalMap.this.size();
      }

      public boolean isEmpty() {
         return MapMakerInternalMap.this.isEmpty();
      }

      public void clear() {
         MapMakerInternalMap.this.clear();
      }
   }

   static final class EvictionQueue extends AbstractQueue {
      final MapMakerInternalMap.ReferenceEntry head = new MapMakerInternalMap.AbstractReferenceEntry() {
         MapMakerInternalMap.ReferenceEntry nextEvictable = this;
         MapMakerInternalMap.ReferenceEntry previousEvictable = this;

         public MapMakerInternalMap.ReferenceEntry getNextEvictable() {
            return this.nextEvictable;
         }

         public void setNextEvictable(MapMakerInternalMap.ReferenceEntry next) {
            this.nextEvictable = next;
         }

         public MapMakerInternalMap.ReferenceEntry getPreviousEvictable() {
            return this.previousEvictable;
         }

         public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry previous) {
            this.previousEvictable = previous;
         }
      };

      public boolean offer(MapMakerInternalMap.ReferenceEntry entry) {
         MapMakerInternalMap.connectEvictables(entry.getPreviousEvictable(), entry.getNextEvictable());
         MapMakerInternalMap.connectEvictables(this.head.getPreviousEvictable(), entry);
         MapMakerInternalMap.connectEvictables(entry, this.head);
         return true;
      }

      public MapMakerInternalMap.ReferenceEntry peek() {
         MapMakerInternalMap.ReferenceEntry<K, V> next = this.head.getNextEvictable();
         return next == this.head?null:next;
      }

      public MapMakerInternalMap.ReferenceEntry poll() {
         MapMakerInternalMap.ReferenceEntry<K, V> next = this.head.getNextEvictable();
         if(next == this.head) {
            return null;
         } else {
            this.remove(next);
            return next;
         }
      }

      public boolean remove(Object o) {
         MapMakerInternalMap.ReferenceEntry<K, V> e = (MapMakerInternalMap.ReferenceEntry)o;
         MapMakerInternalMap.ReferenceEntry<K, V> previous = e.getPreviousEvictable();
         MapMakerInternalMap.ReferenceEntry<K, V> next = e.getNextEvictable();
         MapMakerInternalMap.connectEvictables(previous, next);
         MapMakerInternalMap.nullifyEvictable(e);
         return next != MapMakerInternalMap.NullEntry.INSTANCE;
      }

      public boolean contains(Object o) {
         MapMakerInternalMap.ReferenceEntry<K, V> e = (MapMakerInternalMap.ReferenceEntry)o;
         return e.getNextEvictable() != MapMakerInternalMap.NullEntry.INSTANCE;
      }

      public boolean isEmpty() {
         return this.head.getNextEvictable() == this.head;
      }

      public int size() {
         int size = 0;

         for(MapMakerInternalMap.ReferenceEntry<K, V> e = this.head.getNextEvictable(); e != this.head; e = e.getNextEvictable()) {
            ++size;
         }

         return size;
      }

      public void clear() {
         MapMakerInternalMap.ReferenceEntry<K, V> next;
         for(MapMakerInternalMap.ReferenceEntry<K, V> e = this.head.getNextEvictable(); e != this.head; e = next) {
            next = e.getNextEvictable();
            MapMakerInternalMap.nullifyEvictable(e);
         }

         this.head.setNextEvictable(this.head);
         this.head.setPreviousEvictable(this.head);
      }

      public Iterator iterator() {
         return new AbstractSequentialIterator(this.peek()) {
            protected MapMakerInternalMap.ReferenceEntry computeNext(MapMakerInternalMap.ReferenceEntry previous) {
               MapMakerInternalMap.ReferenceEntry<K, V> next = previous.getNextEvictable();
               return next == EvictionQueue.this.head?null:next;
            }
         };
      }
   }

   static final class ExpirationQueue extends AbstractQueue {
      final MapMakerInternalMap.ReferenceEntry head = new MapMakerInternalMap.AbstractReferenceEntry() {
         MapMakerInternalMap.ReferenceEntry nextExpirable = this;
         MapMakerInternalMap.ReferenceEntry previousExpirable = this;

         public long getExpirationTime() {
            return Long.MAX_VALUE;
         }

         public void setExpirationTime(long time) {
         }

         public MapMakerInternalMap.ReferenceEntry getNextExpirable() {
            return this.nextExpirable;
         }

         public void setNextExpirable(MapMakerInternalMap.ReferenceEntry next) {
            this.nextExpirable = next;
         }

         public MapMakerInternalMap.ReferenceEntry getPreviousExpirable() {
            return this.previousExpirable;
         }

         public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry previous) {
            this.previousExpirable = previous;
         }
      };

      public boolean offer(MapMakerInternalMap.ReferenceEntry entry) {
         MapMakerInternalMap.connectExpirables(entry.getPreviousExpirable(), entry.getNextExpirable());
         MapMakerInternalMap.connectExpirables(this.head.getPreviousExpirable(), entry);
         MapMakerInternalMap.connectExpirables(entry, this.head);
         return true;
      }

      public MapMakerInternalMap.ReferenceEntry peek() {
         MapMakerInternalMap.ReferenceEntry<K, V> next = this.head.getNextExpirable();
         return next == this.head?null:next;
      }

      public MapMakerInternalMap.ReferenceEntry poll() {
         MapMakerInternalMap.ReferenceEntry<K, V> next = this.head.getNextExpirable();
         if(next == this.head) {
            return null;
         } else {
            this.remove(next);
            return next;
         }
      }

      public boolean remove(Object o) {
         MapMakerInternalMap.ReferenceEntry<K, V> e = (MapMakerInternalMap.ReferenceEntry)o;
         MapMakerInternalMap.ReferenceEntry<K, V> previous = e.getPreviousExpirable();
         MapMakerInternalMap.ReferenceEntry<K, V> next = e.getNextExpirable();
         MapMakerInternalMap.connectExpirables(previous, next);
         MapMakerInternalMap.nullifyExpirable(e);
         return next != MapMakerInternalMap.NullEntry.INSTANCE;
      }

      public boolean contains(Object o) {
         MapMakerInternalMap.ReferenceEntry<K, V> e = (MapMakerInternalMap.ReferenceEntry)o;
         return e.getNextExpirable() != MapMakerInternalMap.NullEntry.INSTANCE;
      }

      public boolean isEmpty() {
         return this.head.getNextExpirable() == this.head;
      }

      public int size() {
         int size = 0;

         for(MapMakerInternalMap.ReferenceEntry<K, V> e = this.head.getNextExpirable(); e != this.head; e = e.getNextExpirable()) {
            ++size;
         }

         return size;
      }

      public void clear() {
         MapMakerInternalMap.ReferenceEntry<K, V> next;
         for(MapMakerInternalMap.ReferenceEntry<K, V> e = this.head.getNextExpirable(); e != this.head; e = next) {
            next = e.getNextExpirable();
            MapMakerInternalMap.nullifyExpirable(e);
         }

         this.head.setNextExpirable(this.head);
         this.head.setPreviousExpirable(this.head);
      }

      public Iterator iterator() {
         return new AbstractSequentialIterator(this.peek()) {
            protected MapMakerInternalMap.ReferenceEntry computeNext(MapMakerInternalMap.ReferenceEntry previous) {
               MapMakerInternalMap.ReferenceEntry<K, V> next = previous.getNextExpirable();
               return next == ExpirationQueue.this.head?null:next;
            }
         };
      }
   }

   abstract class HashIterator implements Iterator {
      int nextSegmentIndex;
      int nextTableIndex;
      MapMakerInternalMap.Segment currentSegment;
      AtomicReferenceArray currentTable;
      MapMakerInternalMap.ReferenceEntry nextEntry;
      MapMakerInternalMap.WriteThroughEntry nextExternal;
      MapMakerInternalMap.WriteThroughEntry lastReturned;

      HashIterator() {
         this.nextSegmentIndex = MapMakerInternalMap.this.segments.length - 1;
         this.nextTableIndex = -1;
         this.advance();
      }

      public abstract Object next();

      final void advance() {
         this.nextExternal = null;
         if(!this.nextInChain()) {
            if(!this.nextInTable()) {
               while(this.nextSegmentIndex >= 0) {
                  this.currentSegment = MapMakerInternalMap.this.segments[this.nextSegmentIndex--];
                  if(this.currentSegment.count != 0) {
                     this.currentTable = this.currentSegment.table;
                     this.nextTableIndex = this.currentTable.length() - 1;
                     if(this.nextInTable()) {
                        return;
                     }
                  }
               }

            }
         }
      }

      boolean nextInChain() {
         if(this.nextEntry != null) {
            for(this.nextEntry = this.nextEntry.getNext(); this.nextEntry != null; this.nextEntry = this.nextEntry.getNext()) {
               if(this.advanceTo(this.nextEntry)) {
                  return true;
               }
            }
         }

         return false;
      }

      boolean nextInTable() {
         while(true) {
            if(this.nextTableIndex >= 0) {
               if((this.nextEntry = (MapMakerInternalMap.ReferenceEntry)this.currentTable.get(this.nextTableIndex--)) == null || !this.advanceTo(this.nextEntry) && !this.nextInChain()) {
                  continue;
               }

               return true;
            }

            return false;
         }
      }

      boolean advanceTo(MapMakerInternalMap.ReferenceEntry entry) {
         boolean var4;
         try {
            K key = entry.getKey();
            V value = MapMakerInternalMap.this.getLiveValue(entry);
            if(value == null) {
               var4 = false;
               return var4;
            }

            this.nextExternal = MapMakerInternalMap.this.new WriteThroughEntry(key, value);
            var4 = true;
         } finally {
            this.currentSegment.postReadCleanup();
         }

         return var4;
      }

      public boolean hasNext() {
         return this.nextExternal != null;
      }

      MapMakerInternalMap.WriteThroughEntry nextEntry() {
         if(this.nextExternal == null) {
            throw new NoSuchElementException();
         } else {
            this.lastReturned = this.nextExternal;
            this.advance();
            return this.lastReturned;
         }
      }

      public void remove() {
         CollectPreconditions.checkRemove(this.lastReturned != null);
         MapMakerInternalMap.this.remove(this.lastReturned.getKey());
         this.lastReturned = null;
      }
   }

   final class KeyIterator extends MapMakerInternalMap.HashIterator {
      KeyIterator() {
         super();
      }

      public Object next() {
         return this.nextEntry().getKey();
      }
   }

   final class KeySet extends AbstractSet {
      public Iterator iterator() {
         return MapMakerInternalMap.this.new KeyIterator();
      }

      public int size() {
         return MapMakerInternalMap.this.size();
      }

      public boolean isEmpty() {
         return MapMakerInternalMap.this.isEmpty();
      }

      public boolean contains(Object o) {
         return MapMakerInternalMap.this.containsKey(o);
      }

      public boolean remove(Object o) {
         return MapMakerInternalMap.this.remove(o) != null;
      }

      public void clear() {
         MapMakerInternalMap.this.clear();
      }
   }

   private static enum NullEntry implements MapMakerInternalMap.ReferenceEntry {
      INSTANCE;

      public MapMakerInternalMap.ValueReference getValueReference() {
         return null;
      }

      public void setValueReference(MapMakerInternalMap.ValueReference valueReference) {
      }

      public MapMakerInternalMap.ReferenceEntry getNext() {
         return null;
      }

      public int getHash() {
         return 0;
      }

      public Object getKey() {
         return null;
      }

      public long getExpirationTime() {
         return 0L;
      }

      public void setExpirationTime(long time) {
      }

      public MapMakerInternalMap.ReferenceEntry getNextExpirable() {
         return this;
      }

      public void setNextExpirable(MapMakerInternalMap.ReferenceEntry next) {
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousExpirable() {
         return this;
      }

      public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry previous) {
      }

      public MapMakerInternalMap.ReferenceEntry getNextEvictable() {
         return this;
      }

      public void setNextEvictable(MapMakerInternalMap.ReferenceEntry next) {
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousEvictable() {
         return this;
      }

      public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry previous) {
      }
   }

   interface ReferenceEntry {
      MapMakerInternalMap.ValueReference getValueReference();

      void setValueReference(MapMakerInternalMap.ValueReference var1);

      MapMakerInternalMap.ReferenceEntry getNext();

      int getHash();

      Object getKey();

      long getExpirationTime();

      void setExpirationTime(long var1);

      MapMakerInternalMap.ReferenceEntry getNextExpirable();

      void setNextExpirable(MapMakerInternalMap.ReferenceEntry var1);

      MapMakerInternalMap.ReferenceEntry getPreviousExpirable();

      void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry var1);

      MapMakerInternalMap.ReferenceEntry getNextEvictable();

      void setNextEvictable(MapMakerInternalMap.ReferenceEntry var1);

      MapMakerInternalMap.ReferenceEntry getPreviousEvictable();

      void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry var1);
   }

   static class Segment extends ReentrantLock {
      final MapMakerInternalMap map;
      volatile int count;
      int modCount;
      int threshold;
      volatile AtomicReferenceArray table;
      final int maxSegmentSize;
      final ReferenceQueue keyReferenceQueue;
      final ReferenceQueue valueReferenceQueue;
      final Queue recencyQueue;
      final AtomicInteger readCount = new AtomicInteger();
      @GuardedBy("Segment.this")
      final Queue evictionQueue;
      @GuardedBy("Segment.this")
      final Queue expirationQueue;

      Segment(MapMakerInternalMap map, int initialCapacity, int maxSegmentSize) {
         this.map = map;
         this.maxSegmentSize = maxSegmentSize;
         this.initTable(this.newEntryArray(initialCapacity));
         this.keyReferenceQueue = map.usesKeyReferences()?new ReferenceQueue():null;
         this.valueReferenceQueue = map.usesValueReferences()?new ReferenceQueue():null;
         this.recencyQueue = (Queue)(!map.evictsBySize() && !map.expiresAfterAccess()?MapMakerInternalMap.DISCARDING_QUEUE:new ConcurrentLinkedQueue());
         this.evictionQueue = (Queue)(map.evictsBySize()?new MapMakerInternalMap.EvictionQueue():MapMakerInternalMap.DISCARDING_QUEUE);
         this.expirationQueue = (Queue)(map.expires()?new MapMakerInternalMap.ExpirationQueue():MapMakerInternalMap.DISCARDING_QUEUE);
      }

      AtomicReferenceArray newEntryArray(int size) {
         return new AtomicReferenceArray(size);
      }

      void initTable(AtomicReferenceArray newTable) {
         this.threshold = newTable.length() * 3 / 4;
         if(this.threshold == this.maxSegmentSize) {
            ++this.threshold;
         }

         this.table = newTable;
      }

      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry newEntry(Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
         return this.map.entryFactory.newEntry(this, key, hash, next);
      }

      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry copyEntry(MapMakerInternalMap.ReferenceEntry original, MapMakerInternalMap.ReferenceEntry newNext) {
         if(original.getKey() == null) {
            return null;
         } else {
            MapMakerInternalMap.ValueReference<K, V> valueReference = original.getValueReference();
            V value = valueReference.get();
            if(value == null && !valueReference.isComputingReference()) {
               return null;
            } else {
               MapMakerInternalMap.ReferenceEntry<K, V> newEntry = this.map.entryFactory.copyEntry(this, original, newNext);
               newEntry.setValueReference(valueReference.copyFor(this.valueReferenceQueue, value, newEntry));
               return newEntry;
            }
         }
      }

      @GuardedBy("Segment.this")
      void setValue(MapMakerInternalMap.ReferenceEntry entry, Object value) {
         MapMakerInternalMap.ValueReference<K, V> valueReference = this.map.valueStrength.referenceValue(this, entry, value);
         entry.setValueReference(valueReference);
         this.recordWrite(entry);
      }

      void tryDrainReferenceQueues() {
         if(this.tryLock()) {
            try {
               this.drainReferenceQueues();
            } finally {
               this.unlock();
            }
         }

      }

      @GuardedBy("Segment.this")
      void drainReferenceQueues() {
         if(this.map.usesKeyReferences()) {
            this.drainKeyReferenceQueue();
         }

         if(this.map.usesValueReferences()) {
            this.drainValueReferenceQueue();
         }

      }

      @GuardedBy("Segment.this")
      void drainKeyReferenceQueue() {
         int i = 0;

         Reference<? extends K> ref;
         while((ref = this.keyReferenceQueue.poll()) != null) {
            MapMakerInternalMap.ReferenceEntry<K, V> entry = (MapMakerInternalMap.ReferenceEntry)ref;
            this.map.reclaimKey(entry);
            ++i;
            if(i == 16) {
               break;
            }
         }

      }

      @GuardedBy("Segment.this")
      void drainValueReferenceQueue() {
         int i = 0;

         Reference<? extends V> ref;
         while((ref = this.valueReferenceQueue.poll()) != null) {
            MapMakerInternalMap.ValueReference<K, V> valueReference = (MapMakerInternalMap.ValueReference)ref;
            this.map.reclaimValue(valueReference);
            ++i;
            if(i == 16) {
               break;
            }
         }

      }

      void clearReferenceQueues() {
         if(this.map.usesKeyReferences()) {
            this.clearKeyReferenceQueue();
         }

         if(this.map.usesValueReferences()) {
            this.clearValueReferenceQueue();
         }

      }

      void clearKeyReferenceQueue() {
         while(this.keyReferenceQueue.poll() != null) {
            ;
         }

      }

      void clearValueReferenceQueue() {
         while(this.valueReferenceQueue.poll() != null) {
            ;
         }

      }

      void recordRead(MapMakerInternalMap.ReferenceEntry entry) {
         if(this.map.expiresAfterAccess()) {
            this.recordExpirationTime(entry, this.map.expireAfterAccessNanos);
         }

         this.recencyQueue.add(entry);
      }

      @GuardedBy("Segment.this")
      void recordLockedRead(MapMakerInternalMap.ReferenceEntry entry) {
         this.evictionQueue.add(entry);
         if(this.map.expiresAfterAccess()) {
            this.recordExpirationTime(entry, this.map.expireAfterAccessNanos);
            this.expirationQueue.add(entry);
         }

      }

      @GuardedBy("Segment.this")
      void recordWrite(MapMakerInternalMap.ReferenceEntry entry) {
         this.drainRecencyQueue();
         this.evictionQueue.add(entry);
         if(this.map.expires()) {
            long expiration = this.map.expiresAfterAccess()?this.map.expireAfterAccessNanos:this.map.expireAfterWriteNanos;
            this.recordExpirationTime(entry, expiration);
            this.expirationQueue.add(entry);
         }

      }

      @GuardedBy("Segment.this")
      void drainRecencyQueue() {
         MapMakerInternalMap.ReferenceEntry<K, V> e;
         while((e = (MapMakerInternalMap.ReferenceEntry)this.recencyQueue.poll()) != null) {
            if(this.evictionQueue.contains(e)) {
               this.evictionQueue.add(e);
            }

            if(this.map.expiresAfterAccess() && this.expirationQueue.contains(e)) {
               this.expirationQueue.add(e);
            }
         }

      }

      void recordExpirationTime(MapMakerInternalMap.ReferenceEntry entry, long expirationNanos) {
         entry.setExpirationTime(this.map.ticker.read() + expirationNanos);
      }

      void tryExpireEntries() {
         if(this.tryLock()) {
            try {
               this.expireEntries();
            } finally {
               this.unlock();
            }
         }

      }

      @GuardedBy("Segment.this")
      void expireEntries() {
         this.drainRecencyQueue();
         if(!this.expirationQueue.isEmpty()) {
            long now = this.map.ticker.read();

            MapMakerInternalMap.ReferenceEntry<K, V> e;
            while((e = (MapMakerInternalMap.ReferenceEntry)this.expirationQueue.peek()) != null && this.map.isExpired(e, now)) {
               if(!this.removeEntry(e, e.getHash(), MapMaker.RemovalCause.EXPIRED)) {
                  throw new AssertionError();
               }
            }

         }
      }

      void enqueueNotification(MapMakerInternalMap.ReferenceEntry entry, MapMaker.RemovalCause cause) {
         this.enqueueNotification(entry.getKey(), entry.getHash(), entry.getValueReference().get(), cause);
      }

      void enqueueNotification(@Nullable Object key, int hash, @Nullable Object value, MapMaker.RemovalCause cause) {
         if(this.map.removalNotificationQueue != MapMakerInternalMap.DISCARDING_QUEUE) {
            MapMaker.RemovalNotification<K, V> notification = new MapMaker.RemovalNotification(key, value, cause);
            this.map.removalNotificationQueue.offer(notification);
         }

      }

      @GuardedBy("Segment.this")
      boolean evictEntries() {
         if(this.map.evictsBySize() && this.count >= this.maxSegmentSize) {
            this.drainRecencyQueue();
            MapMakerInternalMap.ReferenceEntry<K, V> e = (MapMakerInternalMap.ReferenceEntry)this.evictionQueue.remove();
            if(!this.removeEntry(e, e.getHash(), MapMaker.RemovalCause.SIZE)) {
               throw new AssertionError();
            } else {
               return true;
            }
         } else {
            return false;
         }
      }

      MapMakerInternalMap.ReferenceEntry getFirst(int hash) {
         AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table = this.table;
         return (MapMakerInternalMap.ReferenceEntry)table.get(hash & table.length() - 1);
      }

      MapMakerInternalMap.ReferenceEntry getEntry(Object key, int hash) {
         if(this.count != 0) {
            for(MapMakerInternalMap.ReferenceEntry<K, V> e = this.getFirst(hash); e != null; e = e.getNext()) {
               if(e.getHash() == hash) {
                  K entryKey = e.getKey();
                  if(entryKey == null) {
                     this.tryDrainReferenceQueues();
                  } else if(this.map.keyEquivalence.equivalent(key, entryKey)) {
                     return e;
                  }
               }
            }
         }

         return null;
      }

      MapMakerInternalMap.ReferenceEntry getLiveEntry(Object key, int hash) {
         MapMakerInternalMap.ReferenceEntry<K, V> e = this.getEntry(key, hash);
         if(e == null) {
            return null;
         } else if(this.map.expires() && this.map.isExpired(e)) {
            this.tryExpireEntries();
            return null;
         } else {
            return e;
         }
      }

      Object get(Object key, int hash) {
         Object value;
         try {
            MapMakerInternalMap.ReferenceEntry<K, V> e = this.getLiveEntry(key, hash);
            if(e != null) {
               value = e.getValueReference().get();
               if(value != null) {
                  this.recordRead(e);
               } else {
                  this.tryDrainReferenceQueues();
               }

               Object var5 = value;
               return var5;
            }

            value = null;
         } finally {
            this.postReadCleanup();
         }

         return value;
      }

      boolean containsKey(Object key, int hash) {
         boolean var4;
         try {
            if(this.count == 0) {
               boolean var8 = false;
               return var8;
            }

            MapMakerInternalMap.ReferenceEntry<K, V> e = this.getLiveEntry(key, hash);
            if(e != null) {
               var4 = e.getValueReference().get() != null;
               return var4;
            }

            var4 = false;
         } finally {
            this.postReadCleanup();
         }

         return var4;
      }

      @VisibleForTesting
      boolean containsValue(Object value) {
         try {
            if(this.count != 0) {
               AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table = this.table;
               int length = table.length();

               for(int i = 0; i < length; ++i) {
                  for(MapMakerInternalMap.ReferenceEntry<K, V> e = (MapMakerInternalMap.ReferenceEntry)table.get(i); e != null; e = e.getNext()) {
                     V entryValue = this.getLiveValue(e);
                     if(entryValue != null && this.map.valueEquivalence.equivalent(value, entryValue)) {
                        boolean var7 = true;
                        return var7;
                     }
                  }
               }
            }

            boolean var11 = false;
            return var11;
         } finally {
            this.postReadCleanup();
         }
      }

      Object put(Object key, int hash, Object value, boolean onlyIfAbsent) {
         this.lock();

         try {
            this.preWriteCleanup();
            int newCount = this.count + 1;
            if(newCount > this.threshold) {
               this.expand();
               newCount = this.count + 1;
            }

            AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            MapMakerInternalMap.ReferenceEntry<K, V> first = (MapMakerInternalMap.ReferenceEntry)table.get(index);

            for(MapMakerInternalMap.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  MapMakerInternalMap.ValueReference<K, V> valueReference = e.getValueReference();
                  V entryValue = valueReference.get();
                  if(entryValue != null) {
                     if(onlyIfAbsent) {
                        this.recordLockedRead(e);
                        Object var20 = entryValue;
                        return var20;
                     }

                     ++this.modCount;
                     this.enqueueNotification(key, hash, entryValue, MapMaker.RemovalCause.REPLACED);
                     this.setValue(e, value);
                     Object var19 = entryValue;
                     return var19;
                  }

                  ++this.modCount;
                  this.setValue(e, value);
                  if(!valueReference.isComputingReference()) {
                     this.enqueueNotification(key, hash, entryValue, MapMaker.RemovalCause.COLLECTED);
                     newCount = this.count;
                  } else if(this.evictEntries()) {
                     newCount = this.count + 1;
                  }

                  this.count = newCount;
                  Object var13 = null;
                  return var13;
               }
            }

            ++this.modCount;
            MapMakerInternalMap.ReferenceEntry<K, V> newEntry = this.newEntry(key, hash, first);
            this.setValue(newEntry, value);
            table.set(index, newEntry);
            if(this.evictEntries()) {
               newCount = this.count + 1;
            }

            this.count = newCount;
            Object var18 = null;
            return var18;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      @GuardedBy("Segment.this")
      void expand() {
         AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> oldTable = this.table;
         int oldCapacity = oldTable.length();
         if(oldCapacity < 1073741824) {
            int newCount = this.count;
            AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> newTable = this.newEntryArray(oldCapacity << 1);
            this.threshold = newTable.length() * 3 / 4;
            int newMask = newTable.length() - 1;

            for(int oldIndex = 0; oldIndex < oldCapacity; ++oldIndex) {
               MapMakerInternalMap.ReferenceEntry<K, V> head = (MapMakerInternalMap.ReferenceEntry)oldTable.get(oldIndex);
               if(head != null) {
                  MapMakerInternalMap.ReferenceEntry<K, V> next = head.getNext();
                  int headIndex = head.getHash() & newMask;
                  if(next == null) {
                     newTable.set(headIndex, head);
                  } else {
                     MapMakerInternalMap.ReferenceEntry<K, V> tail = head;
                     int tailIndex = headIndex;

                     for(MapMakerInternalMap.ReferenceEntry<K, V> e = next; e != null; e = e.getNext()) {
                        int newIndex = e.getHash() & newMask;
                        if(newIndex != tailIndex) {
                           tailIndex = newIndex;
                           tail = e;
                        }
                     }

                     newTable.set(tailIndex, tail);

                     for(MapMakerInternalMap.ReferenceEntry<K, V> e = head; e != tail; e = e.getNext()) {
                        int newIndex = e.getHash() & newMask;
                        MapMakerInternalMap.ReferenceEntry<K, V> newNext = (MapMakerInternalMap.ReferenceEntry)newTable.get(newIndex);
                        MapMakerInternalMap.ReferenceEntry<K, V> newFirst = this.copyEntry(e, newNext);
                        if(newFirst != null) {
                           newTable.set(newIndex, newFirst);
                        } else {
                           this.removeCollectedEntry(e);
                           --newCount;
                        }
                     }
                  }
               }
            }

            this.table = newTable;
            this.count = newCount;
         }
      }

      boolean replace(Object key, int hash, Object oldValue, Object newValue) {
         this.lock();

         try {
            this.preWriteCleanup();
            AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            MapMakerInternalMap.ReferenceEntry<K, V> first = (MapMakerInternalMap.ReferenceEntry)table.get(index);

            for(MapMakerInternalMap.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  MapMakerInternalMap.ValueReference<K, V> valueReference = e.getValueReference();
                  V entryValue = valueReference.get();
                  if(entryValue != null) {
                     if(this.map.valueEquivalence.equivalent(oldValue, entryValue)) {
                        ++this.modCount;
                        this.enqueueNotification(key, hash, entryValue, MapMaker.RemovalCause.REPLACED);
                        this.setValue(e, newValue);
                        boolean var21 = true;
                        return var21;
                     }

                     this.recordLockedRead(e);
                     boolean var20 = false;
                     return var20;
                  }

                  if(this.isCollected(valueReference)) {
                     int newCount = this.count - 1;
                     ++this.modCount;
                     this.enqueueNotification(entryKey, hash, entryValue, MapMaker.RemovalCause.COLLECTED);
                     MapMakerInternalMap.ReferenceEntry<K, V> newFirst = this.removeFromChain(first, e);
                     newCount = this.count - 1;
                     table.set(index, newFirst);
                     this.count = newCount;
                  }

                  boolean var19 = false;
                  return var19;
               }
            }

            boolean var17 = false;
            return var17;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      Object replace(Object key, int hash, Object newValue) {
         this.lock();

         try {
            this.preWriteCleanup();
            AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            MapMakerInternalMap.ReferenceEntry<K, V> first = (MapMakerInternalMap.ReferenceEntry)table.get(index);

            for(MapMakerInternalMap.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  MapMakerInternalMap.ValueReference<K, V> valueReference = e.getValueReference();
                  V entryValue = valueReference.get();
                  if(entryValue != null) {
                     ++this.modCount;
                     this.enqueueNotification(key, hash, entryValue, MapMaker.RemovalCause.REPLACED);
                     this.setValue(e, newValue);
                     Object var19 = entryValue;
                     return var19;
                  }

                  if(this.isCollected(valueReference)) {
                     int newCount = this.count - 1;
                     ++this.modCount;
                     this.enqueueNotification(entryKey, hash, entryValue, MapMaker.RemovalCause.COLLECTED);
                     MapMakerInternalMap.ReferenceEntry<K, V> newFirst = this.removeFromChain(first, e);
                     newCount = this.count - 1;
                     table.set(index, newFirst);
                     this.count = newCount;
                  }

                  Object var18 = null;
                  return var18;
               }
            }

            Object var16 = null;
            return var16;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      Object remove(Object key, int hash) {
         this.lock();

         try {
            this.preWriteCleanup();
            int newCount = this.count - 1;
            AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            MapMakerInternalMap.ReferenceEntry<K, V> first = (MapMakerInternalMap.ReferenceEntry)table.get(index);

            for(MapMakerInternalMap.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  MapMakerInternalMap.ValueReference<K, V> valueReference = e.getValueReference();
                  V entryValue = valueReference.get();
                  MapMaker.RemovalCause cause;
                  if(entryValue != null) {
                     cause = MapMaker.RemovalCause.EXPLICIT;
                  } else {
                     if(!this.isCollected(valueReference)) {
                        Object var19 = null;
                        return var19;
                     }

                     cause = MapMaker.RemovalCause.COLLECTED;
                  }

                  ++this.modCount;
                  this.enqueueNotification(entryKey, hash, entryValue, cause);
                  MapMakerInternalMap.ReferenceEntry<K, V> newFirst = this.removeFromChain(first, e);
                  newCount = this.count - 1;
                  table.set(index, newFirst);
                  this.count = newCount;
                  Object var13 = entryValue;
                  return var13;
               }
            }

            Object var18 = null;
            return var18;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      boolean remove(Object key, int hash, Object value) {
         this.lock();

         try {
            this.preWriteCleanup();
            int newCount = this.count - 1;
            AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            MapMakerInternalMap.ReferenceEntry<K, V> first = (MapMakerInternalMap.ReferenceEntry)table.get(index);

            for(MapMakerInternalMap.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  MapMakerInternalMap.ValueReference<K, V> valueReference = e.getValueReference();
                  V entryValue = valueReference.get();
                  MapMaker.RemovalCause cause;
                  if(this.map.valueEquivalence.equivalent(value, entryValue)) {
                     cause = MapMaker.RemovalCause.EXPLICIT;
                  } else {
                     if(!this.isCollected(valueReference)) {
                        boolean var20 = false;
                        return var20;
                     }

                     cause = MapMaker.RemovalCause.COLLECTED;
                  }

                  ++this.modCount;
                  this.enqueueNotification(entryKey, hash, entryValue, cause);
                  MapMakerInternalMap.ReferenceEntry<K, V> newFirst = this.removeFromChain(first, e);
                  newCount = this.count - 1;
                  table.set(index, newFirst);
                  this.count = newCount;
                  boolean var14 = cause == MapMaker.RemovalCause.EXPLICIT;
                  return var14;
               }
            }

            boolean var19 = false;
            return var19;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      void clear() {
         if(this.count != 0) {
            this.lock();

            try {
               AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table = this.table;
               if(this.map.removalNotificationQueue != MapMakerInternalMap.DISCARDING_QUEUE) {
                  for(int i = 0; i < table.length(); ++i) {
                     for(MapMakerInternalMap.ReferenceEntry<K, V> e = (MapMakerInternalMap.ReferenceEntry)table.get(i); e != null; e = e.getNext()) {
                        if(!e.getValueReference().isComputingReference()) {
                           this.enqueueNotification(e, MapMaker.RemovalCause.EXPLICIT);
                        }
                     }
                  }
               }

               for(int i = 0; i < table.length(); ++i) {
                  table.set(i, (Object)null);
               }

               this.clearReferenceQueues();
               this.evictionQueue.clear();
               this.expirationQueue.clear();
               this.readCount.set(0);
               ++this.modCount;
               this.count = 0;
            } finally {
               this.unlock();
               this.postWriteCleanup();
            }
         }

      }

      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry removeFromChain(MapMakerInternalMap.ReferenceEntry first, MapMakerInternalMap.ReferenceEntry entry) {
         this.evictionQueue.remove(entry);
         this.expirationQueue.remove(entry);
         int newCount = this.count;
         MapMakerInternalMap.ReferenceEntry<K, V> newFirst = entry.getNext();

         for(MapMakerInternalMap.ReferenceEntry<K, V> e = first; e != entry; e = e.getNext()) {
            MapMakerInternalMap.ReferenceEntry<K, V> next = this.copyEntry(e, newFirst);
            if(next != null) {
               newFirst = next;
            } else {
               this.removeCollectedEntry(e);
               --newCount;
            }
         }

         this.count = newCount;
         return newFirst;
      }

      void removeCollectedEntry(MapMakerInternalMap.ReferenceEntry entry) {
         this.enqueueNotification(entry, MapMaker.RemovalCause.COLLECTED);
         this.evictionQueue.remove(entry);
         this.expirationQueue.remove(entry);
      }

      boolean reclaimKey(MapMakerInternalMap.ReferenceEntry entry, int hash) {
         this.lock();

         try {
            int newCount = this.count - 1;
            AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            MapMakerInternalMap.ReferenceEntry<K, V> first = (MapMakerInternalMap.ReferenceEntry)table.get(index);

            for(MapMakerInternalMap.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               if(e == entry) {
                  ++this.modCount;
                  this.enqueueNotification(e.getKey(), hash, e.getValueReference().get(), MapMaker.RemovalCause.COLLECTED);
                  MapMakerInternalMap.ReferenceEntry<K, V> newFirst = this.removeFromChain(first, e);
                  newCount = this.count - 1;
                  table.set(index, newFirst);
                  this.count = newCount;
                  boolean var9 = true;
                  return var9;
               }
            }

            boolean var14 = false;
            return var14;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      boolean reclaimValue(Object key, int hash, MapMakerInternalMap.ValueReference valueReference) {
         this.lock();

         try {
            int newCount = this.count - 1;
            AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            MapMakerInternalMap.ReferenceEntry<K, V> first = (MapMakerInternalMap.ReferenceEntry)table.get(index);

            for(MapMakerInternalMap.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  MapMakerInternalMap.ValueReference<K, V> v = e.getValueReference();
                  if(v != valueReference) {
                     boolean var18 = false;
                     return var18;
                  }

                  ++this.modCount;
                  this.enqueueNotification(key, hash, valueReference.get(), MapMaker.RemovalCause.COLLECTED);
                  MapMakerInternalMap.ReferenceEntry<K, V> newFirst = this.removeFromChain(first, e);
                  newCount = this.count - 1;
                  table.set(index, newFirst);
                  this.count = newCount;
                  boolean var12 = true;
                  return var12;
               }
            }

            boolean var17 = false;
            return var17;
         } finally {
            this.unlock();
            if(!this.isHeldByCurrentThread()) {
               this.postWriteCleanup();
            }

         }
      }

      boolean clearValue(Object key, int hash, MapMakerInternalMap.ValueReference valueReference) {
         this.lock();

         try {
            AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            MapMakerInternalMap.ReferenceEntry<K, V> first = (MapMakerInternalMap.ReferenceEntry)table.get(index);

            for(MapMakerInternalMap.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  MapMakerInternalMap.ValueReference<K, V> v = e.getValueReference();
                  if(v != valueReference) {
                     boolean var16 = false;
                     return var16;
                  }

                  MapMakerInternalMap.ReferenceEntry<K, V> newFirst = this.removeFromChain(first, e);
                  table.set(index, newFirst);
                  boolean var11 = true;
                  return var11;
               }
            }

            boolean var15 = false;
            return var15;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      @GuardedBy("Segment.this")
      boolean removeEntry(MapMakerInternalMap.ReferenceEntry entry, int hash, MapMaker.RemovalCause cause) {
         int newCount = this.count - 1;
         AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table = this.table;
         int index = hash & table.length() - 1;
         MapMakerInternalMap.ReferenceEntry<K, V> first = (MapMakerInternalMap.ReferenceEntry)table.get(index);

         for(MapMakerInternalMap.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
            if(e == entry) {
               ++this.modCount;
               this.enqueueNotification(e.getKey(), hash, e.getValueReference().get(), cause);
               MapMakerInternalMap.ReferenceEntry<K, V> newFirst = this.removeFromChain(first, e);
               newCount = this.count - 1;
               table.set(index, newFirst);
               this.count = newCount;
               return true;
            }
         }

         return false;
      }

      boolean isCollected(MapMakerInternalMap.ValueReference valueReference) {
         return valueReference.isComputingReference()?false:valueReference.get() == null;
      }

      Object getLiveValue(MapMakerInternalMap.ReferenceEntry entry) {
         if(entry.getKey() == null) {
            this.tryDrainReferenceQueues();
            return null;
         } else {
            V value = entry.getValueReference().get();
            if(value == null) {
               this.tryDrainReferenceQueues();
               return null;
            } else if(this.map.expires() && this.map.isExpired(entry)) {
               this.tryExpireEntries();
               return null;
            } else {
               return value;
            }
         }
      }

      void postReadCleanup() {
         if((this.readCount.incrementAndGet() & 63) == 0) {
            this.runCleanup();
         }

      }

      @GuardedBy("Segment.this")
      void preWriteCleanup() {
         this.runLockedCleanup();
      }

      void postWriteCleanup() {
         this.runUnlockedCleanup();
      }

      void runCleanup() {
         this.runLockedCleanup();
         this.runUnlockedCleanup();
      }

      void runLockedCleanup() {
         if(this.tryLock()) {
            try {
               this.drainReferenceQueues();
               this.expireEntries();
               this.readCount.set(0);
            } finally {
               this.unlock();
            }
         }

      }

      void runUnlockedCleanup() {
         if(!this.isHeldByCurrentThread()) {
            this.map.processPendingNotifications();
         }

      }
   }

   private static final class SerializationProxy extends MapMakerInternalMap.AbstractSerializationProxy {
      private static final long serialVersionUID = 3L;

      SerializationProxy(MapMakerInternalMap.Strength keyStrength, MapMakerInternalMap.Strength valueStrength, Equivalence keyEquivalence, Equivalence valueEquivalence, long expireAfterWriteNanos, long expireAfterAccessNanos, int maximumSize, int concurrencyLevel, MapMaker.RemovalListener removalListener, ConcurrentMap delegate) {
         super(keyStrength, valueStrength, keyEquivalence, valueEquivalence, expireAfterWriteNanos, expireAfterAccessNanos, maximumSize, concurrencyLevel, removalListener, delegate);
      }

      private void writeObject(ObjectOutputStream out) throws IOException {
         out.defaultWriteObject();
         this.writeMapTo(out);
      }

      private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         MapMaker mapMaker = this.readMapMaker(in);
         this.delegate = mapMaker.makeMap();
         this.readEntries(in);
      }

      private Object readResolve() {
         return this.delegate;
      }
   }

   static class SoftEntry extends SoftReference implements MapMakerInternalMap.ReferenceEntry {
      final int hash;
      final MapMakerInternalMap.ReferenceEntry next;
      volatile MapMakerInternalMap.ValueReference valueReference = MapMakerInternalMap.UNSET;

      SoftEntry(ReferenceQueue queue, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
         super(key, queue);
         this.hash = hash;
         this.next = next;
      }

      public Object getKey() {
         return this.get();
      }

      public long getExpirationTime() {
         throw new UnsupportedOperationException();
      }

      public void setExpirationTime(long time) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getNextExpirable() {
         throw new UnsupportedOperationException();
      }

      public void setNextExpirable(MapMakerInternalMap.ReferenceEntry next) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousExpirable() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry previous) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getNextEvictable() {
         throw new UnsupportedOperationException();
      }

      public void setNextEvictable(MapMakerInternalMap.ReferenceEntry next) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousEvictable() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry previous) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ValueReference getValueReference() {
         return this.valueReference;
      }

      public void setValueReference(MapMakerInternalMap.ValueReference valueReference) {
         MapMakerInternalMap.ValueReference<K, V> previous = this.valueReference;
         this.valueReference = valueReference;
         previous.clear(valueReference);
      }

      public int getHash() {
         return this.hash;
      }

      public MapMakerInternalMap.ReferenceEntry getNext() {
         return this.next;
      }
   }

   static final class SoftEvictableEntry extends MapMakerInternalMap.SoftEntry implements MapMakerInternalMap.ReferenceEntry {
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry nextEvictable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry previousEvictable = MapMakerInternalMap.nullEntry();

      SoftEvictableEntry(ReferenceQueue queue, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
         super(queue, key, hash, next);
      }

      public MapMakerInternalMap.ReferenceEntry getNextEvictable() {
         return this.nextEvictable;
      }

      public void setNextEvictable(MapMakerInternalMap.ReferenceEntry next) {
         this.nextEvictable = next;
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousEvictable() {
         return this.previousEvictable;
      }

      public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry previous) {
         this.previousEvictable = previous;
      }
   }

   static final class SoftExpirableEntry extends MapMakerInternalMap.SoftEntry implements MapMakerInternalMap.ReferenceEntry {
      volatile long time = Long.MAX_VALUE;
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry nextExpirable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry previousExpirable = MapMakerInternalMap.nullEntry();

      SoftExpirableEntry(ReferenceQueue queue, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
         super(queue, key, hash, next);
      }

      public long getExpirationTime() {
         return this.time;
      }

      public void setExpirationTime(long time) {
         this.time = time;
      }

      public MapMakerInternalMap.ReferenceEntry getNextExpirable() {
         return this.nextExpirable;
      }

      public void setNextExpirable(MapMakerInternalMap.ReferenceEntry next) {
         this.nextExpirable = next;
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousExpirable() {
         return this.previousExpirable;
      }

      public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry previous) {
         this.previousExpirable = previous;
      }
   }

   static final class SoftExpirableEvictableEntry extends MapMakerInternalMap.SoftEntry implements MapMakerInternalMap.ReferenceEntry {
      volatile long time = Long.MAX_VALUE;
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry nextExpirable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry previousExpirable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry nextEvictable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry previousEvictable = MapMakerInternalMap.nullEntry();

      SoftExpirableEvictableEntry(ReferenceQueue queue, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
         super(queue, key, hash, next);
      }

      public long getExpirationTime() {
         return this.time;
      }

      public void setExpirationTime(long time) {
         this.time = time;
      }

      public MapMakerInternalMap.ReferenceEntry getNextExpirable() {
         return this.nextExpirable;
      }

      public void setNextExpirable(MapMakerInternalMap.ReferenceEntry next) {
         this.nextExpirable = next;
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousExpirable() {
         return this.previousExpirable;
      }

      public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry previous) {
         this.previousExpirable = previous;
      }

      public MapMakerInternalMap.ReferenceEntry getNextEvictable() {
         return this.nextEvictable;
      }

      public void setNextEvictable(MapMakerInternalMap.ReferenceEntry next) {
         this.nextEvictable = next;
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousEvictable() {
         return this.previousEvictable;
      }

      public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry previous) {
         this.previousEvictable = previous;
      }
   }

   static final class SoftValueReference extends SoftReference implements MapMakerInternalMap.ValueReference {
      final MapMakerInternalMap.ReferenceEntry entry;

      SoftValueReference(ReferenceQueue queue, Object referent, MapMakerInternalMap.ReferenceEntry entry) {
         super(referent, queue);
         this.entry = entry;
      }

      public MapMakerInternalMap.ReferenceEntry getEntry() {
         return this.entry;
      }

      public void clear(MapMakerInternalMap.ValueReference newValue) {
         this.clear();
      }

      public MapMakerInternalMap.ValueReference copyFor(ReferenceQueue queue, Object value, MapMakerInternalMap.ReferenceEntry entry) {
         return new MapMakerInternalMap.SoftValueReference(queue, value, entry);
      }

      public boolean isComputingReference() {
         return false;
      }

      public Object waitForValue() {
         return this.get();
      }
   }

   static enum Strength {
      STRONG {
         MapMakerInternalMap.ValueReference referenceValue(MapMakerInternalMap.Segment segment, MapMakerInternalMap.ReferenceEntry entry, Object value) {
            return new MapMakerInternalMap.StrongValueReference(value);
         }

         Equivalence defaultEquivalence() {
            return Equivalence.equals();
         }
      },
      SOFT {
         MapMakerInternalMap.ValueReference referenceValue(MapMakerInternalMap.Segment segment, MapMakerInternalMap.ReferenceEntry entry, Object value) {
            return new MapMakerInternalMap.SoftValueReference(segment.valueReferenceQueue, value, entry);
         }

         Equivalence defaultEquivalence() {
            return Equivalence.identity();
         }
      },
      WEAK {
         MapMakerInternalMap.ValueReference referenceValue(MapMakerInternalMap.Segment segment, MapMakerInternalMap.ReferenceEntry entry, Object value) {
            return new MapMakerInternalMap.WeakValueReference(segment.valueReferenceQueue, value, entry);
         }

         Equivalence defaultEquivalence() {
            return Equivalence.identity();
         }
      };

      private Strength() {
      }

      abstract MapMakerInternalMap.ValueReference referenceValue(MapMakerInternalMap.Segment var1, MapMakerInternalMap.ReferenceEntry var2, Object var3);

      abstract Equivalence defaultEquivalence();
   }

   static class StrongEntry implements MapMakerInternalMap.ReferenceEntry {
      final Object key;
      final int hash;
      final MapMakerInternalMap.ReferenceEntry next;
      volatile MapMakerInternalMap.ValueReference valueReference = MapMakerInternalMap.UNSET;

      StrongEntry(Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
         this.key = key;
         this.hash = hash;
         this.next = next;
      }

      public Object getKey() {
         return this.key;
      }

      public long getExpirationTime() {
         throw new UnsupportedOperationException();
      }

      public void setExpirationTime(long time) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getNextExpirable() {
         throw new UnsupportedOperationException();
      }

      public void setNextExpirable(MapMakerInternalMap.ReferenceEntry next) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousExpirable() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry previous) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getNextEvictable() {
         throw new UnsupportedOperationException();
      }

      public void setNextEvictable(MapMakerInternalMap.ReferenceEntry next) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousEvictable() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry previous) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ValueReference getValueReference() {
         return this.valueReference;
      }

      public void setValueReference(MapMakerInternalMap.ValueReference valueReference) {
         MapMakerInternalMap.ValueReference<K, V> previous = this.valueReference;
         this.valueReference = valueReference;
         previous.clear(valueReference);
      }

      public int getHash() {
         return this.hash;
      }

      public MapMakerInternalMap.ReferenceEntry getNext() {
         return this.next;
      }
   }

   static final class StrongEvictableEntry extends MapMakerInternalMap.StrongEntry implements MapMakerInternalMap.ReferenceEntry {
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry nextEvictable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry previousEvictable = MapMakerInternalMap.nullEntry();

      StrongEvictableEntry(Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
         super(key, hash, next);
      }

      public MapMakerInternalMap.ReferenceEntry getNextEvictable() {
         return this.nextEvictable;
      }

      public void setNextEvictable(MapMakerInternalMap.ReferenceEntry next) {
         this.nextEvictable = next;
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousEvictable() {
         return this.previousEvictable;
      }

      public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry previous) {
         this.previousEvictable = previous;
      }
   }

   static final class StrongExpirableEntry extends MapMakerInternalMap.StrongEntry implements MapMakerInternalMap.ReferenceEntry {
      volatile long time = Long.MAX_VALUE;
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry nextExpirable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry previousExpirable = MapMakerInternalMap.nullEntry();

      StrongExpirableEntry(Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
         super(key, hash, next);
      }

      public long getExpirationTime() {
         return this.time;
      }

      public void setExpirationTime(long time) {
         this.time = time;
      }

      public MapMakerInternalMap.ReferenceEntry getNextExpirable() {
         return this.nextExpirable;
      }

      public void setNextExpirable(MapMakerInternalMap.ReferenceEntry next) {
         this.nextExpirable = next;
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousExpirable() {
         return this.previousExpirable;
      }

      public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry previous) {
         this.previousExpirable = previous;
      }
   }

   static final class StrongExpirableEvictableEntry extends MapMakerInternalMap.StrongEntry implements MapMakerInternalMap.ReferenceEntry {
      volatile long time = Long.MAX_VALUE;
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry nextExpirable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry previousExpirable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry nextEvictable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry previousEvictable = MapMakerInternalMap.nullEntry();

      StrongExpirableEvictableEntry(Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
         super(key, hash, next);
      }

      public long getExpirationTime() {
         return this.time;
      }

      public void setExpirationTime(long time) {
         this.time = time;
      }

      public MapMakerInternalMap.ReferenceEntry getNextExpirable() {
         return this.nextExpirable;
      }

      public void setNextExpirable(MapMakerInternalMap.ReferenceEntry next) {
         this.nextExpirable = next;
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousExpirable() {
         return this.previousExpirable;
      }

      public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry previous) {
         this.previousExpirable = previous;
      }

      public MapMakerInternalMap.ReferenceEntry getNextEvictable() {
         return this.nextEvictable;
      }

      public void setNextEvictable(MapMakerInternalMap.ReferenceEntry next) {
         this.nextEvictable = next;
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousEvictable() {
         return this.previousEvictable;
      }

      public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry previous) {
         this.previousEvictable = previous;
      }
   }

   static final class StrongValueReference implements MapMakerInternalMap.ValueReference {
      final Object referent;

      StrongValueReference(Object referent) {
         this.referent = referent;
      }

      public Object get() {
         return this.referent;
      }

      public MapMakerInternalMap.ReferenceEntry getEntry() {
         return null;
      }

      public MapMakerInternalMap.ValueReference copyFor(ReferenceQueue queue, Object value, MapMakerInternalMap.ReferenceEntry entry) {
         return this;
      }

      public boolean isComputingReference() {
         return false;
      }

      public Object waitForValue() {
         return this.get();
      }

      public void clear(MapMakerInternalMap.ValueReference newValue) {
      }
   }

   final class ValueIterator extends MapMakerInternalMap.HashIterator {
      ValueIterator() {
         super();
      }

      public Object next() {
         return this.nextEntry().getValue();
      }
   }

   interface ValueReference {
      Object get();

      Object waitForValue() throws ExecutionException;

      MapMakerInternalMap.ReferenceEntry getEntry();

      MapMakerInternalMap.ValueReference copyFor(ReferenceQueue var1, @Nullable Object var2, MapMakerInternalMap.ReferenceEntry var3);

      void clear(@Nullable MapMakerInternalMap.ValueReference var1);

      boolean isComputingReference();
   }

   final class Values extends AbstractCollection {
      public Iterator iterator() {
         return MapMakerInternalMap.this.new ValueIterator();
      }

      public int size() {
         return MapMakerInternalMap.this.size();
      }

      public boolean isEmpty() {
         return MapMakerInternalMap.this.isEmpty();
      }

      public boolean contains(Object o) {
         return MapMakerInternalMap.this.containsValue(o);
      }

      public void clear() {
         MapMakerInternalMap.this.clear();
      }
   }

   static class WeakEntry extends WeakReference implements MapMakerInternalMap.ReferenceEntry {
      final int hash;
      final MapMakerInternalMap.ReferenceEntry next;
      volatile MapMakerInternalMap.ValueReference valueReference = MapMakerInternalMap.UNSET;

      WeakEntry(ReferenceQueue queue, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
         super(key, queue);
         this.hash = hash;
         this.next = next;
      }

      public Object getKey() {
         return this.get();
      }

      public long getExpirationTime() {
         throw new UnsupportedOperationException();
      }

      public void setExpirationTime(long time) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getNextExpirable() {
         throw new UnsupportedOperationException();
      }

      public void setNextExpirable(MapMakerInternalMap.ReferenceEntry next) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousExpirable() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry previous) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getNextEvictable() {
         throw new UnsupportedOperationException();
      }

      public void setNextEvictable(MapMakerInternalMap.ReferenceEntry next) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousEvictable() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry previous) {
         throw new UnsupportedOperationException();
      }

      public MapMakerInternalMap.ValueReference getValueReference() {
         return this.valueReference;
      }

      public void setValueReference(MapMakerInternalMap.ValueReference valueReference) {
         MapMakerInternalMap.ValueReference<K, V> previous = this.valueReference;
         this.valueReference = valueReference;
         previous.clear(valueReference);
      }

      public int getHash() {
         return this.hash;
      }

      public MapMakerInternalMap.ReferenceEntry getNext() {
         return this.next;
      }
   }

   static final class WeakEvictableEntry extends MapMakerInternalMap.WeakEntry implements MapMakerInternalMap.ReferenceEntry {
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry nextEvictable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry previousEvictable = MapMakerInternalMap.nullEntry();

      WeakEvictableEntry(ReferenceQueue queue, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
         super(queue, key, hash, next);
      }

      public MapMakerInternalMap.ReferenceEntry getNextEvictable() {
         return this.nextEvictable;
      }

      public void setNextEvictable(MapMakerInternalMap.ReferenceEntry next) {
         this.nextEvictable = next;
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousEvictable() {
         return this.previousEvictable;
      }

      public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry previous) {
         this.previousEvictable = previous;
      }
   }

   static final class WeakExpirableEntry extends MapMakerInternalMap.WeakEntry implements MapMakerInternalMap.ReferenceEntry {
      volatile long time = Long.MAX_VALUE;
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry nextExpirable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry previousExpirable = MapMakerInternalMap.nullEntry();

      WeakExpirableEntry(ReferenceQueue queue, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
         super(queue, key, hash, next);
      }

      public long getExpirationTime() {
         return this.time;
      }

      public void setExpirationTime(long time) {
         this.time = time;
      }

      public MapMakerInternalMap.ReferenceEntry getNextExpirable() {
         return this.nextExpirable;
      }

      public void setNextExpirable(MapMakerInternalMap.ReferenceEntry next) {
         this.nextExpirable = next;
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousExpirable() {
         return this.previousExpirable;
      }

      public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry previous) {
         this.previousExpirable = previous;
      }
   }

   static final class WeakExpirableEvictableEntry extends MapMakerInternalMap.WeakEntry implements MapMakerInternalMap.ReferenceEntry {
      volatile long time = Long.MAX_VALUE;
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry nextExpirable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry previousExpirable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry nextEvictable = MapMakerInternalMap.nullEntry();
      @GuardedBy("Segment.this")
      MapMakerInternalMap.ReferenceEntry previousEvictable = MapMakerInternalMap.nullEntry();

      WeakExpirableEvictableEntry(ReferenceQueue queue, Object key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry next) {
         super(queue, key, hash, next);
      }

      public long getExpirationTime() {
         return this.time;
      }

      public void setExpirationTime(long time) {
         this.time = time;
      }

      public MapMakerInternalMap.ReferenceEntry getNextExpirable() {
         return this.nextExpirable;
      }

      public void setNextExpirable(MapMakerInternalMap.ReferenceEntry next) {
         this.nextExpirable = next;
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousExpirable() {
         return this.previousExpirable;
      }

      public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry previous) {
         this.previousExpirable = previous;
      }

      public MapMakerInternalMap.ReferenceEntry getNextEvictable() {
         return this.nextEvictable;
      }

      public void setNextEvictable(MapMakerInternalMap.ReferenceEntry next) {
         this.nextEvictable = next;
      }

      public MapMakerInternalMap.ReferenceEntry getPreviousEvictable() {
         return this.previousEvictable;
      }

      public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry previous) {
         this.previousEvictable = previous;
      }
   }

   static final class WeakValueReference extends WeakReference implements MapMakerInternalMap.ValueReference {
      final MapMakerInternalMap.ReferenceEntry entry;

      WeakValueReference(ReferenceQueue queue, Object referent, MapMakerInternalMap.ReferenceEntry entry) {
         super(referent, queue);
         this.entry = entry;
      }

      public MapMakerInternalMap.ReferenceEntry getEntry() {
         return this.entry;
      }

      public void clear(MapMakerInternalMap.ValueReference newValue) {
         this.clear();
      }

      public MapMakerInternalMap.ValueReference copyFor(ReferenceQueue queue, Object value, MapMakerInternalMap.ReferenceEntry entry) {
         return new MapMakerInternalMap.WeakValueReference(queue, value, entry);
      }

      public boolean isComputingReference() {
         return false;
      }

      public Object waitForValue() {
         return this.get();
      }
   }

   final class WriteThroughEntry extends AbstractMapEntry {
      final Object key;
      Object value;

      WriteThroughEntry(Object key, Object value) {
         this.key = key;
         this.value = value;
      }

      public Object getKey() {
         return this.key;
      }

      public Object getValue() {
         return this.value;
      }

      public boolean equals(@Nullable Object object) {
         if(!(object instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> that = (Entry)object;
            return this.key.equals(that.getKey()) && this.value.equals(that.getValue());
         }
      }

      public int hashCode() {
         return this.key.hashCode() ^ this.value.hashCode();
      }

      public Object setValue(Object newValue) {
         V oldValue = MapMakerInternalMap.this.put(this.key, newValue);
         this.value = newValue;
         return oldValue;
      }
   }
}
