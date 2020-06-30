package org.apache.http.impl.client;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.params.HttpClientParamConfig;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParamConfig;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

public class ProxyClient {
   private final HttpConnectionFactory connFactory;
   private final ConnectionConfig connectionConfig;
   private final RequestConfig requestConfig;
   private final HttpProcessor httpProcessor;
   private final HttpRequestExecutor requestExec;
   private final ProxyAuthenticationStrategy proxyAuthStrategy;
   private final org.apache.http.impl.auth.HttpAuthenticator authenticator;
   private final AuthState proxyAuthState;
   private final AuthSchemeRegistry authSchemeRegistry;
   private final ConnectionReuseStrategy reuseStrategy;

   public ProxyClient(HttpConnectionFactory connFactory, ConnectionConfig connectionConfig, RequestConfig requestConfig) {
      this.connFactory = (HttpConnectionFactory)(connFactory != null?connFactory:ManagedHttpClientConnectionFactory.INSTANCE);
      this.connectionConfig = connectionConfig != null?connectionConfig:ConnectionConfig.DEFAULT;
      this.requestConfig = requestConfig != null?requestConfig:RequestConfig.DEFAULT;
      this.httpProcessor = new ImmutableHttpProcessor(new HttpRequestInterceptor[]{new RequestTargetHost(), new RequestClientConnControl(), new RequestUserAgent()});
      this.requestExec = new HttpRequestExecutor();
      this.proxyAuthStrategy = new ProxyAuthenticationStrategy();
      this.authenticator = new org.apache.http.impl.auth.HttpAuthenticator();
      this.proxyAuthState = new AuthState();
      this.authSchemeRegistry = new AuthSchemeRegistry();
      this.authSchemeRegistry.register("Basic", new BasicSchemeFactory());
      this.authSchemeRegistry.register("Digest", new DigestSchemeFactory());
      this.authSchemeRegistry.register("NTLM", new NTLMSchemeFactory());
      this.authSchemeRegistry.register("negotiate", new SPNegoSchemeFactory());
      this.authSchemeRegistry.register("Kerberos", new KerberosSchemeFactory());
      this.reuseStrategy = new DefaultConnectionReuseStrategy();
   }

   /** @deprecated */
   @Deprecated
   public ProxyClient(HttpParams params) {
      this((HttpConnectionFactory)null, HttpParamConfig.getConnectionConfig(params), HttpClientParamConfig.getRequestConfig(params));
   }

   public ProxyClient(RequestConfig requestConfig) {
      this((HttpConnectionFactory)null, (ConnectionConfig)null, requestConfig);
   }

   public ProxyClient() {
      this((HttpConnectionFactory)null, (ConnectionConfig)null, (RequestConfig)null);
   }

   /** @deprecated */
   @Deprecated
   public HttpParams getParams() {
      return new BasicHttpParams();
   }

   /** @deprecated */
   @Deprecated
   public AuthSchemeRegistry getAuthSchemeRegistry() {
      return this.authSchemeRegistry;
   }

   public Socket tunnel(HttpHost proxy, HttpHost target, Credentials credentials) throws IOException, HttpException {
      Args.notNull(proxy, "Proxy host");
      Args.notNull(target, "Target host");
      Args.notNull(credentials, "Credentials");
      HttpHost host = target;
      if(target.getPort() <= 0) {
         host = new HttpHost(target.getHostName(), 80, target.getSchemeName());
      }

      HttpRoute route = new HttpRoute(host, this.requestConfig.getLocalAddress(), proxy, false, RouteInfo.TunnelType.TUNNELLED, RouteInfo.LayerType.PLAIN);
      ManagedHttpClientConnection conn = (ManagedHttpClientConnection)this.connFactory.create(route, this.connectionConfig);
      HttpContext context = new BasicHttpContext();
      HttpRequest connect = new BasicHttpRequest("CONNECT", host.toHostString(), HttpVersion.HTTP_1_1);
      BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
      credsProvider.setCredentials(new AuthScope(proxy), credentials);
      context.setAttribute("http.target_host", target);
      context.setAttribute("http.connection", conn);
      context.setAttribute("http.request", connect);
      context.setAttribute("http.route", route);
      context.setAttribute("http.auth.proxy-scope", this.proxyAuthState);
      context.setAttribute("http.auth.credentials-provider", credsProvider);
      context.setAttribute("http.authscheme-registry", this.authSchemeRegistry);
      context.setAttribute("http.request-config", this.requestConfig);
      this.requestExec.preProcess(connect, this.httpProcessor, context);

      while(true) {
         if(!conn.isOpen()) {
            Socket socket = new Socket(proxy.getHostName(), proxy.getPort());
            conn.bind(socket);
         }

         this.authenticator.generateAuthResponse(connect, this.proxyAuthState, context);
         HttpResponse response = this.requestExec.execute(connect, conn, context);
         int status = response.getStatusLine().getStatusCode();
         if(status < 200) {
            throw new HttpException("Unexpected response to CONNECT request: " + response.getStatusLine());
         }

         if(!this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context) || !this.authenticator.handleAuthChallenge(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context)) {
            status = response.getStatusLine().getStatusCode();
            if(status > 299) {
               HttpEntity entity = response.getEntity();
               if(entity != null) {
                  response.setEntity(new BufferedHttpEntity(entity));
               }

               conn.close();
               throw new org.apache.http.impl.execchain.TunnelRefusedException("CONNECT refused by proxy: " + response.getStatusLine(), response);
            } else {
               return conn.getSocket();
            }
         }

         if(this.reuseStrategy.keepAlive(response, context)) {
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);
         } else {
            conn.close();
         }

         connect.removeHeaders("Proxy-Authorization");
      }
   }
}
