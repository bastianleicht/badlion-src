package org.apache.commons.compress.compressors.xz;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

public class XZUtils {
   private static final FileNameUtil fileNameUtil;

   public static boolean isXZCompressionAvailable() {
      try {
         XZCompressorInputStream.matches((byte[])null, 0);
         return true;
      } catch (NoClassDefFoundError var1) {
         return false;
      }
   }

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
      Map<String, String> uncompressSuffix = new HashMap();
      uncompressSuffix.put(".txz", ".tar");
      uncompressSuffix.put(".xz", "");
      uncompressSuffix.put("-xz", "");
      fileNameUtil = new FileNameUtil(uncompressSuffix, ".xz");
   }
}
