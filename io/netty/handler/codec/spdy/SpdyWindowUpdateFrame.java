package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyFrame;

public interface SpdyWindowUpdateFrame extends SpdyFrame {
   int streamId();

   SpdyWindowUpdateFrame setStreamId(int var1);

   int deltaWindowSize();

   SpdyWindowUpdateFrame setDeltaWindowSize(int var1);
}
