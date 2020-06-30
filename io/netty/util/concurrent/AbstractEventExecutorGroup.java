package io.netty.util.concurrent;

import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractEventExecutorGroup implements EventExecutorGroup {
   public Future submit(Runnable task) {
      return this.next().submit(task);
   }

   public Future submit(Runnable task, Object result) {
      return this.next().submit(task, result);
   }

   public Future submit(Callable task) {
      return this.next().submit(task);
   }

   public ScheduledFuture schedule(Runnable command, long delay, TimeUnit unit) {
      return this.next().schedule(command, delay, unit);
   }

   public ScheduledFuture schedule(Callable callable, long delay, TimeUnit unit) {
      return this.next().schedule(callable, delay, unit);
   }

   public ScheduledFuture scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      return this.next().scheduleAtFixedRate(command, initialDelay, period, unit);
   }

   public ScheduledFuture scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      return this.next().scheduleWithFixedDelay(command, initialDelay, delay, unit);
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

   public List invokeAll(Collection tasks) throws InterruptedException {
      return this.next().invokeAll(tasks);
   }

   public List invokeAll(Collection tasks, long timeout, TimeUnit unit) throws InterruptedException {
      return this.next().invokeAll(tasks, timeout, unit);
   }

   public Object invokeAny(Collection tasks) throws InterruptedException, ExecutionException {
      return this.next().invokeAny(tasks);
   }

   public Object invokeAny(Collection tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return this.next().invokeAny(tasks, timeout, unit);
   }

   public void execute(Runnable command) {
      this.next().execute(command);
   }
}
