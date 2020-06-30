package org.apache.http.impl.cookie;

import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.impl.cookie.AbstractCookieAttributeHandler;
import org.apache.http.util.Args;

@Immutable
public class RFC2109VersionHandler extends AbstractCookieAttributeHandler {
   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
      Args.notNull(cookie, "Cookie");
      if(value == null) {
         throw new MalformedCookieException("Missing value for version attribute");
      } else if(value.trim().length() == 0) {
         throw new MalformedCookieException("Blank value for version attribute");
      } else {
         try {
            cookie.setVersion(Integer.parseInt(value));
         } catch (NumberFormatException var4) {
            throw new MalformedCookieException("Invalid version: " + var4.getMessage());
         }
      }
   }

   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
      Args.notNull(cookie, "Cookie");
      if(cookie.getVersion() < 0) {
         throw new CookieRestrictionViolationException("Cookie version may not be negative");
      }
   }
}
