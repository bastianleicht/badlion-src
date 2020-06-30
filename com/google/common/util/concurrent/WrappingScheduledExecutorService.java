package com.google.common.util.concurrent;

import com.google.common.util.concurrent.WrappingExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

abstract class WrappingScheduledExecutorService extends WrappingExecutorService implements ScheduledExecutorService {
   final ScheduledExecutorService delegate;

   protected WrappingScheduledExecutorService(ScheduledExecutorService delegate) {
      super(delegate);
      this.delegate = delegate;
   }

   public final ScheduledFuture schedule(Runnable command, long delay, TimeUnit unit) {
      return this.delegate.schedule(this.wrapTask(command), delay, unit);
   }

   public final ScheduledFuture schedule(Callable task, long delay, TimeUnit unit) {
      return this.delegate.schedule(this.wrapTask(task), delay, unit);
   }

   public final ScheduledFuture scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      return this.delegate.scheduleAtFixedRate(this.wrapTask(command), initialDelay, period, unit);
   }

   public final ScheduledFuture scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      return this.delegate.scheduleWithFixedDelay(this.wrapTask(command), initialDelay, delay, unit);
   }
}
