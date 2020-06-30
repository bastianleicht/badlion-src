package io.netty.handler.codec.spdy;

import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.spdy.SpdyDataFrame;
import io.netty.util.internal.PlatformDependent;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

final class SpdySession {
   private final AtomicInteger activeLocalStreams = new AtomicInteger();
   private final AtomicInteger activeRemoteStreams = new AtomicInteger();
   private final Map activeStreams = PlatformDependent.newConcurrentHashMap();
   private final SpdySession.StreamComparator streamComparator = new SpdySession.StreamComparator();
   private final AtomicInteger sendWindowSize;
   private final AtomicInteger receiveWindowSize;

   SpdySession(int sendWindowSize, int receiveWindowSize) {
      this.sendWindowSize = new AtomicInteger(sendWindowSize);
      this.receiveWindowSize = new AtomicInteger(receiveWindowSize);
   }

   int numActiveStreams(boolean remote) {
      return remote?this.activeRemoteStreams.get():this.activeLocalStreams.get();
   }

   boolean noActiveStreams() {
      return this.activeStreams.isEmpty();
   }

   boolean isActiveStream(int streamId) {
      return this.activeStreams.containsKey(Integer.valueOf(streamId));
   }

   Map activeStreams() {
      Map<Integer, SpdySession.StreamState> streams = new TreeMap(this.streamComparator);
      streams.putAll(this.activeStreams);
      return streams;
   }

   void acceptStream(int streamId, byte priority, boolean remoteSideClosed, boolean localSideClosed, int sendWindowSize, int receiveWindowSize, boolean remote) {
      if(!remoteSideClosed || !localSideClosed) {
         SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.put(Integer.valueOf(streamId), new SpdySession.StreamState(priority, remoteSideClosed, localSideClosed, sendWindowSize, receiveWindowSize));
         if(state == null) {
            if(remote) {
               this.activeRemoteStreams.incrementAndGet();
            } else {
               this.activeLocalStreams.incrementAndGet();
            }
         }
      }

   }

   private SpdySession.StreamState removeActiveStream(int streamId, boolean remote) {
      SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.remove(Integer.valueOf(streamId));
      if(state != null) {
         if(remote) {
            this.activeRemoteStreams.decrementAndGet();
         } else {
            this.activeLocalStreams.decrementAndGet();
         }
      }

      return state;
   }

   void removeStream(int streamId, Throwable cause, boolean remote) {
      SpdySession.StreamState state = this.removeActiveStream(streamId, remote);
      if(state != null) {
         state.clearPendingWrites(cause);
      }

   }

   boolean isRemoteSideClosed(int streamId) {
      SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.get(Integer.valueOf(streamId));
      return state == null || state.isRemoteSideClosed();
   }

   void closeRemoteSide(int streamId, boolean remote) {
      SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.get(Integer.valueOf(streamId));
      if(state != null) {
         state.closeRemoteSide();
         if(state.isLocalSideClosed()) {
            this.removeActiveStream(streamId, remote);
         }
      }

   }

   boolean isLocalSideClosed(int streamId) {
      SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.get(Integer.valueOf(streamId));
      return state == null || state.isLocalSideClosed();
   }

   void closeLocalSide(int streamId, boolean remote) {
      SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.get(Integer.valueOf(streamId));
      if(state != null) {
         state.closeLocalSide();
         if(state.isRemoteSideClosed()) {
            this.removeActiveStream(streamId, remote);
         }
      }

   }

   boolean hasReceivedReply(int streamId) {
      SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.get(Integer.valueOf(streamId));
      return state != null && state.hasReceivedReply();
   }

   void receivedReply(int streamId) {
      SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.get(Integer.valueOf(streamId));
      if(state != null) {
         state.receivedReply();
      }

   }

   int getSendWindowSize(int streamId) {
      if(streamId == 0) {
         return this.sendWindowSize.get();
      } else {
         SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.get(Integer.valueOf(streamId));
         return state != null?state.getSendWindowSize():-1;
      }
   }

   int updateSendWindowSize(int streamId, int deltaWindowSize) {
      if(streamId == 0) {
         return this.sendWindowSize.addAndGet(deltaWindowSize);
      } else {
         SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.get(Integer.valueOf(streamId));
         return state != null?state.updateSendWindowSize(deltaWindowSize):-1;
      }
   }

   int updateReceiveWindowSize(int streamId, int deltaWindowSize) {
      if(streamId == 0) {
         return this.receiveWindowSize.addAndGet(deltaWindowSize);
      } else {
         SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.get(Integer.valueOf(streamId));
         if(state == null) {
            return -1;
         } else {
            if(deltaWindowSize > 0) {
               state.setReceiveWindowSizeLowerBound(0);
            }

            return state.updateReceiveWindowSize(deltaWindowSize);
         }
      }
   }

   int getReceiveWindowSizeLowerBound(int streamId) {
      if(streamId == 0) {
         return 0;
      } else {
         SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.get(Integer.valueOf(streamId));
         return state != null?state.getReceiveWindowSizeLowerBound():0;
      }
   }

   void updateAllSendWindowSizes(int deltaWindowSize) {
      for(SpdySession.StreamState state : this.activeStreams.values()) {
         state.updateSendWindowSize(deltaWindowSize);
      }

   }

   void updateAllReceiveWindowSizes(int deltaWindowSize) {
      for(SpdySession.StreamState state : this.activeStreams.values()) {
         state.updateReceiveWindowSize(deltaWindowSize);
         if(deltaWindowSize < 0) {
            state.setReceiveWindowSizeLowerBound(deltaWindowSize);
         }
      }

   }

   boolean putPendingWrite(int streamId, SpdySession.PendingWrite pendingWrite) {
      SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.get(Integer.valueOf(streamId));
      return state != null && state.putPendingWrite(pendingWrite);
   }

   SpdySession.PendingWrite getPendingWrite(int streamId) {
      if(streamId == 0) {
         for(Entry<Integer, SpdySession.StreamState> e : this.activeStreams().entrySet()) {
            SpdySession.StreamState state = (SpdySession.StreamState)e.getValue();
            if(state.getSendWindowSize() > 0) {
               SpdySession.PendingWrite pendingWrite = state.getPendingWrite();
               if(pendingWrite != null) {
                  return pendingWrite;
               }
            }
         }

         return null;
      } else {
         SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.get(Integer.valueOf(streamId));
         return state != null?state.getPendingWrite():null;
      }
   }

   SpdySession.PendingWrite removePendingWrite(int streamId) {
      SpdySession.StreamState state = (SpdySession.StreamState)this.activeStreams.get(Integer.valueOf(streamId));
      return state != null?state.removePendingWrite():null;
   }

   public static final class PendingWrite {
      final SpdyDataFrame spdyDataFrame;
      final ChannelPromise promise;

      PendingWrite(SpdyDataFrame spdyDataFrame, ChannelPromise promise) {
         this.spdyDataFrame = spdyDataFrame;
         this.promise = promise;
      }

      void fail(Throwable cause) {
         this.spdyDataFrame.release();
         this.promise.setFailure(cause);
      }
   }

   private final class StreamComparator implements Comparator, Serializable {
      private static final long serialVersionUID = 1161471649740544848L;

      public int compare(Integer id1, Integer id2) {
         SpdySession.StreamState state1 = (SpdySession.StreamState)SpdySession.this.activeStreams.get(id1);
         SpdySession.StreamState state2 = (SpdySession.StreamState)SpdySession.this.activeStreams.get(id2);
         int result = state1.getPriority() - state2.getPriority();
         return result != 0?result:id1.intValue() - id2.intValue();
      }
   }

   private static final class StreamState {
      private final byte priority;
      private boolean remoteSideClosed;
      private boolean localSideClosed;
      private boolean receivedReply;
      private final AtomicInteger sendWindowSize;
      private final AtomicInteger receiveWindowSize;
      private int receiveWindowSizeLowerBound;
      private final Queue pendingWriteQueue = new ConcurrentLinkedQueue();

      StreamState(byte priority, boolean remoteSideClosed, boolean localSideClosed, int sendWindowSize, int receiveWindowSize) {
         this.priority = priority;
         this.remoteSideClosed = remoteSideClosed;
         this.localSideClosed = localSideClosed;
         this.sendWindowSize = new AtomicInteger(sendWindowSize);
         this.receiveWindowSize = new AtomicInteger(receiveWindowSize);
      }

      byte getPriority() {
         return this.priority;
      }

      boolean isRemoteSideClosed() {
         return this.remoteSideClosed;
      }

      void closeRemoteSide() {
         this.remoteSideClosed = true;
      }

      boolean isLocalSideClosed() {
         return this.localSideClosed;
      }

      void closeLocalSide() {
         this.localSideClosed = true;
      }

      boolean hasReceivedReply() {
         return this.receivedReply;
      }

      void receivedReply() {
         this.receivedReply = true;
      }

      int getSendWindowSize() {
         return this.sendWindowSize.get();
      }

      int updateSendWindowSize(int deltaWindowSize) {
         return this.sendWindowSize.addAndGet(deltaWindowSize);
      }

      int updateReceiveWindowSize(int deltaWindowSize) {
         return this.receiveWindowSize.addAndGet(deltaWindowSize);
      }

      int getReceiveWindowSizeLowerBound() {
         return this.receiveWindowSizeLowerBound;
      }

      void setReceiveWindowSizeLowerBound(int receiveWindowSizeLowerBound) {
         this.receiveWindowSizeLowerBound = receiveWindowSizeLowerBound;
      }

      boolean putPendingWrite(SpdySession.PendingWrite msg) {
         return this.pendingWriteQueue.offer(msg);
      }

      SpdySession.PendingWrite getPendingWrite() {
         return (SpdySession.PendingWrite)this.pendingWriteQueue.peek();
      }

      SpdySession.PendingWrite removePendingWrite() {
         return (SpdySession.PendingWrite)this.pendingWriteQueue.poll();
      }

      void clearPendingWrites(Throwable cause) {
         while(true) {
            SpdySession.PendingWrite pendingWrite = (SpdySession.PendingWrite)this.pendingWriteQueue.poll();
            if(pendingWrite == null) {
               return;
            }

            pendingWrite.fail(cause);
         }
      }
   }
}
