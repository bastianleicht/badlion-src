package org.apache.logging.log4j.core.helpers;

import java.util.concurrent.locks.LockSupport;
import org.apache.logging.log4j.core.helpers.Clock;

public final class CachedClock implements Clock {
   private static final int UPDATE_THRESHOLD = 1023;
   private static CachedClock instance = new CachedClock();
   private volatile long millis = System.currentTimeMillis();
   private volatile short count = 0;
   private final Thread updater = new Thread("Clock Updater Thread") {
      public void run() {
         while(true) {
            long time = System.currentTimeMillis();
            CachedClock.this.millis = time;
            LockSupport.parkNanos(1000000L);
         }
      }
   };

   private CachedClock() {
      this.updater.setDaemon(true);
      this.updater.start();
   }

   public static CachedClock instance() {
      return instance;
   }

   public long currentTimeMillis() {
      if((++this.count & 1023) == 1023) {
         this.millis = System.currentTimeMillis();
      }

      return this.millis;
   }
}
