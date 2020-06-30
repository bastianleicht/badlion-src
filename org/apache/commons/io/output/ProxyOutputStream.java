package org.apache.commons.io.output;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ProxyOutputStream extends FilterOutputStream {
   public ProxyOutputStream(OutputStream proxy) {
      super(proxy);
   }

   public void write(int idx) throws IOException {
      try {
         this.beforeWrite(1);
         this.out.write(idx);
         this.afterWrite(1);
      } catch (IOException var3) {
         this.handleIOException(var3);
      }

   }

   public void write(byte[] bts) throws IOException {
      try {
         int len = bts != null?bts.length:0;
         this.beforeWrite(len);
         this.out.write(bts);
         this.afterWrite(len);
      } catch (IOException var3) {
         this.handleIOException(var3);
      }

   }

   public void write(byte[] bts, int st, int end) throws IOException {
      try {
         this.beforeWrite(end);
         this.out.write(bts, st, end);
         this.afterWrite(end);
      } catch (IOException var5) {
         this.handleIOException(var5);
      }

   }

   public void flush() throws IOException {
      try {
         this.out.flush();
      } catch (IOException var2) {
         this.handleIOException(var2);
      }

   }

   public void close() throws IOException {
      try {
         this.out.close();
      } catch (IOException var2) {
         this.handleIOException(var2);
      }

   }

   protected void beforeWrite(int n) throws IOException {
   }

   protected void afterWrite(int n) throws IOException {
   }

   protected void handleIOException(IOException e) throws IOException {
      throw e;
   }
}
