package org.apache.http.protocol;

public interface HttpContext {
   String RESERVED_PREFIX = "http.";

   Object getAttribute(String var1);

   void setAttribute(String var1, Object var2);

   Object removeAttribute(String var1);
}
