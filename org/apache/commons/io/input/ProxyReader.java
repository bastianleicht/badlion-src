package org.apache.commons.io.input;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

public abstract class ProxyReader extends FilterReader {
   public ProxyReader(Reader proxy) {
      super(proxy);
   }

   public int read() throws IOException {
      try {
         this.beforeRead(1);
         int c = this.in.read();
         this.afterRead(c != -1?1:-1);
         return c;
      } catch (IOException var2) {
         this.handleIOException(var2);
         return -1;
      }
   }

   public int read(char[] chr) throws IOException {
      try {
         this.beforeRead(chr != null?chr.length:0);
         int n = this.in.read(chr);
         this.afterRead(n);
         return n;
      } catch (IOException var3) {
         this.handleIOException(var3);
         return -1;
      }
   }

   public int read(char[] chr, int st, int len) throws IOException {
      try {
         this.beforeRead(len);
         int n = this.in.read(chr, st, len);
         this.afterRead(n);
         return n;
      } catch (IOException var5) {
         this.handleIOException(var5);
         return -1;
      }
   }

   public int read(CharBuffer target) throws IOException {
      try {
         this.beforeRead(target != null?target.length():0);
         int n = this.in.read(target);
         this.afterRead(n);
         return n;
      } catch (IOException var3) {
         this.handleIOException(var3);
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

   public boolean ready() throws IOException {
      try {
         return this.in.ready();
      } catch (IOException var2) {
         this.handleIOException(var2);
         return false;
      }
   }

   public void close() throws IOException {
      try {
         this.in.close();
      } catch (IOException var2) {
         this.handleIOException(var2);
      }

   }

   public synchronized void mark(int idx) throws IOException {
      try {
         this.in.mark(idx);
      } catch (IOException var3) {
         this.handleIOException(var3);
      }

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
