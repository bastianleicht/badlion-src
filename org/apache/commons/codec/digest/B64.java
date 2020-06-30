package org.apache.commons.codec.digest;

import java.util.Random;

class B64 {
   static final String B64T = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

   static void b64from24bit(byte b2, byte b1, byte b0, int outLen, StringBuilder buffer) {
      int w = b2 << 16 & 16777215 | b1 << 8 & '\uffff' | b0 & 255;

      for(int n = outLen; n-- > 0; w >>= 6) {
         buffer.append("./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt(w & 63));
      }

   }

   static String getRandomSalt(int num) {
      StringBuilder saltString = new StringBuilder();

      for(int i = 1; i <= num; ++i) {
         saltString.append("./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt((new Random()).nextInt("./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".length())));
      }

      return saltString.toString();
   }
}
