package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.http.conn.scheme.LayeredSchemeSocketFactory;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.SocketFactoryAdaptor;

/** @deprecated */
@Deprecated
class LayeredSocketFactoryAdaptor extends SocketFactoryAdaptor implements LayeredSocketFactory {
   private final LayeredSchemeSocketFactory factory;

   LayeredSocketFactoryAdaptor(LayeredSchemeSocketFactory factory) {
      super(factory);
      this.factory = factory;
   }

   public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
      return this.factory.createLayeredSocket(socket, host, port, autoClose);
   }
}
