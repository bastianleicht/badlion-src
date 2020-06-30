package org.apache.logging.log4j.core.net;

import org.apache.logging.log4j.Level;

public enum Severity {
   EMERG(0),
   ALERT(1),
   CRITICAL(2),
   ERROR(3),
   WARNING(4),
   NOTICE(5),
   INFO(6),
   DEBUG(7);

   private final int code;

   private Severity(int code) {
      this.code = code;
   }

   public int getCode() {
      return this.code;
   }

   public boolean isEqual(String name) {
      return this.name().equalsIgnoreCase(name);
   }

   public static Severity getSeverity(Level level) {
      switch(level) {
      case ALL:
         return DEBUG;
      case TRACE:
         return DEBUG;
      case DEBUG:
         return DEBUG;
      case INFO:
         return INFO;
      case WARN:
         return WARNING;
      case ERROR:
         return ERROR;
      case FATAL:
         return ALERT;
      case OFF:
         return EMERG;
      default:
         return DEBUG;
      }
   }
}
