package io.netty.channel.group;

import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import java.util.Set;

public interface ChannelGroup extends Set, Comparable {
   String name();

   ChannelGroupFuture write(Object var1);

   ChannelGroupFuture write(Object var1, ChannelMatcher var2);

   ChannelGroup flush();

   ChannelGroup flush(ChannelMatcher var1);

   ChannelGroupFuture writeAndFlush(Object var1);

   /** @deprecated */
   @Deprecated
   ChannelGroupFuture flushAndWrite(Object var1);

   ChannelGroupFuture writeAndFlush(Object var1, ChannelMatcher var2);

   /** @deprecated */
   @Deprecated
   ChannelGroupFuture flushAndWrite(Object var1, ChannelMatcher var2);

   ChannelGroupFuture disconnect();

   ChannelGroupFuture disconnect(ChannelMatcher var1);

   ChannelGroupFuture close();

   ChannelGroupFuture close(ChannelMatcher var1);

   /** @deprecated */
   @Deprecated
   ChannelGroupFuture deregister();

   /** @deprecated */
   @Deprecated
   ChannelGroupFuture deregister(ChannelMatcher var1);
}
