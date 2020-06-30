package org.apache.http.impl.cookie;

import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.BasicDomainHandler;
import org.apache.http.util.Args;

@Immutable
public class NetscapeDomainHandler extends BasicDomainHandler {
   public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
      super.validate(cookie, origin);
      String host = origin.getHost();
      String domain = cookie.getDomain();
      if(host.contains(".")) {
         int domainParts = (new StringTokenizer(domain, ".")).countTokens();
         if(isSpecialDomain(domain)) {
            if(domainParts < 2) {
               throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" violates the Netscape cookie specification for " + "special domains");
            }
         } else if(domainParts < 3) {
            throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" violates the Netscape cookie specification");
         }
      }

   }

   private static boolean isSpecialDomain(String domain) {
      String ucDomain = domain.toUpperCase(Locale.ENGLISH);
      return ucDomain.endsWith(".COM") || ucDomain.endsWith(".EDU") || ucDomain.endsWith(".NET") || ucDomain.endsWith(".GOV") || ucDomain.endsWith(".MIL") || ucDomain.endsWith(".ORG") || ucDomain.endsWith(".INT");
   }

   public boolean match(Cookie cookie, CookieOrigin origin) {
      Args.notNull(cookie, "Cookie");
      Args.notNull(origin, "Cookie origin");
      String host = origin.getHost();
      String domain = cookie.getDomain();
      return domain == null?false:host.endsWith(domain);
   }
}
