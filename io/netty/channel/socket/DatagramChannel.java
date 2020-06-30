package io.netty.channel.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramChannelConfig;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

public interface DatagramChannel extends Channel {
   DatagramChannelConfig config();

   InetSocketAddress localAddress();

   InetSocketAddress remoteAddress();

   boolean isConnected();

   ChannelFuture joinGroup(InetAddress var1);

   ChannelFuture joinGroup(InetAddress var1, ChannelPromise var2);

   ChannelFuture joinGroup(InetSocketAddress var1, NetworkInterface var2);

   ChannelFuture joinGroup(InetSocketAddress var1, NetworkInterface var2, ChannelPromise var3);

   ChannelFuture joinGroup(InetAddress var1, NetworkInterface var2, InetAddress var3);

   ChannelFuture joinGroup(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4);

   ChannelFuture leaveGroup(InetAddress var1);

   ChannelFuture leaveGroup(InetAddress var1, ChannelPromise var2);

   ChannelFuture leaveGroup(InetSocketAddress var1, NetworkInterface var2);

   ChannelFuture leaveGroup(InetSocketAddress var1, NetworkInterface var2, ChannelPromise var3);

   ChannelFuture leaveGroup(InetAddress var1, NetworkInterface var2, InetAddress var3);

   ChannelFuture leaveGroup(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4);

   ChannelFuture block(InetAddress var1, NetworkInterface var2, InetAddress var3);

   ChannelFuture block(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4);

   ChannelFuture block(InetAddress var1, InetAddress var2);

   ChannelFuture block(InetAddress var1, InetAddress var2, ChannelPromise var3);
}
