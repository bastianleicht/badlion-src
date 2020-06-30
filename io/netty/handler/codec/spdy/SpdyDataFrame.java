package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.spdy.SpdyStreamFrame;

public interface SpdyDataFrame extends ByteBufHolder, SpdyStreamFrame {
   SpdyDataFrame setStreamId(int var1);

   SpdyDataFrame setLast(boolean var1);

   ByteBuf content();

   SpdyDataFrame copy();

   SpdyDataFrame duplicate();

   SpdyDataFrame retain();

   SpdyDataFrame retain(int var1);
}
