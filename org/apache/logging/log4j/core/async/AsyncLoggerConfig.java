package org.apache.logging.log4j.core.async;

import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.async.AsyncLoggerConfigHelper;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Booleans;

@Plugin(
   name = "asyncLogger",
   category = "Core",
   printObject = true
)
public class AsyncLoggerConfig extends LoggerConfig {
   private AsyncLoggerConfigHelper helper;

   public AsyncLoggerConfig() {
   }

   public AsyncLoggerConfig(String name, Level level, boolean additive) {
      super(name, level, additive);
   }

   protected AsyncLoggerConfig(String name, List appenders, Filter filter, Level level, boolean additive, Property[] properties, Configuration config, boolean includeLocation) {
      super(name, appenders, filter, level, additive, properties, config, includeLocation);
   }

   protected void callAppenders(LogEvent event) {
      event.getSource();
      event.getThreadName();
      this.helper.callAppendersFromAnotherThread(event);
   }

   void asyncCallAppenders(LogEvent event) {
      super.callAppenders(event);
   }

   public void startFilter() {
      if(this.helper == null) {
         this.helper = new AsyncLoggerConfigHelper(this);
      } else {
         AsyncLoggerConfigHelper.claim();
      }

      super.startFilter();
   }

   public void stopFilter() {
      AsyncLoggerConfigHelper.release();
      super.stopFilter();
   }

   @PluginFactory
   public static LoggerConfig createLogger(@PluginAttribute("additivity") String additivity, @PluginAttribute("level") String levelName, @PluginAttribute("name") String loggerName, @PluginAttribute("includeLocation") String includeLocation, @PluginElement("AppenderRef") AppenderRef[] refs, @PluginElement("Properties") Property[] properties, @PluginConfiguration Configuration config, @PluginElement("Filters") Filter filter) {
      if(loggerName == null) {
         LOGGER.error("Loggers cannot be configured without a name");
         return null;
      } else {
         List<AppenderRef> appenderRefs = Arrays.asList(refs);

         Level level;
         try {
            level = Level.toLevel(levelName, Level.ERROR);
         } catch (Exception var12) {
            LOGGER.error("Invalid Log level specified: {}. Defaulting to Error", new Object[]{levelName});
            level = Level.ERROR;
         }

         String name = loggerName.equals("root")?"":loggerName;
         boolean additive = Booleans.parseBoolean(additivity, true);
         return new AsyncLoggerConfig(name, appenderRefs, filter, level, additive, properties, config, includeLocation(includeLocation));
      }
   }

   protected static boolean includeLocation(String includeLocationConfigValue) {
      return Boolean.parseBoolean(includeLocationConfigValue);
   }

   @Plugin(
      name = "asyncRoot",
      category = "Core",
      printObject = true
   )
   public static class RootLogger extends LoggerConfig {
      @PluginFactory
      public static LoggerConfig createLogger(@PluginAttribute("additivity") String additivity, @PluginAttribute("level") String levelName, @PluginAttribute("includeLocation") String includeLocation, @PluginElement("AppenderRef") AppenderRef[] refs, @PluginElement("Properties") Property[] properties, @PluginConfiguration Configuration config, @PluginElement("Filters") Filter filter) {
         List<AppenderRef> appenderRefs = Arrays.asList(refs);

         Level level;
         try {
            level = Level.toLevel(levelName, Level.ERROR);
         } catch (Exception var10) {
            LOGGER.error("Invalid Log level specified: {}. Defaulting to Error", new Object[]{levelName});
            level = Level.ERROR;
         }

         boolean additive = Booleans.parseBoolean(additivity, true);
         return new AsyncLoggerConfig("", appenderRefs, filter, level, additive, properties, config, includeLocation(includeLocation));
      }
   }
}
