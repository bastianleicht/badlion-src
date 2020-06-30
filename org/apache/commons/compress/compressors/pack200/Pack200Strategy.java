package org.apache.commons.compress.compressors.pack200;

import java.io.IOException;
import org.apache.commons.compress.compressors.pack200.InMemoryCachingStreamBridge;
import org.apache.commons.compress.compressors.pack200.StreamBridge;
import org.apache.commons.compress.compressors.pack200.TempFileCachingStreamBridge;

public enum Pack200Strategy {
   IN_MEMORY {
      StreamBridge newStreamBridge() {
         return new InMemoryCachingStreamBridge();
      }
   },
   TEMP_FILE {
      StreamBridge newStreamBridge() throws IOException {
         return new TempFileCachingStreamBridge();
      }
   };

   private Pack200Strategy() {
   }

   abstract StreamBridge newStreamBridge() throws IOException;
}
