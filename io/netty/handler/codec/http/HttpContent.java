package io.netty.handler.codec.http;

import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.http.HttpObject;

public interface HttpContent extends HttpObject, ByteBufHolder {
   HttpContent copy();

   HttpContent duplicate();

   HttpContent retain();

   HttpContent retain(int var1);
}
