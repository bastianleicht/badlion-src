package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyStreamFrame;
import io.netty.handler.codec.spdy.SpdyStreamStatus;

public interface SpdyRstStreamFrame extends SpdyStreamFrame {
   SpdyStreamStatus status();

   SpdyRstStreamFrame setStatus(SpdyStreamStatus var1);

   SpdyRstStreamFrame setStreamId(int var1);

   SpdyRstStreamFrame setLast(boolean var1);
}
