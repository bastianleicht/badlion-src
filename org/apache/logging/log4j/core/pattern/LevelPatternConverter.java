package org.apache.logging.log4j.core.pattern;

import java.util.EnumMap;
import java.util.Locale;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(
   name = "LevelPatternConverter",
   category = "Converter"
)
@ConverterKeys({"p", "level"})
public final class LevelPatternConverter extends LogEventPatternConverter {
   private static final String OPTION_LENGTH = "length";
   private static final String OPTION_LOWER = "lowerCase";
   private static final LevelPatternConverter INSTANCE = new LevelPatternConverter((EnumMap)null);
   private final EnumMap levelMap;

   private LevelPatternConverter(EnumMap map) {
      super("Level", "level");
      this.levelMap = map;
   }

   public static LevelPatternConverter newInstance(String[] options) {
      if(options != null && options.length != 0) {
         EnumMap<Level, String> levelMap = new EnumMap(Level.class);
         int length = Integer.MAX_VALUE;
         boolean lowerCase = false;
         String[] definitions = options[0].split(",");

         for(String def : definitions) {
            String[] pair = def.split("=");
            if(pair != null && pair.length == 2) {
               String key = pair[0].trim();
               String value = pair[1].trim();
               if("length".equalsIgnoreCase(key)) {
                  length = Integer.parseInt(value);
               } else if("lowerCase".equalsIgnoreCase(key)) {
                  lowerCase = Boolean.parseBoolean(value);
               } else {
                  Level level = Level.toLevel(key, (Level)null);
                  if(level == null) {
                     LOGGER.error("Invalid Level {}", new Object[]{key});
                  } else {
                     levelMap.put(level, value);
                  }
               }
            } else {
               LOGGER.error("Invalid option {}", new Object[]{def});
            }
         }

         if(levelMap.size() == 0 && length == Integer.MAX_VALUE && !lowerCase) {
            return INSTANCE;
         } else {
            for(Level level : Level.values()) {
               if(!levelMap.containsKey(level)) {
                  String left = left(level, length);
                  levelMap.put(level, lowerCase?left.toLowerCase(Locale.US):left);
               }
            }

            return new LevelPatternConverter(levelMap);
         }
      } else {
         return INSTANCE;
      }
   }

   private static String left(Level level, int length) {
      String string = level.toString();
      return length >= string.length()?string:string.substring(0, length);
   }

   public void format(LogEvent event, StringBuilder output) {
      output.append(this.levelMap == null?event.getLevel().toString():(String)this.levelMap.get(event.getLevel()));
   }

   public String getStyleClass(Object e) {
      if(e instanceof LogEvent) {
         Level level = ((LogEvent)e).getLevel();
         switch(level) {
         case TRACE:
            return "level trace";
         case DEBUG:
            return "level debug";
         case INFO:
            return "level info";
         case WARN:
            return "level warn";
         case ERROR:
            return "level error";
         case FATAL:
            return "level fatal";
         default:
            return "level " + ((LogEvent)e).getLevel().toString();
         }
      } else {
         return "level";
      }
   }
}
