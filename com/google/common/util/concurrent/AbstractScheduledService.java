package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.ForwardingFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

@Beta
public abstract class AbstractScheduledService implements Service {
   private static final Logger logger = Logger.getLogger(AbstractScheduledService.class.getName());
   private final AbstractService delegate = new AbstractService() {
      private volatile Future runningTask;
      private volatile ScheduledExecutorService executorService;
      private final ReentrantLock lock = new ReentrantLock();
      private final Runnable task = new Runnable() {
         public void run() {
            lock.lock();

            try {
               AbstractScheduledService.this.runOneIteration();
            } catch (Throwable var8) {
               try {
                  AbstractScheduledService.this.shutDown();
               } catch (Exception var7) {
                  AbstractScheduledService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", var7);
               }

               notifyFailed(var8);
               throw Throwables.propagate(var8);
            } finally {
               lock.unlock();
            }

         }
      };

      protected final void doStart() {
         this.executorService = MoreExecutors.renamingDecorator(AbstractScheduledService.this.executor(), new Supplier() {
            public String get() {
               return AbstractScheduledService.this.serviceName() + " " + state();
            }
         });
         this.executorService.execute(new Runnable() {
            public void run() {
               lock.lock();

               try {
                  AbstractScheduledService.this.startUp();
                  runningTask = AbstractScheduledService.this.scheduler().schedule(AbstractScheduledService.this.delegate, executorService, task);
                  notifyStarted();
               } catch (Throwable var5) {
                  notifyFailed(var5);
                  throw Throwables.propagate(var5);
               } finally {
                  lock.unlock();
               }

            }
         });
      }

      protected final void doStop() {
         this.runningTask.cancel(false);
         this.executorService.execute(new Runnable() {
            public void run() {
               try {
                  lock.lock();

                  label10: {
                     try {
                        if(state() == Service.State.STOPPING) {
                           AbstractScheduledService.this.shutDown();
                           break label10;
                        }
                     } finally {
                        lock.unlock();
                     }

                     return;
                  }

                  notifyStopped();
               } catch (Throwable var5) {
                  notifyFailed(var5);
                  throw Throwables.propagate(var5);
               }
            }
         });
      }
   };

   protected abstract void runOneIteration() throws Exception;

   protected void startUp() throws Exception {
   }

   protected void shutDown() throws Exception {
   }

   protected abstract AbstractScheduledService.Scheduler scheduler();

   protected ScheduledExecutorService executor() {
      final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
         public Thread newThread(Runnable runnable) {
            return MoreExecutors.newThread(AbstractScheduledService.this.serviceName(), runnable);
         }
      });
      this.addListener(new Service.Listener() {
         public void terminated(Service.State from) {
            executor.shutdown();
         }

         public void failed(Service.State from, Throwable failure) {
            executor.shutdown();
         }
      }, MoreExecutors.sameThreadExecutor());
      return executor;
   }

   protected String serviceName() {
      return this.getClass().getSimpleName();
   }

   public String toString() {
      return this.serviceName() + " [" + this.state() + "]";
   }

   public final boolean isRunning() {
      return this.delegate.isRunning();
   }

   public final Service.State state() {
      return this.delegate.state();
   }

   public final void addListener(Service.Listener listener, Executor executor) {
      this.delegate.addListener(listener, executor);
   }

   public final Throwable failureCause() {
      return this.delegate.failureCause();
   }

   public final Service startAsync() {
      this.delegate.startAsync();
      return this;
   }

   public final Service stopAsync() {
      this.delegate.stopAsync();
      return this;
   }

   public final void awaitRunning() {
      this.delegate.awaitRunning();
   }

   public final void awaitRunning(long timeout, TimeUnit unit) throws TimeoutException {
      this.delegate.awaitRunning(timeout, unit);
   }

   public final void awaitTerminated() {
      this.delegate.awaitTerminated();
   }

   public final void awaitTerminated(long timeout, TimeUnit unit) throws TimeoutException {
      this.delegate.awaitTerminated(timeout, unit);
   }

   @Beta
   public abstract static class CustomScheduler extends AbstractScheduledService.Scheduler {
      public CustomScheduler() {
         super(null);
      }

      final Future schedule(AbstractService service, ScheduledExecutorService executor, Runnable runnable) {
         AbstractScheduledService.CustomScheduler.ReschedulableCallable task = new AbstractScheduledService.CustomScheduler.ReschedulableCallable(service, executor, runnable);
         task.reschedule();
         return task;
      }

      protected abstract AbstractScheduledService.CustomScheduler.Schedule getNextSchedule() throws Exception;

      private class ReschedulableCallable extends ForwardingFuture implements Callable {
         private final Runnable wrappedRunnable;
         private final ScheduledExecutorService executor;
         private final AbstractService service;
         private final ReentrantLock lock = new ReentrantLock();
         @GuardedBy("lock")
         private Future currentFuture;

         ReschedulableCallable(AbstractService service, ScheduledExecutorService executor, Runnable runnable) {
            this.wrappedRunnable = runnable;
            this.executor = executor;
            this.service = service;
         }

         public Void call() throws Exception {
            this.wrappedRunnable.run();
            this.reschedule();
            return null;
         }

         public void reschedule() {
            this.lock.lock();

            try {
               if(this.currentFuture == null || !this.currentFuture.isCancelled()) {
                  AbstractScheduledService.CustomScheduler.Schedule schedule = CustomScheduler.this.getNextSchedule();
                  this.currentFuture = this.executor.schedule(this, schedule.delay, schedule.unit);
               }
            } catch (Throwable var5) {
               this.service.notifyFailed(var5);
            } finally {
               this.lock.unlock();
            }

         }

         public boolean cancel(boolean mayInterruptIfRunning) {
            this.lock.lock();

            boolean var2;
            try {
               var2 = this.currentFuture.cancel(mayInterruptIfRunning);
            } finally {
               this.lock.unlock();
            }

            return var2;
         }

         protected Future delegate() {
            throw new UnsupportedOperationException("Only cancel is supported by this future");
         }
      }

      @Beta
      protected static final class Schedule {
         private final long delay;
         private final TimeUnit unit;

         public Schedule(long delay, TimeUnit unit) {
            this.delay = delay;
            this.unit = (TimeUnit)Preconditions.checkNotNull(unit);
         }
      }
   }

   public abstract static class Scheduler {
      public static AbstractScheduledService.Scheduler newFixedDelaySchedule(final long initialDelay, final long delay, final TimeUnit unit) {
         return new AbstractScheduledService.Scheduler(null) {
            public Future schedule(AbstractService service, ScheduledExecutorService executor, Runnable task) {
               return executor.scheduleWithFixedDelay(task, initialDelay, delay, unit);
            }
         };
      }

      public static AbstractScheduledService.Scheduler newFixedRateSchedule(final long initialDelay, final long period, final TimeUnit unit) {
         return new AbstractScheduledService.Scheduler(null) {
            public Future schedule(AbstractService service, ScheduledExecutorService executor, Runnable task) {
               return executor.scheduleAtFixedRate(task, initialDelay, period, unit);
            }
         };
      }

      abstract Future schedule(AbstractService var1, ScheduledExecutorService var2, Runnable var3);

      private Scheduler() {
      }
   }
}
