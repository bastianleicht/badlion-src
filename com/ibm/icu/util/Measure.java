package com.ibm.icu.util;

import com.ibm.icu.util.MeasureUnit;

public abstract class Measure {
   private Number number;
   private MeasureUnit unit;

   protected Measure(Number number, MeasureUnit unit) {
      if(number != null && unit != null) {
         this.number = number;
         this.unit = unit;
      } else {
         throw new NullPointerException();
      }
   }

   public boolean equals(Object obj) {
      if(obj == null) {
         return false;
      } else if(obj == this) {
         return true;
      } else {
         try {
            Measure m = (Measure)obj;
            return this.unit.equals(m.unit) && numbersEqual(this.number, m.number);
         } catch (ClassCastException var3) {
            return false;
         }
      }
   }

   private static boolean numbersEqual(Number a, Number b) {
      return a.equals(b)?true:a.doubleValue() == b.doubleValue();
   }

   public int hashCode() {
      return this.number.hashCode() ^ this.unit.hashCode();
   }

   public String toString() {
      return this.number.toString() + ' ' + this.unit.toString();
   }

   public Number getNumber() {
      return this.number;
   }

   public MeasureUnit getUnit() {
      return this.unit;
   }
}
