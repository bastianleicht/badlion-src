package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
public interface SocketFactory {
   Socket createSocket() throws IOException;

   Socket connectSocket(Socket var1, String var2, int var3, InetAddress var4, int var5, HttpParams var6) throws IOException, UnknownHostException, ConnectTimeoutException;

   boolean isSecure(Socket var1) throws IllegalArgumentException;
}
