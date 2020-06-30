package org.apache.commons.compress.archivers.cpio;

public interface CpioConstants {
   String MAGIC_NEW = "070701";
   String MAGIC_NEW_CRC = "070702";
   String MAGIC_OLD_ASCII = "070707";
   int MAGIC_OLD_BINARY = 29127;
   short FORMAT_NEW = 1;
   short FORMAT_NEW_CRC = 2;
   short FORMAT_OLD_ASCII = 4;
   short FORMAT_OLD_BINARY = 8;
   short FORMAT_NEW_MASK = 3;
   short FORMAT_OLD_MASK = 12;
   int S_IFMT = 61440;
   int C_ISSOCK = 49152;
   int C_ISLNK = 40960;
   int C_ISNWK = 36864;
   int C_ISREG = 32768;
   int C_ISBLK = 24576;
   int C_ISDIR = 16384;
   int C_ISCHR = 8192;
   int C_ISFIFO = 4096;
   int C_ISUID = 2048;
   int C_ISGID = 1024;
   int C_ISVTX = 512;
   int C_IRUSR = 256;
   int C_IWUSR = 128;
   int C_IXUSR = 64;
   int C_IRGRP = 32;
   int C_IWGRP = 16;
   int C_IXGRP = 8;
   int C_IROTH = 4;
   int C_IWOTH = 2;
   int C_IXOTH = 1;
   String CPIO_TRAILER = "TRAILER!!!";
   int BLOCK_SIZE = 512;
}
