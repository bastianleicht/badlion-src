package org.apache.http.impl.cookie;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.util.Args;

@NotThreadSafe
public abstract class AbstractCookieSpec implements CookieSpec {
   private final Map attribHandlerMap = new HashMap(10);

   public void registerAttribHandler(String name, CookieAttributeHandler handler) {
      Args.notNull(name, "Attribute name");
      Args.notNull(handler, "Attribute handler");
      this.attribHandlerMap.put(name, handler);
   }

   protected CookieAttributeHandler findAttribHandler(String name) {
      return (CookieAttributeHandler)this.attribHandlerMap.get(name);
   }

   protected CookieAttributeHandler getAttribHandler(String name) {
      CookieAttributeHandler handler = this.findAttribHandler(name);
      if(handler == null) {
         throw new IllegalStateException("Handler not registered for " + name + " attribute.");
      } else {
         return handler;
      }
   }

   protected Collection getAttribHandlers() {
      return this.attribHandlerMap.values();
   }
}
