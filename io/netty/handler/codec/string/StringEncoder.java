package io.netty.handler.codec.string;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

@ChannelHandler.Sharable
public class StringEncoder extends MessageToMessageEncoder {
   private final Charset charset;

   public StringEncoder() {
      this(Charset.defaultCharset());
   }

   public StringEncoder(Charset charset) {
      if(charset == null) {
         throw new NullPointerException("charset");
      } else {
         this.charset = charset;
      }
   }

   protected void encode(ChannelHandlerContext ctx, CharSequence msg, List out) throws Exception {
      if(msg.length() != 0) {
         out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg), this.charset));
      }
   }
}
