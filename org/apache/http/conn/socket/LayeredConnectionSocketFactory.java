package org.apache.http.conn.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

public interface LayeredConnectionSocketFactory extends ConnectionSocketFactory {
   Socket createLayeredSocket(Socket var1, String var2, int var3, HttpContext var4) throws IOException, UnknownHostException;
}
