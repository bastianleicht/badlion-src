package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.http.conn.scheme.SocketFactory;

/** @deprecated */
@Deprecated
public interface LayeredSocketFactory extends SocketFactory {
   Socket createSocket(Socket var1, String var2, int var3, boolean var4) throws IOException, UnknownHostException;
}
