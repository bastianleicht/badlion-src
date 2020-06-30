package org.apache.http.protocol;

import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
public final class DefaultedHttpContext implements HttpContext {
   private final HttpContext local;
   private final HttpContext defaults;

   public DefaultedHttpContext(HttpContext local, HttpContext defaults) {
      this.local = (HttpContext)Args.notNull(local, "HTTP context");
      this.defaults = defaults;
   }

   public Object getAttribute(String id) {
      Object obj = this.local.getAttribute(id);
      return obj == null?this.defaults.getAttribute(id):obj;
   }

   public Object removeAttribute(String id) {
      return this.local.removeAttribute(id);
   }

   public void setAttribute(String id, Object obj) {
      this.local.setAttribute(id, obj);
   }

   public HttpContext getDefaults() {
      return this.defaults;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("[local: ").append(this.local);
      buf.append("defaults: ").append(this.defaults);
      buf.append("]");
      return buf.toString();
   }
}
