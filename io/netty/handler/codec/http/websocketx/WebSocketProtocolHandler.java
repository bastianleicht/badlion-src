package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.util.List;

abstract class WebSocketProtocolHandler extends MessageToMessageDecoder {
   protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List out) throws Exception {
      if(frame instanceof PingWebSocketFrame) {
         frame.content().retain();
         ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content()));
      } else if(!(frame instanceof PongWebSocketFrame)) {
         out.add(frame.retain());
      }
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      ctx.close();
   }
}
