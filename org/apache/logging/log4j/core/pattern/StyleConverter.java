package org.apache.logging.log4j.core.pattern;

import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.AnsiEscape;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;

@Plugin(
   name = "style",
   category = "Converter"
)
@ConverterKeys({"style"})
public final class StyleConverter extends LogEventPatternConverter {
   private final List patternFormatters;
   private final String style;

   private StyleConverter(List patternFormatters, String style) {
      super("style", "style");
      this.patternFormatters = patternFormatters;
      this.style = style;
   }

   public static StyleConverter newInstance(Configuration config, String[] options) {
      if(options.length < 1) {
         LOGGER.error("Incorrect number of options on style. Expected at least 1, received " + options.length);
         return null;
      } else if(options[0] == null) {
         LOGGER.error("No pattern supplied on style");
         return null;
      } else if(options[1] == null) {
         LOGGER.error("No style attributes provided");
         return null;
      } else {
         PatternParser parser = PatternLayout.createPatternParser(config);
         List<PatternFormatter> formatters = parser.parse(options[0]);
         String style = AnsiEscape.createSequence(options[1].split("\\s*,\\s*"));
         return new StyleConverter(formatters, style);
      }
   }

   public void format(LogEvent event, StringBuilder toAppendTo) {
      StringBuilder buf = new StringBuilder();

      for(PatternFormatter formatter : this.patternFormatters) {
         formatter.format(event, buf);
      }

      if(buf.length() > 0) {
         toAppendTo.append(this.style).append(buf.toString()).append(AnsiEscape.getDefaultStyle());
      }

   }

   public boolean handlesThrowable() {
      for(PatternFormatter formatter : this.patternFormatters) {
         if(formatter.handlesThrowable()) {
            return true;
         }
      }

      return false;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(super.toString());
      sb.append("[style=");
      sb.append(this.style);
      sb.append(", patternFormatters=");
      sb.append(this.patternFormatters);
      sb.append("]");
      return sb.toString();
   }
}
