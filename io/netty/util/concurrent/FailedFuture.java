package io.netty.util.concurrent;

import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.PlatformDependent;

public final class FailedFuture extends CompleteFuture {
   private final Throwable cause;

   public FailedFuture(EventExecutor executor, Throwable cause) {
      super(executor);
      if(cause == null) {
         throw new NullPointerException("cause");
      } else {
         this.cause = cause;
      }
   }

   public Throwable cause() {
      return this.cause;
   }

   public boolean isSuccess() {
      return false;
   }

   public Future sync() {
      PlatformDependent.throwException(this.cause);
      return this;
   }

   public Future syncUninterruptibly() {
      PlatformDependent.throwException(this.cause);
      return this;
   }

   public Object getNow() {
      return null;
   }
}
