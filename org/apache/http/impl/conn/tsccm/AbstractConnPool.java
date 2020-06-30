package org.apache.http.impl.conn.tsccm;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.GuardedBy;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.IdleConnectionHandler;
import org.apache.http.impl.conn.tsccm.BasicPoolEntry;
import org.apache.http.impl.conn.tsccm.PoolEntryRequest;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
public abstract class AbstractConnPool {
   private final Log log = LogFactory.getLog(this.getClass());
   protected final Lock poolLock = new ReentrantLock();
   @GuardedBy("poolLock")
   protected Set leasedConnections = new HashSet();
   @GuardedBy("poolLock")
   protected int numConnections;
   protected volatile boolean isShutDown;
   protected Set issuedConnections;
   protected ReferenceQueue refQueue;
   protected IdleConnectionHandler idleConnHandler = new IdleConnectionHandler();

   public void enableConnectionGC() throws IllegalStateException {
   }

   public final BasicPoolEntry getEntry(HttpRoute route, Object state, long timeout, TimeUnit tunit) throws ConnectionPoolTimeoutException, InterruptedException {
      return this.requestPoolEntry(route, state).getPoolEntry(timeout, tunit);
   }

   public abstract PoolEntryRequest requestPoolEntry(HttpRoute var1, Object var2);

   public abstract void freeEntry(BasicPoolEntry var1, boolean var2, long var3, TimeUnit var5);

   public void handleReference(Reference ref) {
   }

   protected abstract void handleLostEntry(HttpRoute var1);

   public void closeIdleConnections(long idletime, TimeUnit tunit) {
      Args.notNull(tunit, "Time unit");
      this.poolLock.lock();

      try {
         this.idleConnHandler.closeIdleConnections(tunit.toMillis(idletime));
      } finally {
         this.poolLock.unlock();
      }

   }

   public void closeExpiredConnections() {
      this.poolLock.lock();

      try {
         this.idleConnHandler.closeExpiredConnections();
      } finally {
         this.poolLock.unlock();
      }

   }

   public abstract void deleteClosedConnections();

   public void shutdown() {
      this.poolLock.lock();

      try {
         if(!this.isShutDown) {
            Iterator<BasicPoolEntry> iter = this.leasedConnections.iterator();

            while(iter.hasNext()) {
               BasicPoolEntry entry = (BasicPoolEntry)iter.next();
               iter.remove();
               this.closeConnection(entry.getConnection());
            }

            this.idleConnHandler.removeAll();
            this.isShutDown = true;
            return;
         }
      } finally {
         this.poolLock.unlock();
      }

   }

   protected void closeConnection(OperatedClientConnection conn) {
      if(conn != null) {
         try {
            conn.close();
         } catch (IOException var3) {
            this.log.debug("I/O error closing connection", var3);
         }
      }

   }
}
