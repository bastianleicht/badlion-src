package org.apache.commons.codec.binary;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.binary.BaseNCodec;

public class BaseNCodecInputStream extends FilterInputStream {
   private final BaseNCodec baseNCodec;
   private final boolean doEncode;
   private final byte[] singleByte = new byte[1];
   private final BaseNCodec.Context context = new BaseNCodec.Context();

   protected BaseNCodecInputStream(InputStream in, BaseNCodec baseNCodec, boolean doEncode) {
      super(in);
      this.doEncode = doEncode;
      this.baseNCodec = baseNCodec;
   }

   public int available() throws IOException {
      return this.context.eof?0:1;
   }

   public synchronized void mark(int readLimit) {
   }

   public boolean markSupported() {
      return false;
   }

   public int read() throws IOException {
      int r;
      for(r = this.read(this.singleByte, 0, 1); r == 0; r = this.read(this.singleByte, 0, 1)) {
         ;
      }

      if(r > 0) {
         byte b = this.singleByte[0];
         return b < 0?256 + b:b;
      } else {
         return -1;
      }
   }

   public int read(byte[] b, int offset, int len) throws IOException {
      if(b == null) {
         throw new NullPointerException();
      } else if(offset >= 0 && len >= 0) {
         if(offset <= b.length && offset + len <= b.length) {
            if(len == 0) {
               return 0;
            } else {
               int readLen;
               for(readLen = 0; readLen == 0; readLen = this.baseNCodec.readResults(b, offset, len, this.context)) {
                  if(!this.baseNCodec.hasData(this.context)) {
                     byte[] buf = new byte[this.doEncode?4096:8192];
                     int c = this.in.read(buf);
                     if(this.doEncode) {
                        this.baseNCodec.encode(buf, 0, c, this.context);
                     } else {
                        this.baseNCodec.decode(buf, 0, c, this.context);
                     }
                  }
               }

               return readLen;
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized void reset() throws IOException {
      throw new IOException("mark/reset not supported");
   }

   public long skip(long n) throws IOException {
      if(n < 0L) {
         throw new IllegalArgumentException("Negative skip length: " + n);
      } else {
         byte[] b = new byte[512];

         long todo;
         int var7;
         for(todo = n; todo > 0L; todo -= (long)var7) {
            len = (int)Math.min((long)b.length, todo);
            var7 = this.read(b, 0, var7);
            if(var7 == -1) {
               break;
            }
         }

         return n - todo;
      }
   }
}
