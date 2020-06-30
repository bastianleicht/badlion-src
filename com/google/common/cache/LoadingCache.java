package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@Beta
@GwtCompatible
public interface LoadingCache extends Cache, Function {
   Object get(Object var1) throws ExecutionException;

   Object getUnchecked(Object var1);

   ImmutableMap getAll(Iterable var1) throws ExecutionException;

   /** @deprecated */
   @Deprecated
   Object apply(Object var1);

   void refresh(Object var1);

   ConcurrentMap asMap();
}
