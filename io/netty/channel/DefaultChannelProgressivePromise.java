package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFlushPromiseNotifier;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.util.concurrent.DefaultProgressivePromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GenericFutureListener;

public class DefaultChannelProgressivePromise extends DefaultProgressivePromise implements ChannelProgressivePromise, ChannelFlushPromiseNotifier.FlushCheckpoint {
   private final Channel channel;
   private long checkpoint;

   public DefaultChannelProgressivePromise(Channel channel) {
      this.channel = channel;
   }

   public DefaultChannelProgressivePromise(Channel channel, EventExecutor executor) {
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

   public ChannelProgressivePromise setSuccess() {
      return this.setSuccess((Void)null);
   }

   public ChannelProgressivePromise setSuccess(Void result) {
      super.setSuccess(result);
      return this;
   }

   public boolean trySuccess() {
      return this.trySuccess((Object)null);
   }

   public ChannelProgressivePromise setFailure(Throwable cause) {
      super.setFailure(cause);
      return this;
   }

   public ChannelProgressivePromise setProgress(long progress, long total) {
      super.setProgress(progress, total);
      return this;
   }

   public ChannelProgressivePromise addListener(GenericFutureListener listener) {
      super.addListener(listener);
      return this;
   }

   public ChannelProgressivePromise addListeners(GenericFutureListener... listeners) {
      super.addListeners(listeners);
      return this;
   }

   public ChannelProgressivePromise removeListener(GenericFutureListener listener) {
      super.removeListener(listener);
      return this;
   }

   public ChannelProgressivePromise removeListeners(GenericFutureListener... listeners) {
      super.removeListeners(listeners);
      return this;
   }

   public ChannelProgressivePromise sync() throws InterruptedException {
      super.sync();
      return this;
   }

   public ChannelProgressivePromise syncUninterruptibly() {
      super.syncUninterruptibly();
      return this;
   }

   public ChannelProgressivePromise await() throws InterruptedException {
      super.await();
      return this;
   }

   public ChannelProgressivePromise awaitUninterruptibly() {
      super.awaitUninterruptibly();
      return this;
   }

   public long flushCheckpoint() {
      return this.checkpoint;
   }

   public void flushCheckpoint(long checkpoint) {
      this.checkpoint = checkpoint;
   }

   public ChannelProgressivePromise promise() {
      return this;
   }

   protected void checkDeadLock() {
      if(this.channel().isRegistered()) {
         super.checkDeadLock();
      }

   }
}
