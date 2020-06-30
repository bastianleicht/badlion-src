package org.apache.logging.log4j.core.appender.rolling;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Integers;

@Plugin(
   name = "TimeBasedTriggeringPolicy",
   category = "Core",
   printObject = true
)
public final class TimeBasedTriggeringPolicy implements TriggeringPolicy {
   private long nextRollover;
   private final int interval;
   private final boolean modulate;
   private RollingFileManager manager;

   private TimeBasedTriggeringPolicy(int interval, boolean modulate) {
      this.interval = interval;
      this.modulate = modulate;
   }

   public void initialize(RollingFileManager manager) {
      this.manager = manager;
      this.nextRollover = manager.getPatternProcessor().getNextTime(manager.getFileTime(), this.interval, this.modulate);
   }

   public boolean isTriggeringEvent(LogEvent event) {
      if(this.manager.getFileSize() == 0L) {
         return false;
      } else {
         long now = System.currentTimeMillis();
         if(now > this.nextRollover) {
            this.nextRollover = this.manager.getPatternProcessor().getNextTime(now, this.interval, this.modulate);
            return true;
         } else {
            return false;
         }
      }
   }

   public String toString() {
      return "TimeBasedTriggeringPolicy";
   }

   @PluginFactory
   public static TimeBasedTriggeringPolicy createPolicy(@PluginAttribute("interval") String interval, @PluginAttribute("modulate") String modulate) {
      int increment = Integers.parseInt(interval, 1);
      boolean mod = Boolean.parseBoolean(modulate);
      return new TimeBasedTriggeringPolicy(increment, mod);
   }
}
