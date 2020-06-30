package org.apache.logging.log4j.core.pattern;

import java.lang.management.ManagementFactory;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(
   name = "RelativeTimePatternConverter",
   category = "Converter"
)
@ConverterKeys({"r", "relative"})
public class RelativeTimePatternConverter extends LogEventPatternConverter {
   private long lastTimestamp = Long.MIN_VALUE;
   private final long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
   private String relative;

   public RelativeTimePatternConverter() {
      super("Time", "time");
   }

   public static RelativeTimePatternConverter newInstance(String[] options) {
      return new RelativeTimePatternConverter();
   }

   public void format(LogEvent event, StringBuilder toAppendTo) {
      long timestamp = event.getMillis();
      synchronized(this) {
         if(timestamp != this.lastTimestamp) {
            this.lastTimestamp = timestamp;
            this.relative = Long.toString(timestamp - this.startTime);
         }
      }

      toAppendTo.append(this.relative);
   }
}
