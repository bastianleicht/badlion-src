package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.pattern.ArrayPatternConverter;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

public final class LiteralPatternConverter extends LogEventPatternConverter implements ArrayPatternConverter {
   private final String literal;
   private final Configuration config;
   private final boolean substitute;

   public LiteralPatternConverter(Configuration config, String literal) {
      super("Literal", "literal");
      this.literal = literal;
      this.config = config;
      this.substitute = config != null && literal.contains("${");
   }

   public void format(LogEvent event, StringBuilder toAppendTo) {
      toAppendTo.append(this.substitute?this.config.getStrSubstitutor().replace(event, this.literal):this.literal);
   }

   public void format(Object obj, StringBuilder output) {
      output.append(this.substitute?this.config.getStrSubstitutor().replace(this.literal):this.literal);
   }

   public void format(StringBuilder output, Object... objects) {
      output.append(this.substitute?this.config.getStrSubstitutor().replace(this.literal):this.literal);
   }

   public String getLiteral() {
      return this.literal;
   }
}
