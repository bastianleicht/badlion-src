package org.apache.logging.log4j.core.appender.rolling;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "SizeBasedTriggeringPolicy",
   category = "Core",
   printObject = true
)
public class SizeBasedTriggeringPolicy implements TriggeringPolicy {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   private static final long KB = 1024L;
   private static final long MB = 1048576L;
   private static final long GB = 1073741824L;
   private static final long MAX_FILE_SIZE = 10485760L;
   private static final Pattern VALUE_PATTERN = Pattern.compile("([0-9]+([\\.,][0-9]+)?)\\s*(|K|M|G)B?", 2);
   private final long maxFileSize;
   private RollingFileManager manager;

   protected SizeBasedTriggeringPolicy() {
      this.maxFileSize = 10485760L;
   }

   protected SizeBasedTriggeringPolicy(long maxFileSize) {
      this.maxFileSize = maxFileSize;
   }

   public void initialize(RollingFileManager manager) {
      this.manager = manager;
   }

   public boolean isTriggeringEvent(LogEvent event) {
      return this.manager.getFileSize() > this.maxFileSize;
   }

   public String toString() {
      return "SizeBasedTriggeringPolicy(size=" + this.maxFileSize + ")";
   }

   @PluginFactory
   public static SizeBasedTriggeringPolicy createPolicy(@PluginAttribute("size") String size) {
      long maxSize = size == null?10485760L:valueOf(size);
      return new SizeBasedTriggeringPolicy(maxSize);
   }

   private static long valueOf(String string) {
      Matcher matcher = VALUE_PATTERN.matcher(string);
      if(matcher.matches()) {
         try {
            long value = NumberFormat.getNumberInstance(Locale.getDefault()).parse(matcher.group(1)).longValue();
            String units = matcher.group(3);
            if(units.equalsIgnoreCase("")) {
               return value;
            } else if(units.equalsIgnoreCase("K")) {
               return value * 1024L;
            } else if(units.equalsIgnoreCase("M")) {
               return value * 1048576L;
            } else if(units.equalsIgnoreCase("G")) {
               return value * 1073741824L;
            } else {
               LOGGER.error("Units not recognized: " + string);
               return 10485760L;
            }
         } catch (ParseException var5) {
            LOGGER.error((String)("Unable to parse numeric part: " + string), (Throwable)var5);
            return 10485760L;
         }
      } else {
         LOGGER.error("Unable to parse bytes: " + string);
         return 10485760L;
      }
   }
}
