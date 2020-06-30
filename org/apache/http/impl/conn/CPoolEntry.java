package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.http.HttpClientConnection;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.pool.PoolEntry;

@ThreadSafe
class CPoolEntry extends PoolEntry {
   private final Log log;
   private volatile boolean routeComplete;

   public CPoolEntry(Log log, String id, HttpRoute route, ManagedHttpClientConnection conn, long timeToLive, TimeUnit tunit) {
      super(id, route, conn, timeToLive, tunit);
      this.log = log;
   }

   public void markRouteComplete() {
      this.routeComplete = true;
   }

   public boolean isRouteComplete() {
      return this.routeComplete;
   }

   public void closeConnection() throws IOException {
      HttpClientConnection conn = (HttpClientConnection)this.getConnection();
      conn.close();
   }

   public void shutdownConnection() throws IOException {
      HttpClientConnection conn = (HttpClientConnection)this.getConnection();
      conn.shutdown();
   }

   public boolean isExpired(long now) {
      boolean expired = super.isExpired(now);
      if(expired && this.log.isDebugEnabled()) {
         this.log.debug("Connection " + this + " expired @ " + new Date(this.getExpiry()));
      }

      return expired;
   }

   public boolean isClosed() {
      HttpClientConnection conn = (HttpClientConnection)this.getConnection();
      return !conn.isOpen();
   }

   public void close() {
      try {
         this.closeConnection();
      } catch (IOException var2) {
         this.log.debug("I/O error closing connection", var2);
      }

   }
}
