package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class FixedLengthFrameDecoder extends ByteToMessageDecoder {
   private final int frameLength;

   public FixedLengthFrameDecoder(int frameLength) {
      if(frameLength <= 0) {
         throw new IllegalArgumentException("frameLength must be a positive integer: " + frameLength);
      } else {
         this.frameLength = frameLength;
      }
   }

   protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
      Object decoded = this.decode(ctx, in);
      if(decoded != null) {
         out.add(decoded);
      }

   }

   protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
      return in.readableBytes() < this.frameLength?null:in.readSlice(this.frameLength).retain();
   }
}
