package io.netty.channel.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import java.net.InetSocketAddress;

public interface SocketChannel extends Channel {
   ServerSocketChannel parent();

   SocketChannelConfig config();

   InetSocketAddress localAddress();

   InetSocketAddress remoteAddress();

   boolean isInputShutdown();

   boolean isOutputShutdown();

   ChannelFuture shutdownOutput();

   ChannelFuture shutdownOutput(ChannelPromise var1);
}
