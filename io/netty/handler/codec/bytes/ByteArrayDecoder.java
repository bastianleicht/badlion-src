package io.netty.handler.codec.bytes;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

public class ByteArrayDecoder extends MessageToMessageDecoder {
   protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List out) throws Exception {
      byte[] array = new byte[msg.readableBytes()];
      msg.getBytes(0, (byte[])array);
      out.add(array);
   }
}
