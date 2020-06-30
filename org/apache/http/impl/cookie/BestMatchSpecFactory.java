package org.apache.http.impl.cookie;

import java.util.Collection;
import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.cookie.BestMatchSpec;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

@Immutable
public class BestMatchSpecFactory implements CookieSpecFactory, CookieSpecProvider {
   private final String[] datepatterns;
   private final boolean oneHeader;

   public BestMatchSpecFactory(String[] datepatterns, boolean oneHeader) {
      this.datepatterns = datepatterns;
      this.oneHeader = oneHeader;
   }

   public BestMatchSpecFactory() {
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
         return new BestMatchSpec(patterns, singleHeader);
      } else {
         return new BestMatchSpec();
      }
   }

   public CookieSpec create(HttpContext context) {
      return new BestMatchSpec(this.datepatterns, this.oneHeader);
   }
}
