package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public final class Uninterruptibles {
   public static void awaitUninterruptibly(CountDownLatch latch) {
      boolean interrupted = false;

      try {
         while(true) {
            try {
               latch.await();
               return;
            } catch (InterruptedException var6) {
               interrupted = true;
            }
         }
      } finally {
         if(interrupted) {
            Thread.currentThread().interrupt();
         }

      }
   }

   public static boolean awaitUninterruptibly(CountDownLatch latch, long timeout, TimeUnit unit) {
      boolean interrupted = false;

      try {
         long remainingNanos = unit.toNanos(timeout);
         long end = System.nanoTime() + remainingNanos;

         while(true) {
            try {
               boolean e = latch.await(remainingNanos, TimeUnit.NANOSECONDS);
               return e;
            } catch (InterruptedException var13) {
               interrupted = true;
               remainingNanos = end - System.nanoTime();
            }
         }
      } finally {
         if(interrupted) {
            Thread.currentThread().interrupt();
         }

      }
   }

   public static void joinUninterruptibly(Thread toJoin) {
      boolean interrupted = false;

      try {
         while(true) {
            try {
               toJoin.join();
               return;
            } catch (InterruptedException var6) {
               interrupted = true;
            }
         }
      } finally {
         if(interrupted) {
            Thread.currentThread().interrupt();
         }

      }
   }

   public static Object getUninterruptibly(Future future) throws ExecutionException {
      boolean interrupted = false;

      try {
         while(true) {
            try {
               Object e = future.get();
               return e;
            } catch (InterruptedException var6) {
               interrupted = true;
            }
         }
      } finally {
         if(interrupted) {
            Thread.currentThread().interrupt();
         }

      }
   }

   public static Object getUninterruptibly(Future future, long timeout, TimeUnit unit) throws ExecutionException, TimeoutException {
      boolean interrupted = false;

      try {
         long remainingNanos = unit.toNanos(timeout);
         long end = System.nanoTime() + remainingNanos;

         while(true) {
            try {
               Object e = future.get(remainingNanos, TimeUnit.NANOSECONDS);
               return e;
            } catch (InterruptedException var13) {
               interrupted = true;
               remainingNanos = end - System.nanoTime();
            }
         }
      } finally {
         if(interrupted) {
            Thread.currentThread().interrupt();
         }

      }
   }

   public static void joinUninterruptibly(Thread toJoin, long timeout, TimeUnit unit) {
      Preconditions.checkNotNull(toJoin);
      boolean interrupted = false;

      try {
         long remainingNanos = unit.toNanos(timeout);
         long end = System.nanoTime() + remainingNanos;

         while(true) {
            try {
               TimeUnit.NANOSECONDS.timedJoin(toJoin, remainingNanos);
               return;
            } catch (InterruptedException var13) {
               interrupted = true;
               remainingNanos = end - System.nanoTime();
            }
         }
      } finally {
         if(interrupted) {
            Thread.currentThread().interrupt();
         }

      }
   }

   public static Object takeUninterruptibly(BlockingQueue queue) {
      boolean interrupted = false;

      try {
         while(true) {
            try {
               Object e = queue.take();
               return e;
            } catch (InterruptedException var6) {
               interrupted = true;
            }
         }
      } finally {
         if(interrupted) {
            Thread.currentThread().interrupt();
         }

      }
   }

   public static void putUninterruptibly(BlockingQueue queue, Object element) {
      boolean interrupted = false;

      try {
         while(true) {
            try {
               queue.put(element);
               return;
            } catch (InterruptedException var7) {
               interrupted = true;
            }
         }
      } finally {
         if(interrupted) {
            Thread.currentThread().interrupt();
         }

      }
   }

   public static void sleepUninterruptibly(long sleepFor, TimeUnit unit) {
      boolean interrupted = false;

      try {
         long remainingNanos = unit.toNanos(sleepFor);
         long end = System.nanoTime() + remainingNanos;

         while(true) {
            try {
               TimeUnit.NANOSECONDS.sleep(remainingNanos);
               return;
            } catch (InterruptedException var12) {
               interrupted = true;
               remainingNanos = end - System.nanoTime();
            }
         }
      } finally {
         if(interrupted) {
            Thread.currentThread().interrupt();
         }

      }
   }
}
