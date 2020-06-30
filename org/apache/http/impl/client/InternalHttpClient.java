package org.apache.http.impl.client;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.auth.AuthState;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.params.HttpClientParamConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Lookup;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpParamsNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@ThreadSafe
class InternalHttpClient extends CloseableHttpClient {
   private final Log log = LogFactory.getLog(this.getClass());
   private final ClientExecChain execChain;
   private final HttpClientConnectionManager connManager;
   private final HttpRoutePlanner routePlanner;
   private final Lookup cookieSpecRegistry;
   private final Lookup authSchemeRegistry;
   private final CookieStore cookieStore;
   private final CredentialsProvider credentialsProvider;
   private final RequestConfig defaultConfig;
   private final List closeables;

   public InternalHttpClient(ClientExecChain execChain, HttpClientConnectionManager connManager, HttpRoutePlanner routePlanner, Lookup cookieSpecRegistry, Lookup authSchemeRegistry, CookieStore cookieStore, CredentialsProvider credentialsProvider, RequestConfig defaultConfig, List closeables) {
      Args.notNull(execChain, "HTTP client exec chain");
      Args.notNull(connManager, "HTTP connection manager");
      Args.notNull(routePlanner, "HTTP route planner");
      this.execChain = execChain;
      this.connManager = connManager;
      this.routePlanner = routePlanner;
      this.cookieSpecRegistry = cookieSpecRegistry;
      this.authSchemeRegistry = authSchemeRegistry;
      this.cookieStore = cookieStore;
      this.credentialsProvider = credentialsProvider;
      this.defaultConfig = defaultConfig;
      this.closeables = closeables;
   }

   private HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
      HttpHost host = target;
      if(target == null) {
         host = (HttpHost)request.getParams().getParameter("http.default-host");
      }

      Asserts.notNull(host, "Target host");
      return this.routePlanner.determineRoute(host, request, context);
   }

   private void setupContext(HttpClientContext context) {
      if(context.getAttribute("http.auth.target-scope") == null) {
         context.setAttribute("http.auth.target-scope", new AuthState());
      }

      if(context.getAttribute("http.auth.proxy-scope") == null) {
         context.setAttribute("http.auth.proxy-scope", new AuthState());
      }

      if(context.getAttribute("http.authscheme-registry") == null) {
         context.setAttribute("http.authscheme-registry", this.authSchemeRegistry);
      }

      if(context.getAttribute("http.cookiespec-registry") == null) {
         context.setAttribute("http.cookiespec-registry", this.cookieSpecRegistry);
      }

      if(context.getAttribute("http.cookie-store") == null) {
         context.setAttribute("http.cookie-store", this.cookieStore);
      }

      if(context.getAttribute("http.auth.credentials-provider") == null) {
         context.setAttribute("http.auth.credentials-provider", this.credentialsProvider);
      }

      if(context.getAttribute("http.request-config") == null) {
         context.setAttribute("http.request-config", this.defaultConfig);
      }

   }

   protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
      Args.notNull(request, "HTTP request");
      HttpExecutionAware execAware = null;
      if(request instanceof HttpExecutionAware) {
         execAware = (HttpExecutionAware)request;
      }

      try {
         HttpRequestWrapper wrapper = HttpRequestWrapper.wrap(request);
         HttpClientContext localcontext = HttpClientContext.adapt((HttpContext)(context != null?context:new BasicHttpContext()));
         RequestConfig config = null;
         if(request instanceof Configurable) {
            config = ((Configurable)request).getConfig();
         }

         if(config == null) {
            HttpParams params = request.getParams();
            if(params instanceof HttpParamsNames) {
               if(!((HttpParamsNames)params).getNames().isEmpty()) {
                  config = HttpClientParamConfig.getRequestConfig(params);
               }
            } else {
               config = HttpClientParamConfig.getRequestConfig(params);
            }
         }

         if(config != null) {
            localcontext.setRequestConfig(config);
         }

         this.setupContext(localcontext);
         HttpRoute route = this.determineRoute(target, wrapper, localcontext);
         return this.execChain.execute(route, wrapper, localcontext, execAware);
      } catch (HttpException var9) {
         throw new ClientProtocolException(var9);
      }
   }

   public void close() {
      this.connManager.shutdown();
      if(this.closeables != null) {
         for(Closeable closeable : this.closeables) {
            try {
               closeable.close();
            } catch (IOException var4) {
               this.log.error(var4.getMessage(), var4);
            }
         }
      }

   }

   public HttpParams getParams() {
      throw new UnsupportedOperationException();
   }

   public ClientConnectionManager getConnectionManager() {
      return new ClientConnectionManager() {
         public void shutdown() {
            InternalHttpClient.this.connManager.shutdown();
         }

         public ClientConnectionRequest requestConnection(HttpRoute route, Object state) {
            throw new UnsupportedOperationException();
         }

         public void releaseConnection(ManagedClientConnection conn, long validDuration, TimeUnit timeUnit) {
            throw new UnsupportedOperationException();
         }

         public SchemeRegistry getSchemeRegistry() {
            throw new UnsupportedOperationException();
         }

         public void closeIdleConnections(long idletime, TimeUnit tunit) {
            InternalHttpClient.this.connManager.closeIdleConnections(idletime, tunit);
         }

         public void closeExpiredConnections() {
            InternalHttpClient.this.connManager.closeExpiredConnections();
         }
      };
   }
}
