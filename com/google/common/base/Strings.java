package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;

@GwtCompatible
public final class Strings {
   public static String nullToEmpty(@Nullable String string) {
      return string == null?"":string;
   }

   @Nullable
   public static String emptyToNull(@Nullable String string) {
      return isNullOrEmpty(string)?null:string;
   }

   public static boolean isNullOrEmpty(@Nullable String string) {
      return string == null || string.length() == 0;
   }

   public static String padStart(String string, int minLength, char padChar) {
      Preconditions.checkNotNull(string);
      if(string.length() >= minLength) {
         return string;
      } else {
         StringBuilder sb = new StringBuilder(minLength);

         for(int i = string.length(); i < minLength; ++i) {
            sb.append(padChar);
         }

         sb.append(string);
         return sb.toString();
      }
   }

   public static String padEnd(String string, int minLength, char padChar) {
      Preconditions.checkNotNull(string);
      if(string.length() >= minLength) {
         return string;
      } else {
         StringBuilder sb = new StringBuilder(minLength);
         sb.append(string);

         for(int i = string.length(); i < minLength; ++i) {
            sb.append(padChar);
         }

         return sb.toString();
      }
   }

   public static String repeat(String string, int count) {
      Preconditions.checkNotNull(string);
      if(count <= 1) {
         Preconditions.checkArgument(count >= 0, "invalid count: %s", new Object[]{Integer.valueOf(count)});
         return count == 0?"":string;
      } else {
         int len = string.length();
         long longSize = (long)len * (long)count;
         int size = (int)longSize;
         if((long)size != longSize) {
            throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize);
         } else {
            char[] array = new char[size];
            string.getChars(0, len, array, 0);

            int n;
            for(n = len; n < size - n; n <<= 1) {
               System.arraycopy(array, 0, array, n, n);
            }

            System.arraycopy(array, 0, array, n, size - n);
            return new String(array);
         }
      }
   }

   public static String commonPrefix(CharSequence a, CharSequence b) {
      Preconditions.checkNotNull(a);
      Preconditions.checkNotNull(b);
      int maxPrefixLength = Math.min(a.length(), b.length());

      int p;
      for(p = 0; p < maxPrefixLength && a.charAt(p) == b.charAt(p); ++p) {
         ;
      }

      if(validSurrogatePairAt(a, p - 1) || validSurrogatePairAt(b, p - 1)) {
         --p;
      }

      return a.subSequence(0, p).toString();
   }

   public static String commonSuffix(CharSequence a, CharSequence b) {
      Preconditions.checkNotNull(a);
      Preconditions.checkNotNull(b);
      int maxSuffixLength = Math.min(a.length(), b.length());

      int s;
      for(s = 0; s < maxSuffixLength && a.charAt(a.length() - s - 1) == b.charAt(b.length() - s - 1); ++s) {
         ;
      }

      if(validSurrogatePairAt(a, a.length() - s - 1) || validSurrogatePairAt(b, b.length() - s - 1)) {
         --s;
      }

      return a.subSequence(a.length() - s, a.length()).toString();
   }

   @VisibleForTesting
   static boolean validSurrogatePairAt(CharSequence string, int index) {
      return index >= 0 && index <= string.length() - 2 && Character.isHighSurrogate(string.charAt(index)) && Character.isLowSurrogate(string.charAt(index + 1));
   }
}
