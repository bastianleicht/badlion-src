package org.apache.http.conn;

import java.util.concurrent.TimeUnit;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;

/** @deprecated */
@Deprecated
public interface ClientConnectionManager {
   SchemeRegistry getSchemeRegistry();

   ClientConnectionRequest requestConnection(HttpRoute var1, Object var2);

   void releaseConnection(ManagedClientConnection var1, long var2, TimeUnit var4);

   void closeIdleConnections(long var1, TimeUnit var3);

   void closeExpiredConnections();

   void shutdown();
}
