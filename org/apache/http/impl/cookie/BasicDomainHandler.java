package org.apache.http.impl.cookie;

import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.util.Args;

@Immutable
public class BasicDomainHandler implements CookieAttributeHandler {
   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
      Args.notNull(cookie, "Cookie");
      if(value == null) {
         throw new MalformedCookieException("Missing value for domain attribute");
      } else if(value.trim().length() == 0) {
         throw new MalformedCookieException("Blank value for domain attribute");
      } else {
         cookie.setDomain(value);
      }
   }

   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
      Args.notNull(cookie, "Cookie");
      Args.notNull(origin, "Cookie origin");
      String host = origin.getHost();
      String domain = cookie.getDomain();
      if(domain == null) {
         throw new CookieRestrictionViolationException("Cookie domain may not be null");
      } else {
         if(host.contains(".")) {
            if(!host.endsWith(domain)) {
               if(domain.startsWith(".")) {
                  domain = domain.substring(1, domain.length());
               }

               if(!host.equals(domain)) {
                  throw new CookieRestrictionViolationException("Illegal domain attribute \"" + domain + "\". Domain of origin: \"" + host + "\"");
               }
            }
         } else if(!host.equals(domain)) {
            throw new CookieRestrictionViolationException("Illegal domain attribute \"" + domain + "\". Domain of origin: \"" + host + "\"");
         }

      }
   }

   public boolean match(Cookie cookie, CookieOrigin origin) {
      Args.notNull(cookie, "Cookie");
      Args.notNull(origin, "Cookie origin");
      String host = origin.getHost();
      String domain = cookie.getDomain();
      if(domain == null) {
         return false;
      } else if(host.equals(domain)) {
         return true;
      } else {
         if(!domain.startsWith(".")) {
            domain = '.' + domain;
         }

         return host.endsWith(domain) || host.equals(domain.substring(1));
      }
   }
}
