package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

public final class Callables {
   public static Callable returning(@Nullable final Object value) {
      return new Callable() {
         public Object call() {
            return value;
         }
      };
   }

   static Callable threadRenaming(final Callable callable, final Supplier nameSupplier) {
      Preconditions.checkNotNull(nameSupplier);
      Preconditions.checkNotNull(callable);
      return new Callable() {
         public Object call() throws Exception {
            Thread currentThread = Thread.currentThread();
            String oldName = currentThread.getName();
            boolean restoreName = Callables.trySetName((String)nameSupplier.get(), currentThread);

            Object var4;
            try {
               var4 = callable.call();
            } finally {
               if(restoreName) {
                  Callables.trySetName(oldName, currentThread);
               }

            }

            return var4;
         }
      };
   }

   static Runnable threadRenaming(final Runnable task, final Supplier nameSupplier) {
      Preconditions.checkNotNull(nameSupplier);
      Preconditions.checkNotNull(task);
      return new Runnable() {
         public void run() {
            Thread currentThread = Thread.currentThread();
            String oldName = currentThread.getName();
            boolean restoreName = Callables.trySetName((String)nameSupplier.get(), currentThread);

            try {
               task.run();
            } finally {
               if(restoreName) {
                  Callables.trySetName(oldName, currentThread);
               }

            }

         }
      };
   }

   private static boolean trySetName(String threadName, Thread currentThread) {
      try {
         currentThread.setName(threadName);
         return true;
      } catch (SecurityException var3) {
         return false;
      }
   }
}
