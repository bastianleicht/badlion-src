package org.apache.commons.compress.utils;

import java.io.IOException;
import java.io.InputStream;

public class BoundedInputStream extends InputStream {
   private final InputStream in;
   private long bytesRemaining;

   public BoundedInputStream(InputStream in, long size) {
      this.in = in;
      this.bytesRemaining = size;
   }

   public int read() throws IOException {
      if(this.bytesRemaining > 0L) {
         --this.bytesRemaining;
         return this.in.read();
      } else {
         return -1;
      }
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if(this.bytesRemaining == 0L) {
         return -1;
      } else {
         int bytesToRead = len;
         if((long)len > this.bytesRemaining) {
            bytesToRead = (int)this.bytesRemaining;
         }

         int bytesRead = this.in.read(b, off, bytesToRead);
         if(bytesRead >= 0) {
            this.bytesRemaining -= (long)bytesRead;
         }

         return bytesRead;
      }
   }

   public void close() {
   }
}
