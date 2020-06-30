package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(
   name = "FullLocationPatternConverter",
   category = "Converter"
)
@ConverterKeys({"l", "location"})
public final class FullLocationPatternConverter extends LogEventPatternConverter {
   private static final FullLocationPatternConverter INSTANCE = new FullLocationPatternConverter();

   private FullLocationPatternConverter() {
      super("Full Location", "fullLocation");
   }

   public static FullLocationPatternConverter newInstance(String[] options) {
      return INSTANCE;
   }

   public void format(LogEvent event, StringBuilder output) {
      StackTraceElement element = event.getSource();
      if(element != null) {
         output.append(element.toString());
      }

   }
}
