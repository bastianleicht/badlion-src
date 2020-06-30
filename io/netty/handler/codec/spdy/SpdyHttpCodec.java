package io.netty.handler.codec.spdy;

import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.spdy.SpdyHttpDecoder;
import io.netty.handler.codec.spdy.SpdyHttpEncoder;
import io.netty.handler.codec.spdy.SpdyVersion;

public final class SpdyHttpCodec extends CombinedChannelDuplexHandler {
   public SpdyHttpCodec(SpdyVersion version, int maxContentLength) {
      super(new SpdyHttpDecoder(version, maxContentLength), new SpdyHttpEncoder(version));
   }

   public SpdyHttpCodec(SpdyVersion version, int maxContentLength, boolean validateHttpHeaders) {
      super(new SpdyHttpDecoder(version, maxContentLength, validateHttpHeaders), new SpdyHttpEncoder(version));
   }
}
