package io.netty.handler.codec.rtsp;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.rtsp.RtspObjectEncoder;
import io.netty.util.CharsetUtil;

public class RtspRequestEncoder extends RtspObjectEncoder {
   private static final byte[] CRLF = new byte[]{(byte)13, (byte)10};

   public boolean acceptOutboundMessage(Object msg) throws Exception {
      return msg instanceof FullHttpRequest;
   }

   protected void encodeInitialLine(ByteBuf buf, HttpRequest request) throws Exception {
      HttpHeaders.encodeAscii(request.getMethod().toString(), buf);
      buf.writeByte(32);
      buf.writeBytes(request.getUri().getBytes(CharsetUtil.UTF_8));
      buf.writeByte(32);
      encodeAscii(request.getProtocolVersion().toString(), buf);
      buf.writeBytes(CRLF);
   }
}
