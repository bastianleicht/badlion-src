package org.apache.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.util.Args;

@NotThreadSafe
public class IdentityInputStream extends InputStream {
   private final SessionInputBuffer in;
   private boolean closed = false;

   public IdentityInputStream(SessionInputBuffer in) {
      this.in = (SessionInputBuffer)Args.notNull(in, "Session input buffer");
   }

   public int available() throws IOException {
      return this.in instanceof BufferInfo?((BufferInfo)this.in).length():0;
   }

   public void close() throws IOException {
      this.closed = true;
   }

   public int read() throws IOException {
      return this.closed?-1:this.in.read();
   }

   public int read(byte[] b, int off, int len) throws IOException {
      return this.closed?-1:this.in.read(b, off, len);
   }
}
