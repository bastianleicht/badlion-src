package org.apache.logging.log4j.core.appender.rolling;

import java.lang.management.ManagementFactory;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "OnStartupTriggeringPolicy",
   category = "Core",
   printObject = true
)
public class OnStartupTriggeringPolicy implements TriggeringPolicy {
   private static long JVM_START_TIME = ManagementFactory.getRuntimeMXBean().getStartTime();
   private static final Logger LOGGER = StatusLogger.getLogger();
   private boolean evaluated = false;
   private RollingFileManager manager;

   public void initialize(RollingFileManager manager) {
      this.manager = manager;
      if(JVM_START_TIME == 0L) {
         this.evaluated = true;
      }

   }

   public boolean isTriggeringEvent(LogEvent event) {
      if(this.evaluated) {
         return false;
      } else {
         this.evaluated = true;
         return this.manager.getFileTime() < JVM_START_TIME;
      }
   }

   public String toString() {
      return "OnStartupTriggeringPolicy";
   }

   @PluginFactory
   public static OnStartupTriggeringPolicy createPolicy() {
      return new OnStartupTriggeringPolicy();
   }
}
