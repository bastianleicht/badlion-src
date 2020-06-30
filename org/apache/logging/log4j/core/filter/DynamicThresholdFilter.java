package org.apache.logging.log4j.core.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.helpers.KeyValuePair;
import org.apache.logging.log4j.message.Message;

@Plugin(
   name = "DynamicThresholdFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
public final class DynamicThresholdFilter extends AbstractFilter {
   private Map levelMap = new HashMap();
   private Level defaultThreshold = Level.ERROR;
   private final String key;

   private DynamicThresholdFilter(String key, Map pairs, Level defaultLevel, Filter.Result onMatch, Filter.Result onMismatch) {
      super(onMatch, onMismatch);
      if(key == null) {
         throw new NullPointerException("key cannot be null");
      } else {
         this.key = key;
         this.levelMap = pairs;
         this.defaultThreshold = defaultLevel;
      }
   }

   public String getKey() {
      return this.key;
   }

   public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
      return this.filter(level);
   }

   public Filter.Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
      return this.filter(level);
   }

   public Filter.Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
      return this.filter(level);
   }

   public Filter.Result filter(LogEvent event) {
      return this.filter(event.getLevel());
   }

   private Filter.Result filter(Level level) {
      Object value = ThreadContext.get(this.key);
      if(value != null) {
         Level ctxLevel = (Level)this.levelMap.get(value);
         if(ctxLevel == null) {
            ctxLevel = this.defaultThreshold;
         }

         return level.isAtLeastAsSpecificAs(ctxLevel)?this.onMatch:this.onMismatch;
      } else {
         return Filter.Result.NEUTRAL;
      }
   }

   public Map getLevelMap() {
      return this.levelMap;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("key=").append(this.key);
      sb.append(", default=").append(this.defaultThreshold);
      if(this.levelMap.size() > 0) {
         sb.append("{");
         boolean first = true;

         for(Entry<String, Level> entry : this.levelMap.entrySet()) {
            if(!first) {
               sb.append(", ");
               first = false;
            }

            sb.append((String)entry.getKey()).append("=").append(entry.getValue());
         }

         sb.append("}");
      }

      return sb.toString();
   }

   @PluginFactory
   public static DynamicThresholdFilter createFilter(@PluginAttribute("key") String key, @PluginElement("Pairs") KeyValuePair[] pairs, @PluginAttribute("defaultThreshold") String levelName, @PluginAttribute("onMatch") String match, @PluginAttribute("onMismatch") String mismatch) {
      Filter.Result onMatch = Filter.Result.toResult(match);
      Filter.Result onMismatch = Filter.Result.toResult(mismatch);
      Map<String, Level> map = new HashMap();

      for(KeyValuePair pair : pairs) {
         map.put(pair.getKey(), Level.toLevel(pair.getValue()));
      }

      Level level = Level.toLevel(levelName, Level.ERROR);
      return new DynamicThresholdFilter(key, map, level, onMatch, onMismatch);
   }
}
