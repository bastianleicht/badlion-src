package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
public interface SchemeSocketFactory {
   Socket createSocket(HttpParams var1) throws IOException;

   Socket connectSocket(Socket var1, InetSocketAddress var2, InetSocketAddress var3, HttpParams var4) throws IOException, UnknownHostException, ConnectTimeoutException;

   boolean isSecure(Socket var1) throws IllegalArgumentException;
}
