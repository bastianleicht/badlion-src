package io.netty.handler.stream;

import io.netty.channel.ChannelHandlerContext;

public interface ChunkedInput {
   boolean isEndOfInput() throws Exception;

   void close() throws Exception;

   Object readChunk(ChannelHandlerContext var1) throws Exception;
}
