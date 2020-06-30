package org.apache.http.impl.conn;

import java.net.InetAddress;
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
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

/** @deprecated */
@Deprecated
@NotThreadSafe
public class ProxySelectorRoutePlanner implements HttpRoutePlanner {
   protected final SchemeRegistry schemeRegistry;
   protected ProxySelector proxySelector;

   public ProxySelectorRoutePlanner(SchemeRegistry schreg, ProxySelector prosel) {
      Args.notNull(schreg, "SchemeRegistry");
      this.schemeRegistry = schreg;
      this.proxySelector = prosel;
   }

   public ProxySelector getProxySelector() {
      return this.proxySelector;
   }

   public void setProxySelector(ProxySelector prosel) {
      this.proxySelector = prosel;
   }

   public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
      Args.notNull(request, "HTTP request");
      HttpRoute route = ConnRouteParams.getForcedRoute(request.getParams());
      if(route != null) {
         return route;
      } else {
         Asserts.notNull(target, "Target host");
         InetAddress local = ConnRouteParams.getLocalAddress(request.getParams());
         HttpHost proxy = this.determineProxy(target, request, context);
         Scheme schm = this.schemeRegistry.getScheme(target.getSchemeName());
         boolean secure = schm.isLayered();
         if(proxy == null) {
            route = new HttpRoute(target, local, secure);
         } else {
            route = new HttpRoute(target, local, proxy, secure);
         }

         return route;
      }
   }

   protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
      ProxySelector psel = this.proxySelector;
      if(psel == null) {
         psel = ProxySelector.getDefault();
      }

      if(psel == null) {
         return null;
      } else {
         URI targetURI = null;

         try {
            targetURI = new URI(target.toURI());
         } catch (URISyntaxException var10) {
            throw new HttpException("Cannot convert host to URI: " + target, var10);
         }

         List<Proxy> proxies = psel.select(targetURI);
         Proxy p = this.chooseProxy(proxies, target, request, context);
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
   }

   protected String getHost(InetSocketAddress isa) {
      return isa.isUnresolved()?isa.getHostName():isa.getAddress().getHostAddress();
   }

   protected Proxy chooseProxy(List param1, HttpHost param2, HttpRequest param3, HttpContext param4) {
      // $FF: Couldn't be decompiled
   }
}
