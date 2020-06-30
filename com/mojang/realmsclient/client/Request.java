package com.mojang.realmsclient.client;

import com.mojang.realmsclient.client.RealmsClientConfig;
import com.mojang.realmsclient.exception.RealmsHttpException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

public abstract class Request {
   protected HttpURLConnection connection;
   private boolean connected;
   protected String url;
   private static final int DEFAULT_READ_TIMEOUT = 60000;
   private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

   public Request(String url, int connectTimeout, int readTimeout) {
      try {
         this.url = url;
         Proxy proxy = RealmsClientConfig.getProxy();
         if(proxy != null) {
            this.connection = (HttpURLConnection)(new URL(url)).openConnection(proxy);
         } else {
            this.connection = (HttpURLConnection)(new URL(url)).openConnection();
         }

         this.connection.setConnectTimeout(connectTimeout);
         this.connection.setReadTimeout(readTimeout);
      } catch (MalformedURLException var5) {
         throw new RealmsHttpException(var5.getMessage(), var5);
      } catch (IOException var6) {
         throw new RealmsHttpException(var6.getMessage(), var6);
      }
   }

   public void cookie(String key, String value) {
      cookie(this.connection, key, value);
   }

   public static void cookie(HttpURLConnection connection, String key, String value) {
      String cookie = connection.getRequestProperty("Cookie");
      if(cookie == null) {
         connection.setRequestProperty("Cookie", key + "=" + value);
      } else {
         connection.setRequestProperty("Cookie", cookie + ";" + key + "=" + value);
      }

   }

   public Request header(String name, String value) {
      this.connection.addRequestProperty(name, value);
      return this;
   }

   public int getRetryAfterHeader() {
      return getRetryAfterHeader(this.connection);
   }

   public static int getRetryAfterHeader(HttpURLConnection connection) {
      String pauseTime = connection.getHeaderField("Retry-After");

      try {
         return Integer.valueOf(pauseTime).intValue();
      } catch (Exception var3) {
         return 5;
      }
   }

   public int responseCode() {
      try {
         this.connect();
         return this.connection.getResponseCode();
      } catch (Exception var2) {
         throw new RealmsHttpException(var2.getMessage(), var2);
      }
   }

   public String text() {
      try {
         this.connect();
         String result = null;
         if(this.responseCode() >= 400) {
            result = this.read(this.connection.getErrorStream());
         } else {
            result = this.read(this.connection.getInputStream());
         }

         this.dispose();
         return result;
      } catch (IOException var2) {
         throw new RealmsHttpException(var2.getMessage(), var2);
      }
   }

   private String read(InputStream in) throws IOException {
      if(in == null) {
         return "";
      } else {
         InputStreamReader streamReader = new InputStreamReader(in, "UTF-8");
         StringBuilder sb = new StringBuilder();

         for(int x = streamReader.read(); x != -1; x = streamReader.read()) {
            sb.append((char)x);
         }

         return sb.toString();
      }
   }

   private void dispose() {
      byte[] bytes = new byte[1024];

      try {
         int count = 0;
         InputStream in = this.connection.getInputStream();

         while(in.read(bytes) > 0) {
            ;
         }

         in.close();
         return;
      } catch (Exception var10) {
         try {
            InputStream errorStream = this.connection.getErrorStream();
            int ret = 0;
            if(errorStream != null) {
               while(errorStream.read(bytes) > 0) {
                  ;
               }

               errorStream.close();
               return;
            }
         } catch (IOException var9) {
            return;
         }
      } finally {
         if(this.connection != null) {
            this.connection.disconnect();
         }

      }

   }

   protected Request connect() {
      if(!this.connected) {
         T t = this.doConnect();
         this.connected = true;
         return t;
      } else {
         return this;
      }
   }

   protected abstract Request doConnect();

   public static Request get(String url) {
      return new Request.Get(url, 5000, '\uea60');
   }

   public static Request get(String url, int connectTimeoutMillis, int readTimeoutMillis) {
      return new Request.Get(url, connectTimeoutMillis, readTimeoutMillis);
   }

   public static Request post(String uri, String content) {
      return new Request.Post(uri, content.getBytes(), 5000, '\uea60');
   }

   public static Request post(String uri, String content, int connectTimeoutMillis, int readTimeoutMillis) {
      return new Request.Post(uri, content.getBytes(), connectTimeoutMillis, readTimeoutMillis);
   }

   public static Request delete(String url) {
      return new Request.Delete(url, 5000, '\uea60');
   }

   public static Request put(String url, String content) {
      return new Request.Put(url, content.getBytes(), 5000, '\uea60');
   }

   public static Request put(String url, String content, int connectTimeoutMillis, int readTimeoutMillis) {
      return new Request.Put(url, content.getBytes(), connectTimeoutMillis, readTimeoutMillis);
   }

   public String getHeader(String header) {
      return getHeader(this.connection, header);
   }

   public static String getHeader(HttpURLConnection connection, String header) {
      try {
         return connection.getHeaderField(header);
      } catch (Exception var3) {
         return "";
      }
   }

   public static class Delete extends Request {
      public Delete(String uri, int connectTimeout, int readTimeout) {
         super(uri, connectTimeout, readTimeout);
      }

      public Request.Delete doConnect() {
         try {
            this.connection.setDoOutput(true);
            this.connection.setRequestMethod("DELETE");
            this.connection.connect();
            return this;
         } catch (Exception var2) {
            throw new RealmsHttpException(var2.getMessage(), var2);
         }
      }
   }

   public static class Get extends Request {
      public Get(String uri, int connectTimeout, int readTimeout) {
         super(uri, connectTimeout, readTimeout);
      }

      public Request.Get doConnect() {
         try {
            this.connection.setDoInput(true);
            this.connection.setDoOutput(true);
            this.connection.setUseCaches(false);
            this.connection.setRequestMethod("GET");
            return this;
         } catch (Exception var2) {
            throw new RealmsHttpException(var2.getMessage(), var2);
         }
      }
   }

   public static class Post extends Request {
      private byte[] content;

      public Post(String uri, byte[] content, int connectTimeout, int readTimeout) {
         super(uri, connectTimeout, readTimeout);
         this.content = content;
      }

      public Request.Post doConnect() {
         try {
            if(this.content != null) {
               this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            this.connection.setDoInput(true);
            this.connection.setDoOutput(true);
            this.connection.setUseCaches(false);
            this.connection.setRequestMethod("POST");
            OutputStream out = this.connection.getOutputStream();
            out.write(this.content);
            out.flush();
            return this;
         } catch (Exception var2) {
            throw new RealmsHttpException(var2.getMessage(), var2);
         }
      }
   }

   public static class Put extends Request {
      private byte[] content;

      public Put(String uri, byte[] content, int connectTimeout, int readTimeout) {
         super(uri, connectTimeout, readTimeout);
         this.content = content;
      }

      public Request.Put doConnect() {
         try {
            if(this.content != null) {
               this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            this.connection.setDoOutput(true);
            this.connection.setDoInput(true);
            this.connection.setRequestMethod("PUT");
            OutputStream os = this.connection.getOutputStream();
            os.write(this.content);
            os.flush();
            return this;
         } catch (Exception var2) {
            throw new RealmsHttpException(var2.getMessage(), var2);
         }
      }
   }
}
