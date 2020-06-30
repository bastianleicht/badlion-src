package org.apache.http.impl.client;

import java.util.HashMap;
import org.apache.http.HttpHost;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthScheme;
import org.apache.http.client.AuthCache;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.util.Args;

@NotThreadSafe
public class BasicAuthCache implements AuthCache {
   private final HashMap map;
   private final SchemePortResolver schemePortResolver;

   public BasicAuthCache(SchemePortResolver schemePortResolver) {
      this.map = new HashMap();
      this.schemePortResolver = (SchemePortResolver)(schemePortResolver != null?schemePortResolver:DefaultSchemePortResolver.INSTANCE);
   }

   public BasicAuthCache() {
      this((SchemePortResolver)null);
   }

   protected HttpHost getKey(HttpHost host) {
      if(host.getPort() <= 0) {
         int port;
         try {
            port = this.schemePortResolver.resolve(host);
         } catch (UnsupportedSchemeException var4) {
            return host;
         }

         return new HttpHost(host.getHostName(), port, host.getSchemeName());
      } else {
         return host;
      }
   }

   public void put(HttpHost host, AuthScheme authScheme) {
      Args.notNull(host, "HTTP host");
      this.map.put(this.getKey(host), authScheme);
   }

   public AuthScheme get(HttpHost host) {
      Args.notNull(host, "HTTP host");
      return (AuthScheme)this.map.get(this.getKey(host));
   }

   public void remove(HttpHost host) {
      Args.notNull(host, "HTTP host");
      this.map.remove(this.getKey(host));
   }

   public void clear() {
      this.map.clear();
   }

   public String toString() {
      return this.map.toString();
   }
}
