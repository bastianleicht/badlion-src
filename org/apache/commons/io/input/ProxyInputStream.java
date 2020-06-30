package org.apache.commons.io.input;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class ProxyInputStream extends FilterInputStream {
   public ProxyInputStream(InputStream proxy) {
      super(proxy);
   }

   public int read() throws IOException {
      try {
         this.beforeRead(1);
         int b = this.in.read();
         this.afterRead(b != -1?1:-1);
         return b;
      } catch (IOException var2) {
         this.handleIOException(var2);
         return -1;
      }
   }

   public int read(byte[] bts) throws IOException {
      try {
         this.beforeRead(bts != null?bts.length:0);
         int n = this.in.read(bts);
         this.afterRead(n);
         return n;
      } catch (IOException var3) {
         this.handleIOException(var3);
         return -1;
      }
   }

   public int read(byte[] bts, int off, int len) throws IOException {
      try {
         this.beforeRead(len);
         int n = this.in.read(bts, off, len);
         this.afterRead(n);
         return n;
      } catch (IOException var5) {
         this.handleIOException(var5);
         return -1;
      }
   }

   public long skip(long ln) throws IOException {
      try {
         return this.in.skip(ln);
      } catch (IOException var4) {
         this.handleIOException(var4);
         return 0L;
      }
   }

   public int available() throws IOException {
      try {
         return super.available();
      } catch (IOException var2) {
         this.handleIOException(var2);
         return 0;
      }
   }

   public void close() throws IOException {
      try {
         this.in.close();
      } catch (IOException var2) {
         this.handleIOException(var2);
      }

   }

   public synchronized void mark(int readlimit) {
      this.in.mark(readlimit);
   }

   public synchronized void reset() throws IOException {
      try {
         this.in.reset();
      } catch (IOException var2) {
         this.handleIOException(var2);
      }

   }

   public boolean markSupported() {
      return this.in.markSupported();
   }

   protected void beforeRead(int n) throws IOException {
   }

   protected void afterRead(int n) throws IOException {
   }

   protected void handleIOException(IOException e) throws IOException {
      throw e;
   }
}
