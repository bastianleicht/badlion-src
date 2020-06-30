package org.apache.http.impl.auth;

import org.apache.http.annotation.Immutable;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.impl.auth.SPNegoScheme;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

@Immutable
public class SPNegoSchemeFactory implements AuthSchemeFactory, AuthSchemeProvider {
   private final boolean stripPort;

   public SPNegoSchemeFactory(boolean stripPort) {
      this.stripPort = stripPort;
   }

   public SPNegoSchemeFactory() {
      this(false);
   }

   public boolean isStripPort() {
      return this.stripPort;
   }

   public AuthScheme newInstance(HttpParams params) {
      return new SPNegoScheme(this.stripPort);
   }

   public AuthScheme create(HttpContext context) {
      return new SPNegoScheme(this.stripPort);
   }
}
