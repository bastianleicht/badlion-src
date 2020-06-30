package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(
   name = "NDCPatternConverter",
   category = "Converter"
)
@ConverterKeys({"x", "NDC"})
public final class NDCPatternConverter extends LogEventPatternConverter {
   private static final NDCPatternConverter INSTANCE = new NDCPatternConverter();

   private NDCPatternConverter() {
      super("NDC", "ndc");
   }

   public static NDCPatternConverter newInstance(String[] options) {
      return INSTANCE;
   }

   public void format(LogEvent event, StringBuilder toAppendTo) {
      toAppendTo.append(event.getContextStack());
   }
}
