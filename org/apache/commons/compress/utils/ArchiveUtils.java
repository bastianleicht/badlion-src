package org.apache.commons.compress.utils;

import java.io.UnsupportedEncodingException;
import org.apache.commons.compress.archivers.ArchiveEntry;

public class ArchiveUtils {
   public static String toString(ArchiveEntry entry) {
      StringBuilder sb = new StringBuilder();
      sb.append((char)(entry.isDirectory()?'d':'-'));
      String size = Long.toString(entry.getSize());
      sb.append(' ');

      for(int i = 7; i > size.length(); --i) {
         sb.append(' ');
      }

      sb.append(size);
      sb.append(' ').append(entry.getName());
      return sb.toString();
   }

   public static boolean matchAsciiBuffer(String expected, byte[] buffer, int offset, int length) {
      byte[] buffer1;
      try {
         buffer1 = expected.getBytes("US-ASCII");
      } catch (UnsupportedEncodingException var6) {
         throw new RuntimeException(var6);
      }

      return isEqual(buffer1, 0, buffer1.length, buffer, offset, length, false);
   }

   public static boolean matchAsciiBuffer(String expected, byte[] buffer) {
      return matchAsciiBuffer(expected, buffer, 0, buffer.length);
   }

   public static byte[] toAsciiBytes(String inputString) {
      try {
         return inputString.getBytes("US-ASCII");
      } catch (UnsupportedEncodingException var2) {
         throw new RuntimeException(var2);
      }
   }

   public static String toAsciiString(byte[] inputBytes) {
      try {
         return new String(inputBytes, "US-ASCII");
      } catch (UnsupportedEncodingException var2) {
         throw new RuntimeException(var2);
      }
   }

   public static String toAsciiString(byte[] inputBytes, int offset, int length) {
      try {
         return new String(inputBytes, offset, length, "US-ASCII");
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException(var4);
      }
   }

   public static boolean isEqual(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2, boolean ignoreTrailingNulls) {
      int minLen = length1 < length2?length1:length2;

      for(int i = 0; i < minLen; ++i) {
         if(buffer1[offset1 + i] != buffer2[offset2 + i]) {
            return false;
         }
      }

      if(length1 == length2) {
         return true;
      } else if(!ignoreTrailingNulls) {
         return false;
      } else {
         if(length1 > length2) {
            for(int i = length2; i < length1; ++i) {
               if(buffer1[offset1 + i] != 0) {
                  return false;
               }
            }
         } else {
            for(int i = length1; i < length2; ++i) {
               if(buffer2[offset2 + i] != 0) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public static boolean isEqual(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2) {
      return isEqual(buffer1, offset1, length1, buffer2, offset2, length2, false);
   }

   public static boolean isEqual(byte[] buffer1, byte[] buffer2) {
      return isEqual(buffer1, 0, buffer1.length, buffer2, 0, buffer2.length, false);
   }

   public static boolean isEqual(byte[] buffer1, byte[] buffer2, boolean ignoreTrailingNulls) {
      return isEqual(buffer1, 0, buffer1.length, buffer2, 0, buffer2.length, ignoreTrailingNulls);
   }

   public static boolean isEqualWithNull(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2) {
      return isEqual(buffer1, offset1, length1, buffer2, offset2, length2, true);
   }

   public static boolean isArrayZero(byte[] a, int size) {
      for(int i = 0; i < size; ++i) {
         if(a[i] != 0) {
            return false;
         }
      }

      return true;
   }
}
