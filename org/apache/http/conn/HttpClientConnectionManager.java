package org.apache.http.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpClientConnection;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;

public interface HttpClientConnectionManager {
   ConnectionRequest requestConnection(HttpRoute var1, Object var2);

   void releaseConnection(HttpClientConnection var1, Object var2, long var3, TimeUnit var5);

   void connect(HttpClientConnection var1, HttpRoute var2, int var3, HttpContext var4) throws IOException;

   void upgrade(HttpClientConnection var1, HttpRoute var2, HttpContext var3) throws IOException;

   void routeComplete(HttpClientConnection var1, HttpRoute var2, HttpContext var3) throws IOException;

   void closeIdleConnections(long var1, TimeUnit var3);

   void closeExpiredConnections();

   void shutdown();
}
