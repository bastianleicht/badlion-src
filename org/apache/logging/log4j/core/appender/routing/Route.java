package org.apache.logging.log4j.core.appender.routing;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginNode;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "Route",
   category = "Core",
   printObject = true,
   deferChildren = true
)
public final class Route {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final Node node;
   private final String appenderRef;
   private final String key;

   private Route(Node node, String appenderRef, String key) {
      this.node = node;
      this.appenderRef = appenderRef;
      this.key = key;
   }

   public Node getNode() {
      return this.node;
   }

   public String getAppenderRef() {
      return this.appenderRef;
   }

   public String getKey() {
      return this.key;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("Route(");
      sb.append("type=");
      if(this.appenderRef != null) {
         sb.append("static Reference=").append(this.appenderRef);
      } else if(this.node != null) {
         sb.append("dynamic - type=").append(this.node.getName());
      } else {
         sb.append("invalid Route");
      }

      if(this.key != null) {
         sb.append(" key=\'").append(this.key).append("\'");
      } else {
         sb.append(" default");
      }

      sb.append(")");
      return sb.toString();
   }

   @PluginFactory
   public static Route createRoute(@PluginAttribute("ref") String appenderRef, @PluginAttribute("key") String key, @PluginNode Node node) {
      if(node != null && node.hasChildren()) {
         for(Node child : node.getChildren()) {
            ;
         }

         if(appenderRef != null) {
            LOGGER.error("A route cannot be configured with an appender reference and an appender definition");
            return null;
         }
      } else if(appenderRef == null) {
         LOGGER.error("A route must specify an appender reference or an appender definition");
         return null;
      }

      return new Route(node, appenderRef, key);
   }
}
