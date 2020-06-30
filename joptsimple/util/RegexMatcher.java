package joptsimple.util;

import java.util.regex.Pattern;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public class RegexMatcher implements ValueConverter {
   private final Pattern pattern;

   public RegexMatcher(String pattern, int flags) {
      this.pattern = Pattern.compile(pattern, flags);
   }

   public static ValueConverter regex(String pattern) {
      return new RegexMatcher(pattern, 0);
   }

   public String convert(String value) {
      if(!this.pattern.matcher(value).matches()) {
         throw new ValueConversionException("Value [" + value + "] did not match regex [" + this.pattern.pattern() + ']');
      } else {
         return value;
      }
   }

   public Class valueType() {
      return String.class;
   }

   public String valuePattern() {
      return this.pattern.pattern();
   }
}
