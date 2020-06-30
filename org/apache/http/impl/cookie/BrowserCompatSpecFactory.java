package org.apache.http.impl.cookie;

import java.util.Collection;
import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

@Immutable
public class BrowserCompatSpecFactory implements CookieSpecFactory, CookieSpecProvider {
   private final String[] datepatterns;
   private final BrowserCompatSpecFactory.SecurityLevel securityLevel;

   public BrowserCompatSpecFactory(String[] datepatterns, BrowserCompatSpecFactory.SecurityLevel securityLevel) {
      this.datepatterns = datepatterns;
      this.securityLevel = securityLevel;
   }

   public BrowserCompatSpecFactory(String[] datepatterns) {
      this((String[])null, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
   }

   public BrowserCompatSpecFactory() {
      this((String[])null, BrowserCompatSpecFactory.SecurityLevel.SECURITYLEVEL_DEFAULT);
   }

   public CookieSpec newInstance(HttpParams params) {
      if(params != null) {
         String[] patterns = null;
         Collection<?> param = (Collection)params.getParameter("http.protocol.cookie-datepatterns");
         if(param != null) {
            patterns = new String[param.size()];
            patterns = (String[])param.toArray(patterns);
         }

         return new BrowserCompatSpec(patterns, this.securityLevel);
      } else {
         return new BrowserCompatSpec((String[])null, this.securityLevel);
      }
   }

   public CookieSpec create(HttpContext context) {
      return new BrowserCompatSpec(this.datepatterns);
   }

   public static enum SecurityLevel {
      SECURITYLEVEL_DEFAULT,
      SECURITYLEVEL_IE_MEDIUM;
   }
}
