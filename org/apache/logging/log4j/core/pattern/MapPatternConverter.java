package org.apache.logging.log4j.core.pattern;

import java.util.Map;
import java.util.TreeSet;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.message.MapMessage;

@Plugin(
   name = "MapPatternConverter",
   category = "Converter"
)
@ConverterKeys({"K", "map", "MAP"})
public final class MapPatternConverter extends LogEventPatternConverter {
   private final String key;

   private MapPatternConverter(String[] options) {
      super(options != null && options.length > 0?"MAP{" + options[0] + "}":"MAP", "map");
      this.key = options != null && options.length > 0?options[0]:null;
   }

   public static MapPatternConverter newInstance(String[] options) {
      return new MapPatternConverter(options);
   }

   public void format(LogEvent event, StringBuilder toAppendTo) {
      if(event.getMessage() instanceof MapMessage) {
         MapMessage msg = (MapMessage)event.getMessage();
         Map map = msg.getData();
         if(this.key == null) {
            if(map.size() == 0) {
               toAppendTo.append("{}");
               return;
            }

            StringBuilder sb = new StringBuilder("{");

            for(String key : new TreeSet(map.keySet())) {
               if(sb.length() > 1) {
                  sb.append(", ");
               }

               sb.append(key).append("=").append((String)map.get(key));
            }

            sb.append("}");
            toAppendTo.append(sb);
         } else {
            String val = (String)map.get(this.key);
            if(val != null) {
               toAppendTo.append(val);
            }
         }

      }
   }
}
