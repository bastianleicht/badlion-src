package io.netty.util.concurrent;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

class PromiseTask extends DefaultPromise implements RunnableFuture {
   protected final Callable task;

   static Callable toCallable(Runnable runnable, Object result) {
      return new PromiseTask.RunnableAdapter(runnable, result);
   }

   PromiseTask(EventExecutor executor, Runnable runnable, Object result) {
      this(executor, toCallable(runnable, result));
   }

   PromiseTask(EventExecutor executor, Callable callable) {
      super(executor);
      this.task = callable;
   }

   public final int hashCode() {
      return System.identityHashCode(this);
   }

   public final boolean equals(Object obj) {
      return this == obj;
   }

   public void run() {
      try {
         if(this.setUncancellableInternal()) {
            V result = this.task.call();
            this.setSuccessInternal(result);
         }
      } catch (Throwable var2) {
         this.setFailureInternal(var2);
      }

   }

   public final Promise setFailure(Throwable cause) {
      throw new IllegalStateException();
   }

   protected final Promise setFailureInternal(Throwable cause) {
      super.setFailure(cause);
      return this;
   }

   public final boolean tryFailure(Throwable cause) {
      return false;
   }

   protected final boolean tryFailureInternal(Throwable cause) {
      return super.tryFailure(cause);
   }

   public final Promise setSuccess(Object result) {
      throw new IllegalStateException();
   }

   protected final Promise setSuccessInternal(Object result) {
      super.setSuccess(result);
      return this;
   }

   public final boolean trySuccess(Object result) {
      return false;
   }

   protected final boolean trySuccessInternal(Object result) {
      return super.trySuccess(result);
   }

   public final boolean setUncancellable() {
      throw new IllegalStateException();
   }

   protected final boolean setUncancellableInternal() {
      return super.setUncancellable();
   }

   protected StringBuilder toStringBuilder() {
      StringBuilder buf = super.toStringBuilder();
      buf.setCharAt(buf.length() - 1, ',');
      buf.append(" task: ");
      buf.append(this.task);
      buf.append(')');
      return buf;
   }

   private static final class RunnableAdapter implements Callable {
      final Runnable task;
      final Object result;

      RunnableAdapter(Runnable task, Object result) {
         this.task = task;
         this.result = result;
      }

      public Object call() {
         this.task.run();
         return this.result;
      }

      public String toString() {
         return "Callable(task: " + this.task + ", result: " + this.result + ')';
      }
   }
}
