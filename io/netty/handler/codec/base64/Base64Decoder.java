package io.netty.handler.codec.base64;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import java.util.List;

@ChannelHandler.Sharable
public class Base64Decoder extends MessageToMessageDecoder {
   private final Base64Dialect dialect;

   public Base64Decoder() {
      this(Base64Dialect.STANDARD);
   }

   public Base64Decoder(Base64Dialect dialect) {
      if(dialect == null) {
         throw new NullPointerException("dialect");
      } else {
         this.dialect = dialect;
      }
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List out) throws Exception {
      out.add(Base64.decode(msg, msg.readerIndex(), msg.readableBytes(), this.dialect));
   }
}
