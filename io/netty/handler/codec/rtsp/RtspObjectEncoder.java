package io.netty.handler.codec.rtsp;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpObjectEncoder;

@ChannelHandler.Sharable
public abstract class RtspObjectEncoder extends HttpObjectEncoder {
   public boolean acceptOutboundMessage(Object msg) throws Exception {
      return msg instanceof FullHttpMessage;
   }
}
