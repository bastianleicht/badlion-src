package com.google.common.math;

import com.google.common.annotations.GwtCompatible;
import java.math.BigInteger;
import javax.annotation.Nullable;

@GwtCompatible
final class MathPreconditions {
   static int checkPositive(@Nullable String role, int x) {
      if(x <= 0) {
         throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
      } else {
         return x;
      }
   }

   static long checkPositive(@Nullable String role, long x) {
      if(x <= 0L) {
         throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
      } else {
         return x;
      }
   }

   static BigInteger checkPositive(@Nullable String role, BigInteger x) {
      if(x.signum() <= 0) {
         throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
      } else {
         return x;
      }
   }

   static int checkNonNegative(@Nullable String role, int x) {
      if(x < 0) {
         throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
      } else {
         return x;
      }
   }

   static long checkNonNegative(@Nullable String role, long x) {
      if(x < 0L) {
         throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
      } else {
         return x;
      }
   }

   static BigInteger checkNonNegative(@Nullable String role, BigInteger x) {
      if(x.signum() < 0) {
         throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
      } else {
         return x;
      }
   }

   static double checkNonNegative(@Nullable String role, double x) {
      if(x < 0.0D) {
         throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
      } else {
         return x;
      }
   }

   static void checkRoundingUnnecessary(boolean condition) {
      if(!condition) {
         throw new ArithmeticException("mode was UNNECESSARY, but rounding was necessary");
      }
   }

   static void checkInRange(boolean condition) {
      if(!condition) {
         throw new ArithmeticException("not in range");
      }
   }

   static void checkNoOverflow(boolean condition) {
      if(!condition) {
         throw new ArithmeticException("overflow");
      }
   }
}
