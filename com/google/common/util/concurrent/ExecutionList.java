package com.google.common.util.concurrent;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

public final class ExecutionList {
   @VisibleForTesting
   static final Logger log = Logger.getLogger(ExecutionList.class.getName());
   @GuardedBy("this")
   private ExecutionList.RunnableExecutorPair runnables;
   @GuardedBy("this")
   private boolean executed;

   public void add(Runnable runnable, Executor executor) {
      Preconditions.checkNotNull(runnable, "Runnable was null.");
      Preconditions.checkNotNull(executor, "Executor was null.");
      synchronized(this) {
         if(!this.executed) {
            this.runnables = new ExecutionList.RunnableExecutorPair(runnable, executor, this.runnables);
            return;
         }
      }

      executeListener(runnable, executor);
   }

   public void execute() {
      ExecutionList.RunnableExecutorPair list;
      synchronized(this) {
         if(this.executed) {
            return;
         }

         this.executed = true;
         list = this.runnables;
         this.runnables = null;
      }

      ExecutionList.RunnableExecutorPair reversedList;
      ExecutionList.RunnableExecutorPair tmp;
      for(reversedList = null; list != null; reversedList = tmp) {
         tmp = list;
         list = list.next;
         tmp.next = reversedList;
      }

      while(reversedList != null) {
         executeListener(reversedList.runnable, reversedList.executor);
         reversedList = reversedList.next;
      }

   }

   private static void executeListener(Runnable runnable, Executor executor) {
      try {
         executor.execute(runnable);
      } catch (RuntimeException var3) {
         log.log(Level.SEVERE, "RuntimeException while executing runnable " + runnable + " with executor " + executor, var3);
      }

   }

   private static final class RunnableExecutorPair {
      final Runnable runnable;
      final Executor executor;
      @Nullable
      ExecutionList.RunnableExecutorPair next;

      RunnableExecutorPair(Runnable runnable, Executor executor, ExecutionList.RunnableExecutorPair next) {
         this.runnable = runnable;
         this.executor = executor;
         this.next = next;
      }
   }
}
