package org.apache.http.conn;

import org.apache.http.HttpConnection;
import org.apache.http.config.ConnectionConfig;

public interface HttpConnectionFactory {
   HttpConnection create(Object var1, ConnectionConfig var2);
}
