package io.netty.handler.codec.spdy;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.spdy.DefaultSpdyDataFrame;
import io.netty.handler.codec.spdy.DefaultSpdyGoAwayFrame;
import io.netty.handler.codec.spdy.DefaultSpdyRstStreamFrame;
import io.netty.handler.codec.spdy.DefaultSpdyWindowUpdateFrame;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyDataFrame;
import io.netty.handler.codec.spdy.SpdyGoAwayFrame;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyPingFrame;
import io.netty.handler.codec.spdy.SpdyProtocolException;
import io.netty.handler.codec.spdy.SpdyRstStreamFrame;
import io.netty.handler.codec.spdy.SpdySession;
import io.netty.handler.codec.spdy.SpdySessionStatus;
import io.netty.handler.codec.spdy.SpdySettingsFrame;
import io.netty.handler.codec.spdy.SpdyStreamStatus;
import io.netty.handler.codec.spdy.SpdySynReplyFrame;
import io.netty.handler.codec.spdy.SpdySynStreamFrame;
import io.netty.handler.codec.spdy.SpdyVersion;
import io.netty.handler.codec.spdy.SpdyWindowUpdateFrame;
import io.netty.util.internal.EmptyArrays;
import java.util.concurrent.atomic.AtomicInteger;

public class SpdySessionHandler extends ChannelDuplexHandler {
   private static final SpdyProtocolException PROTOCOL_EXCEPTION = new SpdyProtocolException();
   private static final SpdyProtocolException STREAM_CLOSED = new SpdyProtocolException("Stream closed");
   private static final int DEFAULT_WINDOW_SIZE = 65536;
   private int initialSendWindowSize = 65536;
   private int initialReceiveWindowSize = 65536;
   private volatile int initialSessionReceiveWindowSize = 65536;
   private final SpdySession spdySession;
   private int lastGoodStreamId;
   private static final int DEFAULT_MAX_CONCURRENT_STREAMS = Integer.MAX_VALUE;
   private int remoteConcurrentStreams;
   private int localConcurrentStreams;
   private final AtomicInteger pings;
   private boolean sentGoAwayFrame;
   private boolean receivedGoAwayFrame;
   private ChannelFutureListener closeSessionFutureListener;
   private final boolean server;
   private final int minorVersion;

   public SpdySessionHandler(SpdyVersion version, boolean server) {
      this.spdySession = new SpdySession(this.initialSendWindowSize, this.initialReceiveWindowSize);
      this.remoteConcurrentStreams = Integer.MAX_VALUE;
      this.localConcurrentStreams = Integer.MAX_VALUE;
      this.pings = new AtomicInteger();
      if(version == null) {
         throw new NullPointerException("version");
      } else {
         this.server = server;
         this.minorVersion = version.getMinorVersion();
      }
   }

   public void setSessionReceiveWindowSize(int sessionReceiveWindowSize) {
      if(sessionReceiveWindowSize < 0) {
         throw new IllegalArgumentException("sessionReceiveWindowSize");
      } else {
         this.initialSessionReceiveWindowSize = sessionReceiveWindowSize;
      }
   }

   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if(msg instanceof SpdyDataFrame) {
         SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
         int streamId = spdyDataFrame.streamId();
         int deltaWindowSize = -1 * spdyDataFrame.content().readableBytes();
         int newSessionWindowSize = this.spdySession.updateReceiveWindowSize(0, deltaWindowSize);
         if(newSessionWindowSize < 0) {
            this.issueSessionError(ctx, SpdySessionStatus.PROTOCOL_ERROR);
            return;
         }

         if(newSessionWindowSize <= this.initialSessionReceiveWindowSize / 2) {
            int sessionDeltaWindowSize = this.initialSessionReceiveWindowSize - newSessionWindowSize;
            this.spdySession.updateReceiveWindowSize(0, sessionDeltaWindowSize);
            SpdyWindowUpdateFrame spdyWindowUpdateFrame = new DefaultSpdyWindowUpdateFrame(0, sessionDeltaWindowSize);
            ctx.writeAndFlush(spdyWindowUpdateFrame);
         }

         if(!this.spdySession.isActiveStream(streamId)) {
            spdyDataFrame.release();
            if(streamId <= this.lastGoodStreamId) {
               this.issueStreamError(ctx, streamId, SpdyStreamStatus.PROTOCOL_ERROR);
            } else if(!this.sentGoAwayFrame) {
               this.issueStreamError(ctx, streamId, SpdyStreamStatus.INVALID_STREAM);
            }

            return;
         }

         if(this.spdySession.isRemoteSideClosed(streamId)) {
            spdyDataFrame.release();
            this.issueStreamError(ctx, streamId, SpdyStreamStatus.STREAM_ALREADY_CLOSED);
            return;
         }

         if(!this.isRemoteInitiatedId(streamId) && !this.spdySession.hasReceivedReply(streamId)) {
            spdyDataFrame.release();
            this.issueStreamError(ctx, streamId, SpdyStreamStatus.PROTOCOL_ERROR);
            return;
         }

         int newWindowSize = this.spdySession.updateReceiveWindowSize(streamId, deltaWindowSize);
         if(newWindowSize < this.spdySession.getReceiveWindowSizeLowerBound(streamId)) {
            spdyDataFrame.release();
            this.issueStreamError(ctx, streamId, SpdyStreamStatus.FLOW_CONTROL_ERROR);
            return;
         }

         if(newWindowSize < 0) {
            while(spdyDataFrame.content().readableBytes() > this.initialReceiveWindowSize) {
               SpdyDataFrame partialDataFrame = new DefaultSpdyDataFrame(streamId, spdyDataFrame.content().readSlice(this.initialReceiveWindowSize).retain());
               ctx.writeAndFlush(partialDataFrame);
            }
         }

         if(newWindowSize <= this.initialReceiveWindowSize / 2 && !spdyDataFrame.isLast()) {
            int streamDeltaWindowSize = this.initialReceiveWindowSize - newWindowSize;
            this.spdySession.updateReceiveWindowSize(streamId, streamDeltaWindowSize);
            SpdyWindowUpdateFrame spdyWindowUpdateFrame = new DefaultSpdyWindowUpdateFrame(streamId, streamDeltaWindowSize);
            ctx.writeAndFlush(spdyWindowUpdateFrame);
         }

         if(spdyDataFrame.isLast()) {
            this.halfCloseStream(streamId, true, ctx.newSucceededFuture());
         }
      } else if(msg instanceof SpdySynStreamFrame) {
         SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
         int streamId = spdySynStreamFrame.streamId();
         if(spdySynStreamFrame.isInvalid() || !this.isRemoteInitiatedId(streamId) || this.spdySession.isActiveStream(streamId)) {
            this.issueStreamError(ctx, streamId, SpdyStreamStatus.PROTOCOL_ERROR);
            return;
         }

         if(streamId <= this.lastGoodStreamId) {
            this.issueSessionError(ctx, SpdySessionStatus.PROTOCOL_ERROR);
            return;
         }

         byte priority = spdySynStreamFrame.priority();
         boolean remoteSideClosed = spdySynStreamFrame.isLast();
         boolean localSideClosed = spdySynStreamFrame.isUnidirectional();
         if(!this.acceptStream(streamId, priority, remoteSideClosed, localSideClosed)) {
            this.issueStreamError(ctx, streamId, SpdyStreamStatus.REFUSED_STREAM);
            return;
         }
      } else if(msg instanceof SpdySynReplyFrame) {
         SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
         int streamId = spdySynReplyFrame.streamId();
         if(spdySynReplyFrame.isInvalid() || this.isRemoteInitiatedId(streamId) || this.spdySession.isRemoteSideClosed(streamId)) {
            this.issueStreamError(ctx, streamId, SpdyStreamStatus.INVALID_STREAM);
            return;
         }

         if(this.spdySession.hasReceivedReply(streamId)) {
            this.issueStreamError(ctx, streamId, SpdyStreamStatus.STREAM_IN_USE);
            return;
         }

         this.spdySession.receivedReply(streamId);
         if(spdySynReplyFrame.isLast()) {
            this.halfCloseStream(streamId, true, ctx.newSucceededFuture());
         }
      } else if(msg instanceof SpdyRstStreamFrame) {
         SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
         this.removeStream(spdyRstStreamFrame.streamId(), ctx.newSucceededFuture());
      } else if(msg instanceof SpdySettingsFrame) {
         SpdySettingsFrame spdySettingsFrame = (SpdySettingsFrame)msg;
         int settingsMinorVersion = spdySettingsFrame.getValue(0);
         if(settingsMinorVersion >= 0 && settingsMinorVersion != this.minorVersion) {
            this.issueSessionError(ctx, SpdySessionStatus.PROTOCOL_ERROR);
            return;
         }

         int newConcurrentStreams = spdySettingsFrame.getValue(4);
         if(newConcurrentStreams >= 0) {
            this.remoteConcurrentStreams = newConcurrentStreams;
         }

         if(spdySettingsFrame.isPersisted(7)) {
            spdySettingsFrame.removeValue(7);
         }

         spdySettingsFrame.setPersistValue(7, false);
         int newInitialWindowSize = spdySettingsFrame.getValue(7);
         if(newInitialWindowSize >= 0) {
            this.updateInitialSendWindowSize(newInitialWindowSize);
         }
      } else if(msg instanceof SpdyPingFrame) {
         SpdyPingFrame spdyPingFrame = (SpdyPingFrame)msg;
         if(this.isRemoteInitiatedId(spdyPingFrame.id())) {
            ctx.writeAndFlush(spdyPingFrame);
            return;
         }

         if(this.pings.get() == 0) {
            return;
         }

         this.pings.getAndDecrement();
      } else if(msg instanceof SpdyGoAwayFrame) {
         this.receivedGoAwayFrame = true;
      } else if(msg instanceof SpdyHeadersFrame) {
         SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
         int streamId = spdyHeadersFrame.streamId();
         if(spdyHeadersFrame.isInvalid()) {
            this.issueStreamError(ctx, streamId, SpdyStreamStatus.PROTOCOL_ERROR);
            return;
         }

         if(this.spdySession.isRemoteSideClosed(streamId)) {
            this.issueStreamError(ctx, streamId, SpdyStreamStatus.INVALID_STREAM);
            return;
         }

         if(spdyHeadersFrame.isLast()) {
            this.halfCloseStream(streamId, true, ctx.newSucceededFuture());
         }
      } else if(msg instanceof SpdyWindowUpdateFrame) {
         SpdyWindowUpdateFrame spdyWindowUpdateFrame = (SpdyWindowUpdateFrame)msg;
         int streamId = spdyWindowUpdateFrame.streamId();
         int deltaWindowSize = spdyWindowUpdateFrame.deltaWindowSize();
         if(streamId != 0 && this.spdySession.isLocalSideClosed(streamId)) {
            return;
         }

         if(this.spdySession.getSendWindowSize(streamId) > Integer.MAX_VALUE - deltaWindowSize) {
            if(streamId == 0) {
               this.issueSessionError(ctx, SpdySessionStatus.PROTOCOL_ERROR);
            } else {
               this.issueStreamError(ctx, streamId, SpdyStreamStatus.FLOW_CONTROL_ERROR);
            }

            return;
         }

         this.updateSendWindowSize(ctx, streamId, deltaWindowSize);
      }

      ctx.fireChannelRead(msg);
   }

   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      for(Integer streamId : this.spdySession.activeStreams().keySet()) {
         this.removeStream(streamId.intValue(), ctx.newSucceededFuture());
      }

      ctx.fireChannelInactive();
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      if(cause instanceof SpdyProtocolException) {
         this.issueSessionError(ctx, SpdySessionStatus.PROTOCOL_ERROR);
      }

      ctx.fireExceptionCaught(cause);
   }

   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
      this.sendGoAwayFrame(ctx, promise);
   }

   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if(!(msg instanceof SpdyDataFrame) && !(msg instanceof SpdySynStreamFrame) && !(msg instanceof SpdySynReplyFrame) && !(msg instanceof SpdyRstStreamFrame) && !(msg instanceof SpdySettingsFrame) && !(msg instanceof SpdyPingFrame) && !(msg instanceof SpdyGoAwayFrame) && !(msg instanceof SpdyHeadersFrame) && !(msg instanceof SpdyWindowUpdateFrame)) {
         ctx.write(msg, promise);
      } else {
         this.handleOutboundMessage(ctx, msg, promise);
      }

   }

   private void handleOutboundMessage(final ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if(msg instanceof SpdyDataFrame) {
         SpdyDataFrame spdyDataFrame = (SpdyDataFrame)msg;
         int streamId = spdyDataFrame.streamId();
         if(this.spdySession.isLocalSideClosed(streamId)) {
            spdyDataFrame.release();
            promise.setFailure(PROTOCOL_EXCEPTION);
            return;
         }

         int dataLength = spdyDataFrame.content().readableBytes();
         int sendWindowSize = this.spdySession.getSendWindowSize(streamId);
         int sessionSendWindowSize = this.spdySession.getSendWindowSize(0);
         sendWindowSize = Math.min(sendWindowSize, sessionSendWindowSize);
         if(sendWindowSize <= 0) {
            this.spdySession.putPendingWrite(streamId, new SpdySession.PendingWrite(spdyDataFrame, promise));
            return;
         }

         if(sendWindowSize < dataLength) {
            this.spdySession.updateSendWindowSize(streamId, -1 * sendWindowSize);
            this.spdySession.updateSendWindowSize(0, -1 * sendWindowSize);
            SpdyDataFrame partialDataFrame = new DefaultSpdyDataFrame(streamId, spdyDataFrame.content().readSlice(sendWindowSize).retain());
            this.spdySession.putPendingWrite(streamId, new SpdySession.PendingWrite(spdyDataFrame, promise));
            ctx.write(partialDataFrame).addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture future) throws Exception {
                  if(!future.isSuccess()) {
                     SpdySessionHandler.this.issueSessionError(ctx, SpdySessionStatus.INTERNAL_ERROR);
                  }

               }
            });
            return;
         }

         this.spdySession.updateSendWindowSize(streamId, -1 * dataLength);
         this.spdySession.updateSendWindowSize(0, -1 * dataLength);
         promise.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
               if(!future.isSuccess()) {
                  SpdySessionHandler.this.issueSessionError(ctx, SpdySessionStatus.INTERNAL_ERROR);
               }

            }
         });
         if(spdyDataFrame.isLast()) {
            this.halfCloseStream(streamId, false, promise);
         }
      } else if(msg instanceof SpdySynStreamFrame) {
         SpdySynStreamFrame spdySynStreamFrame = (SpdySynStreamFrame)msg;
         int streamId = spdySynStreamFrame.streamId();
         if(this.isRemoteInitiatedId(streamId)) {
            promise.setFailure(PROTOCOL_EXCEPTION);
            return;
         }

         byte priority = spdySynStreamFrame.priority();
         boolean remoteSideClosed = spdySynStreamFrame.isUnidirectional();
         boolean localSideClosed = spdySynStreamFrame.isLast();
         if(!this.acceptStream(streamId, priority, remoteSideClosed, localSideClosed)) {
            promise.setFailure(PROTOCOL_EXCEPTION);
            return;
         }
      } else if(msg instanceof SpdySynReplyFrame) {
         SpdySynReplyFrame spdySynReplyFrame = (SpdySynReplyFrame)msg;
         int streamId = spdySynReplyFrame.streamId();
         if(!this.isRemoteInitiatedId(streamId) || this.spdySession.isLocalSideClosed(streamId)) {
            promise.setFailure(PROTOCOL_EXCEPTION);
            return;
         }

         if(spdySynReplyFrame.isLast()) {
            this.halfCloseStream(streamId, false, promise);
         }
      } else if(msg instanceof SpdyRstStreamFrame) {
         SpdyRstStreamFrame spdyRstStreamFrame = (SpdyRstStreamFrame)msg;
         this.removeStream(spdyRstStreamFrame.streamId(), promise);
      } else if(msg instanceof SpdySettingsFrame) {
         SpdySettingsFrame spdySettingsFrame = (SpdySettingsFrame)msg;
         int settingsMinorVersion = spdySettingsFrame.getValue(0);
         if(settingsMinorVersion >= 0 && settingsMinorVersion != this.minorVersion) {
            promise.setFailure(PROTOCOL_EXCEPTION);
            return;
         }

         int newConcurrentStreams = spdySettingsFrame.getValue(4);
         if(newConcurrentStreams >= 0) {
            this.localConcurrentStreams = newConcurrentStreams;
         }

         if(spdySettingsFrame.isPersisted(7)) {
            spdySettingsFrame.removeValue(7);
         }

         spdySettingsFrame.setPersistValue(7, false);
         int newInitialWindowSize = spdySettingsFrame.getValue(7);
         if(newInitialWindowSize >= 0) {
            this.updateInitialReceiveWindowSize(newInitialWindowSize);
         }
      } else if(msg instanceof SpdyPingFrame) {
         SpdyPingFrame spdyPingFrame = (SpdyPingFrame)msg;
         if(this.isRemoteInitiatedId(spdyPingFrame.id())) {
            ctx.fireExceptionCaught(new IllegalArgumentException("invalid PING ID: " + spdyPingFrame.id()));
            return;
         }

         this.pings.getAndIncrement();
      } else {
         if(msg instanceof SpdyGoAwayFrame) {
            promise.setFailure(PROTOCOL_EXCEPTION);
            return;
         }

         if(msg instanceof SpdyHeadersFrame) {
            SpdyHeadersFrame spdyHeadersFrame = (SpdyHeadersFrame)msg;
            int streamId = spdyHeadersFrame.streamId();
            if(this.spdySession.isLocalSideClosed(streamId)) {
               promise.setFailure(PROTOCOL_EXCEPTION);
               return;
            }

            if(spdyHeadersFrame.isLast()) {
               this.halfCloseStream(streamId, false, promise);
            }
         } else if(msg instanceof SpdyWindowUpdateFrame) {
            promise.setFailure(PROTOCOL_EXCEPTION);
            return;
         }
      }

      ctx.write(msg, promise);
   }

   private void issueSessionError(ChannelHandlerContext ctx, SpdySessionStatus status) {
      this.sendGoAwayFrame(ctx, status).addListener(new SpdySessionHandler.ClosingChannelFutureListener(ctx, ctx.newPromise()));
   }

   private void issueStreamError(ChannelHandlerContext ctx, int streamId, SpdyStreamStatus status) {
      boolean fireChannelRead = !this.spdySession.isRemoteSideClosed(streamId);
      ChannelPromise promise = ctx.newPromise();
      this.removeStream(streamId, promise);
      SpdyRstStreamFrame spdyRstStreamFrame = new DefaultSpdyRstStreamFrame(streamId, status);
      ctx.writeAndFlush(spdyRstStreamFrame, promise);
      if(fireChannelRead) {
         ctx.fireChannelRead(spdyRstStreamFrame);
      }

   }

   private boolean isRemoteInitiatedId(int id) {
      boolean serverId = SpdyCodecUtil.isServerId(id);
      return this.server && !serverId || !this.server && serverId;
   }

   private synchronized void updateInitialSendWindowSize(int newInitialWindowSize) {
      int deltaWindowSize = newInitialWindowSize - this.initialSendWindowSize;
      this.initialSendWindowSize = newInitialWindowSize;
      this.spdySession.updateAllSendWindowSizes(deltaWindowSize);
   }

   private synchronized void updateInitialReceiveWindowSize(int newInitialWindowSize) {
      int deltaWindowSize = newInitialWindowSize - this.initialReceiveWindowSize;
      this.initialReceiveWindowSize = newInitialWindowSize;
      this.spdySession.updateAllReceiveWindowSizes(deltaWindowSize);
   }

   private synchronized boolean acceptStream(int streamId, byte priority, boolean remoteSideClosed, boolean localSideClosed) {
      if(!this.receivedGoAwayFrame && !this.sentGoAwayFrame) {
         boolean remote = this.isRemoteInitiatedId(streamId);
         int maxConcurrentStreams = remote?this.localConcurrentStreams:this.remoteConcurrentStreams;
         if(this.spdySession.numActiveStreams(remote) >= maxConcurrentStreams) {
            return false;
         } else {
            this.spdySession.acceptStream(streamId, priority, remoteSideClosed, localSideClosed, this.initialSendWindowSize, this.initialReceiveWindowSize, remote);
            if(remote) {
               this.lastGoodStreamId = streamId;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private void halfCloseStream(int streamId, boolean remote, ChannelFuture future) {
      if(remote) {
         this.spdySession.closeRemoteSide(streamId, this.isRemoteInitiatedId(streamId));
      } else {
         this.spdySession.closeLocalSide(streamId, this.isRemoteInitiatedId(streamId));
      }

      if(this.closeSessionFutureListener != null && this.spdySession.noActiveStreams()) {
         future.addListener(this.closeSessionFutureListener);
      }

   }

   private void removeStream(int streamId, ChannelFuture future) {
      this.spdySession.removeStream(streamId, STREAM_CLOSED, this.isRemoteInitiatedId(streamId));
      if(this.closeSessionFutureListener != null && this.spdySession.noActiveStreams()) {
         future.addListener(this.closeSessionFutureListener);
      }

   }

   private void updateSendWindowSize(final ChannelHandlerContext ctx, int streamId, int deltaWindowSize) {
      this.spdySession.updateSendWindowSize(streamId, deltaWindowSize);

      while(true) {
         SpdySession.PendingWrite pendingWrite = this.spdySession.getPendingWrite(streamId);
         if(pendingWrite == null) {
            return;
         }

         SpdyDataFrame spdyDataFrame = pendingWrite.spdyDataFrame;
         int dataFrameSize = spdyDataFrame.content().readableBytes();
         int writeStreamId = spdyDataFrame.streamId();
         int sendWindowSize = this.spdySession.getSendWindowSize(writeStreamId);
         int sessionSendWindowSize = this.spdySession.getSendWindowSize(0);
         sendWindowSize = Math.min(sendWindowSize, sessionSendWindowSize);
         if(sendWindowSize <= 0) {
            return;
         }

         if(sendWindowSize < dataFrameSize) {
            this.spdySession.updateSendWindowSize(writeStreamId, -1 * sendWindowSize);
            this.spdySession.updateSendWindowSize(0, -1 * sendWindowSize);
            SpdyDataFrame partialDataFrame = new DefaultSpdyDataFrame(writeStreamId, spdyDataFrame.content().readSlice(sendWindowSize).retain());
            ctx.writeAndFlush(partialDataFrame).addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture future) throws Exception {
                  if(!future.isSuccess()) {
                     SpdySessionHandler.this.issueSessionError(ctx, SpdySessionStatus.INTERNAL_ERROR);
                  }

               }
            });
         } else {
            this.spdySession.removePendingWrite(writeStreamId);
            this.spdySession.updateSendWindowSize(writeStreamId, -1 * dataFrameSize);
            this.spdySession.updateSendWindowSize(0, -1 * dataFrameSize);
            if(spdyDataFrame.isLast()) {
               this.halfCloseStream(writeStreamId, false, pendingWrite.promise);
            }

            ctx.writeAndFlush(spdyDataFrame, pendingWrite.promise).addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture future) throws Exception {
                  if(!future.isSuccess()) {
                     SpdySessionHandler.this.issueSessionError(ctx, SpdySessionStatus.INTERNAL_ERROR);
                  }

               }
            });
         }
      }
   }

   private void sendGoAwayFrame(ChannelHandlerContext ctx, ChannelPromise future) {
      if(!ctx.channel().isActive()) {
         ctx.close(future);
      } else {
         ChannelFuture f = this.sendGoAwayFrame(ctx, SpdySessionStatus.OK);
         if(this.spdySession.noActiveStreams()) {
            f.addListener(new SpdySessionHandler.ClosingChannelFutureListener(ctx, future));
         } else {
            this.closeSessionFutureListener = new SpdySessionHandler.ClosingChannelFutureListener(ctx, future);
         }

      }
   }

   private synchronized ChannelFuture sendGoAwayFrame(ChannelHandlerContext ctx, SpdySessionStatus status) {
      if(!this.sentGoAwayFrame) {
         this.sentGoAwayFrame = true;
         SpdyGoAwayFrame spdyGoAwayFrame = new DefaultSpdyGoAwayFrame(this.lastGoodStreamId, status);
         return ctx.writeAndFlush(spdyGoAwayFrame);
      } else {
         return ctx.newSucceededFuture();
      }
   }

   static {
      PROTOCOL_EXCEPTION.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
      STREAM_CLOSED.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
   }

   private static final class ClosingChannelFutureListener implements ChannelFutureListener {
      private final ChannelHandlerContext ctx;
      private final ChannelPromise promise;

      ClosingChannelFutureListener(ChannelHandlerContext ctx, ChannelPromise promise) {
         this.ctx = ctx;
         this.promise = promise;
      }

      public void operationComplete(ChannelFuture sentGoAwayFuture) throws Exception {
         this.ctx.close(this.promise);
      }
   }
}
