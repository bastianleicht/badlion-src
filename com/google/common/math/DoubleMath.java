package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.DoubleUtils;
import com.google.common.math.LongMath;
import com.google.common.math.MathPreconditions;
import com.google.common.primitives.Booleans;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Iterator;

@GwtCompatible(
   emulated = true
)
public final class DoubleMath {
   private static final double MIN_INT_AS_DOUBLE = -2.147483648E9D;
   private static final double MAX_INT_AS_DOUBLE = 2.147483647E9D;
   private static final double MIN_LONG_AS_DOUBLE = -9.223372036854776E18D;
   private static final double MAX_LONG_AS_DOUBLE_PLUS_ONE = 9.223372036854776E18D;
   private static final double LN_2 = Math.log(2.0D);
   @VisibleForTesting
   static final int MAX_FACTORIAL = 170;
   @VisibleForTesting
   static final double[] everySixteenthFactorial = new double[]{1.0D, 2.0922789888E13D, 2.631308369336935E35D, 1.2413915592536073E61D, 1.2688693218588417E89D, 7.156945704626381E118D, 9.916779348709496E149D, 1.974506857221074E182D, 3.856204823625804E215D, 5.5502938327393044E249D, 4.7147236359920616E284D};

   @GwtIncompatible("#isMathematicalInteger, com.google.common.math.DoubleUtils")
   static double roundIntermediate(double x, RoundingMode mode) {
      if(!DoubleUtils.isFinite(x)) {
         throw new ArithmeticException("input is infinite or NaN");
      } else {
         switch(mode) {
         case UNNECESSARY:
            MathPreconditions.checkRoundingUnnecessary(isMathematicalInteger(x));
            return x;
         case FLOOR:
            if(x < 0.0D && !isMathematicalInteger(x)) {
               return x - 1.0D;
            }

            return x;
         case CEILING:
            if(x > 0.0D && !isMathematicalInteger(x)) {
               return x + 1.0D;
            }

            return x;
         case DOWN:
            return x;
         case UP:
            if(isMathematicalInteger(x)) {
               return x;
            }

            return x + Math.copySign(1.0D, x);
         case HALF_EVEN:
            return Math.rint(x);
         case HALF_UP:
            double z = Math.rint(x);
            if(Math.abs(x - z) == 0.5D) {
               return x + Math.copySign(0.5D, x);
            }

            return z;
         case HALF_DOWN:
            double z = Math.rint(x);
            if(Math.abs(x - z) == 0.5D) {
               return x;
            }

            return z;
         default:
            throw new AssertionError();
         }
      }
   }

   @GwtIncompatible("#roundIntermediate")
   public static int roundToInt(double x, RoundingMode mode) {
      double z = roundIntermediate(x, mode);
      MathPreconditions.checkInRange(z > -2.147483649E9D & z < 2.147483648E9D);
      return (int)z;
   }

   @GwtIncompatible("#roundIntermediate")
   public static long roundToLong(double x, RoundingMode mode) {
      double z = roundIntermediate(x, mode);
      MathPreconditions.checkInRange(-9.223372036854776E18D - z < 1.0D & z < 9.223372036854776E18D);
      return (long)z;
   }

   @GwtIncompatible("#roundIntermediate, java.lang.Math.getExponent, com.google.common.math.DoubleUtils")
   public static BigInteger roundToBigInteger(double x, RoundingMode mode) {
      x = roundIntermediate(x, mode);
      if(-9.223372036854776E18D - x < 1.0D & x < 9.223372036854776E18D) {
         return BigInteger.valueOf((long)x);
      } else {
         int exponent = Math.getExponent(x);
         long significand = DoubleUtils.getSignificand(x);
         BigInteger result = BigInteger.valueOf(significand).shiftLeft(exponent - 52);
         return x < 0.0D?result.negate():result;
      }
   }

   @GwtIncompatible("com.google.common.math.DoubleUtils")
   public static boolean isPowerOfTwo(double x) {
      return x > 0.0D && DoubleUtils.isFinite(x) && LongMath.isPowerOfTwo(DoubleUtils.getSignificand(x));
   }

   public static double log2(double x) {
      return Math.log(x) / LN_2;
   }

   @GwtIncompatible("java.lang.Math.getExponent, com.google.common.math.DoubleUtils")
   public static int log2(double x, RoundingMode mode) {
      Preconditions.checkArgument(x > 0.0D && DoubleUtils.isFinite(x), "x must be positive and finite");
      int exponent = Math.getExponent(x);
      if(!DoubleUtils.isNormal(x)) {
         return log2(x * 4.503599627370496E15D, mode) - 52;
      } else {
         boolean increment;
         switch(mode) {
         case UNNECESSARY:
            MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(x));
         case FLOOR:
            increment = false;
            break;
         case CEILING:
            increment = !isPowerOfTwo(x);
            break;
         case DOWN:
            increment = exponent < 0 & !isPowerOfTwo(x);
            break;
         case UP:
            increment = exponent >= 0 & !isPowerOfTwo(x);
            break;
         case HALF_EVEN:
         case HALF_UP:
         case HALF_DOWN:
            double xScaled = DoubleUtils.scaleNormalize(x);
            increment = xScaled * xScaled > 2.0D;
            break;
         default:
            throw new AssertionError();
         }

         return increment?exponent + 1:exponent;
      }
   }

   @GwtIncompatible("java.lang.Math.getExponent, com.google.common.math.DoubleUtils")
   public static boolean isMathematicalInteger(double x) {
      return DoubleUtils.isFinite(x) && (x == 0.0D || 52 - Long.numberOfTrailingZeros(DoubleUtils.getSignificand(x)) <= Math.getExponent(x));
   }

   public static double factorial(int n) {
      MathPreconditions.checkNonNegative("n", n);
      if(n > 170) {
         return Double.POSITIVE_INFINITY;
      } else {
         double accum = 1.0D;

         for(int i = 1 + (n & -16); i <= n; ++i) {
            accum *= (double)i;
         }

         return accum * everySixteenthFactorial[n >> 4];
      }
   }

   public static boolean fuzzyEquals(double a, double b, double tolerance) {
      MathPreconditions.checkNonNegative("tolerance", tolerance);
      return Math.copySign(a - b, 1.0D) <= tolerance || a == b || Double.isNaN(a) && Double.isNaN(b);
   }

   public static int fuzzyCompare(double a, double b, double tolerance) {
      return fuzzyEquals(a, b, tolerance)?0:(a < b?-1:(a > b?1:Booleans.compare(Double.isNaN(a), Double.isNaN(b))));
   }

   @GwtIncompatible("MeanAccumulator")
   public static double mean(double... values) {
      DoubleMath.MeanAccumulator accumulator = new DoubleMath.MeanAccumulator();

      for(double value : values) {
         accumulator.add(value);
      }

      return accumulator.mean();
   }

   @GwtIncompatible("MeanAccumulator")
   public static double mean(int... values) {
      DoubleMath.MeanAccumulator accumulator = new DoubleMath.MeanAccumulator();

      for(int value : values) {
         accumulator.add((double)value);
      }

      return accumulator.mean();
   }

   @GwtIncompatible("MeanAccumulator")
   public static double mean(long... values) {
      DoubleMath.MeanAccumulator accumulator = new DoubleMath.MeanAccumulator();

      for(long value : values) {
         accumulator.add((double)value);
      }

      return accumulator.mean();
   }

   @GwtIncompatible("MeanAccumulator")
   public static double mean(Iterable values) {
      DoubleMath.MeanAccumulator accumulator = new DoubleMath.MeanAccumulator();

      for(Number value : values) {
         accumulator.add(value.doubleValue());
      }

      return accumulator.mean();
   }

   @GwtIncompatible("MeanAccumulator")
   public static double mean(Iterator values) {
      DoubleMath.MeanAccumulator accumulator = new DoubleMath.MeanAccumulator();

      while(values.hasNext()) {
         accumulator.add(((Number)values.next()).doubleValue());
      }

      return accumulator.mean();
   }

   @GwtIncompatible("com.google.common.math.DoubleUtils")
   private static final class MeanAccumulator {
      private long count;
      private double mean;

      private MeanAccumulator() {
         this.count = 0L;
         this.mean = 0.0D;
      }

      void add(double value) {
         Preconditions.checkArgument(DoubleUtils.isFinite(value));
         ++this.count;
         this.mean += (value - this.mean) / (double)this.count;
      }

      double mean() {
         Preconditions.checkArgument(this.count > 0L, "Cannot take mean of 0 values");
         return this.mean;
      }
   }
}
