package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.concurrent.ScheduledFutureTask;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GlobalEventExecutor extends AbstractEventExecutor {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(GlobalEventExecutor.class);
   private static final long SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos(1L);
   public static final GlobalEventExecutor INSTANCE = new GlobalEventExecutor();
   final BlockingQueue taskQueue = new LinkedBlockingQueue();
   final Queue delayedTaskQueue = new PriorityQueue();
   final ScheduledFutureTask purgeTask;
   private final ThreadFactory threadFactory;
   private final GlobalEventExecutor.TaskRunner taskRunner;
   private final AtomicBoolean started;
   volatile Thread thread;
   private final Future terminationFuture;

   private GlobalEventExecutor() {
      this.purgeTask = new ScheduledFutureTask(this, this.delayedTaskQueue, Executors.callable(new GlobalEventExecutor.PurgeTask(), (Object)null), ScheduledFutureTask.deadlineNanos(SCHEDULE_PURGE_INTERVAL), -SCHEDULE_PURGE_INTERVAL);
      this.threadFactory = new DefaultThreadFactory(this.getClass());
      this.taskRunner = new GlobalEventExecutor.TaskRunner();
      this.started = new AtomicBoolean();
      this.terminationFuture = new FailedFuture(this, new UnsupportedOperationException());
      this.delayedTaskQueue.add(this.purgeTask);
   }

   public EventExecutorGroup parent() {
      return null;
   }

   Runnable takeTask() {
      BlockingQueue<Runnable> taskQueue = this.taskQueue;

      Runnable task;
      while(true) {
         ScheduledFutureTask<?> delayedTask = (ScheduledFutureTask)this.delayedTaskQueue.peek();
         if(delayedTask == null) {
            Runnable task = null;

            try {
               task = (Runnable)taskQueue.take();
            } catch (InterruptedException var7) {
               ;
            }

            return task;
         }

         long delayNanos = delayedTask.delayNanos();
         if(delayNanos > 0L) {
            try {
               task = (Runnable)taskQueue.poll(delayNanos, TimeUnit.NANOSECONDS);
            } catch (InterruptedException var8) {
               return null;
            }
         } else {
            task = (Runnable)taskQueue.poll();
         }

         if(task == null) {
            this.fetchFromDelayedQueue();
            task = (Runnable)taskQueue.poll();
         }

         if(task != null) {
            break;
         }
      }

      return task;
   }

   private void fetchFromDelayedQueue() {
      long nanoTime = 0L;

      while(true) {
         ScheduledFutureTask<?> delayedTask = (ScheduledFutureTask)this.delayedTaskQueue.peek();
         if(delayedTask == null) {
            break;
         }

         if(nanoTime == 0L) {
            nanoTime = ScheduledFutureTask.nanoTime();
         }

         if(delayedTask.deadlineNanos() > nanoTime) {
            break;
         }

         this.delayedTaskQueue.remove();
         this.taskQueue.add(delayedTask);
      }

   }

   public int pendingTasks() {
      return this.taskQueue.size();
   }

   private void addTask(Runnable task) {
      if(task == null) {
         throw new NullPointerException("task");
      } else {
         this.taskQueue.add(task);
      }
   }

   public boolean inEventLoop(Thread thread) {
      return thread == this.thread;
   }

   public Future shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
      return this.terminationFuture();
   }

   public Future terminationFuture() {
      return this.terminationFuture;
   }

   /** @deprecated */
   @Deprecated
   public void shutdown() {
      throw new UnsupportedOperationException();
   }

   public boolean isShuttingDown() {
      return false;
   }

   public boolean isShutdown() {
      return false;
   }

   public boolean isTerminated() {
      return false;
   }

   public boolean awaitTermination(long timeout, TimeUnit unit) {
      return false;
   }

   public boolean awaitInactivity(long timeout, TimeUnit unit) throws InterruptedException {
      if(unit == null) {
         throw new NullPointerException("unit");
      } else {
         Thread thread = this.thread;
         if(thread == null) {
            throw new IllegalStateException("thread was not started");
         } else {
            thread.join(unit.toMillis(timeout));
            return !thread.isAlive();
         }
      }
   }

   public void execute(Runnable task) {
      if(task == null) {
         throw new NullPointerException("task");
      } else {
         this.addTask(task);
         if(!this.inEventLoop()) {
            this.startThread();
         }

      }
   }

   public ScheduledFuture schedule(Runnable command, long delay, TimeUnit unit) {
      if(command == null) {
         throw new NullPointerException("command");
      } else if(unit == null) {
         throw new NullPointerException("unit");
      } else if(delay < 0L) {
         throw new IllegalArgumentException(String.format("delay: %d (expected: >= 0)", new Object[]{Long.valueOf(delay)}));
      } else {
         return this.schedule(new ScheduledFutureTask(this, this.delayedTaskQueue, command, (Object)null, ScheduledFutureTask.deadlineNanos(unit.toNanos(delay))));
      }
   }

   public ScheduledFuture schedule(Callable callable, long delay, TimeUnit unit) {
      if(callable == null) {
         throw new NullPointerException("callable");
      } else if(unit == null) {
         throw new NullPointerException("unit");
      } else if(delay < 0L) {
         throw new IllegalArgumentException(String.format("delay: %d (expected: >= 0)", new Object[]{Long.valueOf(delay)}));
      } else {
         return this.schedule(new ScheduledFutureTask(this, this.delayedTaskQueue, callable, ScheduledFutureTask.deadlineNanos(unit.toNanos(delay))));
      }
   }

   public ScheduledFuture scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      if(command == null) {
         throw new NullPointerException("command");
      } else if(unit == null) {
         throw new NullPointerException("unit");
      } else if(initialDelay < 0L) {
         throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", new Object[]{Long.valueOf(initialDelay)}));
      } else if(period <= 0L) {
         throw new IllegalArgumentException(String.format("period: %d (expected: > 0)", new Object[]{Long.valueOf(period)}));
      } else {
         return this.schedule(new ScheduledFutureTask(this, this.delayedTaskQueue, Executors.callable(command, (Object)null), ScheduledFutureTask.deadlineNanos(unit.toNanos(initialDelay)), unit.toNanos(period)));
      }
   }

   public ScheduledFuture scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      if(command == null) {
         throw new NullPointerException("command");
      } else if(unit == null) {
         throw new NullPointerException("unit");
      } else if(initialDelay < 0L) {
         throw new IllegalArgumentException(String.format("initialDelay: %d (expected: >= 0)", new Object[]{Long.valueOf(initialDelay)}));
      } else if(delay <= 0L) {
         throw new IllegalArgumentException(String.format("delay: %d (expected: > 0)", new Object[]{Long.valueOf(delay)}));
      } else {
         return this.schedule(new ScheduledFutureTask(this, this.delayedTaskQueue, Executors.callable(command, (Object)null), ScheduledFutureTask.deadlineNanos(unit.toNanos(initialDelay)), -unit.toNanos(delay)));
      }
   }

   private ScheduledFuture schedule(final ScheduledFutureTask task) {
      if(task == null) {
         throw new NullPointerException("task");
      } else {
         if(this.inEventLoop()) {
            this.delayedTaskQueue.add(task);
         } else {
            this.execute(new Runnable() {
               public void run() {
                  GlobalEventExecutor.this.delayedTaskQueue.add(task);
               }
            });
         }

         return task;
      }
   }

   private void startThread() {
      if(this.started.compareAndSet(false, true)) {
         Thread t = this.threadFactory.newThread(this.taskRunner);
         t.start();
         this.thread = t;
      }

   }

   private final class PurgeTask implements Runnable {
      private PurgeTask() {
      }

      public void run() {
         Iterator<ScheduledFutureTask<?>> i = GlobalEventExecutor.this.delayedTaskQueue.iterator();

         while(i.hasNext()) {
            ScheduledFutureTask<?> task = (ScheduledFutureTask)i.next();
            if(task.isCancelled()) {
               i.remove();
            }
         }

      }
   }

   final class TaskRunner implements Runnable {
      public void run() {
         while(true) {
            Runnable task = GlobalEventExecutor.this.takeTask();
            if(task != null) {
               try {
                  task.run();
               } catch (Throwable var3) {
                  GlobalEventExecutor.logger.warn("Unexpected exception from the global event executor: ", var3);
               }

               if(task != GlobalEventExecutor.this.purgeTask) {
                  continue;
               }
            }

            if(GlobalEventExecutor.this.taskQueue.isEmpty() && GlobalEventExecutor.this.delayedTaskQueue.size() == 1) {
               boolean stopped = GlobalEventExecutor.this.started.compareAndSet(true, false);

               assert stopped;

               if(GlobalEventExecutor.this.taskQueue.isEmpty() && GlobalEventExecutor.this.delayedTaskQueue.size() == 1 || !GlobalEventExecutor.this.started.compareAndSet(false, true)) {
                  return;
               }
            }
         }
      }
   }
}
