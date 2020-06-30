package org.apache.http.client.methods;

import java.net.URI;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.methods.HttpRequestBase;

@NotThreadSafe
public class HttpHead extends HttpRequestBase {
   public static final String METHOD_NAME = "HEAD";

   public HttpHead() {
   }

   public HttpHead(URI uri) {
      this.setURI(uri);
   }

   public HttpHead(String uri) {
      this.setURI(URI.create(uri));
   }

   public String getMethod() {
      return "HEAD";
   }
}
