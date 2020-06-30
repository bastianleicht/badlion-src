package io.netty.util.concurrent;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.StringUtil;
import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {
   private static final AtomicInteger poolId = new AtomicInteger();
   private final AtomicInteger nextId;
   private final String prefix;
   private final boolean daemon;
   private final int priority;

   public DefaultThreadFactory(Class poolType) {
      this((Class)poolType, false, 5);
   }

   public DefaultThreadFactory(String poolName) {
      this((String)poolName, false, 5);
   }

   public DefaultThreadFactory(Class poolType, boolean daemon) {
      this((Class)poolType, daemon, 5);
   }

   public DefaultThreadFactory(String poolName, boolean daemon) {
      this((String)poolName, daemon, 5);
   }

   public DefaultThreadFactory(Class poolType, int priority) {
      this(poolType, false, priority);
   }

   public DefaultThreadFactory(String poolName, int priority) {
      this(poolName, false, priority);
   }

   public DefaultThreadFactory(Class poolType, boolean daemon, int priority) {
      this(toPoolName(poolType), daemon, priority);
   }

   private static String toPoolName(Class poolType) {
      if(poolType == null) {
         throw new NullPointerException("poolType");
      } else {
         String poolName = StringUtil.simpleClassName(poolType);
         switch(poolName.length()) {
         case 0:
            return "unknown";
         case 1:
            return poolName.toLowerCase(Locale.US);
         default:
            return Character.isUpperCase(poolName.charAt(0)) && Character.isLowerCase(poolName.charAt(1))?Character.toLowerCase(poolName.charAt(0)) + poolName.substring(1):poolName;
         }
      }
   }

   public DefaultThreadFactory(String poolName, boolean daemon, int priority) {
      this.nextId = new AtomicInteger();
      if(poolName == null) {
         throw new NullPointerException("poolName");
      } else if(priority >= 1 && priority <= 10) {
         this.prefix = poolName + '-' + poolId.incrementAndGet() + '-';
         this.daemon = daemon;
         this.priority = priority;
      } else {
         throw new IllegalArgumentException("priority: " + priority + " (expected: Thread.MIN_PRIORITY <= priority <= Thread.MAX_PRIORITY)");
      }
   }

   public Thread newThread(Runnable r) {
      Thread t = this.newThread(new DefaultThreadFactory.DefaultRunnableDecorator(r), this.prefix + this.nextId.incrementAndGet());

      try {
         if(t.isDaemon()) {
            if(!this.daemon) {
               t.setDaemon(false);
            }
         } else if(this.daemon) {
            t.setDaemon(true);
         }

         if(t.getPriority() != this.priority) {
            t.setPriority(this.priority);
         }
      } catch (Exception var4) {
         ;
      }

      return t;
   }

   protected Thread newThread(Runnable r, String name) {
      return new FastThreadLocalThread(r, name);
   }

   private static final class DefaultRunnableDecorator implements Runnable {
      private final Runnable r;

      DefaultRunnableDecorator(Runnable r) {
         this.r = r;
      }

      public void run() {
         try {
            this.r.run();
         } finally {
            FastThreadLocal.removeAll();
         }

      }
   }
}
