package org.apache.commons.compress.archivers.zip;

import java.io.Serializable;

public final class ZipShort implements Cloneable, Serializable {
   private static final long serialVersionUID = 1L;
   private static final int BYTE_1_MASK = 65280;
   private static final int BYTE_1_SHIFT = 8;
   private final int value;

   public ZipShort(int value) {
      this.value = value;
   }

   public ZipShort(byte[] bytes) {
      this(bytes, 0);
   }

   public ZipShort(byte[] bytes, int offset) {
      this.value = getValue(bytes, offset);
   }

   public byte[] getBytes() {
      byte[] result = new byte[]{(byte)(this.value & 255), (byte)((this.value & '\uff00') >> 8)};
      return result;
   }

   public int getValue() {
      return this.value;
   }

   public static byte[] getBytes(int value) {
      byte[] result = new byte[]{(byte)(value & 255), (byte)((value & '\uff00') >> 8)};
      return result;
   }

   public static int getValue(byte[] bytes, int offset) {
      int value = bytes[offset + 1] << 8 & '\uff00';
      value = value + (bytes[offset] & 255);
      return value;
   }

   public static int getValue(byte[] bytes) {
      return getValue(bytes, 0);
   }

   public boolean equals(Object o) {
      return o != null && o instanceof ZipShort?this.value == ((ZipShort)o).getValue():false;
   }

   public int hashCode() {
      return this.value;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new RuntimeException(var2);
      }
   }

   public String toString() {
      return "ZipShort value: " + this.value;
   }
}
