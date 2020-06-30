package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.JettyNpnSslEngine;
import io.netty.handler.ssl.SslContext;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;

public abstract class JdkSslContext extends SslContext {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(JdkSslContext.class);
   static final String PROTOCOL = "TLS";
   static final String[] PROTOCOLS;
   static final List DEFAULT_CIPHERS;
   private final String[] cipherSuites;
   private final List unmodifiableCipherSuites;

   private static void addIfSupported(String[] supported, List enabled, String... names) {
      for(String n : names) {
         for(String s : supported) {
            if(n.equals(s)) {
               enabled.add(s);
               break;
            }
         }
      }

   }

   JdkSslContext(Iterable ciphers) {
      this.cipherSuites = toCipherSuiteArray(ciphers);
      this.unmodifiableCipherSuites = Collections.unmodifiableList(Arrays.asList(this.cipherSuites));
   }

   public abstract SSLContext context();

   public final SSLSessionContext sessionContext() {
      return this.isServer()?this.context().getServerSessionContext():this.context().getClientSessionContext();
   }

   public final List cipherSuites() {
      return this.unmodifiableCipherSuites;
   }

   public final long sessionCacheSize() {
      return (long)this.sessionContext().getSessionCacheSize();
   }

   public final long sessionTimeout() {
      return (long)this.sessionContext().getSessionTimeout();
   }

   public final SSLEngine newEngine(ByteBufAllocator alloc) {
      SSLEngine engine = this.context().createSSLEngine();
      engine.setEnabledCipherSuites(this.cipherSuites);
      engine.setEnabledProtocols(PROTOCOLS);
      engine.setUseClientMode(this.isClient());
      return this.wrapEngine(engine);
   }

   public final SSLEngine newEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
      SSLEngine engine = this.context().createSSLEngine(peerHost, peerPort);
      engine.setEnabledCipherSuites(this.cipherSuites);
      engine.setEnabledProtocols(PROTOCOLS);
      engine.setUseClientMode(this.isClient());
      return this.wrapEngine(engine);
   }

   private SSLEngine wrapEngine(SSLEngine engine) {
      return (SSLEngine)(this.nextProtocols().isEmpty()?engine:new JettyNpnSslEngine(engine, this.nextProtocols(), this.isServer()));
   }

   private static String[] toCipherSuiteArray(Iterable ciphers) {
      if(ciphers == null) {
         return (String[])DEFAULT_CIPHERS.toArray(new String[DEFAULT_CIPHERS.size()]);
      } else {
         List<String> newCiphers = new ArrayList();

         for(String c : ciphers) {
            if(c == null) {
               break;
            }

            newCiphers.add(c);
         }

         return (String[])newCiphers.toArray(new String[newCiphers.size()]);
      }
   }

   static {
      SSLContext context;
      try {
         context = SSLContext.getInstance("TLS");
         context.init((KeyManager[])null, (TrustManager[])null, (SecureRandom)null);
      } catch (Exception var6) {
         throw new Error("failed to initialize the default SSL context", var6);
      }

      SSLEngine engine = context.createSSLEngine();
      String[] supportedProtocols = engine.getSupportedProtocols();
      List<String> protocols = new ArrayList();
      addIfSupported(supportedProtocols, protocols, new String[]{"TLSv1.2", "TLSv1.1", "TLSv1", "SSLv3"});
      if(!protocols.isEmpty()) {
         PROTOCOLS = (String[])protocols.toArray(new String[protocols.size()]);
      } else {
         PROTOCOLS = engine.getEnabledProtocols();
      }

      String[] supportedCiphers = engine.getSupportedCipherSuites();
      List<String> ciphers = new ArrayList();
      addIfSupported(supportedCiphers, ciphers, new String[]{"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_RC4_128_SHA", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", "TLS_RSA_WITH_AES_128_GCM_SHA256", "SSL_RSA_WITH_RC4_128_SHA", "SSL_RSA_WITH_RC4_128_MD5", "TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA", "SSL_RSA_WITH_DES_CBC_SHA"});
      if(!ciphers.isEmpty()) {
         DEFAULT_CIPHERS = Collections.unmodifiableList(ciphers);
      } else {
         DEFAULT_CIPHERS = Collections.unmodifiableList(Arrays.asList(engine.getEnabledCipherSuites()));
      }

      if(logger.isDebugEnabled()) {
         logger.debug("Default protocols (JDK): {} ", (Object)Arrays.asList(PROTOCOLS));
         logger.debug("Default cipher suites (JDK): {}", (Object)DEFAULT_CIPHERS);
      }

   }
}
