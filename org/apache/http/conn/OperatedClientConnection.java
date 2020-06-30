package org.apache.http.conn;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.HttpInetConnection;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
public interface OperatedClientConnection extends HttpClientConnection, HttpInetConnection {
   HttpHost getTargetHost();

   boolean isSecure();

   Socket getSocket();

   void opening(Socket var1, HttpHost var2) throws IOException;

   void openCompleted(boolean var1, HttpParams var2) throws IOException;

   void update(Socket var1, HttpHost var2, boolean var3, HttpParams var4) throws IOException;
}
