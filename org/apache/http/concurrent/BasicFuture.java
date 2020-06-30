package org.apache.http.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.http.concurrent.Cancellable;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.Args;

public class BasicFuture implements Future, Cancellable {
   private final FutureCallback callback;
   private volatile boolean completed;
   private volatile boolean cancelled;
   private volatile Object result;
   private volatile Exception ex;

   public BasicFuture(FutureCallback callback) {
      this.callback = callback;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public boolean isDone() {
      return this.completed;
   }

   private Object getResult() throws ExecutionException {
      if(this.ex != null) {
         throw new ExecutionException(this.ex);
      } else {
         return this.result;
      }
   }

   public synchronized Object get() throws InterruptedException, ExecutionException {
      while(!this.completed) {
         this.wait();
      }

      return this.getResult();
   }

   public synchronized Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      Args.notNull(unit, "Time unit");
      long msecs = unit.toMillis(timeout);
      long startTime = msecs <= 0L?0L:System.currentTimeMillis();
      long waitTime = msecs;
      if(this.completed) {
         return this.getResult();
      } else if(msecs <= 0L) {
         throw new TimeoutException();
      } else {
         while(true) {
            this.wait(waitTime);
            if(this.completed) {
               return this.getResult();
            }

            waitTime = msecs - (System.currentTimeMillis() - startTime);
            if(waitTime <= 0L) {
               break;
            }
         }

         throw new TimeoutException();
      }
   }

   public boolean completed(Object result) {
      synchronized(this) {
         if(this.completed) {
            return false;
         }

         this.completed = true;
         this.result = result;
         this.notifyAll();
      }

      if(this.callback != null) {
         this.callback.completed(result);
      }

      return true;
   }

   public boolean failed(Exception exception) {
      synchronized(this) {
         if(this.completed) {
            return false;
         }

         this.completed = true;
         this.ex = exception;
         this.notifyAll();
      }

      if(this.callback != null) {
         this.callback.failed(exception);
      }

      return true;
   }

   public boolean cancel(boolean mayInterruptIfRunning) {
      synchronized(this) {
         if(this.completed) {
            return false;
         }

         this.completed = true;
         this.cancelled = true;
         this.notifyAll();
      }

      if(this.callback != null) {
         this.callback.cancelled();
      }

      return true;
   }

   public boolean cancel() {
      return this.cancel(true);
   }
}
