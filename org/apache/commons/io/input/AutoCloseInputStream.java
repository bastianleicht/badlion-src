package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.input.ClosedInputStream;
import org.apache.commons.io.input.ProxyInputStream;

public class AutoCloseInputStream extends ProxyInputStream {
   public AutoCloseInputStream(InputStream in) {
      super(in);
   }

   public void close() throws IOException {
      this.in.close();
      this.in = new ClosedInputStream();
   }

   protected void afterRead(int n) throws IOException {
      if(n == -1) {
         this.close();
      }

   }

   protected void finalize() throws Throwable {
      this.close();
      super.finalize();
   }
}
