package org.apache.http.conn.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.apache.http.HttpHost;
import org.apache.http.protocol.HttpContext;

public interface ConnectionSocketFactory {
   Socket createSocket(HttpContext var1) throws IOException;

   Socket connectSocket(int var1, Socket var2, HttpHost var3, InetSocketAddress var4, InetSocketAddress var5, HttpContext var6) throws IOException;
}
