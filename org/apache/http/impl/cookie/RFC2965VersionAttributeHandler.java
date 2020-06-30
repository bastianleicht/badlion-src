package org.apache.http.impl.cookie;

import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.cookie.SetCookie2;
import org.apache.http.util.Args;

@Immutable
public class RFC2965VersionAttributeHandler implements CookieAttributeHandler {
   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
      Args.notNull(cookie, "Cookie");
      if(value == null) {
         throw new MalformedCookieException("Missing value for version attribute");
      } else {
         int version = -1;

         try {
            version = Integer.parseInt(value);
         } catch (NumberFormatException var5) {
            version = -1;
         }

         if(version < 0) {
            throw new MalformedCookieException("Invalid cookie version.");
         } else {
            cookie.setVersion(version);
         }
      }
   }

   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
      Args.notNull(cookie, "Cookie");
      if(cookie instanceof SetCookie2 && cookie instanceof ClientCookie && !((ClientCookie)cookie).containsAttribute("version")) {
         throw new CookieRestrictionViolationException("Violates RFC 2965. Version attribute is required.");
      }
   }

   public boolean match(Cookie cookie, CookieOrigin origin) {
      return true;
   }
}
