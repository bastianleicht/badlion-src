package org.apache.http.impl.execchain;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthState;
import org.apache.http.client.RedirectException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.impl.execchain.Proxies;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

@ThreadSafe
public class RedirectExec implements ClientExecChain {
   private final Log log = LogFactory.getLog(this.getClass());
   private final ClientExecChain requestExecutor;
   private final RedirectStrategy redirectStrategy;
   private final HttpRoutePlanner routePlanner;

   public RedirectExec(ClientExecChain requestExecutor, HttpRoutePlanner routePlanner, RedirectStrategy redirectStrategy) {
      Args.notNull(requestExecutor, "HTTP client request executor");
      Args.notNull(routePlanner, "HTTP route planner");
      Args.notNull(redirectStrategy, "HTTP redirect strategy");
      this.requestExecutor = requestExecutor;
      this.routePlanner = routePlanner;
      this.redirectStrategy = redirectStrategy;
   }

   public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
      Args.notNull(route, "HTTP route");
      Args.notNull(request, "HTTP request");
      Args.notNull(context, "HTTP context");
      List<URI> redirectLocations = context.getRedirectLocations();
      if(redirectLocations != null) {
         redirectLocations.clear();
      }

      RequestConfig config = context.getRequestConfig();
      int maxRedirects = config.getMaxRedirects() > 0?config.getMaxRedirects():50;
      HttpRoute currentRoute = route;
      HttpRequestWrapper currentRequest = request;
      int redirectCount = 0;

      while(true) {
         CloseableHttpResponse response = this.requestExecutor.execute(currentRoute, currentRequest, context, execAware);

         try {
            if(!config.isRedirectsEnabled() || !this.redirectStrategy.isRedirected(currentRequest, response, context)) {
               return response;
            }

            if(redirectCount >= maxRedirects) {
               throw new RedirectException("Maximum redirects (" + maxRedirects + ") exceeded");
            }

            ++redirectCount;
            HttpRequest redirect = this.redirectStrategy.getRedirect(currentRequest, response, context);
            if(!redirect.headerIterator().hasNext()) {
               HttpRequest original = request.getOriginal();
               redirect.setHeaders(original.getAllHeaders());
            }

            currentRequest = HttpRequestWrapper.wrap(redirect);
            if(currentRequest instanceof HttpEntityEnclosingRequest) {
               Proxies.enhanceEntity((HttpEntityEnclosingRequest)currentRequest);
            }

            URI uri = currentRequest.getURI();
            HttpHost newTarget = URIUtils.extractHost(uri);
            if(newTarget == null) {
               throw new ProtocolException("Redirect URI does not specify a valid host name: " + uri);
            }

            if(!currentRoute.getTargetHost().equals(newTarget)) {
               AuthState targetAuthState = context.getTargetAuthState();
               if(targetAuthState != null) {
                  this.log.debug("Resetting target auth state");
                  targetAuthState.reset();
               }

               AuthState proxyAuthState = context.getProxyAuthState();
               if(proxyAuthState != null) {
                  AuthScheme authScheme = proxyAuthState.getAuthScheme();
                  if(authScheme != null && authScheme.isConnectionBased()) {
                     this.log.debug("Resetting proxy auth state");
                     proxyAuthState.reset();
                  }
               }
            }

            currentRoute = this.routePlanner.determineRoute(newTarget, currentRequest, context);
            if(this.log.isDebugEnabled()) {
               this.log.debug("Redirecting to \'" + uri + "\' via " + currentRoute);
            }

            EntityUtils.consume(response.getEntity());
            response.close();
         } catch (RuntimeException var26) {
            response.close();
            throw var26;
         } catch (IOException var27) {
            response.close();
            throw var27;
         } catch (HttpException var28) {
            try {
               EntityUtils.consume(response.getEntity());
            } catch (IOException var24) {
               this.log.debug("I/O error while releasing connection", var24);
            } finally {
               response.close();
            }

            throw var28;
         }
      }
   }
}
