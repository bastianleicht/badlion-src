package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;
import org.apache.http.impl.conn.HttpConnPool;
import org.apache.http.impl.conn.HttpPoolEntry;
import org.apache.http.impl.conn.ManagedClientConnectionImpl;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.pool.PoolStats;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

/** @deprecated */
@Deprecated
@ThreadSafe
public class PoolingClientConnectionManager implements ClientConnectionManager, ConnPoolControl {
   private final Log log;
   private final SchemeRegistry schemeRegistry;
   private final HttpConnPool pool;
   private final ClientConnectionOperator operator;
   private final DnsResolver dnsResolver;

   public PoolingClientConnectionManager(SchemeRegistry schreg) {
      this(schreg, -1L, TimeUnit.MILLISECONDS);
   }

   public PoolingClientConnectionManager(SchemeRegistry schreg, DnsResolver dnsResolver) {
      this(schreg, -1L, TimeUnit.MILLISECONDS, dnsResolver);
   }

   public PoolingClientConnectionManager() {
      this(SchemeRegistryFactory.createDefault());
   }

   public PoolingClientConnectionManager(SchemeRegistry schemeRegistry, long timeToLive, TimeUnit tunit) {
      this(schemeRegistry, timeToLive, tunit, new SystemDefaultDnsResolver());
   }

   public PoolingClientConnectionManager(SchemeRegistry schemeRegistry, long timeToLive, TimeUnit tunit, DnsResolver dnsResolver) {
      this.log = LogFactory.getLog(this.getClass());
      Args.notNull(schemeRegistry, "Scheme registry");
      Args.notNull(dnsResolver, "DNS resolver");
      this.schemeRegistry = schemeRegistry;
      this.dnsResolver = dnsResolver;
      this.operator = this.createConnectionOperator(schemeRegistry);
      this.pool = new HttpConnPool(this.log, this.operator, 2, 20, timeToLive, tunit);
   }

   protected void finalize() throws Throwable {
      try {
         this.shutdown();
      } finally {
         super.finalize();
      }

   }

   protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
      return new DefaultClientConnectionOperator(schreg, this.dnsResolver);
   }

   public SchemeRegistry getSchemeRegistry() {
      return this.schemeRegistry;
   }

   private String format(HttpRoute route, Object state) {
      StringBuilder buf = new StringBuilder();
      buf.append("[route: ").append(route).append("]");
      if(state != null) {
         buf.append("[state: ").append(state).append("]");
      }

      return buf.toString();
   }

   private String formatStats(HttpRoute route) {
      StringBuilder buf = new StringBuilder();
      PoolStats totals = this.pool.getTotalStats();
      PoolStats stats = this.pool.getStats(route);
      buf.append("[total kept alive: ").append(totals.getAvailable()).append("; ");
      buf.append("route allocated: ").append(stats.getLeased() + stats.getAvailable());
      buf.append(" of ").append(stats.getMax()).append("; ");
      buf.append("total allocated: ").append(totals.getLeased() + totals.getAvailable());
      buf.append(" of ").append(totals.getMax()).append("]");
      return buf.toString();
   }

   private String format(HttpPoolEntry entry) {
      StringBuilder buf = new StringBuilder();
      buf.append("[id: ").append(entry.getId()).append("]");
      buf.append("[route: ").append(entry.getRoute()).append("]");
      Object state = entry.getState();
      if(state != null) {
         buf.append("[state: ").append(state).append("]");
      }

      return buf.toString();
   }

   public ClientConnectionRequest requestConnection(HttpRoute route, Object state) {
      Args.notNull(route, "HTTP route");
      if(this.log.isDebugEnabled()) {
         this.log.debug("Connection request: " + this.format(route, state) + this.formatStats(route));
      }

      final Future<HttpPoolEntry> future = this.pool.lease(route, state);
      return new ClientConnectionRequest() {
         public void abortRequest() {
            future.cancel(true);
         }

         public ManagedClientConnection getConnection(long timeout, TimeUnit tunit) throws InterruptedException, ConnectionPoolTimeoutException {
            return PoolingClientConnectionManager.this.leaseConnection(future, timeout, tunit);
         }
      };
   }

   ManagedClientConnection leaseConnection(Future future, long timeout, TimeUnit tunit) throws InterruptedException, ConnectionPoolTimeoutException {
      try {
         HttpPoolEntry entry = (HttpPoolEntry)future.get(timeout, tunit);
         if(entry != null && !future.isCancelled()) {
            Asserts.check(entry.getConnection() != null, "Pool entry with no connection");
            if(this.log.isDebugEnabled()) {
               this.log.debug("Connection leased: " + this.format(entry) + this.formatStats((HttpRoute)entry.getRoute()));
            }

            return new ManagedClientConnectionImpl(this, this.operator, entry);
         } else {
            throw new InterruptedException();
         }
      } catch (ExecutionException var8) {
         Throwable cause = var8.getCause();
         if(cause == null) {
            cause = var8;
         }

         this.log.error("Unexpected exception leasing connection from pool", cause);
         throw new InterruptedException();
      } catch (TimeoutException var9) {
         throw new ConnectionPoolTimeoutException("Timeout waiting for connection from pool");
      }
   }

   public void releaseConnection(ManagedClientConnection conn, long keepalive, TimeUnit tunit) {
      Args.check(conn instanceof ManagedClientConnectionImpl, "Connection class mismatch, connection not obtained from this manager");
      ManagedClientConnectionImpl managedConn = (ManagedClientConnectionImpl)conn;
      Asserts.check(managedConn.getManager() == this, "Connection not obtained from this manager");
      synchronized(managedConn) {
         HttpPoolEntry entry = managedConn.detach();
         if(entry != null) {
            try {
               if(managedConn.isOpen() && !managedConn.isMarkedReusable()) {
                  try {
                     managedConn.shutdown();
                  } catch (IOException var14) {
                     if(this.log.isDebugEnabled()) {
                        this.log.debug("I/O exception shutting down released connection", var14);
                     }
                  }
               }

               if(managedConn.isMarkedReusable()) {
                  entry.updateExpiry(keepalive, tunit != null?tunit:TimeUnit.MILLISECONDS);
                  if(this.log.isDebugEnabled()) {
                     String s;
                     if(keepalive > 0L) {
                        s = "for " + keepalive + " " + tunit;
                     } else {
                        s = "indefinitely";
                     }

                     this.log.debug("Connection " + this.format(entry) + " can be kept alive " + s);
                  }
               }
            } finally {
               this.pool.release(entry, managedConn.isMarkedReusable());
            }

            if(this.log.isDebugEnabled()) {
               this.log.debug("Connection released: " + this.format(entry) + this.formatStats((HttpRoute)entry.getRoute()));
            }

         }
      }
   }

   public void shutdown() {
      this.log.debug("Connection manager is shutting down");

      try {
         this.pool.shutdown();
      } catch (IOException var2) {
         this.log.debug("I/O exception shutting down connection manager", var2);
      }

      this.log.debug("Connection manager shut down");
   }

   public void closeIdleConnections(long idleTimeout, TimeUnit tunit) {
      if(this.log.isDebugEnabled()) {
         this.log.debug("Closing connections idle longer than " + idleTimeout + " " + tunit);
      }

      this.pool.closeIdle(idleTimeout, tunit);
   }

   public void closeExpiredConnections() {
      this.log.debug("Closing expired connections");
      this.pool.closeExpired();
   }

   public int getMaxTotal() {
      return this.pool.getMaxTotal();
   }

   public void setMaxTotal(int max) {
      this.pool.setMaxTotal(max);
   }

   public int getDefaultMaxPerRoute() {
      return this.pool.getDefaultMaxPerRoute();
   }

   public void setDefaultMaxPerRoute(int max) {
      this.pool.setDefaultMaxPerRoute(max);
   }

   public int getMaxPerRoute(HttpRoute route) {
      return this.pool.getMaxPerRoute(route);
   }

   public void setMaxPerRoute(HttpRoute route, int max) {
      this.pool.setMaxPerRoute(route, max);
   }

   public PoolStats getTotalStats() {
      return this.pool.getTotalStats();
   }

   public PoolStats getStats(HttpRoute route) {
      return this.pool.getStats(route);
   }
}
