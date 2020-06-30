package com.google.common.collect;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.google.common.collect.MapMakerInternalMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReferenceArray;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

class ComputingConcurrentHashMap extends MapMakerInternalMap {
   final Function computingFunction;
   private static final long serialVersionUID = 4L;

   ComputingConcurrentHashMap(MapMaker builder, Function computingFunction) {
      super(builder);
      this.computingFunction = (Function)Preconditions.checkNotNull(computingFunction);
   }

   MapMakerInternalMap.Segment createSegment(int initialCapacity, int maxSegmentSize) {
      return new ComputingConcurrentHashMap.ComputingSegment(this, initialCapacity, maxSegmentSize);
   }

   ComputingConcurrentHashMap.ComputingSegment segmentFor(int hash) {
      return (ComputingConcurrentHashMap.ComputingSegment)super.segmentFor(hash);
   }

   Object getOrCompute(Object key) throws ExecutionException {
      int hash = this.hash(Preconditions.checkNotNull(key));
      return this.segmentFor(hash).getOrCompute(key, hash, this.computingFunction);
   }

   Object writeReplace() {
      return new ComputingConcurrentHashMap.ComputingSerializationProxy(this.keyStrength, this.valueStrength, this.keyEquivalence, this.valueEquivalence, this.expireAfterWriteNanos, this.expireAfterAccessNanos, this.maximumSize, this.concurrencyLevel, this.removalListener, this, this.computingFunction);
   }

   private static final class ComputationExceptionReference implements MapMakerInternalMap.ValueReference {
      final Throwable t;

      ComputationExceptionReference(Throwable t) {
         this.t = t;
      }

      public Object get() {
         return null;
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

      public Object waitForValue() throws ExecutionException {
         throw new ExecutionException(this.t);
      }

      public void clear(MapMakerInternalMap.ValueReference newValue) {
      }
   }

   private static final class ComputedReference implements MapMakerInternalMap.ValueReference {
      final Object value;

      ComputedReference(@Nullable Object value) {
         this.value = value;
      }

      public Object get() {
         return this.value;
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

   static final class ComputingSegment extends MapMakerInternalMap.Segment {
      ComputingSegment(MapMakerInternalMap map, int initialCapacity, int maxSegmentSize) {
         super(map, initialCapacity, maxSegmentSize);
      }

      Object getOrCompute(Object key, int hash, Function computingFunction) throws ExecutionException {
         try {
            MapMakerInternalMap.ReferenceEntry<K, V> e;
            V value;
            while(true) {
               e = this.getEntry(key, hash);
               if(e != null) {
                  value = this.getLiveValue(e);
                  if(value != null) {
                     this.recordRead(e);
                     Object var26 = value;
                     return var26;
                  }
               }

               if(e == null || !e.getValueReference().isComputingReference()) {
                  boolean createNewEntry = true;
                  ComputingConcurrentHashMap.ComputingValueReference<K, V> computingValueReference = null;
                  this.lock();

                  try {
                     this.preWriteCleanup();
                     int newCount = this.count - 1;
                     AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table = this.table;
                     int index = hash & table.length() - 1;
                     MapMakerInternalMap.ReferenceEntry<K, V> first = (MapMakerInternalMap.ReferenceEntry)table.get(index);

                     for(e = first; e != null; e = e.getNext()) {
                        K entryKey = e.getKey();
                        if(e.getHash() == hash && entryKey != null && this.map.keyEquivalence.equivalent(key, entryKey)) {
                           MapMakerInternalMap.ValueReference<K, V> valueReference = e.getValueReference();
                           if(valueReference.isComputingReference()) {
                              createNewEntry = false;
                              break;
                           }

                           V value = e.getValueReference().get();
                           if(value == null) {
                              this.enqueueNotification(entryKey, hash, value, MapMaker.RemovalCause.COLLECTED);
                           } else {
                              if(!this.map.expires() || !this.map.isExpired(e)) {
                                 this.recordLockedRead(e);
                                 Object var14 = value;
                                 return var14;
                              }

                              this.enqueueNotification(entryKey, hash, value, MapMaker.RemovalCause.EXPIRED);
                           }

                           this.evictionQueue.remove(e);
                           this.expirationQueue.remove(e);
                           this.count = newCount;
                           break;
                        }
                     }

                     if(createNewEntry) {
                        computingValueReference = new ComputingConcurrentHashMap.ComputingValueReference(computingFunction);
                        if(e == null) {
                           e = this.newEntry(key, hash, first);
                           e.setValueReference(computingValueReference);
                           table.set(index, e);
                        } else {
                           e.setValueReference(computingValueReference);
                        }
                     }
                  } finally {
                     this.unlock();
                     this.postWriteCleanup();
                  }

                  if(createNewEntry) {
                     Object var27 = this.compute(key, hash, e, computingValueReference);
                     return var27;
                  }
               }

               Preconditions.checkState(!Thread.holdsLock(e), "Recursive computation");
               value = e.getValueReference().waitForValue();
               if(value != null) {
                  break;
               }
            }

            this.recordRead(e);
            Object var25 = value;
            return var25;
         } finally {
            this.postReadCleanup();
         }
      }

      Object compute(Object key, int hash, MapMakerInternalMap.ReferenceEntry e, ComputingConcurrentHashMap.ComputingValueReference computingValueReference) throws ExecutionException {
         V value = null;
         long start = System.nanoTime();
         long end = 0L;

         Object var18;
         try {
            synchronized(e) {
               value = computingValueReference.compute(key, hash);
               end = System.nanoTime();
            }

            if(value != null) {
               oldValue = this.put(key, hash, value, true);
               if(var18 != null) {
                  this.enqueueNotification(key, hash, value, MapMaker.RemovalCause.REPLACED);
               }
            }

            var18 = value;
         } finally {
            if(end == 0L) {
               end = System.nanoTime();
            }

            if(value == null) {
               this.clearValue(key, hash, computingValueReference);
            }

         }

         return var18;
      }
   }

   static final class ComputingSerializationProxy extends MapMakerInternalMap.AbstractSerializationProxy {
      final Function computingFunction;
      private static final long serialVersionUID = 4L;

      ComputingSerializationProxy(MapMakerInternalMap.Strength keyStrength, MapMakerInternalMap.Strength valueStrength, Equivalence keyEquivalence, Equivalence valueEquivalence, long expireAfterWriteNanos, long expireAfterAccessNanos, int maximumSize, int concurrencyLevel, MapMaker.RemovalListener removalListener, ConcurrentMap delegate, Function computingFunction) {
         super(keyStrength, valueStrength, keyEquivalence, valueEquivalence, expireAfterWriteNanos, expireAfterAccessNanos, maximumSize, concurrencyLevel, removalListener, delegate);
         this.computingFunction = computingFunction;
      }

      private void writeObject(ObjectOutputStream out) throws IOException {
         out.defaultWriteObject();
         this.writeMapTo(out);
      }

      private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
         in.defaultReadObject();
         MapMaker mapMaker = this.readMapMaker(in);
         this.delegate = mapMaker.makeComputingMap(this.computingFunction);
         this.readEntries(in);
      }

      Object readResolve() {
         return this.delegate;
      }
   }

   private static final class ComputingValueReference implements MapMakerInternalMap.ValueReference {
      final Function computingFunction;
      @GuardedBy("ComputingValueReference.this")
      volatile MapMakerInternalMap.ValueReference computedReference = MapMakerInternalMap.unset();

      public ComputingValueReference(Function computingFunction) {
         this.computingFunction = computingFunction;
      }

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
         return true;
      }

      public Object waitForValue() throws ExecutionException {
         if(this.computedReference == MapMakerInternalMap.UNSET) {
            boolean interrupted = false;

            try {
               synchronized(this) {
                  while(this.computedReference == MapMakerInternalMap.UNSET) {
                     try {
                        this.wait();
                     } catch (InterruptedException var9) {
                        interrupted = true;
                     }
                  }
               }
            } finally {
               if(interrupted) {
                  Thread.currentThread().interrupt();
               }

            }
         }

         return this.computedReference.waitForValue();
      }

      public void clear(MapMakerInternalMap.ValueReference newValue) {
         this.setValueReference(newValue);
      }

      Object compute(Object key, int hash) throws ExecutionException {
         V value;
         try {
            value = this.computingFunction.apply(key);
         } catch (Throwable var5) {
            this.setValueReference(new ComputingConcurrentHashMap.ComputationExceptionReference(var5));
            throw new ExecutionException(var5);
         }

         this.setValueReference(new ComputingConcurrentHashMap.ComputedReference(value));
         return value;
      }

      void setValueReference(MapMakerInternalMap.ValueReference valueReference) {
         synchronized(this) {
            if(this.computedReference == MapMakerInternalMap.UNSET) {
               this.computedReference = valueReference;
               this.notifyAll();
            }

         }
      }
   }
}
