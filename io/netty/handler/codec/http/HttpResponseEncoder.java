package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpObjectEncoder;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public class HttpResponseEncoder extends HttpObjectEncoder {
   private static final byte[] CRLF = new byte[]{(byte)13, (byte)10};

   public boolean acceptOutboundMessage(Object msg) throws Exception {
      return super.acceptOutboundMessage(msg) && !(msg instanceof HttpRequest);
   }

   protected void encodeInitialLine(ByteBuf buf, HttpResponse response) throws Exception {
      response.getProtocolVersion().encode(buf);
      buf.writeByte(32);
      response.getStatus().encode(buf);
      buf.writeBytes(CRLF);
   }
}
