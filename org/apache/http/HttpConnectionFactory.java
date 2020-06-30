package org.apache.http;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.HttpConnection;

public interface HttpConnectionFactory {
   HttpConnection createConnection(Socket var1) throws IOException;
}
