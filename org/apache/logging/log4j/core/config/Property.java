package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginValue;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "property",
   category = "Core",
   printObject = true
)
public final class Property {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final String name;
   private final String value;

   private Property(String name, String value) {
      this.name = name;
      this.value = value;
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   @PluginFactory
   public static Property createProperty(@PluginAttribute("name") String key, @PluginValue("value") String value) {
      if(key == null) {
         LOGGER.error("Property key cannot be null");
      }

      return new Property(key, value);
   }

   public String toString() {
      return this.name + "=" + this.value;
   }
}
