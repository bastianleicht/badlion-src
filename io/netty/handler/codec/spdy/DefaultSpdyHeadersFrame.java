package io.netty.handler.codec.spdy;

import io.netty.handler.codec.spdy.DefaultSpdyHeaders;
import io.netty.handler.codec.spdy.DefaultSpdyStreamFrame;
import io.netty.handler.codec.spdy.SpdyHeaders;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.util.internal.StringUtil;
import java.util.Map.Entry;

public class DefaultSpdyHeadersFrame extends DefaultSpdyStreamFrame implements SpdyHeadersFrame {
   private boolean invalid;
   private boolean truncated;
   private final SpdyHeaders headers = new DefaultSpdyHeaders();

   public DefaultSpdyHeadersFrame(int streamId) {
      super(streamId);
   }

   public SpdyHeadersFrame setStreamId(int streamId) {
      super.setStreamId(streamId);
      return this;
   }

   public SpdyHeadersFrame setLast(boolean last) {
      super.setLast(last);
      return this;
   }

   public boolean isInvalid() {
      return this.invalid;
   }

   public SpdyHeadersFrame setInvalid() {
      this.invalid = true;
      return this;
   }

   public boolean isTruncated() {
      return this.truncated;
   }

   public SpdyHeadersFrame setTruncated() {
      this.truncated = true;
      return this;
   }

   public SpdyHeaders headers() {
      return this.headers;
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

   protected void appendHeaders(StringBuilder buf) {
      for(Entry<String, String> e : this.headers()) {
         buf.append("    ");
         buf.append((String)e.getKey());
         buf.append(": ");
         buf.append((String)e.getValue());
         buf.append(StringUtil.NEWLINE);
      }

   }
}
