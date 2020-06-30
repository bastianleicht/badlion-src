package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

public interface HttpRequest extends HttpMessage {
   HttpMethod getMethod();

   HttpRequest setMethod(HttpMethod var1);

   String getUri();

   HttpRequest setUri(String var1);

   HttpRequest setProtocolVersion(HttpVersion var1);
}
