package org.apache.logging.log4j.core.appender.rewrite;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "PropertiesRewritePolicy",
   category = "Core",
   elementType = "rewritePolicy",
   printObject = true
)
public final class PropertiesRewritePolicy implements RewritePolicy {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   private final Map properties;
   private final Configuration config;

   private PropertiesRewritePolicy(Configuration config, List props) {
      this.config = config;
      this.properties = new HashMap(props.size());

      for(Property property : props) {
         Boolean interpolate = Boolean.valueOf(property.getValue().contains("${"));
         this.properties.put(property, interpolate);
      }

   }

   public LogEvent rewrite(LogEvent source) {
      Map<String, String> props = new HashMap(source.getContextMap());

      for(Entry<Property, Boolean> entry : this.properties.entrySet()) {
         Property prop = (Property)entry.getKey();
         props.put(prop.getName(), ((Boolean)entry.getValue()).booleanValue()?this.config.getStrSubstitutor().replace(prop.getValue()):prop.getValue());
      }

      return new Log4jLogEvent(source.getLoggerName(), source.getMarker(), source.getFQCN(), source.getLevel(), source.getMessage(), source.getThrown(), props, source.getContextStack(), source.getThreadName(), source.getSource(), source.getMillis());
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(" {");
      boolean first = true;

      for(Entry<Property, Boolean> entry : this.properties.entrySet()) {
         if(!first) {
            sb.append(", ");
         }

         Property prop = (Property)entry.getKey();
         sb.append(prop.getName()).append("=").append(prop.getValue());
         first = false;
      }

      sb.append("}");
      return sb.toString();
   }

   @PluginFactory
   public static PropertiesRewritePolicy createPolicy(@PluginConfiguration Configuration config, @PluginElement("Properties") Property[] props) {
      if(props != null && props.length != 0) {
         List<Property> properties = Arrays.asList(props);
         return new PropertiesRewritePolicy(config, properties);
      } else {
         LOGGER.error("Properties must be specified for the PropertiesRewritePolicy");
         return null;
      }
   }
}
