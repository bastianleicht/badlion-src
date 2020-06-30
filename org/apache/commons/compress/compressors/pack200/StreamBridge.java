package org.apache.commons.compress.compressors.pack200;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract class StreamBridge extends FilterOutputStream {
   private InputStream input;
   private final Object INPUT_LOCK;

   protected StreamBridge(OutputStream out) {
      super(out);
      this.INPUT_LOCK = new Object();
   }

   protected StreamBridge() {
      this((OutputStream)null);
   }

   InputStream getInput() throws IOException {
      synchronized(this.INPUT_LOCK) {
         if(this.input == null) {
            this.input = this.getInputView();
         }
      }

      return this.input;
   }

   abstract InputStream getInputView() throws IOException;

   void stop() throws IOException {
      this.close();
      synchronized(this.INPUT_LOCK) {
         if(this.input != null) {
            this.input.close();
            this.input = null;
         }

      }
   }
}
