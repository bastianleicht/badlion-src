package org.apache.http.impl.cookie;

import java.util.Collection;
import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.cookie.RFC2965Spec;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

@Immutable
public class RFC2965SpecFactory implements CookieSpecFactory, CookieSpecProvider {
   private final String[] datepatterns;
   private final boolean oneHeader;

   public RFC2965SpecFactory(String[] datepatterns, boolean oneHeader) {
      this.datepatterns = datepatterns;
      this.oneHeader = oneHeader;
   }

   public RFC2965SpecFactory() {
      this((String[])null, false);
   }

   public CookieSpec newInstance(HttpParams params) {
      if(params != null) {
         String[] patterns = null;
         Collection<?> param = (Collection)params.getParameter("http.protocol.cookie-datepatterns");
         if(param != null) {
            patterns = new String[param.size()];
            patterns = (String[])param.toArray(patterns);
         }

         boolean singleHeader = params.getBooleanParameter("http.protocol.single-cookie-header", false);
         return new RFC2965Spec(patterns, singleHeader);
      } else {
         return new RFC2965Spec();
      }
   }

   public CookieSpec create(HttpContext context) {
      return new RFC2965Spec(this.datepatterns, this.oneHeader);
   }
}
