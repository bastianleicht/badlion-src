package org.apache.logging.log4j.util;

import java.util.Locale;

public final class EnglishEnums {
   public static Enum valueOf(Class enumType, String name) {
      return valueOf(enumType, name, (Enum)null);
   }

   public static Enum valueOf(Class enumType, String name, Enum defaultValue) {
      return name == null?defaultValue:Enum.valueOf(enumType, name.toUpperCase(Locale.ENGLISH));
   }
}
