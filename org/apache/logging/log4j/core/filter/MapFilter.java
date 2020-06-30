package org.apache.logging.log4j.core.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.helpers.KeyValuePair;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.message.Message;

@Plugin(
   name = "MapFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
public class MapFilter extends AbstractFilter {
   private final Map map;
   private final boolean isAnd;

   protected MapFilter(Map map, boolean oper, Filter.Result onMatch, Filter.Result onMismatch) {
      super(onMatch, onMismatch);
      if(map == null) {
         throw new NullPointerException("key cannot be null");
      } else {
         this.isAnd = oper;
         this.map = map;
      }
   }

   public Filter.Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
      return msg instanceof MapMessage?(this.filter(((MapMessage)msg).getData())?this.onMatch:this.onMismatch):Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(LogEvent event) {
      Message msg = event.getMessage();
      return msg instanceof MapMessage?(this.filter(((MapMessage)msg).getData())?this.onMatch:this.onMismatch):Filter.Result.NEUTRAL;
   }

   protected boolean filter(Map data) {
      boolean match = false;

      for(Entry<String, List<String>> entry : this.map.entrySet()) {
         String toMatch = (String)data.get(entry.getKey());
         if(toMatch != null) {
            match = ((List)entry.getValue()).contains(toMatch);
         } else {
            match = false;
         }

         if(!this.isAnd && match || this.isAnd && !match) {
            break;
         }
      }

      return match;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("isAnd=").append(this.isAnd);
      if(this.map.size() > 0) {
         sb.append(", {");
         boolean first = true;

         for(Entry<String, List<String>> entry : this.map.entrySet()) {
            if(!first) {
               sb.append(", ");
            }

            first = false;
            List<String> list = (List)entry.getValue();
            String value = list.size() > 1?(String)list.get(0):list.toString();
            sb.append((String)entry.getKey()).append("=").append(value);
         }

         sb.append("}");
      }

      return sb.toString();
   }

   protected boolean isAnd() {
      return this.isAnd;
   }

   protected Map getMap() {
      return this.map;
   }

   @PluginFactory
   public static MapFilter createFilter(@PluginElement("Pairs") KeyValuePair[] pairs, @PluginAttribute("operator") String oper, @PluginAttribute("onMatch") String match, @PluginAttribute("onMismatch") String mismatch) {
      if(pairs != null && pairs.length != 0) {
         Map<String, List<String>> map = new HashMap();

         for(KeyValuePair pair : pairs) {
            String key = pair.getKey();
            if(key == null) {
               LOGGER.error("A null key is not valid in MapFilter");
            } else {
               String value = pair.getValue();
               if(value == null) {
                  LOGGER.error("A null value for key " + key + " is not allowed in MapFilter");
               } else {
                  List<String> list = (List)map.get(pair.getKey());
                  if(list != null) {
                     list.add(value);
                  } else {
                     ArrayList var15 = new ArrayList();
                     var15.add(value);
                     map.put(pair.getKey(), var15);
                  }
               }
            }
         }

         if(map.size() == 0) {
            LOGGER.error("MapFilter is not configured with any valid key value pairs");
            return null;
         } else {
            boolean isAnd = oper == null || !oper.equalsIgnoreCase("or");
            Filter.Result onMatch = Filter.Result.toResult(match);
            Filter.Result onMismatch = Filter.Result.toResult(mismatch);
            return new MapFilter(map, isAnd, onMatch, onMismatch);
         }
      } else {
         LOGGER.error("keys and values must be specified for the MapFilter");
         return null;
      }
   }
}
