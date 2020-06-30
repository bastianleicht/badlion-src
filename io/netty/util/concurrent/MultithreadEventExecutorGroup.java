package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MultithreadEventExecutorGroup extends AbstractEventExecutorGroup {
   private final EventExecutor[] children;
   private final AtomicInteger childIndex = new AtomicInteger();
   private final AtomicInteger terminatedChildren = new AtomicInteger();
   private final Promise terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
   private final MultithreadEventExecutorGroup.EventExecutorChooser chooser;

   protected MultithreadEventExecutorGroup(int nThreads, ThreadFactory threadFactory, Object... args) {
      if(nThreads <= 0) {
         throw new IllegalArgumentException(String.format("nThreads: %d (expected: > 0)", new Object[]{Integer.valueOf(nThreads)}));
      } else {
         if(threadFactory == null) {
            threadFactory = this.newDefaultThreadFactory();
         }

         this.children = new SingleThreadEventExecutor[nThreads];
         if(isPowerOfTwo(this.children.length)) {
            this.chooser = new MultithreadEventExecutorGroup.PowerOfTwoEventExecutorChooser();
         } else {
            this.chooser = new MultithreadEventExecutorGroup.GenericEventExecutorChooser();
         }

         for(int i = 0; i < nThreads; ++i) {
            boolean success = false;
            boolean var17 = false;

            try {
               var17 = true;
               this.children[i] = this.newChild(threadFactory, args);
               success = true;
               var17 = false;
            } catch (Exception var18) {
               throw new IllegalStateException("failed to create a child event loop", var18);
            } finally {
               if(var17) {
                  if(!success) {
                     for(int j = 0; j < i; ++j) {
                        this.children[j].shutdownGracefully();
                     }

                     for(int j = 0; j < i; ++j) {
                        EventExecutor e = this.children[j];

                        try {
                           while(!e.isTerminated()) {
                              e.awaitTermination(2147483647L, TimeUnit.SECONDS);
                           }
                        } catch (InterruptedException var19) {
                           Thread.currentThread().interrupt();
                           break;
                        }
                     }
                  }

               }
            }

            if(!success) {
               for(int j = 0; j < i; ++j) {
                  this.children[j].shutdownGracefully();
               }

               for(int j = 0; j < i; ++j) {
                  EventExecutor e = this.children[j];

                  try {
                     while(!e.isTerminated()) {
                        e.awaitTermination(2147483647L, TimeUnit.SECONDS);
                     }
                  } catch (InterruptedException var21) {
                     Thread.currentThread().interrupt();
                     break;
                  }
               }
            }
         }

         FutureListener<Object> terminationListener = new FutureListener() {
            public void operationComplete(Future future) throws Exception {
               if(MultithreadEventExecutorGroup.this.terminatedChildren.incrementAndGet() == MultithreadEventExecutorGroup.this.children.length) {
                  MultithreadEventExecutorGroup.this.terminationFuture.setSuccess((Object)null);
               }

            }
         };

         for(EventExecutor e : this.children) {
            e.terminationFuture().addListener(terminationListener);
         }

      }
   }

   protected ThreadFactory newDefaultThreadFactory() {
      return new DefaultThreadFactory(this.getClass());
   }

   public EventExecutor next() {
      return this.chooser.next();
   }

   public Iterator iterator() {
      return this.children().iterator();
   }

   public final int executorCount() {
      return this.children.length;
   }

   protected Set children() {
      Set<EventExecutor> children = Collections.newSetFromMap(new LinkedHashMap());
      Collections.addAll(children, this.children);
      return children;
   }

   protected abstract EventExecutor newChild(ThreadFactory var1, Object... var2) throws Exception;

   public Future shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
      for(EventExecutor l : this.children) {
         l.shutdownGracefully(quietPeriod, timeout, unit);
      }

      return this.terminationFuture();
   }

   public Future terminationFuture() {
      return this.terminationFuture;
   }

   /** @deprecated */
   @Deprecated
   public void shutdown() {
      for(EventExecutor l : this.children) {
         l.shutdown();
      }

   }

   public boolean isShuttingDown() {
      for(EventExecutor l : this.children) {
         if(!l.isShuttingDown()) {
            return false;
         }
      }

      return true;
   }

   public boolean isShutdown() {
      for(EventExecutor l : this.children) {
         if(!l.isShutdown()) {
            return false;
         }
      }

      return true;
   }

   public boolean isTerminated() {
      for(EventExecutor l : this.children) {
         if(!l.isTerminated()) {
            return false;
         }
      }

      return true;
   }

   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      long deadline = System.nanoTime() + unit.toNanos(timeout);

      for(EventExecutor l : this.children) {
         long timeLeft = deadline - System.nanoTime();
         if(timeLeft <= 0L) {
            return this.isTerminated();
         }

         if(l.awaitTermination(timeLeft, TimeUnit.NANOSECONDS)) {
            ;
         }
      }

      return this.isTerminated();
   }

   private static boolean isPowerOfTwo(int val) {
      return (val & -val) == val;
   }

   private interface EventExecutorChooser {
      EventExecutor next();
   }

   private final class GenericEventExecutorChooser implements MultithreadEventExecutorGroup.EventExecutorChooser {
      private GenericEventExecutorChooser() {
      }

      public EventExecutor next() {
         return MultithreadEventExecutorGroup.this.children[Math.abs(MultithreadEventExecutorGroup.this.childIndex.getAndIncrement() % MultithreadEventExecutorGroup.this.children.length)];
      }
   }

   private final class PowerOfTwoEventExecutorChooser implements MultithreadEventExecutorGroup.EventExecutorChooser {
      private PowerOfTwoEventExecutorChooser() {
      }

      public EventExecutor next() {
         return MultithreadEventExecutorGroup.this.children[MultithreadEventExecutorGroup.this.childIndex.getAndIncrement() & MultithreadEventExecutorGroup.this.children.length - 1];
      }
   }
}
