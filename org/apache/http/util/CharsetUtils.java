package org.apache.http.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

public class CharsetUtils {
   public static Charset lookup(String name) {
      if(name == null) {
         return null;
      } else {
         try {
            return Charset.forName(name);
         } catch (UnsupportedCharsetException var2) {
            return null;
         }
      }
   }

   public static Charset get(String name) throws UnsupportedEncodingException {
      if(name == null) {
         return null;
      } else {
         try {
            return Charset.forName(name);
         } catch (UnsupportedCharsetException var2) {
            throw new UnsupportedEncodingException(name);
         }
      }
   }
}
