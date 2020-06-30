package org.apache.http.impl;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.SocketHttpServerConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
@NotThreadSafe
public class DefaultHttpServerConnection extends SocketHttpServerConnection {
   public void bind(Socket socket, HttpParams params) throws IOException {
      Args.notNull(socket, "Socket");
      Args.notNull(params, "HTTP parameters");
      this.assertNotOpen();
      socket.setTcpNoDelay(params.getBooleanParameter("http.tcp.nodelay", true));
      socket.setSoTimeout(params.getIntParameter("http.socket.timeout", 0));
      socket.setKeepAlive(params.getBooleanParameter("http.socket.keepalive", false));
      int linger = params.getIntParameter("http.socket.linger", -1);
      if(linger >= 0) {
         socket.setSoLinger(linger > 0, linger);
      }

      if(linger >= 0) {
         socket.setSoLinger(linger > 0, linger);
      }

      super.bind(socket, params);
   }
}
