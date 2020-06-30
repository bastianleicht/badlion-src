package io.netty.util.concurrent;

import io.netty.util.Signal;
import io.netty.util.concurrent.AbstractFuture;
import io.netty.util.concurrent.BlockingOperationException;
import io.netty.util.concurrent.DefaultFutureListeners;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GenericProgressiveFutureListener;
import io.netty.util.concurrent.ProgressiveFuture;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public class DefaultPromise extends AbstractFuture implements Promise {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultPromise.class);
   private static final InternalLogger rejectedExecutionLogger = InternalLoggerFactory.getInstance(DefaultPromise.class.getName() + ".rejectedExecution");
   private static final int MAX_LISTENER_STACK_DEPTH = 8;
   private static final Signal SUCCESS = Signal.valueOf(DefaultPromise.class.getName() + ".SUCCESS");
   private static final Signal UNCANCELLABLE = Signal.valueOf(DefaultPromise.class.getName() + ".UNCANCELLABLE");
   private static final DefaultPromise.CauseHolder CANCELLATION_CAUSE_HOLDER = new DefaultPromise.CauseHolder(new CancellationException());
   private final EventExecutor executor;
   private volatile Object result;
   private Object listeners;
   private DefaultPromise.LateListeners lateListeners;
   private short waiters;

   public DefaultPromise(EventExecutor executor) {
      if(executor == null) {
         throw new NullPointerException("executor");
      } else {
         this.executor = executor;
      }
   }

   protected DefaultPromise() {
      this.executor = null;
   }

   protected EventExecutor executor() {
      return this.executor;
   }

   public boolean isCancelled() {
      return isCancelled0(this.result);
   }

   private static boolean isCancelled0(Object result) {
      return result instanceof DefaultPromise.CauseHolder && ((DefaultPromise.CauseHolder)result).cause instanceof CancellationException;
   }

   public boolean isCancellable() {
      return this.result == null;
   }

   public boolean isDone() {
      return isDone0(this.result);
   }

   private static boolean isDone0(Object result) {
      return result != null && result != UNCANCELLABLE;
   }

   public boolean isSuccess() {
      Object result = this.result;
      return result != null && result != UNCANCELLABLE?!(result instanceof DefaultPromise.CauseHolder):false;
   }

   public Throwable cause() {
      Object result = this.result;
      return result instanceof DefaultPromise.CauseHolder?((DefaultPromise.CauseHolder)result).cause:null;
   }

   public Promise addListener(GenericFutureListener listener) {
      if(listener == null) {
         throw new NullPointerException("listener");
      } else if(this.isDone()) {
         this.notifyLateListener(listener);
         return this;
      } else {
         synchronized(this) {
            if(!this.isDone()) {
               if(this.listeners == null) {
                  this.listeners = listener;
               } else if(this.listeners instanceof DefaultFutureListeners) {
                  ((DefaultFutureListeners)this.listeners).add(listener);
               } else {
                  GenericFutureListener<? extends Future<V>> firstListener = (GenericFutureListener)this.listeners;
                  this.listeners = new DefaultFutureListeners(firstListener, listener);
               }

               return this;
            }
         }

         this.notifyLateListener(listener);
         return this;
      }
   }

   public Promise addListeners(GenericFutureListener... listeners) {
      if(listeners == null) {
         throw new NullPointerException("listeners");
      } else {
         for(GenericFutureListener<? extends Future<? super V>> l : listeners) {
            if(l == null) {
               break;
            }

            this.addListener(l);
         }

         return this;
      }
   }

   public Promise removeListener(GenericFutureListener listener) {
      if(listener == null) {
         throw new NullPointerException("listener");
      } else if(this.isDone()) {
         return this;
      } else {
         synchronized(this) {
            if(!this.isDone()) {
               if(this.listeners instanceof DefaultFutureListeners) {
                  ((DefaultFutureListeners)this.listeners).remove(listener);
               } else if(this.listeners == listener) {
                  this.listeners = null;
               }
            }

            return this;
         }
      }
   }

   public Promise removeListeners(GenericFutureListener... listeners) {
      if(listeners == null) {
         throw new NullPointerException("listeners");
      } else {
         for(GenericFutureListener<? extends Future<? super V>> l : listeners) {
            if(l == null) {
               break;
            }

            this.removeListener(l);
         }

         return this;
      }
   }

   public Promise sync() throws InterruptedException {
      this.await();
      this.rethrowIfFailed();
      return this;
   }

   public Promise syncUninterruptibly() {
      this.awaitUninterruptibly();
      this.rethrowIfFailed();
      return this;
   }

   private void rethrowIfFailed() {
      Throwable cause = this.cause();
      if(cause != null) {
         PlatformDependent.throwException(cause);
      }
   }

   public Promise await() throws InterruptedException {
      if(this.isDone()) {
         return this;
      } else if(Thread.interrupted()) {
         throw new InterruptedException(this.toString());
      } else {
         synchronized(this) {
            while(!this.isDone()) {
               this.checkDeadLock();
               this.incWaiters();

               try {
                  this.wait();
               } finally {
                  this.decWaiters();
               }
            }

            return this;
         }
      }
   }

   public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
      return this.await0(unit.toNanos(timeout), true);
   }

   public boolean await(long timeoutMillis) throws InterruptedException {
      return this.await0(TimeUnit.MILLISECONDS.toNanos(timeoutMillis), true);
   }

   public Promise awaitUninterruptibly() {
      if(this.isDone()) {
         return this;
      } else {
         boolean interrupted = false;
         synchronized(this) {
            while(!this.isDone()) {
               this.checkDeadLock();
               this.incWaiters();

               try {
                  this.wait();
               } catch (InterruptedException var9) {
                  interrupted = true;
               } finally {
                  this.decWaiters();
               }
            }
         }

         if(interrupted) {
            Thread.currentThread().interrupt();
         }

         return this;
      }
   }

   public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
      try {
         return this.await0(unit.toNanos(timeout), false);
      } catch (InterruptedException var5) {
         throw new InternalError();
      }
   }

   public boolean awaitUninterruptibly(long timeoutMillis) {
      try {
         return this.await0(TimeUnit.MILLISECONDS.toNanos(timeoutMillis), false);
      } catch (InterruptedException var4) {
         throw new InternalError();
      }
   }

   private boolean await0(long timeoutNanos, boolean interruptable) throws InterruptedException {
      if(this.isDone()) {
         return true;
      } else if(timeoutNanos <= 0L) {
         return this.isDone();
      } else if(interruptable && Thread.interrupted()) {
         throw new InterruptedException(this.toString());
      } else {
         long startTime = System.nanoTime();
         long waitTime = timeoutNanos;
         boolean interrupted = false;

         boolean e;
         try {
            synchronized(this) {
               if(!this.isDone()) {
                  if(waitTime <= 0L) {
                     e = this.isDone();
                     return e;
                  }

                  this.checkDeadLock();
                  this.incWaiters();

                  try {
                     while(true) {
                        try {
                           this.wait(waitTime / 1000000L, (int)(waitTime % 1000000L));
                        } catch (InterruptedException var22) {
                           e = var22;
                           if(interruptable) {
                              throw var22;
                           }

                           interrupted = true;
                        }

                        if(this.isDone()) {
                           e = true;
                           return (boolean)e;
                        }

                        waitTime = timeoutNanos - (System.nanoTime() - startTime);
                        if(waitTime <= 0L) {
                           break;
                        }
                     }

                     e = this.isDone();
                     return (boolean)e;
                  } finally {
                     this.decWaiters();
                  }
               }

               e = true;
            }
         } finally {
            if(interrupted) {
               Thread.currentThread().interrupt();
            }

         }

         return e;
      }
   }

   protected void checkDeadLock() {
      EventExecutor e = this.executor();
      if(e != null && e.inEventLoop()) {
         throw new BlockingOperationException(this.toString());
      }
   }

   public Promise setSuccess(Object result) {
      if(this.setSuccess0(result)) {
         this.notifyListeners();
         return this;
      } else {
         throw new IllegalStateException("complete already: " + this);
      }
   }

   public boolean trySuccess(Object result) {
      if(this.setSuccess0(result)) {
         this.notifyListeners();
         return true;
      } else {
         return false;
      }
   }

   public Promise setFailure(Throwable cause) {
      if(this.setFailure0(cause)) {
         this.notifyListeners();
         return this;
      } else {
         throw new IllegalStateException("complete already: " + this, cause);
      }
   }

   public boolean tryFailure(Throwable cause) {
      if(this.setFailure0(cause)) {
         this.notifyListeners();
         return true;
      } else {
         return false;
      }
   }

   public boolean cancel(boolean mayInterruptIfRunning) {
      Object result = this.result;
      if(!isDone0(result) && result != UNCANCELLABLE) {
         synchronized(this) {
            result = this.result;
            if(isDone0(result) || result == UNCANCELLABLE) {
               return false;
            }

            this.result = CANCELLATION_CAUSE_HOLDER;
            if(this.hasWaiters()) {
               this.notifyAll();
            }
         }

         this.notifyListeners();
         return true;
      } else {
         return false;
      }
   }

   public boolean setUncancellable() {
      Object result = this.result;
      if(isDone0(result)) {
         return !isCancelled0(result);
      } else {
         synchronized(this) {
            result = this.result;
            if(isDone0(result)) {
               return !isCancelled0(result);
            } else {
               this.result = UNCANCELLABLE;
               return true;
            }
         }
      }
   }

   private boolean setFailure0(Throwable cause) {
      if(cause == null) {
         throw new NullPointerException("cause");
      } else if(this.isDone()) {
         return false;
      } else {
         synchronized(this) {
            if(this.isDone()) {
               return false;
            } else {
               this.result = new DefaultPromise.CauseHolder(cause);
               if(this.hasWaiters()) {
                  this.notifyAll();
               }

               return true;
            }
         }
      }
   }

   private boolean setSuccess0(Object result) {
      if(this.isDone()) {
         return false;
      } else {
         synchronized(this) {
            if(this.isDone()) {
               return false;
            } else {
               if(result == null) {
                  this.result = SUCCESS;
               } else {
                  this.result = result;
               }

               if(this.hasWaiters()) {
                  this.notifyAll();
               }

               return true;
            }
         }
      }
   }

   public Object getNow() {
      Object result = this.result;
      return !(result instanceof DefaultPromise.CauseHolder) && result != SUCCESS?result:null;
   }

   private boolean hasWaiters() {
      return this.waiters > 0;
   }

   private void incWaiters() {
      if(this.waiters == 32767) {
         throw new IllegalStateException("too many waiters: " + this);
      } else {
         ++this.waiters;
      }
   }

   private void decWaiters() {
      --this.waiters;
   }

   private void notifyListeners() {
      Object listeners = this.listeners;
      if(listeners != null) {
         EventExecutor executor = this.executor();
         if(executor.inEventLoop()) {
            InternalThreadLocalMap threadLocals = InternalThreadLocalMap.get();
            int stackDepth = threadLocals.futureListenerStackDepth();
            if(stackDepth < 8) {
               threadLocals.setFutureListenerStackDepth(stackDepth + 1);

               try {
                  if(listeners instanceof DefaultFutureListeners) {
                     notifyListeners0(this, (DefaultFutureListeners)listeners);
                  } else {
                     GenericFutureListener<? extends Future<V>> l = (GenericFutureListener)listeners;
                     notifyListener0(this, l);
                  }
               } finally {
                  this.listeners = null;
                  threadLocals.setFutureListenerStackDepth(stackDepth);
               }

               return;
            }
         }

         if(listeners instanceof DefaultFutureListeners) {
            final DefaultFutureListeners dfl = (DefaultFutureListeners)listeners;
            execute(executor, new Runnable() {
               public void run() {
                  DefaultPromise.notifyListeners0(DefaultPromise.this, dfl);
                  DefaultPromise.this.listeners = null;
               }
            });
         } else {
            final GenericFutureListener<? extends Future<V>> l = (GenericFutureListener)listeners;
            execute(executor, new Runnable() {
               public void run() {
                  DefaultPromise.notifyListener0(DefaultPromise.this, l);
                  DefaultPromise.this.listeners = null;
               }
            });
         }

      }
   }

   private static void notifyListeners0(Future future, DefaultFutureListeners listeners) {
      GenericFutureListener<?>[] a = listeners.listeners();
      int size = listeners.size();

      for(int i = 0; i < size; ++i) {
         notifyListener0(future, a[i]);
      }

   }

   private void notifyLateListener(GenericFutureListener l) {
      EventExecutor executor = this.executor();
      if(executor.inEventLoop()) {
         if(this.listeners != null || this.lateListeners != null) {
            DefaultPromise<V>.LateListeners lateListeners = this.lateListeners;
            if(lateListeners == null) {
               this.lateListeners = lateListeners = new DefaultPromise.LateListeners();
            }

            lateListeners.add(l);
            execute(executor, lateListeners);
            return;
         }

         InternalThreadLocalMap threadLocals = InternalThreadLocalMap.get();
         int stackDepth = threadLocals.futureListenerStackDepth();
         if(stackDepth < 8) {
            threadLocals.setFutureListenerStackDepth(stackDepth + 1);

            try {
               notifyListener0(this, l);
            } finally {
               threadLocals.setFutureListenerStackDepth(stackDepth);
            }

            return;
         }
      }

      execute(executor, new DefaultPromise.LateListenerNotifier(l));
   }

   protected static void notifyListener(EventExecutor eventExecutor, final Future future, final GenericFutureListener l) {
      if(eventExecutor.inEventLoop()) {
         InternalThreadLocalMap threadLocals = InternalThreadLocalMap.get();
         int stackDepth = threadLocals.futureListenerStackDepth();
         if(stackDepth < 8) {
            threadLocals.setFutureListenerStackDepth(stackDepth + 1);

            try {
               notifyListener0(future, l);
            } finally {
               threadLocals.setFutureListenerStackDepth(stackDepth);
            }

            return;
         }
      }

      execute(eventExecutor, new Runnable() {
         public void run() {
            DefaultPromise.notifyListener0(future, l);
         }
      });
   }

   private static void execute(EventExecutor executor, Runnable task) {
      try {
         executor.execute(task);
      } catch (Throwable var3) {
         rejectedExecutionLogger.error("Failed to submit a listener notification task. Event loop shut down?", var3);
      }

   }

   static void notifyListener0(Future future, GenericFutureListener l) {
      try {
         l.operationComplete(future);
      } catch (Throwable var3) {
         if(logger.isWarnEnabled()) {
            logger.warn("An exception was thrown by " + l.getClass().getName() + ".operationComplete()", var3);
         }
      }

   }

   private synchronized Object progressiveListeners() {
      Object listeners = this.listeners;
      if(listeners == null) {
         return null;
      } else if(listeners instanceof DefaultFutureListeners) {
         DefaultFutureListeners dfl = (DefaultFutureListeners)listeners;
         int progressiveSize = dfl.progressiveSize();
         switch(progressiveSize) {
         case 0:
            return null;
         case 1:
            for(GenericFutureListener<?> l : dfl.listeners()) {
               if(l instanceof GenericProgressiveFutureListener) {
                  return l;
               }
            }

            return null;
         default:
            GenericFutureListener<?>[] array = dfl.listeners();
            GenericProgressiveFutureListener<?>[] copy = new GenericProgressiveFutureListener[progressiveSize];
            int i = 0;

            for(int j = 0; j < progressiveSize; ++i) {
               GenericFutureListener<?> l = array[i];
               if(l instanceof GenericProgressiveFutureListener) {
                  copy[j++] = (GenericProgressiveFutureListener)l;
               }
            }

            return copy;
         }
      } else {
         return listeners instanceof GenericProgressiveFutureListener?listeners:null;
      }
   }

   void notifyProgressiveListeners(final long progress, final long total) {
      Object listeners = this.progressiveListeners();
      if(listeners != null) {
         final ProgressiveFuture<V> self = (ProgressiveFuture)this;
         EventExecutor executor = this.executor();
         if(executor.inEventLoop()) {
            if(listeners instanceof GenericProgressiveFutureListener[]) {
               notifyProgressiveListeners0(self, (GenericProgressiveFutureListener[])((GenericProgressiveFutureListener[])listeners), progress, total);
            } else {
               notifyProgressiveListener0(self, (GenericProgressiveFutureListener)listeners, progress, total);
            }
         } else if(listeners instanceof GenericProgressiveFutureListener[]) {
            final GenericProgressiveFutureListener<?>[] array = (GenericProgressiveFutureListener[])((GenericProgressiveFutureListener[])listeners);
            execute(executor, new Runnable() {
               public void run() {
                  DefaultPromise.access$200(self, array, progress, total);
               }
            });
         } else {
            final GenericProgressiveFutureListener<ProgressiveFuture<V>> l = (GenericProgressiveFutureListener)listeners;
            execute(executor, new Runnable() {
               public void run() {
                  DefaultPromise.access$300(self, l, progress, total);
               }
            });
         }

      }
   }

   private static void notifyProgressiveListeners0(ProgressiveFuture future, GenericProgressiveFutureListener[] listeners, long progress, long total) {
      for(GenericProgressiveFutureListener<?> l : listeners) {
         if(l == null) {
            break;
         }

         notifyProgressiveListener0(future, l, progress, total);
      }

   }

   private static void notifyProgressiveListener0(ProgressiveFuture future, GenericProgressiveFutureListener l, long progress, long total) {
      try {
         l.operationProgressed(future, progress, total);
      } catch (Throwable var7) {
         if(logger.isWarnEnabled()) {
            logger.warn("An exception was thrown by " + l.getClass().getName() + ".operationProgressed()", var7);
         }
      }

   }

   public String toString() {
      return this.toStringBuilder().toString();
   }

   protected StringBuilder toStringBuilder() {
      StringBuilder buf = new StringBuilder(64);
      buf.append(StringUtil.simpleClassName((Object)this));
      buf.append('@');
      buf.append(Integer.toHexString(this.hashCode()));
      Object result = this.result;
      if(result == SUCCESS) {
         buf.append("(success)");
      } else if(result == UNCANCELLABLE) {
         buf.append("(uncancellable)");
      } else if(result instanceof DefaultPromise.CauseHolder) {
         buf.append("(failure(");
         buf.append(((DefaultPromise.CauseHolder)result).cause);
         buf.append(')');
      } else {
         buf.append("(incomplete)");
      }

      return buf;
   }

   // $FF: synthetic method
   static void access$200(ProgressiveFuture x0, GenericProgressiveFutureListener[] x1, long x2, long x3) {
      notifyProgressiveListeners0(x0, x1, x2, x3);
   }

   // $FF: synthetic method
   static void access$300(ProgressiveFuture x0, GenericProgressiveFutureListener x1, long x2, long x3) {
      notifyProgressiveListener0(x0, x1, x2, x3);
   }

   static {
      CANCELLATION_CAUSE_HOLDER.cause.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
   }

   private static final class CauseHolder {
      final Throwable cause;

      CauseHolder(Throwable cause) {
         this.cause = cause;
      }
   }

   private final class LateListenerNotifier implements Runnable {
      private GenericFutureListener l;

      LateListenerNotifier(GenericFutureListener l) {
         this.l = l;
      }

      public void run() {
         DefaultPromise<V>.LateListeners lateListeners = DefaultPromise.this.lateListeners;
         if(this.l != null) {
            if(lateListeners == null) {
               DefaultPromise.this.lateListeners = lateListeners = DefaultPromise.this.new LateListeners();
            }

            lateListeners.add(this.l);
            this.l = null;
         }

         lateListeners.run();
      }
   }

   private final class LateListeners extends ArrayDeque implements Runnable {
      private static final long serialVersionUID = -687137418080392244L;

      LateListeners() {
         super(2);
      }

      public void run() {
         if(DefaultPromise.this.listeners == null) {
            while(true) {
               GenericFutureListener<?> l = (GenericFutureListener)this.poll();
               if(l == null) {
                  break;
               }

               DefaultPromise.notifyListener0(DefaultPromise.this, l);
            }
         } else {
            DefaultPromise.execute(DefaultPromise.this.executor(), this);
         }

      }
   }
}
