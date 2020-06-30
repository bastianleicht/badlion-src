package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyFrame;

public interface SpdyStreamFrame extends SpdyFrame {
   int streamId();

   SpdyStreamFrame setStreamId(int var1);

   boolean isLast();

   SpdyStreamFrame setLast(boolean var1);
}
