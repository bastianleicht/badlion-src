package org.apache.http.impl.conn.tsccm;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.tsccm.BasicPoolEntry;
import org.apache.http.impl.conn.tsccm.WaitingThread;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.LangUtils;

/** @deprecated */
@Deprecated
public class RouteSpecificPool {
   private final Log log = LogFactory.getLog(this.getClass());
   protected final HttpRoute route;
   protected final int maxEntries;
   protected final ConnPerRoute connPerRoute;
   protected final LinkedList freeEntries;
   protected final Queue waitingThreads;
   protected int numEntries;

   /** @deprecated */
   @Deprecated
   public RouteSpecificPool(HttpRoute route, int maxEntries) {
      this.route = route;
      this.maxEntries = maxEntries;
      this.connPerRoute = new ConnPerRoute() {
         public int getMaxForRoute(HttpRoute route) {
            return RouteSpecificPool.this.maxEntries;
         }
      };
      this.freeEntries = new LinkedList();
      this.waitingThreads = new LinkedList();
      this.numEntries = 0;
   }

   public RouteSpecificPool(HttpRoute route, ConnPerRoute connPerRoute) {
      this.route = route;
      this.connPerRoute = connPerRoute;
      this.maxEntries = connPerRoute.getMaxForRoute(route);
      this.freeEntries = new LinkedList();
      this.waitingThreads = new LinkedList();
      this.numEntries = 0;
   }

   public final HttpRoute getRoute() {
      return this.route;
   }

   public final int getMaxEntries() {
      return this.maxEntries;
   }

   public boolean isUnused() {
      return this.numEntries < 1 && this.waitingThreads.isEmpty();
   }

   public int getCapacity() {
      return this.connPerRoute.getMaxForRoute(this.route) - this.numEntries;
   }

   public final int getEntryCount() {
      return this.numEntries;
   }

   public BasicPoolEntry allocEntry(Object state) {
      if(!this.freeEntries.isEmpty()) {
         ListIterator<BasicPoolEntry> it = this.freeEntries.listIterator(this.freeEntries.size());

         while(it.hasPrevious()) {
            BasicPoolEntry entry = (BasicPoolEntry)it.previous();
            if(entry.getState() == null || LangUtils.equals(state, entry.getState())) {
               it.remove();
               return entry;
            }
         }
      }

      if(this.getCapacity() == 0 && !this.freeEntries.isEmpty()) {
         BasicPoolEntry entry = (BasicPoolEntry)this.freeEntries.remove();
         entry.shutdownEntry();
         OperatedClientConnection conn = entry.getConnection();

         try {
            conn.close();
         } catch (IOException var5) {
            this.log.debug("I/O error closing connection", var5);
         }

         return entry;
      } else {
         return null;
      }
   }

   public void freeEntry(BasicPoolEntry entry) {
      if(this.numEntries < 1) {
         throw new IllegalStateException("No entry created for this pool. " + this.route);
      } else if(this.numEntries <= this.freeEntries.size()) {
         throw new IllegalStateException("No entry allocated from this pool. " + this.route);
      } else {
         this.freeEntries.add(entry);
      }
   }

   public void createdEntry(BasicPoolEntry entry) {
      Args.check(this.route.equals(entry.getPlannedRoute()), "Entry not planned for this pool");
      ++this.numEntries;
   }

   public boolean deleteEntry(BasicPoolEntry entry) {
      boolean found = this.freeEntries.remove(entry);
      if(found) {
         --this.numEntries;
      }

      return found;
   }

   public void dropEntry() {
      Asserts.check(this.numEntries > 0, "There is no entry that could be dropped");
      --this.numEntries;
   }

   public void queueThread(WaitingThread wt) {
      Args.notNull(wt, "Waiting thread");
      this.waitingThreads.add(wt);
   }

   public boolean hasThread() {
      return !this.waitingThreads.isEmpty();
   }

   public WaitingThread nextThread() {
      return (WaitingThread)this.waitingThreads.peek();
   }

   public void removeThread(WaitingThread wt) {
      if(wt != null) {
         this.waitingThreads.remove(wt);
      }
   }
}
