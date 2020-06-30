package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.SchemeLayeredSocketFactory;
import org.apache.http.conn.scheme.SchemeSocketFactoryAdaptor;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
class SchemeLayeredSocketFactoryAdaptor extends SchemeSocketFactoryAdaptor implements SchemeLayeredSocketFactory {
   private final LayeredSocketFactory factory;

   SchemeLayeredSocketFactoryAdaptor(LayeredSocketFactory factory) {
      super(factory);
      this.factory = factory;
   }

   public Socket createLayeredSocket(Socket socket, String target, int port, HttpParams params) throws IOException, UnknownHostException {
      return this.factory.createSocket(socket, target, port, true);
   }
}
