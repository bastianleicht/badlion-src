package io.netty.util.concurrent;

import io.netty.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractFuture implements Future {
   public Object get() throws InterruptedException, ExecutionException {
      this.await();
      Throwable cause = this.cause();
      if(cause == null) {
         return this.getNow();
      } else {
         throw new ExecutionException(cause);
      }
   }

   public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      if(this.await(timeout, unit)) {
         Throwable cause = this.cause();
         if(cause == null) {
            return this.getNow();
         } else {
            throw new ExecutionException(cause);
         }
      } else {
         throw new TimeoutException();
      }
   }
}
