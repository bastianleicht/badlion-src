package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyFrame;

public interface SpdyPingFrame extends SpdyFrame {
   int id();

   SpdyPingFrame setId(int var1);
}
