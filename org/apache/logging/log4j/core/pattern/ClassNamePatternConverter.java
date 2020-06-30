package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.NamePatternConverter;

@Plugin(
   name = "ClassNamePatternConverter",
   category = "Converter"
)
@ConverterKeys({"C", "class"})
public final class ClassNamePatternConverter extends NamePatternConverter {
   private static final String NA = "?";

   private ClassNamePatternConverter(String[] options) {
      super("Class Name", "class name", options);
   }

   public static ClassNamePatternConverter newInstance(String[] options) {
      return new ClassNamePatternConverter(options);
   }

   public void format(LogEvent event, StringBuilder toAppendTo) {
      StackTraceElement element = event.getSource();
      if(element == null) {
         toAppendTo.append("?");
      } else {
         toAppendTo.append(this.abbreviate(element.getClassName()));
      }

   }
}
