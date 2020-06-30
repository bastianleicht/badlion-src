package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

@Plugin(
   name = "sys",
   category = "Lookup"
)
public class SystemPropertiesLookup implements StrLookup {
   public String lookup(String key) {
      try {
         return System.getProperty(key);
      } catch (Exception var3) {
         return null;
      }
   }

   public String lookup(LogEvent event, String key) {
      try {
         return System.getProperty(key);
      } catch (Exception var4) {
         return null;
      }
   }
}
