package io.netty.channel.group;

import io.netty.channel.Channel;

public interface ChannelMatcher {
   boolean matches(Channel var1);
}
