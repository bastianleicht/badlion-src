package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

@GwtCompatible(
   emulated = true
)
public abstract class CacheLoader {
   public abstract Object load(Object var1) throws Exception;

   @GwtIncompatible("Futures")
   public ListenableFuture reload(Object key, Object oldValue) throws Exception {
      Preconditions.checkNotNull(key);
      Preconditions.checkNotNull(oldValue);
      return Futures.immediateFuture(this.load(key));
   }

   public Map loadAll(Iterable keys) throws Exception {
      throw new CacheLoader.UnsupportedLoadingOperationException();
   }

   @Beta
   public static CacheLoader from(Function function) {
      return new CacheLoader.FunctionToCacheLoader(function);
   }

   @Beta
   public static CacheLoader from(Supplier supplier) {
      return new CacheLoader.SupplierToCacheLoader(supplier);
   }

   @Beta
   @GwtIncompatible("Executor + Futures")
   public static CacheLoader asyncReloading(final CacheLoader loader, final Executor executor) {
      Preconditions.checkNotNull(loader);
      Preconditions.checkNotNull(executor);
      return new CacheLoader() {
         public Object load(Object key) throws Exception {
            return loader.load(key);
         }

         public ListenableFuture reload(final Object key, final Object oldValue) throws Exception {
            ListenableFutureTask<V> task = ListenableFutureTask.create(new Callable() {
               public Object call() throws Exception {
                  return loader.reload(key, oldValue).get();
               }
            });
            executor.execute(task);
            return task;
         }

         public Map loadAll(Iterable keys) throws Exception {
            return loader.loadAll(keys);
         }
      };
   }

   private static final class FunctionToCacheLoader extends CacheLoader implements Serializable {
      private final Function computingFunction;
      private static final long serialVersionUID = 0L;

      public FunctionToCacheLoader(Function computingFunction) {
         this.computingFunction = (Function)Preconditions.checkNotNull(computingFunction);
      }

      public Object load(Object key) {
         return this.computingFunction.apply(Preconditions.checkNotNull(key));
      }
   }

   public static final class InvalidCacheLoadException extends RuntimeException {
      public InvalidCacheLoadException(String message) {
         super(message);
      }
   }

   private static final class SupplierToCacheLoader extends CacheLoader implements Serializable {
      private final Supplier computingSupplier;
      private static final long serialVersionUID = 0L;

      public SupplierToCacheLoader(Supplier computingSupplier) {
         this.computingSupplier = (Supplier)Preconditions.checkNotNull(computingSupplier);
      }

      public Object load(Object key) {
         Preconditions.checkNotNull(key);
         return this.computingSupplier.get();
      }
   }

   static final class UnsupportedLoadingOperationException extends UnsupportedOperationException {
   }
}
