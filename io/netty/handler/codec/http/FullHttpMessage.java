package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.LastHttpContent;

public interface FullHttpMessage extends HttpMessage, LastHttpContent {
   FullHttpMessage copy();

   FullHttpMessage retain(int var1);

   FullHttpMessage retain();
}
