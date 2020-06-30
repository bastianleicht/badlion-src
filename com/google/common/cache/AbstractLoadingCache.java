package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.cache.AbstractCache;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Beta
public abstract class AbstractLoadingCache extends AbstractCache implements LoadingCache {
   public Object getUnchecked(Object key) {
      try {
         return this.get(key);
      } catch (ExecutionException var3) {
         throw new UncheckedExecutionException(var3.getCause());
      }
   }

   public ImmutableMap getAll(Iterable keys) throws ExecutionException {
      Map<K, V> result = Maps.newLinkedHashMap();

      for(K key : keys) {
         if(!result.containsKey(key)) {
            result.put(key, this.get(key));
         }
      }

      return ImmutableMap.copyOf(result);
   }

   public final Object apply(Object key) {
      return this.getUnchecked(key);
   }

   public void refresh(Object key) {
      throw new UnsupportedOperationException();
   }
}
