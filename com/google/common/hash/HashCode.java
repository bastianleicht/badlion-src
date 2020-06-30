package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedInts;
import java.io.Serializable;
import java.security.MessageDigest;
import javax.annotation.Nullable;

@Beta
public abstract class HashCode {
   private static final char[] hexDigits = "0123456789abcdef".toCharArray();

   public abstract int bits();

   public abstract int asInt();

   public abstract long asLong();

   public abstract long padToLong();

   public abstract byte[] asBytes();

   public int writeBytesTo(byte[] dest, int offset, int maxLength) {
      maxLength = Ints.min(new int[]{maxLength, this.bits() / 8});
      Preconditions.checkPositionIndexes(offset, offset + maxLength, dest.length);
      this.writeBytesToImpl(dest, offset, maxLength);
      return maxLength;
   }

   abstract void writeBytesToImpl(byte[] var1, int var2, int var3);

   byte[] getBytesInternal() {
      return this.asBytes();
   }

   public static HashCode fromInt(int hash) {
      return new HashCode.IntHashCode(hash);
   }

   public static HashCode fromLong(long hash) {
      return new HashCode.LongHashCode(hash);
   }

   public static HashCode fromBytes(byte[] bytes) {
      Preconditions.checkArgument(bytes.length >= 1, "A HashCode must contain at least 1 byte.");
      return fromBytesNoCopy((byte[])bytes.clone());
   }

   static HashCode fromBytesNoCopy(byte[] bytes) {
      return new HashCode.BytesHashCode(bytes);
   }

   public static HashCode fromString(String string) {
      Preconditions.checkArgument(string.length() >= 2, "input string (%s) must have at least 2 characters", new Object[]{string});
      Preconditions.checkArgument(string.length() % 2 == 0, "input string (%s) must have an even number of characters", new Object[]{string});
      byte[] bytes = new byte[string.length() / 2];

      for(int i = 0; i < string.length(); i += 2) {
         int ch1 = decode(string.charAt(i)) << 4;
         int ch2 = decode(string.charAt(i + 1));
         bytes[i / 2] = (byte)(ch1 + ch2);
      }

      return fromBytesNoCopy(bytes);
   }

   private static int decode(char ch) {
      if(ch >= 48 && ch <= 57) {
         return ch - 48;
      } else if(ch >= 97 && ch <= 102) {
         return ch - 97 + 10;
      } else {
         throw new IllegalArgumentException("Illegal hexadecimal character: " + ch);
      }
   }

   public final boolean equals(@Nullable Object object) {
      if(object instanceof HashCode) {
         HashCode that = (HashCode)object;
         return MessageDigest.isEqual(this.asBytes(), that.asBytes());
      } else {
         return false;
      }
   }

   public final int hashCode() {
      if(this.bits() >= 32) {
         return this.asInt();
      } else {
         byte[] bytes = this.asBytes();
         int val = bytes[0] & 255;

         for(int i = 1; i < bytes.length; ++i) {
            val |= (bytes[i] & 255) << i * 8;
         }

         return val;
      }
   }

   public final String toString() {
      byte[] bytes = this.asBytes();
      StringBuilder sb = new StringBuilder(2 * bytes.length);

      for(byte b : bytes) {
         sb.append(hexDigits[b >> 4 & 15]).append(hexDigits[b & 15]);
      }

      return sb.toString();
   }

   private static final class BytesHashCode extends HashCode implements Serializable {
      final byte[] bytes;
      private static final long serialVersionUID = 0L;

      BytesHashCode(byte[] bytes) {
         this.bytes = (byte[])Preconditions.checkNotNull(bytes);
      }

      public int bits() {
         return this.bytes.length * 8;
      }

      public byte[] asBytes() {
         return (byte[])this.bytes.clone();
      }

      public int asInt() {
         Preconditions.checkState(this.bytes.length >= 4, "HashCode#asInt() requires >= 4 bytes (it only has %s bytes).", new Object[]{Integer.valueOf(this.bytes.length)});
         return this.bytes[0] & 255 | (this.bytes[1] & 255) << 8 | (this.bytes[2] & 255) << 16 | (this.bytes[3] & 255) << 24;
      }

      public long asLong() {
         Preconditions.checkState(this.bytes.length >= 8, "HashCode#asLong() requires >= 8 bytes (it only has %s bytes).", new Object[]{Integer.valueOf(this.bytes.length)});
         return this.padToLong();
      }

      public long padToLong() {
         long retVal = (long)(this.bytes[0] & 255);

         for(int i = 1; i < Math.min(this.bytes.length, 8); ++i) {
            retVal |= ((long)this.bytes[i] & 255L) << i * 8;
         }

         return retVal;
      }

      void writeBytesToImpl(byte[] dest, int offset, int maxLength) {
         System.arraycopy(this.bytes, 0, dest, offset, maxLength);
      }

      byte[] getBytesInternal() {
         return this.bytes;
      }
   }

   private static final class IntHashCode extends HashCode implements Serializable {
      final int hash;
      private static final long serialVersionUID = 0L;

      IntHashCode(int hash) {
         this.hash = hash;
      }

      public int bits() {
         return 32;
      }

      public byte[] asBytes() {
         return new byte[]{(byte)this.hash, (byte)(this.hash >> 8), (byte)(this.hash >> 16), (byte)(this.hash >> 24)};
      }

      public int asInt() {
         return this.hash;
      }

      public long asLong() {
         throw new IllegalStateException("this HashCode only has 32 bits; cannot create a long");
      }

      public long padToLong() {
         return UnsignedInts.toLong(this.hash);
      }

      void writeBytesToImpl(byte[] dest, int offset, int maxLength) {
         for(int i = 0; i < maxLength; ++i) {
            dest[offset + i] = (byte)(this.hash >> i * 8);
         }

      }
   }

   private static final class LongHashCode extends HashCode implements Serializable {
      final long hash;
      private static final long serialVersionUID = 0L;

      LongHashCode(long hash) {
         this.hash = hash;
      }

      public int bits() {
         return 64;
      }

      public byte[] asBytes() {
         return new byte[]{(byte)((int)this.hash), (byte)((int)(this.hash >> 8)), (byte)((int)(this.hash >> 16)), (byte)((int)(this.hash >> 24)), (byte)((int)(this.hash >> 32)), (byte)((int)(this.hash >> 40)), (byte)((int)(this.hash >> 48)), (byte)((int)(this.hash >> 56))};
      }

      public int asInt() {
         return (int)this.hash;
      }

      public long asLong() {
         return this.hash;
      }

      public long padToLong() {
         return this.hash;
      }

      void writeBytesToImpl(byte[] dest, int offset, int maxLength) {
         for(int i = 0; i < maxLength; ++i) {
            dest[offset + i] = (byte)((int)(this.hash >> i * 8));
         }

      }
   }
}
