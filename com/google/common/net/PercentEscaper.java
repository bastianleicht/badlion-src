package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.escape.UnicodeEscaper;

@Beta
@GwtCompatible
public final class PercentEscaper extends UnicodeEscaper {
   private static final char[] PLUS_SIGN = new char[]{'+'};
   private static final char[] UPPER_HEX_DIGITS = "0123456789ABCDEF".toCharArray();
   private final boolean plusForSpace;
   private final boolean[] safeOctets;

   public PercentEscaper(String safeChars, boolean plusForSpace) {
      Preconditions.checkNotNull(safeChars);
      if(safeChars.matches(".*[0-9A-Za-z].*")) {
         throw new IllegalArgumentException("Alphanumeric characters are always \'safe\' and should not be explicitly specified");
      } else {
         safeChars = safeChars + "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
         if(plusForSpace && safeChars.contains(" ")) {
            throw new IllegalArgumentException("plusForSpace cannot be specified when space is a \'safe\' character");
         } else {
            this.plusForSpace = plusForSpace;
            this.safeOctets = createSafeOctets(safeChars);
         }
      }
   }

   private static boolean[] createSafeOctets(String safeChars) {
      int maxChar = -1;
      char[] safeCharArray = safeChars.toCharArray();

      for(char c : safeCharArray) {
         maxChar = Math.max(c, maxChar);
      }

      boolean[] octets = new boolean[maxChar + 1];

      for(char c : safeCharArray) {
         octets[c] = true;
      }

      return octets;
   }

   protected int nextEscapeIndex(CharSequence csq, int index, int end) {
      Preconditions.checkNotNull(csq);

      while(index < end) {
         char c = csq.charAt(index);
         if(c >= this.safeOctets.length || !this.safeOctets[c]) {
            break;
         }

         ++index;
      }

      return index;
   }

   public String escape(String s) {
      Preconditions.checkNotNull(s);
      int slen = s.length();

      for(int index = 0; index < slen; ++index) {
         char c = s.charAt(index);
         if(c >= this.safeOctets.length || !this.safeOctets[c]) {
            return this.escapeSlow(s, index);
         }
      }

      return s;
   }

   protected char[] escape(int cp) {
      if(cp < this.safeOctets.length && this.safeOctets[cp]) {
         return null;
      } else if(cp == 32 && this.plusForSpace) {
         return PLUS_SIGN;
      } else if(cp <= 127) {
         char[] dest = new char[]{'%', UPPER_HEX_DIGITS[cp >>> 4], UPPER_HEX_DIGITS[cp & 15]};
         return dest;
      } else if(cp <= 2047) {
         char[] dest = new char[]{'%', '\u0000', '\u0000', '%', '\u0000', UPPER_HEX_DIGITS[cp & 15]};
         cp = cp >>> 4;
         dest[4] = UPPER_HEX_DIGITS[8 | cp & 3];
         cp = cp >>> 2;
         dest[2] = UPPER_HEX_DIGITS[cp & 15];
         cp = cp >>> 4;
         dest[1] = UPPER_HEX_DIGITS[12 | cp];
         return dest;
      } else if(cp <= '\uffff') {
         char[] dest = new char[9];
         dest[0] = 37;
         dest[1] = 69;
         dest[3] = 37;
         dest[6] = 37;
         dest[8] = UPPER_HEX_DIGITS[cp & 15];
         cp = cp >>> 4;
         dest[7] = UPPER_HEX_DIGITS[8 | cp & 3];
         cp = cp >>> 2;
         dest[5] = UPPER_HEX_DIGITS[cp & 15];
         cp = cp >>> 4;
         dest[4] = UPPER_HEX_DIGITS[8 | cp & 3];
         cp = cp >>> 2;
         dest[2] = UPPER_HEX_DIGITS[cp];
         return dest;
      } else if(cp <= 1114111) {
         char[] dest = new char[12];
         dest[0] = 37;
         dest[1] = 70;
         dest[3] = 37;
         dest[6] = 37;
         dest[9] = 37;
         dest[11] = UPPER_HEX_DIGITS[cp & 15];
         cp = cp >>> 4;
         dest[10] = UPPER_HEX_DIGITS[8 | cp & 3];
         cp = cp >>> 2;
         dest[8] = UPPER_HEX_DIGITS[cp & 15];
         cp = cp >>> 4;
         dest[7] = UPPER_HEX_DIGITS[8 | cp & 3];
         cp = cp >>> 2;
         dest[5] = UPPER_HEX_DIGITS[cp & 15];
         cp = cp >>> 4;
         dest[4] = UPPER_HEX_DIGITS[8 | cp & 3];
         cp = cp >>> 2;
         dest[2] = UPPER_HEX_DIGITS[cp & 7];
         return dest;
      } else {
         throw new IllegalArgumentException("Invalid unicode character value " + cp);
      }
   }
}
