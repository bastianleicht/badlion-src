package org.apache.logging.log4j.core.config;

import java.io.File;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.JSONConfiguration;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(
   name = "JSONConfigurationFactory",
   category = "ConfigurationFactory"
)
@Order(6)
public class JSONConfigurationFactory extends ConfigurationFactory {
   public static final String[] SUFFIXES = new String[]{".json", ".jsn"};
   private static String[] dependencies = new String[]{"com.fasterxml.jackson.databind.ObjectMapper", "com.fasterxml.jackson.databind.JsonNode", "com.fasterxml.jackson.core.JsonParser"};
   private final File configFile = null;
   private boolean isActive;

   public JSONConfigurationFactory() {
      try {
         for(String item : dependencies) {
            Class.forName(item);
         }
      } catch (ClassNotFoundException var5) {
         LOGGER.debug("Missing dependencies for Json support");
         this.isActive = false;
         return;
      }

      this.isActive = true;
   }

   protected boolean isActive() {
      return this.isActive;
   }

   public Configuration getConfiguration(ConfigurationFactory.ConfigurationSource source) {
      return !this.isActive?null:new JSONConfiguration(source);
   }

   public String[] getSupportedTypes() {
      return SUFFIXES;
   }
}
