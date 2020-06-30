package io.netty.handler.codec.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.serialization.CompactObjectOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@ChannelHandler.Sharable
public class ObjectEncoder extends MessageToByteEncoder {
   private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

   protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
      int startIdx = out.writerIndex();
      ByteBufOutputStream bout = new ByteBufOutputStream(out);
      bout.write(LENGTH_PLACEHOLDER);
      ObjectOutputStream oout = new CompactObjectOutputStream(bout);
      oout.writeObject(msg);
      oout.flush();
      oout.close();
      int endIdx = out.writerIndex();
      out.setInt(startIdx, endIdx - startIdx - 4);
   }
}
