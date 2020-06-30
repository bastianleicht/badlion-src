package org.apache.commons.compress.compressors.pack200;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.pack200.StreamBridge;

class TempFileCachingStreamBridge extends StreamBridge {
   private final File f = File.createTempFile("commons-compress", "packtemp");

   TempFileCachingStreamBridge() throws IOException {
      this.f.deleteOnExit();
      this.out = new FileOutputStream(this.f);
   }

   InputStream getInputView() throws IOException {
      this.out.close();
      return new FileInputStream(this.f) {
         public void close() throws IOException {
            super.close();
            TempFileCachingStreamBridge.this.f.delete();
         }
      };
   }
}
