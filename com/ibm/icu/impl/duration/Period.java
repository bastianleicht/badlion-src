package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.TimeUnit;

public final class Period {
   final byte timeLimit;
   final boolean inFuture;
   final int[] counts;

   public static Period at(float count, TimeUnit unit) {
      checkCount(count);
      return new Period(0, false, count, unit);
   }

   public static Period moreThan(float count, TimeUnit unit) {
      checkCount(count);
      return new Period(2, false, count, unit);
   }

   public static Period lessThan(float count, TimeUnit unit) {
      checkCount(count);
      return new Period(1, false, count, unit);
   }

   public Period and(float count, TimeUnit unit) {
      checkCount(count);
      return this.setTimeUnitValue(unit, count);
   }

   public Period omit(TimeUnit unit) {
      return this.setTimeUnitInternalValue(unit, 0);
   }

   public Period at() {
      return this.setTimeLimit((byte)0);
   }

   public Period moreThan() {
      return this.setTimeLimit((byte)2);
   }

   public Period lessThan() {
      return this.setTimeLimit((byte)1);
   }

   public Period inFuture() {
      return this.setFuture(true);
   }

   public Period inPast() {
      return this.setFuture(false);
   }

   public Period inFuture(boolean future) {
      return this.setFuture(future);
   }

   public Period inPast(boolean past) {
      return this.setFuture(!past);
   }

   public boolean isSet() {
      for(int i = 0; i < this.counts.length; ++i) {
         if(this.counts[i] != 0) {
            return true;
         }
      }

      return false;
   }

   public boolean isSet(TimeUnit unit) {
      return this.counts[unit.ordinal] > 0;
   }

   public float getCount(TimeUnit unit) {
      int ord = unit.ordinal;
      return this.counts[ord] == 0?0.0F:(float)(this.counts[ord] - 1) / 1000.0F;
   }

   public boolean isInFuture() {
      return this.inFuture;
   }

   public boolean isInPast() {
      return !this.inFuture;
   }

   public boolean isMoreThan() {
      return this.timeLimit == 2;
   }

   public boolean isLessThan() {
      return this.timeLimit == 1;
   }

   public boolean equals(Object rhs) {
      try {
         return this.equals((Period)rhs);
      } catch (ClassCastException var3) {
         return false;
      }
   }

   public boolean equals(Period rhs) {
      if(rhs != null && this.timeLimit == rhs.timeLimit && this.inFuture == rhs.inFuture) {
         for(int i = 0; i < this.counts.length; ++i) {
            if(this.counts[i] != rhs.counts[i]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int hc = this.timeLimit << 1 | (this.inFuture?1:0);

      for(int i = 0; i < this.counts.length; ++i) {
         hc = hc << 2 ^ this.counts[i];
      }

      return hc;
   }

   private Period(int limit, boolean future, float count, TimeUnit unit) {
      this.timeLimit = (byte)limit;
      this.inFuture = future;
      this.counts = new int[TimeUnit.units.length];
      this.counts[unit.ordinal] = (int)(count * 1000.0F) + 1;
   }

   Period(int timeLimit, boolean inFuture, int[] counts) {
      this.timeLimit = (byte)timeLimit;
      this.inFuture = inFuture;
      this.counts = counts;
   }

   private Period setTimeUnitValue(TimeUnit unit, float value) {
      if(value < 0.0F) {
         throw new IllegalArgumentException("value: " + value);
      } else {
         return this.setTimeUnitInternalValue(unit, (int)(value * 1000.0F) + 1);
      }
   }

   private Period setTimeUnitInternalValue(TimeUnit unit, int value) {
      int ord = unit.ordinal;
      if(this.counts[ord] == value) {
         return this;
      } else {
         int[] newCounts = new int[this.counts.length];

         for(int i = 0; i < this.counts.length; ++i) {
            newCounts[i] = this.counts[i];
         }

         newCounts[ord] = value;
         return new Period(this.timeLimit, this.inFuture, newCounts);
      }
   }

   private Period setFuture(boolean future) {
      return this.inFuture != future?new Period(this.timeLimit, future, this.counts):this;
   }

   private Period setTimeLimit(byte limit) {
      return this.timeLimit != limit?new Period(limit, this.inFuture, this.counts):this;
   }

   private static void checkCount(float count) {
      if(count < 0.0F) {
         throw new IllegalArgumentException("count (" + count + ") cannot be negative");
      }
   }
}
