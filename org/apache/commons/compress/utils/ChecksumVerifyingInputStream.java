package org.apache.commons.compress.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Checksum;

public class ChecksumVerifyingInputStream extends InputStream {
   private final InputStream in;
   private long bytesRemaining;
   private final long expectedChecksum;
   private final Checksum checksum;

   public ChecksumVerifyingInputStream(Checksum checksum, InputStream in, long size, long expectedChecksum) {
      this.checksum = checksum;
      this.in = in;
      this.expectedChecksum = expectedChecksum;
      this.bytesRemaining = size;
   }

   public int read() throws IOException {
      if(this.bytesRemaining <= 0L) {
         return -1;
      } else {
         int ret = this.in.read();
         if(ret >= 0) {
            this.checksum.update(ret);
            --this.bytesRemaining;
         }

         if(this.bytesRemaining == 0L && this.expectedChecksum != this.checksum.getValue()) {
            throw new IOException("Checksum verification failed");
         } else {
            return ret;
         }
      }
   }

   public int read(byte[] b) throws IOException {
      return this.read(b, 0, b.length);
   }

   public int read(byte[] b, int off, int len) throws IOException {
      int ret = this.in.read(b, off, len);
      if(ret >= 0) {
         this.checksum.update(b, off, ret);
         this.bytesRemaining -= (long)ret;
      }

      if(this.bytesRemaining <= 0L && this.expectedChecksum != this.checksum.getValue()) {
         throw new IOException("Checksum verification failed");
      } else {
         return ret;
      }
   }

   public long skip(long n) throws IOException {
      return this.read() >= 0?1L:0L;
   }

   public void close() throws IOException {
      this.in.close();
   }
}
