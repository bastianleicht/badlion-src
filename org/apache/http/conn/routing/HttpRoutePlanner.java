package org.apache.http.conn.routing;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;

public interface HttpRoutePlanner {
   HttpRoute determineRoute(HttpHost var1, HttpRequest var2, HttpContext var3) throws HttpException;
}
