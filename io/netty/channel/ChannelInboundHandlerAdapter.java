package io.netty.channel;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

public class ChannelInboundHandlerAdapter extends ChannelHandlerAdapter implements ChannelInboundHandler {
   public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
      ctx.fireChannelRegistered();
   }

   public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
      ctx.fireChannelUnregistered();
   }

   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      ctx.fireChannelActive();
   }

   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      ctx.fireChannelInactive();
   }

   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      ctx.fireChannelRead(msg);
   }

   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      ctx.fireChannelReadComplete();
   }

   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
      ctx.fireUserEventTriggered(evt);
   }

   public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
      ctx.fireChannelWritabilityChanged();
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      ctx.fireExceptionCaught(cause);
   }
}
