package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectDecoder;

public abstract class RtspObjectDecoder extends HttpObjectDecoder {
   protected RtspObjectDecoder() {
      this(4096, 8192, 8192);
   }

   protected RtspObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength) {
      super(maxInitialLineLength, maxHeaderSize, maxContentLength * 2, false);
   }

   protected RtspObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength, boolean validateHeaders) {
      super(maxInitialLineLength, maxHeaderSize, maxContentLength * 2, false, validateHeaders);
   }

   protected boolean isContentAlwaysEmpty(HttpMessage msg) {
      boolean empty = super.isContentAlwaysEmpty(msg);
      return empty?true:(!msg.headers().contains("Content-Length")?true:empty);
   }
}
