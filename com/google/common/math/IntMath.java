package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.math.MathPreconditions;
import java.math.RoundingMode;

@GwtCompatible(
   emulated = true
)
public final class IntMath {
   @VisibleForTesting
   static final int MAX_POWER_OF_SQRT2_UNSIGNED = -1257966797;
   @VisibleForTesting
   static final byte[] maxLog10ForLeadingZeros = new byte[]{(byte)9, (byte)9, (byte)9, (byte)8, (byte)8, (byte)8, (byte)7, (byte)7, (byte)7, (byte)6, (byte)6, (byte)6, (byte)6, (byte)5, (byte)5, (byte)5, (byte)4, (byte)4, (byte)4, (byte)3, (byte)3, (byte)3, (byte)3, (byte)2, (byte)2, (byte)2, (byte)1, (byte)1, (byte)1, (byte)0, (byte)0, (byte)0, (byte)0};
   @VisibleForTesting
   static final int[] powersOf10 = new int[]{1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};
   @VisibleForTesting
   static final int[] halfPowersOf10 = new int[]{3, 31, 316, 3162, 31622, 316227, 3162277, 31622776, 316227766, Integer.MAX_VALUE};
   @VisibleForTesting
   static final int FLOOR_SQRT_MAX_INT = 46340;
   private static final int[] factorials = new int[]{1, 1, 2, 6, 24, 120, 720, 5040, '鶀', 362880, 3628800, 39916800, 479001600};
   @VisibleForTesting
   static int[] biggestBinomials = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, 65536, 2345, 477, 193, 110, 75, 58, 49, 43, 39, 37, 35, 34, 34, 33};

   public static boolean isPowerOfTwo(int x) {
      return x > 0 & (x & x - 1) == 0;
   }

   @VisibleForTesting
   static int lessThanBranchFree(int x, int y) {
      return ~(~(x - y)) >>> 31;
   }

   public static int log2(int x, RoundingMode mode) {
      MathPreconditions.checkPositive("x", x);
      switch(mode) {
      case UNNECESSARY:
         MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(x));
      case DOWN:
      case FLOOR:
         return 31 - Integer.numberOfLeadingZeros(x);
      case UP:
      case CEILING:
         return 32 - Integer.numberOfLeadingZeros(x - 1);
      case HALF_DOWN:
      case HALF_UP:
      case HALF_EVEN:
         int leadingZeros = Integer.numberOfLeadingZeros(x);
         int cmp = -1257966797 >>> leadingZeros;
         int logFloor = 31 - leadingZeros;
         return logFloor + lessThanBranchFree(cmp, x);
      default:
         throw new AssertionError();
      }
   }

   @GwtIncompatible("need BigIntegerMath to adequately test")
   public static int log10(int x, RoundingMode mode) {
      MathPreconditions.checkPositive("x", x);
      int logFloor = log10Floor(x);
      int floorPow = powersOf10[logFloor];
      switch(mode) {
      case UNNECESSARY:
         MathPreconditions.checkRoundingUnnecessary(x == floorPow);
      case DOWN:
      case FLOOR:
         return logFloor;
      case UP:
      case CEILING:
         return logFloor + lessThanBranchFree(floorPow, x);
      case HALF_DOWN:
      case HALF_UP:
      case HALF_EVEN:
         return logFloor + lessThanBranchFree(halfPowersOf10[logFloor], x);
      default:
         throw new AssertionError();
      }
   }

   private static int log10Floor(int x) {
      int y = maxLog10ForLeadingZeros[Integer.numberOfLeadingZeros(x)];
      return y - lessThanBranchFree(x, powersOf10[y]);
   }

   @GwtIncompatible("failing tests")
   public static int pow(int b, int k) {
      MathPreconditions.checkNonNegative("exponent", k);
      switch(b) {
      case -2:
         if(k < 32) {
            return (k & 1) == 0?1 << k:-(1 << k);
         }

         return 0;
      case -1:
         return (k & 1) == 0?1:-1;
      case 0:
         return k == 0?1:0;
      case 1:
         return 1;
      case 2:
         return k < 32?1 << k:0;
      default:
         int accum = 1;

         while(true) {
            switch(k) {
            case 0:
               return accum;
            case 1:
               return b * accum;
            }

            accum *= (k & 1) == 0?1:b;
            b *= b;
            k >>= 1;
         }
      }
   }

   @GwtIncompatible("need BigIntegerMath to adequately test")
   public static int sqrt(int x, RoundingMode mode) {
      MathPreconditions.checkNonNegative("x", x);
      int sqrtFloor = sqrtFloor(x);
      switch(mode) {
      case UNNECESSARY:
         MathPreconditions.checkRoundingUnnecessary(sqrtFloor * sqrtFloor == x);
      case DOWN:
      case FLOOR:
         return sqrtFloor;
      case UP:
      case CEILING:
         return sqrtFloor + lessThanBranchFree(sqrtFloor * sqrtFloor, x);
      case HALF_DOWN:
      case HALF_UP:
      case HALF_EVEN:
         int halfSquare = sqrtFloor * sqrtFloor + sqrtFloor;
         return sqrtFloor + lessThanBranchFree(halfSquare, x);
      default:
         throw new AssertionError();
      }
   }

   private static int sqrtFloor(int x) {
      return (int)Math.sqrt((double)x);
   }

   public static int divide(int p, int q, RoundingMode mode) {
      Preconditions.checkNotNull(mode);
      if(q == 0) {
         throw new ArithmeticException("/ by zero");
      } else {
         int div = p / q;
         int rem = p - q * div;
         if(rem == 0) {
            return div;
         } else {
            int signum = 1 | (p ^ q) >> 31;
            boolean increment;
            switch(mode) {
            case UNNECESSARY:
               MathPreconditions.checkRoundingUnnecessary(rem == 0);
            case DOWN:
               increment = false;
               break;
            case FLOOR:
               increment = signum < 0;
               break;
            case UP:
               increment = true;
               break;
            case CEILING:
               increment = signum > 0;
               break;
            case HALF_DOWN:
            case HALF_UP:
            case HALF_EVEN:
               int absRem = Math.abs(rem);
               int cmpRemToHalfDivisor = absRem - (Math.abs(q) - absRem);
               if(cmpRemToHalfDivisor == 0) {
                  increment = mode == RoundingMode.HALF_UP || mode == RoundingMode.HALF_EVEN & (div & 1) != 0;
               } else {
                  increment = cmpRemToHalfDivisor > 0;
               }
               break;
            default:
               throw new AssertionError();
            }

            return increment?div + signum:div;
         }
      }
   }

   public static int mod(int x, int m) {
      if(m <= 0) {
         throw new ArithmeticException("Modulus " + m + " must be > 0");
      } else {
         int result = x % m;
         return result >= 0?result:result + m;
      }
   }

   public static int gcd(int a, int b) {
      MathPreconditions.checkNonNegative("a", a);
      MathPreconditions.checkNonNegative("b", b);
      if(a == 0) {
         return b;
      } else if(b == 0) {
         return a;
      } else {
         int aTwos = Integer.numberOfTrailingZeros(a);
         a = a >> aTwos;
         int bTwos = Integer.numberOfTrailingZeros(b);

         for(b = b >> bTwos; a != b; a = a >> Integer.numberOfTrailingZeros(a)) {
            int delta = a - b;
            int minDeltaOrZero = delta & delta >> 31;
            a = delta - minDeltaOrZero - minDeltaOrZero;
            b += minDeltaOrZero;
         }

         return a << Math.min(aTwos, bTwos);
      }
   }

   public static int checkedAdd(int a, int b) {
      long result = (long)a + (long)b;
      MathPreconditions.checkNoOverflow(result == (long)((int)result));
      return (int)result;
   }

   public static int checkedSubtract(int a, int b) {
      long result = (long)a - (long)b;
      MathPreconditions.checkNoOverflow(result == (long)((int)result));
      return (int)result;
   }

   public static int checkedMultiply(int a, int b) {
      long result = (long)a * (long)b;
      MathPreconditions.checkNoOverflow(result == (long)((int)result));
      return (int)result;
   }

   public static int checkedPow(int b, int k) {
      MathPreconditions.checkNonNegative("exponent", k);
      switch(b) {
      case -2:
         MathPreconditions.checkNoOverflow(k < 32);
         return (k & 1) == 0?1 << k:-1 << k;
      case -1:
         return (k & 1) == 0?1:-1;
      case 0:
         return k == 0?1:0;
      case 1:
         return 1;
      case 2:
         MathPreconditions.checkNoOverflow(k < 31);
         return 1 << k;
      default:
         int accum = 1;

         while(true) {
            switch(k) {
            case 0:
               return accum;
            case 1:
               return checkedMultiply(accum, b);
            }

            if((k & 1) != 0) {
               accum = checkedMultiply(accum, b);
            }

            k >>= 1;
            if(k > 0) {
               MathPreconditions.checkNoOverflow(-46340 <= b & b <= '딄');
               b *= b;
            }
         }
      }
   }

   public static int factorial(int n) {
      MathPreconditions.checkNonNegative("n", n);
      return n < factorials.length?factorials[n]:Integer.MAX_VALUE;
   }

   @GwtIncompatible("need BigIntegerMath to adequately test")
   public static int binomial(int n, int k) {
      MathPreconditions.checkNonNegative("n", n);
      MathPreconditions.checkNonNegative("k", k);
      Preconditions.checkArgument(k <= n, "k (%s) > n (%s)", new Object[]{Integer.valueOf(k), Integer.valueOf(n)});
      if(k > n >> 1) {
         k = n - k;
      }

      if(k < biggestBinomials.length && n <= biggestBinomials[k]) {
         switch(k) {
         case 0:
            return 1;
         case 1:
            return n;
         default:
            long result = 1L;

            for(int i = 0; i < k; ++i) {
               result = result * (long)(n - i);
               result = result / (long)(i + 1);
            }

            return (int)result;
         }
      } else {
         return Integer.MAX_VALUE;
      }
   }

   public static int mean(int x, int y) {
      return (x & y) + ((x ^ y) >> 1);
   }
}
