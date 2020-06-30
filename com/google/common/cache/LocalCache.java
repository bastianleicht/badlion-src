package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.common.cache.AbstractCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.ForwardingCache;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.cache.Weigher;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.Uninterruptibles;
import java.io.IOException;
import java.io.ObjectInputStream;
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
import java.util.concurrent.Callable;
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

@GwtCompatible(
   emulated = true
)
class LocalCache extends AbstractMap implements ConcurrentMap {
   static final int MAXIMUM_CAPACITY = 1073741824;
   static final int MAX_SEGMENTS = 65536;
   static final int CONTAINS_VALUE_RETRIES = 3;
   static final int DRAIN_THRESHOLD = 63;
   static final int DRAIN_MAX = 16;
   static final Logger logger = Logger.getLogger(LocalCache.class.getName());
   static final ListeningExecutorService sameThreadExecutor = MoreExecutors.sameThreadExecutor();
   final int segmentMask;
   final int segmentShift;
   final LocalCache.Segment[] segments;
   final int concurrencyLevel;
   final Equivalence keyEquivalence;
   final Equivalence valueEquivalence;
   final LocalCache.Strength keyStrength;
   final LocalCache.Strength valueStrength;
   final long maxWeight;
   final Weigher weigher;
   final long expireAfterAccessNanos;
   final long expireAfterWriteNanos;
   final long refreshNanos;
   final Queue removalNotificationQueue;
   final RemovalListener removalListener;
   final Ticker ticker;
   final LocalCache.EntryFactory entryFactory;
   final AbstractCache.StatsCounter globalStatsCounter;
   @Nullable
   final CacheLoader defaultLoader;
   static final LocalCache.ValueReference UNSET = new LocalCache.ValueReference() {
      public Object get() {
         return null;
      }

      public int getWeight() {
         return 0;
      }

      public LocalCache.ReferenceEntry getEntry() {
         return null;
      }

      public LocalCache.ValueReference copyFor(ReferenceQueue queue, @Nullable Object value, LocalCache.ReferenceEntry entry) {
         return this;
      }

      public boolean isLoading() {
         return false;
      }

      public boolean isActive() {
         return false;
      }

      public Object waitForValue() {
         return null;
      }

      public void notifyNewValue(Object newValue) {
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
   Set keySet;
   Collection values;
   Set entrySet;

   LocalCache(CacheBuilder builder, @Nullable CacheLoader loader) {
      this.concurrencyLevel = Math.min(builder.getConcurrencyLevel(), 65536);
      this.keyStrength = builder.getKeyStrength();
      this.valueStrength = builder.getValueStrength();
      this.keyEquivalence = builder.getKeyEquivalence();
      this.valueEquivalence = builder.getValueEquivalence();
      this.maxWeight = builder.getMaximumWeight();
      this.weigher = builder.getWeigher();
      this.expireAfterAccessNanos = builder.getExpireAfterAccessNanos();
      this.expireAfterWriteNanos = builder.getExpireAfterWriteNanos();
      this.refreshNanos = builder.getRefreshNanos();
      this.removalListener = builder.getRemovalListener();
      this.removalNotificationQueue = (Queue)(this.removalListener == CacheBuilder.NullListener.INSTANCE?discardingQueue():new ConcurrentLinkedQueue());
      this.ticker = builder.getTicker(this.recordsTime());
      this.entryFactory = LocalCache.EntryFactory.getFactory(this.keyStrength, this.usesAccessEntries(), this.usesWriteEntries());
      this.globalStatsCounter = (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get();
      this.defaultLoader = loader;
      int initialCapacity = Math.min(builder.getInitialCapacity(), 1073741824);
      if(this.evictsBySize() && !this.customWeigher()) {
         initialCapacity = Math.min(initialCapacity, (int)this.maxWeight);
      }

      int segmentShift = 0;

      int segmentCount;
      for(segmentCount = 1; segmentCount < this.concurrencyLevel && (!this.evictsBySize() || (long)(segmentCount * 20) <= this.maxWeight); segmentCount <<= 1) {
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
         long maxSegmentWeight = this.maxWeight / (long)segmentCount + 1L;
         long remainder = this.maxWeight % (long)segmentCount;

         for(int i = 0; i < this.segments.length; ++i) {
            if((long)i == remainder) {
               --maxSegmentWeight;
            }

            this.segments[i] = this.createSegment(segmentSize, maxSegmentWeight, (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
         }
      } else {
         for(int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = this.createSegment(segmentSize, -1L, (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
         }
      }

   }

   boolean evictsBySize() {
      return this.maxWeight >= 0L;
   }

   boolean customWeigher() {
      return this.weigher != CacheBuilder.OneWeigher.INSTANCE;
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

   boolean refreshes() {
      return this.refreshNanos > 0L;
   }

   boolean usesAccessQueue() {
      return this.expiresAfterAccess() || this.evictsBySize();
   }

   boolean usesWriteQueue() {
      return this.expiresAfterWrite();
   }

   boolean recordsWrite() {
      return this.expiresAfterWrite() || this.refreshes();
   }

   boolean recordsAccess() {
      return this.expiresAfterAccess();
   }

   boolean recordsTime() {
      return this.recordsWrite() || this.recordsAccess();
   }

   boolean usesWriteEntries() {
      return this.usesWriteQueue() || this.recordsWrite();
   }

   boolean usesAccessEntries() {
      return this.usesAccessQueue() || this.recordsAccess();
   }

   boolean usesKeyReferences() {
      return this.keyStrength != LocalCache.Strength.STRONG;
   }

   boolean usesValueReferences() {
      return this.valueStrength != LocalCache.Strength.STRONG;
   }

   static LocalCache.ValueReference unset() {
      return UNSET;
   }

   static LocalCache.ReferenceEntry nullEntry() {
      return LocalCache.NullEntry.INSTANCE;
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
   LocalCache.ReferenceEntry newEntry(Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
      return this.segmentFor(hash).newEntry(key, hash, next);
   }

   @GuardedBy("Segment.this")
   @VisibleForTesting
   LocalCache.ReferenceEntry copyEntry(LocalCache.ReferenceEntry original, LocalCache.ReferenceEntry newNext) {
      int hash = original.getHash();
      return this.segmentFor(hash).copyEntry(original, newNext);
   }

   @GuardedBy("Segment.this")
   @VisibleForTesting
   LocalCache.ValueReference newValueReference(LocalCache.ReferenceEntry entry, Object value, int weight) {
      int hash = entry.getHash();
      return this.valueStrength.referenceValue(this.segmentFor(hash), entry, Preconditions.checkNotNull(value), weight);
   }

   int hash(@Nullable Object key) {
      int h = this.keyEquivalence.hash(key);
      return rehash(h);
   }

   void reclaimValue(LocalCache.ValueReference valueReference) {
      LocalCache.ReferenceEntry<K, V> entry = valueReference.getEntry();
      int hash = entry.getHash();
      this.segmentFor(hash).reclaimValue(entry.getKey(), hash, valueReference);
   }

   void reclaimKey(LocalCache.ReferenceEntry entry) {
      int hash = entry.getHash();
      this.segmentFor(hash).reclaimKey(entry, hash);
   }

   @VisibleForTesting
   boolean isLive(LocalCache.ReferenceEntry entry, long now) {
      return this.segmentFor(entry.getHash()).getLiveValue(entry, now) != null;
   }

   LocalCache.Segment segmentFor(int hash) {
      return this.segments[hash >>> this.segmentShift & this.segmentMask];
   }

   LocalCache.Segment createSegment(int initialCapacity, long maxSegmentWeight, AbstractCache.StatsCounter statsCounter) {
      return new LocalCache.Segment(this, initialCapacity, maxSegmentWeight, statsCounter);
   }

   @Nullable
   Object getLiveValue(LocalCache.ReferenceEntry entry, long now) {
      if(entry.getKey() == null) {
         return null;
      } else {
         V value = entry.getValueReference().get();
         return value == null?null:(this.isExpired(entry, now)?null:value);
      }
   }

   boolean isExpired(LocalCache.ReferenceEntry entry, long now) {
      Preconditions.checkNotNull(entry);
      return this.expiresAfterAccess() && now - entry.getAccessTime() >= this.expireAfterAccessNanos?true:this.expiresAfterWrite() && now - entry.getWriteTime() >= this.expireAfterWriteNanos;
   }

   @GuardedBy("Segment.this")
   static void connectAccessOrder(LocalCache.ReferenceEntry previous, LocalCache.ReferenceEntry next) {
      previous.setNextInAccessQueue(next);
      next.setPreviousInAccessQueue(previous);
   }

   @GuardedBy("Segment.this")
   static void nullifyAccessOrder(LocalCache.ReferenceEntry nulled) {
      LocalCache.ReferenceEntry<K, V> nullEntry = nullEntry();
      nulled.setNextInAccessQueue(nullEntry);
      nulled.setPreviousInAccessQueue(nullEntry);
   }

   @GuardedBy("Segment.this")
   static void connectWriteOrder(LocalCache.ReferenceEntry previous, LocalCache.ReferenceEntry next) {
      previous.setNextInWriteQueue(next);
      next.setPreviousInWriteQueue(previous);
   }

   @GuardedBy("Segment.this")
   static void nullifyWriteOrder(LocalCache.ReferenceEntry nulled) {
      LocalCache.ReferenceEntry<K, V> nullEntry = nullEntry();
      nulled.setNextInWriteQueue(nullEntry);
      nulled.setPreviousInWriteQueue(nullEntry);
   }

   void processPendingNotifications() {
      RemovalNotification<K, V> notification;
      while((notification = (RemovalNotification)this.removalNotificationQueue.poll()) != null) {
         try {
            this.removalListener.onRemoval(notification);
         } catch (Throwable var3) {
            logger.log(Level.WARNING, "Exception thrown by removal listener", var3);
         }
      }

   }

   final LocalCache.Segment[] newSegmentArray(int ssize) {
      return new LocalCache.Segment[ssize];
   }

   public void cleanUp() {
      for(LocalCache.Segment<?, ?> segment : this.segments) {
         segment.cleanUp();
      }

   }

   public boolean isEmpty() {
      long sum = 0L;
      LocalCache.Segment<K, V>[] segments = this.segments;

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

   long longSize() {
      LocalCache.Segment<K, V>[] segments = this.segments;
      long sum = 0L;

      for(int i = 0; i < segments.length; ++i) {
         sum += (long)segments[i].count;
      }

      return sum;
   }

   public int size() {
      return Ints.saturatedCast(this.longSize());
   }

   @Nullable
   public Object get(@Nullable Object key) {
      if(key == null) {
         return null;
      } else {
         int hash = this.hash(key);
         return this.segmentFor(hash).get(key, hash);
      }
   }

   @Nullable
   public Object getIfPresent(Object key) {
      int hash = this.hash(Preconditions.checkNotNull(key));
      V value = this.segmentFor(hash).get(key, hash);
      if(value == null) {
         this.globalStatsCounter.recordMisses(1);
      } else {
         this.globalStatsCounter.recordHits(1);
      }

      return value;
   }

   Object get(Object key, CacheLoader loader) throws ExecutionException {
      int hash = this.hash(Preconditions.checkNotNull(key));
      return this.segmentFor(hash).get(key, hash, loader);
   }

   Object getOrLoad(Object key) throws ExecutionException {
      return this.get(key, this.defaultLoader);
   }

   ImmutableMap getAllPresent(Iterable keys) {
      int hits = 0;
      int misses = 0;
      Map<K, V> result = Maps.newLinkedHashMap();

      for(Object key : keys) {
         V value = this.get(key);
         if(value == null) {
            ++misses;
         } else {
            result.put(key, value);
            ++hits;
         }
      }

      this.globalStatsCounter.recordHits(hits);
      this.globalStatsCounter.recordMisses(misses);
      return ImmutableMap.copyOf(result);
   }

   ImmutableMap getAll(Iterable keys) throws ExecutionException {
      int hits = 0;
      int misses = 0;
      Map<K, V> result = Maps.newLinkedHashMap();
      Set<K> keysToLoad = Sets.newLinkedHashSet();

      for(K key : keys) {
         V value = this.get(key);
         if(!result.containsKey(key)) {
            result.put(key, value);
            if(value == null) {
               ++misses;
               keysToLoad.add(key);
            } else {
               ++hits;
            }
         }
      }

      ImmutableMap e;
      try {
         if(!keysToLoad.isEmpty()) {
            try {
               Map<K, V> newEntries = this.loadAll(keysToLoad, this.defaultLoader);

               for(K key : keysToLoad) {
                  V value = newEntries.get(key);
                  if(value == null) {
                     throw new CacheLoader.InvalidCacheLoadException("loadAll failed to return a value for " + key);
                  }

                  result.put(key, value);
               }
            } catch (CacheLoader.UnsupportedLoadingOperationException var13) {
               for(K key : keysToLoad) {
                  --misses;
                  result.put(key, this.get(key, this.defaultLoader));
               }
            }
         }

         e = ImmutableMap.copyOf(result);
      } finally {
         this.globalStatsCounter.recordHits(hits);
         this.globalStatsCounter.recordMisses(misses);
      }

      return e;
   }

   @Nullable
   Map loadAll(Set keys, CacheLoader loader) throws ExecutionException {
      Preconditions.checkNotNull(loader);
      Preconditions.checkNotNull(keys);
      Stopwatch stopwatch = Stopwatch.createStarted();
      boolean success = false;

      Map<K, V> result;
      try {
         Map<K, V> map = loader.loadAll(keys);
         result = map;
         success = true;
      } catch (CacheLoader.UnsupportedLoadingOperationException var17) {
         success = true;
         throw var17;
      } catch (InterruptedException var18) {
         Thread.currentThread().interrupt();
         throw new ExecutionException(var18);
      } catch (RuntimeException var19) {
         throw new UncheckedExecutionException(var19);
      } catch (Exception var20) {
         throw new ExecutionException(var20);
      } catch (Error var21) {
         throw new ExecutionError(var21);
      } finally {
         if(!success) {
            this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
         }

      }

      if(result == null) {
         this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
         throw new CacheLoader.InvalidCacheLoadException(loader + " returned null map from loadAll");
      } else {
         stopwatch.stop();
         boolean nullsPresent = false;

         for(Entry<K, V> entry : result.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            if(key != null && value != null) {
               this.put(key, value);
            } else {
               nullsPresent = true;
            }
         }

         if(nullsPresent) {
            this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
            throw new CacheLoader.InvalidCacheLoadException(loader + " returned null keys or values from loadAll");
         } else {
            this.globalStatsCounter.recordLoadSuccess(stopwatch.elapsed(TimeUnit.NANOSECONDS));
            return result;
         }
      }
   }

   LocalCache.ReferenceEntry getEntry(@Nullable Object key) {
      if(key == null) {
         return null;
      } else {
         int hash = this.hash(key);
         return this.segmentFor(hash).getEntry(key, hash);
      }
   }

   void refresh(Object key) {
      int hash = this.hash(Preconditions.checkNotNull(key));
      this.segmentFor(hash).refresh(key, hash, this.defaultLoader, false);
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
         long now = this.ticker.read();
         LocalCache.Segment<K, V>[] segments = this.segments;
         long last = -1L;

         for(int i = 0; i < 3; ++i) {
            long sum = 0L;

            for(LocalCache.Segment<K, V> segment : segments) {
               int c = segment.count;
               AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = segment.table;

               for(int j = 0; j < table.length(); ++j) {
                  for(LocalCache.ReferenceEntry<K, V> e = (LocalCache.ReferenceEntry)table.get(j); e != null; e = e.getNext()) {
                     V v = segment.getLiveValue(e, now);
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
      for(LocalCache.Segment<K, V> segment : this.segments) {
         segment.clear();
      }

   }

   void invalidateAll(Iterable keys) {
      for(Object key : keys) {
         this.remove(key);
      }

   }

   public Set keySet() {
      Set<K> ks = this.keySet;
      return ks != null?ks:(this.keySet = new LocalCache.KeySet(this));
   }

   public Collection values() {
      Collection<V> vs = this.values;
      return vs != null?vs:(this.values = new LocalCache.Values(this));
   }

   @GwtIncompatible("Not supported.")
   public Set entrySet() {
      Set<Entry<K, V>> es = this.entrySet;
      return es != null?es:(this.entrySet = new LocalCache.EntrySet(this));
   }

   abstract class AbstractCacheSet extends AbstractSet {
      final ConcurrentMap map;

      AbstractCacheSet(ConcurrentMap map) {
         this.map = map;
      }

      public int size() {
         return this.map.size();
      }

      public boolean isEmpty() {
         return this.map.isEmpty();
      }

      public void clear() {
         this.map.clear();
      }
   }

   abstract static class AbstractReferenceEntry implements LocalCache.ReferenceEntry {
      public LocalCache.ValueReference getValueReference() {
         throw new UnsupportedOperationException();
      }

      public void setValueReference(LocalCache.ValueReference valueReference) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry getNext() {
         throw new UnsupportedOperationException();
      }

      public int getHash() {
         throw new UnsupportedOperationException();
      }

      public Object getKey() {
         throw new UnsupportedOperationException();
      }

      public long getAccessTime() {
         throw new UnsupportedOperationException();
      }

      public void setAccessTime(long time) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry getNextInAccessQueue() {
         throw new UnsupportedOperationException();
      }

      public void setNextInAccessQueue(LocalCache.ReferenceEntry next) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry getPreviousInAccessQueue() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry previous) {
         throw new UnsupportedOperationException();
      }

      public long getWriteTime() {
         throw new UnsupportedOperationException();
      }

      public void setWriteTime(long time) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry getNextInWriteQueue() {
         throw new UnsupportedOperationException();
      }

      public void setNextInWriteQueue(LocalCache.ReferenceEntry next) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry getPreviousInWriteQueue() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry previous) {
         throw new UnsupportedOperationException();
      }
   }

   static final class AccessQueue extends AbstractQueue {
      final LocalCache.ReferenceEntry head = new LocalCache.AbstractReferenceEntry() {
         LocalCache.ReferenceEntry nextAccess = this;
         LocalCache.ReferenceEntry previousAccess = this;

         public long getAccessTime() {
            return Long.MAX_VALUE;
         }

         public void setAccessTime(long time) {
         }

         public LocalCache.ReferenceEntry getNextInAccessQueue() {
            return this.nextAccess;
         }

         public void setNextInAccessQueue(LocalCache.ReferenceEntry next) {
            this.nextAccess = next;
         }

         public LocalCache.ReferenceEntry getPreviousInAccessQueue() {
            return this.previousAccess;
         }

         public void setPreviousInAccessQueue(LocalCache.ReferenceEntry previous) {
            this.previousAccess = previous;
         }
      };

      public boolean offer(LocalCache.ReferenceEntry entry) {
         LocalCache.connectAccessOrder(entry.getPreviousInAccessQueue(), entry.getNextInAccessQueue());
         LocalCache.connectAccessOrder(this.head.getPreviousInAccessQueue(), entry);
         LocalCache.connectAccessOrder(entry, this.head);
         return true;
      }

      public LocalCache.ReferenceEntry peek() {
         LocalCache.ReferenceEntry<K, V> next = this.head.getNextInAccessQueue();
         return next == this.head?null:next;
      }

      public LocalCache.ReferenceEntry poll() {
         LocalCache.ReferenceEntry<K, V> next = this.head.getNextInAccessQueue();
         if(next == this.head) {
            return null;
         } else {
            this.remove(next);
            return next;
         }
      }

      public boolean remove(Object o) {
         LocalCache.ReferenceEntry<K, V> e = (LocalCache.ReferenceEntry)o;
         LocalCache.ReferenceEntry<K, V> previous = e.getPreviousInAccessQueue();
         LocalCache.ReferenceEntry<K, V> next = e.getNextInAccessQueue();
         LocalCache.connectAccessOrder(previous, next);
         LocalCache.nullifyAccessOrder(e);
         return next != LocalCache.NullEntry.INSTANCE;
      }

      public boolean contains(Object o) {
         LocalCache.ReferenceEntry<K, V> e = (LocalCache.ReferenceEntry)o;
         return e.getNextInAccessQueue() != LocalCache.NullEntry.INSTANCE;
      }

      public boolean isEmpty() {
         return this.head.getNextInAccessQueue() == this.head;
      }

      public int size() {
         int size = 0;

         for(LocalCache.ReferenceEntry<K, V> e = this.head.getNextInAccessQueue(); e != this.head; e = e.getNextInAccessQueue()) {
            ++size;
         }

         return size;
      }

      public void clear() {
         LocalCache.ReferenceEntry<K, V> next;
         for(LocalCache.ReferenceEntry<K, V> e = this.head.getNextInAccessQueue(); e != this.head; e = next) {
            next = e.getNextInAccessQueue();
            LocalCache.nullifyAccessOrder(e);
         }

         this.head.setNextInAccessQueue(this.head);
         this.head.setPreviousInAccessQueue(this.head);
      }

      public Iterator iterator() {
         return new AbstractSequentialIterator(this.peek()) {
            protected LocalCache.ReferenceEntry computeNext(LocalCache.ReferenceEntry previous) {
               LocalCache.ReferenceEntry<K, V> next = previous.getNextInAccessQueue();
               return next == AccessQueue.this.head?null:next;
            }
         };
      }
   }

   static enum EntryFactory {
      STRONG {
         LocalCache.ReferenceEntry newEntry(LocalCache.Segment segment, Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
            return new LocalCache.StrongEntry(key, hash, next);
         }
      },
      STRONG_ACCESS {
         LocalCache.ReferenceEntry newEntry(LocalCache.Segment segment, Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
            return new LocalCache.StrongAccessEntry(key, hash, next);
         }

         LocalCache.ReferenceEntry copyEntry(LocalCache.Segment segment, LocalCache.ReferenceEntry original, LocalCache.ReferenceEntry newNext) {
            LocalCache.ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
            this.copyAccessEntry(original, newEntry);
            return newEntry;
         }
      },
      STRONG_WRITE {
         LocalCache.ReferenceEntry newEntry(LocalCache.Segment segment, Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
            return new LocalCache.StrongWriteEntry(key, hash, next);
         }

         LocalCache.ReferenceEntry copyEntry(LocalCache.Segment segment, LocalCache.ReferenceEntry original, LocalCache.ReferenceEntry newNext) {
            LocalCache.ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
            this.copyWriteEntry(original, newEntry);
            return newEntry;
         }
      },
      STRONG_ACCESS_WRITE {
         LocalCache.ReferenceEntry newEntry(LocalCache.Segment segment, Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
            return new LocalCache.StrongAccessWriteEntry(key, hash, next);
         }

         LocalCache.ReferenceEntry copyEntry(LocalCache.Segment segment, LocalCache.ReferenceEntry original, LocalCache.ReferenceEntry newNext) {
            LocalCache.ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
            this.copyAccessEntry(original, newEntry);
            this.copyWriteEntry(original, newEntry);
            return newEntry;
         }
      },
      WEAK {
         LocalCache.ReferenceEntry newEntry(LocalCache.Segment segment, Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
            return new LocalCache.WeakEntry(segment.keyReferenceQueue, key, hash, next);
         }
      },
      WEAK_ACCESS {
         LocalCache.ReferenceEntry newEntry(LocalCache.Segment segment, Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
            return new LocalCache.WeakAccessEntry(segment.keyReferenceQueue, key, hash, next);
         }

         LocalCache.ReferenceEntry copyEntry(LocalCache.Segment segment, LocalCache.ReferenceEntry original, LocalCache.ReferenceEntry newNext) {
            LocalCache.ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
            this.copyAccessEntry(original, newEntry);
            return newEntry;
         }
      },
      WEAK_WRITE {
         LocalCache.ReferenceEntry newEntry(LocalCache.Segment segment, Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
            return new LocalCache.WeakWriteEntry(segment.keyReferenceQueue, key, hash, next);
         }

         LocalCache.ReferenceEntry copyEntry(LocalCache.Segment segment, LocalCache.ReferenceEntry original, LocalCache.ReferenceEntry newNext) {
            LocalCache.ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
            this.copyWriteEntry(original, newEntry);
            return newEntry;
         }
      },
      WEAK_ACCESS_WRITE {
         LocalCache.ReferenceEntry newEntry(LocalCache.Segment segment, Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
            return new LocalCache.WeakAccessWriteEntry(segment.keyReferenceQueue, key, hash, next);
         }

         LocalCache.ReferenceEntry copyEntry(LocalCache.Segment segment, LocalCache.ReferenceEntry original, LocalCache.ReferenceEntry newNext) {
            LocalCache.ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext);
            this.copyAccessEntry(original, newEntry);
            this.copyWriteEntry(original, newEntry);
            return newEntry;
         }
      };

      static final int ACCESS_MASK = 1;
      static final int WRITE_MASK = 2;
      static final int WEAK_MASK = 4;
      static final LocalCache.EntryFactory[] factories = new LocalCache.EntryFactory[]{STRONG, STRONG_ACCESS, STRONG_WRITE, STRONG_ACCESS_WRITE, WEAK, WEAK_ACCESS, WEAK_WRITE, WEAK_ACCESS_WRITE};

      private EntryFactory() {
      }

      static LocalCache.EntryFactory getFactory(LocalCache.Strength keyStrength, boolean usesAccessQueue, boolean usesWriteQueue) {
         int flags = (keyStrength == LocalCache.Strength.WEAK?4:0) | (usesAccessQueue?1:0) | (usesWriteQueue?2:0);
         return factories[flags];
      }

      abstract LocalCache.ReferenceEntry newEntry(LocalCache.Segment var1, Object var2, int var3, @Nullable LocalCache.ReferenceEntry var4);

      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry copyEntry(LocalCache.Segment segment, LocalCache.ReferenceEntry original, LocalCache.ReferenceEntry newNext) {
         return this.newEntry(segment, original.getKey(), original.getHash(), newNext);
      }

      @GuardedBy("Segment.this")
      void copyAccessEntry(LocalCache.ReferenceEntry original, LocalCache.ReferenceEntry newEntry) {
         newEntry.setAccessTime(original.getAccessTime());
         LocalCache.connectAccessOrder(original.getPreviousInAccessQueue(), newEntry);
         LocalCache.connectAccessOrder(newEntry, original.getNextInAccessQueue());
         LocalCache.nullifyAccessOrder(original);
      }

      @GuardedBy("Segment.this")
      void copyWriteEntry(LocalCache.ReferenceEntry original, LocalCache.ReferenceEntry newEntry) {
         newEntry.setWriteTime(original.getWriteTime());
         LocalCache.connectWriteOrder(original.getPreviousInWriteQueue(), newEntry);
         LocalCache.connectWriteOrder(newEntry, original.getNextInWriteQueue());
         LocalCache.nullifyWriteOrder(original);
      }
   }

   final class EntryIterator extends LocalCache.HashIterator {
      EntryIterator() {
         super();
      }

      public Entry next() {
         return this.nextEntry();
      }
   }

   final class EntrySet extends LocalCache.AbstractCacheSet {
      EntrySet(ConcurrentMap map) {
         super(map);
      }

      public Iterator iterator() {
         return LocalCache.this.new EntryIterator();
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
               V v = LocalCache.this.get(key);
               return v != null && LocalCache.this.valueEquivalence.equivalent(e.getValue(), v);
            }
         }
      }

      public boolean remove(Object o) {
         if(!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry)o;
            Object key = e.getKey();
            return key != null && LocalCache.this.remove(key, e.getValue());
         }
      }
   }

   abstract class HashIterator implements Iterator {
      int nextSegmentIndex;
      int nextTableIndex;
      LocalCache.Segment currentSegment;
      AtomicReferenceArray currentTable;
      LocalCache.ReferenceEntry nextEntry;
      LocalCache.WriteThroughEntry nextExternal;
      LocalCache.WriteThroughEntry lastReturned;

      HashIterator() {
         this.nextSegmentIndex = LocalCache.this.segments.length - 1;
         this.nextTableIndex = -1;
         this.advance();
      }

      public abstract Object next();

      final void advance() {
         this.nextExternal = null;
         if(!this.nextInChain()) {
            if(!this.nextInTable()) {
               while(this.nextSegmentIndex >= 0) {
                  this.currentSegment = LocalCache.this.segments[this.nextSegmentIndex--];
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
               if((this.nextEntry = (LocalCache.ReferenceEntry)this.currentTable.get(this.nextTableIndex--)) == null || !this.advanceTo(this.nextEntry) && !this.nextInChain()) {
                  continue;
               }

               return true;
            }

            return false;
         }
      }

      boolean advanceTo(LocalCache.ReferenceEntry entry) {
         boolean var6;
         try {
            long now = LocalCache.this.ticker.read();
            K key = entry.getKey();
            V value = LocalCache.this.getLiveValue(entry, now);
            if(value == null) {
               var6 = false;
               return var6;
            }

            this.nextExternal = LocalCache.this.new WriteThroughEntry(key, value);
            var6 = true;
         } finally {
            this.currentSegment.postReadCleanup();
         }

         return var6;
      }

      public boolean hasNext() {
         return this.nextExternal != null;
      }

      LocalCache.WriteThroughEntry nextEntry() {
         if(this.nextExternal == null) {
            throw new NoSuchElementException();
         } else {
            this.lastReturned = this.nextExternal;
            this.advance();
            return this.lastReturned;
         }
      }

      public void remove() {
         Preconditions.checkState(this.lastReturned != null);
         LocalCache.this.remove(this.lastReturned.getKey());
         this.lastReturned = null;
      }
   }

   final class KeyIterator extends LocalCache.HashIterator {
      KeyIterator() {
         super();
      }

      public Object next() {
         return this.nextEntry().getKey();
      }
   }

   final class KeySet extends LocalCache.AbstractCacheSet {
      KeySet(ConcurrentMap map) {
         super(map);
      }

      public Iterator iterator() {
         return LocalCache.this.new KeyIterator();
      }

      public boolean contains(Object o) {
         return this.map.containsKey(o);
      }

      public boolean remove(Object o) {
         return this.map.remove(o) != null;
      }
   }

   static final class LoadingSerializationProxy extends LocalCache.ManualSerializationProxy implements LoadingCache, Serializable {
      private static final long serialVersionUID = 1L;
      transient LoadingCache autoDelegate;

      LoadingSerializationProxy(LocalCache cache) {
         super(cache);
      }

      private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         CacheBuilder<K, V> builder = this.recreateCacheBuilder();
         this.autoDelegate = builder.build(this.loader);
      }

      public Object get(Object key) throws ExecutionException {
         return this.autoDelegate.get(key);
      }

      public Object getUnchecked(Object key) {
         return this.autoDelegate.getUnchecked(key);
      }

      public ImmutableMap getAll(Iterable keys) throws ExecutionException {
         return this.autoDelegate.getAll(keys);
      }

      public final Object apply(Object key) {
         return this.autoDelegate.apply(key);
      }

      public void refresh(Object key) {
         this.autoDelegate.refresh(key);
      }

      private Object readResolve() {
         return this.autoDelegate;
      }
   }

   static class LoadingValueReference implements LocalCache.ValueReference {
      volatile LocalCache.ValueReference oldValue;
      final SettableFuture futureValue;
      final Stopwatch stopwatch;

      public LoadingValueReference() {
         this(LocalCache.UNSET);
      }

      public LoadingValueReference(LocalCache.ValueReference oldValue) {
         this.futureValue = SettableFuture.create();
         this.stopwatch = Stopwatch.createUnstarted();
         this.oldValue = oldValue;
      }

      public boolean isLoading() {
         return true;
      }

      public boolean isActive() {
         return this.oldValue.isActive();
      }

      public int getWeight() {
         return this.oldValue.getWeight();
      }

      public boolean set(@Nullable Object newValue) {
         return this.futureValue.set(newValue);
      }

      public boolean setException(Throwable t) {
         return this.futureValue.setException(t);
      }

      private ListenableFuture fullyFailedFuture(Throwable t) {
         return Futures.immediateFailedFuture(t);
      }

      public void notifyNewValue(@Nullable Object newValue) {
         if(newValue != null) {
            this.set(newValue);
         } else {
            this.oldValue = LocalCache.UNSET;
         }

      }

      public ListenableFuture loadFuture(Object key, CacheLoader loader) {
         this.stopwatch.start();
         V previousValue = this.oldValue.get();

         try {
            if(previousValue == null) {
               V newValue = loader.load(key);
               return (ListenableFuture)(this.set(newValue)?this.futureValue:Futures.immediateFuture(newValue));
            } else {
               ListenableFuture<V> newValue = loader.reload(key, previousValue);
               return newValue == null?Futures.immediateFuture((Object)null):Futures.transform(newValue, new Function() {
                  public Object apply(Object newValue) {
                     LoadingValueReference.this.set(newValue);
                     return newValue;
                  }
               });
            }
         } catch (Throwable var5) {
            if(var5 instanceof InterruptedException) {
               Thread.currentThread().interrupt();
            }

            return (ListenableFuture)(this.setException(var5)?this.futureValue:this.fullyFailedFuture(var5));
         }
      }

      public long elapsedNanos() {
         return this.stopwatch.elapsed(TimeUnit.NANOSECONDS);
      }

      public Object waitForValue() throws ExecutionException {
         return Uninterruptibles.getUninterruptibly(this.futureValue);
      }

      public Object get() {
         return this.oldValue.get();
      }

      public LocalCache.ValueReference getOldValue() {
         return this.oldValue;
      }

      public LocalCache.ReferenceEntry getEntry() {
         return null;
      }

      public LocalCache.ValueReference copyFor(ReferenceQueue queue, @Nullable Object value, LocalCache.ReferenceEntry entry) {
         return this;
      }
   }

   static class LocalLoadingCache extends LocalCache.LocalManualCache implements LoadingCache {
      private static final long serialVersionUID = 1L;

      LocalLoadingCache(CacheBuilder builder, CacheLoader loader) {
         super(new LocalCache(builder, (CacheLoader)Preconditions.checkNotNull(loader)), null);
      }

      public Object get(Object key) throws ExecutionException {
         return this.localCache.getOrLoad(key);
      }

      public Object getUnchecked(Object key) {
         try {
            return this.get(key);
         } catch (ExecutionException var3) {
            throw new UncheckedExecutionException(var3.getCause());
         }
      }

      public ImmutableMap getAll(Iterable keys) throws ExecutionException {
         return this.localCache.getAll(keys);
      }

      public void refresh(Object key) {
         this.localCache.refresh(key);
      }

      public final Object apply(Object key) {
         return this.getUnchecked(key);
      }

      Object writeReplace() {
         return new LocalCache.LoadingSerializationProxy(this.localCache);
      }
   }

   static class LocalManualCache implements Cache, Serializable {
      final LocalCache localCache;
      private static final long serialVersionUID = 1L;

      LocalManualCache(CacheBuilder builder) {
         this(new LocalCache(builder, (CacheLoader)null));
      }

      private LocalManualCache(LocalCache localCache) {
         this.localCache = localCache;
      }

      @Nullable
      public Object getIfPresent(Object key) {
         return this.localCache.getIfPresent(key);
      }

      public Object get(Object key, final Callable valueLoader) throws ExecutionException {
         Preconditions.checkNotNull(valueLoader);
         return this.localCache.get(key, new CacheLoader() {
            public Object load(Object key) throws Exception {
               return valueLoader.call();
            }
         });
      }

      public ImmutableMap getAllPresent(Iterable keys) {
         return this.localCache.getAllPresent(keys);
      }

      public void put(Object key, Object value) {
         this.localCache.put(key, value);
      }

      public void putAll(Map m) {
         this.localCache.putAll(m);
      }

      public void invalidate(Object key) {
         Preconditions.checkNotNull(key);
         this.localCache.remove(key);
      }

      public void invalidateAll(Iterable keys) {
         this.localCache.invalidateAll(keys);
      }

      public void invalidateAll() {
         this.localCache.clear();
      }

      public long size() {
         return this.localCache.longSize();
      }

      public ConcurrentMap asMap() {
         return this.localCache;
      }

      public CacheStats stats() {
         AbstractCache.SimpleStatsCounter aggregator = new AbstractCache.SimpleStatsCounter();
         aggregator.incrementBy(this.localCache.globalStatsCounter);

         for(LocalCache.Segment<K, V> segment : this.localCache.segments) {
            aggregator.incrementBy(segment.statsCounter);
         }

         return aggregator.snapshot();
      }

      public void cleanUp() {
         this.localCache.cleanUp();
      }

      Object writeReplace() {
         return new LocalCache.ManualSerializationProxy(this.localCache);
      }
   }

   static class ManualSerializationProxy extends ForwardingCache implements Serializable {
      private static final long serialVersionUID = 1L;
      final LocalCache.Strength keyStrength;
      final LocalCache.Strength valueStrength;
      final Equivalence keyEquivalence;
      final Equivalence valueEquivalence;
      final long expireAfterWriteNanos;
      final long expireAfterAccessNanos;
      final long maxWeight;
      final Weigher weigher;
      final int concurrencyLevel;
      final RemovalListener removalListener;
      final Ticker ticker;
      final CacheLoader loader;
      transient Cache delegate;

      ManualSerializationProxy(LocalCache cache) {
         this(cache.keyStrength, cache.valueStrength, cache.keyEquivalence, cache.valueEquivalence, cache.expireAfterWriteNanos, cache.expireAfterAccessNanos, cache.maxWeight, cache.weigher, cache.concurrencyLevel, cache.removalListener, cache.ticker, cache.defaultLoader);
      }

      private ManualSerializationProxy(LocalCache.Strength keyStrength, LocalCache.Strength valueStrength, Equivalence keyEquivalence, Equivalence valueEquivalence, long expireAfterWriteNanos, long expireAfterAccessNanos, long maxWeight, Weigher weigher, int concurrencyLevel, RemovalListener removalListener, Ticker ticker, CacheLoader loader) {
         this.keyStrength = keyStrength;
         this.valueStrength = valueStrength;
         this.keyEquivalence = keyEquivalence;
         this.valueEquivalence = valueEquivalence;
         this.expireAfterWriteNanos = expireAfterWriteNanos;
         this.expireAfterAccessNanos = expireAfterAccessNanos;
         this.maxWeight = maxWeight;
         this.weigher = weigher;
         this.concurrencyLevel = concurrencyLevel;
         this.removalListener = removalListener;
         this.ticker = ticker != Ticker.systemTicker() && ticker != CacheBuilder.NULL_TICKER?ticker:null;
         this.loader = loader;
      }

      CacheBuilder recreateCacheBuilder() {
         CacheBuilder<K, V> builder = CacheBuilder.newBuilder().setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).valueEquivalence(this.valueEquivalence).concurrencyLevel(this.concurrencyLevel).removalListener(this.removalListener);
         builder.strictParsing = false;
         if(this.expireAfterWriteNanos > 0L) {
            builder.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
         }

         if(this.expireAfterAccessNanos > 0L) {
            builder.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
         }

         if(this.weigher != CacheBuilder.OneWeigher.INSTANCE) {
            builder.weigher(this.weigher);
            if(this.maxWeight != -1L) {
               builder.maximumWeight(this.maxWeight);
            }
         } else if(this.maxWeight != -1L) {
            builder.maximumSize(this.maxWeight);
         }

         if(this.ticker != null) {
            builder.ticker(this.ticker);
         }

         return builder;
      }

      private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         CacheBuilder<K, V> builder = this.recreateCacheBuilder();
         this.delegate = builder.build();
      }

      private Object readResolve() {
         return this.delegate;
      }

      protected Cache delegate() {
         return this.delegate;
      }
   }

   private static enum NullEntry implements LocalCache.ReferenceEntry {
      INSTANCE;

      public LocalCache.ValueReference getValueReference() {
         return null;
      }

      public void setValueReference(LocalCache.ValueReference valueReference) {
      }

      public LocalCache.ReferenceEntry getNext() {
         return null;
      }

      public int getHash() {
         return 0;
      }

      public Object getKey() {
         return null;
      }

      public long getAccessTime() {
         return 0L;
      }

      public void setAccessTime(long time) {
      }

      public LocalCache.ReferenceEntry getNextInAccessQueue() {
         return this;
      }

      public void setNextInAccessQueue(LocalCache.ReferenceEntry next) {
      }

      public LocalCache.ReferenceEntry getPreviousInAccessQueue() {
         return this;
      }

      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry previous) {
      }

      public long getWriteTime() {
         return 0L;
      }

      public void setWriteTime(long time) {
      }

      public LocalCache.ReferenceEntry getNextInWriteQueue() {
         return this;
      }

      public void setNextInWriteQueue(LocalCache.ReferenceEntry next) {
      }

      public LocalCache.ReferenceEntry getPreviousInWriteQueue() {
         return this;
      }

      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry previous) {
      }
   }

   interface ReferenceEntry {
      LocalCache.ValueReference getValueReference();

      void setValueReference(LocalCache.ValueReference var1);

      @Nullable
      LocalCache.ReferenceEntry getNext();

      int getHash();

      @Nullable
      Object getKey();

      long getAccessTime();

      void setAccessTime(long var1);

      LocalCache.ReferenceEntry getNextInAccessQueue();

      void setNextInAccessQueue(LocalCache.ReferenceEntry var1);

      LocalCache.ReferenceEntry getPreviousInAccessQueue();

      void setPreviousInAccessQueue(LocalCache.ReferenceEntry var1);

      long getWriteTime();

      void setWriteTime(long var1);

      LocalCache.ReferenceEntry getNextInWriteQueue();

      void setNextInWriteQueue(LocalCache.ReferenceEntry var1);

      LocalCache.ReferenceEntry getPreviousInWriteQueue();

      void setPreviousInWriteQueue(LocalCache.ReferenceEntry var1);
   }

   static class Segment extends ReentrantLock {
      final LocalCache map;
      volatile int count;
      @GuardedBy("Segment.this")
      int totalWeight;
      int modCount;
      int threshold;
      volatile AtomicReferenceArray table;
      final long maxSegmentWeight;
      final ReferenceQueue keyReferenceQueue;
      final ReferenceQueue valueReferenceQueue;
      final Queue recencyQueue;
      final AtomicInteger readCount = new AtomicInteger();
      @GuardedBy("Segment.this")
      final Queue writeQueue;
      @GuardedBy("Segment.this")
      final Queue accessQueue;
      final AbstractCache.StatsCounter statsCounter;

      Segment(LocalCache map, int initialCapacity, long maxSegmentWeight, AbstractCache.StatsCounter statsCounter) {
         this.map = map;
         this.maxSegmentWeight = maxSegmentWeight;
         this.statsCounter = (AbstractCache.StatsCounter)Preconditions.checkNotNull(statsCounter);
         this.initTable(this.newEntryArray(initialCapacity));
         this.keyReferenceQueue = map.usesKeyReferences()?new ReferenceQueue():null;
         this.valueReferenceQueue = map.usesValueReferences()?new ReferenceQueue():null;
         this.recencyQueue = (Queue)(map.usesAccessQueue()?new ConcurrentLinkedQueue():LocalCache.DISCARDING_QUEUE);
         this.writeQueue = (Queue)(map.usesWriteQueue()?new LocalCache.WriteQueue():LocalCache.DISCARDING_QUEUE);
         this.accessQueue = (Queue)(map.usesAccessQueue()?new LocalCache.AccessQueue():LocalCache.DISCARDING_QUEUE);
      }

      AtomicReferenceArray newEntryArray(int size) {
         return new AtomicReferenceArray(size);
      }

      void initTable(AtomicReferenceArray newTable) {
         this.threshold = newTable.length() * 3 / 4;
         if(!this.map.customWeigher() && (long)this.threshold == this.maxSegmentWeight) {
            ++this.threshold;
         }

         this.table = newTable;
      }

      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry newEntry(Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
         return this.map.entryFactory.newEntry(this, Preconditions.checkNotNull(key), hash, next);
      }

      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry copyEntry(LocalCache.ReferenceEntry original, LocalCache.ReferenceEntry newNext) {
         if(original.getKey() == null) {
            return null;
         } else {
            LocalCache.ValueReference<K, V> valueReference = original.getValueReference();
            V value = valueReference.get();
            if(value == null && valueReference.isActive()) {
               return null;
            } else {
               LocalCache.ReferenceEntry<K, V> newEntry = this.map.entryFactory.copyEntry(this, original, newNext);
               newEntry.setValueReference(valueReference.copyFor(this.valueReferenceQueue, value, newEntry));
               return newEntry;
            }
         }
      }

      @GuardedBy("Segment.this")
      void setValue(LocalCache.ReferenceEntry entry, Object key, Object value, long now) {
         LocalCache.ValueReference<K, V> previous = entry.getValueReference();
         int weight = this.map.weigher.weigh(key, value);
         Preconditions.checkState(weight >= 0, "Weights must be non-negative");
         LocalCache.ValueReference<K, V> valueReference = this.map.valueStrength.referenceValue(this, entry, value, weight);
         entry.setValueReference(valueReference);
         this.recordWrite(entry, weight, now);
         previous.notifyNewValue(value);
      }

      Object get(Object key, int hash, CacheLoader loader) throws ExecutionException {
         Preconditions.checkNotNull(key);
         Preconditions.checkNotNull(loader);

         try {
            if(this.count != 0) {
               LocalCache.ReferenceEntry<K, V> e = this.getEntry(key, hash);
               if(e != null) {
                  long now = this.map.ticker.read();
                  V value = this.getLiveValue(e, now);
                  if(value != null) {
                     this.recordRead(e, now);
                     this.statsCounter.recordHits(1);
                     Object var17 = this.scheduleRefresh(e, key, hash, value, now, loader);
                     return var17;
                  }

                  LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
                  if(valueReference.isLoading()) {
                     Object var9 = this.waitForLoadingValue(e, key, valueReference);
                     return var9;
                  }
               }
            }

            Object ee = this.lockedGetOrLoad(key, hash, loader);
            return ee;
         } catch (ExecutionException var13) {
            Throwable cause = var13.getCause();
            if(cause instanceof Error) {
               throw new ExecutionError((Error)cause);
            } else if(cause instanceof RuntimeException) {
               throw new UncheckedExecutionException(cause);
            } else {
               throw var13;
            }
         } finally {
            this.postReadCleanup();
         }
      }

      Object lockedGetOrLoad(Object key, int hash, CacheLoader loader) throws ExecutionException {
         LocalCache.ValueReference<K, V> valueReference = null;
         LocalCache.LoadingValueReference<K, V> loadingValueReference = null;
         boolean createNewEntry = true;
         this.lock();

         LocalCache.ReferenceEntry<K, V> e;
         try {
            long now = this.map.ticker.read();
            this.preWriteCleanup(now);
            int newCount = this.count - 1;
            AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            LocalCache.ReferenceEntry<K, V> first = (LocalCache.ReferenceEntry)table.get(index);

            for(e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  valueReference = e.getValueReference();
                  if(valueReference.isLoading()) {
                     createNewEntry = false;
                  } else {
                     V value = valueReference.get();
                     if(value == null) {
                        this.enqueueNotification(entryKey, hash, valueReference, RemovalCause.COLLECTED);
                     } else {
                        if(!this.map.isExpired(e, now)) {
                           this.recordLockedRead(e, now);
                           this.statsCounter.recordHits(1);
                           Object var16 = value;
                           return var16;
                        }

                        this.enqueueNotification(entryKey, hash, valueReference, RemovalCause.EXPIRED);
                     }

                     this.writeQueue.remove(e);
                     this.accessQueue.remove(e);
                     this.count = newCount;
                  }
                  break;
               }
            }

            if(createNewEntry) {
               loadingValueReference = new LocalCache.LoadingValueReference();
               if(e == null) {
                  e = this.newEntry(key, hash, first);
                  e.setValueReference(loadingValueReference);
                  table.set(index, e);
               } else {
                  e.setValueReference(loadingValueReference);
               }
            }
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }

         if(createNewEntry) {
            Object var9;
            try {
               synchronized(e) {
                  var9 = this.loadSync(key, hash, loadingValueReference, loader);
               }
            } finally {
               this.statsCounter.recordMisses(1);
            }

            return var9;
         } else {
            return this.waitForLoadingValue(e, key, valueReference);
         }
      }

      Object waitForLoadingValue(LocalCache.ReferenceEntry e, Object key, LocalCache.ValueReference valueReference) throws ExecutionException {
         if(!valueReference.isLoading()) {
            throw new AssertionError();
         } else {
            Preconditions.checkState(!Thread.holdsLock(e), "Recursive load of: %s", new Object[]{key});

            Object var7;
            try {
               V value = valueReference.waitForValue();
               if(value == null) {
                  throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + key + ".");
               }

               long now = this.map.ticker.read();
               this.recordRead(e, now);
               var7 = value;
            } finally {
               this.statsCounter.recordMisses(1);
            }

            return var7;
         }
      }

      Object loadSync(Object key, int hash, LocalCache.LoadingValueReference loadingValueReference, CacheLoader loader) throws ExecutionException {
         ListenableFuture<V> loadingFuture = loadingValueReference.loadFuture(key, loader);
         return this.getAndRecordStats(key, hash, loadingValueReference, loadingFuture);
      }

      ListenableFuture loadAsync(final Object key, final int hash, final LocalCache.LoadingValueReference loadingValueReference, CacheLoader loader) {
         final ListenableFuture<V> loadingFuture = loadingValueReference.loadFuture(key, loader);
         loadingFuture.addListener(new Runnable() {
            public void run() {
               try {
                  V newValue = Segment.this.getAndRecordStats(key, hash, loadingValueReference, loadingFuture);
               } catch (Throwable var2) {
                  LocalCache.logger.log(Level.WARNING, "Exception thrown during refresh", var2);
                  loadingValueReference.setException(var2);
               }

            }
         }, LocalCache.sameThreadExecutor);
         return loadingFuture;
      }

      Object getAndRecordStats(Object key, int hash, LocalCache.LoadingValueReference loadingValueReference, ListenableFuture newValue) throws ExecutionException {
         V value = null;

         Object var6;
         try {
            value = Uninterruptibles.getUninterruptibly(newValue);
            if(value == null) {
               throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + key + ".");
            }

            this.statsCounter.recordLoadSuccess(loadingValueReference.elapsedNanos());
            this.storeLoadedValue(key, hash, loadingValueReference, value);
            var6 = value;
         } finally {
            if(value == null) {
               this.statsCounter.recordLoadException(loadingValueReference.elapsedNanos());
               this.removeLoadingValue(key, hash, loadingValueReference);
            }

         }

         return var6;
      }

      Object scheduleRefresh(LocalCache.ReferenceEntry entry, Object key, int hash, Object oldValue, long now, CacheLoader loader) {
         if(this.map.refreshes() && now - entry.getWriteTime() > this.map.refreshNanos && !entry.getValueReference().isLoading()) {
            V newValue = this.refresh(key, hash, loader, true);
            if(newValue != null) {
               return newValue;
            }
         }

         return oldValue;
      }

      @Nullable
      Object refresh(Object key, int hash, CacheLoader loader, boolean checkTime) {
         LocalCache.LoadingValueReference<K, V> loadingValueReference = this.insertLoadingValueReference(key, hash, checkTime);
         if(loadingValueReference == null) {
            return null;
         } else {
            ListenableFuture<V> result = this.loadAsync(key, hash, loadingValueReference, loader);
            if(result.isDone()) {
               try {
                  return Uninterruptibles.getUninterruptibly(result);
               } catch (Throwable var8) {
                  ;
               }
            }

            return null;
         }
      }

      @Nullable
      LocalCache.LoadingValueReference insertLoadingValueReference(Object key, int hash, boolean checkTime) {
         LocalCache.ReferenceEntry<K, V> e = null;
         this.lock();

         try {
            long now = this.map.ticker.read();
            this.preWriteCleanup(now);
            AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            LocalCache.ReferenceEntry<K, V> first = (LocalCache.ReferenceEntry)table.get(index);

            for(e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
                  if(!valueReference.isLoading() && (!checkTime || now - e.getWriteTime() >= this.map.refreshNanos)) {
                     ++this.modCount;
                     LocalCache.LoadingValueReference<K, V> loadingValueReference = new LocalCache.LoadingValueReference(valueReference);
                     e.setValueReference(loadingValueReference);
                     LocalCache.LoadingValueReference var13 = loadingValueReference;
                     return var13;
                  }

                  Object loadingValueReference = null;
                  return (LocalCache.LoadingValueReference)loadingValueReference;
               }
            }

            ++this.modCount;
            LocalCache.LoadingValueReference<K, V> loadingValueReference = new LocalCache.LoadingValueReference();
            e = this.newEntry(key, hash, first);
            e.setValueReference(loadingValueReference);
            table.set(index, e);
            LocalCache.LoadingValueReference var20 = loadingValueReference;
            return var20;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
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
            LocalCache.ReferenceEntry<K, V> entry = (LocalCache.ReferenceEntry)ref;
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
            LocalCache.ValueReference<K, V> valueReference = (LocalCache.ValueReference)ref;
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

      void recordRead(LocalCache.ReferenceEntry entry, long now) {
         if(this.map.recordsAccess()) {
            entry.setAccessTime(now);
         }

         this.recencyQueue.add(entry);
      }

      @GuardedBy("Segment.this")
      void recordLockedRead(LocalCache.ReferenceEntry entry, long now) {
         if(this.map.recordsAccess()) {
            entry.setAccessTime(now);
         }

         this.accessQueue.add(entry);
      }

      @GuardedBy("Segment.this")
      void recordWrite(LocalCache.ReferenceEntry entry, int weight, long now) {
         this.drainRecencyQueue();
         this.totalWeight += weight;
         if(this.map.recordsAccess()) {
            entry.setAccessTime(now);
         }

         if(this.map.recordsWrite()) {
            entry.setWriteTime(now);
         }

         this.accessQueue.add(entry);
         this.writeQueue.add(entry);
      }

      @GuardedBy("Segment.this")
      void drainRecencyQueue() {
         LocalCache.ReferenceEntry<K, V> e;
         while((e = (LocalCache.ReferenceEntry)this.recencyQueue.poll()) != null) {
            if(this.accessQueue.contains(e)) {
               this.accessQueue.add(e);
            }
         }

      }

      void tryExpireEntries(long now) {
         if(this.tryLock()) {
            try {
               this.expireEntries(now);
            } finally {
               this.unlock();
            }
         }

      }

      @GuardedBy("Segment.this")
      void expireEntries(long now) {
         this.drainRecencyQueue();

         LocalCache.ReferenceEntry<K, V> e;
         while((e = (LocalCache.ReferenceEntry)this.writeQueue.peek()) != null && this.map.isExpired(e, now)) {
            if(!this.removeEntry(e, e.getHash(), RemovalCause.EXPIRED)) {
               throw new AssertionError();
            }
         }

         while((e = (LocalCache.ReferenceEntry)this.accessQueue.peek()) != null && this.map.isExpired(e, now)) {
            if(!this.removeEntry(e, e.getHash(), RemovalCause.EXPIRED)) {
               throw new AssertionError();
            }
         }

      }

      @GuardedBy("Segment.this")
      void enqueueNotification(LocalCache.ReferenceEntry entry, RemovalCause cause) {
         this.enqueueNotification(entry.getKey(), entry.getHash(), entry.getValueReference(), cause);
      }

      @GuardedBy("Segment.this")
      void enqueueNotification(@Nullable Object key, int hash, LocalCache.ValueReference valueReference, RemovalCause cause) {
         this.totalWeight -= valueReference.getWeight();
         if(cause.wasEvicted()) {
            this.statsCounter.recordEviction();
         }

         if(this.map.removalNotificationQueue != LocalCache.DISCARDING_QUEUE) {
            V value = valueReference.get();
            RemovalNotification<K, V> notification = new RemovalNotification(key, value, cause);
            this.map.removalNotificationQueue.offer(notification);
         }

      }

      @GuardedBy("Segment.this")
      void evictEntries() {
         if(this.map.evictsBySize()) {
            this.drainRecencyQueue();

            while((long)this.totalWeight > this.maxSegmentWeight) {
               LocalCache.ReferenceEntry<K, V> e = this.getNextEvictable();
               if(!this.removeEntry(e, e.getHash(), RemovalCause.SIZE)) {
                  throw new AssertionError();
               }
            }

         }
      }

      LocalCache.ReferenceEntry getNextEvictable() {
         for(LocalCache.ReferenceEntry<K, V> e : this.accessQueue) {
            int weight = e.getValueReference().getWeight();
            if(weight > 0) {
               return e;
            }
         }

         throw new AssertionError();
      }

      LocalCache.ReferenceEntry getFirst(int hash) {
         AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;
         return (LocalCache.ReferenceEntry)table.get(hash & table.length() - 1);
      }

      @Nullable
      LocalCache.ReferenceEntry getEntry(Object key, int hash) {
         for(LocalCache.ReferenceEntry<K, V> e = this.getFirst(hash); e != null; e = e.getNext()) {
            if(e.getHash() == hash) {
               K entryKey = e.getKey();
               if(entryKey == null) {
                  this.tryDrainReferenceQueues();
               } else if(this.map.keyEquivalence.equivalent(key, entryKey)) {
                  return e;
               }
            }
         }

         return null;
      }

      @Nullable
      LocalCache.ReferenceEntry getLiveEntry(Object key, int hash, long now) {
         LocalCache.ReferenceEntry<K, V> e = this.getEntry(key, hash);
         if(e == null) {
            return null;
         } else if(this.map.isExpired(e, now)) {
            this.tryExpireEntries(now);
            return null;
         } else {
            return e;
         }
      }

      Object getLiveValue(LocalCache.ReferenceEntry entry, long now) {
         if(entry.getKey() == null) {
            this.tryDrainReferenceQueues();
            return null;
         } else {
            V value = entry.getValueReference().get();
            if(value == null) {
               this.tryDrainReferenceQueues();
               return null;
            } else if(this.map.isExpired(entry, now)) {
               this.tryExpireEntries(now);
               return null;
            } else {
               return value;
            }
         }
      }

      @Nullable
      Object get(Object key, int hash) {
         try {
            if(this.count != 0) {
               long now = this.map.ticker.read();
               LocalCache.ReferenceEntry<K, V> e = this.getLiveEntry(key, hash, now);
               if(e == null) {
                  Object var12 = null;
                  return var12;
               }

               V value = e.getValueReference().get();
               if(value != null) {
                  this.recordRead(e, now);
                  Object var7 = this.scheduleRefresh(e, e.getKey(), hash, value, now, this.map.defaultLoader);
                  return var7;
               }

               this.tryDrainReferenceQueues();
            }

            Object var11 = null;
            return var11;
         } finally {
            this.postReadCleanup();
         }
      }

      boolean containsKey(Object key, int hash) {
         boolean var6;
         try {
            if(this.count == 0) {
               boolean var10 = false;
               return var10;
            }

            long now = this.map.ticker.read();
            LocalCache.ReferenceEntry<K, V> e = this.getLiveEntry(key, hash, now);
            if(e != null) {
               var6 = e.getValueReference().get() != null;
               return var6;
            }

            var6 = false;
         } finally {
            this.postReadCleanup();
         }

         return var6;
      }

      @VisibleForTesting
      boolean containsValue(Object value) {
         try {
            if(this.count != 0) {
               long now = this.map.ticker.read();
               AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;
               int length = table.length();

               for(int i = 0; i < length; ++i) {
                  for(LocalCache.ReferenceEntry<K, V> e = (LocalCache.ReferenceEntry)table.get(i); e != null; e = e.getNext()) {
                     V entryValue = this.getLiveValue(e, now);
                     if(entryValue != null && this.map.valueEquivalence.equivalent(value, entryValue)) {
                        boolean var9 = true;
                        return var9;
                     }
                  }
               }
            }

            boolean var13 = false;
            return var13;
         } finally {
            this.postReadCleanup();
         }
      }

      @Nullable
      Object put(Object key, int hash, Object value, boolean onlyIfAbsent) {
         this.lock();

         try {
            long now = this.map.ticker.read();
            this.preWriteCleanup(now);
            int newCount = this.count + 1;
            if(newCount > this.threshold) {
               this.expand();
               newCount = this.count + 1;
            }

            AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            LocalCache.ReferenceEntry<K, V> first = (LocalCache.ReferenceEntry)table.get(index);

            for(LocalCache.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
                  V entryValue = valueReference.get();
                  if(entryValue != null) {
                     if(onlyIfAbsent) {
                        this.recordLockedRead(e, now);
                        Object var25 = entryValue;
                        return var25;
                     }

                     ++this.modCount;
                     this.enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
                     this.setValue(e, key, value, now);
                     this.evictEntries();
                     Object var24 = entryValue;
                     return var24;
                  }

                  ++this.modCount;
                  if(valueReference.isActive()) {
                     this.enqueueNotification(key, hash, valueReference, RemovalCause.COLLECTED);
                     this.setValue(e, key, value, now);
                     newCount = this.count;
                  } else {
                     this.setValue(e, key, value, now);
                     newCount = this.count + 1;
                  }

                  this.count = newCount;
                  this.evictEntries();
                  Object var15 = null;
                  return var15;
               }
            }

            ++this.modCount;
            LocalCache.ReferenceEntry<K, V> newEntry = this.newEntry(key, hash, first);
            this.setValue(newEntry, key, value, now);
            table.set(index, newEntry);
            newCount = this.count + 1;
            this.count = newCount;
            this.evictEntries();
            Object var23 = null;
            return var23;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      @GuardedBy("Segment.this")
      void expand() {
         AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> oldTable = this.table;
         int oldCapacity = oldTable.length();
         if(oldCapacity < 1073741824) {
            int newCount = this.count;
            AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> newTable = this.newEntryArray(oldCapacity << 1);
            this.threshold = newTable.length() * 3 / 4;
            int newMask = newTable.length() - 1;

            for(int oldIndex = 0; oldIndex < oldCapacity; ++oldIndex) {
               LocalCache.ReferenceEntry<K, V> head = (LocalCache.ReferenceEntry)oldTable.get(oldIndex);
               if(head != null) {
                  LocalCache.ReferenceEntry<K, V> next = head.getNext();
                  int headIndex = head.getHash() & newMask;
                  if(next == null) {
                     newTable.set(headIndex, head);
                  } else {
                     LocalCache.ReferenceEntry<K, V> tail = head;
                     int tailIndex = headIndex;

                     for(LocalCache.ReferenceEntry<K, V> e = next; e != null; e = e.getNext()) {
                        int newIndex = e.getHash() & newMask;
                        if(newIndex != tailIndex) {
                           tailIndex = newIndex;
                           tail = e;
                        }
                     }

                     newTable.set(tailIndex, tail);

                     for(LocalCache.ReferenceEntry<K, V> e = head; e != tail; e = e.getNext()) {
                        int newIndex = e.getHash() & newMask;
                        LocalCache.ReferenceEntry<K, V> newNext = (LocalCache.ReferenceEntry)newTable.get(newIndex);
                        LocalCache.ReferenceEntry<K, V> newFirst = this.copyEntry(e, newNext);
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
            long now = this.map.ticker.read();
            this.preWriteCleanup(now);
            AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            LocalCache.ReferenceEntry<K, V> first = (LocalCache.ReferenceEntry)table.get(index);

            for(LocalCache.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
                  V entryValue = valueReference.get();
                  if(entryValue != null) {
                     if(this.map.valueEquivalence.equivalent(oldValue, entryValue)) {
                        ++this.modCount;
                        this.enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
                        this.setValue(e, key, newValue, now);
                        this.evictEntries();
                        boolean var23 = true;
                        return var23;
                     }

                     this.recordLockedRead(e, now);
                     boolean var22 = false;
                     return var22;
                  }

                  if(valueReference.isActive()) {
                     int newCount = this.count - 1;
                     ++this.modCount;
                     LocalCache.ReferenceEntry<K, V> newFirst = this.removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
                     newCount = this.count - 1;
                     table.set(index, newFirst);
                     this.count = newCount;
                  }

                  boolean var21 = false;
                  return var21;
               }
            }

            boolean var19 = false;
            return var19;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      @Nullable
      Object replace(Object key, int hash, Object newValue) {
         this.lock();

         try {
            long now = this.map.ticker.read();
            this.preWriteCleanup(now);
            AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            LocalCache.ReferenceEntry<K, V> first = (LocalCache.ReferenceEntry)table.get(index);

            for(LocalCache.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
                  V entryValue = valueReference.get();
                  if(entryValue != null) {
                     ++this.modCount;
                     this.enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
                     this.setValue(e, key, newValue, now);
                     this.evictEntries();
                     Object var21 = entryValue;
                     return var21;
                  }

                  if(valueReference.isActive()) {
                     int newCount = this.count - 1;
                     ++this.modCount;
                     LocalCache.ReferenceEntry<K, V> newFirst = this.removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
                     newCount = this.count - 1;
                     table.set(index, newFirst);
                     this.count = newCount;
                  }

                  Object var20 = null;
                  return var20;
               }
            }

            Object var18 = null;
            return var18;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      @Nullable
      Object remove(Object key, int hash) {
         this.lock();

         try {
            long now = this.map.ticker.read();
            this.preWriteCleanup(now);
            int newCount = this.count - 1;
            AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            LocalCache.ReferenceEntry<K, V> first = (LocalCache.ReferenceEntry)table.get(index);

            for(LocalCache.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
                  V entryValue = valueReference.get();
                  RemovalCause cause;
                  if(entryValue != null) {
                     cause = RemovalCause.EXPLICIT;
                  } else {
                     if(!valueReference.isActive()) {
                        Object var21 = null;
                        return var21;
                     }

                     cause = RemovalCause.COLLECTED;
                  }

                  ++this.modCount;
                  LocalCache.ReferenceEntry<K, V> newFirst = this.removeValueFromChain(first, e, entryKey, hash, valueReference, cause);
                  newCount = this.count - 1;
                  table.set(index, newFirst);
                  this.count = newCount;
                  Object var15 = entryValue;
                  return var15;
               }
            }

            Object var20 = null;
            return var20;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      boolean storeLoadedValue(Object key, int hash, LocalCache.LoadingValueReference oldValueReference, Object newValue) {
         this.lock();

         try {
            long now = this.map.ticker.read();
            this.preWriteCleanup(now);
            int newCount = this.count + 1;
            if(newCount > this.threshold) {
               this.expand();
               newCount = this.count + 1;
            }

            AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            LocalCache.ReferenceEntry<K, V> first = (LocalCache.ReferenceEntry)table.get(index);

            for(LocalCache.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
                  V entryValue = valueReference.get();
                  if(oldValueReference != valueReference && (entryValue != null || valueReference == LocalCache.UNSET)) {
                     LocalCache.WeightedStrongValueReference var21 = new LocalCache.WeightedStrongValueReference(newValue, 0);
                     this.enqueueNotification(key, hash, var21, RemovalCause.REPLACED);
                     boolean var23 = false;
                     return var23;
                  }

                  ++this.modCount;
                  if(oldValueReference.isActive()) {
                     RemovalCause cause = entryValue == null?RemovalCause.COLLECTED:RemovalCause.REPLACED;
                     this.enqueueNotification(key, hash, oldValueReference, cause);
                     --newCount;
                  }

                  this.setValue(e, key, newValue, now);
                  this.count = newCount;
                  this.evictEntries();
                  boolean var22 = true;
                  return var22;
               }
            }

            ++this.modCount;
            LocalCache.ReferenceEntry<K, V> newEntry = this.newEntry(key, hash, first);
            this.setValue(newEntry, key, newValue, now);
            table.set(index, newEntry);
            this.count = newCount;
            this.evictEntries();
            boolean var20 = true;
            return var20;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      boolean remove(Object key, int hash, Object value) {
         this.lock();

         try {
            long now = this.map.ticker.read();
            this.preWriteCleanup(now);
            int newCount = this.count - 1;
            AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            LocalCache.ReferenceEntry<K, V> first = (LocalCache.ReferenceEntry)table.get(index);

            for(LocalCache.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
                  V entryValue = valueReference.get();
                  RemovalCause cause;
                  if(this.map.valueEquivalence.equivalent(value, entryValue)) {
                     cause = RemovalCause.EXPLICIT;
                  } else {
                     if(entryValue != null || !valueReference.isActive()) {
                        boolean var22 = false;
                        return var22;
                     }

                     cause = RemovalCause.COLLECTED;
                  }

                  ++this.modCount;
                  LocalCache.ReferenceEntry<K, V> newFirst = this.removeValueFromChain(first, e, entryKey, hash, valueReference, cause);
                  newCount = this.count - 1;
                  table.set(index, newFirst);
                  this.count = newCount;
                  boolean var16 = cause == RemovalCause.EXPLICIT;
                  return var16;
               }
            }

            boolean var21 = false;
            return var21;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      void clear() {
         if(this.count != 0) {
            this.lock();

            try {
               AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;

               for(int i = 0; i < table.length(); ++i) {
                  for(LocalCache.ReferenceEntry<K, V> e = (LocalCache.ReferenceEntry)table.get(i); e != null; e = e.getNext()) {
                     if(e.getValueReference().isActive()) {
                        this.enqueueNotification(e, RemovalCause.EXPLICIT);
                     }
                  }
               }

               for(int i = 0; i < table.length(); ++i) {
                  table.set(i, (Object)null);
               }

               this.clearReferenceQueues();
               this.writeQueue.clear();
               this.accessQueue.clear();
               this.readCount.set(0);
               ++this.modCount;
               this.count = 0;
            } finally {
               this.unlock();
               this.postWriteCleanup();
            }
         }

      }

      @Nullable
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry removeValueFromChain(LocalCache.ReferenceEntry first, LocalCache.ReferenceEntry entry, @Nullable Object key, int hash, LocalCache.ValueReference valueReference, RemovalCause cause) {
         this.enqueueNotification(key, hash, valueReference, cause);
         this.writeQueue.remove(entry);
         this.accessQueue.remove(entry);
         if(valueReference.isLoading()) {
            valueReference.notifyNewValue((Object)null);
            return first;
         } else {
            return this.removeEntryFromChain(first, entry);
         }
      }

      @Nullable
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry removeEntryFromChain(LocalCache.ReferenceEntry first, LocalCache.ReferenceEntry entry) {
         int newCount = this.count;
         LocalCache.ReferenceEntry<K, V> newFirst = entry.getNext();

         for(LocalCache.ReferenceEntry<K, V> e = first; e != entry; e = e.getNext()) {
            LocalCache.ReferenceEntry<K, V> next = this.copyEntry(e, newFirst);
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

      @GuardedBy("Segment.this")
      void removeCollectedEntry(LocalCache.ReferenceEntry entry) {
         this.enqueueNotification(entry, RemovalCause.COLLECTED);
         this.writeQueue.remove(entry);
         this.accessQueue.remove(entry);
      }

      boolean reclaimKey(LocalCache.ReferenceEntry entry, int hash) {
         this.lock();

         try {
            int newCount = this.count - 1;
            AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            LocalCache.ReferenceEntry<K, V> first = (LocalCache.ReferenceEntry)table.get(index);

            for(LocalCache.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               if(e == entry) {
                  ++this.modCount;
                  LocalCache.ReferenceEntry<K, V> newFirst = this.removeValueFromChain(first, e, e.getKey(), hash, e.getValueReference(), RemovalCause.COLLECTED);
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

      boolean reclaimValue(Object key, int hash, LocalCache.ValueReference valueReference) {
         this.lock();

         try {
            int newCount = this.count - 1;
            AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            LocalCache.ReferenceEntry<K, V> first = (LocalCache.ReferenceEntry)table.get(index);

            for(LocalCache.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  LocalCache.ValueReference<K, V> v = e.getValueReference();
                  if(v != valueReference) {
                     boolean var18 = false;
                     return var18;
                  }

                  ++this.modCount;
                  LocalCache.ReferenceEntry<K, V> newFirst = this.removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
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

      boolean removeLoadingValue(Object key, int hash, LocalCache.LoadingValueReference valueReference) {
         this.lock();

         try {
            AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;
            int index = hash & table.length() - 1;
            LocalCache.ReferenceEntry<K, V> first = (LocalCache.ReferenceEntry)table.get(index);

            for(LocalCache.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
               K entryKey = e.getKey();
               if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                  LocalCache.ValueReference<K, V> v = e.getValueReference();
                  if(v != valueReference) {
                     boolean var16 = false;
                     return var16;
                  }

                  if(valueReference.isActive()) {
                     e.setValueReference(valueReference.getOldValue());
                  } else {
                     LocalCache.ReferenceEntry<K, V> newFirst = this.removeEntryFromChain(first, e);
                     table.set(index, newFirst);
                  }

                  boolean var15 = true;
                  return var15;
               }
            }

            boolean var14 = false;
            return var14;
         } finally {
            this.unlock();
            this.postWriteCleanup();
         }
      }

      @GuardedBy("Segment.this")
      boolean removeEntry(LocalCache.ReferenceEntry entry, int hash, RemovalCause cause) {
         int newCount = this.count - 1;
         AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table = this.table;
         int index = hash & table.length() - 1;
         LocalCache.ReferenceEntry<K, V> first = (LocalCache.ReferenceEntry)table.get(index);

         for(LocalCache.ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
            if(e == entry) {
               ++this.modCount;
               LocalCache.ReferenceEntry<K, V> newFirst = this.removeValueFromChain(first, e, e.getKey(), hash, e.getValueReference(), cause);
               newCount = this.count - 1;
               table.set(index, newFirst);
               this.count = newCount;
               return true;
            }
         }

         return false;
      }

      void postReadCleanup() {
         if((this.readCount.incrementAndGet() & 63) == 0) {
            this.cleanUp();
         }

      }

      @GuardedBy("Segment.this")
      void preWriteCleanup(long now) {
         this.runLockedCleanup(now);
      }

      void postWriteCleanup() {
         this.runUnlockedCleanup();
      }

      void cleanUp() {
         long now = this.map.ticker.read();
         this.runLockedCleanup(now);
         this.runUnlockedCleanup();
      }

      void runLockedCleanup(long now) {
         if(this.tryLock()) {
            try {
               this.drainReferenceQueues();
               this.expireEntries(now);
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

   static class SoftValueReference extends SoftReference implements LocalCache.ValueReference {
      final LocalCache.ReferenceEntry entry;

      SoftValueReference(ReferenceQueue queue, Object referent, LocalCache.ReferenceEntry entry) {
         super(referent, queue);
         this.entry = entry;
      }

      public int getWeight() {
         return 1;
      }

      public LocalCache.ReferenceEntry getEntry() {
         return this.entry;
      }

      public void notifyNewValue(Object newValue) {
      }

      public LocalCache.ValueReference copyFor(ReferenceQueue queue, Object value, LocalCache.ReferenceEntry entry) {
         return new LocalCache.SoftValueReference(queue, value, entry);
      }

      public boolean isLoading() {
         return false;
      }

      public boolean isActive() {
         return true;
      }

      public Object waitForValue() {
         return this.get();
      }
   }

   static enum Strength {
      STRONG {
         LocalCache.ValueReference referenceValue(LocalCache.Segment segment, LocalCache.ReferenceEntry entry, Object value, int weight) {
            return (LocalCache.ValueReference)(weight == 1?new LocalCache.StrongValueReference(value):new LocalCache.WeightedStrongValueReference(value, weight));
         }

         Equivalence defaultEquivalence() {
            return Equivalence.equals();
         }
      },
      SOFT {
         LocalCache.ValueReference referenceValue(LocalCache.Segment segment, LocalCache.ReferenceEntry entry, Object value, int weight) {
            return (LocalCache.ValueReference)(weight == 1?new LocalCache.SoftValueReference(segment.valueReferenceQueue, value, entry):new LocalCache.WeightedSoftValueReference(segment.valueReferenceQueue, value, entry, weight));
         }

         Equivalence defaultEquivalence() {
            return Equivalence.identity();
         }
      },
      WEAK {
         LocalCache.ValueReference referenceValue(LocalCache.Segment segment, LocalCache.ReferenceEntry entry, Object value, int weight) {
            return (LocalCache.ValueReference)(weight == 1?new LocalCache.WeakValueReference(segment.valueReferenceQueue, value, entry):new LocalCache.WeightedWeakValueReference(segment.valueReferenceQueue, value, entry, weight));
         }

         Equivalence defaultEquivalence() {
            return Equivalence.identity();
         }
      };

      private Strength() {
      }

      abstract LocalCache.ValueReference referenceValue(LocalCache.Segment var1, LocalCache.ReferenceEntry var2, Object var3, int var4);

      abstract Equivalence defaultEquivalence();
   }

   static final class StrongAccessEntry extends LocalCache.StrongEntry {
      volatile long accessTime = Long.MAX_VALUE;
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry nextAccess = LocalCache.nullEntry();
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry previousAccess = LocalCache.nullEntry();

      StrongAccessEntry(Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
         super(key, hash, next);
      }

      public long getAccessTime() {
         return this.accessTime;
      }

      public void setAccessTime(long time) {
         this.accessTime = time;
      }

      public LocalCache.ReferenceEntry getNextInAccessQueue() {
         return this.nextAccess;
      }

      public void setNextInAccessQueue(LocalCache.ReferenceEntry next) {
         this.nextAccess = next;
      }

      public LocalCache.ReferenceEntry getPreviousInAccessQueue() {
         return this.previousAccess;
      }

      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry previous) {
         this.previousAccess = previous;
      }
   }

   static final class StrongAccessWriteEntry extends LocalCache.StrongEntry {
      volatile long accessTime = Long.MAX_VALUE;
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry nextAccess = LocalCache.nullEntry();
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry previousAccess = LocalCache.nullEntry();
      volatile long writeTime = Long.MAX_VALUE;
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry nextWrite = LocalCache.nullEntry();
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry previousWrite = LocalCache.nullEntry();

      StrongAccessWriteEntry(Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
         super(key, hash, next);
      }

      public long getAccessTime() {
         return this.accessTime;
      }

      public void setAccessTime(long time) {
         this.accessTime = time;
      }

      public LocalCache.ReferenceEntry getNextInAccessQueue() {
         return this.nextAccess;
      }

      public void setNextInAccessQueue(LocalCache.ReferenceEntry next) {
         this.nextAccess = next;
      }

      public LocalCache.ReferenceEntry getPreviousInAccessQueue() {
         return this.previousAccess;
      }

      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry previous) {
         this.previousAccess = previous;
      }

      public long getWriteTime() {
         return this.writeTime;
      }

      public void setWriteTime(long time) {
         this.writeTime = time;
      }

      public LocalCache.ReferenceEntry getNextInWriteQueue() {
         return this.nextWrite;
      }

      public void setNextInWriteQueue(LocalCache.ReferenceEntry next) {
         this.nextWrite = next;
      }

      public LocalCache.ReferenceEntry getPreviousInWriteQueue() {
         return this.previousWrite;
      }

      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry previous) {
         this.previousWrite = previous;
      }
   }

   static class StrongEntry extends LocalCache.AbstractReferenceEntry {
      final Object key;
      final int hash;
      final LocalCache.ReferenceEntry next;
      volatile LocalCache.ValueReference valueReference = LocalCache.UNSET;

      StrongEntry(Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
         this.key = key;
         this.hash = hash;
         this.next = next;
      }

      public Object getKey() {
         return this.key;
      }

      public LocalCache.ValueReference getValueReference() {
         return this.valueReference;
      }

      public void setValueReference(LocalCache.ValueReference valueReference) {
         this.valueReference = valueReference;
      }

      public int getHash() {
         return this.hash;
      }

      public LocalCache.ReferenceEntry getNext() {
         return this.next;
      }
   }

   static class StrongValueReference implements LocalCache.ValueReference {
      final Object referent;

      StrongValueReference(Object referent) {
         this.referent = referent;
      }

      public Object get() {
         return this.referent;
      }

      public int getWeight() {
         return 1;
      }

      public LocalCache.ReferenceEntry getEntry() {
         return null;
      }

      public LocalCache.ValueReference copyFor(ReferenceQueue queue, Object value, LocalCache.ReferenceEntry entry) {
         return this;
      }

      public boolean isLoading() {
         return false;
      }

      public boolean isActive() {
         return true;
      }

      public Object waitForValue() {
         return this.get();
      }

      public void notifyNewValue(Object newValue) {
      }
   }

   static final class StrongWriteEntry extends LocalCache.StrongEntry {
      volatile long writeTime = Long.MAX_VALUE;
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry nextWrite = LocalCache.nullEntry();
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry previousWrite = LocalCache.nullEntry();

      StrongWriteEntry(Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
         super(key, hash, next);
      }

      public long getWriteTime() {
         return this.writeTime;
      }

      public void setWriteTime(long time) {
         this.writeTime = time;
      }

      public LocalCache.ReferenceEntry getNextInWriteQueue() {
         return this.nextWrite;
      }

      public void setNextInWriteQueue(LocalCache.ReferenceEntry next) {
         this.nextWrite = next;
      }

      public LocalCache.ReferenceEntry getPreviousInWriteQueue() {
         return this.previousWrite;
      }

      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry previous) {
         this.previousWrite = previous;
      }
   }

   final class ValueIterator extends LocalCache.HashIterator {
      ValueIterator() {
         super();
      }

      public Object next() {
         return this.nextEntry().getValue();
      }
   }

   interface ValueReference {
      @Nullable
      Object get();

      Object waitForValue() throws ExecutionException;

      int getWeight();

      @Nullable
      LocalCache.ReferenceEntry getEntry();

      LocalCache.ValueReference copyFor(ReferenceQueue var1, @Nullable Object var2, LocalCache.ReferenceEntry var3);

      void notifyNewValue(@Nullable Object var1);

      boolean isLoading();

      boolean isActive();
   }

   final class Values extends AbstractCollection {
      private final ConcurrentMap map;

      Values(ConcurrentMap map) {
         this.map = map;
      }

      public int size() {
         return this.map.size();
      }

      public boolean isEmpty() {
         return this.map.isEmpty();
      }

      public void clear() {
         this.map.clear();
      }

      public Iterator iterator() {
         return LocalCache.this.new ValueIterator();
      }

      public boolean contains(Object o) {
         return this.map.containsValue(o);
      }
   }

   static final class WeakAccessEntry extends LocalCache.WeakEntry {
      volatile long accessTime = Long.MAX_VALUE;
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry nextAccess = LocalCache.nullEntry();
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry previousAccess = LocalCache.nullEntry();

      WeakAccessEntry(ReferenceQueue queue, Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
         super(queue, key, hash, next);
      }

      public long getAccessTime() {
         return this.accessTime;
      }

      public void setAccessTime(long time) {
         this.accessTime = time;
      }

      public LocalCache.ReferenceEntry getNextInAccessQueue() {
         return this.nextAccess;
      }

      public void setNextInAccessQueue(LocalCache.ReferenceEntry next) {
         this.nextAccess = next;
      }

      public LocalCache.ReferenceEntry getPreviousInAccessQueue() {
         return this.previousAccess;
      }

      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry previous) {
         this.previousAccess = previous;
      }
   }

   static final class WeakAccessWriteEntry extends LocalCache.WeakEntry {
      volatile long accessTime = Long.MAX_VALUE;
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry nextAccess = LocalCache.nullEntry();
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry previousAccess = LocalCache.nullEntry();
      volatile long writeTime = Long.MAX_VALUE;
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry nextWrite = LocalCache.nullEntry();
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry previousWrite = LocalCache.nullEntry();

      WeakAccessWriteEntry(ReferenceQueue queue, Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
         super(queue, key, hash, next);
      }

      public long getAccessTime() {
         return this.accessTime;
      }

      public void setAccessTime(long time) {
         this.accessTime = time;
      }

      public LocalCache.ReferenceEntry getNextInAccessQueue() {
         return this.nextAccess;
      }

      public void setNextInAccessQueue(LocalCache.ReferenceEntry next) {
         this.nextAccess = next;
      }

      public LocalCache.ReferenceEntry getPreviousInAccessQueue() {
         return this.previousAccess;
      }

      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry previous) {
         this.previousAccess = previous;
      }

      public long getWriteTime() {
         return this.writeTime;
      }

      public void setWriteTime(long time) {
         this.writeTime = time;
      }

      public LocalCache.ReferenceEntry getNextInWriteQueue() {
         return this.nextWrite;
      }

      public void setNextInWriteQueue(LocalCache.ReferenceEntry next) {
         this.nextWrite = next;
      }

      public LocalCache.ReferenceEntry getPreviousInWriteQueue() {
         return this.previousWrite;
      }

      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry previous) {
         this.previousWrite = previous;
      }
   }

   static class WeakEntry extends WeakReference implements LocalCache.ReferenceEntry {
      final int hash;
      final LocalCache.ReferenceEntry next;
      volatile LocalCache.ValueReference valueReference = LocalCache.UNSET;

      WeakEntry(ReferenceQueue queue, Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
         super(key, queue);
         this.hash = hash;
         this.next = next;
      }

      public Object getKey() {
         return this.get();
      }

      public long getAccessTime() {
         throw new UnsupportedOperationException();
      }

      public void setAccessTime(long time) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry getNextInAccessQueue() {
         throw new UnsupportedOperationException();
      }

      public void setNextInAccessQueue(LocalCache.ReferenceEntry next) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry getPreviousInAccessQueue() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousInAccessQueue(LocalCache.ReferenceEntry previous) {
         throw new UnsupportedOperationException();
      }

      public long getWriteTime() {
         throw new UnsupportedOperationException();
      }

      public void setWriteTime(long time) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry getNextInWriteQueue() {
         throw new UnsupportedOperationException();
      }

      public void setNextInWriteQueue(LocalCache.ReferenceEntry next) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ReferenceEntry getPreviousInWriteQueue() {
         throw new UnsupportedOperationException();
      }

      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry previous) {
         throw new UnsupportedOperationException();
      }

      public LocalCache.ValueReference getValueReference() {
         return this.valueReference;
      }

      public void setValueReference(LocalCache.ValueReference valueReference) {
         this.valueReference = valueReference;
      }

      public int getHash() {
         return this.hash;
      }

      public LocalCache.ReferenceEntry getNext() {
         return this.next;
      }
   }

   static class WeakValueReference extends WeakReference implements LocalCache.ValueReference {
      final LocalCache.ReferenceEntry entry;

      WeakValueReference(ReferenceQueue queue, Object referent, LocalCache.ReferenceEntry entry) {
         super(referent, queue);
         this.entry = entry;
      }

      public int getWeight() {
         return 1;
      }

      public LocalCache.ReferenceEntry getEntry() {
         return this.entry;
      }

      public void notifyNewValue(Object newValue) {
      }

      public LocalCache.ValueReference copyFor(ReferenceQueue queue, Object value, LocalCache.ReferenceEntry entry) {
         return new LocalCache.WeakValueReference(queue, value, entry);
      }

      public boolean isLoading() {
         return false;
      }

      public boolean isActive() {
         return true;
      }

      public Object waitForValue() {
         return this.get();
      }
   }

   static final class WeakWriteEntry extends LocalCache.WeakEntry {
      volatile long writeTime = Long.MAX_VALUE;
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry nextWrite = LocalCache.nullEntry();
      @GuardedBy("Segment.this")
      LocalCache.ReferenceEntry previousWrite = LocalCache.nullEntry();

      WeakWriteEntry(ReferenceQueue queue, Object key, int hash, @Nullable LocalCache.ReferenceEntry next) {
         super(queue, key, hash, next);
      }

      public long getWriteTime() {
         return this.writeTime;
      }

      public void setWriteTime(long time) {
         this.writeTime = time;
      }

      public LocalCache.ReferenceEntry getNextInWriteQueue() {
         return this.nextWrite;
      }

      public void setNextInWriteQueue(LocalCache.ReferenceEntry next) {
         this.nextWrite = next;
      }

      public LocalCache.ReferenceEntry getPreviousInWriteQueue() {
         return this.previousWrite;
      }

      public void setPreviousInWriteQueue(LocalCache.ReferenceEntry previous) {
         this.previousWrite = previous;
      }
   }

   static final class WeightedSoftValueReference extends LocalCache.SoftValueReference {
      final int weight;

      WeightedSoftValueReference(ReferenceQueue queue, Object referent, LocalCache.ReferenceEntry entry, int weight) {
         super(queue, referent, entry);
         this.weight = weight;
      }

      public int getWeight() {
         return this.weight;
      }

      public LocalCache.ValueReference copyFor(ReferenceQueue queue, Object value, LocalCache.ReferenceEntry entry) {
         return new LocalCache.WeightedSoftValueReference(queue, value, entry, this.weight);
      }
   }

   static final class WeightedStrongValueReference extends LocalCache.StrongValueReference {
      final int weight;

      WeightedStrongValueReference(Object referent, int weight) {
         super(referent);
         this.weight = weight;
      }

      public int getWeight() {
         return this.weight;
      }
   }

   static final class WeightedWeakValueReference extends LocalCache.WeakValueReference {
      final int weight;

      WeightedWeakValueReference(ReferenceQueue queue, Object referent, LocalCache.ReferenceEntry entry, int weight) {
         super(queue, referent, entry);
         this.weight = weight;
      }

      public int getWeight() {
         return this.weight;
      }

      public LocalCache.ValueReference copyFor(ReferenceQueue queue, Object value, LocalCache.ReferenceEntry entry) {
         return new LocalCache.WeightedWeakValueReference(queue, value, entry, this.weight);
      }
   }

   static final class WriteQueue extends AbstractQueue {
      final LocalCache.ReferenceEntry head = new LocalCache.AbstractReferenceEntry() {
         LocalCache.ReferenceEntry nextWrite = this;
         LocalCache.ReferenceEntry previousWrite = this;

         public long getWriteTime() {
            return Long.MAX_VALUE;
         }

         public void setWriteTime(long time) {
         }

         public LocalCache.ReferenceEntry getNextInWriteQueue() {
            return this.nextWrite;
         }

         public void setNextInWriteQueue(LocalCache.ReferenceEntry next) {
            this.nextWrite = next;
         }

         public LocalCache.ReferenceEntry getPreviousInWriteQueue() {
            return this.previousWrite;
         }

         public void setPreviousInWriteQueue(LocalCache.ReferenceEntry previous) {
            this.previousWrite = previous;
         }
      };

      public boolean offer(LocalCache.ReferenceEntry entry) {
         LocalCache.connectWriteOrder(entry.getPreviousInWriteQueue(), entry.getNextInWriteQueue());
         LocalCache.connectWriteOrder(this.head.getPreviousInWriteQueue(), entry);
         LocalCache.connectWriteOrder(entry, this.head);
         return true;
      }

      public LocalCache.ReferenceEntry peek() {
         LocalCache.ReferenceEntry<K, V> next = this.head.getNextInWriteQueue();
         return next == this.head?null:next;
      }

      public LocalCache.ReferenceEntry poll() {
         LocalCache.ReferenceEntry<K, V> next = this.head.getNextInWriteQueue();
         if(next == this.head) {
            return null;
         } else {
            this.remove(next);
            return next;
         }
      }

      public boolean remove(Object o) {
         LocalCache.ReferenceEntry<K, V> e = (LocalCache.ReferenceEntry)o;
         LocalCache.ReferenceEntry<K, V> previous = e.getPreviousInWriteQueue();
         LocalCache.ReferenceEntry<K, V> next = e.getNextInWriteQueue();
         LocalCache.connectWriteOrder(previous, next);
         LocalCache.nullifyWriteOrder(e);
         return next != LocalCache.NullEntry.INSTANCE;
      }

      public boolean contains(Object o) {
         LocalCache.ReferenceEntry<K, V> e = (LocalCache.ReferenceEntry)o;
         return e.getNextInWriteQueue() != LocalCache.NullEntry.INSTANCE;
      }

      public boolean isEmpty() {
         return this.head.getNextInWriteQueue() == this.head;
      }

      public int size() {
         int size = 0;

         for(LocalCache.ReferenceEntry<K, V> e = this.head.getNextInWriteQueue(); e != this.head; e = e.getNextInWriteQueue()) {
            ++size;
         }

         return size;
      }

      public void clear() {
         LocalCache.ReferenceEntry<K, V> next;
         for(LocalCache.ReferenceEntry<K, V> e = this.head.getNextInWriteQueue(); e != this.head; e = next) {
            next = e.getNextInWriteQueue();
            LocalCache.nullifyWriteOrder(e);
         }

         this.head.setNextInWriteQueue(this.head);
         this.head.setPreviousInWriteQueue(this.head);
      }

      public Iterator iterator() {
         return new AbstractSequentialIterator(this.peek()) {
            protected LocalCache.ReferenceEntry computeNext(LocalCache.ReferenceEntry previous) {
               LocalCache.ReferenceEntry<K, V> next = previous.getNextInWriteQueue();
               return next == WriteQueue.this.head?null:next;
            }
         };
      }
   }

   final class WriteThroughEntry implements Entry {
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
         throw new UnsupportedOperationException();
      }

      public String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }
}
