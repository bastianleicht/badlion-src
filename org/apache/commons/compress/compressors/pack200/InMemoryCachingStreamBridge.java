package org.apache.commons.compress.compressors.pack200;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.pack200.StreamBridge;

class InMemoryCachingStreamBridge extends StreamBridge {
   InMemoryCachingStreamBridge() {
      super(new ByteArrayOutputStream());
   }

   InputStream getInputView() throws IOException {
      return new ByteArrayInputStream(((ByteArrayOutputStream)this.out).toByteArray());
   }
}
