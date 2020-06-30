package com.mojang.authlib;

import com.mojang.authlib.BaseAuthenticationService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class HttpAuthenticationService extends BaseAuthenticationService {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Proxy proxy;

   protected HttpAuthenticationService(Proxy proxy) {
      Validate.notNull(proxy);
      this.proxy = proxy;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   protected HttpURLConnection createUrlConnection(URL url) throws IOException {
      Validate.notNull(url);
      LOGGER.debug("Opening connection to " + url);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection(this.proxy);
      connection.setConnectTimeout(15000);
      connection.setReadTimeout(15000);
      connection.setUseCaches(false);
      return connection;
   }

   public String performPostRequest(URL url, String post, String contentType) throws IOException {
      Validate.notNull(url);
      Validate.notNull(post);
      Validate.notNull(contentType);
      HttpURLConnection connection = this.createUrlConnection(url);
      byte[] postAsBytes = post.getBytes(Charsets.UTF_8);
      connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");
      connection.setRequestProperty("Content-Length", "" + postAsBytes.length);
      connection.setDoOutput(true);
      LOGGER.debug("Writing POST data to " + url + ": " + post);
      OutputStream outputStream = null;

      try {
         outputStream = connection.getOutputStream();
         IOUtils.write(postAsBytes, outputStream);
      } finally {
         IOUtils.closeQuietly(outputStream);
      }

      LOGGER.debug("Reading data from " + url);
      InputStream inputStream = null;

      String var10;
      try {
         inputStream = connection.getInputStream();
         String result = IOUtils.toString(inputStream, Charsets.UTF_8);
         LOGGER.debug("Successful read, server response was " + connection.getResponseCode());
         LOGGER.debug("Response: " + result);
         String var21 = result;
         return var21;
      } catch (IOException var19) {
         IOUtils.closeQuietly(inputStream);
         inputStream = connection.getErrorStream();
         if(inputStream == null) {
            LOGGER.debug((String)"Request failed", (Throwable)var19);
            throw var19;
         }

         LOGGER.debug("Reading error page from " + url);
         String result = IOUtils.toString(inputStream, Charsets.UTF_8);
         LOGGER.debug("Successful read, server response was " + connection.getResponseCode());
         LOGGER.debug("Response: " + result);
         var10 = result;
      } finally {
         IOUtils.closeQuietly(inputStream);
      }

      return var10;
   }

   public String performGetRequest(URL url) throws IOException {
      Validate.notNull(url);
      HttpURLConnection connection = this.createUrlConnection(url);
      LOGGER.debug("Reading data from " + url);
      InputStream inputStream = null;

      String var6;
      try {
         inputStream = connection.getInputStream();
         String result = IOUtils.toString(inputStream, Charsets.UTF_8);
         LOGGER.debug("Successful read, server response was " + connection.getResponseCode());
         LOGGER.debug("Response: " + result);
         String var12 = result;
         return var12;
      } catch (IOException var10) {
         IOUtils.closeQuietly(inputStream);
         inputStream = connection.getErrorStream();
         if(inputStream == null) {
            LOGGER.debug((String)"Request failed", (Throwable)var10);
            throw var10;
         }

         LOGGER.debug("Reading error page from " + url);
         String result = IOUtils.toString(inputStream, Charsets.UTF_8);
         LOGGER.debug("Successful read, server response was " + connection.getResponseCode());
         LOGGER.debug("Response: " + result);
         var6 = result;
      } finally {
         IOUtils.closeQuietly(inputStream);
      }

      return var6;
   }

   public static URL constantURL(String url) {
      try {
         return new URL(url);
      } catch (MalformedURLException var2) {
         throw new Error("Couldn\'t create constant for " + url, var2);
      }
   }

   public static String buildQuery(Map query) {
      if(query == null) {
         return "";
      } else {
         StringBuilder builder = new StringBuilder();

         for(Entry<String, Object> entry : query.entrySet()) {
            if(builder.length() > 0) {
               builder.append('&');
            }

            try {
               builder.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
            } catch (UnsupportedEncodingException var6) {
               LOGGER.error((String)"Unexpected exception building query", (Throwable)var6);
            }

            if(entry.getValue() != null) {
               builder.append('=');

               try {
                  builder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
               } catch (UnsupportedEncodingException var5) {
                  LOGGER.error((String)"Unexpected exception building query", (Throwable)var5);
               }
            }
         }

         return builder.toString();
      }
   }

   public static URL concatenateURL(URL url, String query) {
      try {
         return url.getQuery() != null && url.getQuery().length() > 0?new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "&" + query):new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "?" + query);
      } catch (MalformedURLException var3) {
         throw new IllegalArgumentException("Could not concatenate given URL with GET arguments!", var3);
      }
   }
}
