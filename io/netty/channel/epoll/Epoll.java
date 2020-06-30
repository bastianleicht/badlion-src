package io.netty.channel.epoll;

import io.netty.channel.epoll.Native;

public final class Epoll {
   private static final Throwable UNAVAILABILITY_CAUSE;

   public static boolean isAvailable() {
      return UNAVAILABILITY_CAUSE == null;
   }

   public static void ensureAvailability() {
      if(UNAVAILABILITY_CAUSE != null) {
         throw (Error)(new UnsatisfiedLinkError("failed to load the required native library")).initCause(UNAVAILABILITY_CAUSE);
      }
   }

   public static Throwable unavailabilityCause() {
      return UNAVAILABILITY_CAUSE;
   }

   static {
      Throwable cause = null;
      int epollFd = -1;
      int eventFd = -1;

      try {
         epollFd = Native.epollCreate();
         eventFd = Native.eventFd();
      } catch (Throwable var16) {
         cause = var16;
      } finally {
         if(epollFd != -1) {
            try {
               Native.close(epollFd);
            } catch (Exception var15) {
               ;
            }
         }

         if(eventFd != -1) {
            try {
               Native.close(eventFd);
            } catch (Exception var14) {
               ;
            }
         }

      }

      if(cause != null) {
         UNAVAILABILITY_CAUSE = cause;
      } else {
         UNAVAILABILITY_CAUSE = null;
      }

   }
}
