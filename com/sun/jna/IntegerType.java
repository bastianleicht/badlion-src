package com.sun.jna;

import com.sun.jna.FromNativeContext;
import com.sun.jna.NativeMapped;

public abstract class IntegerType extends Number implements NativeMapped {
   private int size;
   private Number number;
   private boolean unsigned;
   private long value;

   public IntegerType(int size) {
      this(size, 0L, false);
   }

   public IntegerType(int size, boolean unsigned) {
      this(size, 0L, unsigned);
   }

   public IntegerType(int size, long value) {
      this(size, value, false);
   }

   public IntegerType(int size, long value, boolean unsigned) {
      this.size = size;
      this.unsigned = unsigned;
      this.setValue(value);
   }

   public void setValue(long value) {
      long truncated = value;
      this.value = value;
      switch(this.size) {
      case 1:
         if(this.unsigned) {
            this.value = value & 255L;
         }

         truncated = (long)((byte)((int)value));
         this.number = new Byte((byte)((int)value));
         break;
      case 2:
         if(this.unsigned) {
            this.value = value & 65535L;
         }

         truncated = (long)((short)((int)value));
         this.number = new Short((short)((int)value));
         break;
      case 3:
      case 5:
      case 6:
      case 7:
      default:
         throw new IllegalArgumentException("Unsupported size: " + this.size);
      case 4:
         if(this.unsigned) {
            this.value = value & 4294967295L;
         }

         truncated = (long)((int)value);
         this.number = new Integer((int)value);
         break;
      case 8:
         this.number = new Long(value);
      }

      if(this.size < 8) {
         long mask = ~((1L << this.size * 8) - 1L);
         if(value < 0L && truncated != value || value >= 0L && (mask & value) != 0L) {
            throw new IllegalArgumentException("Argument value 0x" + Long.toHexString(value) + " exceeds native capacity (" + this.size + " bytes) mask=0x" + Long.toHexString(mask));
         }
      }

   }

   public Object toNative() {
      return this.number;
   }

   public Object fromNative(Object nativeValue, FromNativeContext context) {
      long value = nativeValue == null?0L:((Number)nativeValue).longValue();

      try {
         IntegerType number = (IntegerType)this.getClass().newInstance();
         number.setValue(value);
         return number;
      } catch (InstantiationException var6) {
         throw new IllegalArgumentException("Can\'t instantiate " + this.getClass());
      } catch (IllegalAccessException var7) {
         throw new IllegalArgumentException("Not allowed to instantiate " + this.getClass());
      }
   }

   public Class nativeType() {
      return this.number.getClass();
   }

   public int intValue() {
      return (int)this.value;
   }

   public long longValue() {
      return this.value;
   }

   public float floatValue() {
      return this.number.floatValue();
   }

   public double doubleValue() {
      return this.number.doubleValue();
   }

   public boolean equals(Object rhs) {
      return rhs instanceof IntegerType && this.number.equals(((IntegerType)rhs).number);
   }

   public String toString() {
      return this.number.toString();
   }

   public int hashCode() {
      return this.number.hashCode();
   }
}
