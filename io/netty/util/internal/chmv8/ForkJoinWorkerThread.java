package io.netty.util.internal.chmv8;

import io.netty.util.internal.chmv8.ForkJoinPool;

public class ForkJoinWorkerThread extends Thread {
   final ForkJoinPool pool;
   final ForkJoinPool.WorkQueue workQueue;

   protected ForkJoinWorkerThread(ForkJoinPool pool) {
      super("aForkJoinWorkerThread");
      this.pool = pool;
      this.workQueue = pool.registerWorker(this);
   }

   public ForkJoinPool getPool() {
      return this.pool;
   }

   public int getPoolIndex() {
      return this.workQueue.poolIndex >>> 1;
   }

   protected void onStart() {
   }

   protected void onTermination(Throwable exception) {
   }

   public void run() {
      Throwable exception = null;

      try {
         this.onStart();
         this.pool.runWorker(this.workQueue);
      } catch (Throwable var40) {
         exception = var40;
      } finally {
         try {
            this.onTermination(exception);
         } catch (Throwable var41) {
            if(exception == null) {
               exception = var41;
            }
         } finally {
            this.pool.deregisterWorker(this, exception);
         }

      }

   }
}
