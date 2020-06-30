package io.netty.handler.ssl;

import io.netty.handler.ssl.JettyNpnSslSession;
import java.nio.ByteBuffer;
import java.util.List;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import org.eclipse.jetty.npn.NextProtoNego;
import org.eclipse.jetty.npn.NextProtoNego.ClientProvider;
import org.eclipse.jetty.npn.NextProtoNego.ServerProvider;

final class JettyNpnSslEngine extends SSLEngine {
   private static boolean available;
   private final SSLEngine engine;
   private final JettyNpnSslSession session;

   static boolean isAvailable() {
      updateAvailability();
      return available;
   }

   private static void updateAvailability() {
      if(!available) {
         try {
            ClassLoader bootloader = ClassLoader.getSystemClassLoader().getParent();
            if(bootloader == null) {
               bootloader = ClassLoader.getSystemClassLoader();
            }

            Class.forName("sun.security.ssl.NextProtoNegoExtension", true, bootloader);
            available = true;
         } catch (Exception var1) {
            ;
         }

      }
   }

   JettyNpnSslEngine(SSLEngine engine, final List nextProtocols, boolean server) {
      assert !nextProtocols.isEmpty();

      this.engine = engine;
      this.session = new JettyNpnSslSession(engine);
      if(server) {
         NextProtoNego.put(engine, new ServerProvider() {
            public void unsupported() {
               JettyNpnSslEngine.this.getSession().setApplicationProtocol((String)nextProtocols.get(nextProtocols.size() - 1));
            }

            public List protocols() {
               return nextProtocols;
            }

            public void protocolSelected(String protocol) {
               JettyNpnSslEngine.this.getSession().setApplicationProtocol(protocol);
            }
         });
      } else {
         final String[] list = (String[])nextProtocols.toArray(new String[nextProtocols.size()]);
         final String fallback = list[list.length - 1];
         NextProtoNego.put(engine, new ClientProvider() {
            public boolean supports() {
               return true;
            }

            public void unsupported() {
               JettyNpnSslEngine.this.session.setApplicationProtocol((String)null);
            }

            public String selectProtocol(List protocols) {
               for(String p : list) {
                  if(protocols.contains(p)) {
                     return p;
                  }
               }

               return fallback;
            }
         });
      }

   }

   public JettyNpnSslSession getSession() {
      return this.session;
   }

   public void closeInbound() throws SSLException {
      NextProtoNego.remove(this.engine);
      this.engine.closeInbound();
   }

   public void closeOutbound() {
      NextProtoNego.remove(this.engine);
      this.engine.closeOutbound();
   }

   public String getPeerHost() {
      return this.engine.getPeerHost();
   }

   public int getPeerPort() {
      return this.engine.getPeerPort();
   }

   public SSLEngineResult wrap(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws SSLException {
      return this.engine.wrap(byteBuffer, byteBuffer2);
   }

   public SSLEngineResult wrap(ByteBuffer[] byteBuffers, ByteBuffer byteBuffer) throws SSLException {
      return this.engine.wrap(byteBuffers, byteBuffer);
   }

   public SSLEngineResult wrap(ByteBuffer[] byteBuffers, int i, int i2, ByteBuffer byteBuffer) throws SSLException {
      return this.engine.wrap(byteBuffers, i, i2, byteBuffer);
   }

   public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws SSLException {
      return this.engine.unwrap(byteBuffer, byteBuffer2);
   }

   public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer[] byteBuffers) throws SSLException {
      return this.engine.unwrap(byteBuffer, byteBuffers);
   }

   public SSLEngineResult unwrap(ByteBuffer byteBuffer, ByteBuffer[] byteBuffers, int i, int i2) throws SSLException {
      return this.engine.unwrap(byteBuffer, byteBuffers, i, i2);
   }

   public Runnable getDelegatedTask() {
      return this.engine.getDelegatedTask();
   }

   public boolean isInboundDone() {
      return this.engine.isInboundDone();
   }

   public boolean isOutboundDone() {
      return this.engine.isOutboundDone();
   }

   public String[] getSupportedCipherSuites() {
      return this.engine.getSupportedCipherSuites();
   }

   public String[] getEnabledCipherSuites() {
      return this.engine.getEnabledCipherSuites();
   }

   public void setEnabledCipherSuites(String[] strings) {
      this.engine.setEnabledCipherSuites(strings);
   }

   public String[] getSupportedProtocols() {
      return this.engine.getSupportedProtocols();
   }

   public String[] getEnabledProtocols() {
      return this.engine.getEnabledProtocols();
   }

   public void setEnabledProtocols(String[] strings) {
      this.engine.setEnabledProtocols(strings);
   }

   public SSLSession getHandshakeSession() {
      return this.engine.getHandshakeSession();
   }

   public void beginHandshake() throws SSLException {
      this.engine.beginHandshake();
   }

   public HandshakeStatus getHandshakeStatus() {
      return this.engine.getHandshakeStatus();
   }

   public void setUseClientMode(boolean b) {
      this.engine.setUseClientMode(b);
   }

   public boolean getUseClientMode() {
      return this.engine.getUseClientMode();
   }

   public void setNeedClientAuth(boolean b) {
      this.engine.setNeedClientAuth(b);
   }

   public boolean getNeedClientAuth() {
      return this.engine.getNeedClientAuth();
   }

   public void setWantClientAuth(boolean b) {
      this.engine.setWantClientAuth(b);
   }

   public boolean getWantClientAuth() {
      return this.engine.getWantClientAuth();
   }

   public void setEnableSessionCreation(boolean b) {
      this.engine.setEnableSessionCreation(b);
   }

   public boolean getEnableSessionCreation() {
      return this.engine.getEnableSessionCreation();
   }

   public SSLParameters getSSLParameters() {
      return this.engine.getSSLParameters();
   }

   public void setSSLParameters(SSLParameters sslParameters) {
      this.engine.setSSLParameters(sslParameters);
   }
}
