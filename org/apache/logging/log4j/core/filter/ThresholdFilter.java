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
   name = "ThresholdFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
public final class ThresholdFilter extends AbstractFilter {
   private final Level level;

   private ThresholdFilter(Level level, Filter.Result onMatch, Filter.Result onMismatch) {
      super(onMatch, onMismatch);
      this.level = level;
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
      return level.isAtLeastAsSpecificAs(this.level)?this.onMatch:this.onMismatch;
   }

   public String toString() {
      return this.level.toString();
   }

   @PluginFactory
   public static ThresholdFilter createFilter(@PluginAttribute("level") String levelName, @PluginAttribute("onMatch") String match, @PluginAttribute("onMismatch") String mismatch) {
      Level level = Level.toLevel(levelName, Level.ERROR);
      Filter.Result onMatch = Filter.Result.toResult(match, Filter.Result.NEUTRAL);
      Filter.Result onMismatch = Filter.Result.toResult(mismatch, Filter.Result.DENY);
      return new ThresholdFilter(level, onMatch, onMismatch);
   }
}
