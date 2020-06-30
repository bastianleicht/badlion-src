package org.apache.http.impl.pool;

import java.io.IOException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.pool.PoolEntry;

@ThreadSafe
public class BasicPoolEntry extends PoolEntry {
   public BasicPoolEntry(String id, HttpHost route, HttpClientConnection conn) {
      super(id, route, conn);
   }

   public void close() {
      try {
         ((HttpClientConnection)this.getConnection()).close();
      } catch (IOException var2) {
         ;
      }

   }

   public boolean isClosed() {
      return !((HttpClientConnection)this.getConnection()).isOpen();
   }
}
