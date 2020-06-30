package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpClientConnection;
import org.apache.http.annotation.GuardedBy;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;
import org.apache.http.impl.conn.HttpPoolEntry;
import org.apache.http.impl.conn.ManagedClientConnectionImpl;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

/** @deprecated */
@Deprecated
@ThreadSafe
public class BasicClientConnectionManager implements ClientConnectionManager {
   private final Log log;
   private static final AtomicLong COUNTER = new AtomicLong();
   public static final String MISUSE_MESSAGE = "Invalid use of BasicClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.";
   private final SchemeRegistry schemeRegistry;
   private final ClientConnectionOperator connOperator;
   @GuardedBy("this")
   private HttpPoolEntry poolEntry;
   @GuardedBy("this")
   private ManagedClientConnectionImpl conn;
   @GuardedBy("this")
   private volatile boolean shutdown;

   public BasicClientConnectionManager(SchemeRegistry schreg) {
      this.log = LogFactory.getLog(this.getClass());
      Args.notNull(schreg, "Scheme registry");
      this.schemeRegistry = schreg;
      this.connOperator = this.createConnectionOperator(schreg);
   }

   public BasicClientConnectionManager() {
      this(SchemeRegistryFactory.createDefault());
   }

   protected void finalize() throws Throwable {
      try {
         this.shutdown();
      } finally {
         super.finalize();
      }

   }

   public SchemeRegistry getSchemeRegistry() {
      return this.schemeRegistry;
   }

   protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
      return new DefaultClientConnectionOperator(schreg);
   }

   public final ClientConnectionRequest requestConnection(final HttpRoute route, final Object state) {
      return new ClientConnectionRequest() {
         public void abortRequest() {
         }

         public ManagedClientConnection getConnection(long timeout, TimeUnit tunit) {
            return BasicClientConnectionManager.this.getConnection(route, state);
         }
      };
   }

   private void assertNotShutdown() {
      Asserts.check(!this.shutdown, "Connection manager has been shut down");
   }

   ManagedClientConnection getConnection(HttpRoute route, Object state) {
      Args.notNull(route, "Route");
      synchronized(this) {
         this.assertNotShutdown();
         if(this.log.isDebugEnabled()) {
            this.log.debug("Get connection for route " + route);
         }

         Asserts.check(this.conn == null, "Invalid use of BasicClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.");
         if(this.poolEntry != null && !this.poolEntry.getPlannedRoute().equals(route)) {
            this.poolEntry.close();
            this.poolEntry = null;
         }

         if(this.poolEntry == null) {
            String id = Long.toString(COUNTER.getAndIncrement());
            OperatedClientConnection conn = this.connOperator.createConnection();
            this.poolEntry = new HttpPoolEntry(this.log, id, route, conn, 0L, TimeUnit.MILLISECONDS);
         }

         long now = System.currentTimeMillis();
         if(this.poolEntry.isExpired(now)) {
            this.poolEntry.close();
            this.poolEntry.getTracker().reset();
         }

         this.conn = new ManagedClientConnectionImpl(this, this.connOperator, this.poolEntry);
         return this.conn;
      }
   }

   private void shutdownConnection(HttpClientConnection conn) {
      try {
         conn.shutdown();
      } catch (IOException var3) {
         if(this.log.isDebugEnabled()) {
            this.log.debug("I/O exception shutting down connection", var3);
         }
      }

   }

   public void releaseConnection(ManagedClientConnection conn, long keepalive, TimeUnit tunit) {
      Args.check(conn instanceof ManagedClientConnectionImpl, "Connection class mismatch, connection not obtained from this manager");
      ManagedClientConnectionImpl managedConn = (ManagedClientConnectionImpl)conn;
      synchronized(managedConn) {
         if(this.log.isDebugEnabled()) {
            this.log.debug("Releasing connection " + conn);
         }

         if(managedConn.getPoolEntry() != null) {
            ClientConnectionManager manager = managedConn.getManager();
            Asserts.check(manager == this, "Connection not obtained from this manager");
            synchronized(this) {
               if(this.shutdown) {
                  this.shutdownConnection(managedConn);
                  return;
               }

               try {
                  if(managedConn.isOpen() && !managedConn.isMarkedReusable()) {
                     this.shutdownConnection(managedConn);
                  }

                  if(managedConn.isMarkedReusable()) {
                     this.poolEntry.updateExpiry(keepalive, tunit != null?tunit:TimeUnit.MILLISECONDS);
                     if(this.log.isDebugEnabled()) {
                        String s;
                        if(keepalive > 0L) {
                           s = "for " + keepalive + " " + tunit;
                        } else {
                           s = "indefinitely";
                        }

                        this.log.debug("Connection can be kept alive " + s);
                     }
                  }
               } finally {
                  managedConn.detach();
                  this.conn = null;
                  if(this.poolEntry.isClosed()) {
                     this.poolEntry = null;
                  }

               }
            }

         }
      }
   }

   public void closeExpiredConnections() {
      synchronized(this) {
         this.assertNotShutdown();
         long now = System.currentTimeMillis();
         if(this.poolEntry != null && this.poolEntry.isExpired(now)) {
            this.poolEntry.close();
            this.poolEntry.getTracker().reset();
         }

      }
   }

   public void closeIdleConnections(long idletime, TimeUnit tunit) {
      Args.notNull(tunit, "Time unit");
      synchronized(this) {
         this.assertNotShutdown();
         long time = tunit.toMillis(idletime);
         if(time < 0L) {
            time = 0L;
         }

         long deadline = System.currentTimeMillis() - time;
         if(this.poolEntry != null && this.poolEntry.getUpdated() <= deadline) {
            this.poolEntry.close();
            this.poolEntry.getTracker().reset();
         }

      }
   }

   public void shutdown() {
      synchronized(this) {
         this.shutdown = true;

         try {
            if(this.poolEntry != null) {
               this.poolEntry.close();
            }
         } finally {
            this.poolEntry = null;
            this.conn = null;
         }

      }
   }
}
