package org.apache.logging.log4j.core.pattern;

import java.util.UUID;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.helpers.UUIDUtil;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(
   name = "UUIDPatternConverter",
   category = "Converter"
)
@ConverterKeys({"u", "uuid"})
public final class UUIDPatternConverter extends LogEventPatternConverter {
   private final boolean isRandom;

   private UUIDPatternConverter(boolean isRandom) {
      super("u", "uuid");
      this.isRandom = isRandom;
   }

   public static UUIDPatternConverter newInstance(String[] options) {
      if(options.length == 0) {
         return new UUIDPatternConverter(false);
      } else {
         if(options.length > 1 || !options[0].equalsIgnoreCase("RANDOM") && !options[0].equalsIgnoreCase("Time")) {
            LOGGER.error("UUID Pattern Converter only accepts a single option with the value \"RANDOM\" or \"TIME\"");
         }

         return new UUIDPatternConverter(options[0].equalsIgnoreCase("RANDOM"));
      }
   }

   public void format(LogEvent event, StringBuilder toAppendTo) {
      UUID uuid = this.isRandom?UUID.randomUUID():UUIDUtil.getTimeBasedUUID();
      toAppendTo.append(uuid.toString());
   }
}
