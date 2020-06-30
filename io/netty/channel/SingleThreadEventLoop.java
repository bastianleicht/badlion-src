package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.util.concurrent.ThreadFactory;

public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {
   protected SingleThreadEventLoop(EventLoopGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp) {
      super(parent, threadFactory, addTaskWakesUp);
   }

   public EventLoopGroup parent() {
      return (EventLoopGroup)super.parent();
   }

   public EventLoop next() {
      return (EventLoop)super.next();
   }

   public ChannelFuture register(Channel channel) {
      return this.register(channel, new DefaultChannelPromise(channel, this));
   }

   public ChannelFuture register(Channel channel, ChannelPromise promise) {
      if(channel == null) {
         throw new NullPointerException("channel");
      } else if(promise == null) {
         throw new NullPointerException("promise");
      } else {
         channel.unsafe().register(this, promise);
         return promise;
      }
   }

   protected boolean wakesUpForTask(Runnable task) {
      return !(task instanceof SingleThreadEventLoop.NonWakeupRunnable);
   }

   interface NonWakeupRunnable extends Runnable {
   }
}
