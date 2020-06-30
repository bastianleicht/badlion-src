package io.netty.util.concurrent;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.PromiseTask;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

final class ScheduledFutureTask extends PromiseTask implements ScheduledFuture {
   private static final AtomicLong nextTaskId = new AtomicLong();
   private static final long START_TIME = System.nanoTime();
   private final long id;
   private final Queue delayedTaskQueue;
   private long deadlineNanos;
   private final long periodNanos;

   static long nanoTime() {
      return System.nanoTime() - START_TIME;
   }

   static long deadlineNanos(long delay) {
      return nanoTime() + delay;
   }

   ScheduledFutureTask(EventExecutor executor, Queue delayedTaskQueue, Runnable runnable, Object result, long nanoTime) {
      this(executor, delayedTaskQueue, toCallable(runnable, result), nanoTime);
   }

   ScheduledFutureTask(EventExecutor executor, Queue delayedTaskQueue, Callable callable, long nanoTime, long period) {
      super(executor, callable);
      this.id = nextTaskId.getAndIncrement();
      if(period == 0L) {
         throw new IllegalArgumentException("period: 0 (expected: != 0)");
      } else {
         this.delayedTaskQueue = delayedTaskQueue;
         this.deadlineNanos = nanoTime;
         this.periodNanos = period;
      }
   }

   ScheduledFutureTask(EventExecutor executor, Queue delayedTaskQueue, Callable callable, long nanoTime) {
      super(executor, callable);
      this.id = nextTaskId.getAndIncrement();
      this.delayedTaskQueue = delayedTaskQueue;
      this.deadlineNanos = nanoTime;
      this.periodNanos = 0L;
   }

   protected EventExecutor executor() {
      return super.executor();
   }

   public long deadlineNanos() {
      return this.deadlineNanos;
   }

   public long delayNanos() {
      return Math.max(0L, this.deadlineNanos() - nanoTime());
   }

   public long delayNanos(long currentTimeNanos) {
      return Math.max(0L, this.deadlineNanos() - (currentTimeNanos - START_TIME));
   }

   public long getDelay(TimeUnit unit) {
      return unit.convert(this.delayNanos(), TimeUnit.NANOSECONDS);
   }

   public int compareTo(Delayed o) {
      if(this == o) {
         return 0;
      } else {
         ScheduledFutureTask<?> that = (ScheduledFutureTask)o;
         long d = this.deadlineNanos() - that.deadlineNanos();
         if(d < 0L) {
            return -1;
         } else if(d > 0L) {
            return 1;
         } else if(this.id < that.id) {
            return -1;
         } else if(this.id == that.id) {
            throw new Error();
         } else {
            return 1;
         }
      }
   }

   public void run() {
      assert this.executor().inEventLoop();

      try {
         if(this.periodNanos == 0L) {
            if(this.setUncancellableInternal()) {
               V result = this.task.call();
               this.setSuccessInternal(result);
            }
         } else if(!this.isCancelled()) {
            this.task.call();
            if(!this.executor().isShutdown()) {
               long p = this.periodNanos;
               if(p > 0L) {
                  this.deadlineNanos += p;
               } else {
                  this.deadlineNanos = nanoTime() - p;
               }

               if(!this.isCancelled()) {
                  this.delayedTaskQueue.add(this);
               }
            }
         }
      } catch (Throwable var3) {
         this.setFailureInternal(var3);
      }

   }

   protected StringBuilder toStringBuilder() {
      StringBuilder buf = super.toStringBuilder();
      buf.setCharAt(buf.length() - 1, ',');
      buf.append(" id: ");
      buf.append(this.id);
      buf.append(", deadline: ");
      buf.append(this.deadlineNanos);
      buf.append(", period: ");
      buf.append(this.periodNanos);
      buf.append(')');
      return buf;
   }
}
