package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;

public interface EofSensorWatcher {
   boolean eofDetected(InputStream var1) throws IOException;

   boolean streamClosed(InputStream var1) throws IOException;

   boolean streamAbort(InputStream var1) throws IOException;
}
