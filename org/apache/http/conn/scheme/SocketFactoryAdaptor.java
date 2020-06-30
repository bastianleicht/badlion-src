package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
class SocketFactoryAdaptor implements SocketFactory {
   private final SchemeSocketFactory factory;

   SocketFactoryAdaptor(SchemeSocketFactory factory) {
      this.factory = factory;
   }

   public Socket createSocket() throws IOException {
      HttpParams params = new BasicHttpParams();
      return this.factory.createSocket(params);
   }

   public Socket connectSocket(Socket socket, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
      InetSocketAddress local = null;
      if(localAddress != null || localPort > 0) {
         local = new InetSocketAddress(localAddress, localPort > 0?localPort:0);
      }

      InetAddress remoteAddress = InetAddress.getByName(host);
      InetSocketAddress remote = new InetSocketAddress(remoteAddress, port);
      return this.factory.connectSocket(socket, remote, local, params);
   }

   public boolean isSecure(Socket socket) throws IllegalArgumentException {
      return this.factory.isSecure(socket);
   }

   public SchemeSocketFactory getFactory() {
      return this.factory;
   }

   public boolean equals(Object obj) {
      return obj == null?false:(this == obj?true:(obj instanceof SocketFactoryAdaptor?this.factory.equals(((SocketFactoryAdaptor)obj).factory):this.factory.equals(obj)));
   }

   public int hashCode() {
      return this.factory.hashCode();
   }
}
