package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Iterator;

public interface ChannelGroupFuture extends Future, Iterable {
   ChannelGroup group();

   ChannelFuture find(Channel var1);

   boolean isSuccess();

   ChannelGroupException cause();

   boolean isPartialSuccess();

   boolean isPartialFailure();

   ChannelGroupFuture addListener(GenericFutureListener var1);

   ChannelGroupFuture addListeners(GenericFutureListener... var1);

   ChannelGroupFuture removeListener(GenericFutureListener var1);

   ChannelGroupFuture removeListeners(GenericFutureListener... var1);

   ChannelGroupFuture await() throws InterruptedException;

   ChannelGroupFuture awaitUninterruptibly();

   ChannelGroupFuture syncUninterruptibly();

   ChannelGroupFuture sync() throws InterruptedException;

   Iterator iterator();
}
