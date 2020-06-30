package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.annotation.Immutable;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Immutable
public class RequestExpectContinue implements HttpRequestInterceptor {
   private final boolean activeByDefault;

   /** @deprecated */
   @Deprecated
   public RequestExpectContinue() {
      this(false);
   }

   public RequestExpectContinue(boolean activeByDefault) {
      this.activeByDefault = activeByDefault;
   }

   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
      Args.notNull(request, "HTTP request");
      if(!request.containsHeader("Expect") && request instanceof HttpEntityEnclosingRequest) {
         ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
         HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
         if(entity != null && entity.getContentLength() != 0L && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
            boolean active = request.getParams().getBooleanParameter("http.protocol.expect-continue", this.activeByDefault);
            if(active) {
               request.addHeader("Expect", "100-continue");
            }
         }
      }

   }
}
