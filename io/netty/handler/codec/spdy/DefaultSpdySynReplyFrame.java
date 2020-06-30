package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.DefaultSpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdySynReplyFrame;
import io.netty.util.internal.StringUtil;

public class DefaultSpdySynReplyFrame extends DefaultSpdyHeadersFrame implements SpdySynReplyFrame {
   public DefaultSpdySynReplyFrame(int streamId) {
      super(streamId);
   }

   public SpdySynReplyFrame setStreamId(int streamId) {
      super.setStreamId(streamId);
      return this;
   }

   public SpdySynReplyFrame setLast(boolean last) {
      super.setLast(last);
      return this;
   }

   public SpdySynReplyFrame setInvalid() {
      super.setInvalid();
      return this;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(StringUtil.simpleClassName((Object)this));
      buf.append("(last: ");
      buf.append(this.isLast());
      buf.append(')');
      buf.append(StringUtil.NEWLINE);
      buf.append("--> Stream-ID = ");
      buf.append(this.streamId());
      buf.append(StringUtil.NEWLINE);
      buf.append("--> Headers:");
      buf.append(StringUtil.NEWLINE);
      this.appendHeaders(buf);
      buf.setLength(buf.length() - StringUtil.NEWLINE.length());
      return buf.toString();
   }
}
