package org.apache.http.impl.cookie;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.client.utils.Punycode;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;

public class PublicSuffixFilter implements CookieAttributeHandler {
   private final CookieAttributeHandler wrapped;
   private Set exceptions;
   private Set suffixes;

   public PublicSuffixFilter(CookieAttributeHandler wrapped) {
      this.wrapped = wrapped;
   }

   public void setPublicSuffixes(Collection suffixes) {
      this.suffixes = new HashSet(suffixes);
   }

   public void setExceptions(Collection exceptions) {
      this.exceptions = new HashSet(exceptions);
   }

   public boolean match(Cookie cookie, CookieOrigin origin) {
      return this.isForPublicSuffix(cookie)?false:this.wrapped.match(cookie, origin);
   }

   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
      this.wrapped.parse(cookie, value);
   }

   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
      this.wrapped.validate(cookie, origin);
   }

   private boolean isForPublicSuffix(Cookie cookie) {
      String domain = cookie.getDomain();
      if(domain.startsWith(".")) {
         domain = domain.substring(1);
      }

      domain = Punycode.toUnicode(domain);
      if(this.exceptions != null && this.exceptions.contains(domain)) {
         return false;
      } else if(this.suffixes == null) {
         return false;
      } else {
         while(!this.suffixes.contains(domain)) {
            if(domain.startsWith("*.")) {
               domain = domain.substring(2);
            }

            int nextdot = domain.indexOf(46);
            if(nextdot != -1) {
               domain = "*" + domain.substring(nextdot);
               if(domain.length() > 0) {
                  continue;
               }
            }

            return false;
         }

         return true;
      }
   }
}
