package org.apache.http.impl.auth;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.auth.AuthSchemeBase;
import org.apache.http.message.BufferedHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

@NotThreadSafe
public abstract class GGSSchemeBase extends AuthSchemeBase {
   private final Log log;
   private final Base64 base64codec;
   private final boolean stripPort;
   private GGSSchemeBase.State state;
   private byte[] token;

   GGSSchemeBase(boolean stripPort) {
      this.log = LogFactory.getLog(this.getClass());
      this.base64codec = new Base64(0);
      this.stripPort = stripPort;
      this.state = GGSSchemeBase.State.UNINITIATED;
   }

   GGSSchemeBase() {
      this(false);
   }

   protected GSSManager getManager() {
      return GSSManager.getInstance();
   }

   protected byte[] generateGSSToken(byte[] input, Oid oid, String authServer) throws GSSException {
      byte[] token = input;
      if(input == null) {
         token = new byte[0];
      }

      GSSManager manager = this.getManager();
      GSSName serverName = manager.createName("HTTP@" + authServer, GSSName.NT_HOSTBASED_SERVICE);
      GSSContext gssContext = manager.createContext(serverName.canonicalize(oid), oid, (GSSCredential)null, 0);
      gssContext.requestMutualAuth(true);
      gssContext.requestCredDeleg(true);
      return gssContext.initSecContext(token, 0, token.length);
   }

   protected abstract byte[] generateToken(byte[] var1, String var2) throws GSSException;

   public boolean isComplete() {
      return this.state == GGSSchemeBase.State.TOKEN_GENERATED || this.state == GGSSchemeBase.State.FAILED;
   }

   /** @deprecated */
   @Deprecated
   public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
      return this.authenticate(credentials, request, (HttpContext)null);
   }

   public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
      Args.notNull(request, "HTTP request");
      switch(this.state) {
      case UNINITIATED:
         throw new AuthenticationException(this.getSchemeName() + " authentication has not been initiated");
      case FAILED:
         throw new AuthenticationException(this.getSchemeName() + " authentication has failed");
      case CHALLENGE_RECEIVED:
         try {
            HttpRoute route = (HttpRoute)context.getAttribute("http.route");
            if(route == null) {
               throw new AuthenticationException("Connection route is not available");
            } else {
               HttpHost host;
               if(this.isProxy()) {
                  host = route.getProxyHost();
                  if(host == null) {
                     host = route.getTargetHost();
                  }
               } else {
                  host = route.getTargetHost();
               }

               String authServer;
               if(!this.stripPort && host.getPort() > 0) {
                  authServer = host.toHostString();
               } else {
                  authServer = host.getHostName();
               }

               if(this.log.isDebugEnabled()) {
                  this.log.debug("init " + authServer);
               }

               this.token = this.generateToken(this.token, authServer);
               this.state = GGSSchemeBase.State.TOKEN_GENERATED;
            }
         } catch (GSSException var7) {
            this.state = GGSSchemeBase.State.FAILED;
            if(var7.getMajor() != 9 && var7.getMajor() != 8) {
               if(var7.getMajor() == 13) {
                  throw new InvalidCredentialsException(var7.getMessage(), var7);
               }

               if(var7.getMajor() != 10 && var7.getMajor() != 19 && var7.getMajor() != 20) {
                  throw new AuthenticationException(var7.getMessage());
               }

               throw new AuthenticationException(var7.getMessage(), var7);
            }

            throw new InvalidCredentialsException(var7.getMessage(), var7);
         }
      case TOKEN_GENERATED:
         String tokenstr = new String(this.base64codec.encode(this.token));
         if(this.log.isDebugEnabled()) {
            this.log.debug("Sending response \'" + tokenstr + "\' back to the auth server");
         }

         CharArrayBuffer buffer = new CharArrayBuffer(32);
         if(this.isProxy()) {
            buffer.append("Proxy-Authorization");
         } else {
            buffer.append("Authorization");
         }

         buffer.append(": Negotiate ");
         buffer.append(tokenstr);
         return new BufferedHeader(buffer);
      default:
         throw new IllegalStateException("Illegal state: " + this.state);
      }
   }

   protected void parseChallenge(CharArrayBuffer buffer, int beginIndex, int endIndex) throws MalformedChallengeException {
      String challenge = buffer.substringTrimmed(beginIndex, endIndex);
      if(this.log.isDebugEnabled()) {
         this.log.debug("Received challenge \'" + challenge + "\' from the auth server");
      }

      if(this.state == GGSSchemeBase.State.UNINITIATED) {
         this.token = Base64.decodeBase64(challenge.getBytes());
         this.state = GGSSchemeBase.State.CHALLENGE_RECEIVED;
      } else {
         this.log.debug("Authentication already attempted");
         this.state = GGSSchemeBase.State.FAILED;
      }

   }

   static enum State {
      UNINITIATED,
      CHALLENGE_RECEIVED,
      TOKEN_GENERATED,
      FAILED;
   }
}
