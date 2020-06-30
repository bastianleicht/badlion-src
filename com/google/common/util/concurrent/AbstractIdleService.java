package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public abstract class AbstractIdleService implements Service {
   private final Supplier threadNameSupplier = new Supplier() {
      public String get() {
         return AbstractIdleService.this.serviceName() + " " + AbstractIdleService.this.state();
      }
   };
   private final Service delegate = new AbstractService() {
      protected final void doStart() {
         MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier).execute(new Runnable() {
            public void run() {
               try {
                  AbstractIdleService.this.startUp();
                  notifyStarted();
               } catch (Throwable var2) {
                  notifyFailed(var2);
                  throw Throwables.propagate(var2);
               }
            }
         });
      }

      protected final void doStop() {
         MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier).execute(new Runnable() {
            public void run() {
               try {
                  AbstractIdleService.this.shutDown();
                  notifyStopped();
               } catch (Throwable var2) {
                  notifyFailed(var2);
                  throw Throwables.propagate(var2);
               }
            }
         });
      }
   };

   protected abstract void startUp() throws Exception;

   protected abstract void shutDown() throws Exception;

   protected Executor executor() {
      return new Executor() {
         public void execute(Runnable command) {
            MoreExecutors.newThread((String)AbstractIdleService.this.threadNameSupplier.get(), command).start();
         }
      };
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

   protected String serviceName() {
      return this.getClass().getSimpleName();
   }
}
