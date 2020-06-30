package org.apache.http.impl.client;

import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.RequestWrapper;

/** @deprecated */
@Deprecated
@NotThreadSafe
public class RoutedRequest {
   protected final RequestWrapper request;
   protected final HttpRoute route;

   public RoutedRequest(RequestWrapper req, HttpRoute route) {
      this.request = req;
      this.route = route;
   }

   public final RequestWrapper getRequest() {
      return this.request;
   }

   public final HttpRoute getRoute() {
      return this.route;
   }
}
