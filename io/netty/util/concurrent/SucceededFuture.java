package io.netty.util.concurrent;

import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.EventExecutor;

public final class SucceededFuture extends CompleteFuture {
   private final Object result;

   public SucceededFuture(EventExecutor executor, Object result) {
      super(executor);
      this.result = result;
   }

   public Throwable cause() {
      return null;
   }

   public boolean isSuccess() {
      return true;
   }

   public Object getNow() {
      return this.result;
   }
}
