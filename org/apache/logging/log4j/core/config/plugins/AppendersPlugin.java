package org.apache.logging.log4j.core.config.plugins;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "appenders",
   category = "Core"
)
public final class AppendersPlugin {
   @PluginFactory
   public static ConcurrentMap createAppenders(@PluginElement("Appenders") Appender[] appenders) {
      ConcurrentMap<String, Appender> map = new ConcurrentHashMap();

      for(Appender appender : appenders) {
         map.put(appender.getName(), appender);
      }

      return map;
   }
}
