package org.apache.commons.compress.archivers.tar;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;

public class TarUtils {
   private static final int BYTE_MASK = 255;
   static final ZipEncoding DEFAULT_ENCODING = ZipEncodingHelper.getZipEncoding((String)null);
   static final ZipEncoding FALLBACK_ENCODING = new ZipEncoding() {
      public boolean canEncode(String name) {
         return true;
      }

      public ByteBuffer encode(String name) {
         int length = name.length();
         byte[] buf = new byte[length];

         for(int i = 0; i < length; ++i) {
            buf[i] = (byte)name.charAt(i);
         }

         return ByteBuffer.wrap(buf);
      }

      public String decode(byte[] buffer) {
         int length = buffer.length;
         StringBuilder result = new StringBuilder(length);

         for(int i = 0; i < length; ++i) {
            byte b = buffer[i];
            if(b == 0) {
               break;
            }

            result.append((char)(b & 255));
         }

         return result.toString();
      }
   };

   public static long parseOctal(byte[] buffer, int offset, int length) {
      long result = 0L;
      int end = offset + length;
      int start = offset;
      if(length < 2) {
         throw new IllegalArgumentException("Length " + length + " must be at least 2");
      } else if(buffer[offset] == 0) {
         return 0L;
      } else {
         while(start < end && buffer[start] == 32) {
            ++start;
         }

         for(byte trailer = buffer[end - 1]; start < end && (trailer == 0 || trailer == 32); trailer = buffer[end - 1]) {
            --end;
         }

         while(start < end) {
            byte currentByte = buffer[start];
            if(currentByte < 48 || currentByte > 55) {
               throw new IllegalArgumentException(exceptionMessage(buffer, offset, length, start, currentByte));
            }

            result = (result << 3) + (long)(currentByte - 48);
            ++start;
         }

         return result;
      }
   }

   public static long parseOctalOrBinary(byte[] buffer, int offset, int length) {
      if((buffer[offset] & 128) == 0) {
         return parseOctal(buffer, offset, length);
      } else {
         boolean negative = buffer[offset] == -1;
         return length < 9?parseBinaryLong(buffer, offset, length, negative):parseBinaryBigInteger(buffer, offset, length, negative);
      }
   }

   private static long parseBinaryLong(byte[] buffer, int offset, int length, boolean negative) {
      if(length >= 9) {
         throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number" + " exceeds maximum signed long" + " value");
      } else {
         long val = 0L;

         for(int i = 1; i < length; ++i) {
            val = (val << 8) + (long)(buffer[offset + i] & 255);
         }

         if(negative) {
            --val;
            val ^= (long)Math.pow(2.0D, (double)((length - 1) * 8)) - 1L;
         }

         return negative?-val:val;
      }
   }

   private static long parseBinaryBigInteger(byte[] buffer, int offset, int length, boolean negative) {
      byte[] remainder = new byte[length - 1];
      System.arraycopy(buffer, offset + 1, remainder, 0, length - 1);
      BigInteger val = new BigInteger(remainder);
      if(negative) {
         val = val.add(BigInteger.valueOf(-1L)).not();
      }

      if(val.bitLength() > 63) {
         throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number" + " exceeds maximum signed long" + " value");
      } else {
         return negative?-val.longValue():val.longValue();
      }
   }

   public static boolean parseBoolean(byte[] buffer, int offset) {
      return buffer[offset] == 1;
   }

   private static String exceptionMessage(byte[] buffer, int offset, int length, int current, byte currentByte) {
      String string = new String(buffer, offset, length);
      string = string.replaceAll("\u0000", "{NUL}");
      String s = "Invalid byte " + currentByte + " at offset " + (current - offset) + " in \'" + string + "\' len=" + length;
      return s;
   }

   public static String parseName(byte[] buffer, int offset, int length) {
      try {
         return parseName(buffer, offset, length, DEFAULT_ENCODING);
      } catch (IOException var6) {
         try {
            return parseName(buffer, offset, length, FALLBACK_ENCODING);
         } catch (IOException var5) {
            throw new RuntimeException(var5);
         }
      }
   }

   public static String parseName(byte[] buffer, int offset, int length, ZipEncoding encoding) throws IOException {
      int len;
      for(len = length; len > 0 && buffer[offset + len - 1] == 0; --len) {
         ;
      }

      if(len > 0) {
         byte[] b = new byte[len];
         System.arraycopy(buffer, offset, b, 0, len);
         return encoding.decode(b);
      } else {
         return "";
      }
   }

   public static int formatNameBytes(String name, byte[] buf, int offset, int length) {
      try {
         return formatNameBytes(name, buf, offset, length, DEFAULT_ENCODING);
      } catch (IOException var7) {
         try {
            return formatNameBytes(name, buf, offset, length, FALLBACK_ENCODING);
         } catch (IOException var6) {
            throw new RuntimeException(var6);
         }
      }
   }

   public static int formatNameBytes(String name, byte[] buf, int offset, int length, ZipEncoding encoding) throws IOException {
      int len = name.length();

      ByteBuffer b;
      for(b = encoding.encode(name); b.limit() > length && len > 0; b = encoding.encode(name.substring(0, len))) {
         --len;
      }

      int limit = b.limit() - b.position();
      System.arraycopy(b.array(), b.arrayOffset(), buf, offset, limit);

      for(int i = limit; i < length; ++i) {
         buf[offset + i] = 0;
      }

      return offset + length;
   }

   public static void formatUnsignedOctalString(long value, byte[] buffer, int offset, int length) {
      int remaining = length - 1;
      if(value == 0L) {
         buffer[offset + remaining--] = 48;
      } else {
         long val;
         for(val = value; remaining >= 0 && val != 0L; --remaining) {
            buffer[offset + remaining] = (byte)(48 + (byte)((int)(val & 7L)));
            val >>>= 3;
         }

         if(val != 0L) {
            throw new IllegalArgumentException(value + "=" + Long.toOctalString(value) + " will not fit in octal number buffer of length " + length);
         }
      }

      while(remaining >= 0) {
         buffer[offset + remaining] = 48;
         --remaining;
      }

   }

   public static int formatOctalBytes(long value, byte[] buf, int offset, int length) {
      int idx = length - 2;
      formatUnsignedOctalString(value, buf, offset, idx);
      buf[offset + idx++] = 32;
      buf[offset + idx] = 0;
      return offset + length;
   }

   public static int formatLongOctalBytes(long value, byte[] buf, int offset, int length) {
      int idx = length - 1;
      formatUnsignedOctalString(value, buf, offset, idx);
      buf[offset + idx] = 32;
      return offset + length;
   }

   public static int formatLongOctalOrBinaryBytes(long value, byte[] buf, int offset, int length) {
      long maxAsOctalChar = length == 8?2097151L:8589934591L;
      boolean negative = value < 0L;
      if(!negative && value <= maxAsOctalChar) {
         return formatLongOctalBytes(value, buf, offset, length);
      } else {
         if(length < 9) {
            formatLongBinary(value, buf, offset, length, negative);
         }

         formatBigIntegerBinary(value, buf, offset, length, negative);
         buf[offset] = (byte)(negative?255:128);
         return offset + length;
      }
   }

   private static void formatLongBinary(long value, byte[] buf, int offset, int length, boolean negative) {
      // $FF: Couldn't be decompiled
   }

   private static void formatBigIntegerBinary(long value, byte[] buf, int offset, int length, boolean negative) {
      BigInteger val = BigInteger.valueOf(value);
      byte[] b = val.toByteArray();
      int len = b.length;
      int off = offset + length - len;
      System.arraycopy(b, 0, buf, off, len);
      byte fill = (byte)(negative?255:0);

      for(int i = offset + 1; i < off; ++i) {
         buf[i] = fill;
      }

   }

   public static int formatCheckSumOctalBytes(long value, byte[] buf, int offset, int length) {
      int idx = length - 2;
      formatUnsignedOctalString(value, buf, offset, idx);
      buf[offset + idx++] = 0;
      buf[offset + idx] = 32;
      return offset + length;
   }

   public static long computeCheckSum(byte[] buf) {
      long sum = 0L;

      for(byte element : buf) {
         sum += (long)(255 & element);
      }

      return sum;
   }

   public static boolean verifyCheckSum(byte[] header) {
      long storedSum = 0L;
      long unsignedSum = 0L;
      long signedSum = 0L;
      int digits = 0;

      for(int i = 0; i < header.length; ++i) {
         byte b = header[i];
         if(148 <= i && i < 156) {
            if(48 <= b && b <= 55 && digits++ < 6) {
               storedSum = storedSum * 8L + (long)b - 48L;
            } else if(digits > 0) {
               digits = 6;
            }

            b = 32;
         }

         unsignedSum += (long)(255 & b);
         signedSum += (long)b;
      }

      return storedSum == unsignedSum || storedSum == signedSum || storedSum > unsignedSum;
   }
}
