package org.apache.http.protocol;

import org.apache.http.protocol.HttpRequestHandler;

/** @deprecated */
@Deprecated
public interface HttpRequestHandlerResolver {
   HttpRequestHandler lookup(String var1);
}
