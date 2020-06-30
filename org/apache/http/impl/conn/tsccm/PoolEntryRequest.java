package org.apache.http.impl.conn.tsccm;

import java.util.concurrent.TimeUnit;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.impl.conn.tsccm.BasicPoolEntry;

/** @deprecated */
@Deprecated
public interface PoolEntryRequest {
   BasicPoolEntry getPoolEntry(long var1, TimeUnit var3) throws InterruptedException, ConnectionPoolTimeoutException;

   void abortRequest();
}
