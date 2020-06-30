package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public interface HttpResponse extends HttpMessage {
   HttpResponseStatus getStatus();

   HttpResponse setStatus(HttpResponseStatus var1);

   HttpResponse setProtocolVersion(HttpVersion var1);
}
