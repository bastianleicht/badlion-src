package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpDateGenerator;
import org.apache.http.util.Args;

@ThreadSafe
public class ResponseDate implements HttpResponseInterceptor {
   private static final HttpDateGenerator DATE_GENERATOR = new HttpDateGenerator();

   public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
      Args.notNull(response, "HTTP response");
      int status = response.getStatusLine().getStatusCode();
      if(status >= 200 && !response.containsHeader("Date")) {
         String httpdate = DATE_GENERATOR.getCurrentDate();
         response.setHeader("Date", httpdate);
      }

   }
}
