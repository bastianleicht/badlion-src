package org.apache.http.impl.conn.tsccm;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.tsccm.AbstractConnPool;
import org.apache.http.impl.conn.tsccm.BasicPoolEntry;
import org.apache.http.impl.conn.tsccm.PoolEntryRequest;
import org.apache.http.impl.conn.tsccm.RouteSpecificPool;
import org.apache.http.impl.conn.tsccm.WaitingThread;
import org.apache.http.impl.conn.tsccm.WaitingThreadAborter;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

/** @deprecated */
@Deprecated
public class ConnPoolByRoute extends AbstractConnPool {
   private final Log log;
   private final Lock poolLock;
   protected final ClientConnectionOperator operator;
   protected final ConnPerRoute connPerRoute;
   protected final Set leasedConnections;
   protected final Queue freeConnections;
   protected final Queue waitingThreads;
   protected final Map routeToPool;
   private final long connTTL;
   private final TimeUnit connTTLTimeUnit;
   protected volatile boolean shutdown;
   protected volatile int maxTotalConnections;
   protected volatile int numConnections;

   public ConnPoolByRoute(ClientConnectionOperator operator, ConnPerRoute connPerRoute, int maxTotalConnections) {
      this(operator, connPerRoute, maxTotalConnections, -1L, TimeUnit.MILLISECONDS);
   }

   public ConnPoolByRoute(ClientConnectionOperator operator, ConnPerRoute connPerRoute, int maxTotalConnections, long connTTL, TimeUnit connTTLTimeUnit) {
      this.log = LogFactory.getLog(this.getClass());
      Args.notNull(operator, "Connection operator");
      Args.notNull(connPerRoute, "Connections per route");
      this.poolLock = super.poolLock;
      this.leasedConnections = super.leasedConnections;
      this.operator = operator;
      this.connPerRoute = connPerRoute;
      this.maxTotalConnections = maxTotalConnections;
      this.freeConnections = this.createFreeConnQueue();
      this.waitingThreads = this.createWaitingThreadQueue();
      this.routeToPool = this.createRouteToPoolMap();
      this.connTTL = connTTL;
      this.connTTLTimeUnit = connTTLTimeUnit;
   }

   protected Lock getLock() {
      return this.poolLock;
   }

   /** @deprecated */
   @Deprecated
   public ConnPoolByRoute(ClientConnectionOperator operator, HttpParams params) {
      this(operator, ConnManagerParams.getMaxConnectionsPerRoute(params), ConnManagerParams.getMaxTotalConnections(params));
   }

   protected Queue createFreeConnQueue() {
      return new LinkedList();
   }

   protected Queue createWaitingThreadQueue() {
      return new LinkedList();
   }

   protected Map createRouteToPoolMap() {
      return new HashMap();
   }

   protected RouteSpecificPool newRouteSpecificPool(HttpRoute route) {
      return new RouteSpecificPool(route, this.connPerRoute);
   }

   protected WaitingThread newWaitingThread(Condition cond, RouteSpecificPool rospl) {
      return new WaitingThread(cond, rospl);
   }

   private void closeConnection(BasicPoolEntry entry) {
      OperatedClientConnection conn = entry.getConnection();
      if(conn != null) {
         try {
            conn.close();
         } catch (IOException var4) {
            this.log.debug("I/O error closing connection", var4);
         }
      }

   }

   protected RouteSpecificPool getRoutePool(HttpRoute route, boolean create) {
      RouteSpecificPool rospl = null;
      this.poolLock.lock();

      try {
         rospl = (RouteSpecificPool)this.routeToPool.get(route);
         if(rospl == null && create) {
            rospl = this.newRouteSpecificPool(route);
            this.routeToPool.put(route, rospl);
         }
      } finally {
         this.poolLock.unlock();
      }

      return rospl;
   }

   public int getConnectionsInPool(HttpRoute route) {
      this.poolLock.lock();

      int var3;
      try {
         RouteSpecificPool rospl = this.getRoutePool(route, false);
         var3 = rospl != null?rospl.getEntryCount():0;
      } finally {
         this.poolLock.unlock();
      }

      return var3;
   }

   public int getConnectionsInPool() {
      this.poolLock.lock();

      int var1;
      try {
         var1 = this.numConnections;
      } finally {
         this.poolLock.unlock();
      }

      return var1;
   }

   public PoolEntryRequest requestPoolEntry(final HttpRoute route, final Object state) {
      final WaitingThreadAborter aborter = new WaitingThreadAborter();
      return new PoolEntryRequest() {
         public void abortRequest() {
            ConnPoolByRoute.this.poolLock.lock();

            try {
               aborter.abort();
            } finally {
               ConnPoolByRoute.this.poolLock.unlock();
            }

         }

         public BasicPoolEntry getPoolEntry(long timeout, TimeUnit tunit) throws InterruptedException, ConnectionPoolTimeoutException {
            return ConnPoolByRoute.this.getEntryBlocking(route, state, timeout, tunit, aborter);
         }
      };
   }

   protected BasicPoolEntry getEntryBlocking(HttpRoute route, Object state, long timeout, TimeUnit tunit, WaitingThreadAborter aborter) throws ConnectionPoolTimeoutException, InterruptedException {
      Date deadline = null;
      if(timeout > 0L) {
         deadline = new Date(System.currentTimeMillis() + tunit.toMillis(timeout));
      }

      BasicPoolEntry entry = null;
      this.poolLock.lock();

      try {
         RouteSpecificPool rospl = this.getRoutePool(route, true);
         WaitingThread waitingThread = null;

         while(entry == null) {
            Asserts.check(!this.shutdown, "Connection pool shut down");
            if(this.log.isDebugEnabled()) {
               this.log.debug("[" + route + "] total kept alive: " + this.freeConnections.size() + ", total issued: " + this.leasedConnections.size() + ", total allocated: " + this.numConnections + " out of " + this.maxTotalConnections);
            }

            entry = this.getFreeEntry(rospl, state);
            if(entry != null) {
               break;
            }

            boolean hasCapacity = rospl.getCapacity() > 0;
            if(this.log.isDebugEnabled()) {
               this.log.debug("Available capacity: " + rospl.getCapacity() + " out of " + rospl.getMaxEntries() + " [" + route + "][" + state + "]");
            }

            if(hasCapacity && this.numConnections < this.maxTotalConnections) {
               entry = this.createEntry(rospl, this.operator);
            } else if(hasCapacity && !this.freeConnections.isEmpty()) {
               this.deleteLeastUsedEntry();
               rospl = this.getRoutePool(route, true);
               entry = this.createEntry(rospl, this.operator);
            } else {
               if(this.log.isDebugEnabled()) {
                  this.log.debug("Need to wait for connection [" + route + "][" + state + "]");
               }

               if(waitingThread == null) {
                  waitingThread = this.newWaitingThread(this.poolLock.newCondition(), rospl);
                  aborter.setWaitingThread(waitingThread);
               }

               boolean success = false;

               try {
                  rospl.queueThread(waitingThread);
                  this.waitingThreads.add(waitingThread);
                  success = waitingThread.await(deadline);
               } finally {
                  rospl.removeThread(waitingThread);
                  this.waitingThreads.remove(waitingThread);
               }

               if(!success && deadline != null && deadline.getTime() <= System.currentTimeMillis()) {
                  throw new ConnectionPoolTimeoutException("Timeout waiting for connection from pool");
               }
            }
         }
      } finally {
         this.poolLock.unlock();
      }

      return entry;
   }

   public void freeEntry(BasicPoolEntry entry, boolean reusable, long validDuration, TimeUnit timeUnit) {
      HttpRoute route = entry.getPlannedRoute();
      if(this.log.isDebugEnabled()) {
         this.log.debug("Releasing connection [" + route + "][" + entry.getState() + "]");
      }

      this.poolLock.lock();

      try {
         if(!this.shutdown) {
            this.leasedConnections.remove(entry);
            RouteSpecificPool rospl = this.getRoutePool(route, true);
            if(reusable && rospl.getCapacity() >= 0) {
               if(this.log.isDebugEnabled()) {
                  String s;
                  if(validDuration > 0L) {
                     s = "for " + validDuration + " " + timeUnit;
                  } else {
                     s = "indefinitely";
                  }

                  this.log.debug("Pooling connection [" + route + "][" + entry.getState() + "]; keep alive " + s);
               }

               rospl.freeEntry(entry);
               entry.updateExpiry(validDuration, timeUnit);
               this.freeConnections.add(entry);
            } else {
               this.closeConnection(entry);
               rospl.dropEntry();
               --this.numConnections;
            }

            this.notifyWaitingThread(rospl);
            return;
         }

         this.closeConnection(entry);
      } finally {
         this.poolLock.unlock();
      }

   }

   protected BasicPoolEntry getFreeEntry(RouteSpecificPool rospl, Object state) {
      BasicPoolEntry entry = null;
      this.poolLock.lock();

      try {
         boolean done = false;

         while(!done) {
            entry = rospl.allocEntry(state);
            if(entry != null) {
               if(this.log.isDebugEnabled()) {
                  this.log.debug("Getting free connection [" + rospl.getRoute() + "][" + state + "]");
               }

               this.freeConnections.remove(entry);
               if(entry.isExpired(System.currentTimeMillis())) {
                  if(this.log.isDebugEnabled()) {
                     this.log.debug("Closing expired free connection [" + rospl.getRoute() + "][" + state + "]");
                  }

                  this.closeConnection(entry);
                  rospl.dropEntry();
                  --this.numConnections;
               } else {
                  this.leasedConnections.add(entry);
                  done = true;
               }
            } else {
               done = true;
               if(this.log.isDebugEnabled()) {
                  this.log.debug("No free connections [" + rospl.getRoute() + "][" + state + "]");
               }
            }
         }
      } finally {
         this.poolLock.unlock();
      }

      return entry;
   }

   protected BasicPoolEntry createEntry(RouteSpecificPool rospl, ClientConnectionOperator op) {
      if(this.log.isDebugEnabled()) {
         this.log.debug("Creating new connection [" + rospl.getRoute() + "]");
      }

      BasicPoolEntry entry = new BasicPoolEntry(op, rospl.getRoute(), this.connTTL, this.connTTLTimeUnit);
      this.poolLock.lock();

      try {
         rospl.createdEntry(entry);
         ++this.numConnections;
         this.leasedConnections.add(entry);
      } finally {
         this.poolLock.unlock();
      }

      return entry;
   }

   protected void deleteEntry(BasicPoolEntry entry) {
      HttpRoute route = entry.getPlannedRoute();
      if(this.log.isDebugEnabled()) {
         this.log.debug("Deleting connection [" + route + "][" + entry.getState() + "]");
      }

      this.poolLock.lock();

      try {
         this.closeConnection(entry);
         RouteSpecificPool rospl = this.getRoutePool(route, true);
         rospl.deleteEntry(entry);
         --this.numConnections;
         if(rospl.isUnused()) {
            this.routeToPool.remove(route);
         }
      } finally {
         this.poolLock.unlock();
      }

   }

   protected void deleteLeastUsedEntry() {
      this.poolLock.lock();

      try {
         BasicPoolEntry entry = (BasicPoolEntry)this.freeConnections.remove();
         if(entry != null) {
            this.deleteEntry(entry);
         } else if(this.log.isDebugEnabled()) {
            this.log.debug("No free connection to delete");
         }
      } finally {
         this.poolLock.unlock();
      }

   }

   protected void handleLostEntry(HttpRoute route) {
      this.poolLock.lock();

      try {
         RouteSpecificPool rospl = this.getRoutePool(route, true);
         rospl.dropEntry();
         if(rospl.isUnused()) {
            this.routeToPool.remove(route);
         }

         --this.numConnections;
         this.notifyWaitingThread(rospl);
      } finally {
         this.poolLock.unlock();
      }

   }

   protected void notifyWaitingThread(RouteSpecificPool rospl) {
      WaitingThread waitingThread = null;
      this.poolLock.lock();

      try {
         if(rospl != null && rospl.hasThread()) {
            if(this.log.isDebugEnabled()) {
               this.log.debug("Notifying thread waiting on pool [" + rospl.getRoute() + "]");
            }

            waitingThread = rospl.nextThread();
         } else if(!this.waitingThreads.isEmpty()) {
            if(this.log.isDebugEnabled()) {
               this.log.debug("Notifying thread waiting on any pool");
            }

            waitingThread = (WaitingThread)this.waitingThreads.remove();
         } else if(this.log.isDebugEnabled()) {
            this.log.debug("Notifying no-one, there are no waiting threads");
         }

         if(waitingThread != null) {
            waitingThread.wakeup();
         }
      } finally {
         this.poolLock.unlock();
      }

   }

   public void deleteClosedConnections() {
      this.poolLock.lock();

      try {
         Iterator<BasicPoolEntry> iter = this.freeConnections.iterator();

         while(iter.hasNext()) {
            BasicPoolEntry entry = (BasicPoolEntry)iter.next();
            if(!entry.getConnection().isOpen()) {
               iter.remove();
               this.deleteEntry(entry);
            }
         }
      } finally {
         this.poolLock.unlock();
      }

   }

   public void closeIdleConnections(long idletime, TimeUnit tunit) {
      Args.notNull(tunit, "Time unit");
      long t = idletime > 0L?idletime:0L;
      if(this.log.isDebugEnabled()) {
         this.log.debug("Closing connections idle longer than " + t + " " + tunit);
      }

      long deadline = System.currentTimeMillis() - tunit.toMillis(t);
      this.poolLock.lock();

      try {
         Iterator<BasicPoolEntry> iter = this.freeConnections.iterator();

         while(iter.hasNext()) {
            BasicPoolEntry entry = (BasicPoolEntry)iter.next();
            if(entry.getUpdated() <= deadline) {
               if(this.log.isDebugEnabled()) {
                  this.log.debug("Closing connection last used @ " + new Date(entry.getUpdated()));
               }

               iter.remove();
               this.deleteEntry(entry);
            }
         }
      } finally {
         this.poolLock.unlock();
      }

   }

   public void closeExpiredConnections() {
      this.log.debug("Closing expired connections");
      long now = System.currentTimeMillis();
      this.poolLock.lock();

      try {
         Iterator<BasicPoolEntry> iter = this.freeConnections.iterator();

         while(iter.hasNext()) {
            BasicPoolEntry entry = (BasicPoolEntry)iter.next();
            if(entry.isExpired(now)) {
               if(this.log.isDebugEnabled()) {
                  this.log.debug("Closing connection expired @ " + new Date(entry.getExpiry()));
               }

               iter.remove();
               this.deleteEntry(entry);
            }
         }
      } finally {
         this.poolLock.unlock();
      }

   }

   public void shutdown() {
      this.poolLock.lock();

      try {
         if(!this.shutdown) {
            this.shutdown = true;
            Iterator<BasicPoolEntry> iter1 = this.leasedConnections.iterator();

            while(iter1.hasNext()) {
               BasicPoolEntry entry = (BasicPoolEntry)iter1.next();
               iter1.remove();
               this.closeConnection(entry);
            }

            BasicPoolEntry entry;
            for(Iterator<BasicPoolEntry> iter2 = this.freeConnections.iterator(); iter2.hasNext(); this.closeConnection(entry)) {
               entry = (BasicPoolEntry)iter2.next();
               iter2.remove();
               if(this.log.isDebugEnabled()) {
                  this.log.debug("Closing connection [" + entry.getPlannedRoute() + "][" + entry.getState() + "]");
               }
            }

            Iterator<WaitingThread> iwth = this.waitingThreads.iterator();

            while(iwth.hasNext()) {
               WaitingThread waiter = (WaitingThread)iwth.next();
               iwth.remove();
               waiter.wakeup();
            }

            this.routeToPool.clear();
            return;
         }
      } finally {
         this.poolLock.unlock();
      }

   }

   public void setMaxTotalConnections(int max) {
      this.poolLock.lock();

      try {
         this.maxTotalConnections = max;
      } finally {
         this.poolLock.unlock();
      }

   }

   public int getMaxTotalConnections() {
      return this.maxTotalConnections;
   }
}
