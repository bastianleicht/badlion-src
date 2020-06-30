package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFlushPromiseNotifier;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GenericFutureListener;

public class DefaultChannelPromise extends DefaultPromise implements ChannelPromise, ChannelFlushPromiseNotifier.FlushCheckpoint {
   private final Channel channel;
   private long checkpoint;

   public DefaultChannelPromise(Channel channel) {
      this.channel = channel;
   }

   public DefaultChannelPromise(Channel channel, EventExecutor executor) {
      super(executor);
      this.channel = channel;
   }

   protected EventExecutor executor() {
      EventExecutor e = super.executor();
      return (EventExecutor)(e == null?this.channel().eventLoop():e);
   }

   public Channel channel() {
      return this.channel;
   }

   public ChannelPromise setSuccess() {
      return this.setSuccess((Void)null);
   }

   public ChannelPromise setSuccess(Void result) {
      super.setSuccess(result);
      return this;
   }

   public boolean trySuccess() {
      return this.trySuccess((Object)null);
   }

   public ChannelPromise setFailure(Throwable cause) {
      super.setFailure(cause);
      return this;
   }

   public ChannelPromise addListener(GenericFutureListener listener) {
      super.addListener(listener);
      return this;
   }

   public ChannelPromise addListeners(GenericFutureListener... listeners) {
      super.addListeners(listeners);
      return this;
   }

   public ChannelPromise removeListener(GenericFutureListener listener) {
      super.removeListener(listener);
      return this;
   }

   public ChannelPromise removeListeners(GenericFutureListener... listeners) {
      super.removeListeners(listeners);
      return this;
   }

   public ChannelPromise sync() throws InterruptedException {
      super.sync();
      return this;
   }

   public ChannelPromise syncUninterruptibly() {
      super.syncUninterruptibly();
      return this;
   }

   public ChannelPromise await() throws InterruptedException {
      super.await();
      return this;
   }

   public ChannelPromise awaitUninterruptibly() {
      super.awaitUninterruptibly();
      return this;
   }

   public long flushCheckpoint() {
      return this.checkpoint;
   }

   public void flushCheckpoint(long checkpoint) {
      this.checkpoint = checkpoint;
   }

   public ChannelPromise promise() {
      return this;
   }

   protected void checkDeadLock() {
      if(this.channel().isRegistered()) {
         super.checkDeadLock();
      }

   }
}
