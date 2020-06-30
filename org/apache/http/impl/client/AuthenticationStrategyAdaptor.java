package org.apache.http.impl.client;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Immutable;
import org.apache.http.auth.AuthOption;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.AuthCache;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
@Immutable
class AuthenticationStrategyAdaptor implements AuthenticationStrategy {
   private final Log log = LogFactory.getLog(this.getClass());
   private final AuthenticationHandler handler;

   public AuthenticationStrategyAdaptor(AuthenticationHandler handler) {
      this.handler = handler;
   }

   public boolean isAuthenticationRequested(HttpHost authhost, HttpResponse response, HttpContext context) {
      return this.handler.isAuthenticationRequested(response, context);
   }

   public Map getChallenges(HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
      return this.handler.getChallenges(response, context);
   }

   public Queue select(Map challenges, HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
      Args.notNull(challenges, "Map of auth challenges");
      Args.notNull(authhost, "Host");
      Args.notNull(response, "HTTP response");
      Args.notNull(context, "HTTP context");
      Queue<AuthOption> options = new LinkedList();
      CredentialsProvider credsProvider = (CredentialsProvider)context.getAttribute("http.auth.credentials-provider");
      if(credsProvider == null) {
         this.log.debug("Credentials provider not set in the context");
         return options;
      } else {
         AuthScheme authScheme;
         try {
            authScheme = this.handler.selectScheme(challenges, response, context);
         } catch (AuthenticationException var12) {
            if(this.log.isWarnEnabled()) {
               this.log.warn(var12.getMessage(), var12);
            }

            return options;
         }

         String id = authScheme.getSchemeName();
         Header challenge = (Header)challenges.get(id.toLowerCase(Locale.US));
         authScheme.processChallenge(challenge);
         AuthScope authScope = new AuthScope(authhost.getHostName(), authhost.getPort(), authScheme.getRealm(), authScheme.getSchemeName());
         Credentials credentials = credsProvider.getCredentials(authScope);
         if(credentials != null) {
            options.add(new AuthOption(authScheme, credentials));
         }

         return options;
      }
   }

   public void authSucceeded(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
      AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
      if(this.isCachable(authScheme)) {
         if(authCache == null) {
            authCache = new BasicAuthCache();
            context.setAttribute("http.auth.auth-cache", authCache);
         }

         if(this.log.isDebugEnabled()) {
            this.log.debug("Caching \'" + authScheme.getSchemeName() + "\' auth scheme for " + authhost);
         }

         authCache.put(authhost, authScheme);
      }

   }

   public void authFailed(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
      AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
      if(authCache != null) {
         if(this.log.isDebugEnabled()) {
            this.log.debug("Removing from cache \'" + authScheme.getSchemeName() + "\' auth scheme for " + authhost);
         }

         authCache.remove(authhost);
      }
   }

   private boolean isCachable(AuthScheme authScheme) {
      if(authScheme != null && authScheme.isComplete()) {
         String schemeName = authScheme.getSchemeName();
         return schemeName.equalsIgnoreCase("Basic") || schemeName.equalsIgnoreCase("Digest");
      } else {
         return false;
      }
   }

   public AuthenticationHandler getHandler() {
      return this.handler;
   }
}
