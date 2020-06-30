package io.netty.handler.codec.http;

import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public interface FullHttpResponse extends HttpResponse, FullHttpMessage {
   FullHttpResponse copy();

   FullHttpResponse retain(int var1);

   FullHttpResponse retain();

   FullHttpResponse setProtocolVersion(HttpVersion var1);

   FullHttpResponse setStatus(HttpResponseStatus var1);
}
