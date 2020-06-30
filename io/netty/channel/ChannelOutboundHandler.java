package io.netty.channel;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.net.SocketAddress;

public interface ChannelOutboundHandler extends ChannelHandler {
   void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception;

   void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception;

   void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception;

   void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception;

   void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception;

   void read(ChannelHandlerContext var1) throws Exception;

   void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception;

   void flush(ChannelHandlerContext var1) throws Exception;
}
