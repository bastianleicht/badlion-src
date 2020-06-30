package org.apache.logging.log4j.core.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
   name = "RegexFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
public final class RegexFilter extends AbstractFilter {
   private final Pattern pattern;
   private final boolean useRawMessage;

   private RegexFilter(boolean raw, Pattern pattern, Filter.Result onMatch, Filter.Result onMismatch) {
      super(onMatch, onMismatch);
      this.pattern = pattern;
      this.useRawMessage = raw;
   }

   public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
      return this.filter(msg);
   }

   public Filter.Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
      return msg == null?this.onMismatch:this.filter(msg.toString());
   }

   public Filter.Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
      if(msg == null) {
         return this.onMismatch;
      } else {
         String text = this.useRawMessage?msg.getFormat():msg.getFormattedMessage();
         return this.filter(text);
      }
   }

   public Filter.Result filter(LogEvent event) {
      String text = this.useRawMessage?event.getMessage().getFormat():event.getMessage().getFormattedMessage();
      return this.filter(text);
   }

   private Filter.Result filter(String msg) {
      if(msg == null) {
         return this.onMismatch;
      } else {
         Matcher m = this.pattern.matcher(msg);
         return m.matches()?this.onMatch:this.onMismatch;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("useRaw=").append(this.useRawMessage);
      sb.append(", pattern=").append(this.pattern.toString());
      return sb.toString();
   }

   @PluginFactory
   public static RegexFilter createFilter(@PluginAttribute("regex") String regex, @PluginAttribute("useRawMsg") String useRawMsg, @PluginAttribute("onMatch") String match, @PluginAttribute("onMismatch") String mismatch) {
      if(regex == null) {
         LOGGER.error("A regular expression must be provided for RegexFilter");
         return null;
      } else {
         boolean raw = Boolean.parseBoolean(useRawMsg);

         Pattern pattern;
         try {
            pattern = Pattern.compile(regex);
         } catch (Exception var8) {
            LOGGER.error("RegexFilter caught exception compiling pattern: " + regex + " cause: " + var8.getMessage());
            return null;
         }

         Filter.Result onMatch = Filter.Result.toResult(match);
         Filter.Result onMismatch = Filter.Result.toResult(mismatch);
         return new RegexFilter(raw, pattern, onMatch, onMismatch);
      }
   }
}
