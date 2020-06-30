package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.CompleteChannelFuture;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.PlatformDependent;

final class FailedChannelFuture extends CompleteChannelFuture {
   private final Throwable cause;

   FailedChannelFuture(Channel channel, EventExecutor executor, Throwable cause) {
      super(channel, executor);
      if(cause == null) {
         throw new NullPointerException("cause");
      } else {
         this.cause = cause;
      }
   }

   public Throwable cause() {
      return this.cause;
   }

   public boolean isSuccess() {
      return false;
   }

   public ChannelFuture sync() {
      PlatformDependent.throwException(this.cause);
      return this;
   }

   public ChannelFuture syncUninterruptibly() {
      PlatformDependent.throwException(this.cause);
      return this;
   }
}
