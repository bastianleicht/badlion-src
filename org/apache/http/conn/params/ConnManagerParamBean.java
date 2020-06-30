package org.apache.http.conn.params;

import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.params.HttpAbstractParamBean;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
@NotThreadSafe
public class ConnManagerParamBean extends HttpAbstractParamBean {
   public ConnManagerParamBean(HttpParams params) {
      super(params);
   }

   public void setTimeout(long timeout) {
      this.params.setLongParameter("http.conn-manager.timeout", timeout);
   }

   public void setMaxTotalConnections(int maxConnections) {
      this.params.setIntParameter("http.conn-manager.max-total", maxConnections);
   }

   public void setConnectionsPerRoute(ConnPerRouteBean connPerRoute) {
      this.params.setParameter("http.conn-manager.max-per-route", connPerRoute);
   }
}
