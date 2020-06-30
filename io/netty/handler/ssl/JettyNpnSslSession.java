package io.netty.handler.ssl;

import java.security.Principal;
import java.security.cert.Certificate;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.security.cert.X509Certificate;

final class JettyNpnSslSession implements SSLSession {
   private final SSLEngine engine;
   private volatile String applicationProtocol;

   JettyNpnSslSession(SSLEngine engine) {
      this.engine = engine;
   }

   void setApplicationProtocol(String applicationProtocol) {
      if(applicationProtocol != null) {
         applicationProtocol = applicationProtocol.replace(':', '_');
      }

      this.applicationProtocol = applicationProtocol;
   }

   public String getProtocol() {
      String protocol = this.unwrap().getProtocol();
      String applicationProtocol = this.applicationProtocol;
      if(applicationProtocol == null) {
         return protocol != null?protocol.replace(':', '_'):null;
      } else {
         StringBuilder buf = new StringBuilder(32);
         if(protocol != null) {
            buf.append(protocol.replace(':', '_'));
            buf.append(':');
         } else {
            buf.append("null:");
         }

         buf.append(applicationProtocol);
         return buf.toString();
      }
   }

   private SSLSession unwrap() {
      return this.engine.getSession();
   }

   public byte[] getId() {
      return this.unwrap().getId();
   }

   public SSLSessionContext getSessionContext() {
      return this.unwrap().getSessionContext();
   }

   public long getCreationTime() {
      return this.unwrap().getCreationTime();
   }

   public long getLastAccessedTime() {
      return this.unwrap().getLastAccessedTime();
   }

   public void invalidate() {
      this.unwrap().invalidate();
   }

   public boolean isValid() {
      return this.unwrap().isValid();
   }

   public void putValue(String s, Object o) {
      this.unwrap().putValue(s, o);
   }

   public Object getValue(String s) {
      return this.unwrap().getValue(s);
   }

   public void removeValue(String s) {
      this.unwrap().removeValue(s);
   }

   public String[] getValueNames() {
      return this.unwrap().getValueNames();
   }

   public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
      return this.unwrap().getPeerCertificates();
   }

   public Certificate[] getLocalCertificates() {
      return this.unwrap().getLocalCertificates();
   }

   public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
      return this.unwrap().getPeerCertificateChain();
   }

   public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
      return this.unwrap().getPeerPrincipal();
   }

   public Principal getLocalPrincipal() {
      return this.unwrap().getLocalPrincipal();
   }

   public String getCipherSuite() {
      return this.unwrap().getCipherSuite();
   }

   public String getPeerHost() {
      return this.unwrap().getPeerHost();
   }

   public int getPeerPort() {
      return this.unwrap().getPeerPort();
   }

   public int getPacketBufferSize() {
      return this.unwrap().getPacketBufferSize();
   }

   public int getApplicationBufferSize() {
      return this.unwrap().getApplicationBufferSize();
   }
}
