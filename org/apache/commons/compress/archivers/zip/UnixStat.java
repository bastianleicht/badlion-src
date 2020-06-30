package org.apache.commons.compress.archivers.zip;

public interface UnixStat {
   int PERM_MASK = 4095;
   int LINK_FLAG = 40960;
   int FILE_FLAG = 32768;
   int DIR_FLAG = 16384;
   int DEFAULT_LINK_PERM = 511;
   int DEFAULT_DIR_PERM = 493;
   int DEFAULT_FILE_PERM = 420;
}
