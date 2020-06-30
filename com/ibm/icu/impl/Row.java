package com.ibm.icu.impl;

import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.Freezable;

public class Row implements Comparable, Cloneable, Freezable {
   protected Object[] items;
   protected boolean frozen;

   public static Row.R2 of(Object p0, Object p1) {
      return new Row.R2(p0, p1);
   }

   public static Row.R3 of(Object p0, Object p1, Object p2) {
      return new Row.R3(p0, p1, p2);
   }

   public static Row.R4 of(Object p0, Object p1, Object p2, Object p3) {
      return new Row.R4(p0, p1, p2, p3);
   }

   public static Row.R5 of(Object p0, Object p1, Object p2, Object p3, Object p4) {
      return new Row.R5(p0, p1, p2, p3, p4);
   }

   public Row set0(Object item) {
      return this.set(0, item);
   }

   public Object get0() {
      return this.items[0];
   }

   public Row set1(Object item) {
      return this.set(1, item);
   }

   public Object get1() {
      return this.items[1];
   }

   public Row set2(Object item) {
      return this.set(2, item);
   }

   public Object get2() {
      return this.items[2];
   }

   public Row set3(Object item) {
      return this.set(3, item);
   }

   public Object get3() {
      return this.items[3];
   }

   public Row set4(Object item) {
      return this.set(4, item);
   }

   public Object get4() {
      return this.items[4];
   }

   protected Row set(int i, Object item) {
      if(this.frozen) {
         throw new UnsupportedOperationException("Attempt to modify frozen object");
      } else {
         this.items[i] = item;
         return this;
      }
   }

   public int hashCode() {
      int sum = this.items.length;

      for(Object item : this.items) {
         sum = sum * 37 + Utility.checkHash(item);
      }

      return sum;
   }

   public boolean equals(Object other) {
      if(other == null) {
         return false;
      } else if(this == other) {
         return true;
      } else {
         try {
            Row<C0, C1, C2, C3, C4> that = (Row)other;
            if(this.items.length != that.items.length) {
               return false;
            } else {
               int i = 0;

               for(Object item : this.items) {
                  if(!Utility.objectEquals(item, that.items[i++])) {
                     return false;
                  }
               }

               return true;
            }
         } catch (Exception var8) {
            return false;
         }
      }
   }

   public int compareTo(Object other) {
      Row<C0, C1, C2, C3, C4> that = (Row)other;
      int result = this.items.length - that.items.length;
      if(result != 0) {
         return result;
      } else {
         int i = 0;

         for(Object item : this.items) {
            result = Utility.checkCompare((Comparable)item, (Comparable)that.items[i++]);
            if(result != 0) {
               return result;
            }
         }

         return 0;
      }
   }

   public String toString() {
      StringBuilder result = new StringBuilder("[");
      boolean first = true;

      for(Object item : this.items) {
         if(first) {
            first = false;
         } else {
            result.append(", ");
         }

         result.append(item);
      }

      return result.append("]").toString();
   }

   public boolean isFrozen() {
      return this.frozen;
   }

   public Row freeze() {
      this.frozen = true;
      return this;
   }

   public Object clone() {
      if(this.frozen) {
         return this;
      } else {
         try {
            Row<C0, C1, C2, C3, C4> result = (Row)super.clone();
            this.items = (Object[])this.items.clone();
            return result;
         } catch (CloneNotSupportedException var2) {
            return null;
         }
      }
   }

   public Row cloneAsThawed() {
      try {
         Row<C0, C1, C2, C3, C4> result = (Row)super.clone();
         this.items = (Object[])this.items.clone();
         result.frozen = false;
         return result;
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   public static class R2 extends Row {
      public R2(Object a, Object b) {
         this.items = new Object[]{a, b};
      }
   }

   public static class R3 extends Row {
      public R3(Object a, Object b, Object c) {
         this.items = new Object[]{a, b, c};
      }
   }

   public static class R4 extends Row {
      public R4(Object a, Object b, Object c, Object d) {
         this.items = new Object[]{a, b, c, d};
      }
   }

   public static class R5 extends Row {
      public R5(Object a, Object b, Object c, Object d, Object e) {
         this.items = new Object[]{a, b, c, d, e};
      }
   }
}
