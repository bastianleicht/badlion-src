package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;

@GwtCompatible
public final class SignedBytes {
   public static final byte MAX_POWER_OF_TWO = 64;

   public static byte checkedCast(long value) {
      byte result = (byte)((int)value);
      if((long)result != value) {
         throw new IllegalArgumentException("Out of range: " + value);
      } else {
         return result;
      }
   }

   public static byte saturatedCast(long value) {
      return value > 127L?127:(value < -128L?-128:(byte)((int)value));
   }

   public static int compare(byte a, byte b) {
      return a - b;
   }

   public static byte min(byte... array) {
      Preconditions.checkArgument(array.length > 0);
      byte min = array[0];

      for(int i = 1; i < array.length; ++i) {
         if(array[i] < min) {
            min = array[i];
         }
      }

      return min;
   }

   public static byte max(byte... array) {
      Preconditions.checkArgument(array.length > 0);
      byte max = array[0];

      for(int i = 1; i < array.length; ++i) {
         if(array[i] > max) {
            max = array[i];
         }
      }

      return max;
   }

   public static String join(String separator, byte... array) {
      Preconditions.checkNotNull(separator);
      if(array.length == 0) {
         return "";
      } else {
         StringBuilder builder = new StringBuilder(array.length * 5);
         builder.append(array[0]);

         for(int i = 1; i < array.length; ++i) {
            builder.append(separator).append(array[i]);
         }

         return builder.toString();
      }
   }

   public static Comparator lexicographicalComparator() {
      return SignedBytes.LexicographicalComparator.INSTANCE;
   }

   private static enum LexicographicalComparator implements Comparator {
      INSTANCE;

      public int compare(byte[] left, byte[] right) {
         int minLength = Math.min(left.length, right.length);

         for(int i = 0; i < minLength; ++i) {
            int result = SignedBytes.compare(left[i], right[i]);
            if(result != 0) {
               return result;
            }
         }

         return left.length - right.length;
      }
   }
}
