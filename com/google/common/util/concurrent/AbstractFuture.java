package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ExecutionList;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import javax.annotation.Nullable;

public abstract class AbstractFuture implements ListenableFuture {
   private final AbstractFuture.Sync sync = new AbstractFuture.Sync();
   private final ExecutionList executionList = new ExecutionList();

   public Object get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
      return this.sync.get(unit.toNanos(timeout));
   }

   public Object get() throws InterruptedException, ExecutionException {
      return this.sync.get();
   }

   public boolean isDone() {
      return this.sync.isDone();
   }

   public boolean isCancelled() {
      return this.sync.isCancelled();
   }

   public boolean cancel(boolean mayInterruptIfRunning) {
      if(!this.sync.cancel(mayInterruptIfRunning)) {
         return false;
      } else {
         this.executionList.execute();
         if(mayInterruptIfRunning) {
            this.interruptTask();
         }

         return true;
      }
   }

   protected void interruptTask() {
   }

   protected final boolean wasInterrupted() {
      return this.sync.wasInterrupted();
   }

   public void addListener(Runnable listener, Executor exec) {
      this.executionList.add(listener, exec);
   }

   protected boolean set(@Nullable Object value) {
      boolean result = this.sync.set(value);
      if(result) {
         this.executionList.execute();
      }

      return result;
   }

   protected boolean setException(Throwable throwable) {
      boolean result = this.sync.setException((Throwable)Preconditions.checkNotNull(throwable));
      if(result) {
         this.executionList.execute();
      }

      return result;
   }

   static final CancellationException cancellationExceptionWithCause(@Nullable String message, @Nullable Throwable cause) {
      CancellationException exception = new CancellationException(message);
      exception.initCause(cause);
      return exception;
   }

   static final class Sync extends AbstractQueuedSynchronizer {
      private static final long serialVersionUID = 0L;
      static final int RUNNING = 0;
      static final int COMPLETING = 1;
      static final int COMPLETED = 2;
      static final int CANCELLED = 4;
      static final int INTERRUPTED = 8;
      private Object value;
      private Throwable exception;

      protected int tryAcquireShared(int ignored) {
         return this.isDone()?1:-1;
      }

      protected boolean tryReleaseShared(int finalState) {
         this.setState(finalState);
         return true;
      }

      Object get(long nanos) throws TimeoutException, CancellationException, ExecutionException, InterruptedException {
         if(!this.tryAcquireSharedNanos(-1, nanos)) {
            throw new TimeoutException("Timeout waiting for task.");
         } else {
            return this.getValue();
         }
      }

      Object get() throws CancellationException, ExecutionException, InterruptedException {
         this.acquireSharedInterruptibly(-1);
         return this.getValue();
      }

      private Object getValue() throws CancellationException, ExecutionException {
         int state = this.getState();
         switch(state) {
         case 2:
            if(this.exception != null) {
               throw new ExecutionException(this.exception);
            }

            return this.value;
         case 4:
         case 8:
            throw AbstractFuture.cancellationExceptionWithCause("Task was cancelled.", this.exception);
         default:
            throw new IllegalStateException("Error, synchronizer in invalid state: " + state);
         }
      }

      boolean isDone() {
         return (this.getState() & 14) != 0;
      }

      boolean isCancelled() {
         return (this.getState() & 12) != 0;
      }

      boolean wasInterrupted() {
         return this.getState() == 8;
      }

      boolean set(@Nullable Object v) {
         return this.complete(v, (Throwable)null, 2);
      }

      boolean setException(Throwable t) {
         return this.complete((Object)null, t, 2);
      }

      boolean cancel(boolean interrupt) {
         return this.complete((Object)null, (Throwable)null, interrupt?8:4);
      }

      private boolean complete(@Nullable Object v, @Nullable Throwable t, int finalState) {
         boolean doCompletion = this.compareAndSetState(0, 1);
         if(doCompletion) {
            this.value = v;
            this.exception = (Throwable)((finalState & 12) != 0?new CancellationException("Future.cancel() was called."):t);
            this.releaseShared(finalState);
         } else if(this.getState() == 1) {
            this.acquireShared(-1);
         }

         return doCompletion;
      }
   }
}
