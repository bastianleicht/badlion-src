package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.RouteInfo;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Immutable
public class RequestClientConnControl implements HttpRequestInterceptor {
   private final Log log = LogFactory.getLog(this.getClass());
   private static final String PROXY_CONN_DIRECTIVE = "Proxy-Connection";

   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
      Args.notNull(request, "HTTP request");
      String method = request.getRequestLine().getMethod();
      if(method.equalsIgnoreCase("CONNECT")) {
         request.setHeader("Proxy-Connection", "Keep-Alive");
      } else {
         HttpClientContext clientContext = HttpClientContext.adapt(context);
         RouteInfo route = clientContext.getHttpRoute();
         if(route == null) {
            this.log.debug("Connection route not set in the context");
         } else {
            if((route.getHopCount() == 1 || route.isTunnelled()) && !request.containsHeader("Connection")) {
               request.addHeader("Connection", "Keep-Alive");
            }

            if(route.getHopCount() == 2 && !route.isTunnelled() && !request.containsHeader("Proxy-Connection")) {
               request.addHeader("Proxy-Connection", "Keep-Alive");
            }

         }
      }
   }
}
