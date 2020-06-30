package org.apache.logging.log4j.core.pattern;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ArrayPatternConverter;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(
   name = "DatePatternConverter",
   category = "Converter"
)
@ConverterKeys({"d", "date"})
public final class DatePatternConverter extends LogEventPatternConverter implements ArrayPatternConverter {
   private static final String ABSOLUTE_FORMAT = "ABSOLUTE";
   private static final String COMPACT_FORMAT = "COMPACT";
   private static final String ABSOLUTE_TIME_PATTERN = "HH:mm:ss,SSS";
   private static final String DATE_AND_TIME_FORMAT = "DATE";
   private static final String DATE_AND_TIME_PATTERN = "dd MMM yyyy HH:mm:ss,SSS";
   private static final String ISO8601_FORMAT = "ISO8601";
   private static final String ISO8601_BASIC_FORMAT = "ISO8601_BASIC";
   private static final String ISO8601_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";
   private static final String ISO8601_BASIC_PATTERN = "yyyyMMdd HHmmss,SSS";
   private static final String COMPACT_PATTERN = "yyyyMMddHHmmssSSS";
   private String cachedDate;
   private long lastTimestamp;
   private final SimpleDateFormat simpleFormat;

   private DatePatternConverter(String[] options) {
      super("Date", "date");
      String patternOption;
      if(options != null && options.length != 0) {
         patternOption = options[0];
      } else {
         patternOption = null;
      }

      String pattern;
      if(patternOption != null && !patternOption.equalsIgnoreCase("ISO8601")) {
         if(patternOption.equalsIgnoreCase("ISO8601_BASIC")) {
            pattern = "yyyyMMdd HHmmss,SSS";
         } else if(patternOption.equalsIgnoreCase("ABSOLUTE")) {
            pattern = "HH:mm:ss,SSS";
         } else if(patternOption.equalsIgnoreCase("DATE")) {
            pattern = "dd MMM yyyy HH:mm:ss,SSS";
         } else if(patternOption.equalsIgnoreCase("COMPACT")) {
            pattern = "yyyyMMddHHmmssSSS";
         } else {
            pattern = patternOption;
         }
      } else {
         pattern = "yyyy-MM-dd HH:mm:ss,SSS";
      }

      SimpleDateFormat tempFormat;
      try {
         tempFormat = new SimpleDateFormat(pattern);
      } catch (IllegalArgumentException var6) {
         LOGGER.warn((String)("Could not instantiate SimpleDateFormat with pattern " + patternOption), (Throwable)var6);
         tempFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
      }

      if(options != null && options.length > 1) {
         TimeZone tz = TimeZone.getTimeZone(options[1]);
         tempFormat.setTimeZone(tz);
      }

      this.simpleFormat = tempFormat;
   }

   public static DatePatternConverter newInstance(String[] options) {
      return new DatePatternConverter(options);
   }

   public void format(LogEvent event, StringBuilder output) {
      long timestamp = event.getMillis();
      synchronized(this) {
         if(timestamp != this.lastTimestamp) {
            this.lastTimestamp = timestamp;
            this.cachedDate = this.simpleFormat.format(Long.valueOf(timestamp));
         }
      }

      output.append(this.cachedDate);
   }

   public void format(StringBuilder toAppendTo, Object... objects) {
      for(Object obj : objects) {
         if(obj instanceof Date) {
            this.format(obj, toAppendTo);
            break;
         }
      }

   }

   public void format(Object obj, StringBuilder output) {
      if(obj instanceof Date) {
         this.format((Date)obj, output);
      }

      super.format(obj, output);
   }

   public void format(Date date, StringBuilder toAppendTo) {
      synchronized(this) {
         toAppendTo.append(this.simpleFormat.format(Long.valueOf(date.getTime())));
      }
   }

   public String getPattern() {
      return this.simpleFormat.toPattern();
   }
}
