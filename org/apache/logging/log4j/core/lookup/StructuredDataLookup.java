package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.apache.logging.log4j.message.StructuredDataMessage;

@Plugin(
   name = "sd",
   category = "Lookup"
)
public class StructuredDataLookup implements StrLookup {
   public String lookup(String key) {
      return null;
   }

   public String lookup(LogEvent event, String key) {
      if(event != null && event.getMessage() instanceof StructuredDataMessage) {
         StructuredDataMessage msg = (StructuredDataMessage)event.getMessage();
         return key.equalsIgnoreCase("id")?msg.getId().getName():(key.equalsIgnoreCase("type")?msg.getType():msg.get(key));
      } else {
         return null;
      }
   }
}
