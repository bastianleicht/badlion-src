package org.apache.http;

import java.net.InetAddress;
import org.apache.http.HttpConnection;

public interface HttpInetConnection extends HttpConnection {
   InetAddress getLocalAddress();

   int getLocalPort();

   InetAddress getRemoteAddress();

   int getRemotePort();
}
