package com.google.common.primitives;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Longs;
import com.google.common.primitives.ParseRequest;
import java.math.BigInteger;
import java.util.Comparator;

@Beta
@GwtCompatible
public final class UnsignedLongs {
   public static final long MAX_VALUE = -1L;
   private static final long[] maxValueDivs = new long[37];
   private static final int[] maxValueMods = new int[37];
   private static final int[] maxSafeDigits = new int[37];

   private static long flip(long a) {
      return a ^ Long.MIN_VALUE;
   }

   public static int compare(long a, long b) {
      return Longs.compare(flip(a), flip(b));
   }

   public static long min(long... array) {
      Preconditions.checkArgument(array.length > 0);
      long min = flip(array[0]);

      for(int i = 1; i < array.length; ++i) {
         long next = flip(array[i]);
         if(next < min) {
            min = next;
         }
      }

      return flip(min);
   }

   public static long max(long... array) {
      Preconditions.checkArgument(array.length > 0);
      long max = flip(array[0]);

      for(int i = 1; i < array.length; ++i) {
         long next = flip(array[i]);
         if(next > max) {
            max = next;
         }
      }

      return flip(max);
   }

   public static String join(String separator, long... array) {
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
      return UnsignedLongs.LexicographicalComparator.INSTANCE;
   }

   public static long divide(long dividend, long divisor) {
      if(divisor < 0L) {
         return compare(dividend, divisor) < 0?0L:1L;
      } else if(dividend >= 0L) {
         return dividend / divisor;
      } else {
         long quotient = (dividend >>> 1) / divisor << 1;
         long rem = dividend - quotient * divisor;
         return quotient + (long)(compare(rem, divisor) >= 0?1:0);
      }
   }

   public static long remainder(long dividend, long divisor) {
      if(divisor < 0L) {
         return compare(dividend, divisor) < 0?dividend:dividend - divisor;
      } else if(dividend >= 0L) {
         return dividend % divisor;
      } else {
         long quotient = (dividend >>> 1) / divisor << 1;
         long rem = dividend - quotient * divisor;
         return rem - (compare(rem, divisor) >= 0?divisor:0L);
      }
   }

   public static long parseUnsignedLong(String s) {
      return parseUnsignedLong(s, 10);
   }

   public static long decode(String stringValue) {
      ParseRequest request = ParseRequest.fromString(stringValue);

      try {
         return parseUnsignedLong(request.rawValue, request.radix);
      } catch (NumberFormatException var4) {
         NumberFormatException decodeException = new NumberFormatException("Error parsing value: " + stringValue);
         decodeException.initCause(var4);
         throw decodeException;
      }
   }

   public static long parseUnsignedLong(String s, int radix) {
      Preconditions.checkNotNull(s);
      if(s.length() == 0) {
         throw new NumberFormatException("empty string");
      } else if(radix >= 2 && radix <= 36) {
         int max_safe_pos = maxSafeDigits[radix] - 1;
         long value = 0L;

         for(int pos = 0; pos < s.length(); ++pos) {
            int digit = Character.digit(s.charAt(pos), radix);
            if(digit == -1) {
               throw new NumberFormatException(s);
            }

            if(pos > max_safe_pos && overflowInParse(value, digit, radix)) {
               throw new NumberFormatException("Too large for unsigned long: " + s);
            }

            value = value * (long)radix + (long)digit;
         }

         return value;
      } else {
         throw new NumberFormatException("illegal radix: " + radix);
      }
   }

   private static boolean overflowInParse(long current, int digit, int radix) {
      return current >= 0L?(current < maxValueDivs[radix]?false:(current > maxValueDivs[radix]?true:digit > maxValueMods[radix])):true;
   }

   public static String toString(long x) {
      return toString(x, 10);
   }

   public static String toString(long x, int radix) {
      Preconditions.checkArgument(radix >= 2 && radix <= 36, "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", new Object[]{Integer.valueOf(radix)});
      if(x == 0L) {
         return "0";
      } else {
         char[] buf = new char[64];
         int i = buf.length;
         if(x < 0L) {
            long quotient = divide(x, (long)radix);
            long rem = x - quotient * (long)radix;
            --i;
            buf[i] = Character.forDigit((int)rem, radix);
            x = quotient;
         }

         while(x > 0L) {
            --i;
            buf[i] = Character.forDigit((int)(x % (long)radix), radix);
            x /= (long)radix;
         }

         return new String(buf, i, buf.length - i);
      }
   }

   static {
      BigInteger overflow = new BigInteger("10000000000000000", 16);

      for(int i = 2; i <= 36; ++i) {
         maxValueDivs[i] = divide(-1L, (long)i);
         maxValueMods[i] = (int)remainder(-1L, (long)i);
         maxSafeDigits[i] = overflow.toString(i).length() - 1;
      }

   }

   static enum LexicographicalComparator implements Comparator {
      INSTANCE;

      public int compare(long[] left, long[] right) {
         int minLength = Math.min(left.length, right.length);

         for(int i = 0; i < minLength; ++i) {
            if(left[i] != right[i]) {
               return UnsignedLongs.compare(left[i], right[i]);
            }
         }

         return left.length - right.length;
      }
   }
}
