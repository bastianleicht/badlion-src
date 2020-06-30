package org.apache.logging.log4j.core.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.async.AsyncLoggerContextSelector;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.helpers.Booleans;
import org.apache.logging.log4j.core.helpers.Loader;
import org.apache.logging.log4j.core.helpers.Strings;
import org.apache.logging.log4j.core.impl.DefaultLogEventFactory;
import org.apache.logging.log4j.core.impl.LogEventFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

@Plugin(
   name = "logger",
   category = "Core",
   printObject = true
)
public class LoggerConfig extends AbstractFilterable {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   private static final int MAX_RETRIES = 3;
   private static final long WAIT_TIME = 1000L;
   private static LogEventFactory LOG_EVENT_FACTORY = null;
   private List appenderRefs = new ArrayList();
   private final Map appenders = new ConcurrentHashMap();
   private final String name;
   private LogEventFactory logEventFactory;
   private Level level;
   private boolean additive = true;
   private boolean includeLocation = true;
   private LoggerConfig parent;
   private final AtomicInteger counter = new AtomicInteger();
   private boolean shutdown = false;
   private final Map properties;
   private final Configuration config;

   public LoggerConfig() {
      this.logEventFactory = LOG_EVENT_FACTORY;
      this.level = Level.ERROR;
      this.name = "";
      this.properties = null;
      this.config = null;
   }

   public LoggerConfig(String name, Level level, boolean additive) {
      this.logEventFactory = LOG_EVENT_FACTORY;
      this.name = name;
      this.level = level;
      this.additive = additive;
      this.properties = null;
      this.config = null;
   }

   protected LoggerConfig(String name, List appenders, Filter filter, Level level, boolean additive, Property[] properties, Configuration config, boolean includeLocation) {
      super(filter);
      this.logEventFactory = LOG_EVENT_FACTORY;
      this.name = name;
      this.appenderRefs = appenders;
      this.level = level;
      this.additive = additive;
      this.includeLocation = includeLocation;
      this.config = config;
      if(properties != null && properties.length > 0) {
         this.properties = new HashMap(properties.length);

         for(Property prop : properties) {
            boolean interpolate = prop.getValue().contains("${");
            this.properties.put(prop, Boolean.valueOf(interpolate));
         }
      } else {
         this.properties = null;
      }

   }

   public Filter getFilter() {
      return super.getFilter();
   }

   public String getName() {
      return this.name;
   }

   public void setParent(LoggerConfig parent) {
      this.parent = parent;
   }

   public LoggerConfig getParent() {
      return this.parent;
   }

   public void addAppender(Appender appender, Level level, Filter filter) {
      this.appenders.put(appender.getName(), new AppenderControl(appender, level, filter));
   }

   public void removeAppender(String name) {
      AppenderControl ctl = (AppenderControl)this.appenders.remove(name);
      if(ctl != null) {
         this.cleanupFilter(ctl);
      }

   }

   public Map getAppenders() {
      Map<String, Appender> map = new HashMap();

      for(Entry<String, AppenderControl> entry : this.appenders.entrySet()) {
         map.put(entry.getKey(), ((AppenderControl)entry.getValue()).getAppender());
      }

      return map;
   }

   protected void clearAppenders() {
      this.waitForCompletion();
      Collection<AppenderControl> controls = this.appenders.values();
      Iterator<AppenderControl> iterator = controls.iterator();

      while(iterator.hasNext()) {
         AppenderControl ctl = (AppenderControl)iterator.next();
         iterator.remove();
         this.cleanupFilter(ctl);
      }

   }

   private void cleanupFilter(AppenderControl ctl) {
      Filter filter = ctl.getFilter();
      if(filter != null) {
         ctl.removeFilter(filter);
         if(filter instanceof LifeCycle) {
            ((LifeCycle)filter).stop();
         }
      }

   }

   public List getAppenderRefs() {
      return this.appenderRefs;
   }

   public void setLevel(Level level) {
      this.level = level;
   }

   public Level getLevel() {
      return this.level;
   }

   public LogEventFactory getLogEventFactory() {
      return this.logEventFactory;
   }

   public void setLogEventFactory(LogEventFactory logEventFactory) {
      this.logEventFactory = logEventFactory;
   }

   public boolean isAdditive() {
      return this.additive;
   }

   public void setAdditive(boolean additive) {
      this.additive = additive;
   }

   public boolean isIncludeLocation() {
      return this.includeLocation;
   }

   public Map getProperties() {
      return this.properties == null?null:Collections.unmodifiableMap(this.properties);
   }

   public void log(String loggerName, Marker marker, String fqcn, Level level, Message data, Throwable t) {
      List<Property> props = null;
      if(this.properties != null) {
         props = new ArrayList(this.properties.size());

         for(Entry<Property, Boolean> entry : this.properties.entrySet()) {
            Property prop = (Property)entry.getKey();
            String value = ((Boolean)entry.getValue()).booleanValue()?this.config.getStrSubstitutor().replace(prop.getValue()):prop.getValue();
            props.add(Property.createProperty(prop.getName(), value));
         }
      }

      LogEvent event = this.logEventFactory.createEvent(loggerName, marker, fqcn, level, data, props, t);
      this.log(event);
   }

   private synchronized void waitForCompletion() {
      if(!this.shutdown) {
         this.shutdown = true;
         int retries = 0;

         while(this.counter.get() > 0) {
            try {
               this.wait(1000L * (long)(retries + 1));
            } catch (InterruptedException var3) {
               ++retries;
               if(retries > 3) {
                  break;
               }
            }
         }

      }
   }

   public void log(LogEvent event) {
      // $FF: Couldn't be decompiled
   }

   protected void callAppenders(LogEvent event) {
      for(AppenderControl control : this.appenders.values()) {
         control.callAppender(event);
      }

   }

   public String toString() {
      return Strings.isEmpty(this.name)?"root":this.name;
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
         return new LoggerConfig(name, appenderRefs, filter, level, additive, properties, config, includeLocation(includeLocation));
      }
   }

   protected static boolean includeLocation(String includeLocationConfigValue) {
      if(includeLocationConfigValue == null) {
         boolean sync = !AsyncLoggerContextSelector.class.getName().equals(System.getProperty("Log4jContextSelector"));
         return sync;
      } else {
         return Boolean.parseBoolean(includeLocationConfigValue);
      }
   }

   static {
      String factory = PropertiesUtil.getProperties().getStringProperty("Log4jLogEventFactory");
      if(factory != null) {
         try {
            Class<?> clazz = Loader.loadClass(factory);
            if(clazz != null && LogEventFactory.class.isAssignableFrom(clazz)) {
               LOG_EVENT_FACTORY = (LogEventFactory)clazz.newInstance();
            }
         } catch (Exception var2) {
            LOGGER.error((String)("Unable to create LogEventFactory " + factory), (Throwable)var2);
         }
      }

      if(LOG_EVENT_FACTORY == null) {
         LOG_EVENT_FACTORY = new DefaultLogEventFactory();
      }

   }

   @Plugin(
      name = "root",
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
         return new LoggerConfig("", appenderRefs, filter, level, additive, properties, config, includeLocation(includeLocation));
      }
   }
}
