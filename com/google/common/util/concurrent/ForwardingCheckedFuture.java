package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.ForwardingListenableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public abstract class ForwardingCheckedFuture extends ForwardingListenableFuture implements CheckedFuture {
   public Object checkedGet() throws Exception {
      return this.delegate().checkedGet();
   }

   public Object checkedGet(long timeout, TimeUnit unit) throws TimeoutException, Exception {
      return this.delegate().checkedGet(timeout, unit);
   }

   protected abstract CheckedFuture delegate();

   @Beta
   public abstract static class SimpleForwardingCheckedFuture extends ForwardingCheckedFuture {
      private final CheckedFuture delegate;

      protected SimpleForwardingCheckedFuture(CheckedFuture delegate) {
         this.delegate = (CheckedFuture)Preconditions.checkNotNull(delegate);
      }

      protected final CheckedFuture delegate() {
         return this.delegate;
      }
   }
}
