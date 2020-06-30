package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.pattern.AbstractPatternConverter;

public abstract class LogEventPatternConverter extends AbstractPatternConverter {
   protected LogEventPatternConverter(String name, String style) {
      super(name, style);
   }

   public abstract void format(LogEvent var1, StringBuilder var2);

   public void format(Object obj, StringBuilder output) {
      if(obj instanceof LogEvent) {
         this.format((LogEvent)obj, output);
      }

   }

   public boolean handlesThrowable() {
      return false;
   }
}
