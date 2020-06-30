package org.apache.logging.log4j.core.appender.routing;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.appender.routing.Route;
import org.apache.logging.log4j.core.appender.routing.Routes;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Booleans;

@Plugin(
   name = "Routing",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class RoutingAppender extends AbstractAppender {
   private static final String DEFAULT_KEY = "ROUTING_APPENDER_DEFAULT";
   private final Routes routes;
   private final Route defaultRoute;
   private final Configuration config;
   private final ConcurrentMap appenders = new ConcurrentHashMap();
   private final RewritePolicy rewritePolicy;

   private RoutingAppender(String name, Filter filter, boolean ignoreExceptions, Routes routes, RewritePolicy rewritePolicy, Configuration config) {
      super(name, filter, (Layout)null, ignoreExceptions);
      this.routes = routes;
      this.config = config;
      this.rewritePolicy = rewritePolicy;
      Route defRoute = null;

      for(Route route : routes.getRoutes()) {
         if(route.getKey() == null) {
            if(defRoute == null) {
               defRoute = route;
            } else {
               this.error("Multiple default routes. Route " + route.toString() + " will be ignored");
            }
         }
      }

      this.defaultRoute = defRoute;
   }

   public void start() {
      Map<String, Appender> map = this.config.getAppenders();

      for(Route route : this.routes.getRoutes()) {
         if(route.getAppenderRef() != null) {
            Appender appender = (Appender)map.get(route.getAppenderRef());
            if(appender != null) {
               String key = route == this.defaultRoute?"ROUTING_APPENDER_DEFAULT":route.getKey();
               this.appenders.put(key, new AppenderControl(appender, (Level)null, (Filter)null));
            } else {
               LOGGER.error("Appender " + route.getAppenderRef() + " cannot be located. Route ignored");
            }
         }
      }

      super.start();
   }

   public void stop() {
      super.stop();
      Map<String, Appender> map = this.config.getAppenders();

      for(Entry<String, AppenderControl> entry : this.appenders.entrySet()) {
         String name = ((AppenderControl)entry.getValue()).getAppender().getName();
         if(!map.containsKey(name)) {
            ((AppenderControl)entry.getValue()).getAppender().stop();
         }
      }

   }

   public void append(LogEvent event) {
      if(this.rewritePolicy != null) {
         event = this.rewritePolicy.rewrite(event);
      }

      String key = this.config.getStrSubstitutor().replace(event, this.routes.getPattern());
      AppenderControl control = this.getControl(key, event);
      if(control != null) {
         control.callAppender(event);
      }

   }

   private synchronized AppenderControl getControl(String key, LogEvent event) {
      AppenderControl control = (AppenderControl)this.appenders.get(key);
      if(control != null) {
         return control;
      } else {
         Route route = null;

         for(Route r : this.routes.getRoutes()) {
            if(r.getAppenderRef() == null && key.equals(r.getKey())) {
               route = r;
               break;
            }
         }

         if(route == null) {
            route = this.defaultRoute;
            control = (AppenderControl)this.appenders.get("ROUTING_APPENDER_DEFAULT");
            if(control != null) {
               return control;
            }
         }

         if(route != null) {
            Appender app = this.createAppender(route, event);
            if(app == null) {
               return null;
            }

            control = new AppenderControl(app, (Level)null, (Filter)null);
            this.appenders.put(key, control);
         }

         return control;
      }
   }

   private Appender createAppender(Route route, LogEvent event) {
      Node routeNode = route.getNode();

      for(Node node : routeNode.getChildren()) {
         if(node.getType().getElementName().equals("appender")) {
            Node appNode = new Node(node);
            this.config.createConfiguration(appNode, event);
            if(appNode.getObject() instanceof Appender) {
               Appender app = (Appender)appNode.getObject();
               app.start();
               return app;
            }

            LOGGER.error("Unable to create Appender of type " + node.getName());
            return null;
         }
      }

      LOGGER.error("No Appender was configured for route " + route.getKey());
      return null;
   }

   @PluginFactory
   public static RoutingAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("ignoreExceptions") String ignore, @PluginElement("Routes") Routes routes, @PluginConfiguration Configuration config, @PluginElement("RewritePolicy") RewritePolicy rewritePolicy, @PluginElement("Filters") Filter filter) {
      boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
      if(name == null) {
         LOGGER.error("No name provided for RoutingAppender");
         return null;
      } else if(routes == null) {
         LOGGER.error("No routes defined for RoutingAppender");
         return null;
      } else {
         return new RoutingAppender(name, filter, ignoreExceptions, routes, rewritePolicy, config);
      }
   }
}
