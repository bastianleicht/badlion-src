package org.apache.http.conn.routing;

import org.apache.http.conn.routing.RouteInfo;

public interface HttpRouteDirector {
   int UNREACHABLE = -1;
   int COMPLETE = 0;
   int CONNECT_TARGET = 1;
   int CONNECT_PROXY = 2;
   int TUNNEL_TARGET = 3;
   int TUNNEL_PROXY = 4;
   int LAYER_PROTOCOL = 5;

   int nextStep(RouteInfo var1, RouteInfo var2);
}
