package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(
   name = "ThreadPatternConverter",
   category = "Converter"
)
@ConverterKeys({"t", "thread"})
public final class ThreadPatternConverter extends LogEventPatternConverter {
   private static final ThreadPatternConverter INSTANCE = new ThreadPatternConverter();

   private ThreadPatternConverter() {
      super("Thread", "thread");
   }

   public static ThreadPatternConverter newInstance(String[] options) {
      return INSTANCE;
   }

   public void format(LogEvent event, StringBuilder toAppendTo) {
      toAppendTo.append(event.getThreadName());
   }
}
