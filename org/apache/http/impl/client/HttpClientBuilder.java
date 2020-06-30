package org.apache.http.impl.client;

import java.io.Closeable;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.BackoffManager;
import org.apache.http.client.ConnectionBackoffStrategy;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.RequestAuthCache;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.client.protocol.RequestExpectContinue;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.DefaultUserTokenHandler;
import org.apache.http.impl.client.InternalHttpClient;
import org.apache.http.impl.client.NoopUserTokenHandler;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;
import org.apache.http.impl.client.TargetAuthenticationStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.impl.cookie.IgnoreSpecFactory;
import org.apache.http.impl.cookie.NetscapeDraftSpecFactory;
import org.apache.http.impl.cookie.RFC2109SpecFactory;
import org.apache.http.impl.cookie.RFC2965SpecFactory;
import org.apache.http.impl.execchain.BackoffStrategyExec;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.impl.execchain.MainClientExec;
import org.apache.http.impl.execchain.ProtocolExec;
import org.apache.http.impl.execchain.RedirectExec;
import org.apache.http.impl.execchain.RetryExec;
import org.apache.http.impl.execchain.ServiceUnavailableRetryExec;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.TextUtils;
import org.apache.http.util.VersionInfo;

@NotThreadSafe
public class HttpClientBuilder {
   private HttpRequestExecutor requestExec;
   private X509HostnameVerifier hostnameVerifier;
   private LayeredConnectionSocketFactory sslSocketFactory;
   private SSLContext sslcontext;
   private HttpClientConnectionManager connManager;
   private SchemePortResolver schemePortResolver;
   private ConnectionReuseStrategy reuseStrategy;
   private ConnectionKeepAliveStrategy keepAliveStrategy;
   private AuthenticationStrategy targetAuthStrategy;
   private AuthenticationStrategy proxyAuthStrategy;
   private UserTokenHandler userTokenHandler;
   private HttpProcessor httpprocessor;
   private LinkedList requestFirst;
   private LinkedList requestLast;
   private LinkedList responseFirst;
   private LinkedList responseLast;
   private HttpRequestRetryHandler retryHandler;
   private HttpRoutePlanner routePlanner;
   private RedirectStrategy redirectStrategy;
   private ConnectionBackoffStrategy connectionBackoffStrategy;
   private BackoffManager backoffManager;
   private ServiceUnavailableRetryStrategy serviceUnavailStrategy;
   private Lookup authSchemeRegistry;
   private Lookup cookieSpecRegistry;
   private CookieStore cookieStore;
   private CredentialsProvider credentialsProvider;
   private String userAgent;
   private HttpHost proxy;
   private Collection defaultHeaders;
   private SocketConfig defaultSocketConfig;
   private ConnectionConfig defaultConnectionConfig;
   private RequestConfig defaultRequestConfig;
   private boolean systemProperties;
   private boolean redirectHandlingDisabled;
   private boolean automaticRetriesDisabled;
   private boolean contentCompressionDisabled;
   private boolean cookieManagementDisabled;
   private boolean authCachingDisabled;
   private boolean connectionStateDisabled;
   private int maxConnTotal = 0;
   private int maxConnPerRoute = 0;
   private List closeables;
   static final String DEFAULT_USER_AGENT;

   public static HttpClientBuilder create() {
      return new HttpClientBuilder();
   }

   public final HttpClientBuilder setRequestExecutor(HttpRequestExecutor requestExec) {
      this.requestExec = requestExec;
      return this;
   }

   public final HttpClientBuilder setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
      this.hostnameVerifier = hostnameVerifier;
      return this;
   }

   public final HttpClientBuilder setSslcontext(SSLContext sslcontext) {
      this.sslcontext = sslcontext;
      return this;
   }

   public final HttpClientBuilder setSSLSocketFactory(LayeredConnectionSocketFactory sslSocketFactory) {
      this.sslSocketFactory = sslSocketFactory;
      return this;
   }

   public final HttpClientBuilder setMaxConnTotal(int maxConnTotal) {
      this.maxConnTotal = maxConnTotal;
      return this;
   }

   public final HttpClientBuilder setMaxConnPerRoute(int maxConnPerRoute) {
      this.maxConnPerRoute = maxConnPerRoute;
      return this;
   }

   public final HttpClientBuilder setDefaultSocketConfig(SocketConfig config) {
      this.defaultSocketConfig = config;
      return this;
   }

   public final HttpClientBuilder setDefaultConnectionConfig(ConnectionConfig config) {
      this.defaultConnectionConfig = config;
      return this;
   }

   public final HttpClientBuilder setConnectionManager(HttpClientConnectionManager connManager) {
      this.connManager = connManager;
      return this;
   }

   public final HttpClientBuilder setConnectionReuseStrategy(ConnectionReuseStrategy reuseStrategy) {
      this.reuseStrategy = reuseStrategy;
      return this;
   }

   public final HttpClientBuilder setKeepAliveStrategy(ConnectionKeepAliveStrategy keepAliveStrategy) {
      this.keepAliveStrategy = keepAliveStrategy;
      return this;
   }

   public final HttpClientBuilder setTargetAuthenticationStrategy(AuthenticationStrategy targetAuthStrategy) {
      this.targetAuthStrategy = targetAuthStrategy;
      return this;
   }

   public final HttpClientBuilder setProxyAuthenticationStrategy(AuthenticationStrategy proxyAuthStrategy) {
      this.proxyAuthStrategy = proxyAuthStrategy;
      return this;
   }

   public final HttpClientBuilder setUserTokenHandler(UserTokenHandler userTokenHandler) {
      this.userTokenHandler = userTokenHandler;
      return this;
   }

   public final HttpClientBuilder disableConnectionState() {
      this.connectionStateDisabled = true;
      return this;
   }

   public final HttpClientBuilder setSchemePortResolver(SchemePortResolver schemePortResolver) {
      this.schemePortResolver = schemePortResolver;
      return this;
   }

   public final HttpClientBuilder setUserAgent(String userAgent) {
      this.userAgent = userAgent;
      return this;
   }

   public final HttpClientBuilder setDefaultHeaders(Collection defaultHeaders) {
      this.defaultHeaders = defaultHeaders;
      return this;
   }

   public final HttpClientBuilder addInterceptorFirst(HttpResponseInterceptor itcp) {
      if(itcp == null) {
         return this;
      } else {
         if(this.responseFirst == null) {
            this.responseFirst = new LinkedList();
         }

         this.responseFirst.addFirst(itcp);
         return this;
      }
   }

   public final HttpClientBuilder addInterceptorLast(HttpResponseInterceptor itcp) {
      if(itcp == null) {
         return this;
      } else {
         if(this.responseLast == null) {
            this.responseLast = new LinkedList();
         }

         this.responseLast.addLast(itcp);
         return this;
      }
   }

   public final HttpClientBuilder addInterceptorFirst(HttpRequestInterceptor itcp) {
      if(itcp == null) {
         return this;
      } else {
         if(this.requestFirst == null) {
            this.requestFirst = new LinkedList();
         }

         this.requestFirst.addFirst(itcp);
         return this;
      }
   }

   public final HttpClientBuilder addInterceptorLast(HttpRequestInterceptor itcp) {
      if(itcp == null) {
         return this;
      } else {
         if(this.requestLast == null) {
            this.requestLast = new LinkedList();
         }

         this.requestLast.addLast(itcp);
         return this;
      }
   }

   public final HttpClientBuilder disableCookieManagement() {
      this.cookieManagementDisabled = true;
      return this;
   }

   public final HttpClientBuilder disableContentCompression() {
      this.contentCompressionDisabled = true;
      return this;
   }

   public final HttpClientBuilder disableAuthCaching() {
      this.authCachingDisabled = true;
      return this;
   }

   public final HttpClientBuilder setHttpProcessor(HttpProcessor httpprocessor) {
      this.httpprocessor = httpprocessor;
      return this;
   }

   public final HttpClientBuilder setRetryHandler(HttpRequestRetryHandler retryHandler) {
      this.retryHandler = retryHandler;
      return this;
   }

   public final HttpClientBuilder disableAutomaticRetries() {
      this.automaticRetriesDisabled = true;
      return this;
   }

   public final HttpClientBuilder setProxy(HttpHost proxy) {
      this.proxy = proxy;
      return this;
   }

   public final HttpClientBuilder setRoutePlanner(HttpRoutePlanner routePlanner) {
      this.routePlanner = routePlanner;
      return this;
   }

   public final HttpClientBuilder setRedirectStrategy(RedirectStrategy redirectStrategy) {
      this.redirectStrategy = redirectStrategy;
      return this;
   }

   public final HttpClientBuilder disableRedirectHandling() {
      this.redirectHandlingDisabled = true;
      return this;
   }

   public final HttpClientBuilder setConnectionBackoffStrategy(ConnectionBackoffStrategy connectionBackoffStrategy) {
      this.connectionBackoffStrategy = connectionBackoffStrategy;
      return this;
   }

   public final HttpClientBuilder setBackoffManager(BackoffManager backoffManager) {
      this.backoffManager = backoffManager;
      return this;
   }

   public final HttpClientBuilder setServiceUnavailableRetryStrategy(ServiceUnavailableRetryStrategy serviceUnavailStrategy) {
      this.serviceUnavailStrategy = serviceUnavailStrategy;
      return this;
   }

   public final HttpClientBuilder setDefaultCookieStore(CookieStore cookieStore) {
      this.cookieStore = cookieStore;
      return this;
   }

   public final HttpClientBuilder setDefaultCredentialsProvider(CredentialsProvider credentialsProvider) {
      this.credentialsProvider = credentialsProvider;
      return this;
   }

   public final HttpClientBuilder setDefaultAuthSchemeRegistry(Lookup authSchemeRegistry) {
      this.authSchemeRegistry = authSchemeRegistry;
      return this;
   }

   public final HttpClientBuilder setDefaultCookieSpecRegistry(Lookup cookieSpecRegistry) {
      this.cookieSpecRegistry = cookieSpecRegistry;
      return this;
   }

   public final HttpClientBuilder setDefaultRequestConfig(RequestConfig config) {
      this.defaultRequestConfig = config;
      return this;
   }

   public final HttpClientBuilder useSystemProperties() {
      this.systemProperties = true;
      return this;
   }

   protected ClientExecChain decorateMainExec(ClientExecChain mainExec) {
      return mainExec;
   }

   protected ClientExecChain decorateProtocolExec(ClientExecChain protocolExec) {
      return protocolExec;
   }

   protected void addCloseable(Closeable closeable) {
      if(closeable != null) {
         if(this.closeables == null) {
            this.closeables = new ArrayList();
         }

         this.closeables.add(closeable);
      }
   }

   private static String[] split(String s) {
      return TextUtils.isBlank(s)?null:s.split(" *, *");
   }

   public CloseableHttpClient build() {
      HttpRequestExecutor requestExec = this.requestExec;
      if(requestExec == null) {
         requestExec = new HttpRequestExecutor();
      }

      HttpClientConnectionManager connManager = this.connManager;
      if(connManager == null) {
         LayeredConnectionSocketFactory sslSocketFactory = this.sslSocketFactory;
         if(sslSocketFactory == null) {
            String[] supportedProtocols = this.systemProperties?split(System.getProperty("https.protocols")):null;
            String[] supportedCipherSuites = this.systemProperties?split(System.getProperty("https.cipherSuites")):null;
            X509HostnameVerifier hostnameVerifier = this.hostnameVerifier;
            if(hostnameVerifier == null) {
               hostnameVerifier = SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
            }

            if(this.sslcontext != null) {
               sslSocketFactory = new SSLConnectionSocketFactory(this.sslcontext, supportedProtocols, supportedCipherSuites, hostnameVerifier);
            } else if(this.systemProperties) {
               sslSocketFactory = new SSLConnectionSocketFactory((SSLSocketFactory)SSLSocketFactory.getDefault(), supportedProtocols, supportedCipherSuites, hostnameVerifier);
            } else {
               sslSocketFactory = new SSLConnectionSocketFactory(SSLContexts.createDefault(), hostnameVerifier);
            }
         }

         PoolingHttpClientConnectionManager poolingmgr = new PoolingHttpClientConnectionManager(RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslSocketFactory).build());
         if(this.defaultSocketConfig != null) {
            poolingmgr.setDefaultSocketConfig(this.defaultSocketConfig);
         }

         if(this.defaultConnectionConfig != null) {
            poolingmgr.setDefaultConnectionConfig(this.defaultConnectionConfig);
         }

         if(this.systemProperties) {
            String s = System.getProperty("http.keepAlive", "true");
            if("true".equalsIgnoreCase(s)) {
               s = System.getProperty("http.maxConnections", "5");
               int max = Integer.parseInt(s);
               poolingmgr.setDefaultMaxPerRoute(max);
               poolingmgr.setMaxTotal(2 * max);
            }
         }

         if(this.maxConnTotal > 0) {
            poolingmgr.setMaxTotal(this.maxConnTotal);
         }

         if(this.maxConnPerRoute > 0) {
            poolingmgr.setDefaultMaxPerRoute(this.maxConnPerRoute);
         }

         connManager = poolingmgr;
      }

      ConnectionReuseStrategy reuseStrategy = this.reuseStrategy;
      if(reuseStrategy == null) {
         if(this.systemProperties) {
            String s = System.getProperty("http.keepAlive", "true");
            if("true".equalsIgnoreCase(s)) {
               reuseStrategy = DefaultConnectionReuseStrategy.INSTANCE;
            } else {
               reuseStrategy = NoConnectionReuseStrategy.INSTANCE;
            }
         } else {
            reuseStrategy = DefaultConnectionReuseStrategy.INSTANCE;
         }
      }

      ConnectionKeepAliveStrategy keepAliveStrategy = this.keepAliveStrategy;
      if(keepAliveStrategy == null) {
         keepAliveStrategy = DefaultConnectionKeepAliveStrategy.INSTANCE;
      }

      AuthenticationStrategy targetAuthStrategy = this.targetAuthStrategy;
      if(targetAuthStrategy == null) {
         targetAuthStrategy = TargetAuthenticationStrategy.INSTANCE;
      }

      AuthenticationStrategy proxyAuthStrategy = this.proxyAuthStrategy;
      if(proxyAuthStrategy == null) {
         proxyAuthStrategy = ProxyAuthenticationStrategy.INSTANCE;
      }

      UserTokenHandler userTokenHandler = this.userTokenHandler;
      if(userTokenHandler == null) {
         if(!this.connectionStateDisabled) {
            userTokenHandler = DefaultUserTokenHandler.INSTANCE;
         } else {
            userTokenHandler = NoopUserTokenHandler.INSTANCE;
         }
      }

      ClientExecChain execChain = new MainClientExec(requestExec, connManager, reuseStrategy, keepAliveStrategy, targetAuthStrategy, proxyAuthStrategy, userTokenHandler);
      execChain = this.decorateMainExec(execChain);
      HttpProcessor httpprocessor = this.httpprocessor;
      if(httpprocessor == null) {
         String userAgent = this.userAgent;
         if(userAgent == null) {
            if(this.systemProperties) {
               userAgent = System.getProperty("http.agent");
            }

            if(userAgent == null) {
               userAgent = DEFAULT_USER_AGENT;
            }
         }

         HttpProcessorBuilder b = HttpProcessorBuilder.create();
         if(this.requestFirst != null) {
            for(HttpRequestInterceptor i : this.requestFirst) {
               b.addFirst(i);
            }
         }

         if(this.responseFirst != null) {
            for(HttpResponseInterceptor i : this.responseFirst) {
               b.addFirst(i);
            }
         }

         b.addAll(new HttpRequestInterceptor[]{new RequestDefaultHeaders(this.defaultHeaders), new RequestContent(), new RequestTargetHost(), new RequestClientConnControl(), new RequestUserAgent(userAgent), new RequestExpectContinue()});
         if(!this.cookieManagementDisabled) {
            b.add((HttpRequestInterceptor)(new RequestAddCookies()));
         }

         if(!this.contentCompressionDisabled) {
            b.add((HttpRequestInterceptor)(new RequestAcceptEncoding()));
         }

         if(!this.authCachingDisabled) {
            b.add((HttpRequestInterceptor)(new RequestAuthCache()));
         }

         if(!this.cookieManagementDisabled) {
            b.add((HttpResponseInterceptor)(new ResponseProcessCookies()));
         }

         if(!this.contentCompressionDisabled) {
            b.add((HttpResponseInterceptor)(new ResponseContentEncoding()));
         }

         if(this.requestLast != null) {
            for(HttpRequestInterceptor i : this.requestLast) {
               b.addLast(i);
            }
         }

         if(this.responseLast != null) {
            for(HttpResponseInterceptor i : this.responseLast) {
               b.addLast(i);
            }
         }

         httpprocessor = b.build();
      }

      ProtocolExec var28 = new ProtocolExec(execChain, httpprocessor);
      var28 = this.decorateProtocolExec(var28);
      if(!this.automaticRetriesDisabled) {
         HttpRequestRetryHandler retryHandler = this.retryHandler;
         if(retryHandler == null) {
            retryHandler = DefaultHttpRequestRetryHandler.INSTANCE;
         }

         var28 = new RetryExec(var28, retryHandler);
      }

      HttpRoutePlanner routePlanner = this.routePlanner;
      if(routePlanner == null) {
         SchemePortResolver schemePortResolver = this.schemePortResolver;
         if(schemePortResolver == null) {
            schemePortResolver = DefaultSchemePortResolver.INSTANCE;
         }

         if(this.proxy != null) {
            routePlanner = new DefaultProxyRoutePlanner(this.proxy, schemePortResolver);
         } else if(this.systemProperties) {
            routePlanner = new SystemDefaultRoutePlanner(schemePortResolver, ProxySelector.getDefault());
         } else {
            routePlanner = new DefaultRoutePlanner(schemePortResolver);
         }
      }

      if(!this.redirectHandlingDisabled) {
         RedirectStrategy redirectStrategy = this.redirectStrategy;
         if(redirectStrategy == null) {
            redirectStrategy = DefaultRedirectStrategy.INSTANCE;
         }

         var28 = new RedirectExec(var28, routePlanner, redirectStrategy);
      }

      ServiceUnavailableRetryStrategy serviceUnavailStrategy = this.serviceUnavailStrategy;
      if(serviceUnavailStrategy != null) {
         var28 = new ServiceUnavailableRetryExec(var28, serviceUnavailStrategy);
      }

      BackoffManager backoffManager = this.backoffManager;
      ConnectionBackoffStrategy connectionBackoffStrategy = this.connectionBackoffStrategy;
      if(backoffManager != null && connectionBackoffStrategy != null) {
         var28 = new BackoffStrategyExec(var28, connectionBackoffStrategy, backoffManager);
      }

      Lookup<AuthSchemeProvider> authSchemeRegistry = this.authSchemeRegistry;
      if(authSchemeRegistry == null) {
         authSchemeRegistry = RegistryBuilder.create().register("Basic", new BasicSchemeFactory()).register("Digest", new DigestSchemeFactory()).register("NTLM", new NTLMSchemeFactory()).register("negotiate", new SPNegoSchemeFactory()).register("Kerberos", new KerberosSchemeFactory()).build();
      }

      Lookup<CookieSpecProvider> cookieSpecRegistry = this.cookieSpecRegistry;
      if(cookieSpecRegistry == null) {
         cookieSpecRegistry = RegistryBuilder.create().register("best-match", new BestMatchSpecFactory()).register("standard", new RFC2965SpecFactory()).register("compatibility", new BrowserCompatSpecFactory()).register("netscape", new NetscapeDraftSpecFactory()).register("ignoreCookies", new IgnoreSpecFactory()).register("rfc2109", new RFC2109SpecFactory()).register("rfc2965", new RFC2965SpecFactory()).build();
      }

      CookieStore defaultCookieStore = this.cookieStore;
      if(defaultCookieStore == null) {
         defaultCookieStore = new BasicCookieStore();
      }

      CredentialsProvider defaultCredentialsProvider = this.credentialsProvider;
      if(defaultCredentialsProvider == null) {
         if(this.systemProperties) {
            defaultCredentialsProvider = new SystemDefaultCredentialsProvider();
         } else {
            defaultCredentialsProvider = new BasicCredentialsProvider();
         }
      }

      return new InternalHttpClient(var28, connManager, routePlanner, cookieSpecRegistry, authSchemeRegistry, defaultCookieStore, defaultCredentialsProvider, this.defaultRequestConfig != null?this.defaultRequestConfig:RequestConfig.DEFAULT, this.closeables != null?new ArrayList(this.closeables):null);
   }

   static {
      VersionInfo vi = VersionInfo.loadVersionInfo("org.apache.http.client", HttpClientBuilder.class.getClassLoader());
      String release = vi != null?vi.getRelease():"UNAVAILABLE";
      DEFAULT_USER_AGENT = "Apache-HttpClient/" + release + " (java 1.5)";
   }
}
