package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyStreamFrame;

public abstract class DefaultSpdyStreamFrame implements SpdyStreamFrame {
   private int streamId;
   private boolean last;

   protected DefaultSpdyStreamFrame(int streamId) {
      this.setStreamId(streamId);
   }

   public int streamId() {
      return this.streamId;
   }

   public SpdyStreamFrame setStreamId(int streamId) {
      if(streamId <= 0) {
         throw new IllegalArgumentException("Stream-ID must be positive: " + streamId);
      } else {
         this.streamId = streamId;
         return this;
      }
   }

   public boolean isLast() {
      return this.last;
   }

   public SpdyStreamFrame setLast(boolean last) {
      this.last = last;
      return this;
   }
}
