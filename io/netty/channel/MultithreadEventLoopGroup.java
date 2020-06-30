package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.MultithreadEventExecutorGroup;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.ThreadFactory;

public abstract class MultithreadEventLoopGroup extends MultithreadEventExecutorGroup implements EventLoopGroup {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(MultithreadEventLoopGroup.class);
   private static final int DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", Runtime.getRuntime().availableProcessors() * 2));

   protected MultithreadEventLoopGroup(int nThreads, ThreadFactory threadFactory, Object... args) {
      super(nThreads == 0?DEFAULT_EVENT_LOOP_THREADS:nThreads, threadFactory, args);
   }

   protected ThreadFactory newDefaultThreadFactory() {
      return new DefaultThreadFactory(this.getClass(), 10);
   }

   public EventLoop next() {
      return (EventLoop)super.next();
   }

   public ChannelFuture register(Channel channel) {
      return this.next().register(channel);
   }

   public ChannelFuture register(Channel channel, ChannelPromise promise) {
      return this.next().register(channel, promise);
   }

   static {
      if(logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.eventLoopThreads: {}", (Object)Integer.valueOf(DEFAULT_EVENT_LOOP_THREADS));
      }

   }
}
