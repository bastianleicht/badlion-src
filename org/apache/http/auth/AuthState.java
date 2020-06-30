package org.apache.http.auth;

import java.util.Collection;
import java.util.Queue;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthProtocolState;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.util.Args;

@NotThreadSafe
public class AuthState {
   private AuthProtocolState state = AuthProtocolState.UNCHALLENGED;
   private AuthScheme authScheme;
   private AuthScope authScope;
   private Credentials credentials;
   private Queue authOptions;

   public void reset() {
      this.state = AuthProtocolState.UNCHALLENGED;
      this.authOptions = null;
      this.authScheme = null;
      this.authScope = null;
      this.credentials = null;
   }

   public AuthProtocolState getState() {
      return this.state;
   }

   public void setState(AuthProtocolState state) {
      this.state = state != null?state:AuthProtocolState.UNCHALLENGED;
   }

   public AuthScheme getAuthScheme() {
      return this.authScheme;
   }

   public Credentials getCredentials() {
      return this.credentials;
   }

   public void update(AuthScheme authScheme, Credentials credentials) {
      Args.notNull(authScheme, "Auth scheme");
      Args.notNull(credentials, "Credentials");
      this.authScheme = authScheme;
      this.credentials = credentials;
      this.authOptions = null;
   }

   public Queue getAuthOptions() {
      return this.authOptions;
   }

   public boolean hasAuthOptions() {
      return this.authOptions != null && !this.authOptions.isEmpty();
   }

   public void update(Queue authOptions) {
      Args.notEmpty((Collection)authOptions, "Queue of auth options");
      this.authOptions = authOptions;
      this.authScheme = null;
      this.credentials = null;
   }

   /** @deprecated */
   @Deprecated
   public void invalidate() {
      this.reset();
   }

   /** @deprecated */
   @Deprecated
   public boolean isValid() {
      return this.authScheme != null;
   }

   /** @deprecated */
   @Deprecated
   public void setAuthScheme(AuthScheme authScheme) {
      if(authScheme == null) {
         this.reset();
      } else {
         this.authScheme = authScheme;
      }
   }

   /** @deprecated */
   @Deprecated
   public void setCredentials(Credentials credentials) {
      this.credentials = credentials;
   }

   /** @deprecated */
   @Deprecated
   public AuthScope getAuthScope() {
      return this.authScope;
   }

   /** @deprecated */
   @Deprecated
   public void setAuthScope(AuthScope authScope) {
      this.authScope = authScope;
   }

   public String toString() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("state:").append(this.state).append(";");
      if(this.authScheme != null) {
         buffer.append("auth scheme:").append(this.authScheme.getSchemeName()).append(";");
      }

      if(this.credentials != null) {
         buffer.append("credentials present");
      }

      return buffer.toString();
   }
}
