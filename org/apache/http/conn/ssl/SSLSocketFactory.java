package org.apache.http.conn.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import org.apache.http.HttpHost;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpInetSocketAddress;
import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.scheme.LayeredSchemeSocketFactory;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.SchemeLayeredSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLInitializationException;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.TextUtils;

/** @deprecated */
@Deprecated
@ThreadSafe
public class SSLSocketFactory implements LayeredConnectionSocketFactory, SchemeLayeredSocketFactory, LayeredSchemeSocketFactory, LayeredSocketFactory {
   public static final String TLS = "TLS";
   public static final String SSL = "SSL";
   public static final String SSLV2 = "SSLv2";
   public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER = new AllowAllHostnameVerifier();
   public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = new BrowserCompatHostnameVerifier();
   public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER = new StrictHostnameVerifier();
   private final javax.net.ssl.SSLSocketFactory socketfactory;
   private final HostNameResolver nameResolver;
   private volatile X509HostnameVerifier hostnameVerifier;
   private final String[] supportedProtocols;
   private final String[] supportedCipherSuites;

   public static SSLSocketFactory getSocketFactory() throws SSLInitializationException {
      return new SSLSocketFactory(SSLContexts.createDefault(), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
   }

   private static String[] split(String s) {
      return TextUtils.isBlank(s)?null:s.split(" *, *");
   }

   public static SSLSocketFactory getSystemSocketFactory() throws SSLInitializationException {
      return new SSLSocketFactory((javax.net.ssl.SSLSocketFactory)javax.net.ssl.SSLSocketFactory.getDefault(), split(System.getProperty("https.protocols")), split(System.getProperty("https.cipherSuites")), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
   }

   public SSLSocketFactory(String algorithm, KeyStore keystore, String keyPassword, KeyStore truststore, SecureRandom random, HostNameResolver nameResolver) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
      this(SSLContexts.custom().useProtocol(algorithm).setSecureRandom(random).loadKeyMaterial(keystore, keyPassword != null?keyPassword.toCharArray():null).loadTrustMaterial(truststore).build(), nameResolver);
   }

   public SSLSocketFactory(String algorithm, KeyStore keystore, String keyPassword, KeyStore truststore, SecureRandom random, TrustStrategy trustStrategy, X509HostnameVerifier hostnameVerifier) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
      this(SSLContexts.custom().useProtocol(algorithm).setSecureRandom(random).loadKeyMaterial(keystore, keyPassword != null?keyPassword.toCharArray():null).loadTrustMaterial(truststore, trustStrategy).build(), hostnameVerifier);
   }

   public SSLSocketFactory(String algorithm, KeyStore keystore, String keyPassword, KeyStore truststore, SecureRandom random, X509HostnameVerifier hostnameVerifier) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
      this(SSLContexts.custom().useProtocol(algorithm).setSecureRandom(random).loadKeyMaterial(keystore, keyPassword != null?keyPassword.toCharArray():null).loadTrustMaterial(truststore).build(), hostnameVerifier);
   }

   public SSLSocketFactory(KeyStore keystore, String keystorePassword, KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
      this(SSLContexts.custom().loadKeyMaterial(keystore, keystorePassword != null?keystorePassword.toCharArray():null).loadTrustMaterial(truststore).build(), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
   }

   public SSLSocketFactory(KeyStore keystore, String keystorePassword) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
      this(SSLContexts.custom().loadKeyMaterial(keystore, keystorePassword != null?keystorePassword.toCharArray():null).build(), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
   }

   public SSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
      this(SSLContexts.custom().loadTrustMaterial(truststore).build(), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
   }

   public SSLSocketFactory(TrustStrategy trustStrategy, X509HostnameVerifier hostnameVerifier) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
      this(SSLContexts.custom().loadTrustMaterial((KeyStore)null, trustStrategy).build(), hostnameVerifier);
   }

   public SSLSocketFactory(TrustStrategy trustStrategy) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
      this(SSLContexts.custom().loadTrustMaterial((KeyStore)null, trustStrategy).build(), BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
   }

   public SSLSocketFactory(SSLContext sslContext) {
      this(sslContext, BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
   }

   public SSLSocketFactory(SSLContext sslContext, HostNameResolver nameResolver) {
      this.socketfactory = sslContext.getSocketFactory();
      this.hostnameVerifier = BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
      this.nameResolver = nameResolver;
      this.supportedProtocols = null;
      this.supportedCipherSuites = null;
   }

   public SSLSocketFactory(SSLContext sslContext, X509HostnameVerifier hostnameVerifier) {
      this((javax.net.ssl.SSLSocketFactory)((SSLContext)Args.notNull(sslContext, "SSL context")).getSocketFactory(), (String[])null, (String[])null, hostnameVerifier);
   }

   public SSLSocketFactory(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, X509HostnameVerifier hostnameVerifier) {
      this(((SSLContext)Args.notNull(sslContext, "SSL context")).getSocketFactory(), supportedProtocols, supportedCipherSuites, hostnameVerifier);
   }

   public SSLSocketFactory(javax.net.ssl.SSLSocketFactory socketfactory, X509HostnameVerifier hostnameVerifier) {
      this((javax.net.ssl.SSLSocketFactory)socketfactory, (String[])null, (String[])null, hostnameVerifier);
   }

   public SSLSocketFactory(javax.net.ssl.SSLSocketFactory socketfactory, String[] supportedProtocols, String[] supportedCipherSuites, X509HostnameVerifier hostnameVerifier) {
      this.socketfactory = (javax.net.ssl.SSLSocketFactory)Args.notNull(socketfactory, "SSL socket factory");
      this.supportedProtocols = supportedProtocols;
      this.supportedCipherSuites = supportedCipherSuites;
      this.hostnameVerifier = hostnameVerifier != null?hostnameVerifier:BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
      this.nameResolver = null;
   }

   public Socket createSocket(HttpParams params) throws IOException {
      return this.createSocket((HttpContext)null);
   }

   public Socket createSocket() throws IOException {
      return this.createSocket((HttpContext)null);
   }

   public Socket connectSocket(Socket socket, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
      Args.notNull(remoteAddress, "Remote address");
      Args.notNull(params, "HTTP parameters");
      HttpHost host;
      if(remoteAddress instanceof HttpInetSocketAddress) {
         host = ((HttpInetSocketAddress)remoteAddress).getHttpHost();
      } else {
         host = new HttpHost(remoteAddress.getHostName(), remoteAddress.getPort(), "https");
      }

      int connectTimeout = HttpConnectionParams.getConnectionTimeout(params);
      return this.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, (HttpContext)null);
   }

   public boolean isSecure(Socket sock) throws IllegalArgumentException {
      Args.notNull(sock, "Socket");
      Asserts.check(sock instanceof SSLSocket, "Socket not created by this factory");
      Asserts.check(!sock.isClosed(), "Socket is closed");
      return true;
   }

   public Socket createLayeredSocket(Socket socket, String host, int port, HttpParams params) throws IOException, UnknownHostException {
      return this.createLayeredSocket(socket, host, port, (HttpContext)null);
   }

   public Socket createLayeredSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
      return this.createLayeredSocket(socket, host, port, (HttpContext)null);
   }

   public void setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
      Args.notNull(hostnameVerifier, "Hostname verifier");
      this.hostnameVerifier = hostnameVerifier;
   }

   public X509HostnameVerifier getHostnameVerifier() {
      return this.hostnameVerifier;
   }

   public Socket connectSocket(Socket socket, String host, int port, InetAddress local, int localPort, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
      InetAddress remote;
      if(this.nameResolver != null) {
         remote = this.nameResolver.resolve(host);
      } else {
         remote = InetAddress.getByName(host);
      }

      InetSocketAddress localAddress = null;
      if(local != null || localPort > 0) {
         localAddress = new InetSocketAddress(local, localPort > 0?localPort:0);
      }

      InetSocketAddress remoteAddress = new HttpInetSocketAddress(new HttpHost(host, port), remote, port);
      return this.connectSocket(socket, remoteAddress, localAddress, params);
   }

   public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
      return this.createLayeredSocket(socket, host, port, autoClose);
   }

   protected void prepareSocket(SSLSocket socket) throws IOException {
   }

   private void internalPrepareSocket(SSLSocket socket) throws IOException {
      if(this.supportedProtocols != null) {
         socket.setEnabledProtocols(this.supportedProtocols);
      }

      if(this.supportedCipherSuites != null) {
         socket.setEnabledCipherSuites(this.supportedCipherSuites);
      }

      this.prepareSocket(socket);
   }

   public Socket createSocket(HttpContext context) throws IOException {
      SSLSocket sock = (SSLSocket)this.socketfactory.createSocket();
      this.internalPrepareSocket(sock);
      return sock;
   }

   public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
      Args.notNull(host, "HTTP host");
      Args.notNull(remoteAddress, "Remote address");
      Socket sock = socket != null?socket:this.createSocket(context);
      if(localAddress != null) {
         sock.bind(localAddress);
      }

      try {
         sock.connect(remoteAddress, connectTimeout);
      } catch (IOException var11) {
         try {
            sock.close();
         } catch (IOException var10) {
            ;
         }

         throw var11;
      }

      if(sock instanceof SSLSocket) {
         SSLSocket sslsock = (SSLSocket)sock;
         sslsock.startHandshake();
         this.verifyHostname(sslsock, host.getHostName());
         return sock;
      } else {
         return this.createLayeredSocket(sock, host.getHostName(), remoteAddress.getPort(), context);
      }
   }

   public Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException {
      SSLSocket sslsock = (SSLSocket)this.socketfactory.createSocket(socket, target, port, true);
      this.internalPrepareSocket(sslsock);
      sslsock.startHandshake();
      this.verifyHostname(sslsock, target);
      return sslsock;
   }

   private void verifyHostname(SSLSocket sslsock, String hostname) throws IOException {
      try {
         this.hostnameVerifier.verify(hostname, sslsock);
      } catch (IOException var6) {
         try {
            sslsock.close();
         } catch (Exception var5) {
            ;
         }

         throw var6;
      }
   }
}
