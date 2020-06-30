package org.apache.http.impl.auth;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.impl.auth.GGSSchemeBase;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;

@NotThreadSafe
public class SPNegoScheme extends GGSSchemeBase {
   private static final String SPNEGO_OID = "1.3.6.1.5.5.2";

   public SPNegoScheme(boolean stripPort) {
      super(stripPort);
   }

   public SPNegoScheme() {
      super(false);
   }

   public String getSchemeName() {
      return "Negotiate";
   }

   public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
      return super.authenticate(credentials, request, context);
   }

   protected byte[] generateToken(byte[] input, String authServer) throws GSSException {
      return this.generateGSSToken(input, new Oid("1.3.6.1.5.5.2"), authServer);
   }

   public String getParameter(String name) {
      Args.notNull(name, "Parameter name");
      return null;
   }

   public String getRealm() {
      return null;
   }

   public boolean isConnectionBased() {
      return true;
   }
}
