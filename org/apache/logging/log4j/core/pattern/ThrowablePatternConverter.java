package org.apache.logging.log4j.core.pattern;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.helpers.Constants;
import org.apache.logging.log4j.core.impl.ThrowableFormatOptions;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(
   name = "ThrowablePatternConverter",
   category = "Converter"
)
@ConverterKeys({"ex", "throwable", "exception"})
public class ThrowablePatternConverter extends LogEventPatternConverter {
   private String rawOption;
   protected final ThrowableFormatOptions options;

   protected ThrowablePatternConverter(String name, String style, String[] options) {
      super(name, style);
      this.options = ThrowableFormatOptions.newInstance(options);
      if(options != null && options.length > 0) {
         this.rawOption = options[0];
      }

   }

   public static ThrowablePatternConverter newInstance(String[] options) {
      return new ThrowablePatternConverter("Throwable", "throwable", options);
   }

   public void format(LogEvent event, StringBuilder buffer) {
      Throwable t = event.getThrown();
      if(this.isSubShortOption()) {
         this.formatSubShortOption(t, buffer);
      } else if(t != null && this.options.anyLines()) {
         this.formatOption(t, buffer);
      }

   }

   private boolean isSubShortOption() {
      return "short.message".equalsIgnoreCase(this.rawOption) || "short.localizedMessage".equalsIgnoreCase(this.rawOption) || "short.fileName".equalsIgnoreCase(this.rawOption) || "short.lineNumber".equalsIgnoreCase(this.rawOption) || "short.methodName".equalsIgnoreCase(this.rawOption) || "short.className".equalsIgnoreCase(this.rawOption);
   }

   private void formatSubShortOption(Throwable t, StringBuilder buffer) {
      StackTraceElement throwingMethod = null;
      if(t != null) {
         StackTraceElement[] trace = t.getStackTrace();
         if(trace != null && trace.length > 0) {
            throwingMethod = trace[0];
         }
      }

      if(t != null && throwingMethod != null) {
         String toAppend = "";
         if("short.className".equalsIgnoreCase(this.rawOption)) {
            toAppend = throwingMethod.getClassName();
         } else if("short.methodName".equalsIgnoreCase(this.rawOption)) {
            toAppend = throwingMethod.getMethodName();
         } else if("short.lineNumber".equalsIgnoreCase(this.rawOption)) {
            toAppend = String.valueOf(throwingMethod.getLineNumber());
         } else if("short.message".equalsIgnoreCase(this.rawOption)) {
            toAppend = t.getMessage();
         } else if("short.localizedMessage".equalsIgnoreCase(this.rawOption)) {
            toAppend = t.getLocalizedMessage();
         } else if("short.fileName".equalsIgnoreCase(this.rawOption)) {
            toAppend = throwingMethod.getFileName();
         }

         int len = buffer.length();
         if(len > 0 && !Character.isWhitespace(buffer.charAt(len - 1))) {
            buffer.append(" ");
         }

         buffer.append(toAppend);
      }

   }

   private void formatOption(Throwable throwable, StringBuilder buffer) {
      StringWriter w = new StringWriter();
      throwable.printStackTrace(new PrintWriter(w));
      int len = buffer.length();
      if(len > 0 && !Character.isWhitespace(buffer.charAt(len - 1))) {
         buffer.append(' ');
      }

      if(this.options.allLines() && Constants.LINE_SEP.equals(this.options.getSeparator())) {
         buffer.append(w.toString());
      } else {
         StringBuilder sb = new StringBuilder();
         String[] array = w.toString().split(Constants.LINE_SEP);
         int limit = this.options.minLines(array.length) - 1;

         for(int i = 0; i <= limit; ++i) {
            sb.append(array[i]);
            if(i < limit) {
               sb.append(this.options.getSeparator());
            }
         }

         buffer.append(sb.toString());
      }

   }

   public boolean handlesThrowable() {
      return true;
   }
}
