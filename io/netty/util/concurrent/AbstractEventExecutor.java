package io.netty.util.concurrent;

import io.netty.util.concurrent.DefaultProgressivePromise;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ProgressivePromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseTask;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.concurrent.SucceededFuture;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractEventExecutor extends AbstractExecutorService implements EventExecutor {
   public EventExecutor next() {
      return this;
   }

   public boolean inEventLoop() {
      return this.inEventLoop(Thread.currentThread());
   }

   public Iterator iterator() {
      return new AbstractEventExecutor.EventExecutorIterator();
   }

   public Future shutdownGracefully() {
      return this.shutdownGracefully(2L, 15L, TimeUnit.SECONDS);
   }

   /** @deprecated */
   @Deprecated
   public abstract void shutdown();

   /** @deprecated */
   @Deprecated
   public List shutdownNow() {
      this.shutdown();
      return Collections.emptyList();
   }

   public Promise newPromise() {
      return new DefaultPromise(this);
   }

   public ProgressivePromise newProgressivePromise() {
      return new DefaultProgressivePromise(this);
   }

   public Future newSucceededFuture(Object result) {
      return new SucceededFuture(this, result);
   }

   public Future newFailedFuture(Throwable cause) {
      return new FailedFuture(this, cause);
   }

   public Future submit(Runnable task) {
      return (Future)super.submit(task);
   }

   public Future submit(Runnable task, Object result) {
      return (Future)super.submit(task, result);
   }

   public Future submit(Callable task) {
      return (Future)super.submit(task);
   }

   protected final RunnableFuture newTaskFor(Runnable runnable, Object value) {
      return new PromiseTask(this, runnable, value);
   }

   protected final RunnableFuture newTaskFor(Callable callable) {
      return new PromiseTask(this, callable);
   }

   public ScheduledFuture schedule(Runnable command, long delay, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   public ScheduledFuture schedule(Callable callable, long delay, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   public ScheduledFuture scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   public ScheduledFuture scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   private final class EventExecutorIterator implements Iterator {
      private boolean nextCalled;

      private EventExecutorIterator() {
      }

      public boolean hasNext() {
         return !this.nextCalled;
      }

      public EventExecutor next() {
         if(!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.nextCalled = true;
            return AbstractEventExecutor.this;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException("read-only");
      }
   }
}
