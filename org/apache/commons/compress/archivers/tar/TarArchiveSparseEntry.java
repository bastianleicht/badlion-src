package org.apache.commons.compress.archivers.tar;

import java.io.IOException;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.compress.archivers.tar.TarUtils;

public class TarArchiveSparseEntry implements TarConstants {
   private final boolean isExtended;

   public TarArchiveSparseEntry(byte[] headerBuf) throws IOException {
      int offset = 0;
      offset = offset + 504;
      this.isExtended = TarUtils.parseBoolean(headerBuf, offset);
   }

   public boolean isExtended() {
      return this.isExtended;
   }
}
