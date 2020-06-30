package org.apache.http.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSession;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.conn.HttpRoutedConnection;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/** @deprecated */
@Deprecated
public interface ManagedClientConnection extends HttpClientConnection, HttpRoutedConnection, ManagedHttpClientConnection, ConnectionReleaseTrigger {
   boolean isSecure();

   HttpRoute getRoute();

   SSLSession getSSLSession();

   void open(HttpRoute var1, HttpContext var2, HttpParams var3) throws IOException;

   void tunnelTarget(boolean var1, HttpParams var2) throws IOException;

   void tunnelProxy(HttpHost var1, boolean var2, HttpParams var3) throws IOException;

   void layerProtocol(HttpContext var1, HttpParams var2) throws IOException;

   void markReusable();

   void unmarkReusable();

   boolean isMarkedReusable();

   void setState(Object var1);

   Object getState();

   void setIdleDuration(long var1, TimeUnit var3);
}
