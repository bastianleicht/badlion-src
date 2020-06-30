package org.apache.http.conn;

import javax.net.ssl.SSLSession;
import org.apache.http.HttpInetConnection;
import org.apache.http.conn.routing.HttpRoute;

/** @deprecated */
@Deprecated
public interface HttpRoutedConnection extends HttpInetConnection {
   boolean isSecure();

   HttpRoute getRoute();

   SSLSession getSSLSession();
}
