package io.netty.handler.codec.marshalling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.marshalling.ChannelBufferByteInput;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

public class MarshallingDecoder extends LengthFieldBasedFrameDecoder {
   private final UnmarshallerProvider provider;

   public MarshallingDecoder(UnmarshallerProvider provider) {
      this(provider, 1048576);
   }

   public MarshallingDecoder(UnmarshallerProvider provider, int maxObjectSize) {
      super(maxObjectSize, 0, 4, 0, 4);
      this.provider = provider;
   }

   protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
      ByteBuf frame = (ByteBuf)super.decode(ctx, in);
      if(frame == null) {
         return null;
      } else {
         Unmarshaller unmarshaller = this.provider.getUnmarshaller(ctx);
         ByteInput input = new ChannelBufferByteInput(frame);

         Object var7;
         try {
            unmarshaller.start(input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            var7 = obj;
         } finally {
            unmarshaller.close();
         }

         return var7;
      }
   }

   protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
      return buffer.slice(index, length);
   }
}
