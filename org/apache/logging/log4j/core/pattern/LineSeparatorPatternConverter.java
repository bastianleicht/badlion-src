package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.helpers.Constants;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(
   name = "LineSeparatorPatternConverter",
   category = "Converter"
)
@ConverterKeys({"n"})
public final class LineSeparatorPatternConverter extends LogEventPatternConverter {
   private static final LineSeparatorPatternConverter INSTANCE = new LineSeparatorPatternConverter();
   private final String lineSep = Constants.LINE_SEP;

   private LineSeparatorPatternConverter() {
      super("Line Sep", "lineSep");
   }

   public static LineSeparatorPatternConverter newInstance(String[] options) {
      return INSTANCE;
   }

   public void format(LogEvent event, StringBuilder toAppendTo) {
      toAppendTo.append(this.lineSep);
   }
}
