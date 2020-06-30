package io.netty.channel.embedded;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.Future;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

final class EmbeddedEventLoop extends AbstractEventExecutor implements EventLoop {
   private final Queue tasks = new ArrayDeque(2);

   public void execute(Runnable command) {
      if(command == null) {
         throw new NullPointerException("command");
      } else {
         this.tasks.add(command);
      }
   }

   void runTasks() {
      while(true) {
         Runnable task = (Runnable)this.tasks.poll();
         if(task == null) {
            return;
         }

         task.run();
      }
   }

   public Future shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
      throw new UnsupportedOperationException();
   }

   public Future terminationFuture() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public void shutdown() {
      throw new UnsupportedOperationException();
   }

   public boolean isShuttingDown() {
      return false;
   }

   public boolean isShutdown() {
      return false;
   }

   public boolean isTerminated() {
      return false;
   }

   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      Thread.sleep(unit.toMillis(timeout));
      return false;
   }

   public ChannelFuture register(Channel channel) {
      return this.register(channel, new DefaultChannelPromise(channel, this));
   }

   public ChannelFuture register(Channel channel, ChannelPromise promise) {
      channel.unsafe().register(this, promise);
      return promise;
   }

   public boolean inEventLoop() {
      return true;
   }

   public boolean inEventLoop(Thread thread) {
      return true;
   }

   public EventLoop next() {
      return this;
   }

   public EventLoopGroup parent() {
      return this;
   }
}
