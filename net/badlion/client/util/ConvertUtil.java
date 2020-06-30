package net.badlion.client.util;

public class ConvertUtil {
   public static boolean isPositiveInteger(String text) {
      try {
         int i = Integer.parseInt(text);
         return i > 0;
      } catch (NumberFormatException var2) {
         return false;
      }
   }
}
