package io.netty.channel.local;

import io.netty.channel.AbstractServerChannel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalChannelRegistry;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;

public class LocalServerChannel extends AbstractServerChannel {
   private final ChannelConfig config = new DefaultChannelConfig(this);
   private final Queue inboundBuffer = new ArrayDeque();
   private final Runnable shutdownHook = new Runnable() {
      public void run() {
         LocalServerChannel.this.unsafe().close(LocalServerChannel.this.unsafe().voidPromise());
      }
   };
   private volatile int state;
   private volatile LocalAddress localAddress;
   private volatile boolean acceptInProgress;

   public ChannelConfig config() {
      return this.config;
   }

   public LocalAddress localAddress() {
      return (LocalAddress)super.localAddress();
   }

   public LocalAddress remoteAddress() {
      return (LocalAddress)super.remoteAddress();
   }

   public boolean isOpen() {
      return this.state < 2;
   }

   public boolean isActive() {
      return this.state == 1;
   }

   protected boolean isCompatible(EventLoop loop) {
      return loop instanceof SingleThreadEventLoop;
   }

   protected SocketAddress localAddress0() {
      return this.localAddress;
   }

   protected void doRegister() throws Exception {
      ((SingleThreadEventExecutor)this.eventLoop()).addShutdownHook(this.shutdownHook);
   }

   protected void doBind(SocketAddress localAddress) throws Exception {
      this.localAddress = LocalChannelRegistry.register(this, this.localAddress, localAddress);
      this.state = 1;
   }

   protected void doClose() throws Exception {
      if(this.state <= 1) {
         if(this.localAddress != null) {
            LocalChannelRegistry.unregister(this.localAddress);
            this.localAddress = null;
         }

         this.state = 2;
      }

   }

   protected void doDeregister() throws Exception {
      ((SingleThreadEventExecutor)this.eventLoop()).removeShutdownHook(this.shutdownHook);
   }

   protected void doBeginRead() throws Exception {
      if(!this.acceptInProgress) {
         Queue<Object> inboundBuffer = this.inboundBuffer;
         if(inboundBuffer.isEmpty()) {
            this.acceptInProgress = true;
         } else {
            ChannelPipeline pipeline = this.pipeline();

            while(true) {
               Object m = inboundBuffer.poll();
               if(m == null) {
                  pipeline.fireChannelReadComplete();
                  return;
               }

               pipeline.fireChannelRead(m);
            }
         }
      }
   }

   LocalChannel serve(LocalChannel peer) {
      final LocalChannel child = new LocalChannel(this, peer);
      if(this.eventLoop().inEventLoop()) {
         this.serve0(child);
      } else {
         this.eventLoop().execute(new Runnable() {
            public void run() {
               LocalServerChannel.this.serve0(child);
            }
         });
      }

      return child;
   }

   private void serve0(LocalChannel child) {
      this.inboundBuffer.add(child);
      if(this.acceptInProgress) {
         this.acceptInProgress = false;
         ChannelPipeline pipeline = this.pipeline();

         while(true) {
            Object m = this.inboundBuffer.poll();
            if(m == null) {
               pipeline.fireChannelReadComplete();
               break;
            }

            pipeline.fireChannelRead(m);
         }
      }

   }
}
