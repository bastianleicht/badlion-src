package io.netty.handler.codec.bytes;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@ChannelHandler.Sharable
public class ByteArrayEncoder extends MessageToMessageEncoder {
   protected void encode(ChannelHandlerContext ctx, byte[] msg, List out) throws Exception {
      out.add(Unpooled.wrappedBuffer(msg));
   }
}
