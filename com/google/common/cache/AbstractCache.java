package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LongAddable;
import com.google.common.cache.LongAddables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@Beta
@GwtCompatible
public abstract class AbstractCache implements Cache {
   public Object get(Object key, Callable valueLoader) throws ExecutionException {
      throw new UnsupportedOperationException();
   }

   public ImmutableMap getAllPresent(Iterable keys) {
      Map<K, V> result = Maps.newLinkedHashMap();

      for(Object key : keys) {
         if(!result.containsKey(key)) {
            result.put(key, this.getIfPresent(key));
         }
      }

      return ImmutableMap.copyOf(result);
   }

   public void put(Object key, Object value) {
      throw new UnsupportedOperationException();
   }

   public void putAll(Map m) {
      for(Entry<? extends K, ? extends V> entry : m.entrySet()) {
         this.put(entry.getKey(), entry.getValue());
      }

   }

   public void cleanUp() {
   }

   public long size() {
      throw new UnsupportedOperationException();
   }

   public void invalidate(Object key) {
      throw new UnsupportedOperationException();
   }

   public void invalidateAll(Iterable keys) {
      for(Object key : keys) {
         this.invalidate(key);
      }

   }

   public void invalidateAll() {
      throw new UnsupportedOperationException();
   }

   public CacheStats stats() {
      throw new UnsupportedOperationException();
   }

   public ConcurrentMap asMap() {
      throw new UnsupportedOperationException();
   }

   @Beta
   public static final class SimpleStatsCounter implements AbstractCache.StatsCounter {
      private final LongAddable hitCount = LongAddables.create();
      private final LongAddable missCount = LongAddables.create();
      private final LongAddable loadSuccessCount = LongAddables.create();
      private final LongAddable loadExceptionCount = LongAddables.create();
      private final LongAddable totalLoadTime = LongAddables.create();
      private final LongAddable evictionCount = LongAddables.create();

      public void recordHits(int count) {
         this.hitCount.add((long)count);
      }

      public void recordMisses(int count) {
         this.missCount.add((long)count);
      }

      public void recordLoadSuccess(long loadTime) {
         this.loadSuccessCount.increment();
         this.totalLoadTime.add(loadTime);
      }

      public void recordLoadException(long loadTime) {
         this.loadExceptionCount.increment();
         this.totalLoadTime.add(loadTime);
      }

      public void recordEviction() {
         this.evictionCount.increment();
      }

      public CacheStats snapshot() {
         return new CacheStats(this.hitCount.sum(), this.missCount.sum(), this.loadSuccessCount.sum(), this.loadExceptionCount.sum(), this.totalLoadTime.sum(), this.evictionCount.sum());
      }

      public void incrementBy(AbstractCache.StatsCounter other) {
         CacheStats otherStats = other.snapshot();
         this.hitCount.add(otherStats.hitCount());
         this.missCount.add(otherStats.missCount());
         this.loadSuccessCount.add(otherStats.loadSuccessCount());
         this.loadExceptionCount.add(otherStats.loadExceptionCount());
         this.totalLoadTime.add(otherStats.totalLoadTime());
         this.evictionCount.add(otherStats.evictionCount());
      }
   }

   @Beta
   public interface StatsCounter {
      void recordHits(int var1);

      void recordMisses(int var1);

      void recordLoadSuccess(long var1);

      void recordLoadException(long var1);

      void recordEviction();

      CacheStats snapshot();
   }
}
