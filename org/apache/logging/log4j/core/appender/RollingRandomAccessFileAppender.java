package org.apache.logging.log4j.core.appender;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.RollingRandomAccessFileManager;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Booleans;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.pattern.RegexReplacement;

@Plugin(
   name = "RollingRandomAccessFile",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class RollingRandomAccessFileAppender extends AbstractOutputStreamAppender {
   private final String fileName;
   private final String filePattern;
   private Object advertisement;
   private final Advertiser advertiser;

   private RollingRandomAccessFileAppender(String name, Layout layout, Filter filter, RollingFileManager manager, String fileName, String filePattern, boolean ignoreExceptions, boolean immediateFlush, Advertiser advertiser) {
      super(name, layout, filter, ignoreExceptions, immediateFlush, manager);
      if(advertiser != null) {
         Map<String, String> configuration = new HashMap(layout.getContentFormat());
         configuration.put("contentType", layout.getContentType());
         configuration.put("name", name);
         this.advertisement = advertiser.advertise(configuration);
      }

      this.fileName = fileName;
      this.filePattern = filePattern;
      this.advertiser = advertiser;
   }

   public void stop() {
      super.stop();
      if(this.advertiser != null) {
         this.advertiser.unadvertise(this.advertisement);
      }

   }

   public void append(LogEvent event) {
      RollingRandomAccessFileManager manager = (RollingRandomAccessFileManager)this.getManager();
      manager.checkRollover(event);
      manager.setEndOfBatch(event.isEndOfBatch());
      super.append(event);
   }

   public String getFileName() {
      return this.fileName;
   }

   public String getFilePattern() {
      return this.filePattern;
   }

   @PluginFactory
   public static RollingRandomAccessFileAppender createAppender(@PluginAttribute("fileName") String fileName, @PluginAttribute("filePattern") String filePattern, @PluginAttribute("append") String append, @PluginAttribute("name") String name, @PluginAttribute("immediateFlush") String immediateFlush, @PluginElement("Policy") TriggeringPolicy policy, @PluginElement("Strategy") RolloverStrategy strategy, @PluginElement("Layout") Layout layout, @PluginElement("Filter") Filter filter, @PluginAttribute("ignoreExceptions") String ignore, @PluginAttribute("advertise") String advertise, @PluginAttribute("advertiseURI") String advertiseURI, @PluginConfiguration Configuration config) {
      boolean isAppend = Booleans.parseBoolean(append, true);
      boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
      boolean isFlush = Booleans.parseBoolean(immediateFlush, true);
      boolean isAdvertise = Boolean.parseBoolean(advertise);
      if(name == null) {
         LOGGER.error("No name provided for FileAppender");
         return null;
      } else if(fileName == null) {
         LOGGER.error("No filename was provided for FileAppender with name " + name);
         return null;
      } else if(filePattern == null) {
         LOGGER.error("No filename pattern provided for FileAppender with name " + name);
         return null;
      } else if(policy == null) {
         LOGGER.error("A TriggeringPolicy must be provided");
         return null;
      } else {
         if(strategy == null) {
            strategy = DefaultRolloverStrategy.createStrategy((String)null, (String)null, (String)null, String.valueOf(-1), config);
         }

         if(layout == null) {
            layout = PatternLayout.createLayout((String)null, (Configuration)null, (RegexReplacement)null, (String)null, (String)null);
         }

         RollingRandomAccessFileManager manager = RollingRandomAccessFileManager.getRollingRandomAccessFileManager(fileName, filePattern, isAppend, isFlush, policy, (RolloverStrategy)strategy, advertiseURI, (Layout)layout);
         return manager == null?null:new RollingRandomAccessFileAppender(name, (Layout)layout, filter, manager, fileName, filePattern, ignoreExceptions, isFlush, isAdvertise?config.getAdvertiser():null);
      }
   }
}
