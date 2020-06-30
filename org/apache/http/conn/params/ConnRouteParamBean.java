package org.apache.http.conn.params;

import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.HttpAbstractParamBean;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
@NotThreadSafe
public class ConnRouteParamBean extends HttpAbstractParamBean {
   public ConnRouteParamBean(HttpParams params) {
      super(params);
   }

   public void setDefaultProxy(HttpHost defaultProxy) {
      this.params.setParameter("http.route.default-proxy", defaultProxy);
   }

   public void setLocalAddress(InetAddress address) {
      this.params.setParameter("http.route.local-address", address);
   }

   public void setForcedRoute(HttpRoute route) {
      this.params.setParameter("http.route.forced-route", route);
   }
}
