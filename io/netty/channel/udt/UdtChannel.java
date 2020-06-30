package io.netty.channel.udt;

import io.netty.channel.Channel;
import io.netty.channel.udt.UdtChannelConfig;
import java.net.InetSocketAddress;

public interface UdtChannel extends Channel {
   UdtChannelConfig config();

   InetSocketAddress localAddress();

   InetSocketAddress remoteAddress();
}
