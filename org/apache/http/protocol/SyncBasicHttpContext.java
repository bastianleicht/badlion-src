package org.apache.http.protocol;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/** @deprecated */
@Deprecated
public class SyncBasicHttpContext extends BasicHttpContext {
   public SyncBasicHttpContext(HttpContext parentContext) {
      super(parentContext);
   }

   public SyncBasicHttpContext() {
   }

   public synchronized Object getAttribute(String id) {
      return super.getAttribute(id);
   }

   public synchronized void setAttribute(String id, Object obj) {
      super.setAttribute(id, obj);
   }

   public synchronized Object removeAttribute(String id) {
      return super.removeAttribute(id);
   }

   public synchronized void clear() {
      super.clear();
   }
}
