package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.concurrent.GuardedBy;

@Beta
public final class Monitor {
   private final boolean fair;
   private final ReentrantLock lock;
   @GuardedBy("lock")
   private Monitor.Guard activeGuards;

   public Monitor() {
      this(false);
   }

   public Monitor(boolean fair) {
      this.activeGuards = null;
      this.fair = fair;
      this.lock = new ReentrantLock(fair);
   }

   public void enter() {
      this.lock.lock();
   }

   public void enterInterruptibly() throws InterruptedException {
      this.lock.lockInterruptibly();
   }

   public boolean enter(long time, TimeUnit unit) {
      long timeoutNanos = unit.toNanos(time);
      ReentrantLock lock = this.lock;
      if(!this.fair && lock.tryLock()) {
         return true;
      } else {
         long deadline = System.nanoTime() + timeoutNanos;
         boolean interrupted = Thread.interrupted();

         try {
            while(true) {
               try {
                  boolean interrupt = lock.tryLock(timeoutNanos, TimeUnit.NANOSECONDS);
                  return interrupt;
               } catch (InterruptedException var14) {
                  interrupted = true;
                  timeoutNanos = deadline - System.nanoTime();
               }
            }
         } finally {
            if(interrupted) {
               Thread.currentThread().interrupt();
            }

         }
      }
   }

   public boolean enterInterruptibly(long time, TimeUnit unit) throws InterruptedException {
      return this.lock.tryLock(time, unit);
   }

   public boolean tryEnter() {
      return this.lock.tryLock();
   }

   public void enterWhen(Monitor.Guard guard) throws InterruptedException {
      if(guard.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock lock = this.lock;
         boolean signalBeforeWaiting = lock.isHeldByCurrentThread();
         lock.lockInterruptibly();
         boolean satisfied = false;

         try {
            if(!guard.isSatisfied()) {
               this.await(guard, signalBeforeWaiting);
            }

            satisfied = true;
         } finally {
            if(!satisfied) {
               this.leave();
            }

         }

      }
   }

   public void enterWhenUninterruptibly(Monitor.Guard guard) {
      if(guard.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock lock = this.lock;
         boolean signalBeforeWaiting = lock.isHeldByCurrentThread();
         lock.lock();
         boolean satisfied = false;

         try {
            if(!guard.isSatisfied()) {
               this.awaitUninterruptibly(guard, signalBeforeWaiting);
            }

            satisfied = true;
         } finally {
            if(!satisfied) {
               this.leave();
            }

         }

      }
   }

   public boolean enterWhen(Monitor.Guard guard, long time, TimeUnit unit) throws InterruptedException {
      long timeoutNanos = unit.toNanos(time);
      if(guard.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock lock = this.lock;
         boolean reentrant = lock.isHeldByCurrentThread();
         if(this.fair || !lock.tryLock()) {
            long deadline = System.nanoTime() + timeoutNanos;
            if(!lock.tryLock(time, unit)) {
               return false;
            }

            timeoutNanos = deadline - System.nanoTime();
         }

         boolean satisfied = false;
         boolean threw = true;

         boolean var11;
         try {
            satisfied = guard.isSatisfied() || this.awaitNanos(guard, timeoutNanos, reentrant);
            threw = false;
            var11 = satisfied;
         } finally {
            if(!satisfied) {
               try {
                  if(threw && !reentrant) {
                     this.signalNextWaiter();
                  }
               } finally {
                  lock.unlock();
               }
            }

         }

         return var11;
      }
   }

   public boolean enterWhenUninterruptibly(Monitor.Guard guard, long time, TimeUnit unit) {
      long timeoutNanos = unit.toNanos(time);
      if(guard.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock lock = this.lock;
         long deadline = System.nanoTime() + timeoutNanos;
         boolean signalBeforeWaiting = lock.isHeldByCurrentThread();
         boolean interrupted = Thread.interrupted();

         try {
            InterruptedException interrupt;
            if(this.fair || !lock.tryLock()) {
               boolean locked = false;

               while(true) {
                  try {
                     locked = lock.tryLock(timeoutNanos, TimeUnit.NANOSECONDS);
                     if(!locked) {
                        boolean interrupt = false;
                        return interrupt;
                     }
                  } catch (InterruptedException var24) {
                     interrupt = var24;
                     interrupted = true;
                  }

                  timeoutNanos = deadline - System.nanoTime();
                  if(locked) {
                     break;
                  }
               }
            }

            boolean satisfied = false;

            try {
               while(true) {
                  try {
                     interrupt = satisfied = guard.isSatisfied() || this.awaitNanos(guard, timeoutNanos, signalBeforeWaiting);
                     return (boolean)interrupt;
                  } catch (InterruptedException var25) {
                     interrupted = true;
                     signalBeforeWaiting = false;
                     timeoutNanos = deadline - System.nanoTime();
                  }
               }
            } finally {
               if(!satisfied) {
                  lock.unlock();
               }

            }
         } finally {
            if(interrupted) {
               Thread.currentThread().interrupt();
            }

         }
      }
   }

   public boolean enterIf(Monitor.Guard guard) {
      if(guard.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock lock = this.lock;
         lock.lock();
         boolean satisfied = false;

         boolean var4;
         try {
            var4 = satisfied = guard.isSatisfied();
         } finally {
            if(!satisfied) {
               lock.unlock();
            }

         }

         return var4;
      }
   }

   public boolean enterIfInterruptibly(Monitor.Guard guard) throws InterruptedException {
      if(guard.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock lock = this.lock;
         lock.lockInterruptibly();
         boolean satisfied = false;

         boolean var4;
         try {
            var4 = satisfied = guard.isSatisfied();
         } finally {
            if(!satisfied) {
               lock.unlock();
            }

         }

         return var4;
      }
   }

   public boolean enterIf(Monitor.Guard guard, long time, TimeUnit unit) {
      if(guard.monitor != this) {
         throw new IllegalMonitorStateException();
      } else if(!this.enter(time, unit)) {
         return false;
      } else {
         boolean satisfied = false;

         boolean var6;
         try {
            var6 = satisfied = guard.isSatisfied();
         } finally {
            if(!satisfied) {
               this.lock.unlock();
            }

         }

         return var6;
      }
   }

   public boolean enterIfInterruptibly(Monitor.Guard guard, long time, TimeUnit unit) throws InterruptedException {
      if(guard.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock lock = this.lock;
         if(!lock.tryLock(time, unit)) {
            return false;
         } else {
            boolean satisfied = false;

            boolean var7;
            try {
               var7 = satisfied = guard.isSatisfied();
            } finally {
               if(!satisfied) {
                  lock.unlock();
               }

            }

            return var7;
         }
      }
   }

   public boolean tryEnterIf(Monitor.Guard guard) {
      if(guard.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         ReentrantLock lock = this.lock;
         if(!lock.tryLock()) {
            return false;
         } else {
            boolean satisfied = false;

            boolean var4;
            try {
               var4 = satisfied = guard.isSatisfied();
            } finally {
               if(!satisfied) {
                  lock.unlock();
               }

            }

            return var4;
         }
      }
   }

   public void waitFor(Monitor.Guard guard) throws InterruptedException {
      if(!(guard.monitor == this & this.lock.isHeldByCurrentThread())) {
         throw new IllegalMonitorStateException();
      } else {
         if(!guard.isSatisfied()) {
            this.await(guard, true);
         }

      }
   }

   public void waitForUninterruptibly(Monitor.Guard guard) {
      if(!(guard.monitor == this & this.lock.isHeldByCurrentThread())) {
         throw new IllegalMonitorStateException();
      } else {
         if(!guard.isSatisfied()) {
            this.awaitUninterruptibly(guard, true);
         }

      }
   }

   public boolean waitFor(Monitor.Guard guard, long time, TimeUnit unit) throws InterruptedException {
      long timeoutNanos = unit.toNanos(time);
      if(!(guard.monitor == this & this.lock.isHeldByCurrentThread())) {
         throw new IllegalMonitorStateException();
      } else {
         return guard.isSatisfied() || this.awaitNanos(guard, timeoutNanos, true);
      }
   }

   public boolean waitForUninterruptibly(Monitor.Guard guard, long time, TimeUnit unit) {
      long timeoutNanos = unit.toNanos(time);
      if(!(guard.monitor == this & this.lock.isHeldByCurrentThread())) {
         throw new IllegalMonitorStateException();
      } else if(guard.isSatisfied()) {
         return true;
      } else {
         boolean signalBeforeWaiting = true;
         long deadline = System.nanoTime() + timeoutNanos;
         boolean interrupted = Thread.interrupted();

         try {
            while(true) {
               try {
                  boolean interrupt = this.awaitNanos(guard, timeoutNanos, signalBeforeWaiting);
                  return interrupt;
               } catch (InterruptedException var16) {
                  interrupted = true;
                  if(!guard.isSatisfied()) {
                     signalBeforeWaiting = false;
                     timeoutNanos = deadline - System.nanoTime();
                  } else {
                     boolean var12 = true;
                     return var12;
                  }
               }
            }
         } finally {
            if(interrupted) {
               Thread.currentThread().interrupt();
            }

         }
      }
   }

   public void leave() {
      ReentrantLock lock = this.lock;

      try {
         if(lock.getHoldCount() == 1) {
            this.signalNextWaiter();
         }
      } finally {
         lock.unlock();
      }

   }

   public boolean isFair() {
      return this.fair;
   }

   public boolean isOccupied() {
      return this.lock.isLocked();
   }

   public boolean isOccupiedByCurrentThread() {
      return this.lock.isHeldByCurrentThread();
   }

   public int getOccupiedDepth() {
      return this.lock.getHoldCount();
   }

   public int getQueueLength() {
      return this.lock.getQueueLength();
   }

   public boolean hasQueuedThreads() {
      return this.lock.hasQueuedThreads();
   }

   public boolean hasQueuedThread(Thread thread) {
      return this.lock.hasQueuedThread(thread);
   }

   public boolean hasWaiters(Monitor.Guard guard) {
      return this.getWaitQueueLength(guard) > 0;
   }

   public int getWaitQueueLength(Monitor.Guard guard) {
      if(guard.monitor != this) {
         throw new IllegalMonitorStateException();
      } else {
         this.lock.lock();

         int var2;
         try {
            var2 = guard.waiterCount;
         } finally {
            this.lock.unlock();
         }

         return var2;
      }
   }

   @GuardedBy("lock")
   private void signalNextWaiter() {
      for(Monitor.Guard guard = this.activeGuards; guard != null; guard = guard.next) {
         if(this.isSatisfied(guard)) {
            guard.condition.signal();
            break;
         }
      }

   }

   @GuardedBy("lock")
   private boolean isSatisfied(Monitor.Guard guard) {
      try {
         return guard.isSatisfied();
      } catch (Throwable var3) {
         this.signalAllWaiters();
         throw Throwables.propagate(var3);
      }
   }

   @GuardedBy("lock")
   private void signalAllWaiters() {
      for(Monitor.Guard guard = this.activeGuards; guard != null; guard = guard.next) {
         guard.condition.signalAll();
      }

   }

   @GuardedBy("lock")
   private void beginWaitingFor(Monitor.Guard guard) {
      int waiters = guard.waiterCount++;
      if(waiters == 0) {
         guard.next = this.activeGuards;
         this.activeGuards = guard;
      }

   }

   @GuardedBy("lock")
   private void endWaitingFor(Monitor.Guard guard) {
      int waiters = --guard.waiterCount;
      if(waiters == 0) {
         Monitor.Guard p = this.activeGuards;

         Monitor.Guard pred;
         for(pred = null; p != guard; p = p.next) {
            pred = p;
         }

         if(pred == null) {
            this.activeGuards = p.next;
         } else {
            pred.next = p.next;
         }

         p.next = null;
      }

   }

   @GuardedBy("lock")
   private void await(Monitor.Guard guard, boolean signalBeforeWaiting) throws InterruptedException {
      if(signalBeforeWaiting) {
         this.signalNextWaiter();
      }

      this.beginWaitingFor(guard);

      try {
         while(true) {
            guard.condition.await();
            if(guard.isSatisfied()) {
               break;
            }
         }

      } finally {
         this.endWaitingFor(guard);
      }
   }

   @GuardedBy("lock")
   private void awaitUninterruptibly(Monitor.Guard guard, boolean signalBeforeWaiting) {
      if(signalBeforeWaiting) {
         this.signalNextWaiter();
      }

      this.beginWaitingFor(guard);

      try {
         while(true) {
            guard.condition.awaitUninterruptibly();
            if(guard.isSatisfied()) {
               break;
            }
         }

      } finally {
         this.endWaitingFor(guard);
      }
   }

   @GuardedBy("lock")
   private boolean awaitNanos(Monitor.Guard guard, long nanos, boolean signalBeforeWaiting) throws InterruptedException {
      if(signalBeforeWaiting) {
         this.signalNextWaiter();
      }

      this.beginWaitingFor(guard);

      boolean var9;
      try {
         while(nanos >= 0L) {
            nanos = guard.condition.awaitNanos(nanos);
            if(guard.isSatisfied()) {
               var9 = true;
               return var9;
            }
         }

         var9 = false;
      } finally {
         this.endWaitingFor(guard);
      }

      return var9;
   }

   @Beta
   public abstract static class Guard {
      final Monitor monitor;
      final Condition condition;
      @GuardedBy("monitor.lock")
      int waiterCount = 0;
      @GuardedBy("monitor.lock")
      Monitor.Guard next;

      protected Guard(Monitor monitor) {
         this.monitor = (Monitor)Preconditions.checkNotNull(monitor, "monitor");
         this.condition = monitor.lock.newCondition();
      }

      public abstract boolean isSatisfied();
   }
}
