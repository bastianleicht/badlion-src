package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.annotation.Immutable;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Immutable
public class ResponseContent implements HttpResponseInterceptor {
   private final boolean overwrite;

   public ResponseContent() {
      this(false);
   }

   public ResponseContent(boolean overwrite) {
      this.overwrite = overwrite;
   }

   public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
      Args.notNull(response, "HTTP response");
      if(this.overwrite) {
         response.removeHeaders("Transfer-Encoding");
         response.removeHeaders("Content-Length");
      } else {
         if(response.containsHeader("Transfer-Encoding")) {
            throw new ProtocolException("Transfer-encoding header already present");
         }

         if(response.containsHeader("Content-Length")) {
            throw new ProtocolException("Content-Length header already present");
         }
      }

      ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
      HttpEntity entity = response.getEntity();
      if(entity != null) {
         long len = entity.getContentLength();
         if(entity.isChunked() && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
            response.addHeader("Transfer-Encoding", "chunked");
         } else if(len >= 0L) {
            response.addHeader("Content-Length", Long.toString(entity.getContentLength()));
         }

         if(entity.getContentType() != null && !response.containsHeader("Content-Type")) {
            response.addHeader(entity.getContentType());
         }

         if(entity.getContentEncoding() != null && !response.containsHeader("Content-Encoding")) {
            response.addHeader(entity.getContentEncoding());
         }
      } else {
         int status = response.getStatusLine().getStatusCode();
         if(status != 204 && status != 304 && status != 205) {
            response.addHeader("Content-Length", "0");
         }
      }

   }
}
