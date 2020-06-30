package org.apache.commons.compress.archivers.dump;

import java.io.IOException;
import org.apache.commons.compress.archivers.zip.ZipEncoding;

class DumpArchiveUtil {
   public static int calculateChecksum(byte[] buffer) {
      int calc = 0;

      for(int i = 0; i < 256; ++i) {
         calc += convert32(buffer, 4 * i);
      }

      return 84446 - (calc - convert32(buffer, 28));
   }

   public static final boolean verify(byte[] buffer) {
      int magic = convert32(buffer, 24);
      if(magic != '\uea6c') {
         return false;
      } else {
         int checksum = convert32(buffer, 28);
         return checksum == calculateChecksum(buffer);
      }
   }

   public static final int getIno(byte[] buffer) {
      return convert32(buffer, 20);
   }

   public static final long convert64(byte[] buffer, int offset) {
      long i = 0L;
      i = i + ((long)buffer[offset + 7] << 56);
      i = i + ((long)buffer[offset + 6] << 48 & 71776119061217280L);
      i = i + ((long)buffer[offset + 5] << 40 & 280375465082880L);
      i = i + ((long)buffer[offset + 4] << 32 & 1095216660480L);
      i = i + ((long)buffer[offset + 3] << 24 & 4278190080L);
      i = i + ((long)buffer[offset + 2] << 16 & 16711680L);
      i = i + ((long)buffer[offset + 1] << 8 & 65280L);
      i = i + ((long)buffer[offset] & 255L);
      return i;
   }

   public static final int convert32(byte[] buffer, int offset) {
      int i = 0;
      i = buffer[offset + 3] << 24;
      i = i + (buffer[offset + 2] << 16 & 16711680);
      i = i + (buffer[offset + 1] << 8 & '\uff00');
      i = i + (buffer[offset] & 255);
      return i;
   }

   public static final int convert16(byte[] buffer, int offset) {
      int i = 0;
      i = i + (buffer[offset + 1] << 8 & '\uff00');
      i = i + (buffer[offset] & 255);
      return i;
   }

   static String decode(ZipEncoding encoding, byte[] b, int offset, int len) throws IOException {
      byte[] copy = new byte[len];
      System.arraycopy(b, offset, copy, 0, len);
      return encoding.decode(copy);
   }
}
