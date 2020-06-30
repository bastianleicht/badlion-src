package org.apache.commons.io;

class ThreadMonitor implements Runnable {
   private final Thread thread;
   private final long timeout;

   public static Thread start(long timeout) {
      return start(Thread.currentThread(), timeout);
   }

   public static Thread start(Thread thread, long timeout) {
      Thread monitor = null;
      if(timeout > 0L) {
         ThreadMonitor timout = new ThreadMonitor(thread, timeout);
         monitor = new Thread(timout, ThreadMonitor.class.getSimpleName());
         monitor.setDaemon(true);
         monitor.start();
      }

      return monitor;
   }

   public static void stop(Thread thread) {
      if(thread != null) {
         thread.interrupt();
      }

   }

   private ThreadMonitor(Thread thread, long timeout) {
      this.thread = thread;
      this.timeout = timeout;
   }

   public void run() {
      try {
         Thread.sleep(this.timeout);
         this.thread.interrupt();
      } catch (InterruptedException var2) {
         ;
      }

   }
}
