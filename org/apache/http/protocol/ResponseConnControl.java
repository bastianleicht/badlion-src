package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.annotation.Immutable;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.Args;

@Immutable
public class ResponseConnControl implements HttpResponseInterceptor {
   public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
      Args.notNull(response, "HTTP response");
      HttpCoreContext corecontext = HttpCoreContext.adapt(context);
      int status = response.getStatusLine().getStatusCode();
      if(status != 400 && status != 408 && status != 411 && status != 413 && status != 414 && status != 503 && status != 501) {
         Header explicit = response.getFirstHeader("Connection");
         if(explicit == null || !"Close".equalsIgnoreCase(explicit.getValue())) {
            HttpEntity entity = response.getEntity();
            if(entity != null) {
               ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
               if(entity.getContentLength() < 0L && (!entity.isChunked() || ver.lessEquals(HttpVersion.HTTP_1_0))) {
                  response.setHeader("Connection", "Close");
                  return;
               }
            }

            HttpRequest request = corecontext.getRequest();
            if(request != null) {
               Header header = request.getFirstHeader("Connection");
               if(header != null) {
                  response.setHeader("Connection", header.getValue());
               } else if(request.getProtocolVersion().lessEquals(HttpVersion.HTTP_1_0)) {
                  response.setHeader("Connection", "Close");
               }
            }

         }
      } else {
         response.setHeader("Connection", "Close");
      }
   }
}
