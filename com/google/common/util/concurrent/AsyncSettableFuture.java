package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ForwardingListenableFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import javax.annotation.Nullable;

final class AsyncSettableFuture extends ForwardingListenableFuture {
   private final AsyncSettableFuture.NestedFuture nested = new AsyncSettableFuture.NestedFuture();
   private final ListenableFuture dereferenced;

   public static AsyncSettableFuture create() {
      return new AsyncSettableFuture();
   }

   private AsyncSettableFuture() {
      this.dereferenced = Futures.dereference(this.nested);
   }

   protected ListenableFuture delegate() {
      return this.dereferenced;
   }

   public boolean setFuture(ListenableFuture future) {
      return this.nested.setFuture((ListenableFuture)Preconditions.checkNotNull(future));
   }

   public boolean setValue(@Nullable Object value) {
      return this.setFuture(Futures.immediateFuture(value));
   }

   public boolean setException(Throwable exception) {
      return this.setFuture(Futures.immediateFailedFuture(exception));
   }

   public boolean isSet() {
      return this.nested.isDone();
   }

   private static final class NestedFuture extends AbstractFuture {
      private NestedFuture() {
      }

      boolean setFuture(ListenableFuture value) {
         boolean result = this.set(value);
         if(this.isCancelled()) {
            value.cancel(this.wasInterrupted());
         }

         return result;
      }
   }
}
