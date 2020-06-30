package org.apache.commons.compress.compressors.gzip;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.compress.compressors.FileNameUtil;

public class GzipUtils {
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
      uncompressSuffix.put(".tgz", ".tar");
      uncompressSuffix.put(".taz", ".tar");
      uncompressSuffix.put(".svgz", ".svg");
      uncompressSuffix.put(".cpgz", ".cpio");
      uncompressSuffix.put(".wmz", ".wmf");
      uncompressSuffix.put(".emz", ".emf");
      uncompressSuffix.put(".gz", "");
      uncompressSuffix.put(".z", "");
      uncompressSuffix.put("-gz", "");
      uncompressSuffix.put("-z", "");
      uncompressSuffix.put("_z", "");
      fileNameUtil = new FileNameUtil(uncompressSuffix, ".gz");
   }
}
