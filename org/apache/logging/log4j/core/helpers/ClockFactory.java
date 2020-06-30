package org.apache.logging.log4j.core.helpers;

import org.apache.logging.log4j.core.helpers.CachedClock;
import org.apache.logging.log4j.core.helpers.Clock;
import org.apache.logging.log4j.core.helpers.CoarseCachedClock;
import org.apache.logging.log4j.core.helpers.SystemClock;
import org.apache.logging.log4j.status.StatusLogger;

public final class ClockFactory {
   public static final String PROPERTY_NAME = "log4j.Clock";
   private static final StatusLogger LOGGER = StatusLogger.getLogger();

   public static Clock getClock() {
      return createClock();
   }

   private static Clock createClock() {
      String userRequest = System.getProperty("log4j.Clock");
      if(userRequest != null && !"SystemClock".equals(userRequest)) {
         if(!CachedClock.class.getName().equals(userRequest) && !"CachedClock".equals(userRequest)) {
            if(!CoarseCachedClock.class.getName().equals(userRequest) && !"CoarseCachedClock".equals(userRequest)) {
               try {
                  Clock result = (Clock)Class.forName(userRequest).newInstance();
                  LOGGER.debug("Using {} for timestamps", new Object[]{userRequest});
                  return result;
               } catch (Exception var3) {
                  String fmt = "Could not create {}: {}, using default SystemClock for timestamps";
                  LOGGER.error("Could not create {}: {}, using default SystemClock for timestamps", new Object[]{userRequest, var3});
                  return new SystemClock();
               }
            } else {
               LOGGER.debug("Using specified CoarseCachedClock for timestamps");
               return CoarseCachedClock.instance();
            }
         } else {
            LOGGER.debug("Using specified CachedClock for timestamps");
            return CachedClock.instance();
         }
      } else {
         LOGGER.debug("Using default SystemClock for timestamps");
         return new SystemClock();
      }
   }
}
