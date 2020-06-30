package org.apache.http.pool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.pool.PoolEntry;
import org.apache.http.pool.PoolEntryFuture;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@NotThreadSafe
abstract class RouteSpecificPool {
   private final Object route;
   private final Set leased;
   private final LinkedList available;
   private final LinkedList pending;

   RouteSpecificPool(Object route) {
      this.route = route;
      this.leased = new HashSet();
      this.available = new LinkedList();
      this.pending = new LinkedList();
   }

   protected abstract PoolEntry createEntry(Object var1);

   public final Object getRoute() {
      return this.route;
   }

   public int getLeasedCount() {
      return this.leased.size();
   }

   public int getPendingCount() {
      return this.pending.size();
   }

   public int getAvailableCount() {
      return this.available.size();
   }

   public int getAllocatedCount() {
      return this.available.size() + this.leased.size();
   }

   public PoolEntry getFree(Object state) {
      if(!this.available.isEmpty()) {
         if(state != null) {
            Iterator<E> it = this.available.iterator();

            while(it.hasNext()) {
               E entry = (PoolEntry)it.next();
               if(state.equals(entry.getState())) {
                  it.remove();
                  this.leased.add(entry);
                  return entry;
               }
            }
         }

         Iterator<E> it = this.available.iterator();

         while(it.hasNext()) {
            E entry = (PoolEntry)it.next();
            if(entry.getState() == null) {
               it.remove();
               this.leased.add(entry);
               return entry;
            }
         }
      }

      return null;
   }

   public PoolEntry getLastUsed() {
      return !this.available.isEmpty()?(PoolEntry)this.available.getLast():null;
   }

   public boolean remove(PoolEntry entry) {
      Args.notNull(entry, "Pool entry");
      return this.available.remove(entry) || this.leased.remove(entry);
   }

   public void free(PoolEntry entry, boolean reusable) {
      Args.notNull(entry, "Pool entry");
      boolean found = this.leased.remove(entry);
      Asserts.check(found, "Entry %s has not been leased from this pool", new Object[]{entry});
      if(reusable) {
         this.available.addFirst(entry);
      }

   }

   public PoolEntry add(Object conn) {
      E entry = this.createEntry(conn);
      this.leased.add(entry);
      return entry;
   }

   public void queue(PoolEntryFuture future) {
      if(future != null) {
         this.pending.add(future);
      }
   }

   public PoolEntryFuture nextPending() {
      return (PoolEntryFuture)this.pending.poll();
   }

   public void unqueue(PoolEntryFuture future) {
      if(future != null) {
         this.pending.remove(future);
      }
   }

   public void shutdown() {
      for(PoolEntryFuture<E> future : this.pending) {
         future.cancel(true);
      }

      this.pending.clear();

      for(E entry : this.available) {
         entry.close();
      }

      this.available.clear();

      for(E entry : this.leased) {
         entry.close();
      }

      this.leased.clear();
   }

   public String toString() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("[route: ");
      buffer.append(this.route);
      buffer.append("][leased: ");
      buffer.append(this.leased.size());
      buffer.append("][available: ");
      buffer.append(this.available.size());
      buffer.append("][pending: ");
      buffer.append(this.pending.size());
      buffer.append("]");
      return buffer.toString();
   }
}
