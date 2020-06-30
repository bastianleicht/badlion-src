package org.apache.logging.log4j.core.lookup;

import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.apache.logging.log4j.message.MapMessage;

@Plugin(
   name = "map",
   category = "Lookup"
)
public class MapLookup implements StrLookup {
   private final Map map;

   public MapLookup(Map map) {
      this.map = map;
   }

   public MapLookup() {
      this.map = null;
   }

   public String lookup(String key) {
      if(this.map == null) {
         return null;
      } else {
         String obj = (String)this.map.get(key);
         return obj == null?null:obj;
      }
   }

   public String lookup(LogEvent event, String key) {
      if(this.map == null && !(event.getMessage() instanceof MapMessage)) {
         return null;
      } else {
         if(this.map != null && this.map.containsKey(key)) {
            String obj = (String)this.map.get(key);
            if(obj != null) {
               return obj;
            }
         }

         return event.getMessage() instanceof MapMessage?((MapMessage)event.getMessage()).get(key):null;
      }
   }
}
