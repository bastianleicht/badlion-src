package org.apache.http.impl.pool;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.pool.BasicConnFactory;
import org.apache.http.impl.pool.BasicPoolEntry;
import org.apache.http.params.HttpParams;
import org.apache.http.pool.AbstractConnPool;
import org.apache.http.pool.ConnFactory;

@ThreadSafe
public class BasicConnPool extends AbstractConnPool {
   private static final AtomicLong COUNTER = new AtomicLong();

   public BasicConnPool(ConnFactory connFactory) {
      super(connFactory, 2, 20);
   }

   /** @deprecated */
   @Deprecated
   public BasicConnPool(HttpParams params) {
      super(new BasicConnFactory(params), 2, 20);
   }

   public BasicConnPool(SocketConfig sconfig, ConnectionConfig cconfig) {
      super(new BasicConnFactory(sconfig, cconfig), 2, 20);
   }

   public BasicConnPool() {
      super(new BasicConnFactory(SocketConfig.DEFAULT, ConnectionConfig.DEFAULT), 2, 20);
   }

   protected BasicPoolEntry createEntry(HttpHost host, HttpClientConnection conn) {
      return new BasicPoolEntry(Long.toString(COUNTER.getAndIncrement()), host, conn);
   }
}
