package org.apache.logging.log4j;

import java.util.Locale;

public enum Level {
   OFF(0),
   FATAL(1),
   ERROR(2),
   WARN(3),
   INFO(4),
   DEBUG(5),
   TRACE(6),
   ALL(Integer.MAX_VALUE);

   private final int intLevel;

   private Level(int val) {
      this.intLevel = val;
   }

   public static Level toLevel(String sArg) {
      return toLevel(sArg, DEBUG);
   }

   public static Level toLevel(String name, Level defaultLevel) {
      if(name == null) {
         return defaultLevel;
      } else {
         String cleanLevel = name.toUpperCase(Locale.ENGLISH);

         for(Level level : values()) {
            if(level.name().equals(cleanLevel)) {
               return level;
            }
         }

         return defaultLevel;
      }
   }

   public boolean isAtLeastAsSpecificAs(Level level) {
      return this.intLevel <= level.intLevel;
   }

   public boolean isAtLeastAsSpecificAs(int level) {
      return this.intLevel <= level;
   }

   public boolean lessOrEqual(Level level) {
      return this.intLevel <= level.intLevel;
   }

   public boolean lessOrEqual(int level) {
      return this.intLevel <= level;
   }

   public int intLevel() {
      return this.intLevel;
   }
}
