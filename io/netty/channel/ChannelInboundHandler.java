package io.netty.channel;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

public interface ChannelInboundHandler extends ChannelHandler {
   void channelRegistered(ChannelHandlerContext var1) throws Exception;

   void channelUnregistered(ChannelHandlerContext var1) throws Exception;

   void channelActive(ChannelHandlerContext var1) throws Exception;

   void channelInactive(ChannelHandlerContext var1) throws Exception;

   void channelRead(ChannelHandlerContext var1, Object var2) throws Exception;

   void channelReadComplete(ChannelHandlerContext var1) throws Exception;

   void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception;

   void channelWritabilityChanged(ChannelHandlerContext var1) throws Exception;

   void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception;
}
