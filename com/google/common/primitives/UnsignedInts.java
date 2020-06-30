package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.common.primitives.ParseRequest;
import java.util.Comparator;

@Beta
@GwtCompatible
public final class UnsignedInts {
   static final long INT_MASK = 4294967295L;

   static int flip(int value) {
      return value ^ Integer.MIN_VALUE;
   }

   public static int compare(int a, int b) {
      return Ints.compare(flip(a), flip(b));
   }

   public static long toLong(int value) {
      return (long)value & 4294967295L;
   }

   public static int min(int... array) {
      Preconditions.checkArgument(array.length > 0);
      int min = flip(array[0]);

      for(int i = 1; i < array.length; ++i) {
         int next = flip(array[i]);
         if(next < min) {
            min = next;
         }
      }

      return flip(min);
   }

   public static int max(int... array) {
      Preconditions.checkArgument(array.length > 0);
      int max = flip(array[0]);

      for(int i = 1; i < array.length; ++i) {
         int next = flip(array[i]);
         if(next > max) {
            max = next;
         }
      }

      return flip(max);
   }

   public static String join(String separator, int... array) {
      Preconditions.checkNotNull(separator);
      if(array.length == 0) {
         return "";
      } else {
         StringBuilder builder = new StringBuilder(array.length * 5);
         builder.append(toString(array[0]));

         for(int i = 1; i < array.length; ++i) {
            builder.append(separator).append(toString(array[i]));
         }

         return builder.toString();
      }
   }

   public static Comparator lexicographicalComparator() {
      return UnsignedInts.LexicographicalComparator.INSTANCE;
   }

   public static int divide(int dividend, int divisor) {
      return (int)(toLong(dividend) / toLong(divisor));
   }

   public static int remainder(int dividend, int divisor) {
      return (int)(toLong(dividend) % toLong(divisor));
   }

   public static int decode(String stringValue) {
      ParseRequest request = ParseRequest.fromString(stringValue);

      try {
         return parseUnsignedInt(request.rawValue, request.radix);
      } catch (NumberFormatException var4) {
         NumberFormatException decodeException = new NumberFormatException("Error parsing value: " + stringValue);
         decodeException.initCause(var4);
         throw decodeException;
      }
   }

   public static int parseUnsignedInt(String s) {
      return parseUnsignedInt(s, 10);
   }

   public static int parseUnsignedInt(String string, int radix) {
      Preconditions.checkNotNull(string);
      long result = Long.parseLong(string, radix);
      if((result & 4294967295L) != result) {
         throw new NumberFormatException("Input " + string + " in base " + radix + " is not in the range of an unsigned integer");
      } else {
         return (int)result;
      }
   }

   public static String toString(int x) {
      return toString(x, 10);
   }

   public static String toString(int x, int radix) {
      long asLong = (long)x & 4294967295L;
      return Long.toString(asLong, radix);
   }

   static enum LexicographicalComparator implements Comparator {
      INSTANCE;

      public int compare(int[] left, int[] right) {
         int minLength = Math.min(left.length, right.length);

         for(int i = 0; i < minLength; ++i) {
            if(left[i] != right[i]) {
               return UnsignedInts.compare(left[i], right[i]);
            }
         }

         return left.length - right.length;
      }
   }
}
