package org.apache.commons.compress.archivers.zip;

import java.io.Serializable;

public final class ZipLong implements Cloneable, Serializable {
   private static final long serialVersionUID = 1L;
   private static final int BYTE_1 = 1;
   private static final int BYTE_1_MASK = 65280;
   private static final int BYTE_1_SHIFT = 8;
   private static final int BYTE_2 = 2;
   private static final int BYTE_2_MASK = 16711680;
   private static final int BYTE_2_SHIFT = 16;
   private static final int BYTE_3 = 3;
   private static final long BYTE_3_MASK = 4278190080L;
   private static final int BYTE_3_SHIFT = 24;
   private final long value;
   public static final ZipLong CFH_SIG = new ZipLong(33639248L);
   public static final ZipLong LFH_SIG = new ZipLong(67324752L);
   public static final ZipLong DD_SIG = new ZipLong(134695760L);
   static final ZipLong ZIP64_MAGIC = new ZipLong(4294967295L);
   public static final ZipLong SINGLE_SEGMENT_SPLIT_MARKER = new ZipLong(808471376L);
   public static final ZipLong AED_SIG = new ZipLong(134630224L);

   public ZipLong(long value) {
      this.value = value;
   }

   public ZipLong(byte[] bytes) {
      this(bytes, 0);
   }

   public ZipLong(byte[] bytes, int offset) {
      this.value = getValue(bytes, offset);
   }

   public byte[] getBytes() {
      return getBytes(this.value);
   }

   public long getValue() {
      return this.value;
   }

   public static byte[] getBytes(long value) {
      byte[] result = new byte[]{(byte)((int)(value & 255L)), (byte)((int)((value & 65280L) >> 8)), (byte)((int)((value & 16711680L) >> 16)), (byte)((int)((value & 4278190080L) >> 24))};
      return result;
   }

   public static long getValue(byte[] bytes, int offset) {
      long value = (long)(bytes[offset + 3] << 24) & 4278190080L;
      value = value + (long)(bytes[offset + 2] << 16 & 16711680);
      value = value + (long)(bytes[offset + 1] << 8 & '\uff00');
      value = value + (long)(bytes[offset] & 255);
      return value;
   }

   public static long getValue(byte[] bytes) {
      return getValue(bytes, 0);
   }

   public boolean equals(Object o) {
      return o != null && o instanceof ZipLong?this.value == ((ZipLong)o).getValue():false;
   }

   public int hashCode() {
      return (int)this.value;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new RuntimeException(var2);
      }
   }

   public String toString() {
      return "ZipLong value: " + this.value;
   }
}
