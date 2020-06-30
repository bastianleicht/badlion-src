package org.apache.commons.io.output;

import java.io.OutputStream;
import org.apache.commons.io.output.ClosedOutputStream;
import org.apache.commons.io.output.ProxyOutputStream;

public class CloseShieldOutputStream extends ProxyOutputStream {
   public CloseShieldOutputStream(OutputStream out) {
      super(out);
   }

   public void close() {
      this.out = new ClosedOutputStream();
   }
}
