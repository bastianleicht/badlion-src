package io.netty.channel.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoop;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.OneTimeTask;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractNioChannel extends AbstractChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractNioChannel.class);
   private final SelectableChannel ch;
   protected final int readInterestOp;
   volatile SelectionKey selectionKey;
   private volatile boolean inputShutdown;
   private volatile boolean readPending;
   private ChannelPromise connectPromise;
   private ScheduledFuture connectTimeoutFuture;
   private SocketAddress requestedRemoteAddress;

   protected AbstractNioChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
      super(parent);
      this.ch = ch;
      this.readInterestOp = readInterestOp;

      try {
         ch.configureBlocking(false);
      } catch (IOException var7) {
         try {
            ch.close();
         } catch (IOException var6) {
            if(logger.isWarnEnabled()) {
               logger.warn("Failed to close a partially initialized socket.", (Throwable)var6);
            }
         }

         throw new ChannelException("Failed to enter non-blocking mode.", var7);
      }
   }

   public boolean isOpen() {
      return this.ch.isOpen();
   }

   public AbstractNioChannel.NioUnsafe unsafe() {
      return (AbstractNioChannel.NioUnsafe)super.unsafe();
   }

   protected SelectableChannel javaChannel() {
      return this.ch;
   }

   public NioEventLoop eventLoop() {
      return (NioEventLoop)super.eventLoop();
   }

   protected SelectionKey selectionKey() {
      assert this.selectionKey != null;

      return this.selectionKey;
   }

   protected boolean isReadPending() {
      return this.readPending;
   }

   protected void setReadPending(boolean readPending) {
      this.readPending = readPending;
   }

   protected boolean isInputShutdown() {
      return this.inputShutdown;
   }

   void setInputShutdown() {
      this.inputShutdown = true;
   }

   protected boolean isCompatible(EventLoop loop) {
      return loop instanceof NioEventLoop;
   }

   protected void doRegister() throws Exception {
      boolean selected = false;

      while(true) {
         try {
            this.selectionKey = this.javaChannel().register(this.eventLoop().selector, 0, this);
            return;
         } catch (CancelledKeyException var3) {
            if(selected) {
               throw var3;
            }

            this.eventLoop().selectNow();
            selected = true;
         }
      }
   }

   protected void doDeregister() throws Exception {
      this.eventLoop().cancel(this.selectionKey());
   }

   protected void doBeginRead() throws Exception {
      if(!this.inputShutdown) {
         SelectionKey selectionKey = this.selectionKey;
         if(selectionKey.isValid()) {
            this.readPending = true;
            int interestOps = selectionKey.interestOps();
            if((interestOps & this.readInterestOp) == 0) {
               selectionKey.interestOps(interestOps | this.readInterestOp);
            }

         }
      }
   }

   protected abstract boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception;

   protected abstract void doFinishConnect() throws Exception;

   protected final ByteBuf newDirectBuffer(ByteBuf buf) {
      int readableBytes = buf.readableBytes();
      if(readableBytes == 0) {
         ReferenceCountUtil.safeRelease(buf);
         return Unpooled.EMPTY_BUFFER;
      } else {
         ByteBufAllocator alloc = this.alloc();
         if(alloc.isDirectBufferPooled()) {
            ByteBuf directBuf = alloc.directBuffer(readableBytes);
            directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
            ReferenceCountUtil.safeRelease(buf);
            return directBuf;
         } else {
            ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
            if(directBuf != null) {
               directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
               ReferenceCountUtil.safeRelease(buf);
               return directBuf;
            } else {
               return buf;
            }
         }
      }
   }

   protected final ByteBuf newDirectBuffer(ReferenceCounted holder, ByteBuf buf) {
      int readableBytes = buf.readableBytes();
      if(readableBytes == 0) {
         ReferenceCountUtil.safeRelease(holder);
         return Unpooled.EMPTY_BUFFER;
      } else {
         ByteBufAllocator alloc = this.alloc();
         if(alloc.isDirectBufferPooled()) {
            ByteBuf directBuf = alloc.directBuffer(readableBytes);
            directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
            ReferenceCountUtil.safeRelease(holder);
            return directBuf;
         } else {
            ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
            if(directBuf != null) {
               directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
               ReferenceCountUtil.safeRelease(holder);
               return directBuf;
            } else {
               if(holder != buf) {
                  buf.retain();
                  ReferenceCountUtil.safeRelease(holder);
               }

               return buf;
            }
         }
      }
   }

   protected abstract class AbstractNioUnsafe extends AbstractChannel.AbstractUnsafe implements AbstractNioChannel.NioUnsafe {
      protected AbstractNioUnsafe() {
         super();
      }

      protected final void removeReadOp() {
         SelectionKey key = AbstractNioChannel.this.selectionKey();
         if(key.isValid()) {
            int interestOps = key.interestOps();
            if((interestOps & AbstractNioChannel.this.readInterestOp) != 0) {
               key.interestOps(interestOps & ~AbstractNioChannel.this.readInterestOp);
            }

         }
      }

      public final SelectableChannel ch() {
         return AbstractNioChannel.this.javaChannel();
      }

      public final void connect(final SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
         if(promise.setUncancellable() && this.ensureOpen(promise)) {
            try {
               if(AbstractNioChannel.this.connectPromise != null) {
                  throw new IllegalStateException("connection attempt already made");
               }

               boolean wasActive = AbstractNioChannel.this.isActive();
               if(AbstractNioChannel.this.doConnect(remoteAddress, localAddress)) {
                  this.fulfillConnectPromise(promise, wasActive);
               } else {
                  AbstractNioChannel.this.connectPromise = promise;
                  AbstractNioChannel.this.requestedRemoteAddress = remoteAddress;
                  int connectTimeoutMillis = AbstractNioChannel.this.config().getConnectTimeoutMillis();
                  if(connectTimeoutMillis > 0) {
                     AbstractNioChannel.this.connectTimeoutFuture = AbstractNioChannel.this.eventLoop().schedule(new OneTimeTask() {
                        public void run() {
                           ChannelPromise connectPromise = AbstractNioChannel.this.connectPromise;
                           ConnectTimeoutException cause = new ConnectTimeoutException("connection timed out: " + remoteAddress);
                           if(connectPromise != null && connectPromise.tryFailure(cause)) {
                              AbstractNioUnsafe.this.close(AbstractNioUnsafe.this.voidPromise());
                           }

                        }
                     }, (long)connectTimeoutMillis, TimeUnit.MILLISECONDS);
                  }

                  promise.addListener(new ChannelFutureListener() {
                     public void operationComplete(ChannelFuture future) throws Exception {
                        if(future.isCancelled()) {
                           if(AbstractNioChannel.this.connectTimeoutFuture != null) {
                              AbstractNioChannel.this.connectTimeoutFuture.cancel(false);
                           }

                           AbstractNioChannel.this.connectPromise = null;
                           AbstractNioUnsafe.this.close(AbstractNioUnsafe.this.voidPromise());
                        }

                     }
                  });
               }
            } catch (Throwable var6) {
               Throwable t = var6;
               if(var6 instanceof ConnectException) {
                  Throwable newT = new ConnectException(var6.getMessage() + ": " + remoteAddress);
                  newT.setStackTrace(var6.getStackTrace());
                  t = newT;
               }

               promise.tryFailure(t);
               this.closeIfClosed();
            }

         }
      }

      private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
         if(promise != null) {
            boolean promiseSet = promise.trySuccess();
            if(!wasActive && AbstractNioChannel.this.isActive()) {
               AbstractNioChannel.this.pipeline().fireChannelActive();
            }

            if(!promiseSet) {
               this.close(this.voidPromise());
            }

         }
      }

      private void fulfillConnectPromise(ChannelPromise promise, Throwable cause) {
         if(promise != null) {
            promise.tryFailure(cause);
            this.closeIfClosed();
         }
      }

      public final void finishConnect() {
         assert AbstractNioChannel.this.eventLoop().inEventLoop();

         try {
            boolean wasActive = AbstractNioChannel.this.isActive();
            AbstractNioChannel.this.doFinishConnect();
            this.fulfillConnectPromise(AbstractNioChannel.this.connectPromise, wasActive);
         } catch (Throwable var6) {
            Throwable t = var6;
            if(var6 instanceof ConnectException) {
               Throwable newT = new ConnectException(var6.getMessage() + ": " + AbstractNioChannel.this.requestedRemoteAddress);
               newT.setStackTrace(var6.getStackTrace());
               t = newT;
            }

            this.fulfillConnectPromise(AbstractNioChannel.this.connectPromise, t);
         } finally {
            if(AbstractNioChannel.this.connectTimeoutFuture != null) {
               AbstractNioChannel.this.connectTimeoutFuture.cancel(false);
            }

            AbstractNioChannel.this.connectPromise = null;
         }

      }

      protected final void flush0() {
         if(!this.isFlushPending()) {
            super.flush0();
         }
      }

      public final void forceFlush() {
         super.flush0();
      }

      private boolean isFlushPending() {
         SelectionKey selectionKey = AbstractNioChannel.this.selectionKey();
         return selectionKey.isValid() && (selectionKey.interestOps() & 4) != 0;
      }
   }

   public interface NioUnsafe extends Channel.Unsafe {
      SelectableChannel ch();

      void finishConnect();

      void read();

      void forceFlush();
   }
}
