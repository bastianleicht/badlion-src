package org.apache.http.params;

import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
public abstract class HttpAbstractParamBean {
   protected final HttpParams params;

   public HttpAbstractParamBean(HttpParams params) {
      this.params = (HttpParams)Args.notNull(params, "HTTP parameters");
   }
}
