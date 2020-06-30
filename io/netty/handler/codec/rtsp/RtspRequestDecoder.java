package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.handler.codec.rtsp.RtspObjectDecoder;
import io.netty.handler.codec.rtsp.RtspVersions;

public class RtspRequestDecoder extends RtspObjectDecoder {
   public RtspRequestDecoder() {
   }

   public RtspRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength) {
      super(maxInitialLineLength, maxHeaderSize, maxContentLength);
   }

   public RtspRequestDecoder(int maxInitialLineLength, int maxHeaderSize, int maxContentLength, boolean validateHeaders) {
      super(maxInitialLineLength, maxHeaderSize, maxContentLength, validateHeaders);
   }

   protected HttpMessage createMessage(String[] initialLine) throws Exception {
      return new DefaultHttpRequest(RtspVersions.valueOf(initialLine[2]), RtspMethods.valueOf(initialLine[0]), initialLine[1], this.validateHeaders);
   }

   protected HttpMessage createInvalidMessage() {
      return new DefaultHttpRequest(RtspVersions.RTSP_1_0, RtspMethods.OPTIONS, "/bad-request", this.validateHeaders);
   }

   protected boolean isDecodingRequest() {
      return true;
   }
}
