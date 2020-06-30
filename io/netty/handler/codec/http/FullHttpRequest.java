package io.netty.handler.codec.http;

import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

public interface FullHttpRequest extends HttpRequest, FullHttpMessage {
   FullHttpRequest copy();

   FullHttpRequest retain(int var1);

   FullHttpRequest retain();

   FullHttpRequest setProtocolVersion(HttpVersion var1);

   FullHttpRequest setMethod(HttpMethod var1);

   FullHttpRequest setUri(String var1);
}
