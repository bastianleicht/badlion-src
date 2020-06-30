package joptsimple.internal;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class Strings {
   public static final String EMPTY = "";
   public static final String SINGLE_QUOTE = "\'";
   public static final String LINE_SEPARATOR = System.getProperty("line.separator");

   private Strings() {
      throw new UnsupportedOperationException();
   }

   public static String repeat(char ch, int count) {
      StringBuilder buffer = new StringBuilder();

      for(int i = 0; i < count; ++i) {
         buffer.append(ch);
      }

      return buffer.toString();
   }

   public static boolean isNullOrEmpty(String target) {
      return target == null || "".equals(target);
   }

   public static String surround(String target, char begin, char end) {
      return begin + target + end;
   }

   public static String join(String[] pieces, String separator) {
      return join(Arrays.asList(pieces), separator);
   }

   public static String join(List pieces, String separator) {
      StringBuilder buffer = new StringBuilder();
      Iterator<String> iter = pieces.iterator();

      while(iter.hasNext()) {
         buffer.append((String)iter.next());
         if(iter.hasNext()) {
            buffer.append(separator);
         }
      }

      return buffer.toString();
   }
}
