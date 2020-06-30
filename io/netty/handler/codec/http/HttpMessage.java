package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpVersion;

public interface HttpMessage extends HttpObject {
   HttpVersion getProtocolVersion();

   HttpMessage setProtocolVersion(HttpVersion var1);

   HttpHeaders headers();
}
