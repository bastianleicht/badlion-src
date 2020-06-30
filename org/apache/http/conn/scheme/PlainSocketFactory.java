package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import org.apache.http.annotation.Immutable;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
@Immutable
public class PlainSocketFactory implements SocketFactory, SchemeSocketFactory {
   private final HostNameResolver nameResolver;

   public static PlainSocketFactory getSocketFactory() {
      return new PlainSocketFactory();
   }

   /** @deprecated */
   @Deprecated
   public PlainSocketFactory(HostNameResolver nameResolver) {
      this.nameResolver = nameResolver;
   }

   public PlainSocketFactory() {
      this.nameResolver = null;
   }

   public Socket createSocket(HttpParams params) {
      return new Socket();
   }

   public Socket createSocket() {
      return new Socket();
   }

   public Socket connectSocket(Socket socket, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params) throws IOException, ConnectTimeoutException {
      Args.notNull(remoteAddress, "Remote address");
      Args.notNull(params, "HTTP parameters");
      Socket sock = socket;
      if(socket == null) {
         sock = this.createSocket();
      }

      if(localAddress != null) {
         sock.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params));
         sock.bind(localAddress);
      }

      int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
      int soTimeout = HttpConnectionParams.getSoTimeout(params);

      try {
         sock.setSoTimeout(soTimeout);
         sock.connect(remoteAddress, connTimeout);
         return sock;
      } catch (SocketTimeoutException var9) {
         throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
      }
   }

   public final boolean isSecure(Socket sock) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public Socket connectSocket(Socket socket, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
      InetSocketAddress local = null;
      if(localAddress != null || localPort > 0) {
         local = new InetSocketAddress(localAddress, localPort > 0?localPort:0);
      }

      InetAddress remoteAddress;
      if(this.nameResolver != null) {
         remoteAddress = this.nameResolver.resolve(host);
      } else {
         remoteAddress = InetAddress.getByName(host);
      }

      InetSocketAddress remote = new InetSocketAddress(remoteAddress, port);
      return this.connectSocket(socket, remote, local, params);
   }
}
