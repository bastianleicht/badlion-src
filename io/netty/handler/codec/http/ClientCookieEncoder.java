package io.netty.handler.codec.http;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieEncoderUtil;
import io.netty.handler.codec.http.DefaultCookie;
import java.util.Iterator;

public final class ClientCookieEncoder {
   public static String encode(String name, String value) {
      return encode((Cookie)(new DefaultCookie(name, value)));
   }

   public static String encode(Cookie cookie) {
      if(cookie == null) {
         throw new NullPointerException("cookie");
      } else {
         StringBuilder buf = CookieEncoderUtil.stringBuilder();
         encode(buf, cookie);
         return CookieEncoderUtil.stripTrailingSeparator(buf);
      }
   }

   public static String encode(Cookie... cookies) {
      if(cookies == null) {
         throw new NullPointerException("cookies");
      } else {
         StringBuilder buf = CookieEncoderUtil.stringBuilder();

         for(Cookie c : cookies) {
            if(c == null) {
               break;
            }

            encode(buf, c);
         }

         return CookieEncoderUtil.stripTrailingSeparator(buf);
      }
   }

   public static String encode(Iterable cookies) {
      if(cookies == null) {
         throw new NullPointerException("cookies");
      } else {
         StringBuilder buf = CookieEncoderUtil.stringBuilder();

         for(Cookie c : cookies) {
            if(c == null) {
               break;
            }

            encode(buf, c);
         }

         return CookieEncoderUtil.stripTrailingSeparator(buf);
      }
   }

   private static void encode(StringBuilder buf, Cookie c) {
      if(c.getVersion() >= 1) {
         CookieEncoderUtil.add(buf, "$Version", 1L);
      }

      CookieEncoderUtil.add(buf, c.getName(), c.getValue());
      if(c.getPath() != null) {
         CookieEncoderUtil.add(buf, "$Path", c.getPath());
      }

      if(c.getDomain() != null) {
         CookieEncoderUtil.add(buf, "$Domain", c.getDomain());
      }

      if(c.getVersion() >= 1 && !c.getPorts().isEmpty()) {
         buf.append('$');
         buf.append("Port");
         buf.append('=');
         buf.append('\"');
         Iterator i$ = c.getPorts().iterator();

         while(i$.hasNext()) {
            int port = ((Integer)i$.next()).intValue();
            buf.append(port);
            buf.append(',');
         }

         buf.setCharAt(buf.length() - 1, '\"');
         buf.append(';');
         buf.append(' ');
      }

   }
}
