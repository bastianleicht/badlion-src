package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingObject;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class ForwardingFuture extends ForwardingObject implements Future {
   protected abstract Future delegate();

   public boolean cancel(boolean mayInterruptIfRunning) {
      return this.delegate().cancel(mayInterruptIfRunning);
   }

   public boolean isCancelled() {
      return this.delegate().isCancelled();
   }

   public boolean isDone() {
      return this.delegate().isDone();
   }

   public Object get() throws InterruptedException, ExecutionException {
      return this.delegate().get();
   }

   public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return this.delegate().get(timeout, unit);
   }

   public abstract static class SimpleForwardingFuture extends ForwardingFuture {
      private final Future delegate;

      protected SimpleForwardingFuture(Future delegate) {
         this.delegate = (Future)Preconditions.checkNotNull(delegate);
      }

      protected final Future delegate() {
         return this.delegate;
      }
   }
}
