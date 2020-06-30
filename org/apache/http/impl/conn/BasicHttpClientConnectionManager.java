package org.apache.http.impl.conn;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.annotation.GuardedBy;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.HttpClientConnectionOperator;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.LangUtils;

@ThreadSafe
public class BasicHttpClientConnectionManager implements HttpClientConnectionManager, Closeable {
   private final Log log;
   private final HttpClientConnectionOperator connectionOperator;
   private final HttpConnectionFactory connFactory;
   @GuardedBy("this")
   private ManagedHttpClientConnection conn;
   @GuardedBy("this")
   private HttpRoute route;
   @GuardedBy("this")
   private Object state;
   @GuardedBy("this")
   private long updated;
   @GuardedBy("this")
   private long expiry;
   @GuardedBy("this")
   private boolean leased;
   @GuardedBy("this")
   private SocketConfig socketConfig;
   @GuardedBy("this")
   private ConnectionConfig connConfig;
   @GuardedBy("this")
   private volatile boolean shutdown;

   private static Registry getDefaultRegistry() {
      return RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
   }

   public BasicHttpClientConnectionManager(Lookup socketFactoryRegistry, HttpConnectionFactory connFactory, SchemePortResolver schemePortResolver, DnsResolver dnsResolver) {
      this.log = LogFactory.getLog(this.getClass());
      this.connectionOperator = new HttpClientConnectionOperator(socketFactoryRegistry, schemePortResolver, dnsResolver);
      this.connFactory = (HttpConnectionFactory)(connFactory != null?connFactory:ManagedHttpClientConnectionFactory.INSTANCE);
      this.expiry = Long.MAX_VALUE;
      this.socketConfig = SocketConfig.DEFAULT;
      this.connConfig = ConnectionConfig.DEFAULT;
   }

   public BasicHttpClientConnectionManager(Lookup socketFactoryRegistry, HttpConnectionFactory connFactory) {
      this(socketFactoryRegistry, connFactory, (SchemePortResolver)null, (DnsResolver)null);
   }

   public BasicHttpClientConnectionManager(Lookup socketFactoryRegistry) {
      this(socketFactoryRegistry, (HttpConnectionFactory)null, (SchemePortResolver)null, (DnsResolver)null);
   }

   public BasicHttpClientConnectionManager() {
      this(getDefaultRegistry(), (HttpConnectionFactory)null, (SchemePortResolver)null, (DnsResolver)null);
   }

   protected void finalize() throws Throwable {
      try {
         this.shutdown();
      } finally {
         super.finalize();
      }

   }

   public void close() {
      this.shutdown();
   }

   HttpRoute getRoute() {
      return this.route;
   }

   Object getState() {
      return this.state;
   }

   public synchronized SocketConfig getSocketConfig() {
      return this.socketConfig;
   }

   public synchronized void setSocketConfig(SocketConfig socketConfig) {
      this.socketConfig = socketConfig != null?socketConfig:SocketConfig.DEFAULT;
   }

   public synchronized ConnectionConfig getConnectionConfig() {
      return this.connConfig;
   }

   public synchronized void setConnectionConfig(ConnectionConfig connConfig) {
      this.connConfig = connConfig != null?connConfig:ConnectionConfig.DEFAULT;
   }

   public final ConnectionRequest requestConnection(final HttpRoute route, final Object state) {
      Args.notNull(route, "Route");
      return new ConnectionRequest() {
         public boolean cancel() {
            return false;
         }

         public HttpClientConnection get(long timeout, TimeUnit tunit) {
            return BasicHttpClientConnectionManager.this.getConnection(route, state);
         }
      };
   }

   private void closeConnection() {
      if(this.conn != null) {
         this.log.debug("Closing connection");

         try {
            this.conn.close();
         } catch (IOException var2) {
            if(this.log.isDebugEnabled()) {
               this.log.debug("I/O exception closing connection", var2);
            }
         }

         this.conn = null;
      }

   }

   private void shutdownConnection() {
      if(this.conn != null) {
         this.log.debug("Shutting down connection");

         try {
            this.conn.shutdown();
         } catch (IOException var2) {
            if(this.log.isDebugEnabled()) {
               this.log.debug("I/O exception shutting down connection", var2);
            }
         }

         this.conn = null;
      }

   }

   private void checkExpiry() {
      if(this.conn != null && System.currentTimeMillis() >= this.expiry) {
         if(this.log.isDebugEnabled()) {
            this.log.debug("Connection expired @ " + new Date(this.expiry));
         }

         this.closeConnection();
      }

   }

   synchronized HttpClientConnection getConnection(HttpRoute route, Object state) {
      Asserts.check(!this.shutdown, "Connection manager has been shut down");
      if(this.log.isDebugEnabled()) {
         this.log.debug("Get connection for route " + route);
      }

      Asserts.check(!this.leased, "Connection is still allocated");
      if(!LangUtils.equals((Object)this.route, (Object)route) || !LangUtils.equals(this.state, state)) {
         this.closeConnection();
      }

      this.route = route;
      this.state = state;
      this.checkExpiry();
      if(this.conn == null) {
         this.conn = (ManagedHttpClientConnection)this.connFactory.create(route, this.connConfig);
      }

      this.leased = true;
      return this.conn;
   }

   public synchronized void releaseConnection(HttpClientConnection conn, Object state, long keepalive, TimeUnit tunit) {
      Args.notNull(conn, "Connection");
      Asserts.check(conn == this.conn, "Connection not obtained from this manager");
      if(this.log.isDebugEnabled()) {
         this.log.debug("Releasing connection " + conn);
      }

      if(this.shutdown) {
         this.shutdownConnection();
      } else {
         try {
            this.updated = System.currentTimeMillis();
            if(!this.conn.isOpen()) {
               this.conn = null;
               this.route = null;
               this.conn = null;
               this.expiry = Long.MAX_VALUE;
            } else {
               this.state = state;
               if(this.log.isDebugEnabled()) {
                  String s;
                  if(keepalive > 0L) {
                     s = "for " + keepalive + " " + tunit;
                  } else {
                     s = "indefinitely";
                  }

                  this.log.debug("Connection can be kept alive " + s);
               }

               if(keepalive > 0L) {
                  this.expiry = this.updated + tunit.toMillis(keepalive);
               } else {
                  this.expiry = Long.MAX_VALUE;
               }
            }
         } finally {
            this.leased = false;
         }

      }
   }

   public void connect(HttpClientConnection conn, HttpRoute route, int connectTimeout, HttpContext context) throws IOException {
      Args.notNull(conn, "Connection");
      Args.notNull(route, "HTTP route");
      Asserts.check(conn == this.conn, "Connection not obtained from this manager");
      HttpHost host;
      if(route.getProxyHost() != null) {
         host = route.getProxyHost();
      } else {
         host = route.getTargetHost();
      }

      InetSocketAddress localAddress = route.getLocalSocketAddress();
      this.connectionOperator.connect(this.conn, host, localAddress, connectTimeout, this.socketConfig, context);
   }

   public void upgrade(HttpClientConnection conn, HttpRoute route, HttpContext context) throws IOException {
      Args.notNull(conn, "Connection");
      Args.notNull(route, "HTTP route");
      Asserts.check(conn == this.conn, "Connection not obtained from this manager");
      this.connectionOperator.upgrade(this.conn, route.getTargetHost(), context);
   }

   public void routeComplete(HttpClientConnection conn, HttpRoute route, HttpContext context) throws IOException {
   }

   public synchronized void closeExpiredConnections() {
      if(!this.shutdown) {
         if(!this.leased) {
            this.checkExpiry();
         }

      }
   }

   public synchronized void closeIdleConnections(long idletime, TimeUnit tunit) {
      Args.notNull(tunit, "Time unit");
      if(!this.shutdown) {
         if(!this.leased) {
            long time = tunit.toMillis(idletime);
            if(time < 0L) {
               time = 0L;
            }

            long deadline = System.currentTimeMillis() - time;
            if(this.updated <= deadline) {
               this.closeConnection();
            }
         }

      }
   }

   public synchronized void shutdown() {
      if(!this.shutdown) {
         this.shutdown = true;
         this.shutdownConnection();
      }
   }
}
