package io.netty.util.concurrent;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ProgressivePromise;

public class DefaultProgressivePromise extends DefaultPromise implements ProgressivePromise {
   public DefaultProgressivePromise(EventExecutor executor) {
      super(executor);
   }

   protected DefaultProgressivePromise() {
   }

   public ProgressivePromise setProgress(long progress, long total) {
      if(total < 0L) {
         total = -1L;
         if(progress < 0L) {
            throw new IllegalArgumentException("progress: " + progress + " (expected: >= 0)");
         }
      } else if(progress < 0L || progress > total) {
         throw new IllegalArgumentException("progress: " + progress + " (expected: 0 <= progress <= total (" + total + "))");
      }

      if(this.isDone()) {
         throw new IllegalStateException("complete already");
      } else {
         this.notifyProgressiveListeners(progress, total);
         return this;
      }
   }

   public boolean tryProgress(long progress, long total) {
      if(total < 0L) {
         total = -1L;
         if(progress < 0L || this.isDone()) {
            return false;
         }
      } else if(progress < 0L || progress > total || this.isDone()) {
         return false;
      }

      this.notifyProgressiveListeners(progress, total);
      return true;
   }

   public ProgressivePromise addListener(GenericFutureListener listener) {
      super.addListener(listener);
      return this;
   }

   public ProgressivePromise addListeners(GenericFutureListener... listeners) {
      super.addListeners(listeners);
      return this;
   }

   public ProgressivePromise removeListener(GenericFutureListener listener) {
      super.removeListener(listener);
      return this;
   }

   public ProgressivePromise removeListeners(GenericFutureListener... listeners) {
      super.removeListeners(listeners);
      return this;
   }

   public ProgressivePromise sync() throws InterruptedException {
      super.sync();
      return this;
   }

   public ProgressivePromise syncUninterruptibly() {
      super.syncUninterruptibly();
      return this;
   }

   public ProgressivePromise await() throws InterruptedException {
      super.await();
      return this;
   }

   public ProgressivePromise awaitUninterruptibly() {
      super.awaitUninterruptibly();
      return this;
   }

   public ProgressivePromise setSuccess(Object result) {
      super.setSuccess(result);
      return this;
   }

   public ProgressivePromise setFailure(Throwable cause) {
      super.setFailure(cause);
      return this;
   }
}
