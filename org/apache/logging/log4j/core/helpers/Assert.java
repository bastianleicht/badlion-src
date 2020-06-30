package org.apache.logging.log4j.core.helpers;

public final class Assert {
   public static Object isNotNull(Object checkMe, String name) {
      if(checkMe == null) {
         throw new NullPointerException(name + " is null");
      } else {
         return checkMe;
      }
   }
}
