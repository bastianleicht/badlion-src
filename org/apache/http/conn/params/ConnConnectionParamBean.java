package org.apache.http.conn.params;

import org.apache.http.params.HttpAbstractParamBean;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
public class ConnConnectionParamBean extends HttpAbstractParamBean {
   public ConnConnectionParamBean(HttpParams params) {
      super(params);
   }

   /** @deprecated */
   @Deprecated
   public void setMaxStatusLineGarbage(int maxStatusLineGarbage) {
      this.params.setIntParameter("http.connection.max-status-line-garbage", maxStatusLineGarbage);
   }
}
