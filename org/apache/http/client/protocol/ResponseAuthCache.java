package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Immutable;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthState;
import org.apache.http.client.AuthCache;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
@Immutable
public class ResponseAuthCache implements HttpResponseInterceptor {
   private final Log log = LogFactory.getLog(this.getClass());

   public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
      Args.notNull(response, "HTTP request");
      Args.notNull(context, "HTTP context");
      AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
      HttpHost target = (HttpHost)context.getAttribute("http.target_host");
      AuthState targetState = (AuthState)context.getAttribute("http.auth.target-scope");
      if(target != null && targetState != null) {
         if(this.log.isDebugEnabled()) {
            this.log.debug("Target auth state: " + targetState.getState());
         }

         if(this.isCachable(targetState)) {
            SchemeRegistry schemeRegistry = (SchemeRegistry)context.getAttribute("http.scheme-registry");
            if(target.getPort() < 0) {
               Scheme scheme = schemeRegistry.getScheme(target);
               target = new HttpHost(target.getHostName(), scheme.resolvePort(target.getPort()), target.getSchemeName());
            }

            if(authCache == null) {
               authCache = new BasicAuthCache();
               context.setAttribute("http.auth.auth-cache", authCache);
            }

            switch(targetState.getState()) {
            case CHALLENGED:
               this.cache(authCache, target, targetState.getAuthScheme());
               break;
            case FAILURE:
               this.uncache(authCache, target, targetState.getAuthScheme());
            }
         }
      }

      HttpHost proxy = (HttpHost)context.getAttribute("http.proxy_host");
      AuthState proxyState = (AuthState)context.getAttribute("http.auth.proxy-scope");
      if(proxy != null && proxyState != null) {
         if(this.log.isDebugEnabled()) {
            this.log.debug("Proxy auth state: " + proxyState.getState());
         }

         if(this.isCachable(proxyState)) {
            if(authCache == null) {
               authCache = new BasicAuthCache();
               context.setAttribute("http.auth.auth-cache", authCache);
            }

            switch(proxyState.getState()) {
            case CHALLENGED:
               this.cache(authCache, proxy, proxyState.getAuthScheme());
               break;
            case FAILURE:
               this.uncache(authCache, proxy, proxyState.getAuthScheme());
            }
         }
      }

   }

   private boolean isCachable(AuthState authState) {
      AuthScheme authScheme = authState.getAuthScheme();
      if(authScheme != null && authScheme.isComplete()) {
         String schemeName = authScheme.getSchemeName();
         return schemeName.equalsIgnoreCase("Basic") || schemeName.equalsIgnoreCase("Digest");
      } else {
         return false;
      }
   }

   private void cache(AuthCache authCache, HttpHost host, AuthScheme authScheme) {
      if(this.log.isDebugEnabled()) {
         this.log.debug("Caching \'" + authScheme.getSchemeName() + "\' auth scheme for " + host);
      }

      authCache.put(host, authScheme);
   }

   private void uncache(AuthCache authCache, HttpHost host, AuthScheme authScheme) {
      if(this.log.isDebugEnabled()) {
         this.log.debug("Removing from cache \'" + authScheme.getSchemeName() + "\' auth scheme for " + host);
      }

      authCache.remove(host);
   }
}
