package io.netty.handler.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class MessageToMessageDecoder extends ChannelInboundHandlerAdapter {
   private final TypeParameterMatcher matcher;

   protected MessageToMessageDecoder() {
      this.matcher = TypeParameterMatcher.find(this, MessageToMessageDecoder.class, "I");
   }

   protected MessageToMessageDecoder(Class inboundMessageType) {
      this.matcher = TypeParameterMatcher.get(inboundMessageType);
   }

   public boolean acceptInboundMessage(Object msg) throws Exception {
      return this.matcher.match(msg);
   }

   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      RecyclableArrayList out = RecyclableArrayList.newInstance();
      boolean var13 = false;

      try {
         var13 = true;
         if(this.acceptInboundMessage(msg)) {
            I cast = msg;

            try {
               this.decode(ctx, cast, out);
            } finally {
               ReferenceCountUtil.release(msg);
            }

            var13 = false;
         } else {
            out.add(msg);
            var13 = false;
         }
      } catch (DecoderException var19) {
         throw var19;
      } catch (Exception var20) {
         throw new DecoderException(var20);
      } finally {
         if(var13) {
            int size = out.size();

            for(int i = 0; i < size; ++i) {
               ctx.fireChannelRead(out.get(i));
            }

            out.recycle();
         }
      }

      int size = out.size();

      for(int i = 0; i < size; ++i) {
         ctx.fireChannelRead(out.get(i));
      }

      out.recycle();
   }

   protected abstract void decode(ChannelHandlerContext var1, Object var2, List var3) throws Exception;
}
