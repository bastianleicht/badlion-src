package org.apache.commons.compress.archivers.dump;

public final class DumpArchiveConstants {
   public static final int TP_SIZE = 1024;
   public static final int NTREC = 10;
   public static final int HIGH_DENSITY_NTREC = 32;
   public static final int OFS_MAGIC = 60011;
   public static final int NFS_MAGIC = 60012;
   public static final int FS_UFS2_MAGIC = 424935705;
   public static final int CHECKSUM = 84446;
   public static final int LBLSIZE = 16;
   public static final int NAMELEN = 64;

   public static enum COMPRESSION_TYPE {
      ZLIB(0),
      BZLIB(1),
      LZO(2);

      int code;

      private COMPRESSION_TYPE(int code) {
         this.code = code;
      }

      public static DumpArchiveConstants.COMPRESSION_TYPE find(int code) {
         for(DumpArchiveConstants.COMPRESSION_TYPE t : values()) {
            if(t.code == code) {
               return t;
            }
         }

         return null;
      }
   }

   public static enum SEGMENT_TYPE {
      TAPE(1),
      INODE(2),
      BITS(3),
      ADDR(4),
      END(5),
      CLRI(6);

      int code;

      private SEGMENT_TYPE(int code) {
         this.code = code;
      }

      public static DumpArchiveConstants.SEGMENT_TYPE find(int code) {
         for(DumpArchiveConstants.SEGMENT_TYPE t : values()) {
            if(t.code == code) {
               return t;
            }
         }

         return null;
      }
   }
}
