package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

@ChannelHandler.Sharable
public abstract class ChannelInitializer extends ChannelInboundHandlerAdapter {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelInitializer.class);

   protected abstract void initChannel(Channel var1) throws Exception;

   public final void channelRegistered(ChannelHandlerContext ctx) throws Exception {
      ChannelPipeline pipeline = ctx.pipeline();
      boolean success = false;

      try {
         this.initChannel(ctx.channel());
         pipeline.remove((ChannelHandler)this);
         ctx.fireChannelRegistered();
         success = true;
      } catch (Throwable var8) {
         logger.warn("Failed to initialize a channel. Closing: " + ctx.channel(), var8);
      } finally {
         if(pipeline.context((ChannelHandler)this) != null) {
            pipeline.remove((ChannelHandler)this);
         }

         if(!success) {
            ctx.close();
         }

      }

   }
}
