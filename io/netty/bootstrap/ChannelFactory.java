package io.netty.bootstrap;

import io.netty.channel.Channel;

public interface ChannelFactory {
   Channel newChannel();
}
