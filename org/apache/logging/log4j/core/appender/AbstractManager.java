package org.apache.logging.log4j.core.appender;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.status.StatusLogger;

public abstract class AbstractManager {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   private static final Map MAP = new HashMap();
   private static final Lock LOCK = new ReentrantLock();
   protected int count;
   private final String name;

   protected AbstractManager(String name) {
      this.name = name;
      LOGGER.debug("Starting {} {}", new Object[]{this.getClass().getSimpleName(), name});
   }

   public static AbstractManager getManager(String name, ManagerFactory factory, Object data) {
      LOCK.lock();

      AbstractManager var4;
      try {
         M manager = (AbstractManager)MAP.get(name);
         if(manager == null) {
            manager = (AbstractManager)factory.createManager(name, data);
            if(manager == null) {
               throw new IllegalStateException("Unable to create a manager");
            }

            MAP.put(name, manager);
         }

         ++manager.count;
         var4 = manager;
      } finally {
         LOCK.unlock();
      }

      return var4;
   }

   public static boolean hasManager(String name) {
      LOCK.lock();

      boolean var1;
      try {
         var1 = MAP.containsKey(name);
      } finally {
         LOCK.unlock();
      }

      return var1;
   }

   protected void releaseSub() {
   }

   protected int getCount() {
      return this.count;
   }

   public void release() {
      LOCK.lock();

      try {
         --this.count;
         if(this.count <= 0) {
            MAP.remove(this.name);
            LOGGER.debug("Shutting down {} {}", new Object[]{this.getClass().getSimpleName(), this.getName()});
            this.releaseSub();
         }
      } finally {
         LOCK.unlock();
      }

   }

   public String getName() {
      return this.name;
   }

   public Map getContentFormat() {
      return new HashMap();
   }
}
