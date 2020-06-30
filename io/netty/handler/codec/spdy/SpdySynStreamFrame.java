package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyHeadersFrame;

public interface SpdySynStreamFrame extends SpdyHeadersFrame {
   int associatedStreamId();

   SpdySynStreamFrame setAssociatedStreamId(int var1);

   byte priority();

   SpdySynStreamFrame setPriority(byte var1);

   boolean isUnidirectional();

   SpdySynStreamFrame setUnidirectional(boolean var1);

   SpdySynStreamFrame setStreamId(int var1);

   SpdySynStreamFrame setLast(boolean var1);

   SpdySynStreamFrame setInvalid();
}
