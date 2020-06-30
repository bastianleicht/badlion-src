package org.apache.http.conn;

import java.io.IOException;
import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/** @deprecated */
@Deprecated
public interface ClientConnectionOperator {
   OperatedClientConnection createConnection();

   void openConnection(OperatedClientConnection var1, HttpHost var2, InetAddress var3, HttpContext var4, HttpParams var5) throws IOException;

   void updateSecureConnection(OperatedClientConnection var1, HttpHost var2, HttpContext var3, HttpParams var4) throws IOException;
}
