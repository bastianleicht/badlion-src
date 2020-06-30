package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

@Plugin(
   name = "env",
   category = "Lookup"
)
public class EnvironmentLookup implements StrLookup {
   public String lookup(String key) {
      return System.getenv(key);
   }

   public String lookup(LogEvent event, String key) {
      return System.getenv(key);
   }
}
