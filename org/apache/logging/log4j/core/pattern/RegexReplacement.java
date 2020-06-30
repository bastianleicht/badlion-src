package org.apache.logging.log4j.core.pattern;

import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "replace",
   category = "Core",
   printObject = true
)
public final class RegexReplacement {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final Pattern pattern;
   private final String substitution;

   private RegexReplacement(Pattern pattern, String substitution) {
      this.pattern = pattern;
      this.substitution = substitution;
   }

   public String format(String msg) {
      return this.pattern.matcher(msg).replaceAll(this.substitution);
   }

   public String toString() {
      return "replace(regex=" + this.pattern.pattern() + ", replacement=" + this.substitution + ")";
   }

   @PluginFactory
   public static RegexReplacement createRegexReplacement(@PluginAttribute("regex") String regex, @PluginAttribute("replacement") String replacement) {
      if(regex == null) {
         LOGGER.error("A regular expression is required for replacement");
         return null;
      } else {
         if(replacement == null) {
            LOGGER.error("A replacement string is required to perform replacement");
         }

         Pattern p = Pattern.compile(regex);
         return new RegexReplacement(p, replacement);
      }
   }
}
