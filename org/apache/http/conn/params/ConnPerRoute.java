package org.apache.http.conn.params;

import org.apache.http.conn.routing.HttpRoute;

/** @deprecated */
@Deprecated
public interface ConnPerRoute {
   int getMaxForRoute(HttpRoute var1);
}
