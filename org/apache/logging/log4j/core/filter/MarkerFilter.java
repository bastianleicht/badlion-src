package org.apache.logging.log4j.core.filter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

@Plugin(
   name = "MarkerFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
public final class MarkerFilter extends AbstractFilter {
   private final String name;

   private MarkerFilter(String name, Filter.Result onMatch, Filter.Result onMismatch) {
      super(onMatch, onMismatch);
      this.name = name;
   }

   public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
      return this.filter(marker);
   }

   public Filter.Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
      return this.filter(marker);
   }

   public Filter.Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
      return this.filter(marker);
   }

   public Filter.Result filter(LogEvent event) {
      return this.filter(event.getMarker());
   }

   private Filter.Result filter(Marker marker) {
      return marker != null && marker.isInstanceOf(this.name)?this.onMatch:this.onMismatch;
   }

   public String toString() {
      return this.name;
   }

   @PluginFactory
   public static MarkerFilter createFilter(@PluginAttribute("marker") String marker, @PluginAttribute("onMatch") String match, @PluginAttribute("onMismatch") String mismatch) {
      if(marker == null) {
         LOGGER.error("A marker must be provided for MarkerFilter");
         return null;
      } else {
         Filter.Result onMatch = Filter.Result.toResult(match);
         Filter.Result onMismatch = Filter.Result.toResult(mismatch);
         return new MarkerFilter(marker, onMatch, onMismatch);
      }
   }
}
