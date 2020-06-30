package io.netty.channel.sctp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ServerChannel;
import io.netty.channel.sctp.SctpServerChannelConfig;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;

public interface SctpServerChannel extends ServerChannel {
   SctpServerChannelConfig config();

   InetSocketAddress localAddress();

   Set allLocalAddresses();

   ChannelFuture bindAddress(InetAddress var1);

   ChannelFuture bindAddress(InetAddress var1, ChannelPromise var2);

   ChannelFuture unbindAddress(InetAddress var1);

   ChannelFuture unbindAddress(InetAddress var1, ChannelPromise var2);
}
