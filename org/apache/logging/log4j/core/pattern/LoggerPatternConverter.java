package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.NamePatternConverter;

@Plugin(
   name = "LoggerPatternConverter",
   category = "Converter"
)
@ConverterKeys({"c", "logger"})
public final class LoggerPatternConverter extends NamePatternConverter {
   private static final LoggerPatternConverter INSTANCE = new LoggerPatternConverter((String[])null);

   private LoggerPatternConverter(String[] options) {
      super("Logger", "logger", options);
   }

   public static LoggerPatternConverter newInstance(String[] options) {
      return options != null && options.length != 0?new LoggerPatternConverter(options):INSTANCE;
   }

   public void format(LogEvent event, StringBuilder toAppendTo) {
      toAppendTo.append(this.abbreviate(event.getLoggerName()));
   }
}
