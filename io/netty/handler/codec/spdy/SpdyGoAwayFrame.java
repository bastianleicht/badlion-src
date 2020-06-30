package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyFrame;
import io.netty.handler.codec.spdy.SpdySessionStatus;

public interface SpdyGoAwayFrame extends SpdyFrame {
   int lastGoodStreamId();

   SpdyGoAwayFrame setLastGoodStreamId(int var1);

   SpdySessionStatus status();

   SpdyGoAwayFrame setStatus(SpdySessionStatus var1);
}
