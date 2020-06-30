package io.netty.channel.local;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannelRegistry;
import io.netty.channel.local.LocalServerChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import io.netty.util.internal.InternalThreadLocalMap;
import java.net.SocketAddress;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;

public class LocalChannel extends AbstractChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
   private static final int MAX_READER_STACK_DEPTH = 8;
   private final ChannelConfig config = new DefaultChannelConfig(this);
   private final Queue inboundBuffer = new ArrayDeque();
   private final Runnable readTask = new Runnable() {
      public void run() {
         ChannelPipeline pipeline = LocalChannel.this.pipeline();

         while(true) {
            Object m = LocalChannel.this.inboundBuffer.poll();
            if(m == null) {
               pipeline.fireChannelReadComplete();
               return;
            }

            pipeline.fireChannelRead(m);
         }
      }
   };
   private final Runnable shutdownHook = new Runnable() {
      public void run() {
         LocalChannel.this.unsafe().close(LocalChannel.this.unsafe().voidPromise());
      }
   };
   private volatile int state;
   private volatile LocalChannel peer;
   private volatile LocalAddress localAddress;
   private volatile LocalAddress remoteAddress;
   private volatile ChannelPromise connectPromise;
   private volatile boolean readInProgress;
   private volatile boolean registerInProgress;

   public LocalChannel() {
      super((Channel)null);
   }

   LocalChannel(LocalServerChannel parent, LocalChannel peer) {
      super(parent);
      this.peer = peer;
      this.localAddress = parent.localAddress();
      this.remoteAddress = peer.localAddress();
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   public ChannelConfig config() {
      return this.config;
   }

   public LocalServerChannel parent() {
      return (LocalServerChannel)super.parent();
   }

   public LocalAddress localAddress() {
      return (LocalAddress)super.localAddress();
   }

   public LocalAddress remoteAddress() {
      return (LocalAddress)super.remoteAddress();
   }

   public boolean isOpen() {
      return this.state < 3;
   }

   public boolean isActive() {
      return this.state == 2;
   }

   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new LocalChannel.LocalUnsafe();
   }

   protected boolean isCompatible(EventLoop loop) {
      return loop instanceof SingleThreadEventLoop;
   }

   protected SocketAddress localAddress0() {
      return this.localAddress;
   }

   protected SocketAddress remoteAddress0() {
      return this.remoteAddress;
   }

   protected void doRegister() throws Exception {
      if(this.peer != null && this.parent() != null) {
         final LocalChannel peer = this.peer;
         this.registerInProgress = true;
         this.state = 2;
         peer.remoteAddress = this.parent().localAddress();
         peer.state = 2;
         peer.eventLoop().execute(new Runnable() {
            public void run() {
               LocalChannel.this.registerInProgress = false;
               peer.pipeline().fireChannelActive();
               peer.connectPromise.setSuccess();
            }
         });
      }

      ((SingleThreadEventExecutor)this.eventLoop()).addShutdownHook(this.shutdownHook);
   }

   protected void doBind(SocketAddress localAddress) throws Exception {
      this.localAddress = LocalChannelRegistry.register(this, this.localAddress, localAddress);
      this.state = 1;
   }

   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   protected void doClose() throws Exception {
      if(this.state <= 2) {
         if(this.localAddress != null) {
            if(this.parent() == null) {
               LocalChannelRegistry.unregister(this.localAddress);
            }

            this.localAddress = null;
         }

         this.state = 3;
      }

      final LocalChannel peer = this.peer;
      if(peer != null && peer.isActive()) {
         EventLoop eventLoop = peer.eventLoop();
         if(eventLoop.inEventLoop() && !this.registerInProgress) {
            peer.unsafe().close(this.unsafe().voidPromise());
         } else {
            peer.eventLoop().execute(new Runnable() {
               public void run() {
                  peer.unsafe().close(LocalChannel.this.unsafe().voidPromise());
               }
            });
         }

         this.peer = null;
      }

   }

   protected void doDeregister() throws Exception {
      ((SingleThreadEventExecutor)this.eventLoop()).removeShutdownHook(this.shutdownHook);
   }

   protected void doBeginRead() throws Exception {
      if(!this.readInProgress) {
         ChannelPipeline pipeline = this.pipeline();
         Queue<Object> inboundBuffer = this.inboundBuffer;
         if(inboundBuffer.isEmpty()) {
            this.readInProgress = true;
         } else {
            InternalThreadLocalMap threadLocals = InternalThreadLocalMap.get();
            Integer stackDepth = Integer.valueOf(threadLocals.localChannelReaderStackDepth());
            if(stackDepth.intValue() < 8) {
               threadLocals.setLocalChannelReaderStackDepth(stackDepth.intValue() + 1);

               try {
                  while(true) {
                     Object received = inboundBuffer.poll();
                     if(received == null) {
                        pipeline.fireChannelReadComplete();
                        break;
                     }

                     pipeline.fireChannelRead(received);
                  }
               } finally {
                  threadLocals.setLocalChannelReaderStackDepth(stackDepth.intValue());
               }
            } else {
               this.eventLoop().execute(this.readTask);
            }

         }
      }
   }

   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      if(this.state < 2) {
         throw new NotYetConnectedException();
      } else if(this.state > 2) {
         throw new ClosedChannelException();
      } else {
         final LocalChannel peer = this.peer;
         final ChannelPipeline peerPipeline = peer.pipeline();
         EventLoop peerLoop = peer.eventLoop();
         if(peerLoop != this.eventLoop()) {
            final Object[] msgsCopy = new Object[in.size()];

            for(int i = 0; i < msgsCopy.length; ++i) {
               msgsCopy[i] = ReferenceCountUtil.retain(in.current());
               in.remove();
            }

            peerLoop.execute(new Runnable() {
               public void run() {
                  Collections.addAll(peer.inboundBuffer, msgsCopy);
                  LocalChannel.finishPeerRead(peer, peerPipeline);
               }
            });
         } else {
            while(true) {
               Object msg = in.current();
               if(msg == null) {
                  finishPeerRead(peer, peerPipeline);
                  break;
               }

               peer.inboundBuffer.add(msg);
               ReferenceCountUtil.retain(msg);
               in.remove();
            }
         }

      }
   }

   private static void finishPeerRead(LocalChannel peer, ChannelPipeline peerPipeline) {
      if(peer.readInProgress) {
         peer.readInProgress = false;

         while(true) {
            Object received = peer.inboundBuffer.poll();
            if(received == null) {
               peerPipeline.fireChannelReadComplete();
               break;
            }

            peerPipeline.fireChannelRead(received);
         }
      }

   }

   private class LocalUnsafe extends AbstractChannel.AbstractUnsafe {
      private LocalUnsafe() {
         super();
      }

      public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
         if(promise.setUncancellable() && this.ensureOpen(promise)) {
            if(LocalChannel.this.state == 2) {
               Exception cause = new AlreadyConnectedException();
               this.safeSetFailure(promise, cause);
               LocalChannel.this.pipeline().fireExceptionCaught(cause);
            } else if(LocalChannel.this.connectPromise != null) {
               throw new ConnectionPendingException();
            } else {
               LocalChannel.this.connectPromise = promise;
               if(LocalChannel.this.state != 1 && localAddress == null) {
                  localAddress = new LocalAddress(LocalChannel.this);
               }

               if(localAddress != null) {
                  try {
                     LocalChannel.this.doBind((SocketAddress)localAddress);
                  } catch (Throwable var6) {
                     this.safeSetFailure(promise, var6);
                     this.close(this.voidPromise());
                     return;
                  }
               }

               Channel boundChannel = LocalChannelRegistry.get(remoteAddress);
               if(!(boundChannel instanceof LocalServerChannel)) {
                  Exception cause = new ChannelException("connection refused");
                  this.safeSetFailure(promise, cause);
                  this.close(this.voidPromise());
               } else {
                  LocalServerChannel serverChannel = (LocalServerChannel)boundChannel;
                  LocalChannel.this.peer = serverChannel.serve(LocalChannel.this);
               }
            }
         }
      }
   }
}
