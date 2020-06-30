package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.DefaultSpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdySynStreamFrame;
import io.netty.util.internal.StringUtil;

public class DefaultSpdySynStreamFrame extends DefaultSpdyHeadersFrame implements SpdySynStreamFrame {
   private int associatedStreamId;
   private byte priority;
   private boolean unidirectional;

   public DefaultSpdySynStreamFrame(int streamId, int associatedStreamId, byte priority) {
      super(streamId);
      this.setAssociatedStreamId(associatedStreamId);
      this.setPriority(priority);
   }

   public SpdySynStreamFrame setStreamId(int streamId) {
      super.setStreamId(streamId);
      return this;
   }

   public SpdySynStreamFrame setLast(boolean last) {
      super.setLast(last);
      return this;
   }

   public SpdySynStreamFrame setInvalid() {
      super.setInvalid();
      return this;
   }

   public int associatedStreamId() {
      return this.associatedStreamId;
   }

   public SpdySynStreamFrame setAssociatedStreamId(int associatedStreamId) {
      if(associatedStreamId < 0) {
         throw new IllegalArgumentException("Associated-To-Stream-ID cannot be negative: " + associatedStreamId);
      } else {
         this.associatedStreamId = associatedStreamId;
         return this;
      }
   }

   public byte priority() {
      return this.priority;
   }

   public SpdySynStreamFrame setPriority(byte priority) {
      if(priority >= 0 && priority <= 7) {
         this.priority = priority;
         return this;
      } else {
         throw new IllegalArgumentException("Priority must be between 0 and 7 inclusive: " + priority);
      }
   }

   public boolean isUnidirectional() {
      return this.unidirectional;
   }

   public SpdySynStreamFrame setUnidirectional(boolean unidirectional) {
      this.unidirectional = unidirectional;
      return this;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(StringUtil.simpleClassName((Object)this));
      buf.append("(last: ");
      buf.append(this.isLast());
      buf.append("; unidirectional: ");
      buf.append(this.isUnidirectional());
      buf.append(')');
      buf.append(StringUtil.NEWLINE);
      buf.append("--> Stream-ID = ");
      buf.append(this.streamId());
      buf.append(StringUtil.NEWLINE);
      if(this.associatedStreamId != 0) {
         buf.append("--> Associated-To-Stream-ID = ");
         buf.append(this.associatedStreamId());
         buf.append(StringUtil.NEWLINE);
      }

      buf.append("--> Priority = ");
      buf.append(this.priority());
      buf.append(StringUtil.NEWLINE);
      buf.append("--> Headers:");
      buf.append(StringUtil.NEWLINE);
      this.appendHeaders(buf);
      buf.setLength(buf.length() - StringUtil.NEWLINE.length());
      return buf.toString();
   }
}
