package org.apache.http.conn;

import java.io.IOException;

public interface ConnectionReleaseTrigger {
   void releaseConnection() throws IOException;

   void abortConnection() throws IOException;
}
