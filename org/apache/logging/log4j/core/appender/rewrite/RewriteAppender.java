package org.apache.logging.log4j.core.appender.rewrite;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Booleans;

@Plugin(
   name = "Rewrite",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class RewriteAppender extends AbstractAppender {
   private final Configuration config;
   private final ConcurrentMap appenders = new ConcurrentHashMap();
   private final RewritePolicy rewritePolicy;
   private final AppenderRef[] appenderRefs;

   private RewriteAppender(String name, Filter filter, boolean ignoreExceptions, AppenderRef[] appenderRefs, RewritePolicy rewritePolicy, Configuration config) {
      super(name, filter, (Layout)null, ignoreExceptions);
      this.config = config;
      this.rewritePolicy = rewritePolicy;
      this.appenderRefs = appenderRefs;
   }

   public void start() {
      Map<String, Appender> map = this.config.getAppenders();

      for(AppenderRef ref : this.appenderRefs) {
         String name = ref.getRef();
         Appender appender = (Appender)map.get(name);
         if(appender != null) {
            Filter filter = appender instanceof AbstractAppender?((AbstractAppender)appender).getFilter():null;
            this.appenders.put(name, new AppenderControl(appender, ref.getLevel(), filter));
         } else {
            LOGGER.error("Appender " + ref + " cannot be located. Reference ignored");
         }
      }

      super.start();
   }

   public void stop() {
      super.stop();
   }

   public void append(LogEvent event) {
      if(this.rewritePolicy != null) {
         event = this.rewritePolicy.rewrite(event);
      }

      for(AppenderControl control : this.appenders.values()) {
         control.callAppender(event);
      }

   }

   @PluginFactory
   public static RewriteAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("ignoreExceptions") String ignore, @PluginElement("AppenderRef") AppenderRef[] appenderRefs, @PluginConfiguration Configuration config, @PluginElement("RewritePolicy") RewritePolicy rewritePolicy, @PluginElement("Filter") Filter filter) {
      boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
      if(name == null) {
         LOGGER.error("No name provided for RewriteAppender");
         return null;
      } else if(appenderRefs == null) {
         LOGGER.error("No appender references defined for RewriteAppender");
         return null;
      } else {
         return new RewriteAppender(name, filter, ignoreExceptions, appenderRefs, rewritePolicy, config);
      }
   }
}
