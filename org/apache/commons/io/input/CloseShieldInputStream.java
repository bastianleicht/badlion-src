package org.apache.commons.io.input;

import java.io.InputStream;
import org.apache.commons.io.input.ClosedInputStream;
import org.apache.commons.io.input.ProxyInputStream;

public class CloseShieldInputStream extends ProxyInputStream {
   public CloseShieldInputStream(InputStream in) {
      super(in);
   }

   public void close() {
      this.in = new ClosedInputStream();
   }
}
