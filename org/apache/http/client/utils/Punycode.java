package org.apache.http.client.utils;

import org.apache.http.annotation.Immutable;
import org.apache.http.client.utils.Idn;
import org.apache.http.client.utils.JdkIdn;
import org.apache.http.client.utils.Rfc3492Idn;

@Immutable
public class Punycode {
   private static final Idn impl;

   public static String toUnicode(String punycode) {
      return impl.toUnicode(punycode);
   }

   static {
      Idn _impl;
      try {
         _impl = new JdkIdn();
      } catch (Exception var2) {
         _impl = new Rfc3492Idn();
      }

      impl = _impl;
   }
}
