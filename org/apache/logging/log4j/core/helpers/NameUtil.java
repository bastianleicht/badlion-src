package org.apache.logging.log4j.core.helpers;

import java.security.MessageDigest;

public final class NameUtil {
   private static final int MASK = 255;

   public static String getSubName(String name) {
      if(name.isEmpty()) {
         return null;
      } else {
         int i = name.lastIndexOf(46);
         return i > 0?name.substring(0, i):"";
      }
   }

   public static String md5(String string) {
      try {
         MessageDigest digest = MessageDigest.getInstance("MD5");
         digest.update(string.getBytes());
         byte[] bytes = digest.digest();
         StringBuilder md5 = new StringBuilder();

         for(byte b : bytes) {
            String hex = Integer.toHexString(255 & b);
            if(hex.length() == 1) {
               md5.append('0');
            }

            md5.append(hex);
         }

         return md5.toString();
      } catch (Exception var9) {
         return string;
      }
   }
}
