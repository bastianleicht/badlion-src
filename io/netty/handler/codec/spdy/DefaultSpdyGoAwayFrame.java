package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.SpdyGoAwayFrame;
import io.netty.handler.codec.spdy.SpdySessionStatus;
import io.netty.util.internal.StringUtil;

public class DefaultSpdyGoAwayFrame implements SpdyGoAwayFrame {
   private int lastGoodStreamId;
   private SpdySessionStatus status;

   public DefaultSpdyGoAwayFrame(int lastGoodStreamId) {
      this(lastGoodStreamId, 0);
   }

   public DefaultSpdyGoAwayFrame(int lastGoodStreamId, int statusCode) {
      this(lastGoodStreamId, SpdySessionStatus.valueOf(statusCode));
   }

   public DefaultSpdyGoAwayFrame(int lastGoodStreamId, SpdySessionStatus status) {
      this.setLastGoodStreamId(lastGoodStreamId);
      this.setStatus(status);
   }

   public int lastGoodStreamId() {
      return this.lastGoodStreamId;
   }

   public SpdyGoAwayFrame setLastGoodStreamId(int lastGoodStreamId) {
      if(lastGoodStreamId < 0) {
         throw new IllegalArgumentException("Last-good-stream-ID cannot be negative: " + lastGoodStreamId);
      } else {
         this.lastGoodStreamId = lastGoodStreamId;
         return this;
      }
   }

   public SpdySessionStatus status() {
      return this.status;
   }

   public SpdyGoAwayFrame setStatus(SpdySessionStatus status) {
      this.status = status;
      return this;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(StringUtil.simpleClassName((Object)this));
      buf.append(StringUtil.NEWLINE);
      buf.append("--> Last-good-stream-ID = ");
      buf.append(this.lastGoodStreamId());
      buf.append(StringUtil.NEWLINE);
      buf.append("--> Status: ");
      buf.append(this.status());
      return buf.toString();
   }
}
