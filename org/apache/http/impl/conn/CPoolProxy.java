package org.apache.http.impl.conn;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpConnection;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.impl.conn.CPoolEntry;
import org.apache.http.impl.conn.ConnectionShutdownException;
import org.apache.http.protocol.HttpContext;

@NotThreadSafe
class CPoolProxy implements InvocationHandler {
   private static final Method CLOSE_METHOD;
   private static final Method SHUTDOWN_METHOD;
   private static final Method IS_OPEN_METHOD;
   private static final Method IS_STALE_METHOD;
   private volatile CPoolEntry poolEntry;

   CPoolProxy(CPoolEntry entry) {
      this.poolEntry = entry;
   }

   CPoolEntry getPoolEntry() {
      return this.poolEntry;
   }

   CPoolEntry detach() {
      CPoolEntry local = this.poolEntry;
      this.poolEntry = null;
      return local;
   }

   HttpClientConnection getConnection() {
      CPoolEntry local = this.poolEntry;
      return local == null?null:(HttpClientConnection)local.getConnection();
   }

   public void close() throws IOException {
      CPoolEntry local = this.poolEntry;
      if(local != null) {
         local.closeConnection();
      }

   }

   public void shutdown() throws IOException {
      CPoolEntry local = this.poolEntry;
      if(local != null) {
         local.shutdownConnection();
      }

   }

   public boolean isOpen() {
      CPoolEntry local = this.poolEntry;
      return local != null?!local.isClosed():false;
   }

   public boolean isStale() {
      HttpClientConnection conn = this.getConnection();
      return conn != null?conn.isStale():true;
   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if(method.equals(CLOSE_METHOD)) {
         this.close();
         return null;
      } else if(method.equals(SHUTDOWN_METHOD)) {
         this.shutdown();
         return null;
      } else if(method.equals(IS_OPEN_METHOD)) {
         return Boolean.valueOf(this.isOpen());
      } else if(method.equals(IS_STALE_METHOD)) {
         return Boolean.valueOf(this.isStale());
      } else {
         HttpClientConnection conn = this.getConnection();
         if(conn == null) {
            throw new ConnectionShutdownException();
         } else {
            try {
               return method.invoke(conn, args);
            } catch (InvocationTargetException var7) {
               Throwable cause = var7.getCause();
               if(cause != null) {
                  throw cause;
               } else {
                  throw var7;
               }
            }
         }
      }
   }

   public static HttpClientConnection newProxy(CPoolEntry poolEntry) {
      return (HttpClientConnection)Proxy.newProxyInstance(CPoolProxy.class.getClassLoader(), new Class[]{ManagedHttpClientConnection.class, HttpContext.class}, new CPoolProxy(poolEntry));
   }

   private static CPoolProxy getHandler(HttpClientConnection proxy) {
      InvocationHandler handler = Proxy.getInvocationHandler(proxy);
      if(!CPoolProxy.class.isInstance(handler)) {
         throw new IllegalStateException("Unexpected proxy handler class: " + handler);
      } else {
         return (CPoolProxy)CPoolProxy.class.cast(handler);
      }
   }

   public static CPoolEntry getPoolEntry(HttpClientConnection proxy) {
      CPoolEntry entry = getHandler(proxy).getPoolEntry();
      if(entry == null) {
         throw new ConnectionShutdownException();
      } else {
         return entry;
      }
   }

   public static CPoolEntry detach(HttpClientConnection proxy) {
      return getHandler(proxy).detach();
   }

   static {
      try {
         CLOSE_METHOD = HttpConnection.class.getMethod("close", new Class[0]);
         SHUTDOWN_METHOD = HttpConnection.class.getMethod("shutdown", new Class[0]);
         IS_OPEN_METHOD = HttpConnection.class.getMethod("isOpen", new Class[0]);
         IS_STALE_METHOD = HttpConnection.class.getMethod("isStale", new Class[0]);
      } catch (NoSuchMethodException var1) {
         throw new Error(var1);
      }
   }
}
