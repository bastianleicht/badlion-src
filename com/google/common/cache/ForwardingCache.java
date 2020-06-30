package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ForwardingObject;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;

@Beta
public abstract class ForwardingCache extends ForwardingObject implements Cache {
   protected abstract Cache delegate();

   @Nullable
   public Object getIfPresent(Object key) {
      return this.delegate().getIfPresent(key);
   }

   public Object get(Object key, Callable valueLoader) throws ExecutionException {
      return this.delegate().get(key, valueLoader);
   }

   public ImmutableMap getAllPresent(Iterable keys) {
      return this.delegate().getAllPresent(keys);
   }

   public void put(Object key, Object value) {
      this.delegate().put(key, value);
   }

   public void putAll(Map m) {
      this.delegate().putAll(m);
   }

   public void invalidate(Object key) {
      this.delegate().invalidate(key);
   }

   public void invalidateAll(Iterable keys) {
      this.delegate().invalidateAll(keys);
   }

   public void invalidateAll() {
      this.delegate().invalidateAll();
   }

   public long size() {
      return this.delegate().size();
   }

   public CacheStats stats() {
      return this.delegate().stats();
   }

   public ConcurrentMap asMap() {
      return this.delegate().asMap();
   }

   public void cleanUp() {
      this.delegate().cleanUp();
   }

   @Beta
   public abstract static class SimpleForwardingCache extends ForwardingCache {
      private final Cache delegate;

      protected SimpleForwardingCache(Cache delegate) {
         this.delegate = (Cache)Preconditions.checkNotNull(delegate);
      }

      protected final Cache delegate() {
         return this.delegate;
      }
   }
}
