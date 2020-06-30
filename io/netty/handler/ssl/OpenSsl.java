package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslEngine;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.apache.tomcat.jni.Library;
import org.apache.tomcat.jni.SSL;

public final class OpenSsl {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSsl.class);
   private static final Throwable UNAVAILABILITY_CAUSE;
   static final String IGNORABLE_ERROR_PREFIX = "error:00000000:";

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

      try {
         NativeLibraryLoader.load("netty-tcnative", SSL.class.getClassLoader());
         Library.initialize("provided");
         SSL.initialize((String)null);
      } catch (Throwable var2) {
         cause = var2;
         logger.debug("Failed to load netty-tcnative; " + OpenSslEngine.class.getSimpleName() + " will be unavailable.", var2);
      }

      UNAVAILABILITY_CAUSE = cause;
   }
}
