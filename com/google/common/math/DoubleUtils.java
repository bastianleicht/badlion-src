package com.google.common.math;

import com.google.common.base.Preconditions;
import java.math.BigInteger;

final class DoubleUtils {
   static final long SIGNIFICAND_MASK = 4503599627370495L;
   static final long EXPONENT_MASK = 9218868437227405312L;
   static final long SIGN_MASK = Long.MIN_VALUE;
   static final int SIGNIFICAND_BITS = 52;
   static final int EXPONENT_BIAS = 1023;
   static final long IMPLICIT_BIT = 4503599627370496L;
   private static final long ONE_BITS = Double.doubleToRawLongBits(1.0D);

   static double nextDown(double d) {
      return -Math.nextUp(-d);
   }

   static long getSignificand(double d) {
      Preconditions.checkArgument(isFinite(d), "not a normal value");
      int exponent = Math.getExponent(d);
      long bits = Double.doubleToRawLongBits(d);
      bits = bits & 4503599627370495L;
      return exponent == -1023?bits << 1:bits | 4503599627370496L;
   }

   static boolean isFinite(double d) {
      return Math.getExponent(d) <= 1023;
   }

   static boolean isNormal(double d) {
      return Math.getExponent(d) >= -1022;
   }

   static double scaleNormalize(double x) {
      long significand = Double.doubleToRawLongBits(x) & 4503599627370495L;
      return Double.longBitsToDouble(significand | ONE_BITS);
   }

   static double bigToDouble(BigInteger x) {
      BigInteger absX = x.abs();
      int exponent = absX.bitLength() - 1;
      if(exponent < 63) {
         return (double)x.longValue();
      } else if(exponent > 1023) {
         return (double)x.signum() * Double.POSITIVE_INFINITY;
      } else {
         int shift = exponent - 52 - 1;
         long twiceSignifFloor = absX.shiftRight(shift).longValue();
         long signifFloor = twiceSignifFloor >> 1;
         signifFloor = signifFloor & 4503599627370495L;
         boolean increment = (twiceSignifFloor & 1L) != 0L && ((signifFloor & 1L) != 0L || absX.getLowestSetBit() < shift);
         long signifRounded = increment?signifFloor + 1L:signifFloor;
         long bits = (long)(exponent + 1023) << 52;
         bits = bits + signifRounded;
         bits = bits | (long)x.signum() & Long.MIN_VALUE;
         return Double.longBitsToDouble(bits);
      }
   }

   static double ensureNonNegative(double value) {
      Preconditions.checkArgument(!Double.isNaN(value));
      return value > 0.0D?value:0.0D;
   }
}
