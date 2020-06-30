package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandshakeHandler;
import io.netty.util.AttributeKey;
import java.util.List;

public class WebSocketServerProtocolHandler extends WebSocketProtocolHandler {
   private static final AttributeKey HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class.getName() + ".HANDSHAKER");
   private final String websocketPath;
   private final String subprotocols;
   private final boolean allowExtensions;
   private final int maxFramePayloadLength;

   public WebSocketServerProtocolHandler(String websocketPath) {
      this(websocketPath, (String)null, false);
   }

   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols) {
      this(websocketPath, subprotocols, false);
   }

   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions) {
      this(websocketPath, subprotocols, allowExtensions, 65536);
   }

   public WebSocketServerProtocolHandler(String websocketPath, String subprotocols, boolean allowExtensions, int maxFrameSize) {
      this.websocketPath = websocketPath;
      this.subprotocols = subprotocols;
      this.allowExtensions = allowExtensions;
      this.maxFramePayloadLength = maxFrameSize;
   }

   public void handlerAdded(ChannelHandlerContext ctx) {
      ChannelPipeline cp = ctx.pipeline();
      if(cp.get(WebSocketServerProtocolHandshakeHandler.class) == null) {
         ctx.pipeline().addBefore(ctx.name(), WebSocketServerProtocolHandshakeHandler.class.getName(), new WebSocketServerProtocolHandshakeHandler(this.websocketPath, this.subprotocols, this.allowExtensions, this.maxFramePayloadLength));
      }

   }

   protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List out) throws Exception {
      if(frame instanceof CloseWebSocketFrame) {
         WebSocketServerHandshaker handshaker = getHandshaker(ctx);
         if(handshaker != null) {
            frame.retain();
            handshaker.close(ctx.channel(), (CloseWebSocketFrame)frame);
         } else {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
         }

      } else {
         super.decode(ctx, frame, out);
      }
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      if(cause instanceof WebSocketHandshakeException) {
         FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, Unpooled.wrappedBuffer(cause.getMessage().getBytes()));
         ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
      } else {
         ctx.close();
      }

   }

   static WebSocketServerHandshaker getHandshaker(ChannelHandlerContext ctx) {
      return (WebSocketServerHandshaker)ctx.attr(HANDSHAKER_ATTR_KEY).get();
   }

   static void setHandshaker(ChannelHandlerContext ctx, WebSocketServerHandshaker handshaker) {
      ctx.attr(HANDSHAKER_ATTR_KEY).set(handshaker);
   }

   static ChannelHandler forbiddenHttpRequestResponder() {
      return new ChannelInboundHandlerAdapter() {
         public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg instanceof FullHttpRequest) {
               ((FullHttpRequest)msg).release();
               FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
               ctx.channel().writeAndFlush(response);
            } else {
               ctx.fireChannelRead(msg);
            }

         }
      };
   }

   public static enum ServerHandshakeStateEvent {
      HANDSHAKE_COMPLETE;
   }
}
