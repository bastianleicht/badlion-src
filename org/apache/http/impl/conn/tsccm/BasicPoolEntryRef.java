package org.apache.http.impl.conn.tsccm;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.tsccm.BasicPoolEntry;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
public class BasicPoolEntryRef extends WeakReference {
   private final HttpRoute route;

   public BasicPoolEntryRef(BasicPoolEntry entry, ReferenceQueue queue) {
      super(entry, queue);
      Args.notNull(entry, "Pool entry");
      this.route = entry.getPlannedRoute();
   }

   public final HttpRoute getRoute() {
      return this.route;
   }
}
