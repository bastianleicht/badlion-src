package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import javax.annotation.CheckReturnValue;

@GwtCompatible
public final class Ascii {
   public static final byte NUL = 0;
   public static final byte SOH = 1;
   public static final byte STX = 2;
   public static final byte ETX = 3;
   public static final byte EOT = 4;
   public static final byte ENQ = 5;
   public static final byte ACK = 6;
   public static final byte BEL = 7;
   public static final byte BS = 8;
   public static final byte HT = 9;
   public static final byte LF = 10;
   public static final byte NL = 10;
   public static final byte VT = 11;
   public static final byte FF = 12;
   public static final byte CR = 13;
   public static final byte SO = 14;
   public static final byte SI = 15;
   public static final byte DLE = 16;
   public static final byte DC1 = 17;
   public static final byte XON = 17;
   public static final byte DC2 = 18;
   public static final byte DC3 = 19;
   public static final byte XOFF = 19;
   public static final byte DC4 = 20;
   public static final byte NAK = 21;
   public static final byte SYN = 22;
   public static final byte ETB = 23;
   public static final byte CAN = 24;
   public static final byte EM = 25;
   public static final byte SUB = 26;
   public static final byte ESC = 27;
   public static final byte FS = 28;
   public static final byte GS = 29;
   public static final byte RS = 30;
   public static final byte US = 31;
   public static final byte SP = 32;
   public static final byte SPACE = 32;
   public static final byte DEL = 127;
   public static final char MIN = '\u0000';
   public static final char MAX = '\u007f';

   public static String toLowerCase(String string) {
      int length = string.length();

      for(int i = 0; i < length; ++i) {
         if(isUpperCase(string.charAt(i))) {
            char[] chars;
            for(chars = string.toCharArray(); i < length; ++i) {
               char c = chars[i];
               if(isUpperCase(c)) {
                  chars[i] = (char)(c ^ 32);
               }
            }

            return String.valueOf(chars);
         }
      }

      return string;
   }

   public static String toLowerCase(CharSequence chars) {
      if(chars instanceof String) {
         return toLowerCase((String)chars);
      } else {
         int length = chars.length();
         StringBuilder builder = new StringBuilder(length);

         for(int i = 0; i < length; ++i) {
            builder.append(toLowerCase(chars.charAt(i)));
         }

         return builder.toString();
      }
   }

   public static char toLowerCase(char c) {
      return isUpperCase(c)?(char)(c ^ 32):c;
   }

   public static String toUpperCase(String string) {
      int length = string.length();

      for(int i = 0; i < length; ++i) {
         if(isLowerCase(string.charAt(i))) {
            char[] chars;
            for(chars = string.toCharArray(); i < length; ++i) {
               char c = chars[i];
               if(isLowerCase(c)) {
                  chars[i] = (char)(c & 95);
               }
            }

            return String.valueOf(chars);
         }
      }

      return string;
   }

   public static String toUpperCase(CharSequence chars) {
      if(chars instanceof String) {
         return toUpperCase((String)chars);
      } else {
         int length = chars.length();
         StringBuilder builder = new StringBuilder(length);

         for(int i = 0; i < length; ++i) {
            builder.append(toUpperCase(chars.charAt(i)));
         }

         return builder.toString();
      }
   }

   public static char toUpperCase(char c) {
      return isLowerCase(c)?(char)(c & 95):c;
   }

   public static boolean isLowerCase(char c) {
      return c >= 97 && c <= 122;
   }

   public static boolean isUpperCase(char c) {
      return c >= 65 && c <= 90;
   }

   @CheckReturnValue
   @Beta
   public static String truncate(CharSequence seq, int maxLength, String truncationIndicator) {
      Preconditions.checkNotNull(seq);
      int truncationLength = maxLength - truncationIndicator.length();
      Preconditions.checkArgument(truncationLength >= 0, "maxLength (%s) must be >= length of the truncation indicator (%s)", new Object[]{Integer.valueOf(maxLength), Integer.valueOf(truncationIndicator.length())});
      if(((CharSequence)seq).length() <= maxLength) {
         String string = ((CharSequence)seq).toString();
         if(string.length() <= maxLength) {
            return string;
         }

         seq = string;
      }

      return (new StringBuilder(maxLength)).append((CharSequence)seq, 0, truncationLength).append(truncationIndicator).toString();
   }

   @Beta
   public static boolean equalsIgnoreCase(CharSequence s1, CharSequence s2) {
      int length = s1.length();
      if(s1 == s2) {
         return true;
      } else if(length != s2.length()) {
         return false;
      } else {
         for(int i = 0; i < length; ++i) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);
            if(c1 != c2) {
               int alphaIndex = getAlphaIndex(c1);
               if(alphaIndex >= 26 || alphaIndex != getAlphaIndex(c2)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private static int getAlphaIndex(char c) {
      return (char)((c | 32) - 97);
   }
}
