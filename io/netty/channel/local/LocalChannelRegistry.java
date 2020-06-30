package io.netty.channel.local;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.local.LocalAddress;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentMap;

final class LocalChannelRegistry {
   private static final ConcurrentMap boundChannels = PlatformDependent.newConcurrentHashMap();

   static LocalAddress register(Channel channel, LocalAddress oldLocalAddress, SocketAddress localAddress) {
      if(oldLocalAddress != null) {
         throw new ChannelException("already bound");
      } else if(!(localAddress instanceof LocalAddress)) {
         throw new ChannelException("unsupported address type: " + StringUtil.simpleClassName((Object)localAddress));
      } else {
         LocalAddress addr = (LocalAddress)localAddress;
         if(LocalAddress.ANY.equals(addr)) {
            addr = new LocalAddress(channel);
         }

         Channel boundChannel = (Channel)boundChannels.putIfAbsent(addr, channel);
         if(boundChannel != null) {
            throw new ChannelException("address already in use by: " + boundChannel);
         } else {
            return addr;
         }
      }
   }

   static Channel get(SocketAddress localAddress) {
      return (Channel)boundChannels.get(localAddress);
   }

   static void unregister(LocalAddress localAddress) {
      boundChannels.remove(localAddress);
   }
}
