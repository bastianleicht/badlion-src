package org.apache.http.client.methods;

import java.io.IOException;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionReleaseTrigger;

/** @deprecated */
@Deprecated
public interface AbortableHttpRequest {
   void setConnectionRequest(ClientConnectionRequest var1) throws IOException;

   void setReleaseTrigger(ConnectionReleaseTrigger var1) throws IOException;

   void abort();
}
