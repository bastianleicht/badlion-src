package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@GwtCompatible
public final class AtomicLongMap {
   private final ConcurrentHashMap map;
   private transient Map asMap;

   private AtomicLongMap(ConcurrentHashMap map) {
      this.map = (ConcurrentHashMap)Preconditions.checkNotNull(map);
   }

   public static AtomicLongMap create() {
      return new AtomicLongMap(new ConcurrentHashMap());
   }

   public static AtomicLongMap create(Map m) {
      AtomicLongMap<K> result = create();
      result.putAll(m);
      return result;
   }

   public long get(Object key) {
      AtomicLong atomic = (AtomicLong)this.map.get(key);
      return atomic == null?0L:atomic.get();
   }

   public long incrementAndGet(Object key) {
      return this.addAndGet(key, 1L);
   }

   public long decrementAndGet(Object key) {
      return this.addAndGet(key, -1L);
   }

   public long addAndGet(Object key, long delta) {
      label0:
      while(true) {
         AtomicLong atomic = (AtomicLong)this.map.get(key);
         if(atomic == null) {
            atomic = (AtomicLong)this.map.putIfAbsent(key, new AtomicLong(delta));
            if(atomic == null) {
               return delta;
            }
         }

         long newValue;
         while(true) {
            long oldValue = atomic.get();
            if(oldValue == 0L) {
               if(this.map.replace(key, atomic, new AtomicLong(delta))) {
                  break label0;
               }
               continue label0;
            }

            newValue = oldValue + delta;
            if(atomic.compareAndSet(oldValue, newValue)) {
               break;
            }
         }

         return newValue;
      }

      return delta;
   }

   public long getAndIncrement(Object key) {
      return this.getAndAdd(key, 1L);
   }

   public long getAndDecrement(Object key) {
      return this.getAndAdd(key, -1L);
   }

   public long getAndAdd(Object key, long delta) {
      label0:
      while(true) {
         AtomicLong atomic = (AtomicLong)this.map.get(key);
         if(atomic == null) {
            atomic = (AtomicLong)this.map.putIfAbsent(key, new AtomicLong(delta));
            if(atomic == null) {
               return 0L;
            }
         }

         long oldValue;
         while(true) {
            oldValue = atomic.get();
            if(oldValue == 0L) {
               if(this.map.replace(key, atomic, new AtomicLong(delta))) {
                  break label0;
               }
               continue label0;
            }

            long newValue = oldValue + delta;
            if(atomic.compareAndSet(oldValue, newValue)) {
               break;
            }
         }

         return oldValue;
      }

      return 0L;
   }

   public long put(Object key, long newValue) {
      label0:
      while(true) {
         AtomicLong atomic = (AtomicLong)this.map.get(key);
         if(atomic == null) {
            atomic = (AtomicLong)this.map.putIfAbsent(key, new AtomicLong(newValue));
            if(atomic == null) {
               return 0L;
            }
         }

         long oldValue;
         while(true) {
            oldValue = atomic.get();
            if(oldValue == 0L) {
               if(this.map.replace(key, atomic, new AtomicLong(newValue))) {
                  break label0;
               }
               continue label0;
            }

            if(atomic.compareAndSet(oldValue, newValue)) {
               break;
            }
         }

         return oldValue;
      }

      return 0L;
   }

   public void putAll(Map m) {
      for(Entry<? extends K, ? extends Long> entry : m.entrySet()) {
         this.put(entry.getKey(), ((Long)entry.getValue()).longValue());
      }

   }

   public long remove(Object key) {
      AtomicLong atomic = (AtomicLong)this.map.get(key);
      if(atomic == null) {
         return 0L;
      } else {
         long oldValue;
         while(true) {
            oldValue = atomic.get();
            if(oldValue == 0L || atomic.compareAndSet(oldValue, 0L)) {
               break;
            }
         }

         this.map.remove(key, atomic);
         return oldValue;
      }
   }

   public void removeAllZeros() {
      for(K key : this.map.keySet()) {
         AtomicLong atomic = (AtomicLong)this.map.get(key);
         if(atomic != null && atomic.get() == 0L) {
            this.map.remove(key, atomic);
         }
      }

   }

   public long sum() {
      long sum = 0L;

      for(AtomicLong value : this.map.values()) {
         sum += value.get();
      }

      return sum;
   }

   public Map asMap() {
      Map<K, Long> result = this.asMap;
      return result == null?(this.asMap = this.createAsMap()):result;
   }

   private Map createAsMap() {
      return Collections.unmodifiableMap(Maps.transformValues((Map)this.map, new Function() {
         public Long apply(AtomicLong atomic) {
            return Long.valueOf(atomic.get());
         }
      }));
   }

   public boolean containsKey(Object key) {
      return this.map.containsKey(key);
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

   public String toString() {
      return this.map.toString();
   }

   long putIfAbsent(Object key, long newValue) {
      while(true) {
         AtomicLong atomic = (AtomicLong)this.map.get(key);
         if(atomic == null) {
            atomic = (AtomicLong)this.map.putIfAbsent(key, new AtomicLong(newValue));
            if(atomic == null) {
               return 0L;
            }
         }

         long oldValue = atomic.get();
         if(oldValue == 0L) {
            if(!this.map.replace(key, atomic, new AtomicLong(newValue))) {
               continue;
            }

            return 0L;
         }

         return oldValue;
      }
   }

   boolean replace(Object key, long expectedOldValue, long newValue) {
      if(expectedOldValue == 0L) {
         return this.putIfAbsent(key, newValue) == 0L;
      } else {
         AtomicLong atomic = (AtomicLong)this.map.get(key);
         return atomic == null?false:atomic.compareAndSet(expectedOldValue, newValue);
      }
   }

   boolean remove(Object key, long value) {
      AtomicLong atomic = (AtomicLong)this.map.get(key);
      if(atomic == null) {
         return false;
      } else {
         long oldValue = atomic.get();
         if(oldValue != value) {
            return false;
         } else if(oldValue != 0L && !atomic.compareAndSet(oldValue, 0L)) {
            return false;
         } else {
            this.map.remove(key, atomic);
            return true;
         }
      }
   }
}
