package org.apache.commons.compress.compressors.bzip2;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.compress.compressors.FileNameUtil;

public abstract class BZip2Utils {
   private static final FileNameUtil fileNameUtil;

   public static boolean isCompressedFilename(String filename) {
      return fileNameUtil.isCompressedFilename(filename);
   }

   public static String getUncompressedFilename(String filename) {
      return fileNameUtil.getUncompressedFilename(filename);
   }

   public static String getCompressedFilename(String filename) {
      return fileNameUtil.getCompressedFilename(filename);
   }

   static {
      Map<String, String> uncompressSuffix = new LinkedHashMap();
      uncompressSuffix.put(".tar.bz2", ".tar");
      uncompressSuffix.put(".tbz2", ".tar");
      uncompressSuffix.put(".tbz", ".tar");
      uncompressSuffix.put(".bz2", "");
      uncompressSuffix.put(".bz", "");
      fileNameUtil = new FileNameUtil(uncompressSuffix, ".bz2");
   }
}
