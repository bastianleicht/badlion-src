package org.apache.http.client.methods;

import java.net.URI;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.methods.HttpRequestBase;

@NotThreadSafe
public class HttpTrace extends HttpRequestBase {
   public static final String METHOD_NAME = "TRACE";

   public HttpTrace() {
   }

   public HttpTrace(URI uri) {
      this.setURI(uri);
   }

   public HttpTrace(String uri) {
      this.setURI(URI.create(uri));
   }

   public String getMethod() {
      return "TRACE";
   }
}
