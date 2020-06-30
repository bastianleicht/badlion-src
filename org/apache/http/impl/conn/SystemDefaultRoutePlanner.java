package org.apache.http.impl.conn;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.Proxy.Type;
import java.util.List;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Immutable;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.protocol.HttpContext;

@Immutable
public class SystemDefaultRoutePlanner extends DefaultRoutePlanner {
   private final ProxySelector proxySelector;

   public SystemDefaultRoutePlanner(SchemePortResolver schemePortResolver, ProxySelector proxySelector) {
      super(schemePortResolver);
      this.proxySelector = proxySelector != null?proxySelector:ProxySelector.getDefault();
   }

   public SystemDefaultRoutePlanner(ProxySelector proxySelector) {
      this((SchemePortResolver)null, proxySelector);
   }

   protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
      URI targetURI;
      try {
         targetURI = new URI(target.toURI());
      } catch (URISyntaxException var9) {
         throw new HttpException("Cannot convert host to URI: " + target, var9);
      }

      List<Proxy> proxies = this.proxySelector.select(targetURI);
      Proxy p = this.chooseProxy(proxies);
      HttpHost result = null;
      if(p.type() == Type.HTTP) {
         if(!(p.address() instanceof InetSocketAddress)) {
            throw new HttpException("Unable to handle non-Inet proxy address: " + p.address());
         }

         InetSocketAddress isa = (InetSocketAddress)p.address();
         result = new HttpHost(this.getHost(isa), isa.getPort());
      }

      return result;
   }

   private String getHost(InetSocketAddress isa) {
      return isa.isUnresolved()?isa.getHostName():isa.getAddress().getHostAddress();
   }

   private Proxy chooseProxy(List param1) {
      // $FF: Couldn't be decompiled
   }
}
