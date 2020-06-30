package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.DatePatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;

@Plugin(
   name = "FileDatePatternConverter",
   category = "FileConverter"
)
@ConverterKeys({"d", "date"})
public final class FileDatePatternConverter {
   public static PatternConverter newInstance(String[] options) {
      return options != null && options.length != 0?DatePatternConverter.newInstance(options):DatePatternConverter.newInstance(new String[]{"yyyy-MM-dd"});
   }
}
