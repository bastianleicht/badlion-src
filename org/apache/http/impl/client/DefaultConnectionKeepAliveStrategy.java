package org.apache.http.impl.client;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Immutable;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Immutable
public class DefaultConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {
   public static final DefaultConnectionKeepAliveStrategy INSTANCE = new DefaultConnectionKeepAliveStrategy();

   public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
      Args.notNull(response, "HTTP response");
      HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));

      while(((HeaderElementIterator)it).hasNext()) {
         HeaderElement he = it.nextElement();
         String param = he.getName();
         String value = he.getValue();
         if(value != null && param.equalsIgnoreCase("timeout")) {
            try {
               return Long.parseLong(value) * 1000L;
            } catch (NumberFormatException var8) {
               ;
            }
         }
      }

      return -1L;
   }
}
