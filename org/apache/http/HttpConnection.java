package org.apache.http;

import java.io.Closeable;
import java.io.IOException;
import org.apache.http.HttpConnectionMetrics;

public interface HttpConnection extends Closeable {
   void close() throws IOException;

   boolean isOpen();

   boolean isStale();

   void setSocketTimeout(int var1);

   int getSocketTimeout();

   void shutdown() throws IOException;

   HttpConnectionMetrics getMetrics();
}
