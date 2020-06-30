package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

final class ListenerCallQueue implements Runnable {
   private static final Logger logger = Logger.getLogger(ListenerCallQueue.class.getName());
   private final Object listener;
   private final Executor executor;
   @GuardedBy("this")
   private final Queue waitQueue = Queues.newArrayDeque();
   @GuardedBy("this")
   private boolean isThreadScheduled;

   ListenerCallQueue(Object listener, Executor executor) {
      this.listener = Preconditions.checkNotNull(listener);
      this.executor = (Executor)Preconditions.checkNotNull(executor);
   }

   synchronized void add(ListenerCallQueue.Callback callback) {
      this.waitQueue.add(callback);
   }

   void execute() {
      boolean scheduleTaskRunner = false;
      synchronized(this) {
         if(!this.isThreadScheduled) {
            this.isThreadScheduled = true;
            scheduleTaskRunner = true;
         }
      }

      if(scheduleTaskRunner) {
         try {
            this.executor.execute(this);
         } catch (RuntimeException var6) {
            synchronized(this) {
               this.isThreadScheduled = false;
            }

            logger.log(Level.SEVERE, "Exception while running callbacks for " + this.listener + " on " + this.executor, var6);
            throw var6;
         }
      }

   }

   public void run() {
      boolean stillRunning = true;

      while(true) {
         boolean var14 = false;

         try {
            var14 = true;
            ListenerCallQueue.Callback<L> nextToRun;
            synchronized(this) {
               Preconditions.checkState(this.isThreadScheduled);
               nextToRun = (ListenerCallQueue.Callback)this.waitQueue.poll();
               if(nextToRun == null) {
                  this.isThreadScheduled = false;
                  stillRunning = false;
                  var14 = false;
                  break;
               }
            }

            try {
               nextToRun.call(this.listener);
            } catch (RuntimeException var17) {
               logger.log(Level.SEVERE, "Exception while executing callback: " + this.listener + "." + nextToRun.methodCall, var17);
            }
         } finally {
            if(var14) {
               if(stillRunning) {
                  synchronized(this) {
                     this.isThreadScheduled = false;
                  }
               }

            }
         }
      }

      if(stillRunning) {
         synchronized(this) {
            this.isThreadScheduled = false;
         }
      }

   }

   abstract static class Callback {
      private final String methodCall;

      Callback(String methodCall) {
         this.methodCall = methodCall;
      }

      abstract void call(Object var1);

      void enqueueOn(Iterable queues) {
         for(ListenerCallQueue<L> queue : queues) {
            queue.add(this);
         }

      }
   }
}
