package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.socks.SocksMessage;

@ChannelHandler.Sharable
public class SocksMessageEncoder extends MessageToByteEncoder {
   private static final String name = "SOCKS_MESSAGE_ENCODER";

   /** @deprecated */
   @Deprecated
   public static String getName() {
      return "SOCKS_MESSAGE_ENCODER";
   }

   protected void encode(ChannelHandlerContext ctx, SocksMessage msg, ByteBuf out) throws Exception {
      msg.encodeAsByteBuf(out);
   }
}
