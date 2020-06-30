package org.apache.http.pool;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.pool.ConnFactory;
import org.apache.http.pool.ConnPool;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.pool.PoolEntry;
import org.apache.http.pool.PoolEntryCallback;
import org.apache.http.pool.PoolEntryFuture;
import org.apache.http.pool.PoolStats;
import org.apache.http.pool.RouteSpecificPool;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@ThreadSafe
public abstract class AbstractConnPool implements ConnPool, ConnPoolControl {
   private final Lock lock;
   private final ConnFactory connFactory;
   private final Map routeToPool;
   private final Set leased;
   private final LinkedList available;
   private final LinkedList pending;
   private final Map maxPerRoute;
   private volatile boolean isShutDown;
   private volatile int defaultMaxPerRoute;
   private volatile int maxTotal;

   public AbstractConnPool(ConnFactory connFactory, int defaultMaxPerRoute, int maxTotal) {
      this.connFactory = (ConnFactory)Args.notNull(connFactory, "Connection factory");
      this.defaultMaxPerRoute = Args.notNegative(defaultMaxPerRoute, "Max per route value");
      this.maxTotal = Args.notNegative(maxTotal, "Max total value");
      this.lock = new ReentrantLock();
      this.routeToPool = new HashMap();
      this.leased = new HashSet();
      this.available = new LinkedList();
      this.pending = new LinkedList();
      this.maxPerRoute = new HashMap();
   }

   protected abstract PoolEntry createEntry(Object var1, Object var2);

   protected void onLease(PoolEntry entry) {
   }

   protected void onRelease(PoolEntry entry) {
   }

   public boolean isShutdown() {
      return this.isShutDown;
   }

   public void shutdown() throws IOException {
      if(!this.isShutDown) {
         this.isShutDown = true;
         this.lock.lock();

         try {
            for(E entry : this.available) {
               entry.close();
            }

            for(E entry : this.leased) {
               entry.close();
            }

            for(RouteSpecificPool<T, C, E> pool : this.routeToPool.values()) {
               pool.shutdown();
            }

            this.routeToPool.clear();
            this.leased.clear();
            this.available.clear();
         } finally {
            this.lock.unlock();
         }

      }
   }

   private RouteSpecificPool getPool(final Object route) {
      RouteSpecificPool<T, C, E> pool = (RouteSpecificPool)this.routeToPool.get(route);
      if(pool == null) {
         pool = new RouteSpecificPool(route) {
            protected PoolEntry createEntry(Object conn) {
               return AbstractConnPool.this.createEntry(route, conn);
            }
         };
         this.routeToPool.put(route, pool);
      }

      return pool;
   }

   public Future lease(final Object route, final Object state, final FutureCallback callback) {
      Args.notNull(route, "Route");
      Asserts.check(!this.isShutDown, "Connection pool shut down");
      return new PoolEntryFuture(this.lock, callback) {
         public PoolEntry getPoolEntry(long timeout, TimeUnit tunit) throws InterruptedException, TimeoutException, IOException {
            E entry = AbstractConnPool.access$000(AbstractConnPool.this, route, state, timeout, tunit, this);
            AbstractConnPool.this.onLease(entry);
            return entry;
         }
      };
   }

   public Future lease(Object route, Object state) {
      return this.lease(route, state, (FutureCallback)null);
   }

   private PoolEntry getPoolEntryBlocking(Object route, Object state, long timeout, TimeUnit tunit, PoolEntryFuture future) throws IOException, InterruptedException, TimeoutException {
      Date deadline = null;
      if(timeout > 0L) {
         deadline = new Date(System.currentTimeMillis() + tunit.toMillis(timeout));
      }

      this.lock.lock();

      try {
         RouteSpecificPool<T, C, E> pool = this.getPool(route);
         E entry = null;

         while(entry == null) {
            Asserts.check(!this.isShutDown, "Connection pool shut down");

            while(true) {
               entry = pool.getFree(state);
               if(entry == null || !entry.isClosed() && !entry.isExpired(System.currentTimeMillis())) {
                  if(entry != null) {
                     this.available.remove(entry);
                     this.leased.add(entry);
                     PoolEntry var26 = entry;
                     return var26;
                  }

                  int maxPerRoute = this.getMax(route);
                  int excess = Math.max(0, pool.getAllocatedCount() + 1 - maxPerRoute);
                  if(excess > 0) {
                     for(int i = 0; i < excess; ++i) {
                        E lastUsed = pool.getLastUsed();
                        if(lastUsed == null) {
                           break;
                        }

                        lastUsed.close();
                        this.available.remove(lastUsed);
                        pool.remove(lastUsed);
                     }
                  }

                  if(pool.getAllocatedCount() < maxPerRoute) {
                     int totalUsed = this.leased.size();
                     int freeCapacity = Math.max(this.maxTotal - totalUsed, 0);
                     if(freeCapacity > 0) {
                        int totalAvailable = this.available.size();
                        if(totalAvailable > freeCapacity - 1 && !this.available.isEmpty()) {
                           E lastUsed = (PoolEntry)this.available.removeLast();
                           lastUsed.close();
                           RouteSpecificPool<T, C, E> otherpool = this.getPool(lastUsed.getRoute());
                           otherpool.remove(lastUsed);
                        }

                        C conn = this.connFactory.create(route);
                        entry = pool.add(conn);
                        this.leased.add(entry);
                        PoolEntry var32 = entry;
                        return var32;
                     }
                  }

                  boolean success = false;

                  try {
                     pool.queue(future);
                     this.pending.add(future);
                     success = future.await(deadline);
                  } finally {
                     pool.unqueue(future);
                     this.pending.remove(future);
                  }

                  if(!success && deadline != null && deadline.getTime() <= System.currentTimeMillis()) {
                     throw new TimeoutException("Timeout waiting for connection");
                  }
                  break;
               }

               entry.close();
               this.available.remove(entry);
               pool.free(entry, false);
            }
         }

         throw new TimeoutException("Timeout waiting for connection");
      } finally {
         this.lock.unlock();
      }
   }

   public void release(PoolEntry entry, boolean reusable) {
      this.lock.lock();

      try {
         if(this.leased.remove(entry)) {
            RouteSpecificPool<T, C, E> pool = this.getPool(entry.getRoute());
            pool.free(entry, reusable);
            if(reusable && !this.isShutDown) {
               this.available.addFirst(entry);
               this.onRelease(entry);
            } else {
               entry.close();
            }

            PoolEntryFuture<E> future = pool.nextPending();
            if(future != null) {
               this.pending.remove(future);
            } else {
               future = (PoolEntryFuture)this.pending.poll();
            }

            if(future != null) {
               future.wakeup();
            }
         }
      } finally {
         this.lock.unlock();
      }

   }

   private int getMax(Object route) {
      Integer v = (Integer)this.maxPerRoute.get(route);
      return v != null?v.intValue():this.defaultMaxPerRoute;
   }

   public void setMaxTotal(int max) {
      Args.notNegative(max, "Max value");
      this.lock.lock();

      try {
         this.maxTotal = max;
      } finally {
         this.lock.unlock();
      }

   }

   public int getMaxTotal() {
      this.lock.lock();

      int var1;
      try {
         var1 = this.maxTotal;
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   public void setDefaultMaxPerRoute(int max) {
      Args.notNegative(max, "Max per route value");
      this.lock.lock();

      try {
         this.defaultMaxPerRoute = max;
      } finally {
         this.lock.unlock();
      }

   }

   public int getDefaultMaxPerRoute() {
      this.lock.lock();

      int var1;
      try {
         var1 = this.defaultMaxPerRoute;
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   public void setMaxPerRoute(Object route, int max) {
      Args.notNull(route, "Route");
      Args.notNegative(max, "Max per route value");
      this.lock.lock();

      try {
         this.maxPerRoute.put(route, Integer.valueOf(max));
      } finally {
         this.lock.unlock();
      }

   }

   public int getMaxPerRoute(Object route) {
      Args.notNull(route, "Route");
      this.lock.lock();

      int var2;
      try {
         var2 = this.getMax(route);
      } finally {
         this.lock.unlock();
      }

      return var2;
   }

   public PoolStats getTotalStats() {
      this.lock.lock();

      PoolStats var1;
      try {
         var1 = new PoolStats(this.leased.size(), this.pending.size(), this.available.size(), this.maxTotal);
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   public PoolStats getStats(Object route) {
      Args.notNull(route, "Route");
      this.lock.lock();

      PoolStats var3;
      try {
         RouteSpecificPool<T, C, E> pool = this.getPool(route);
         var3 = new PoolStats(pool.getLeasedCount(), pool.getPendingCount(), pool.getAvailableCount(), this.getMax(route));
      } finally {
         this.lock.unlock();
      }

      return var3;
   }

   protected void enumAvailable(PoolEntryCallback callback) {
      this.lock.lock();

      try {
         Iterator<E> it = this.available.iterator();

         while(it.hasNext()) {
            E entry = (PoolEntry)it.next();
            callback.process(entry);
            if(entry.isClosed()) {
               RouteSpecificPool<T, C, E> pool = this.getPool(entry.getRoute());
               pool.remove(entry);
               it.remove();
            }
         }

         this.purgePoolMap();
      } finally {
         this.lock.unlock();
      }

   }

   protected void enumLeased(PoolEntryCallback callback) {
      this.lock.lock();

      try {
         for(E entry : this.leased) {
            callback.process(entry);
         }
      } finally {
         this.lock.unlock();
      }

   }

   private void purgePoolMap() {
      Iterator<Entry<T, RouteSpecificPool<T, C, E>>> it = this.routeToPool.entrySet().iterator();

      while(it.hasNext()) {
         Entry<T, RouteSpecificPool<T, C, E>> entry = (Entry)it.next();
         RouteSpecificPool<T, C, E> pool = (RouteSpecificPool)entry.getValue();
         if(pool.getPendingCount() + pool.getAllocatedCount() == 0) {
            it.remove();
         }
      }

   }

   public void closeIdle(long idletime, TimeUnit tunit) {
      Args.notNull(tunit, "Time unit");
      long time = tunit.toMillis(idletime);
      if(time < 0L) {
         time = 0L;
      }

      final long deadline = System.currentTimeMillis() - time;
      this.enumAvailable(new PoolEntryCallback() {
         public void process(PoolEntry entry) {
            if(entry.getUpdated() <= deadline) {
               entry.close();
            }

         }
      });
   }

   public void closeExpired() {
      final long now = System.currentTimeMillis();
      this.enumAvailable(new PoolEntryCallback() {
         public void process(PoolEntry entry) {
            if(entry.isExpired(now)) {
               entry.close();
            }

         }
      });
   }

   public String toString() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("[leased: ");
      buffer.append(this.leased);
      buffer.append("][available: ");
      buffer.append(this.available);
      buffer.append("][pending: ");
      buffer.append(this.pending);
      buffer.append("]");
      return buffer.toString();
   }

   // $FF: synthetic method
   static PoolEntry access$000(AbstractConnPool x0, Object x1, Object x2, long x3, TimeUnit x4, PoolEntryFuture x5) throws IOException, InterruptedException, TimeoutException {
      return x0.getPoolEntryBlocking(x1, x2, x3, x4, x5);
   }
}
