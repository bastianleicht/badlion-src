package io.netty.channel.embedded;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.embedded.EmbeddedEventLoop;
import io.netty.channel.embedded.EmbeddedSocketAddress;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

public class EmbeddedChannel extends AbstractChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(EmbeddedChannel.class);
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
   private final EmbeddedEventLoop loop = new EmbeddedEventLoop();
   private final ChannelConfig config = new DefaultChannelConfig(this);
   private final SocketAddress localAddress = new EmbeddedSocketAddress();
   private final SocketAddress remoteAddress = new EmbeddedSocketAddress();
   private final Queue inboundMessages = new ArrayDeque();
   private final Queue outboundMessages = new ArrayDeque();
   private Throwable lastException;
   private int state;

   public EmbeddedChannel(ChannelHandler... handlers) {
      super((Channel)null);
      if(handlers == null) {
         throw new NullPointerException("handlers");
      } else {
         int nHandlers = 0;
         ChannelPipeline p = this.pipeline();

         for(ChannelHandler h : handlers) {
            if(h == null) {
               break;
            }

            ++nHandlers;
            p.addLast(new ChannelHandler[]{h});
         }

         if(nHandlers == 0) {
            throw new IllegalArgumentException("handlers is empty.");
         } else {
            this.loop.register(this);
            p.addLast(new ChannelHandler[]{new EmbeddedChannel.LastInboundHandler()});
         }
      }
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   public ChannelConfig config() {
      return this.config;
   }

   public boolean isOpen() {
      return this.state < 2;
   }

   public boolean isActive() {
      return this.state == 1;
   }

   public Queue inboundMessages() {
      return this.inboundMessages;
   }

   /** @deprecated */
   @Deprecated
   public Queue lastInboundBuffer() {
      return this.inboundMessages();
   }

   public Queue outboundMessages() {
      return this.outboundMessages;
   }

   /** @deprecated */
   @Deprecated
   public Queue lastOutboundBuffer() {
      return this.outboundMessages();
   }

   public Object readInbound() {
      return this.inboundMessages.poll();
   }

   public Object readOutbound() {
      return this.outboundMessages.poll();
   }

   public boolean writeInbound(Object... msgs) {
      this.ensureOpen();
      if(msgs.length == 0) {
         return !this.inboundMessages.isEmpty();
      } else {
         ChannelPipeline p = this.pipeline();

         for(Object m : msgs) {
            p.fireChannelRead(m);
         }

         p.fireChannelReadComplete();
         this.runPendingTasks();
         this.checkException();
         return !this.inboundMessages.isEmpty();
      }
   }

   public boolean writeOutbound(Object... msgs) {
      this.ensureOpen();
      if(msgs.length == 0) {
         return !this.outboundMessages.isEmpty();
      } else {
         RecyclableArrayList futures = RecyclableArrayList.newInstance(msgs.length);

         int var12;
         try {
            for(Object m : msgs) {
               if(m == null) {
                  break;
               }

               futures.add(this.write(m));
            }

            this.flush();
            int size = futures.size();

            for(i = 0; var12 < size; ++var12) {
               ChannelFuture future = (ChannelFuture)futures.get(var12);

               assert future.isDone();

               if(future.cause() != null) {
                  this.recordException(future.cause());
               }
            }

            this.runPendingTasks();
            this.checkException();
            var12 = !this.outboundMessages.isEmpty();
         } finally {
            futures.recycle();
         }

         return (boolean)var12;
      }
   }

   public boolean finish() {
      this.close();
      this.runPendingTasks();
      this.checkException();
      return !this.inboundMessages.isEmpty() || !this.outboundMessages.isEmpty();
   }

   public void runPendingTasks() {
      try {
         this.loop.runTasks();
      } catch (Exception var2) {
         this.recordException(var2);
      }

   }

   private void recordException(Throwable cause) {
      if(this.lastException == null) {
         this.lastException = cause;
      } else {
         logger.warn("More than one exception was raised. Will report only the first one and log others.", cause);
      }

   }

   public void checkException() {
      Throwable t = this.lastException;
      if(t != null) {
         this.lastException = null;
         PlatformDependent.throwException(t);
      }
   }

   protected final void ensureOpen() {
      if(!this.isOpen()) {
         this.recordException(new ClosedChannelException());
         this.checkException();
      }

   }

   protected boolean isCompatible(EventLoop loop) {
      return loop instanceof EmbeddedEventLoop;
   }

   protected SocketAddress localAddress0() {
      return this.isActive()?this.localAddress:null;
   }

   protected SocketAddress remoteAddress0() {
      return this.isActive()?this.remoteAddress:null;
   }

   protected void doRegister() throws Exception {
      this.state = 1;
   }

   protected void doBind(SocketAddress localAddress) throws Exception {
   }

   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   protected void doClose() throws Exception {
      this.state = 2;
   }

   protected void doBeginRead() throws Exception {
   }

   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new EmbeddedChannel.DefaultUnsafe();
   }

   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      while(true) {
         Object msg = in.current();
         if(msg == null) {
            return;
         }

         ReferenceCountUtil.retain(msg);
         this.outboundMessages.add(msg);
         in.remove();
      }
   }

   private class DefaultUnsafe extends AbstractChannel.AbstractUnsafe {
      private DefaultUnsafe() {
         super();
      }

      public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
         this.safeSetSuccess(promise);
      }
   }

   private final class LastInboundHandler extends ChannelInboundHandlerAdapter {
      private LastInboundHandler() {
      }

      public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
         EmbeddedChannel.this.inboundMessages.add(msg);
      }

      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
         EmbeddedChannel.this.recordException(cause);
      }
   }
}
