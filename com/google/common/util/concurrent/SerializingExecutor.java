package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.concurrent.GuardedBy;

final class SerializingExecutor implements Executor {
   private static final Logger log = Logger.getLogger(SerializingExecutor.class.getName());
   private final Executor executor;
   @GuardedBy("internalLock")
   private final Queue waitQueue = new ArrayDeque();
   @GuardedBy("internalLock")
   private boolean isThreadScheduled = false;
   private final SerializingExecutor.TaskRunner taskRunner = new SerializingExecutor.TaskRunner();
   private final Object internalLock = new Object() {
      public String toString() {
         return "SerializingExecutor lock: " + super.toString();
      }
   };

   public SerializingExecutor(Executor executor) {
      Preconditions.checkNotNull(executor, "\'executor\' must not be null.");
      this.executor = executor;
   }

   public void execute(Runnable r) {
      Preconditions.checkNotNull(r, "\'r\' must not be null.");
      boolean scheduleTaskRunner = false;
      synchronized(this.internalLock) {
         this.waitQueue.add(r);
         if(!this.isThreadScheduled) {
            this.isThreadScheduled = true;
            scheduleTaskRunner = true;
         }
      }

      if(scheduleTaskRunner) {
         boolean threw = true;
         boolean var13 = false;

         try {
            var13 = true;
            this.executor.execute(this.taskRunner);
            threw = false;
            var13 = false;
         } finally {
            if(var13) {
               if(threw) {
                  synchronized(this.internalLock) {
                     this.isThreadScheduled = false;
                  }
               }

            }
         }

         if(threw) {
            synchronized(this.internalLock) {
               this.isThreadScheduled = false;
            }
         }
      }

   }

   private class TaskRunner implements Runnable {
      private TaskRunner() {
      }

      public void run() {
         boolean stillRunning = true;

         while(true) {
            boolean var14 = false;

            try {
               var14 = true;
               Preconditions.checkState(SerializingExecutor.this.isThreadScheduled);
               Runnable nextToRun;
               synchronized(SerializingExecutor.this.internalLock) {
                  nextToRun = (Runnable)SerializingExecutor.this.waitQueue.poll();
                  if(nextToRun == null) {
                     SerializingExecutor.this.isThreadScheduled = false;
                     stillRunning = false;
                     var14 = false;
                     break;
                  }
               }

               try {
                  nextToRun.run();
               } catch (RuntimeException var17) {
                  SerializingExecutor.log.log(Level.SEVERE, "Exception while executing runnable " + nextToRun, var17);
               }
            } finally {
               if(var14) {
                  if(stillRunning) {
                     synchronized(SerializingExecutor.this.internalLock) {
                        SerializingExecutor.this.isThreadScheduled = false;
                     }
                  }

               }
            }
         }

         if(stillRunning) {
            synchronized(SerializingExecutor.this.internalLock) {
               SerializingExecutor.this.isThreadScheduled = false;
            }
         }

      }
   }
}
