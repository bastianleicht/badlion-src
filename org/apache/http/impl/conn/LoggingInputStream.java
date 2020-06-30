package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.conn.Wire;

@NotThreadSafe
class LoggingInputStream extends InputStream {
   private final InputStream in;
   private final Wire wire;

   public LoggingInputStream(InputStream in, Wire wire) {
      this.in = in;
      this.wire = wire;
   }

   public int read() throws IOException {
      try {
         int b = this.in.read();
         if(b == -1) {
            this.wire.input("end of stream");
         } else {
            this.wire.input(b);
         }

         return b;
      } catch (IOException var2) {
         this.wire.input("[read] I/O error: " + var2.getMessage());
         throw var2;
      }
   }

   public int read(byte[] b) throws IOException {
      try {
         int bytesRead = this.in.read(b);
         if(bytesRead == -1) {
            this.wire.input("end of stream");
         } else if(bytesRead > 0) {
            this.wire.input(b, 0, bytesRead);
         }

         return bytesRead;
      } catch (IOException var3) {
         this.wire.input("[read] I/O error: " + var3.getMessage());
         throw var3;
      }
   }

   public int read(byte[] b, int off, int len) throws IOException {
      try {
         int bytesRead = this.in.read(b, off, len);
         if(bytesRead == -1) {
            this.wire.input("end of stream");
         } else if(bytesRead > 0) {
            this.wire.input(b, off, bytesRead);
         }

         return bytesRead;
      } catch (IOException var5) {
         this.wire.input("[read] I/O error: " + var5.getMessage());
         throw var5;
      }
   }

   public long skip(long n) throws IOException {
      try {
         return super.skip(n);
      } catch (IOException var4) {
         this.wire.input("[skip] I/O error: " + var4.getMessage());
         throw var4;
      }
   }

   public int available() throws IOException {
      try {
         return this.in.available();
      } catch (IOException var2) {
         this.wire.input("[available] I/O error : " + var2.getMessage());
         throw var2;
      }
   }

   public void mark(int readlimit) {
      super.mark(readlimit);
   }

   public void reset() throws IOException {
      super.reset();
   }

   public boolean markSupported() {
      return false;
   }

   public void close() throws IOException {
      try {
         this.in.close();
      } catch (IOException var2) {
         this.wire.input("[close] I/O error: " + var2.getMessage());
         throw var2;
      }
   }
}
