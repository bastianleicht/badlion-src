package joptsimple.util;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public class DateConverter implements ValueConverter {
   private final DateFormat formatter;

   public DateConverter(DateFormat formatter) {
      if(formatter == null) {
         throw new NullPointerException("illegal null formatter");
      } else {
         this.formatter = formatter;
      }
   }

   public static DateConverter datePattern(String pattern) {
      SimpleDateFormat formatter = new SimpleDateFormat(pattern);
      formatter.setLenient(false);
      return new DateConverter(formatter);
   }

   public Date convert(String value) {
      ParsePosition position = new ParsePosition(0);
      Date date = this.formatter.parse(value, position);
      if(position.getIndex() != value.length()) {
         throw new ValueConversionException(this.message(value));
      } else {
         return date;
      }
   }

   public Class valueType() {
      return Date.class;
   }

   public String valuePattern() {
      return this.formatter instanceof SimpleDateFormat?((SimpleDateFormat)this.formatter).toPattern():"";
   }

   private String message(String value) {
      String message = "Value [" + value + "] does not match date/time pattern";
      if(this.formatter instanceof SimpleDateFormat) {
         message = message + " [" + ((SimpleDateFormat)this.formatter).toPattern() + ']';
      }

      return message;
   }
}
