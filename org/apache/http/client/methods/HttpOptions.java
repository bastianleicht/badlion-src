package org.apache.http.client.methods;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.Args;

@NotThreadSafe
public class HttpOptions extends HttpRequestBase {
   public static final String METHOD_NAME = "OPTIONS";

   public HttpOptions() {
   }

   public HttpOptions(URI uri) {
      this.setURI(uri);
   }

   public HttpOptions(String uri) {
      this.setURI(URI.create(uri));
   }

   public String getMethod() {
      return "OPTIONS";
   }

   public Set getAllowedMethods(HttpResponse response) {
      Args.notNull(response, "HTTP response");
      HeaderIterator it = response.headerIterator("Allow");
      Set<String> methods = new HashSet();

      while(it.hasNext()) {
         Header header = it.nextHeader();
         HeaderElement[] elements = header.getElements();

         for(HeaderElement element : elements) {
            methods.add(element.getName());
         }
      }

      return methods;
   }
}
