package io.netty.channel.sctp;

import com.sun.nio.sctp.Association;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.sctp.SctpChannelConfig;
import io.netty.channel.sctp.SctpServerChannel;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;

public interface SctpChannel extends Channel {
   SctpServerChannel parent();

   Association association();

   InetSocketAddress localAddress();

   Set allLocalAddresses();

   SctpChannelConfig config();

   InetSocketAddress remoteAddress();

   Set allRemoteAddresses();

   ChannelFuture bindAddress(InetAddress var1);

   ChannelFuture bindAddress(InetAddress var1, ChannelPromise var2);

   ChannelFuture unbindAddress(InetAddress var1);

   ChannelFuture unbindAddress(InetAddress var1, ChannelPromise var2);
}
