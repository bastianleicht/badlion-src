package io.netty.util;

import io.netty.util.ReferenceCounted;
import io.netty.util.ThreadDeathWatcher;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class ReferenceCountUtil {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountUtil.class);

   public static Object retain(Object msg) {
      return msg instanceof ReferenceCounted?((ReferenceCounted)msg).retain():msg;
   }

   public static Object retain(Object msg, int increment) {
      return msg instanceof ReferenceCounted?((ReferenceCounted)msg).retain(increment):msg;
   }

   public static boolean release(Object msg) {
      return msg instanceof ReferenceCounted?((ReferenceCounted)msg).release():false;
   }

   public static boolean release(Object msg, int decrement) {
      return msg instanceof ReferenceCounted?((ReferenceCounted)msg).release(decrement):false;
   }

   public static void safeRelease(Object msg) {
      try {
         release(msg);
      } catch (Throwable var2) {
         logger.warn("Failed to release a message: {}", msg, var2);
      }

   }

   public static void safeRelease(Object msg, int decrement) {
      try {
         release(msg, decrement);
      } catch (Throwable var3) {
         if(logger.isWarnEnabled()) {
            logger.warn("Failed to release a message: {} (decrement: {})", new Object[]{msg, Integer.valueOf(decrement), var3});
         }
      }

   }

   public static Object releaseLater(Object msg) {
      return releaseLater(msg, 1);
   }

   public static Object releaseLater(Object msg, int decrement) {
      if(msg instanceof ReferenceCounted) {
         ThreadDeathWatcher.watch(Thread.currentThread(), new ReferenceCountUtil.ReleasingTask((ReferenceCounted)msg, decrement));
      }

      return msg;
   }

   private static final class ReleasingTask implements Runnable {
      private final ReferenceCounted obj;
      private final int decrement;

      ReleasingTask(ReferenceCounted obj, int decrement) {
         this.obj = obj;
         this.decrement = decrement;
      }

      public void run() {
         try {
            if(!this.obj.release(this.decrement)) {
               ReferenceCountUtil.logger.warn("Non-zero refCnt: {}", (Object)this);
            } else {
               ReferenceCountUtil.logger.debug("Released: {}", (Object)this);
            }
         } catch (Exception var2) {
            ReferenceCountUtil.logger.warn("Failed to release an object: {}", this.obj, var2);
         }

      }

      public String toString() {
         return StringUtil.simpleClassName((Object)this.obj) + ".release(" + this.decrement + ") refCnt: " + this.obj.refCnt();
      }
   }
}
