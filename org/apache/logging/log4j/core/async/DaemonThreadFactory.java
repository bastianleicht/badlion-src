package org.apache.logging.log4j.core.async;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DaemonThreadFactory implements ThreadFactory {
   final ThreadGroup group;
   final AtomicInteger threadNumber = new AtomicInteger(1);
   final String threadNamePrefix;

   public DaemonThreadFactory(String threadNamePrefix) {
      this.threadNamePrefix = threadNamePrefix;
      SecurityManager securityManager = System.getSecurityManager();
      this.group = securityManager != null?securityManager.getThreadGroup():Thread.currentThread().getThreadGroup();
   }

   public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(this.group, runnable, this.threadNamePrefix + this.threadNumber.getAndIncrement(), 0L);
      if(!thread.isDaemon()) {
         thread.setDaemon(true);
      }

      if(thread.getPriority() != 5) {
         thread.setPriority(5);
      }

      return thread;
   }
}
