package org.apache.commons.compress.compressors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class FileNameUtil {
   private final Map compressSuffix = new HashMap();
   private final Map uncompressSuffix;
   private final int longestCompressedSuffix;
   private final int shortestCompressedSuffix;
   private final int longestUncompressedSuffix;
   private final int shortestUncompressedSuffix;
   private final String defaultExtension;

   public FileNameUtil(Map uncompressSuffix, String defaultExtension) {
      this.uncompressSuffix = Collections.unmodifiableMap(uncompressSuffix);
      int lc = Integer.MIN_VALUE;
      int sc = Integer.MAX_VALUE;
      int lu = Integer.MIN_VALUE;
      int su = Integer.MAX_VALUE;

      for(Entry<String, String> ent : uncompressSuffix.entrySet()) {
         int cl = ((String)ent.getKey()).length();
         if(cl > lc) {
            lc = cl;
         }

         if(cl < sc) {
            sc = cl;
         }

         String u = (String)ent.getValue();
         int ul = u.length();
         if(ul > 0) {
            if(!this.compressSuffix.containsKey(u)) {
               this.compressSuffix.put(u, ent.getKey());
            }

            if(ul > lu) {
               lu = ul;
            }

            if(ul < su) {
               su = ul;
            }
         }
      }

      this.longestCompressedSuffix = lc;
      this.longestUncompressedSuffix = lu;
      this.shortestCompressedSuffix = sc;
      this.shortestUncompressedSuffix = su;
      this.defaultExtension = defaultExtension;
   }

   public boolean isCompressedFilename(String filename) {
      String lower = filename.toLowerCase(Locale.ENGLISH);
      int n = lower.length();

      for(int i = this.shortestCompressedSuffix; i <= this.longestCompressedSuffix && i < n; ++i) {
         if(this.uncompressSuffix.containsKey(lower.substring(n - i))) {
            return true;
         }
      }

      return false;
   }

   public String getUncompressedFilename(String filename) {
      String lower = filename.toLowerCase(Locale.ENGLISH);
      int n = lower.length();

      for(int i = this.shortestCompressedSuffix; i <= this.longestCompressedSuffix && i < n; ++i) {
         String suffix = (String)this.uncompressSuffix.get(lower.substring(n - i));
         if(suffix != null) {
            return filename.substring(0, n - i) + suffix;
         }
      }

      return filename;
   }

   public String getCompressedFilename(String filename) {
      String lower = filename.toLowerCase(Locale.ENGLISH);
      int n = lower.length();

      for(int i = this.shortestUncompressedSuffix; i <= this.longestUncompressedSuffix && i < n; ++i) {
         String suffix = (String)this.compressSuffix.get(lower.substring(n - i));
         if(suffix != null) {
            return filename.substring(0, n - i) + suffix;
         }
      }

      return filename + this.defaultExtension;
   }
}
