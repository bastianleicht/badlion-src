package org.apache.http.params;

import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
public final class HttpConnectionParams implements CoreConnectionPNames {
   public static int getSoTimeout(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      return params.getIntParameter("http.socket.timeout", 0);
   }

   public static void setSoTimeout(HttpParams params, int timeout) {
      Args.notNull(params, "HTTP parameters");
      params.setIntParameter("http.socket.timeout", timeout);
   }

   public static boolean getSoReuseaddr(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      return params.getBooleanParameter("http.socket.reuseaddr", false);
   }

   public static void setSoReuseaddr(HttpParams params, boolean reuseaddr) {
      Args.notNull(params, "HTTP parameters");
      params.setBooleanParameter("http.socket.reuseaddr", reuseaddr);
   }

   public static boolean getTcpNoDelay(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      return params.getBooleanParameter("http.tcp.nodelay", true);
   }

   public static void setTcpNoDelay(HttpParams params, boolean value) {
      Args.notNull(params, "HTTP parameters");
      params.setBooleanParameter("http.tcp.nodelay", value);
   }

   public static int getSocketBufferSize(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      return params.getIntParameter("http.socket.buffer-size", -1);
   }

   public static void setSocketBufferSize(HttpParams params, int size) {
      Args.notNull(params, "HTTP parameters");
      params.setIntParameter("http.socket.buffer-size", size);
   }

   public static int getLinger(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      return params.getIntParameter("http.socket.linger", -1);
   }

   public static void setLinger(HttpParams params, int value) {
      Args.notNull(params, "HTTP parameters");
      params.setIntParameter("http.socket.linger", value);
   }

   public static int getConnectionTimeout(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      return params.getIntParameter("http.connection.timeout", 0);
   }

   public static void setConnectionTimeout(HttpParams params, int timeout) {
      Args.notNull(params, "HTTP parameters");
      params.setIntParameter("http.connection.timeout", timeout);
   }

   public static boolean isStaleCheckingEnabled(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      return params.getBooleanParameter("http.connection.stalecheck", true);
   }

   public static void setStaleCheckingEnabled(HttpParams params, boolean value) {
      Args.notNull(params, "HTTP parameters");
      params.setBooleanParameter("http.connection.stalecheck", value);
   }

   public static boolean getSoKeepalive(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      return params.getBooleanParameter("http.socket.keepalive", false);
   }

   public static void setSoKeepalive(HttpParams params, boolean enableKeepalive) {
      Args.notNull(params, "HTTP parameters");
      params.setBooleanParameter("http.socket.keepalive", enableKeepalive);
   }
}
