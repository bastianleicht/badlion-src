package org.apache.commons.compress.archivers.cpio;

class CpioUtil {
   static long fileType(long mode) {
      return mode & 61440L;
   }

   static long byteArray2long(byte[] number, boolean swapHalfWord) {
      if(number.length % 2 != 0) {
         throw new UnsupportedOperationException();
      } else {
         long ret = 0L;
         int pos = 0;
         byte[] tmp_number = new byte[number.length];
         System.arraycopy(number, 0, tmp_number, 0, number.length);
         if(!swapHalfWord) {
            byte tmp = 0;

            for(pos = 0; pos < tmp_number.length; ++pos) {
               tmp = tmp_number[pos];
               tmp_number[pos++] = tmp_number[pos];
               tmp_number[pos] = tmp;
            }
         }

         ret = (long)(tmp_number[0] & 255);

         for(pos = 1; pos < tmp_number.length; ++pos) {
            ret = ret << 8;
            ret = ret | (long)(tmp_number[pos] & 255);
         }

         return ret;
      }
   }

   static byte[] long2byteArray(long number, int length, boolean swapHalfWord) {
      // $FF: Couldn't be decompiled
   }
}
