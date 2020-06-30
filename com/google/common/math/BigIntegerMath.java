package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.DoubleMath;
import com.google.common.math.DoubleUtils;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.google.common.math.MathPreconditions;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@GwtCompatible(
   emulated = true
)
public final class BigIntegerMath {
   @VisibleForTesting
   static final int SQRT2_PRECOMPUTE_THRESHOLD = 256;
   @VisibleForTesting
   static final BigInteger SQRT2_PRECOMPUTED_BITS = new BigInteger("16a09e667f3bcc908b2fb1366ea957d3e3adec17512775099da2f590b0667322a", 16);
   private static final double LN_10 = Math.log(10.0D);
   private static final double LN_2 = Math.log(2.0D);

   public static boolean isPowerOfTwo(BigInteger x) {
      Preconditions.checkNotNull(x);
      return x.signum() > 0 && x.getLowestSetBit() == x.bitLength() - 1;
   }

   public static int log2(BigInteger x, RoundingMode mode) {
      MathPreconditions.checkPositive("x", (BigInteger)Preconditions.checkNotNull(x));
      int logFloor = x.bitLength() - 1;
      switch(mode) {
      case UNNECESSARY:
         MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(x));
      case DOWN:
      case FLOOR:
         return logFloor;
      case UP:
      case CEILING:
         return isPowerOfTwo(x)?logFloor:logFloor + 1;
      case HALF_DOWN:
      case HALF_UP:
      case HALF_EVEN:
         if(logFloor < 256) {
            BigInteger halfPower = SQRT2_PRECOMPUTED_BITS.shiftRight(256 - logFloor);
            if(x.compareTo(halfPower) <= 0) {
               return logFloor;
            }

            return logFloor + 1;
         }

         BigInteger x2 = x.pow(2);
         int logX2Floor = x2.bitLength() - 1;
         return logX2Floor < 2 * logFloor + 1?logFloor:logFloor + 1;
      default:
         throw new AssertionError();
      }
   }

   @GwtIncompatible("TODO")
   public static int log10(BigInteger x, RoundingMode mode) {
      MathPreconditions.checkPositive("x", x);
      if(fitsInLong(x)) {
         return LongMath.log10(x.longValue(), mode);
      } else {
         int approxLog10 = (int)((double)log2(x, RoundingMode.FLOOR) * LN_2 / LN_10);
         BigInteger approxPow = BigInteger.TEN.pow(approxLog10);
         int approxCmp = approxPow.compareTo(x);
         if(approxCmp > 0) {
            while(true) {
               --approxLog10;
               approxPow = approxPow.divide(BigInteger.TEN);
               approxCmp = approxPow.compareTo(x);
               if(approxCmp <= 0) {
                  break;
               }
            }
         } else {
            BigInteger nextPow = BigInteger.TEN.multiply(approxPow);

            for(int nextCmp = nextPow.compareTo(x); nextCmp <= 0; nextCmp = nextPow.compareTo(x)) {
               ++approxLog10;
               approxPow = nextPow;
               approxCmp = nextCmp;
               nextPow = BigInteger.TEN.multiply(nextPow);
            }
         }

         switch(mode) {
         case UNNECESSARY:
            MathPreconditions.checkRoundingUnnecessary(approxCmp == 0);
         case DOWN:
         case FLOOR:
            return approxLog10;
         case UP:
         case CEILING:
            return approxPow.equals(x)?approxLog10:approxLog10 + 1;
         case HALF_DOWN:
         case HALF_UP:
         case HALF_EVEN:
            BigInteger x2 = x.pow(2);
            BigInteger halfPowerSquared = approxPow.pow(2).multiply(BigInteger.TEN);
            return x2.compareTo(halfPowerSquared) <= 0?approxLog10:approxLog10 + 1;
         default:
            throw new AssertionError();
         }
      }
   }

   @GwtIncompatible("TODO")
   public static BigInteger sqrt(BigInteger x, RoundingMode mode) {
      MathPreconditions.checkNonNegative("x", x);
      if(fitsInLong(x)) {
         return BigInteger.valueOf(LongMath.sqrt(x.longValue(), mode));
      } else {
         BigInteger sqrtFloor = sqrtFloor(x);
         switch(mode) {
         case UNNECESSARY:
            MathPreconditions.checkRoundingUnnecessary(sqrtFloor.pow(2).equals(x));
         case DOWN:
         case FLOOR:
            return sqrtFloor;
         case UP:
         case CEILING:
            int sqrtFloorInt = sqrtFloor.intValue();
            boolean sqrtFloorIsExact = sqrtFloorInt * sqrtFloorInt == x.intValue() && sqrtFloor.pow(2).equals(x);
            return sqrtFloorIsExact?sqrtFloor:sqrtFloor.add(BigInteger.ONE);
         case HALF_DOWN:
         case HALF_UP:
         case HALF_EVEN:
            BigInteger halfSquare = sqrtFloor.pow(2).add(sqrtFloor);
            return halfSquare.compareTo(x) >= 0?sqrtFloor:sqrtFloor.add(BigInteger.ONE);
         default:
            throw new AssertionError();
         }
      }
   }

   @GwtIncompatible("TODO")
   private static BigInteger sqrtFloor(BigInteger x) {
      int log2 = log2(x, RoundingMode.FLOOR);
      BigInteger sqrt0;
      if(log2 < 1023) {
         sqrt0 = sqrtApproxWithDoubles(x);
      } else {
         int shift = log2 - 52 & -2;
         sqrt0 = sqrtApproxWithDoubles(x.shiftRight(shift)).shiftLeft(shift >> 1);
      }

      BigInteger sqrt1 = sqrt0.add(x.divide(sqrt0)).shiftRight(1);
      if(sqrt0.equals(sqrt1)) {
         return sqrt0;
      } else {
         while(true) {
            sqrt0 = sqrt1;
            sqrt1 = sqrt1.add(x.divide(sqrt1)).shiftRight(1);
            if(sqrt1.compareTo(sqrt0) >= 0) {
               break;
            }
         }

         return sqrt0;
      }
   }

   @GwtIncompatible("TODO")
   private static BigInteger sqrtApproxWithDoubles(BigInteger x) {
      return DoubleMath.roundToBigInteger(Math.sqrt(DoubleUtils.bigToDouble(x)), RoundingMode.HALF_EVEN);
   }

   @GwtIncompatible("TODO")
   public static BigInteger divide(BigInteger p, BigInteger q, RoundingMode mode) {
      BigDecimal pDec = new BigDecimal(p);
      BigDecimal qDec = new BigDecimal(q);
      return pDec.divide(qDec, 0, mode).toBigIntegerExact();
   }

   public static BigInteger factorial(int n) {
      MathPreconditions.checkNonNegative("n", n);
      if(n < LongMath.factorials.length) {
         return BigInteger.valueOf(LongMath.factorials[n]);
      } else {
         int approxSize = IntMath.divide(n * IntMath.log2(n, RoundingMode.CEILING), 64, RoundingMode.CEILING);
         ArrayList<BigInteger> bignums = new ArrayList(approxSize);
         int startingNumber = LongMath.factorials.length;
         long product = LongMath.factorials[startingNumber - 1];
         int shift = Long.numberOfTrailingZeros(product);
         product = product >> shift;
         int productBits = LongMath.log2(product, RoundingMode.FLOOR) + 1;
         int bits = LongMath.log2((long)startingNumber, RoundingMode.FLOOR) + 1;
         int nextPowerOfTwo = 1 << bits - 1;

         for(long num = (long)startingNumber; num <= (long)n; ++num) {
            if((num & (long)nextPowerOfTwo) != 0L) {
               nextPowerOfTwo <<= 1;
               ++bits;
            }

            int tz = Long.numberOfTrailingZeros(num);
            long normalizedNum = num >> tz;
            shift += tz;
            int normalizedBits = bits - tz;
            if(normalizedBits + productBits >= 64) {
               bignums.add(BigInteger.valueOf(product));
               product = 1L;
               productBits = 0;
            }

            product *= normalizedNum;
            productBits = LongMath.log2(product, RoundingMode.FLOOR) + 1;
         }

         if(product > 1L) {
            bignums.add(BigInteger.valueOf(product));
         }

         return listProduct(bignums).shiftLeft(shift);
      }
   }

   static BigInteger listProduct(List nums) {
      return listProduct(nums, 0, nums.size());
   }

   static BigInteger listProduct(List nums, int start, int end) {
      switch(end - start) {
      case 0:
         return BigInteger.ONE;
      case 1:
         return (BigInteger)nums.get(start);
      case 2:
         return ((BigInteger)nums.get(start)).multiply((BigInteger)nums.get(start + 1));
      case 3:
         return ((BigInteger)nums.get(start)).multiply((BigInteger)nums.get(start + 1)).multiply((BigInteger)nums.get(start + 2));
      default:
         int m = end + start >>> 1;
         return listProduct(nums, start, m).multiply(listProduct(nums, m, end));
      }
   }

   public static BigInteger binomial(int n, int k) {
      MathPreconditions.checkNonNegative("n", n);
      MathPreconditions.checkNonNegative("k", k);
      Preconditions.checkArgument(k <= n, "k (%s) > n (%s)", new Object[]{Integer.valueOf(k), Integer.valueOf(n)});
      if(k > n >> 1) {
         k = n - k;
      }

      if(k < LongMath.biggestBinomials.length && n <= LongMath.biggestBinomials[k]) {
         return BigInteger.valueOf(LongMath.binomial(n, k));
      } else {
         BigInteger accum = BigInteger.ONE;
         long numeratorAccum = (long)n;
         long denominatorAccum = 1L;
         int bits = LongMath.log2((long)n, RoundingMode.CEILING);
         int numeratorBits = bits;

         for(int i = 1; i < k; ++i) {
            int p = n - i;
            int q = i + 1;
            if(numeratorBits + bits >= 63) {
               accum = accum.multiply(BigInteger.valueOf(numeratorAccum)).divide(BigInteger.valueOf(denominatorAccum));
               numeratorAccum = (long)p;
               denominatorAccum = (long)q;
               numeratorBits = bits;
            } else {
               numeratorAccum *= (long)p;
               denominatorAccum *= (long)q;
               numeratorBits += bits;
            }
         }

         return accum.multiply(BigInteger.valueOf(numeratorAccum)).divide(BigInteger.valueOf(denominatorAccum));
      }
   }

   @GwtIncompatible("TODO")
   static boolean fitsInLong(BigInteger x) {
      return x.bitLength() <= 63;
   }
}
