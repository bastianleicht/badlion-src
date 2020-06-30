package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.ssl.JdkSslContext;
import io.netty.handler.ssl.JettyNpnSslEngine;
import io.netty.handler.ssl.PemReader;
import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.security.auth.x500.X500Principal;

public final class JdkSslClientContext extends JdkSslContext {
   private final SSLContext ctx;
   private final List nextProtocols;

   public JdkSslClientContext() throws SSLException {
      this((File)null, (TrustManagerFactory)null, (Iterable)null, (Iterable)null, 0L, 0L);
   }

   public JdkSslClientContext(File certChainFile) throws SSLException {
      this(certChainFile, (TrustManagerFactory)null);
   }

   public JdkSslClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
      this((File)null, trustManagerFactory);
   }

   public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory) throws SSLException {
      this(certChainFile, trustManagerFactory, (Iterable)null, (Iterable)null, 0L, 0L);
   }

   public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory, Iterable ciphers, Iterable nextProtocols, long sessionCacheSize, long sessionTimeout) throws SSLException {
      super(ciphers);
      if(nextProtocols != null && nextProtocols.iterator().hasNext()) {
         if(!JettyNpnSslEngine.isAvailable()) {
            throw new SSLException("NPN/ALPN unsupported: " + nextProtocols);
         }

         List<String> nextProtoList = new ArrayList();

         for(String p : nextProtocols) {
            if(p == null) {
               break;
            }

            nextProtoList.add(p);
         }

         this.nextProtocols = Collections.unmodifiableList(nextProtoList);
      } else {
         this.nextProtocols = Collections.emptyList();
      }

      try {
         if(certChainFile == null) {
            this.ctx = SSLContext.getInstance("TLS");
            if(trustManagerFactory == null) {
               this.ctx.init((KeyManager[])null, (TrustManager[])null, (SecureRandom)null);
            } else {
               trustManagerFactory.init((KeyStore)null);
               this.ctx.init((KeyManager[])null, trustManagerFactory.getTrustManagers(), (SecureRandom)null);
            }
         } else {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load((InputStream)null, (char[])null);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ByteBuf[] certs = PemReader.readCertificates(certChainFile);
            boolean var25 = false;

            try {
               var25 = true;

               for(ByteBuf buf : certs) {
                  X509Certificate cert = (X509Certificate)cf.generateCertificate(new ByteBufInputStream(buf));
                  X500Principal principal = cert.getSubjectX500Principal();
                  ks.setCertificateEntry(principal.getName("RFC2253"), cert);
               }

               var25 = false;
            } finally {
               if(var25) {
                  for(ByteBuf buf : certs) {
                     buf.release();
                  }

               }
            }

            for(ByteBuf buf : certs) {
               buf.release();
            }

            if(trustManagerFactory == null) {
               trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            }

            trustManagerFactory.init(ks);
            this.ctx = SSLContext.getInstance("TLS");
            this.ctx.init((KeyManager[])null, trustManagerFactory.getTrustManagers(), (SecureRandom)null);
         }

         SSLSessionContext sessCtx = this.ctx.getClientSessionContext();
         if(sessionCacheSize > 0L) {
            sessCtx.setSessionCacheSize((int)Math.min(sessionCacheSize, 2147483647L));
         }

         if(sessionTimeout > 0L) {
            sessCtx.setSessionTimeout((int)Math.min(sessionTimeout, 2147483647L));
         }

      } catch (Exception var27) {
         throw new SSLException("failed to initialize the server-side SSL context", var27);
      }
   }

   public boolean isClient() {
      return true;
   }

   public List nextProtocols() {
      return this.nextProtocols;
   }

   public SSLContext context() {
      return this.ctx;
   }
}
