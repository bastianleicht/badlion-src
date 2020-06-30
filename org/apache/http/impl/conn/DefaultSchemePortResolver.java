package org.apache.http.impl.conn;

import org.apache.http.HttpHost;
import org.apache.http.annotation.Immutable;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.util.Args;

@Immutable
public class DefaultSchemePortResolver implements SchemePortResolver {
   public static final DefaultSchemePortResolver INSTANCE = new DefaultSchemePortResolver();

   public int resolve(HttpHost host) throws UnsupportedSchemeException {
      Args.notNull(host, "HTTP host");
      int port = host.getPort();
      if(port > 0) {
         return port;
      } else {
         String name = host.getSchemeName();
         if(name.equalsIgnoreCase("http")) {
            return 80;
         } else if(name.equalsIgnoreCase("https")) {
            return 443;
         } else {
            throw new UnsupportedSchemeException(name + " protocol is not supported");
         }
      }
   }
}
