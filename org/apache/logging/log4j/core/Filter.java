package org.apache.logging.log4j.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.EnglishEnums;

public interface Filter {
   Filter.Result getOnMismatch();

   Filter.Result getOnMatch();

   Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5);

   Filter.Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5);

   Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5);

   Filter.Result filter(LogEvent var1);

   public static enum Result {
      ACCEPT,
      NEUTRAL,
      DENY;

      public static Filter.Result toResult(String name) {
         return toResult(name, (Filter.Result)null);
      }

      public static Filter.Result toResult(String name, Filter.Result defaultResult) {
         return (Filter.Result)EnglishEnums.valueOf(Filter.Result.class, name, defaultResult);
      }
   }
}
