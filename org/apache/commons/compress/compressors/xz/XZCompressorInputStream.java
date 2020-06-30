package org.apache.commons.compress.compressors.xz;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.tukaani.xz.SingleXZInputStream;
import org.tukaani.xz.XZ;
import org.tukaani.xz.XZInputStream;

public class XZCompressorInputStream extends CompressorInputStream {
   private final InputStream in;

   public static boolean matches(byte[] signature, int length) {
      if(length < XZ.HEADER_MAGIC.length) {
         return false;
      } else {
         for(int i = 0; i < XZ.HEADER_MAGIC.length; ++i) {
            if(signature[i] != XZ.HEADER_MAGIC[i]) {
               return false;
            }
         }

         return true;
      }
   }

   public XZCompressorInputStream(InputStream inputStream) throws IOException {
      this(inputStream, false);
   }

   public XZCompressorInputStream(InputStream inputStream, boolean decompressConcatenated) throws IOException {
      if(decompressConcatenated) {
         this.in = new XZInputStream(inputStream);
      } else {
         this.in = new SingleXZInputStream(inputStream);
      }

   }

   public int read() throws IOException {
      int ret = this.in.read();
      this.count(ret == -1?-1:1);
      return ret;
   }

   public int read(byte[] buf, int off, int len) throws IOException {
      int ret = this.in.read(buf, off, len);
      this.count(ret);
      return ret;
   }

   public long skip(long n) throws IOException {
      return this.in.skip(n);
   }

   public int available() throws IOException {
      return this.in.available();
   }

   public void close() throws IOException {
      this.in.close();
   }
}
