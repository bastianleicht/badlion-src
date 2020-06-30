package org.apache.http.conn;

import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLSession;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpInetConnection;

public interface ManagedHttpClientConnection extends HttpClientConnection, HttpInetConnection {
   String getId();

   void bind(Socket var1) throws IOException;

   Socket getSocket();

   SSLSession getSSLSession();
}
