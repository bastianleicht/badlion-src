package com.google.common.io;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.annotation.Nullable;

final class MultiInputStream extends InputStream {
   private Iterator it;
   private InputStream in;

   public MultiInputStream(Iterator it) throws IOException {
      this.it = (Iterator)Preconditions.checkNotNull(it);
      this.advance();
   }

   public void close() throws IOException {
      if(this.in != null) {
         try {
            this.in.close();
         } finally {
            this.in = null;
         }
      }

   }

   private void advance() throws IOException {
      this.close();
      if(this.it.hasNext()) {
         this.in = ((ByteSource)this.it.next()).openStream();
      }

   }

   public int available() throws IOException {
      return this.in == null?0:this.in.available();
   }

   public boolean markSupported() {
      return false;
   }

   public int read() throws IOException {
      if(this.in == null) {
         return -1;
      } else {
         int result = this.in.read();
         if(result == -1) {
            this.advance();
            return this.read();
         } else {
            return result;
         }
      }
   }

   public int read(@Nullable byte[] b, int off, int len) throws IOException {
      if(this.in == null) {
         return -1;
      } else {
         int result = this.in.read(b, off, len);
         if(result == -1) {
            this.advance();
            return this.read(b, off, len);
         } else {
            return result;
         }
      }
   }

   public long skip(long n) throws IOException {
      if(this.in != null && n > 0L) {
         long result = this.in.skip(n);
         return result != 0L?result:(this.read() == -1?0L:1L + this.in.skip(n - 1L));
      } else {
         return 0L;
      }
   }
}
