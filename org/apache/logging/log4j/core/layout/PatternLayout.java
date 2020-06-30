package org.apache.logging.log4j.core.layout;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Booleans;
import org.apache.logging.log4j.core.helpers.Charsets;
import org.apache.logging.log4j.core.helpers.OptionConverter;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.core.pattern.RegexReplacement;

@Plugin(
   name = "PatternLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class PatternLayout extends AbstractStringLayout {
   public static final String DEFAULT_CONVERSION_PATTERN = "%m%n";
   public static final String TTCC_CONVERSION_PATTERN = "%r [%t] %p %c %x - %m%n";
   public static final String SIMPLE_CONVERSION_PATTERN = "%d [%t] %p %c - %m%n";
   public static final String KEY = "Converter";
   private List formatters;
   private final String conversionPattern;
   private final Configuration config;
   private final RegexReplacement replace;
   private final boolean alwaysWriteExceptions;

   private PatternLayout(Configuration config, RegexReplacement replace, String pattern, Charset charset, boolean alwaysWriteExceptions) {
      super(charset);
      this.replace = replace;
      this.conversionPattern = pattern;
      this.config = config;
      this.alwaysWriteExceptions = alwaysWriteExceptions;
      PatternParser parser = createPatternParser(config);
      this.formatters = parser.parse(pattern == null?"%m%n":pattern, this.alwaysWriteExceptions);
   }

   public void setConversionPattern(String conversionPattern) {
      String pattern = OptionConverter.convertSpecialChars(conversionPattern);
      if(pattern != null) {
         PatternParser parser = createPatternParser(this.config);
         this.formatters = parser.parse(pattern, this.alwaysWriteExceptions);
      }
   }

   public String getConversionPattern() {
      return this.conversionPattern;
   }

   public Map getContentFormat() {
      Map<String, String> result = new HashMap();
      result.put("structured", "false");
      result.put("formatType", "conversion");
      result.put("format", this.conversionPattern);
      return result;
   }

   public String toSerializable(LogEvent event) {
      StringBuilder buf = new StringBuilder();

      for(PatternFormatter formatter : this.formatters) {
         formatter.format(event, buf);
      }

      String str = buf.toString();
      if(this.replace != null) {
         str = this.replace.format(str);
      }

      return str;
   }

   public static PatternParser createPatternParser(Configuration config) {
      if(config == null) {
         return new PatternParser(config, "Converter", LogEventPatternConverter.class);
      } else {
         PatternParser parser = (PatternParser)config.getComponent("Converter");
         if(parser == null) {
            parser = new PatternParser(config, "Converter", LogEventPatternConverter.class);
            config.addComponent("Converter", parser);
            parser = (PatternParser)config.getComponent("Converter");
         }

         return parser;
      }
   }

   public String toString() {
      return this.conversionPattern;
   }

   @PluginFactory
   public static PatternLayout createLayout(@PluginAttribute("pattern") String pattern, @PluginConfiguration Configuration config, @PluginElement("Replace") RegexReplacement replace, @PluginAttribute("charset") String charsetName, @PluginAttribute("alwaysWriteExceptions") String always) {
      Charset charset = Charsets.getSupportedCharset(charsetName);
      boolean alwaysWriteExceptions = Booleans.parseBoolean(always, true);
      return new PatternLayout(config, replace, pattern == null?"%m%n":pattern, charset, alwaysWriteExceptions);
   }
}
