package org.apache.logging.log4j.core.config.plugins;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Loggers;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "loggers",
   category = "Core"
)
public final class LoggersPlugin {
   @PluginFactory
   public static Loggers createLoggers(@PluginElement("Loggers") LoggerConfig[] loggers) {
      ConcurrentMap<String, LoggerConfig> loggerMap = new ConcurrentHashMap();
      LoggerConfig root = null;

      for(LoggerConfig logger : loggers) {
         if(logger != null) {
            if(logger.getName().isEmpty()) {
               root = logger;
            }

            loggerMap.put(logger.getName(), logger);
         }
      }

      return new Loggers(loggerMap, root);
   }
}
