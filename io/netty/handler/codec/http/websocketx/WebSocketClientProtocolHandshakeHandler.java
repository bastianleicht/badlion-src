package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;

class WebSocketClientProtocolHandshakeHandler extends ChannelInboundHandlerAdapter {
   private final WebSocketClientHandshaker handshaker;

   WebSocketClientProtocolHandshakeHandler(WebSocketClientHandshaker handshaker) {
      this.handshaker = handshaker;
   }

   public void channelActive(final ChannelHandlerContext ctx) throws Exception {
      super.channelActive(ctx);
      this.handshaker.handshake(ctx.channel()).addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture future) throws Exception {
            if(!future.isSuccess()) {
               ctx.fireExceptionCaught(future.cause());
            } else {
               ctx.fireUserEventTriggered(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_ISSUED);
            }

         }
      });
   }

   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if(!(msg instanceof FullHttpResponse)) {
         ctx.fireChannelRead(msg);
      } else if(!this.handshaker.isHandshakeComplete()) {
         this.handshaker.finishHandshake(ctx.channel(), (FullHttpResponse)msg);
         ctx.fireUserEventTriggered(WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE);
         ctx.pipeline().remove((ChannelHandler)this);
      } else {
         throw new IllegalStateException("WebSocketClientHandshaker should have been non finished yet");
      }
   }
}
