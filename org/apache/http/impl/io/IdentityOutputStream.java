package org.apache.http.impl.io;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.util.Args;

@NotThreadSafe
public class IdentityOutputStream extends OutputStream {
   private final SessionOutputBuffer out;
   private boolean closed = false;

   public IdentityOutputStream(SessionOutputBuffer out) {
      this.out = (SessionOutputBuffer)Args.notNull(out, "Session output buffer");
   }

   public void close() throws IOException {
      if(!this.closed) {
         this.closed = true;
         this.out.flush();
      }

   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public void write(byte[] b, int off, int len) throws IOException {
      if(this.closed) {
         throw new IOException("Attempted write to closed stream.");
      } else {
         this.out.write(b, off, len);
      }
   }

   public void write(byte[] b) throws IOException {
      this.write(b, 0, b.length);
   }

   public void write(int b) throws IOException {
      if(this.closed) {
         throw new IOException("Attempted write to closed stream.");
      } else {
         this.out.write(b);
      }
   }
}
