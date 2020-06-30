package org.apache.commons.lang3.text;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public abstract class StrMatcher {
   private static final StrMatcher COMMA_MATCHER = new StrMatcher.CharMatcher(',');
   private static final StrMatcher TAB_MATCHER = new StrMatcher.CharMatcher('\t');
   private static final StrMatcher SPACE_MATCHER = new StrMatcher.CharMatcher(' ');
   private static final StrMatcher SPLIT_MATCHER = new StrMatcher.CharSetMatcher(" \t\n\r\f".toCharArray());
   private static final StrMatcher TRIM_MATCHER = new StrMatcher.TrimMatcher();
   private static final StrMatcher SINGLE_QUOTE_MATCHER = new StrMatcher.CharMatcher('\'');
   private static final StrMatcher DOUBLE_QUOTE_MATCHER = new StrMatcher.CharMatcher('\"');
   private static final StrMatcher QUOTE_MATCHER = new StrMatcher.CharSetMatcher("\'\"".toCharArray());
   private static final StrMatcher NONE_MATCHER = new StrMatcher.NoMatcher();

   public static StrMatcher commaMatcher() {
      return COMMA_MATCHER;
   }

   public static StrMatcher tabMatcher() {
      return TAB_MATCHER;
   }

   public static StrMatcher spaceMatcher() {
      return SPACE_MATCHER;
   }

   public static StrMatcher splitMatcher() {
      return SPLIT_MATCHER;
   }

   public static StrMatcher trimMatcher() {
      return TRIM_MATCHER;
   }

   public static StrMatcher singleQuoteMatcher() {
      return SINGLE_QUOTE_MATCHER;
   }

   public static StrMatcher doubleQuoteMatcher() {
      return DOUBLE_QUOTE_MATCHER;
   }

   public static StrMatcher quoteMatcher() {
      return QUOTE_MATCHER;
   }

   public static StrMatcher noneMatcher() {
      return NONE_MATCHER;
   }

   public static StrMatcher charMatcher(char ch) {
      return new StrMatcher.CharMatcher(ch);
   }

   public static StrMatcher charSetMatcher(char... chars) {
      return (StrMatcher)(chars != null && chars.length != 0?(chars.length == 1?new StrMatcher.CharMatcher(chars[0]):new StrMatcher.CharSetMatcher(chars)):NONE_MATCHER);
   }

   public static StrMatcher charSetMatcher(String chars) {
      return (StrMatcher)(StringUtils.isEmpty(chars)?NONE_MATCHER:(chars.length() == 1?new StrMatcher.CharMatcher(chars.charAt(0)):new StrMatcher.CharSetMatcher(chars.toCharArray())));
   }

   public static StrMatcher stringMatcher(String str) {
      return (StrMatcher)(StringUtils.isEmpty(str)?NONE_MATCHER:new StrMatcher.StringMatcher(str));
   }

   public abstract int isMatch(char[] var1, int var2, int var3, int var4);

   public int isMatch(char[] buffer, int pos) {
      return this.isMatch(buffer, pos, 0, buffer.length);
   }

   static final class CharMatcher extends StrMatcher {
      private final char ch;

      CharMatcher(char ch) {
         this.ch = ch;
      }

      public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
         return this.ch == buffer[pos]?1:0;
      }
   }

   static final class CharSetMatcher extends StrMatcher {
      private final char[] chars;

      CharSetMatcher(char[] chars) {
         this.chars = (char[])chars.clone();
         Arrays.sort(this.chars);
      }

      public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
         return Arrays.binarySearch(this.chars, buffer[pos]) >= 0?1:0;
      }
   }

   static final class NoMatcher extends StrMatcher {
      public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
         return 0;
      }
   }

   static final class StringMatcher extends StrMatcher {
      private final char[] chars;

      StringMatcher(String str) {
         this.chars = str.toCharArray();
      }

      public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
         int len = this.chars.length;
         if(pos + len > bufferEnd) {
            return 0;
         } else {
            for(int i = 0; i < this.chars.length; ++pos) {
               if(this.chars[i] != buffer[pos]) {
                  return 0;
               }

               ++i;
            }

            return len;
         }
      }
   }

   static final class TrimMatcher extends StrMatcher {
      public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
         return buffer[pos] <= 32?1:0;
      }
   }
}
