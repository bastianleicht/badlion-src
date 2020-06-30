package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(
   name = "FileLocationPatternConverter",
   category = "Converter"
)
@ConverterKeys({"F", "file"})
public final class FileLocationPatternConverter extends LogEventPatternConverter {
   private static final FileLocationPatternConverter INSTANCE = new FileLocationPatternConverter();

   private FileLocationPatternConverter() {
      super("File Location", "file");
   }

   public static FileLocationPatternConverter newInstance(String[] options) {
      return INSTANCE;
   }

   public void format(LogEvent event, StringBuilder output) {
      StackTraceElement element = event.getSource();
      if(element != null) {
         output.append(element.getFileName());
      }

   }
}
