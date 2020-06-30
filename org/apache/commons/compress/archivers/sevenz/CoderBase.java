package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.sevenz.Coder;

abstract class CoderBase {
   private final Class[] acceptableOptions;
   private static final byte[] NONE = new byte[0];

   protected CoderBase(Class... acceptableOptions) {
      this.acceptableOptions = acceptableOptions;
   }

   boolean canAcceptOptions(Object opts) {
      for(Class<?> c : this.acceptableOptions) {
         if(c.isInstance(opts)) {
            return true;
         }
      }

      return false;
   }

   byte[] getOptionsAsProperties(Object options) {
      return NONE;
   }

   Object getOptionsFromCoder(Coder coder, InputStream in) {
      return null;
   }

   abstract InputStream decode(InputStream var1, Coder var2, byte[] var3) throws IOException;

   OutputStream encode(OutputStream out, Object options) throws IOException {
      throw new UnsupportedOperationException("method doesn\'t support writing");
   }

   protected static int numberOptionOrDefault(Object options, int defaultValue) {
      return options instanceof Number?((Number)options).intValue():defaultValue;
   }
}
