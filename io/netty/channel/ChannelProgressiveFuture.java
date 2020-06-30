package io.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ProgressiveFuture;

public interface ChannelProgressiveFuture extends ChannelFuture, ProgressiveFuture {
   ChannelProgressiveFuture addListener(GenericFutureListener var1);

   ChannelProgressiveFuture addListeners(GenericFutureListener... var1);

   ChannelProgressiveFuture removeListener(GenericFutureListener var1);

   ChannelProgressiveFuture removeListeners(GenericFutureListener... var1);

   ChannelProgressiveFuture sync() throws InterruptedException;

   ChannelProgressiveFuture syncUninterruptibly();

   ChannelProgressiveFuture await() throws InterruptedException;

   ChannelProgressiveFuture awaitUninterruptibly();
}
