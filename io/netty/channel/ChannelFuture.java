package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public interface ChannelFuture extends Future {
   Channel channel();

   ChannelFuture addListener(GenericFutureListener var1);

   ChannelFuture addListeners(GenericFutureListener... var1);

   ChannelFuture removeListener(GenericFutureListener var1);

   ChannelFuture removeListeners(GenericFutureListener... var1);

   ChannelFuture sync() throws InterruptedException;

   ChannelFuture syncUninterruptibly();

   ChannelFuture await() throws InterruptedException;

   ChannelFuture awaitUninterruptibly();
}
