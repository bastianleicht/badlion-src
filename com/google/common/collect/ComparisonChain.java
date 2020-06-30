package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import java.util.Comparator;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ComparisonChain {
   private static final ComparisonChain ACTIVE = new ComparisonChain(null) {
      public ComparisonChain compare(Comparable left, Comparable right) {
         return this.classify(left.compareTo(right));
      }

      public ComparisonChain compare(@Nullable Object left, @Nullable Object right, Comparator comparator) {
         return this.classify(comparator.compare(left, right));
      }

      public ComparisonChain compare(int left, int right) {
         return this.classify(Ints.compare(left, right));
      }

      public ComparisonChain compare(long left, long right) {
         return this.classify(Longs.compare(left, right));
      }

      public ComparisonChain compare(float left, float right) {
         return this.classify(Float.compare(left, right));
      }

      public ComparisonChain compare(double left, double right) {
         return this.classify(Double.compare(left, right));
      }

      public ComparisonChain compareTrueFirst(boolean left, boolean right) {
         return this.classify(Booleans.compare(right, left));
      }

      public ComparisonChain compareFalseFirst(boolean left, boolean right) {
         return this.classify(Booleans.compare(left, right));
      }

      ComparisonChain classify(int result) {
         return result < 0?ComparisonChain.LESS:(result > 0?ComparisonChain.GREATER:ComparisonChain.ACTIVE);
      }

      public int result() {
         return 0;
      }
   };
   private static final ComparisonChain LESS = new ComparisonChain.InactiveComparisonChain(-1);
   private static final ComparisonChain GREATER = new ComparisonChain.InactiveComparisonChain(1);

   private ComparisonChain() {
   }

   public static ComparisonChain start() {
      return ACTIVE;
   }

   public abstract ComparisonChain compare(Comparable var1, Comparable var2);

   public abstract ComparisonChain compare(@Nullable Object var1, @Nullable Object var2, Comparator var3);

   public abstract ComparisonChain compare(int var1, int var2);

   public abstract ComparisonChain compare(long var1, long var3);

   public abstract ComparisonChain compare(float var1, float var2);

   public abstract ComparisonChain compare(double var1, double var3);

   public abstract ComparisonChain compareTrueFirst(boolean var1, boolean var2);

   public abstract ComparisonChain compareFalseFirst(boolean var1, boolean var2);

   public abstract int result();

   private static final class InactiveComparisonChain extends ComparisonChain {
      final int result;

      InactiveComparisonChain(int result) {
         super(null);
         this.result = result;
      }

      public ComparisonChain compare(@Nullable Comparable left, @Nullable Comparable right) {
         return this;
      }

      public ComparisonChain compare(@Nullable Object left, @Nullable Object right, @Nullable Comparator comparator) {
         return this;
      }

      public ComparisonChain compare(int left, int right) {
         return this;
      }

      public ComparisonChain compare(long left, long right) {
         return this;
      }

      public ComparisonChain compare(float left, float right) {
         return this;
      }

      public ComparisonChain compare(double left, double right) {
         return this;
      }

      public ComparisonChain compareTrueFirst(boolean left, boolean right) {
         return this;
      }

      public ComparisonChain compareFalseFirst(boolean left, boolean right) {
         return this;
      }

      public int result() {
         return this.result;
      }
   }
}
