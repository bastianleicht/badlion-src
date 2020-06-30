package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyHeadersFrame;

public interface SpdySynReplyFrame extends SpdyHeadersFrame {
   SpdySynReplyFrame setStreamId(int var1);

   SpdySynReplyFrame setLast(boolean var1);

   SpdySynReplyFrame setInvalid();
}
