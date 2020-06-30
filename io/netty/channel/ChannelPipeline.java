package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutorGroup;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

public interface ChannelPipeline extends Iterable {
   ChannelPipeline addFirst(String var1, ChannelHandler var2);

   ChannelPipeline addFirst(EventExecutorGroup var1, String var2, ChannelHandler var3);

   ChannelPipeline addLast(String var1, ChannelHandler var2);

   ChannelPipeline addLast(EventExecutorGroup var1, String var2, ChannelHandler var3);

   ChannelPipeline addBefore(String var1, String var2, ChannelHandler var3);

   ChannelPipeline addBefore(EventExecutorGroup var1, String var2, String var3, ChannelHandler var4);

   ChannelPipeline addAfter(String var1, String var2, ChannelHandler var3);

   ChannelPipeline addAfter(EventExecutorGroup var1, String var2, String var3, ChannelHandler var4);

   ChannelPipeline addFirst(ChannelHandler... var1);

   ChannelPipeline addFirst(EventExecutorGroup var1, ChannelHandler... var2);

   ChannelPipeline addLast(ChannelHandler... var1);

   ChannelPipeline addLast(EventExecutorGroup var1, ChannelHandler... var2);

   ChannelPipeline remove(ChannelHandler var1);

   ChannelHandler remove(String var1);

   ChannelHandler remove(Class var1);

   ChannelHandler removeFirst();

   ChannelHandler removeLast();

   ChannelPipeline replace(ChannelHandler var1, String var2, ChannelHandler var3);

   ChannelHandler replace(String var1, String var2, ChannelHandler var3);

   ChannelHandler replace(Class var1, String var2, ChannelHandler var3);

   ChannelHandler first();

   ChannelHandlerContext firstContext();

   ChannelHandler last();

   ChannelHandlerContext lastContext();

   ChannelHandler get(String var1);

   ChannelHandler get(Class var1);

   ChannelHandlerContext context(ChannelHandler var1);

   ChannelHandlerContext context(String var1);

   ChannelHandlerContext context(Class var1);

   Channel channel();

   List names();

   Map toMap();

   ChannelPipeline fireChannelRegistered();

   ChannelPipeline fireChannelUnregistered();

   ChannelPipeline fireChannelActive();

   ChannelPipeline fireChannelInactive();

   ChannelPipeline fireExceptionCaught(Throwable var1);

   ChannelPipeline fireUserEventTriggered(Object var1);

   ChannelPipeline fireChannelRead(Object var1);

   ChannelPipeline fireChannelReadComplete();

   ChannelPipeline fireChannelWritabilityChanged();

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

   ChannelPipeline read();

   ChannelFuture write(Object var1);

   ChannelFuture write(Object var1, ChannelPromise var2);

   ChannelPipeline flush();

   ChannelFuture writeAndFlush(Object var1, ChannelPromise var2);

   ChannelFuture writeAndFlush(Object var1);
}
