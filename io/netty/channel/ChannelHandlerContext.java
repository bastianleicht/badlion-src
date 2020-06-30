package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeMap;
import io.netty.util.concurrent.EventExecutor;
import java.net.SocketAddress;

public interface ChannelHandlerContext extends AttributeMap {
   Channel channel();

   EventExecutor executor();

   String name();

   ChannelHandler handler();

   boolean isRemoved();

   ChannelHandlerContext fireChannelRegistered();

   ChannelHandlerContext fireChannelUnregistered();

   ChannelHandlerContext fireChannelActive();

   ChannelHandlerContext fireChannelInactive();

   ChannelHandlerContext fireExceptionCaught(Throwable var1);

   ChannelHandlerContext fireUserEventTriggered(Object var1);

   ChannelHandlerContext fireChannelRead(Object var1);

   ChannelHandlerContext fireChannelReadComplete();

   ChannelHandlerContext fireChannelWritabilityChanged();

   ChannelFuture bind(SocketAddress var1);

   ChannelFuture connect(SocketAddress var1);

   ChannelFuture connect(SocketAddress var1, SocketAddress var2);

   ChannelFuture disconnect();

   ChannelFuture close();

   ChannelFuture deregister();

   ChannelFuture bind(SocketAddress var1, ChannelPromise var2);

   ChannelFuture connect(SocketAddress var1, ChannelPromise var2);

   ChannelFuture connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3);

   ChannelFuture disconnect(ChannelPromise var1);

   ChannelFuture close(ChannelPromise var1);

   ChannelFuture deregister(ChannelPromise var1);

   ChannelHandlerContext read();

   ChannelFuture write(Object var1);

   ChannelFuture write(Object var1, ChannelPromise var2);

   ChannelHandlerContext flush();

   ChannelFuture writeAndFlush(Object var1, ChannelPromise var2);

   ChannelFuture writeAndFlush(Object var1);

   ChannelPipeline pipeline();

   ByteBufAllocator alloc();

   ChannelPromise newPromise();

   ChannelProgressivePromise newProgressivePromise();

   ChannelFuture newSucceededFuture();

   ChannelFuture newFailedFuture(Throwable var1);

   ChannelPromise voidPromise();
}
