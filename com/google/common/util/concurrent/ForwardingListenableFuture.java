package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ForwardingFuture;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Executor;

public abstract class ForwardingListenableFuture extends ForwardingFuture implements ListenableFuture {
   protected abstract ListenableFuture delegate();

   public void addListener(Runnable listener, Executor exec) {
      this.delegate().addListener(listener, exec);
   }

   public abstract static class SimpleForwardingListenableFuture extends ForwardingListenableFuture {
      private final ListenableFuture delegate;

      protected SimpleForwardingListenableFuture(ListenableFuture delegate) {
         this.delegate = (ListenableFuture)Preconditions.checkNotNull(delegate);
      }

      protected final ListenableFuture delegate() {
         return this.delegate;
      }
   }
}
