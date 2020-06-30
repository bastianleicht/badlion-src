package io.netty.handler.codec.string;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.nio.charset.Charset;
import java.util.List;

@ChannelHandler.Sharable
public class StringDecoder extends MessageToMessageDecoder {
   private final Charset charset;

   public StringDecoder() {
      this(Charset.defaultCharset());
   }

   public StringDecoder(Charset charset) {
      if(charset == null) {
         throw new NullPointerException("charset");
      } else {
         this.charset = charset;
      }
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List out) throws Exception {
      out.add(msg.toString(this.charset));
   }
}
