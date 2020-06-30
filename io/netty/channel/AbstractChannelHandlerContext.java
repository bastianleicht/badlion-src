package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.DefaultChannelProgressivePromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.FailedChannelFuture;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.SucceededChannelFuture;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.internal.OneTimeTask;
import io.netty.util.internal.RecyclableMpscLinkedQueueNode;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;

abstract class AbstractChannelHandlerContext extends DefaultAttributeMap implements ChannelHandlerContext {
   volatile AbstractChannelHandlerContext next;
   volatile AbstractChannelHandlerContext prev;
   private final boolean inbound;
   private final boolean outbound;
   private final AbstractChannel channel;
   private final DefaultChannelPipeline pipeline;
   private final String name;
   private boolean removed;
   final EventExecutor executor;
   private ChannelFuture succeededFuture;
   private volatile Runnable invokeChannelReadCompleteTask;
   private volatile Runnable invokeReadTask;
   private volatile Runnable invokeChannelWritableStateChangedTask;
   private volatile Runnable invokeFlushTask;

   AbstractChannelHandlerContext(DefaultChannelPipeline pipeline, EventExecutorGroup group, String name, boolean inbound, boolean outbound) {
      if(name == null) {
         throw new NullPointerException("name");
      } else {
         this.channel = pipeline.channel;
         this.pipeline = pipeline;
         this.name = name;
         if(group != null) {
            EventExecutor childExecutor = (EventExecutor)pipeline.childExecutors.get(group);
            if(childExecutor == null) {
               childExecutor = group.next();
               pipeline.childExecutors.put(group, childExecutor);
            }

            this.executor = childExecutor;
         } else {
            this.executor = null;
         }

         this.inbound = inbound;
         this.outbound = outbound;
      }
   }

   void teardown() {
      EventExecutor executor = this.executor();
      if(executor.inEventLoop()) {
         this.teardown0();
      } else {
         executor.execute(new Runnable() {
            public void run() {
               AbstractChannelHandlerContext.this.teardown0();
            }
         });
      }

   }

   private void teardown0() {
      AbstractChannelHandlerContext prev = this.prev;
      if(prev != null) {
         synchronized(this.pipeline) {
            this.pipeline.remove0(this);
         }

         prev.teardown();
      }

   }

   public Channel channel() {
      return this.channel;
   }

   public ChannelPipeline pipeline() {
      return this.pipeline;
   }

   public ByteBufAllocator alloc() {
      return this.channel().config().getAllocator();
   }

   public EventExecutor executor() {
      return (EventExecutor)(this.executor == null?this.channel().eventLoop():this.executor);
   }

   public String name() {
      return this.name;
   }

   public ChannelHandlerContext fireChannelRegistered() {
      final AbstractChannelHandlerContext next = this.findContextInbound();
      EventExecutor executor = next.executor();
      if(executor.inEventLoop()) {
         next.invokeChannelRegistered();
      } else {
         executor.execute(new OneTimeTask() {
            public void run() {
               next.invokeChannelRegistered();
            }
         });
      }

      return this;
   }

   private void invokeChannelRegistered() {
      try {
         ((ChannelInboundHandler)this.handler()).channelRegistered(this);
      } catch (Throwable var2) {
         this.notifyHandlerException(var2);
      }

   }

   public ChannelHandlerContext fireChannelUnregistered() {
      final AbstractChannelHandlerContext next = this.findContextInbound();
      EventExecutor executor = next.executor();
      if(executor.inEventLoop()) {
         next.invokeChannelUnregistered();
      } else {
         executor.execute(new OneTimeTask() {
            public void run() {
               next.invokeChannelUnregistered();
            }
         });
      }

      return this;
   }

   private void invokeChannelUnregistered() {
      try {
         ((ChannelInboundHandler)this.handler()).channelUnregistered(this);
      } catch (Throwable var2) {
         this.notifyHandlerException(var2);
      }

   }

   public ChannelHandlerContext fireChannelActive() {
      final AbstractChannelHandlerContext next = this.findContextInbound();
      EventExecutor executor = next.executor();
      if(executor.inEventLoop()) {
         next.invokeChannelActive();
      } else {
         executor.execute(new OneTimeTask() {
            public void run() {
               next.invokeChannelActive();
            }
         });
      }

      return this;
   }

   private void invokeChannelActive() {
      try {
         ((ChannelInboundHandler)this.handler()).channelActive(this);
      } catch (Throwable var2) {
         this.notifyHandlerException(var2);
      }

   }

   public ChannelHandlerContext fireChannelInactive() {
      final AbstractChannelHandlerContext next = this.findContextInbound();
      EventExecutor executor = next.executor();
      if(executor.inEventLoop()) {
         next.invokeChannelInactive();
      } else {
         executor.execute(new OneTimeTask() {
            public void run() {
               next.invokeChannelInactive();
            }
         });
      }

      return this;
   }

   private void invokeChannelInactive() {
      try {
         ((ChannelInboundHandler)this.handler()).channelInactive(this);
      } catch (Throwable var2) {
         this.notifyHandlerException(var2);
      }

   }

   public ChannelHandlerContext fireExceptionCaught(final Throwable cause) {
      if(cause == null) {
         throw new NullPointerException("cause");
      } else {
         final AbstractChannelHandlerContext next = this.next;
         EventExecutor executor = next.executor();
         if(executor.inEventLoop()) {
            next.invokeExceptionCaught(cause);
         } else {
            try {
               executor.execute(new OneTimeTask() {
                  public void run() {
                     next.invokeExceptionCaught(cause);
                  }
               });
            } catch (Throwable var5) {
               if(DefaultChannelPipeline.logger.isWarnEnabled()) {
                  DefaultChannelPipeline.logger.warn("Failed to submit an exceptionCaught() event.", var5);
                  DefaultChannelPipeline.logger.warn("The exceptionCaught() event that was failed to submit was:", cause);
               }
            }
         }

         return this;
      }
   }

   private void invokeExceptionCaught(Throwable cause) {
      try {
         this.handler().exceptionCaught(this, cause);
      } catch (Throwable var3) {
         if(DefaultChannelPipeline.logger.isWarnEnabled()) {
            DefaultChannelPipeline.logger.warn("An exception was thrown by a user handler\'s exceptionCaught() method while handling the following exception:", cause);
         }
      }

   }

   public ChannelHandlerContext fireUserEventTriggered(final Object event) {
      if(event == null) {
         throw new NullPointerException("event");
      } else {
         final AbstractChannelHandlerContext next = this.findContextInbound();
         EventExecutor executor = next.executor();
         if(executor.inEventLoop()) {
            next.invokeUserEventTriggered(event);
         } else {
            executor.execute(new OneTimeTask() {
               public void run() {
                  next.invokeUserEventTriggered(event);
               }
            });
         }

         return this;
      }
   }

   private void invokeUserEventTriggered(Object event) {
      try {
         ((ChannelInboundHandler)this.handler()).userEventTriggered(this, event);
      } catch (Throwable var3) {
         this.notifyHandlerException(var3);
      }

   }

   public ChannelHandlerContext fireChannelRead(final Object msg) {
      if(msg == null) {
         throw new NullPointerException("msg");
      } else {
         final AbstractChannelHandlerContext next = this.findContextInbound();
         EventExecutor executor = next.executor();
         if(executor.inEventLoop()) {
            next.invokeChannelRead(msg);
         } else {
            executor.execute(new OneTimeTask() {
               public void run() {
                  next.invokeChannelRead(msg);
               }
            });
         }

         return this;
      }
   }

   private void invokeChannelRead(Object msg) {
      try {
         ((ChannelInboundHandler)this.handler()).channelRead(this, msg);
      } catch (Throwable var3) {
         this.notifyHandlerException(var3);
      }

   }

   public ChannelHandlerContext fireChannelReadComplete() {
      final AbstractChannelHandlerContext next = this.findContextInbound();
      EventExecutor executor = next.executor();
      if(executor.inEventLoop()) {
         next.invokeChannelReadComplete();
      } else {
         Runnable task = next.invokeChannelReadCompleteTask;
         if(task == null) {
            next.invokeChannelReadCompleteTask = task = new Runnable() {
               public void run() {
                  next.invokeChannelReadComplete();
               }
            };
         }

         executor.execute(task);
      }

      return this;
   }

   private void invokeChannelReadComplete() {
      try {
         ((ChannelInboundHandler)this.handler()).channelReadComplete(this);
      } catch (Throwable var2) {
         this.notifyHandlerException(var2);
      }

   }

   public ChannelHandlerContext fireChannelWritabilityChanged() {
      final AbstractChannelHandlerContext next = this.findContextInbound();
      EventExecutor executor = next.executor();
      if(executor.inEventLoop()) {
         next.invokeChannelWritabilityChanged();
      } else {
         Runnable task = next.invokeChannelWritableStateChangedTask;
         if(task == null) {
            next.invokeChannelWritableStateChangedTask = task = new Runnable() {
               public void run() {
                  next.invokeChannelWritabilityChanged();
               }
            };
         }

         executor.execute(task);
      }

      return this;
   }

   private void invokeChannelWritabilityChanged() {
      try {
         ((ChannelInboundHandler)this.handler()).channelWritabilityChanged(this);
      } catch (Throwable var2) {
         this.notifyHandlerException(var2);
      }

   }

   public ChannelFuture bind(SocketAddress localAddress) {
      return this.bind(localAddress, this.newPromise());
   }

   public ChannelFuture connect(SocketAddress remoteAddress) {
      return this.connect(remoteAddress, this.newPromise());
   }

   public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
      return this.connect(remoteAddress, localAddress, this.newPromise());
   }

   public ChannelFuture disconnect() {
      return this.disconnect(this.newPromise());
   }

   public ChannelFuture close() {
      return this.close(this.newPromise());
   }

   public ChannelFuture deregister() {
      return this.deregister(this.newPromise());
   }

   public ChannelFuture bind(final SocketAddress localAddress, final ChannelPromise promise) {
      if(localAddress == null) {
         throw new NullPointerException("localAddress");
      } else if(!this.validatePromise(promise, false)) {
         return promise;
      } else {
         final AbstractChannelHandlerContext next = this.findContextOutbound();
         EventExecutor executor = next.executor();
         if(executor.inEventLoop()) {
            next.invokeBind(localAddress, promise);
         } else {
            safeExecute(executor, new OneTimeTask() {
               public void run() {
                  next.invokeBind(localAddress, promise);
               }
            }, promise, (Object)null);
         }

         return promise;
      }
   }

   private void invokeBind(SocketAddress localAddress, ChannelPromise promise) {
      try {
         ((ChannelOutboundHandler)this.handler()).bind(this, localAddress, promise);
      } catch (Throwable var4) {
         notifyOutboundHandlerException(var4, promise);
      }

   }

   public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
      return this.connect(remoteAddress, (SocketAddress)null, promise);
   }

   public ChannelFuture connect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
      if(remoteAddress == null) {
         throw new NullPointerException("remoteAddress");
      } else if(!this.validatePromise(promise, false)) {
         return promise;
      } else {
         final AbstractChannelHandlerContext next = this.findContextOutbound();
         EventExecutor executor = next.executor();
         if(executor.inEventLoop()) {
            next.invokeConnect(remoteAddress, localAddress, promise);
         } else {
            safeExecute(executor, new OneTimeTask() {
               public void run() {
                  next.invokeConnect(remoteAddress, localAddress, promise);
               }
            }, promise, (Object)null);
         }

         return promise;
      }
   }

   private void invokeConnect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      try {
         ((ChannelOutboundHandler)this.handler()).connect(this, remoteAddress, localAddress, promise);
      } catch (Throwable var5) {
         notifyOutboundHandlerException(var5, promise);
      }

   }

   public ChannelFuture disconnect(final ChannelPromise promise) {
      if(!this.validatePromise(promise, false)) {
         return promise;
      } else {
         final AbstractChannelHandlerContext next = this.findContextOutbound();
         EventExecutor executor = next.executor();
         if(executor.inEventLoop()) {
            if(!this.channel().metadata().hasDisconnect()) {
               next.invokeClose(promise);
            } else {
               next.invokeDisconnect(promise);
            }
         } else {
            safeExecute(executor, new OneTimeTask() {
               public void run() {
                  if(!AbstractChannelHandlerContext.this.channel().metadata().hasDisconnect()) {
                     next.invokeClose(promise);
                  } else {
                     next.invokeDisconnect(promise);
                  }

               }
            }, promise, (Object)null);
         }

         return promise;
      }
   }

   private void invokeDisconnect(ChannelPromise promise) {
      try {
         ((ChannelOutboundHandler)this.handler()).disconnect(this, promise);
      } catch (Throwable var3) {
         notifyOutboundHandlerException(var3, promise);
      }

   }

   public ChannelFuture close(final ChannelPromise promise) {
      if(!this.validatePromise(promise, false)) {
         return promise;
      } else {
         final AbstractChannelHandlerContext next = this.findContextOutbound();
         EventExecutor executor = next.executor();
         if(executor.inEventLoop()) {
            next.invokeClose(promise);
         } else {
            safeExecute(executor, new OneTimeTask() {
               public void run() {
                  next.invokeClose(promise);
               }
            }, promise, (Object)null);
         }

         return promise;
      }
   }

   private void invokeClose(ChannelPromise promise) {
      try {
         ((ChannelOutboundHandler)this.handler()).close(this, promise);
      } catch (Throwable var3) {
         notifyOutboundHandlerException(var3, promise);
      }

   }

   public ChannelFuture deregister(final ChannelPromise promise) {
      if(!this.validatePromise(promise, false)) {
         return promise;
      } else {
         final AbstractChannelHandlerContext next = this.findContextOutbound();
         EventExecutor executor = next.executor();
         if(executor.inEventLoop()) {
            next.invokeDeregister(promise);
         } else {
            safeExecute(executor, new OneTimeTask() {
               public void run() {
                  next.invokeDeregister(promise);
               }
            }, promise, (Object)null);
         }

         return promise;
      }
   }

   private void invokeDeregister(ChannelPromise promise) {
      try {
         ((ChannelOutboundHandler)this.handler()).deregister(this, promise);
      } catch (Throwable var3) {
         notifyOutboundHandlerException(var3, promise);
      }

   }

   public ChannelHandlerContext read() {
      final AbstractChannelHandlerContext next = this.findContextOutbound();
      EventExecutor executor = next.executor();
      if(executor.inEventLoop()) {
         next.invokeRead();
      } else {
         Runnable task = next.invokeReadTask;
         if(task == null) {
            next.invokeReadTask = task = new Runnable() {
               public void run() {
                  next.invokeRead();
               }
            };
         }

         executor.execute(task);
      }

      return this;
   }

   private void invokeRead() {
      try {
         ((ChannelOutboundHandler)this.handler()).read(this);
      } catch (Throwable var2) {
         this.notifyHandlerException(var2);
      }

   }

   public ChannelFuture write(Object msg) {
      return this.write(msg, this.newPromise());
   }

   public ChannelFuture write(Object msg, ChannelPromise promise) {
      if(msg == null) {
         throw new NullPointerException("msg");
      } else if(!this.validatePromise(promise, true)) {
         ReferenceCountUtil.release(msg);
         return promise;
      } else {
         this.write(msg, false, promise);
         return promise;
      }
   }

   private void invokeWrite(Object msg, ChannelPromise promise) {
      try {
         ((ChannelOutboundHandler)this.handler()).write(this, msg, promise);
      } catch (Throwable var4) {
         notifyOutboundHandlerException(var4, promise);
      }

   }

   public ChannelHandlerContext flush() {
      final AbstractChannelHandlerContext next = this.findContextOutbound();
      EventExecutor executor = next.executor();
      if(executor.inEventLoop()) {
         next.invokeFlush();
      } else {
         Runnable task = next.invokeFlushTask;
         if(task == null) {
            next.invokeFlushTask = task = new Runnable() {
               public void run() {
                  next.invokeFlush();
               }
            };
         }

         safeExecute(executor, task, this.channel.voidPromise(), (Object)null);
      }

      return this;
   }

   private void invokeFlush() {
      try {
         ((ChannelOutboundHandler)this.handler()).flush(this);
      } catch (Throwable var2) {
         this.notifyHandlerException(var2);
      }

   }

   public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
      if(msg == null) {
         throw new NullPointerException("msg");
      } else if(!this.validatePromise(promise, true)) {
         ReferenceCountUtil.release(msg);
         return promise;
      } else {
         this.write(msg, true, promise);
         return promise;
      }
   }

   private void write(Object msg, boolean flush, ChannelPromise promise) {
      AbstractChannelHandlerContext next = this.findContextOutbound();
      EventExecutor executor = next.executor();
      if(executor.inEventLoop()) {
         next.invokeWrite(msg, promise);
         if(flush) {
            next.invokeFlush();
         }
      } else {
         int size = this.channel.estimatorHandle().size(msg);
         if(size > 0) {
            ChannelOutboundBuffer buffer = this.channel.unsafe().outboundBuffer();
            if(buffer != null) {
               buffer.incrementPendingOutboundBytes((long)size);
            }
         }

         Runnable task;
         if(flush) {
            task = AbstractChannelHandlerContext.WriteAndFlushTask.newInstance(next, msg, size, promise);
         } else {
            task = AbstractChannelHandlerContext.WriteTask.newInstance(next, msg, size, promise);
         }

         safeExecute(executor, task, promise, msg);
      }

   }

   public ChannelFuture writeAndFlush(Object msg) {
      return this.writeAndFlush(msg, this.newPromise());
   }

   private static void notifyOutboundHandlerException(Throwable cause, ChannelPromise promise) {
      if(!(promise instanceof VoidChannelPromise)) {
         if(!promise.tryFailure(cause) && DefaultChannelPipeline.logger.isWarnEnabled()) {
            DefaultChannelPipeline.logger.warn("Failed to fail the promise because it\'s done already: {}", promise, cause);
         }

      }
   }

   private void notifyHandlerException(Throwable cause) {
      if(inExceptionCaught(cause)) {
         if(DefaultChannelPipeline.logger.isWarnEnabled()) {
            DefaultChannelPipeline.logger.warn("An exception was thrown by a user handler while handling an exceptionCaught event", cause);
         }

      } else {
         this.invokeExceptionCaught(cause);
      }
   }

   private static boolean inExceptionCaught(Throwable cause) {
      while(true) {
         StackTraceElement[] trace = cause.getStackTrace();
         if(trace != null) {
            for(StackTraceElement t : trace) {
               if(t == null) {
                  break;
               }

               if("exceptionCaught".equals(t.getMethodName())) {
                  return true;
               }
            }
         }

         cause = cause.getCause();
         if(cause == null) {
            break;
         }
      }

      return false;
   }

   public ChannelPromise newPromise() {
      return new DefaultChannelPromise(this.channel(), this.executor());
   }

   public ChannelProgressivePromise newProgressivePromise() {
      return new DefaultChannelProgressivePromise(this.channel(), this.executor());
   }

   public ChannelFuture newSucceededFuture() {
      ChannelFuture succeededFuture = this.succeededFuture;
      if(succeededFuture == null) {
         this.succeededFuture = succeededFuture = new SucceededChannelFuture(this.channel(), this.executor());
      }

      return succeededFuture;
   }

   public ChannelFuture newFailedFuture(Throwable cause) {
      return new FailedChannelFuture(this.channel(), this.executor(), cause);
   }

   private boolean validatePromise(ChannelPromise promise, boolean allowVoidPromise) {
      if(promise == null) {
         throw new NullPointerException("promise");
      } else if(promise.isDone()) {
         if(promise.isCancelled()) {
            return false;
         } else {
            throw new IllegalArgumentException("promise already done: " + promise);
         }
      } else if(promise.channel() != this.channel()) {
         throw new IllegalArgumentException(String.format("promise.channel does not match: %s (expected: %s)", new Object[]{promise.channel(), this.channel()}));
      } else if(promise.getClass() == DefaultChannelPromise.class) {
         return true;
      } else if(!allowVoidPromise && promise instanceof VoidChannelPromise) {
         throw new IllegalArgumentException(StringUtil.simpleClassName(VoidChannelPromise.class) + " not allowed for this operation");
      } else if(promise instanceof AbstractChannel.CloseFuture) {
         throw new IllegalArgumentException(StringUtil.simpleClassName(AbstractChannel.CloseFuture.class) + " not allowed in a pipeline");
      } else {
         return true;
      }
   }

   private AbstractChannelHandlerContext findContextInbound() {
      AbstractChannelHandlerContext ctx = this;

      while(true) {
         ctx = ctx.next;
         if(ctx.inbound) {
            break;
         }
      }

      return ctx;
   }

   private AbstractChannelHandlerContext findContextOutbound() {
      AbstractChannelHandlerContext ctx = this;

      while(true) {
         ctx = ctx.prev;
         if(ctx.outbound) {
            break;
         }
      }

      return ctx;
   }

   public ChannelPromise voidPromise() {
      return this.channel.voidPromise();
   }

   void setRemoved() {
      this.removed = true;
   }

   public boolean isRemoved() {
      return this.removed;
   }

   private static void safeExecute(EventExecutor executor, Runnable runnable, ChannelPromise promise, Object msg) {
      try {
         executor.execute(runnable);
      } catch (Throwable var9) {
         Throwable cause = var9;

         try {
            promise.setFailure(cause);
         } finally {
            if(msg != null) {
               ReferenceCountUtil.release(msg);
            }

         }
      }

   }

   abstract static class AbstractWriteTask extends RecyclableMpscLinkedQueueNode implements Runnable {
      private AbstractChannelHandlerContext ctx;
      private Object msg;
      private ChannelPromise promise;
      private int size;

      private AbstractWriteTask(Recycler.Handle handle) {
         super(handle);
      }

      protected static void init(AbstractChannelHandlerContext.AbstractWriteTask task, AbstractChannelHandlerContext ctx, Object msg, int size, ChannelPromise promise) {
         task.ctx = ctx;
         task.msg = msg;
         task.promise = promise;
         task.size = size;
      }

      public final void run() {
         try {
            if(this.size > 0) {
               ChannelOutboundBuffer buffer = this.ctx.channel.unsafe().outboundBuffer();
               if(buffer != null) {
                  buffer.decrementPendingOutboundBytes((long)this.size);
               }
            }

            this.write(this.ctx, this.msg, this.promise);
         } finally {
            this.ctx = null;
            this.msg = null;
            this.promise = null;
         }

      }

      public Runnable value() {
         return this;
      }

      protected void write(AbstractChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
         ctx.invokeWrite(msg, promise);
      }
   }

   static final class WriteAndFlushTask extends AbstractChannelHandlerContext.AbstractWriteTask {
      private static final Recycler RECYCLER = new Recycler() {
         protected AbstractChannelHandlerContext.WriteAndFlushTask newObject(Recycler.Handle handle) {
            return new AbstractChannelHandlerContext.WriteAndFlushTask(handle);
         }
      };

      private static AbstractChannelHandlerContext.WriteAndFlushTask newInstance(AbstractChannelHandlerContext ctx, Object msg, int size, ChannelPromise promise) {
         AbstractChannelHandlerContext.WriteAndFlushTask task = (AbstractChannelHandlerContext.WriteAndFlushTask)RECYCLER.get();
         init(task, ctx, msg, size, promise);
         return task;
      }

      private WriteAndFlushTask(Recycler.Handle handle) {
         super(handle, null);
      }

      public void write(AbstractChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
         super.write(ctx, msg, promise);
         ctx.invokeFlush();
      }

      protected void recycle(Recycler.Handle handle) {
         RECYCLER.recycle(this, handle);
      }
   }

   static final class WriteTask extends AbstractChannelHandlerContext.AbstractWriteTask implements SingleThreadEventLoop.NonWakeupRunnable {
      private static final Recycler RECYCLER = new Recycler() {
         protected AbstractChannelHandlerContext.WriteTask newObject(Recycler.Handle handle) {
            return new AbstractChannelHandlerContext.WriteTask(handle);
         }
      };

      private static AbstractChannelHandlerContext.WriteTask newInstance(AbstractChannelHandlerContext ctx, Object msg, int size, ChannelPromise promise) {
         AbstractChannelHandlerContext.WriteTask task = (AbstractChannelHandlerContext.WriteTask)RECYCLER.get();
         init(task, ctx, msg, size, promise);
         return task;
      }

      private WriteTask(Recycler.Handle handle) {
         super(handle, null);
      }

      protected void recycle(Recycler.Handle handle) {
         RECYCLER.recycle(this, handle);
      }
   }
}
