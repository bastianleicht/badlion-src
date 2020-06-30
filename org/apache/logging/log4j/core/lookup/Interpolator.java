package org.apache.logging.log4j.core.lookup;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.PluginManager;
import org.apache.logging.log4j.core.config.plugins.PluginType;
import org.apache.logging.log4j.core.lookup.EnvironmentLookup;
import org.apache.logging.log4j.core.lookup.JndiLookup;
import org.apache.logging.log4j.core.lookup.MapLookup;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.apache.logging.log4j.core.lookup.SystemPropertiesLookup;
import org.apache.logging.log4j.core.lookup.WebLookup;
import org.apache.logging.log4j.status.StatusLogger;

public class Interpolator implements StrLookup {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final char PREFIX_SEPARATOR = ':';
   private final Map lookups = new HashMap();
   private final StrLookup defaultLookup;

   public Interpolator(StrLookup defaultLookup) {
      this.defaultLookup = (StrLookup)(defaultLookup == null?new MapLookup(new HashMap()):defaultLookup);
      PluginManager manager = new PluginManager("Lookup");
      manager.collectPlugins();
      Map<String, PluginType<?>> plugins = manager.getPlugins();

      for(Entry<String, PluginType<?>> entry : plugins.entrySet()) {
         Class<? extends StrLookup> clazz = ((PluginType)entry.getValue()).getPluginClass();

         try {
            this.lookups.put(entry.getKey(), clazz.newInstance());
         } catch (Exception var8) {
            LOGGER.error((String)("Unable to create Lookup for " + (String)entry.getKey()), (Throwable)var8);
         }
      }

   }

   public Interpolator() {
      this.defaultLookup = new MapLookup(new HashMap());
      this.lookups.put("sys", new SystemPropertiesLookup());
      this.lookups.put("env", new EnvironmentLookup());
      this.lookups.put("jndi", new JndiLookup());

      try {
         if(Class.forName("javax.servlet.ServletContext") != null) {
            this.lookups.put("web", new WebLookup());
         }
      } catch (ClassNotFoundException var2) {
         LOGGER.debug("ServletContext not present - WebLookup not added");
      } catch (Exception var3) {
         LOGGER.error((String)"Unable to locate ServletContext", (Throwable)var3);
      }

   }

   public String lookup(String var) {
      return this.lookup((LogEvent)null, var);
   }

   public String lookup(LogEvent event, String var) {
      if(var == null) {
         return null;
      } else {
         int prefixPos = var.indexOf(58);
         if(prefixPos >= 0) {
            String prefix = var.substring(0, prefixPos);
            String name = var.substring(prefixPos + 1);
            StrLookup lookup = (StrLookup)this.lookups.get(prefix);
            String value = null;
            if(lookup != null) {
               value = event == null?lookup.lookup(name):lookup.lookup(event, name);
            }

            if(value != null) {
               return value;
            }

            var = var.substring(prefixPos + 1);
         }

         return this.defaultLookup != null?(event == null?this.defaultLookup.lookup(var):this.defaultLookup.lookup(event, var)):null;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();

      for(String name : this.lookups.keySet()) {
         if(sb.length() == 0) {
            sb.append("{");
         } else {
            sb.append(", ");
         }

         sb.append(name);
      }

      if(sb.length() > 0) {
         sb.append("}");
      }

      return sb.toString();
   }
}
