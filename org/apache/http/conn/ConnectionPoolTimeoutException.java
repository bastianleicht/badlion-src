package org.apache.http.conn;

import org.apache.http.annotation.Immutable;
import org.apache.http.conn.ConnectTimeoutException;

@Immutable
public class ConnectionPoolTimeoutException extends ConnectTimeoutException {
   private static final long serialVersionUID = -7898874842020245128L;

   public ConnectionPoolTimeoutException() {
   }

   public ConnectionPoolTimeoutException(String message) {
      super(message);
   }
}
