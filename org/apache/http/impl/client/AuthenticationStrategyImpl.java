package org.apache.http.impl.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Immutable;
import org.apache.http.auth.AuthOption;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.AuthCache;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Immutable
abstract class AuthenticationStrategyImpl implements AuthenticationStrategy {
   private final Log log = LogFactory.getLog(this.getClass());
   private static final List DEFAULT_SCHEME_PRIORITY = Collections.unmodifiableList(Arrays.asList(new String[]{"negotiate", "Kerberos", "NTLM", "Digest", "Basic"}));
   private final int challengeCode;
   private final String headerName;

   AuthenticationStrategyImpl(int challengeCode, String headerName) {
      this.challengeCode = challengeCode;
      this.headerName = headerName;
   }

   public boolean isAuthenticationRequested(HttpHost authhost, HttpResponse response, HttpContext context) {
      Args.notNull(response, "HTTP response");
      int status = response.getStatusLine().getStatusCode();
      return status == this.challengeCode;
   }

   public Map getChallenges(HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
      Args.notNull(response, "HTTP response");
      Header[] headers = response.getHeaders(this.headerName);
      Map<String, Header> map = new HashMap(headers.length);

      for(Header header : headers) {
         CharArrayBuffer buffer;
         int pos;
         if(header instanceof FormattedHeader) {
            buffer = ((FormattedHeader)header).getBuffer();
            pos = ((FormattedHeader)header).getValuePos();
         } else {
            String s = header.getValue();
            if(s == null) {
               throw new MalformedChallengeException("Header value is null");
            }

            buffer = new CharArrayBuffer(s.length());
            buffer.append(s);
            pos = 0;
         }

         while(pos < buffer.length() && HTTP.isWhitespace(buffer.charAt(pos))) {
            ++pos;
         }

         int beginIndex;
         for(beginIndex = pos; pos < buffer.length() && !HTTP.isWhitespace(buffer.charAt(pos)); ++pos) {
            ;
         }

         String s = buffer.substring(beginIndex, pos);
         map.put(s.toLowerCase(Locale.US), header);
      }

      return map;
   }

   abstract Collection getPreferredAuthSchemes(RequestConfig var1);

   public Queue select(Map challenges, HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
      Args.notNull(challenges, "Map of auth challenges");
      Args.notNull(authhost, "Host");
      Args.notNull(response, "HTTP response");
      Args.notNull(context, "HTTP context");
      HttpClientContext clientContext = HttpClientContext.adapt(context);
      Queue<AuthOption> options = new LinkedList();
      Lookup<AuthSchemeProvider> registry = clientContext.getAuthSchemeRegistry();
      if(registry == null) {
         this.log.debug("Auth scheme registry not set in the context");
         return options;
      } else {
         CredentialsProvider credsProvider = clientContext.getCredentialsProvider();
         if(credsProvider == null) {
            this.log.debug("Credentials provider not set in the context");
            return options;
         } else {
            RequestConfig config = clientContext.getRequestConfig();
            Collection<String> authPrefs = this.getPreferredAuthSchemes(config);
            if(authPrefs == null) {
               authPrefs = DEFAULT_SCHEME_PRIORITY;
            }

            if(this.log.isDebugEnabled()) {
               this.log.debug("Authentication schemes in the order of preference: " + authPrefs);
            }

            for(String id : authPrefs) {
               Header challenge = (Header)challenges.get(id.toLowerCase(Locale.US));
               if(challenge != null) {
                  AuthSchemeProvider authSchemeProvider = (AuthSchemeProvider)registry.lookup(id);
                  if(authSchemeProvider == null) {
                     if(this.log.isWarnEnabled()) {
                        this.log.warn("Authentication scheme " + id + " not supported");
                     }
                  } else {
                     AuthScheme authScheme = authSchemeProvider.create(context);
                     authScheme.processChallenge(challenge);
                     AuthScope authScope = new AuthScope(authhost.getHostName(), authhost.getPort(), authScheme.getRealm(), authScheme.getSchemeName());
                     Credentials credentials = credsProvider.getCredentials(authScope);
                     if(credentials != null) {
                        options.add(new AuthOption(authScheme, credentials));
                     }
                  }
               } else if(this.log.isDebugEnabled()) {
                  this.log.debug("Challenge for " + id + " authentication scheme not available");
               }
            }

            return options;
         }
      }
   }

   public void authSucceeded(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
      Args.notNull(authhost, "Host");
      Args.notNull(authScheme, "Auth scheme");
      Args.notNull(context, "HTTP context");
      HttpClientContext clientContext = HttpClientContext.adapt(context);
      if(this.isCachable(authScheme)) {
         AuthCache authCache = clientContext.getAuthCache();
         if(authCache == null) {
            authCache = new BasicAuthCache();
            clientContext.setAuthCache(authCache);
         }

         if(this.log.isDebugEnabled()) {
            this.log.debug("Caching \'" + authScheme.getSchemeName() + "\' auth scheme for " + authhost);
         }

         authCache.put(authhost, authScheme);
      }

   }

   protected boolean isCachable(AuthScheme authScheme) {
      if(authScheme != null && authScheme.isComplete()) {
         String schemeName = authScheme.getSchemeName();
         return schemeName.equalsIgnoreCase("Basic") || schemeName.equalsIgnoreCase("Digest");
      } else {
         return false;
      }
   }

   public void authFailed(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
      Args.notNull(authhost, "Host");
      Args.notNull(context, "HTTP context");
      HttpClientContext clientContext = HttpClientContext.adapt(context);
      AuthCache authCache = clientContext.getAuthCache();
      if(authCache != null) {
         if(this.log.isDebugEnabled()) {
            this.log.debug("Clearing cached auth scheme for " + authhost);
         }

         authCache.remove(authhost);
      }

   }
}
