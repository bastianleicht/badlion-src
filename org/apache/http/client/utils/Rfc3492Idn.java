package org.apache.http.client.utils;

import java.util.StringTokenizer;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.utils.Idn;

@Immutable
public class Rfc3492Idn implements Idn {
   private static final int base = 36;
   private static final int tmin = 1;
   private static final int tmax = 26;
   private static final int skew = 38;
   private static final int damp = 700;
   private static final int initial_bias = 72;
   private static final int initial_n = 128;
   private static final char delimiter = '-';
   private static final String ACE_PREFIX = "xn--";

   private int adapt(int delta, int numpoints, boolean firsttime) {
      int d;
      if(firsttime) {
         d = delta / 700;
      } else {
         d = delta / 2;
      }

      d = d + d / numpoints;

      int k;
      for(k = 0; d > 455; k += 36) {
         d /= 35;
      }

      return k + 36 * d / (d + 38);
   }

   private int digit(char c) {
      if(c >= 65 && c <= 90) {
         return c - 65;
      } else if(c >= 97 && c <= 122) {
         return c - 97;
      } else if(c >= 48 && c <= 57) {
         return c - 48 + 26;
      } else {
         throw new IllegalArgumentException("illegal digit: " + c);
      }
   }

   public String toUnicode(String punycode) {
      StringBuilder unicode = new StringBuilder(punycode.length());

      String t;
      for(StringTokenizer tok = new StringTokenizer(punycode, "."); tok.hasMoreTokens(); unicode.append(t)) {
         t = tok.nextToken();
         if(unicode.length() > 0) {
            unicode.append('.');
         }

         if(t.startsWith("xn--")) {
            t = this.decode(t.substring(4));
         }
      }

      return unicode.toString();
   }

   protected String decode(String s) {
      String input = s;
      int n = 128;
      int i = 0;
      int bias = 72;
      StringBuilder output = new StringBuilder(s.length());
      int lastdelim = s.lastIndexOf(45);
      if(lastdelim != -1) {
         output.append(s.subSequence(0, lastdelim));
         input = s.substring(lastdelim + 1);
      }

      while(input.length() > 0) {
         int oldi = i;
         int w = 1;

         for(int k = 36; input.length() != 0; k += 36) {
            char c = input.charAt(0);
            input = input.substring(1);
            int digit = this.digit(c);
            i += digit * w;
            int t;
            if(k <= bias + 1) {
               t = 1;
            } else if(k >= bias + 26) {
               t = 26;
            } else {
               t = k - bias;
            }

            if(digit < t) {
               break;
            }

            w *= 36 - t;
         }

         bias = this.adapt(i - oldi, output.length() + 1, oldi == 0);
         n += i / (output.length() + 1);
         i %= output.length() + 1;
         output.insert(i, (char)n);
         ++i;
      }

      return output.toString();
   }
}
