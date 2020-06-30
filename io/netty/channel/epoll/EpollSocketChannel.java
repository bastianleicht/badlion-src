package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollSocketChannelConfig;
import io.netty.channel.epoll.IovArray;
import io.netty.channel.epoll.Native;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class EpollSocketChannel extends AbstractEpollChannel implements SocketChannel {
   private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(DefaultFileRegion.class) + ')';
   private final EpollSocketChannelConfig config = new EpollSocketChannelConfig(this);
   private ChannelPromise connectPromise;
   private ScheduledFuture connectTimeoutFuture;
   private SocketAddress requestedRemoteAddress;
   private volatile InetSocketAddress local;
   private volatile InetSocketAddress remote;
   private volatile boolean inputShutdown;
   private volatile boolean outputShutdown;

   EpollSocketChannel(Channel parent, int fd) {
      super(parent, fd, 1, true);
      this.remote = Native.remoteAddress(fd);
      this.local = Native.localAddress(fd);
   }

   public EpollSocketChannel() {
      super(Native.socketStreamFd(), 1);
   }

   protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
      return new EpollSocketChannel.EpollSocketUnsafe();
   }

   protected SocketAddress localAddress0() {
      return this.local;
   }

   protected SocketAddress remoteAddress0() {
      return this.remote;
   }

   protected void doBind(SocketAddress local) throws Exception {
      InetSocketAddress localAddress = (InetSocketAddress)local;
      Native.bind(this.fd, localAddress.getAddress(), localAddress.getPort());
      this.local = Native.localAddress(this.fd);
   }

   private boolean writeBytes(ChannelOutboundBuffer in, ByteBuf buf) throws Exception {
      int readableBytes = buf.readableBytes();
      if(readableBytes == 0) {
         in.remove();
         return true;
      } else {
         boolean done = false;
         long writtenBytes = 0L;
         if(buf.hasMemoryAddress()) {
            long memoryAddress = buf.memoryAddress();
            int readerIndex = buf.readerIndex();
            int writerIndex = buf.writerIndex();

            while(true) {
               int localFlushedAmount = Native.writeAddress(this.fd, memoryAddress, readerIndex, writerIndex);
               if(localFlushedAmount <= 0) {
                  this.setEpollOut();
                  break;
               }

               writtenBytes += (long)localFlushedAmount;
               if(writtenBytes == (long)readableBytes) {
                  done = true;
                  break;
               }

               readerIndex += localFlushedAmount;
            }

            in.removeBytes(writtenBytes);
            return done;
         } else if(buf.nioBufferCount() != 1) {
            ByteBuffer[] nioBuffers = buf.nioBuffers();
            return this.writeBytesMultiple(in, nioBuffers, nioBuffers.length, (long)readableBytes);
         } else {
            int readerIndex = buf.readerIndex();
            ByteBuffer nioBuf = buf.internalNioBuffer(readerIndex, buf.readableBytes());

            while(true) {
               int pos = nioBuf.position();
               int limit = nioBuf.limit();
               int localFlushedAmount = Native.write(this.fd, nioBuf, pos, limit);
               if(localFlushedAmount > 0) {
                  nioBuf.position(pos + localFlushedAmount);
                  writtenBytes += (long)localFlushedAmount;
                  if(writtenBytes != (long)readableBytes) {
                     continue;
                  }

                  done = true;
                  break;
               }

               this.setEpollOut();
               break;
            }

            in.removeBytes(writtenBytes);
            return done;
         }
      }
   }

   private boolean writeBytesMultiple(ChannelOutboundBuffer in, IovArray array) throws IOException {
      long expectedWrittenBytes = array.size();
      int cnt = array.count();

      assert expectedWrittenBytes != 0L;

      assert cnt != 0;

      boolean done = false;
      long writtenBytes = 0L;
      int offset = 0;
      int end = offset + cnt;

      while(true) {
         long localWrittenBytes = Native.writevAddresses(this.fd, array.memoryAddress(offset), cnt);
         if(localWrittenBytes == 0L) {
            this.setEpollOut();
            break;
         }

         expectedWrittenBytes -= localWrittenBytes;
         writtenBytes += localWrittenBytes;
         if(expectedWrittenBytes == 0L) {
            done = true;
            break;
         }

         while(true) {
            long bytes = array.processWritten(offset, localWrittenBytes);
            if(bytes == -1L) {
               break;
            }

            ++offset;
            --cnt;
            localWrittenBytes -= bytes;
            if(offset >= end || localWrittenBytes <= 0L) {
               break;
            }
         }
      }

      in.removeBytes(writtenBytes);
      return done;
   }

   private boolean writeBytesMultiple(ChannelOutboundBuffer in, ByteBuffer[] nioBuffers, int nioBufferCnt, long expectedWrittenBytes) throws IOException {
      assert expectedWrittenBytes != 0L;

      boolean done = false;
      long writtenBytes = 0L;
      int offset = 0;
      int end = offset + nioBufferCnt;

      while(true) {
         long localWrittenBytes = Native.writev(this.fd, nioBuffers, offset, nioBufferCnt);
         if(localWrittenBytes == 0L) {
            this.setEpollOut();
            break;
         }

         expectedWrittenBytes -= localWrittenBytes;
         writtenBytes += localWrittenBytes;
         if(expectedWrittenBytes == 0L) {
            done = true;
            break;
         }

         while(true) {
            ByteBuffer buffer = nioBuffers[offset];
            int pos = buffer.position();
            int bytes = buffer.limit() - pos;
            if((long)bytes > localWrittenBytes) {
               buffer.position(pos + (int)localWrittenBytes);
               break;
            }

            ++offset;
            --nioBufferCnt;
            localWrittenBytes -= (long)bytes;
            if(offset >= end || localWrittenBytes <= 0L) {
               break;
            }
         }
      }

      in.removeBytes(writtenBytes);
      return done;
   }

   private boolean writeFileRegion(ChannelOutboundBuffer in, DefaultFileRegion region) throws Exception {
      long regionCount = region.count();
      if(region.transfered() >= regionCount) {
         in.remove();
         return true;
      } else {
         long baseOffset = region.position();
         boolean done = false;
         long flushedAmount = 0L;

         for(int i = this.config().getWriteSpinCount() - 1; i >= 0; --i) {
            long offset = region.transfered();
            long localFlushedAmount = Native.sendfile(this.fd, region, baseOffset, offset, regionCount - offset);
            if(localFlushedAmount == 0L) {
               this.setEpollOut();
               break;
            }

            flushedAmount += localFlushedAmount;
            if(region.transfered() >= regionCount) {
               done = true;
               break;
            }
         }

         if(flushedAmount > 0L) {
            in.progress(flushedAmount);
         }

         if(done) {
            in.remove();
         }

         return done;
      }
   }

   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      while(true) {
         int msgCount = in.size();
         if(msgCount == 0) {
            this.clearEpollOut();
         } else if(msgCount > 1 && in.current() instanceof ByteBuf) {
            if(this.doWriteMultiple(in)) {
               continue;
            }
         } else if(this.doWriteSingle(in)) {
            continue;
         }

         return;
      }
   }

   private boolean doWriteSingle(ChannelOutboundBuffer in) throws Exception {
      Object msg = in.current();
      if(msg instanceof ByteBuf) {
         ByteBuf buf = (ByteBuf)msg;
         if(!this.writeBytes(in, buf)) {
            return false;
         }
      } else {
         if(!(msg instanceof DefaultFileRegion)) {
            throw new Error();
         }

         DefaultFileRegion region = (DefaultFileRegion)msg;
         if(!this.writeFileRegion(in, region)) {
            return false;
         }
      }

      return true;
   }

   private boolean doWriteMultiple(ChannelOutboundBuffer in) throws Exception {
      if(PlatformDependent.hasUnsafe()) {
         IovArray array = IovArray.get(in);
         int cnt = array.count();
         if(cnt >= 1) {
            if(!this.writeBytesMultiple(in, array)) {
               return false;
            }
         } else {
            in.removeBytes(0L);
         }
      } else {
         ByteBuffer[] buffers = in.nioBuffers();
         int cnt = in.nioBufferCount();
         if(cnt >= 1) {
            if(!this.writeBytesMultiple(in, buffers, cnt, in.nioBufferSize())) {
               return false;
            }
         } else {
            in.removeBytes(0L);
         }
      }

      return true;
   }

   protected Object filterOutboundMessage(Object msg) {
      if(!(msg instanceof ByteBuf)) {
         if(msg instanceof DefaultFileRegion) {
            return msg;
         } else {
            throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
         }
      } else {
         ByteBuf buf = (ByteBuf)msg;
         if(!buf.hasMemoryAddress() && (PlatformDependent.hasUnsafe() || !buf.isDirect())) {
            buf = this.newDirectBuffer(buf);

            assert buf.hasMemoryAddress();
         }

         return buf;
      }
   }

   public EpollSocketChannelConfig config() {
      return this.config;
   }

   public boolean isInputShutdown() {
      return this.inputShutdown;
   }

   public boolean isOutputShutdown() {
      return this.outputShutdown || !this.isActive();
   }

   public ChannelFuture shutdownOutput() {
      return this.shutdownOutput(this.newPromise());
   }

   public ChannelFuture shutdownOutput(final ChannelPromise promise) {
      EventLoop loop = this.eventLoop();
      if(loop.inEventLoop()) {
         try {
            Native.shutdown(this.fd, false, true);
            this.outputShutdown = true;
            promise.setSuccess();
         } catch (Throwable var4) {
            promise.setFailure(var4);
         }
      } else {
         loop.execute(new Runnable() {
            public void run() {
               EpollSocketChannel.this.shutdownOutput(promise);
            }
         });
      }

      return promise;
   }

   public ServerSocketChannel parent() {
      return (ServerSocketChannel)super.parent();
   }

   final class EpollSocketUnsafe extends AbstractEpollChannel.AbstractEpollUnsafe {
      private RecvByteBufAllocator.Handle allocHandle;

      EpollSocketUnsafe() {
         super();
      }

      private void closeOnRead(ChannelPipeline pipeline) {
         EpollSocketChannel.this.inputShutdown = true;
         if(EpollSocketChannel.this.isOpen()) {
            if(Boolean.TRUE.equals(EpollSocketChannel.this.config().getOption(ChannelOption.ALLOW_HALF_CLOSURE))) {
               this.clearEpollIn0();
               pipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
            } else {
               this.close(this.voidPromise());
            }
         }

      }

      private boolean handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close) {
         if(byteBuf != null) {
            if(byteBuf.isReadable()) {
               this.readPending = false;
               pipeline.fireChannelRead(byteBuf);
            } else {
               byteBuf.release();
            }
         }

         pipeline.fireChannelReadComplete();
         pipeline.fireExceptionCaught(cause);
         if(!close && !(cause instanceof IOException)) {
            return false;
         } else {
            this.closeOnRead(pipeline);
            return true;
         }
      }

      public void connect(final SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
         if(promise.setUncancellable() && this.ensureOpen(promise)) {
            try {
               if(EpollSocketChannel.this.connectPromise != null) {
                  throw new IllegalStateException("connection attempt already made");
               }

               boolean wasActive = EpollSocketChannel.this.isActive();
               if(this.doConnect((InetSocketAddress)remoteAddress, (InetSocketAddress)localAddress)) {
                  this.fulfillConnectPromise(promise, wasActive);
               } else {
                  EpollSocketChannel.this.connectPromise = promise;
                  EpollSocketChannel.this.requestedRemoteAddress = remoteAddress;
                  int connectTimeoutMillis = EpollSocketChannel.this.config().getConnectTimeoutMillis();
                  if(connectTimeoutMillis > 0) {
                     EpollSocketChannel.this.connectTimeoutFuture = EpollSocketChannel.this.eventLoop().schedule(new Runnable() {
                        public void run() {
                           ChannelPromise connectPromise = EpollSocketChannel.this.connectPromise;
                           ConnectTimeoutException cause = new ConnectTimeoutException("connection timed out: " + remoteAddress);
                           if(connectPromise != null && connectPromise.tryFailure(cause)) {
                              EpollSocketUnsafe.this.close(EpollSocketUnsafe.this.voidPromise());
                           }

                        }
                     }, (long)connectTimeoutMillis, TimeUnit.MILLISECONDS);
                  }

                  promise.addListener(new ChannelFutureListener() {
                     public void operationComplete(ChannelFuture future) throws Exception {
                        if(future.isCancelled()) {
                           if(EpollSocketChannel.this.connectTimeoutFuture != null) {
                              EpollSocketChannel.this.connectTimeoutFuture.cancel(false);
                           }

                           EpollSocketChannel.this.connectPromise = null;
                           EpollSocketUnsafe.this.close(EpollSocketUnsafe.this.voidPromise());
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

               this.closeIfClosed();
               promise.tryFailure(t);
            }

         }
      }

      private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
         if(promise != null) {
            EpollSocketChannel.this.active = true;
            boolean promiseSet = promise.trySuccess();
            if(!wasActive && EpollSocketChannel.this.isActive()) {
               EpollSocketChannel.this.pipeline().fireChannelActive();
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

      private void finishConnect() {
         assert EpollSocketChannel.this.eventLoop().inEventLoop();

         boolean connectStillInProgress = false;

         try {
            boolean wasActive = EpollSocketChannel.this.isActive();
            if(this.doFinishConnect()) {
               this.fulfillConnectPromise(EpollSocketChannel.this.connectPromise, wasActive);
               return;
            }

            connectStillInProgress = true;
         } catch (Throwable var7) {
            Throwable t = var7;
            if(var7 instanceof ConnectException) {
               Throwable newT = new ConnectException(var7.getMessage() + ": " + EpollSocketChannel.this.requestedRemoteAddress);
               newT.setStackTrace(var7.getStackTrace());
               t = newT;
            }

            this.fulfillConnectPromise(EpollSocketChannel.this.connectPromise, t);
            return;
         } finally {
            if(!connectStillInProgress) {
               if(EpollSocketChannel.this.connectTimeoutFuture != null) {
                  EpollSocketChannel.this.connectTimeoutFuture.cancel(false);
               }

               EpollSocketChannel.this.connectPromise = null;
            }

         }

      }

      void epollOutReady() {
         if(EpollSocketChannel.this.connectPromise != null) {
            this.finishConnect();
         } else {
            super.epollOutReady();
         }

      }

      private boolean doConnect(InetSocketAddress remoteAddress, InetSocketAddress localAddress) throws Exception {
         if(localAddress != null) {
            AbstractEpollChannel.checkResolvable(localAddress);
            Native.bind(EpollSocketChannel.this.fd, localAddress.getAddress(), localAddress.getPort());
         }

         boolean success = false;

         boolean var5;
         try {
            AbstractEpollChannel.checkResolvable(remoteAddress);
            boolean connected = Native.connect(EpollSocketChannel.this.fd, remoteAddress.getAddress(), remoteAddress.getPort());
            EpollSocketChannel.this.remote = remoteAddress;
            EpollSocketChannel.this.local = Native.localAddress(EpollSocketChannel.this.fd);
            if(!connected) {
               EpollSocketChannel.this.setEpollOut();
            }

            success = true;
            var5 = connected;
         } finally {
            if(!success) {
               EpollSocketChannel.this.doClose();
            }

         }

         return var5;
      }

      private boolean doFinishConnect() throws Exception {
         if(Native.finishConnect(EpollSocketChannel.this.fd)) {
            EpollSocketChannel.this.clearEpollOut();
            return true;
         } else {
            EpollSocketChannel.this.setEpollOut();
            return false;
         }
      }

      private int doReadBytes(ByteBuf byteBuf) throws Exception {
         int writerIndex = byteBuf.writerIndex();
         int localReadAmount;
         if(byteBuf.hasMemoryAddress()) {
            localReadAmount = Native.readAddress(EpollSocketChannel.this.fd, byteBuf.memoryAddress(), writerIndex, byteBuf.capacity());
         } else {
            ByteBuffer buf = byteBuf.internalNioBuffer(writerIndex, byteBuf.writableBytes());
            localReadAmount = Native.read(EpollSocketChannel.this.fd, buf, buf.position(), buf.limit());
         }

         if(localReadAmount > 0) {
            byteBuf.writerIndex(writerIndex + localReadAmount);
         }

         return localReadAmount;
      }

      void epollRdHupReady() {
         if(EpollSocketChannel.this.isActive()) {
            this.epollInReady();
         } else {
            this.closeOnRead(EpollSocketChannel.this.pipeline());
         }

      }

      void epollInReady() {
         ChannelConfig config = EpollSocketChannel.this.config();
         ChannelPipeline pipeline = EpollSocketChannel.this.pipeline();
         ByteBufAllocator allocator = config.getAllocator();
         RecvByteBufAllocator.Handle allocHandle = this.allocHandle;
         if(allocHandle == null) {
            this.allocHandle = allocHandle = config.getRecvByteBufAllocator().newHandle();
         }

         ByteBuf byteBuf = null;
         boolean close = false;

         try {
            int totalReadAmount = 0;

            while(true) {
               byteBuf = allocHandle.allocate(allocator);
               int writable = byteBuf.writableBytes();
               int localReadAmount = this.doReadBytes(byteBuf);
               if(localReadAmount <= 0) {
                  byteBuf.release();
                  close = localReadAmount < 0;
                  break;
               }

               this.readPending = false;
               pipeline.fireChannelRead(byteBuf);
               byteBuf = null;
               if(totalReadAmount >= Integer.MAX_VALUE - localReadAmount) {
                  allocHandle.record(totalReadAmount);
                  totalReadAmount = localReadAmount;
               } else {
                  totalReadAmount += localReadAmount;
               }

               if(localReadAmount < writable) {
                  break;
               }
            }

            pipeline.fireChannelReadComplete();
            allocHandle.record(totalReadAmount);
            if(close) {
               this.closeOnRead(pipeline);
               close = false;
            }
         } catch (Throwable var13) {
            boolean closed = this.handleReadException(pipeline, byteBuf, var13, close);
            if(!closed) {
               EpollSocketChannel.this.eventLoop().execute(new Runnable() {
                  public void run() {
                     EpollSocketUnsafe.this.epollInReady();
                  }
               });
            }
         } finally {
            if(!config.isAutoRead() && !this.readPending) {
               this.clearEpollIn0();
            }

         }

      }
   }
}
