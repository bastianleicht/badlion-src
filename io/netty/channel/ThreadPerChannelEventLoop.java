package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.ThreadPerChannelEventLoopGroup;

public class ThreadPerChannelEventLoop extends SingleThreadEventLoop {
   private final ThreadPerChannelEventLoopGroup parent;
   private Channel ch;

   public ThreadPerChannelEventLoop(ThreadPerChannelEventLoopGroup parent) {
      super(parent, parent.threadFactory, true);
      this.parent = parent;
   }

   public ChannelFuture register(Channel channel, ChannelPromise promise) {
      return super.register(channel, promise).addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture future) throws Exception {
            if(future.isSuccess()) {
               ThreadPerChannelEventLoop.this.ch = future.channel();
            } else {
               ThreadPerChannelEventLoop.this.deregister();
            }

         }
      });
   }

   protected void run() {
      while(true) {
         Runnable task = this.takeTask();
         if(task != null) {
            task.run();
            this.updateLastExecutionTime();
         }

         Channel ch = this.ch;
         if(this.isShuttingDown()) {
            if(ch != null) {
               ch.unsafe().close(ch.unsafe().voidPromise());
            }

            if(this.confirmShutdown()) {
               return;
            }
         } else if(ch != null && !ch.isRegistered()) {
            this.runAllTasks();
            this.deregister();
         }
      }
   }

   protected void deregister() {
      this.ch = null;
      this.parent.activeChildren.remove(this);
      this.parent.idleChildren.add(this);
   }
}
