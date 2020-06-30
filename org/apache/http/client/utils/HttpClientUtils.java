package org.apache.http.client.utils;

import java.io.Closeable;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

public class HttpClientUtils {
   public static void closeQuietly(HttpResponse response) {
      if(response != null) {
         HttpEntity entity = response.getEntity();
         if(entity != null) {
            try {
               EntityUtils.consume(entity);
            } catch (IOException var3) {
               ;
            }
         }
      }

   }

   public static void closeQuietly(CloseableHttpResponse response) {
      if(response != null) {
         try {
            try {
               EntityUtils.consume(response.getEntity());
            } finally {
               response.close();
            }
         } catch (IOException var5) {
            ;
         }
      }

   }

   public static void closeQuietly(HttpClient httpClient) {
      if(httpClient != null && httpClient instanceof Closeable) {
         try {
            ((Closeable)httpClient).close();
         } catch (IOException var2) {
            ;
         }
      }

   }
}
