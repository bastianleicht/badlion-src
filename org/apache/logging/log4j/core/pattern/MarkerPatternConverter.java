package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(
   name = "MarkerPatternConverter",
   category = "Converter"
)
@ConverterKeys({"marker"})
public final class MarkerPatternConverter extends LogEventPatternConverter {
   private MarkerPatternConverter(String[] options) {
      super("Marker", "marker");
   }

   public static MarkerPatternConverter newInstance(String[] options) {
      return new MarkerPatternConverter(options);
   }

   public void format(LogEvent event, StringBuilder toAppendTo) {
      Marker marker = event.getMarker();
      if(marker != null) {
         toAppendTo.append(marker.toString());
      }

   }
}
