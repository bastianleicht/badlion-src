package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.PendingWriteQueue;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.ssl.NotSslRecordException;
import io.netty.handler.ssl.OpenSslEngine;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;

public class SslHandler extends ByteToMessageDecoder implements ChannelOutboundHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslHandler.class);
   private static final Pattern IGNORABLE_CLASS_IN_STACK = Pattern.compile("^.*(?:Socket|Datagram|Sctp|Udt)Channel.*$");
   private static final Pattern IGNORABLE_ERROR_MESSAGE = Pattern.compile("^.*(?:connection.*(?:reset|closed|abort|broken)|broken.*pipe).*$", 2);
   private static final SSLException SSLENGINE_CLOSED = new SSLException("SSLEngine closed already");
   private static final SSLException HANDSHAKE_TIMED_OUT = new SSLException("handshake timed out");
   private static final ClosedChannelException CHANNEL_CLOSED = new ClosedChannelException();
   private volatile ChannelHandlerContext ctx;
   private final SSLEngine engine;
   private final int maxPacketBufferSize;
   private final Executor delegatedTaskExecutor;
   private final boolean wantsDirectBuffer;
   private final boolean wantsLargeOutboundNetworkBuffer;
   private boolean wantsInboundHeapBuffer;
   private final boolean startTls;
   private boolean sentFirstMessage;
   private boolean flushedBeforeHandshakeDone;
   private PendingWriteQueue pendingUnencryptedWrites;
   private final SslHandler.LazyChannelPromise handshakePromise;
   private final SslHandler.LazyChannelPromise sslCloseFuture;
   private boolean needsFlush;
   private int packetLength;
   private volatile long handshakeTimeoutMillis;
   private volatile long closeNotifyTimeoutMillis;

   public SslHandler(SSLEngine engine) {
      this(engine, false);
   }

   public SslHandler(SSLEngine engine, boolean startTls) {
      this(engine, startTls, ImmediateExecutor.INSTANCE);
   }

   /** @deprecated */
   @Deprecated
   public SslHandler(SSLEngine engine, Executor delegatedTaskExecutor) {
      this(engine, false, delegatedTaskExecutor);
   }

   /** @deprecated */
   @Deprecated
   public SslHandler(SSLEngine engine, boolean startTls, Executor delegatedTaskExecutor) {
      this.handshakePromise = new SslHandler.LazyChannelPromise();
      this.sslCloseFuture = new SslHandler.LazyChannelPromise();
      this.handshakeTimeoutMillis = 10000L;
      this.closeNotifyTimeoutMillis = 3000L;
      if(engine == null) {
         throw new NullPointerException("engine");
      } else if(delegatedTaskExecutor == null) {
         throw new NullPointerException("delegatedTaskExecutor");
      } else {
         this.engine = engine;
         this.delegatedTaskExecutor = delegatedTaskExecutor;
         this.startTls = startTls;
         this.maxPacketBufferSize = engine.getSession().getPacketBufferSize();
         this.wantsDirectBuffer = engine instanceof OpenSslEngine;
         this.wantsLargeOutboundNetworkBuffer = !(engine instanceof OpenSslEngine);
      }
   }

   public long getHandshakeTimeoutMillis() {
      return this.handshakeTimeoutMillis;
   }

   public void setHandshakeTimeout(long handshakeTimeout, TimeUnit unit) {
      if(unit == null) {
         throw new NullPointerException("unit");
      } else {
         this.setHandshakeTimeoutMillis(unit.toMillis(handshakeTimeout));
      }
   }

   public void setHandshakeTimeoutMillis(long handshakeTimeoutMillis) {
      if(handshakeTimeoutMillis < 0L) {
         throw new IllegalArgumentException("handshakeTimeoutMillis: " + handshakeTimeoutMillis + " (expected: >= 0)");
      } else {
         this.handshakeTimeoutMillis = handshakeTimeoutMillis;
      }
   }

   public long getCloseNotifyTimeoutMillis() {
      return this.closeNotifyTimeoutMillis;
   }

   public void setCloseNotifyTimeout(long closeNotifyTimeout, TimeUnit unit) {
      if(unit == null) {
         throw new NullPointerException("unit");
      } else {
         this.setCloseNotifyTimeoutMillis(unit.toMillis(closeNotifyTimeout));
      }
   }

   public void setCloseNotifyTimeoutMillis(long closeNotifyTimeoutMillis) {
      if(closeNotifyTimeoutMillis < 0L) {
         throw new IllegalArgumentException("closeNotifyTimeoutMillis: " + closeNotifyTimeoutMillis + " (expected: >= 0)");
      } else {
         this.closeNotifyTimeoutMillis = closeNotifyTimeoutMillis;
      }
   }

   public SSLEngine engine() {
      return this.engine;
   }

   public Future handshakeFuture() {
      return this.handshakePromise;
   }

   public ChannelFuture close() {
      return this.close(this.ctx.newPromise());
   }

   public ChannelFuture close(final ChannelPromise future) {
      final ChannelHandlerContext ctx = this.ctx;
      ctx.executor().execute(new Runnable() {
         public void run() {
            SslHandler.this.engine.closeOutbound();

            try {
               SslHandler.this.write(ctx, Unpooled.EMPTY_BUFFER, future);
               SslHandler.this.flush(ctx);
            } catch (Exception var2) {
               if(!future.tryFailure(var2)) {
                  SslHandler.logger.warn("flush() raised a masked exception.", (Throwable)var2);
               }
            }

         }
      });
      return future;
   }

   public Future sslCloseFuture() {
      return this.sslCloseFuture;
   }

   public void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
      if(!this.pendingUnencryptedWrites.isEmpty()) {
         this.pendingUnencryptedWrites.removeAndFailAll(new ChannelException("Pending write on removal of SslHandler"));
      }

   }

   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
      ctx.bind(localAddress, promise);
   }

   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
      ctx.connect(remoteAddress, localAddress, promise);
   }

   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      ctx.deregister(promise);
   }

   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      this.closeOutboundAndChannel(ctx, promise, true);
   }

   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      this.closeOutboundAndChannel(ctx, promise, false);
   }

   public void read(ChannelHandlerContext ctx) {
      ctx.read();
   }

   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      this.pendingUnencryptedWrites.add(msg, promise);
   }

   public void flush(ChannelHandlerContext ctx) throws Exception {
      if(this.startTls && !this.sentFirstMessage) {
         this.sentFirstMessage = true;
         this.pendingUnencryptedWrites.removeAndWriteAll();
         ctx.flush();
      } else {
         if(this.pendingUnencryptedWrites.isEmpty()) {
            this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, ctx.voidPromise());
         }

         if(!this.handshakePromise.isDone()) {
            this.flushedBeforeHandshakeDone = true;
         }

         this.wrap(ctx, false);
         ctx.flush();
      }
   }

   private void wrap(ChannelHandlerContext ctx, boolean inUnwrap) throws SSLException {
      ByteBuf out = null;
      ChannelPromise promise = null;

      try {
         while(true) {
            ByteBuf buf;
            while(true) {
               Object msg = this.pendingUnencryptedWrites.current();
               if(msg == null) {
                  return;
               }

               if(msg instanceof ByteBuf) {
                  buf = (ByteBuf)msg;
                  if(out == null) {
                     out = this.allocateOutNetBuf(ctx, buf.readableBytes());
                  }
                  break;
               }

               this.pendingUnencryptedWrites.removeAndWrite();
            }

            SSLEngineResult result = this.wrap(this.engine, buf, out);
            if(!buf.isReadable()) {
               promise = this.pendingUnencryptedWrites.remove();
            } else {
               promise = null;
            }

            if(result.getStatus() == Status.CLOSED) {
               this.pendingUnencryptedWrites.removeAndFailAll(SSLENGINE_CLOSED);
               return;
            }

            switch(result.getHandshakeStatus()) {
            case NEED_TASK:
               this.runDelegatedTasks();
               break;
            case FINISHED:
               this.setHandshakeSuccess();
            case NOT_HANDSHAKING:
               this.setHandshakeSuccessIfStillHandshaking();
            case NEED_WRAP:
               this.finishWrap(ctx, out, promise, inUnwrap);
               promise = null;
               out = null;
               break;
            case NEED_UNWRAP:
               return;
            default:
               throw new IllegalStateException("Unknown handshake status: " + result.getHandshakeStatus());
            }
         }
      } catch (SSLException var11) {
         this.setHandshakeFailure(var11);
         throw var11;
      } finally {
         this.finishWrap(ctx, out, promise, inUnwrap);
      }
   }

   private void finishWrap(ChannelHandlerContext ctx, ByteBuf out, ChannelPromise promise, boolean inUnwrap) {
      if(out == null) {
         out = Unpooled.EMPTY_BUFFER;
      } else if(!out.isReadable()) {
         out.release();
         out = Unpooled.EMPTY_BUFFER;
      }

      if(promise != null) {
         ctx.write(out, promise);
      } else {
         ctx.write(out);
      }

      if(inUnwrap) {
         this.needsFlush = true;
      }

   }

   private void wrapNonAppData(ChannelHandlerContext ctx, boolean inUnwrap) throws SSLException {
      ByteBuf out = null;

      try {
         while(true) {
            if(out == null) {
               out = this.allocateOutNetBuf(ctx, 0);
            }

            SSLEngineResult result = this.wrap(this.engine, Unpooled.EMPTY_BUFFER, out);
            if(result.bytesProduced() > 0) {
               ctx.write(out);
               if(inUnwrap) {
                  this.needsFlush = true;
               }

               out = null;
            }

            switch(result.getHandshakeStatus()) {
            case NEED_TASK:
               this.runDelegatedTasks();
               break;
            case FINISHED:
               this.setHandshakeSuccess();
               break;
            case NOT_HANDSHAKING:
               this.setHandshakeSuccessIfStillHandshaking();
               if(!inUnwrap) {
                  this.unwrapNonAppData(ctx);
               }
            case NEED_WRAP:
               break;
            case NEED_UNWRAP:
               if(!inUnwrap) {
                  this.unwrapNonAppData(ctx);
               }
               break;
            default:
               throw new IllegalStateException("Unknown handshake status: " + result.getHandshakeStatus());
            }

            if(result.bytesProduced() == 0) {
               break;
            }
         }

      } catch (SSLException var8) {
         this.setHandshakeFailure(var8);
         throw var8;
      } finally {
         if(out != null) {
            out.release();
         }

      }
   }

   private SSLEngineResult wrap(SSLEngine engine, ByteBuf in, ByteBuf out) throws SSLException {
      ByteBuffer in0 = in.nioBuffer();
      if(!in0.isDirect()) {
         ByteBuffer newIn0 = ByteBuffer.allocateDirect(in0.remaining());
         newIn0.put(in0).flip();
         in0 = newIn0;
      }

      while(true) {
         ByteBuffer out0 = out.nioBuffer(out.writerIndex(), out.writableBytes());
         SSLEngineResult result = engine.wrap(in0, out0);
         in.skipBytes(result.bytesConsumed());
         out.writerIndex(out.writerIndex() + result.bytesProduced());
         switch(result.getStatus()) {
         case BUFFER_OVERFLOW:
            out.ensureWritable(this.maxPacketBufferSize);
            break;
         default:
            return result;
         }
      }
   }

   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      this.setHandshakeFailure(CHANNEL_CLOSED);
      super.channelInactive(ctx);
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      if(this.ignoreException(cause)) {
         if(logger.isDebugEnabled()) {
            logger.debug("Swallowing a harmless \'connection reset by peer / broken pipe\' error that occurred while writing close_notify in response to the peer\'s close_notify", cause);
         }

         if(ctx.channel().isActive()) {
            ctx.close();
         }
      } else {
         ctx.fireExceptionCaught(cause);
      }

   }

   private boolean ignoreException(Throwable t) {
      if(!(t instanceof SSLException) && t instanceof IOException && this.sslCloseFuture.isDone()) {
         String message = String.valueOf(t.getMessage()).toLowerCase();
         if(IGNORABLE_ERROR_MESSAGE.matcher(message).matches()) {
            return true;
         }

         StackTraceElement[] elements = t.getStackTrace();

         for(StackTraceElement element : elements) {
            String classname = element.getClassName();
            String methodname = element.getMethodName();
            if(!classname.startsWith("io.netty.") && "read".equals(methodname)) {
               if(IGNORABLE_CLASS_IN_STACK.matcher(classname).matches()) {
                  return true;
               }

               try {
                  Class<?> clazz = PlatformDependent.getClassLoader(this.getClass()).loadClass(classname);
                  if(SocketChannel.class.isAssignableFrom(clazz) || DatagramChannel.class.isAssignableFrom(clazz)) {
                     return true;
                  }

                  if(PlatformDependent.javaVersion() >= 7 && "com.sun.nio.sctp.SctpChannel".equals(clazz.getSuperclass().getName())) {
                     return true;
                  }
               } catch (ClassNotFoundException var11) {
                  ;
               }
            }
         }
      }

      return false;
   }

   public static boolean isEncrypted(ByteBuf buffer) {
      if(buffer.readableBytes() < 5) {
         throw new IllegalArgumentException("buffer must have at least 5 readable bytes");
      } else {
         return getEncryptedPacketLength(buffer, buffer.readerIndex()) != -1;
      }
   }

   private static int getEncryptedPacketLength(ByteBuf buffer, int offset) {
      int packetLength = 0;
      boolean tls;
      switch(buffer.getUnsignedByte(offset)) {
      case 20:
      case 21:
      case 22:
      case 23:
         tls = true;
         break;
      default:
         tls = false;
      }

      if(tls) {
         int majorVersion = buffer.getUnsignedByte(offset + 1);
         if(majorVersion == 3) {
            packetLength = buffer.getUnsignedShort(offset + 3) + 5;
            if(packetLength <= 5) {
               tls = false;
            }
         } else {
            tls = false;
         }
      }

      if(!tls) {
         boolean sslv2 = true;
         int headerLength = (buffer.getUnsignedByte(offset) & 128) != 0?2:3;
         int majorVersion = buffer.getUnsignedByte(offset + headerLength + 1);
         if(majorVersion != 2 && majorVersion != 3) {
            sslv2 = false;
         } else {
            if(headerLength == 2) {
               packetLength = (buffer.getShort(offset) & 32767) + 2;
            } else {
               packetLength = (buffer.getShort(offset) & 16383) + 3;
            }

            if(packetLength <= headerLength) {
               sslv2 = false;
            }
         }

         if(!sslv2) {
            return -1;
         }
      }

      return packetLength;
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws SSLException {
      int startOffset = in.readerIndex();
      int endOffset = in.writerIndex();
      int offset = startOffset;
      int totalLength = 0;
      if(this.packetLength > 0) {
         if(endOffset - startOffset < this.packetLength) {
            return;
         }

         offset = startOffset + this.packetLength;
         totalLength = this.packetLength;
         this.packetLength = 0;
      }

      boolean nonSslRecord;
      int newTotalLength;
      for(nonSslRecord = false; totalLength < 18713; totalLength = newTotalLength) {
         int readableBytes = endOffset - offset;
         if(readableBytes < 5) {
            break;
         }

         int packetLength = getEncryptedPacketLength(in, offset);
         if(packetLength == -1) {
            nonSslRecord = true;
            break;
         }

         assert packetLength > 0;

         if(packetLength > readableBytes) {
            this.packetLength = packetLength;
            break;
         }

         newTotalLength = totalLength + packetLength;
         if(newTotalLength > 18713) {
            break;
         }

         offset += packetLength;
      }

      if(totalLength > 0) {
         in.skipBytes(totalLength);
         ByteBuffer inNetBuf = in.nioBuffer(startOffset, totalLength);
         this.unwrap(ctx, inNetBuf, totalLength);

         assert !inNetBuf.hasRemaining() || this.engine.isInboundDone();
      }

      if(nonSslRecord) {
         NotSslRecordException e = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(in));
         in.skipBytes(in.readableBytes());
         ctx.fireExceptionCaught(e);
         this.setHandshakeFailure(e);
      }

   }

   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      if(this.needsFlush) {
         this.needsFlush = false;
         ctx.flush();
      }

      super.channelReadComplete(ctx);
   }

   private void unwrapNonAppData(ChannelHandlerContext ctx) throws SSLException {
      this.unwrap(ctx, Unpooled.EMPTY_BUFFER.nioBuffer(), 0);
   }

   private void unwrap(ChannelHandlerContext ctx, ByteBuffer packet, int initialOutAppBufCapacity) throws SSLException {
      int oldPos = packet.position();
      ByteBuffer oldPacket;
      ByteBuf newPacket;
      if(this.wantsInboundHeapBuffer && packet.isDirect()) {
         newPacket = ctx.alloc().heapBuffer(packet.limit() - oldPos);
         newPacket.writeBytes(packet);
         oldPacket = packet;
         packet = newPacket.nioBuffer();
      } else {
         oldPacket = null;
         newPacket = null;
      }

      boolean wrapLater = false;
      ByteBuf decodeOut = this.allocate(ctx, initialOutAppBufCapacity);

      try {
         while(true) {
            SSLEngineResult result = unwrap(this.engine, packet, decodeOut);
            Status status = result.getStatus();
            HandshakeStatus handshakeStatus = result.getHandshakeStatus();
            int produced = result.bytesProduced();
            int consumed = result.bytesConsumed();
            if(status == Status.CLOSED) {
               this.sslCloseFuture.trySuccess(ctx.channel());
            } else {
               switch(handshakeStatus) {
               case NEED_TASK:
                  this.runDelegatedTasks();
                  break;
               case FINISHED:
                  this.setHandshakeSuccess();
                  wrapLater = true;
                  continue;
               case NOT_HANDSHAKING:
                  if(this.setHandshakeSuccessIfStillHandshaking()) {
                     wrapLater = true;
                     continue;
                  }

                  if(this.flushedBeforeHandshakeDone) {
                     this.flushedBeforeHandshakeDone = false;
                     wrapLater = true;
                  }
                  break;
               case NEED_WRAP:
                  this.wrapNonAppData(ctx, true);
               case NEED_UNWRAP:
                  break;
               default:
                  throw new IllegalStateException("Unknown handshake status: " + handshakeStatus);
               }

               if(status != Status.BUFFER_UNDERFLOW && (consumed != 0 || produced != 0)) {
                  continue;
               }
            }

            if(wrapLater) {
               this.wrap(ctx, true);
            }
            break;
         }
      } catch (SSLException var17) {
         this.setHandshakeFailure(var17);
         throw var17;
      } finally {
         if(newPacket != null) {
            oldPacket.position(oldPos + packet.position());
            newPacket.release();
         }

         if(decodeOut.isReadable()) {
            ctx.fireChannelRead(decodeOut);
         } else {
            decodeOut.release();
         }

      }

   }

   private static SSLEngineResult unwrap(SSLEngine engine, ByteBuffer in, ByteBuf out) throws SSLException {
      int overflows = 0;

      while(true) {
         ByteBuffer out0 = out.nioBuffer(out.writerIndex(), out.writableBytes());
         SSLEngineResult result = engine.unwrap(in, out0);
         out.writerIndex(out.writerIndex() + result.bytesProduced());
         switch(result.getStatus()) {
         case BUFFER_OVERFLOW:
            int max = engine.getSession().getApplicationBufferSize();
            switch(overflows++) {
            case 0:
               out.ensureWritable(Math.min(max, in.remaining()));
               continue;
            default:
               out.ensureWritable(max);
               continue;
            }
         default:
            return result;
         }
      }
   }

   private void runDelegatedTasks() {
      if(this.delegatedTaskExecutor == ImmediateExecutor.INSTANCE) {
         while(true) {
            Runnable task = this.engine.getDelegatedTask();
            if(task == null) {
               break;
            }

            task.run();
         }
      } else {
         final List<Runnable> tasks = new ArrayList(2);

         while(true) {
            Runnable task = this.engine.getDelegatedTask();
            if(task == null) {
               if(tasks.isEmpty()) {
                  return;
               }

               final CountDownLatch latch = new CountDownLatch(1);
               this.delegatedTaskExecutor.execute(new Runnable() {
                  public void run() {
                     try {
                        for(Runnable task : tasks) {
                           task.run();
                        }
                     } catch (Exception var6) {
                        SslHandler.this.ctx.fireExceptionCaught(var6);
                     } finally {
                        latch.countDown();
                     }

                  }
               });
               boolean interrupted = false;

               while(latch.getCount() != 0L) {
                  try {
                     latch.await();
                  } catch (InterruptedException var5) {
                     interrupted = true;
                  }
               }

               if(interrupted) {
                  Thread.currentThread().interrupt();
               }
               break;
            }

            tasks.add(task);
         }
      }

   }

   private boolean setHandshakeSuccessIfStillHandshaking() {
      if(!this.handshakePromise.isDone()) {
         this.setHandshakeSuccess();
         return true;
      } else {
         return false;
      }
   }

   private void setHandshakeSuccess() {
      String cipherSuite = String.valueOf(this.engine.getSession().getCipherSuite());
      if(!this.wantsDirectBuffer && (cipherSuite.contains("_GCM_") || cipherSuite.contains("-GCM-"))) {
         this.wantsInboundHeapBuffer = true;
      }

      if(this.handshakePromise.trySuccess(this.ctx.channel())) {
         if(logger.isDebugEnabled()) {
            logger.debug(this.ctx.channel() + " HANDSHAKEN: " + this.engine.getSession().getCipherSuite());
         }

         this.ctx.fireUserEventTriggered(SslHandshakeCompletionEvent.SUCCESS);
      }

   }

   private void setHandshakeFailure(Throwable cause) {
      this.engine.closeOutbound();

      try {
         this.engine.closeInbound();
      } catch (SSLException var4) {
         String msg = var4.getMessage();
         if(msg == null || !msg.contains("possible truncation attack")) {
            logger.debug("SSLEngine.closeInbound() raised an exception.", (Throwable)var4);
         }
      }

      this.notifyHandshakeFailure(cause);
      this.pendingUnencryptedWrites.removeAndFailAll(cause);
   }

   private void notifyHandshakeFailure(Throwable cause) {
      if(this.handshakePromise.tryFailure(cause)) {
         this.ctx.fireUserEventTriggered(new SslHandshakeCompletionEvent(cause));
         this.ctx.close();
      }

   }

   private void closeOutboundAndChannel(ChannelHandlerContext ctx, ChannelPromise promise, boolean disconnect) throws Exception {
      if(!ctx.channel().isActive()) {
         if(disconnect) {
            ctx.disconnect(promise);
         } else {
            ctx.close(promise);
         }

      } else {
         this.engine.closeOutbound();
         ChannelPromise closeNotifyFuture = ctx.newPromise();
         this.write(ctx, Unpooled.EMPTY_BUFFER, closeNotifyFuture);
         this.flush(ctx);
         this.safeClose(ctx, closeNotifyFuture, promise);
      }
   }

   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      this.ctx = ctx;
      this.pendingUnencryptedWrites = new PendingWriteQueue(ctx);
      if(ctx.channel().isActive() && this.engine.getUseClientMode()) {
         this.handshake();
      }

   }

   private Future handshake() {
      final ScheduledFuture<?> timeoutFuture;
      if(this.handshakeTimeoutMillis > 0L) {
         timeoutFuture = this.ctx.executor().schedule(new Runnable() {
            public void run() {
               if(!SslHandler.this.handshakePromise.isDone()) {
                  SslHandler.this.notifyHandshakeFailure(SslHandler.HANDSHAKE_TIMED_OUT);
               }
            }
         }, this.handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
      } else {
         timeoutFuture = null;
      }

      this.handshakePromise.addListener(new GenericFutureListener() {
         public void operationComplete(Future f) throws Exception {
            if(timeoutFuture != null) {
               timeoutFuture.cancel(false);
            }

         }
      });

      try {
         this.engine.beginHandshake();
         this.wrapNonAppData(this.ctx, false);
         this.ctx.flush();
      } catch (Exception var3) {
         this.notifyHandshakeFailure(var3);
      }

      return this.handshakePromise;
   }

   public void channelActive(final ChannelHandlerContext ctx) throws Exception {
      if(!this.startTls && this.engine.getUseClientMode()) {
         this.handshake().addListener(new GenericFutureListener() {
            public void operationComplete(Future future) throws Exception {
               if(!future.isSuccess()) {
                  SslHandler.logger.debug("Failed to complete handshake", future.cause());
                  ctx.close();
               }

            }
         });
      }

      ctx.fireChannelActive();
   }

   private void safeClose(final ChannelHandlerContext ctx, ChannelFuture flushFuture, final ChannelPromise promise) {
      if(!ctx.channel().isActive()) {
         ctx.close(promise);
      } else {
         final ScheduledFuture<?> timeoutFuture;
         if(this.closeNotifyTimeoutMillis > 0L) {
            timeoutFuture = ctx.executor().schedule(new Runnable() {
               public void run() {
                  SslHandler.logger.warn(ctx.channel() + " last write attempt timed out." + " Force-closing the connection.");
                  ctx.close(promise);
               }
            }, this.closeNotifyTimeoutMillis, TimeUnit.MILLISECONDS);
         } else {
            timeoutFuture = null;
         }

         flushFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture f) throws Exception {
               if(timeoutFuture != null) {
                  timeoutFuture.cancel(false);
               }

               ctx.close(promise);
            }
         });
      }
   }

   private ByteBuf allocate(ChannelHandlerContext ctx, int capacity) {
      ByteBufAllocator alloc = ctx.alloc();
      return this.wantsDirectBuffer?alloc.directBuffer(capacity):alloc.buffer(capacity);
   }

   private ByteBuf allocateOutNetBuf(ChannelHandlerContext ctx, int pendingBytes) {
      return this.wantsLargeOutboundNetworkBuffer?this.allocate(ctx, this.maxPacketBufferSize):this.allocate(ctx, Math.min(pendingBytes + 2329, this.maxPacketBufferSize));
   }

   static {
      SSLENGINE_CLOSED.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
      HANDSHAKE_TIMED_OUT.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
      CHANNEL_CLOSED.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
   }

   private final class LazyChannelPromise extends DefaultPromise {
      private LazyChannelPromise() {
      }

      protected EventExecutor executor() {
         if(SslHandler.this.ctx == null) {
            throw new IllegalStateException();
         } else {
            return SslHandler.this.ctx.executor();
         }
      }
   }
}
