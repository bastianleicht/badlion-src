package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.cache.ForwardingCache;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;

@Beta
public abstract class ForwardingLoadingCache extends ForwardingCache implements LoadingCache {
   protected abstract LoadingCache delegate();

   public Object get(Object key) throws ExecutionException {
      return this.delegate().get(key);
   }

   public Object getUnchecked(Object key) {
      return this.delegate().getUnchecked(key);
   }

   public ImmutableMap getAll(Iterable keys) throws ExecutionException {
      return this.delegate().getAll(keys);
   }

   public Object apply(Object key) {
      return this.delegate().apply(key);
   }

   public void refresh(Object key) {
      this.delegate().refresh(key);
   }

   @Beta
   public abstract static class SimpleForwardingLoadingCache extends ForwardingLoadingCache {
      private final LoadingCache delegate;

      protected SimpleForwardingLoadingCache(LoadingCache delegate) {
         this.delegate = (LoadingCache)Preconditions.checkNotNull(delegate);
      }

      protected final LoadingCache delegate() {
         return this.delegate;
      }
   }
}
