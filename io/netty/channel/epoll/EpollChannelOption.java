package io.netty.channel.epoll;

import io.netty.channel.ChannelOption;

public final class EpollChannelOption extends ChannelOption {
   public static final ChannelOption TCP_CORK = valueOf("TCP_CORK");
   public static final ChannelOption TCP_KEEPIDLE = valueOf("TCP_KEEPIDLE");
   public static final ChannelOption TCP_KEEPINTVL = valueOf("TCP_KEEPINTVL");
   public static final ChannelOption TCP_KEEPCNT = valueOf("TCP_KEEPCNT");
   public static final ChannelOption SO_REUSEPORT = valueOf("SO_REUSEPORT");

   private EpollChannelOption(String name) {
      super(name);
   }
}
