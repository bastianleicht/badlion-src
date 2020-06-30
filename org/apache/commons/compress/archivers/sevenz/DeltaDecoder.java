package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.sevenz.Coder;
import org.apache.commons.compress.archivers.sevenz.CoderBase;
import org.tukaani.xz.DeltaOptions;
import org.tukaani.xz.FinishableWrapperOutputStream;
import org.tukaani.xz.UnsupportedOptionsException;

class DeltaDecoder extends CoderBase {
   DeltaDecoder() {
      super(new Class[]{Number.class});
   }

   InputStream decode(InputStream in, Coder coder, byte[] password) throws IOException {
      return (new DeltaOptions(this.getOptionsFromCoder(coder))).getInputStream(in);
   }

   OutputStream encode(OutputStream out, Object options) throws IOException {
      int distance = numberOptionOrDefault(options, 1);

      try {
         return (new DeltaOptions(distance)).getOutputStream(new FinishableWrapperOutputStream(out));
      } catch (UnsupportedOptionsException var5) {
         throw new IOException(var5.getMessage());
      }
   }

   byte[] getOptionsAsProperties(Object options) {
      return new byte[]{(byte)(numberOptionOrDefault(options, 1) - 1)};
   }

   Object getOptionsFromCoder(Coder coder, InputStream in) {
      return Integer.valueOf(this.getOptionsFromCoder(coder));
   }

   private int getOptionsFromCoder(Coder coder) {
      return coder.properties != null && coder.properties.length != 0?(255 & coder.properties[0]) + 1:1;
   }
}
