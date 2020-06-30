package io.netty.handler.codec.rtsp;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.rtsp.RtspObjectEncoder;
import io.netty.util.CharsetUtil;

public class RtspResponseEncoder extends RtspObjectEncoder {
   private static final byte[] CRLF = new byte[]{(byte)13, (byte)10};

   public boolean acceptOutboundMessage(Object msg) throws Exception {
      return msg instanceof FullHttpResponse;
   }

   protected void encodeInitialLine(ByteBuf buf, HttpResponse response) throws Exception {
      HttpHeaders.encodeAscii(response.getProtocolVersion().toString(), buf);
      buf.writeByte(32);
      buf.writeBytes(String.valueOf(response.getStatus().code()).getBytes(CharsetUtil.US_ASCII));
      buf.writeByte(32);
      encodeAscii(String.valueOf(response.getStatus().reasonPhrase()), buf);
      buf.writeBytes(CRLF);
   }
}
