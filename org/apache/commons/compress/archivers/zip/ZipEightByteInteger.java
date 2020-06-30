package org.apache.commons.compress.archivers.zip;

import java.io.Serializable;
import java.math.BigInteger;

public final class ZipEightByteInteger implements Serializable {
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
   private static final int BYTE_4 = 4;
   private static final long BYTE_4_MASK = 1095216660480L;
   private static final int BYTE_4_SHIFT = 32;
   private static final int BYTE_5 = 5;
   private static final long BYTE_5_MASK = 280375465082880L;
   private static final int BYTE_5_SHIFT = 40;
   private static final int BYTE_6 = 6;
   private static final long BYTE_6_MASK = 71776119061217280L;
   private static final int BYTE_6_SHIFT = 48;
   private static final int BYTE_7 = 7;
   private static final long BYTE_7_MASK = 9151314442816847872L;
   private static final int BYTE_7_SHIFT = 56;
   private static final int LEFTMOST_BIT_SHIFT = 63;
   private static final byte LEFTMOST_BIT = -128;
   private final BigInteger value;
   public static final ZipEightByteInteger ZERO = new ZipEightByteInteger(0L);

   public ZipEightByteInteger(long value) {
      this(BigInteger.valueOf(value));
   }

   public ZipEightByteInteger(BigInteger value) {
      this.value = value;
   }

   public ZipEightByteInteger(byte[] bytes) {
      this(bytes, 0);
   }

   public ZipEightByteInteger(byte[] bytes, int offset) {
      this.value = getValue(bytes, offset);
   }

   public byte[] getBytes() {
      return getBytes(this.value);
   }

   public long getLongValue() {
      return this.value.longValue();
   }

   public BigInteger getValue() {
      return this.value;
   }

   public static byte[] getBytes(long value) {
      return getBytes(BigInteger.valueOf(value));
   }

   public static byte[] getBytes(BigInteger value) {
      byte[] result = new byte[8];
      long val = value.longValue();
      result[0] = (byte)((int)(val & 255L));
      result[1] = (byte)((int)((val & 65280L) >> 8));
      result[2] = (byte)((int)((val & 16711680L) >> 16));
      result[3] = (byte)((int)((val & 4278190080L) >> 24));
      result[4] = (byte)((int)((val & 1095216660480L) >> 32));
      result[5] = (byte)((int)((val & 280375465082880L) >> 40));
      result[6] = (byte)((int)((val & 71776119061217280L) >> 48));
      result[7] = (byte)((int)((val & 9151314442816847872L) >> 56));
      if(value.testBit(63)) {
         result[7] |= -128;
      }

      return result;
   }

   public static long getLongValue(byte[] bytes, int offset) {
      return getValue(bytes, offset).longValue();
   }

   public static BigInteger getValue(byte[] bytes, int offset) {
      long value = (long)bytes[offset + 7] << 56 & 9151314442816847872L;
      value = value + ((long)bytes[offset + 6] << 48 & 71776119061217280L);
      value = value + ((long)bytes[offset + 5] << 40 & 280375465082880L);
      value = value + ((long)bytes[offset + 4] << 32 & 1095216660480L);
      value = value + ((long)bytes[offset + 3] << 24 & 4278190080L);
      value = value + ((long)bytes[offset + 2] << 16 & 16711680L);
      value = value + ((long)bytes[offset + 1] << 8 & 65280L);
      value = value + ((long)bytes[offset] & 255L);
      BigInteger val = BigInteger.valueOf(value);
      return (bytes[offset + 7] & -128) == -128?val.setBit(63):val;
   }

   public static long getLongValue(byte[] bytes) {
      return getLongValue(bytes, 0);
   }

   public static BigInteger getValue(byte[] bytes) {
      return getValue(bytes, 0);
   }

   public boolean equals(Object o) {
      return o != null && o instanceof ZipEightByteInteger?this.value.equals(((ZipEightByteInteger)o).getValue()):false;
   }

   public int hashCode() {
      return this.value.hashCode();
   }

   public String toString() {
      return "ZipEightByteInteger value: " + this.value;
   }
}
