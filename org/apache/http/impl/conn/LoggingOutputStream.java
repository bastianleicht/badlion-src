package org.apache.http.impl.conn;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.conn.Wire;

@NotThreadSafe
class LoggingOutputStream extends OutputStream {
   private final OutputStream out;
   private final Wire wire;

   public LoggingOutputStream(OutputStream out, Wire wire) {
      this.out = out;
      this.wire = wire;
   }

   public void write(int b) throws IOException {
      try {
         this.wire.output(b);
      } catch (IOException var3) {
         this.wire.output("[write] I/O error: " + var3.getMessage());
         throw var3;
      }
   }

   public void write(byte[] b) throws IOException {
      try {
         this.wire.output(b);
         this.out.write(b);
      } catch (IOException var3) {
         this.wire.output("[write] I/O error: " + var3.getMessage());
         throw var3;
      }
   }

   public void write(byte[] b, int off, int len) throws IOException {
      try {
         this.wire.output(b, off, len);
         this.out.write(b, off, len);
      } catch (IOException var5) {
         this.wire.output("[write] I/O error: " + var5.getMessage());
         throw var5;
      }
   }

   public void flush() throws IOException {
      try {
         this.out.flush();
      } catch (IOException var2) {
         this.wire.output("[flush] I/O error: " + var2.getMessage());
         throw var2;
      }
   }

   public void close() throws IOException {
      try {
         this.out.close();
      } catch (IOException var2) {
         this.wire.output("[close] I/O error: " + var2.getMessage());
         throw var2;
      }
   }
}
