package org.apache.http.protocol;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpRequestHandler;

public interface HttpRequestHandlerMapper {
   HttpRequestHandler lookup(HttpRequest var1);
}
