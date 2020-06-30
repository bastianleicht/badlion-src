package org.apache.logging.log4j.core.appender.routing;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.routing.Route;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "Routes",
   category = "Core",
   printObject = true
)
public final class Routes {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final String pattern;
   private final Route[] routes;

   private Routes(String pattern, Route... routes) {
      this.pattern = pattern;
      this.routes = routes;
   }

   public String getPattern() {
      return this.pattern;
   }

   public Route[] getRoutes() {
      return this.routes;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("{");
      boolean first = true;

      for(Route route : this.routes) {
         if(!first) {
            sb.append(",");
         }

         first = false;
         sb.append(route.toString());
      }

      sb.append("}");
      return sb.toString();
   }

   @PluginFactory
   public static Routes createRoutes(@PluginAttribute("pattern") String pattern, @PluginElement("Routes") Route... routes) {
      if(pattern == null) {
         LOGGER.error("A pattern is required");
         return null;
      } else if(routes != null && routes.length != 0) {
         return new Routes(pattern, routes);
      } else {
         LOGGER.error("No routes configured");
         return null;
      }
   }
}
