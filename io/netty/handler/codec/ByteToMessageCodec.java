package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.internal.TypeParameterMatcher;
import java.util.List;

public abstract class ByteToMessageCodec extends ChannelDuplexHandler {
   private final TypeParameterMatcher outboundMsgMatcher;
   private final MessageToByteEncoder encoder;
   private final ByteToMessageDecoder decoder;

   protected ByteToMessageCodec() {
      this(true);
   }

   protected ByteToMessageCodec(Class outboundMessageType) {
      this(outboundMessageType, true);
   }

   protected ByteToMessageCodec(boolean preferDirect) {
      this.decoder = new ByteToMessageDecoder() {
         public void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
            ByteToMessageCodec.this.decode(ctx, in, out);
         }

         protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
            ByteToMessageCodec.this.decodeLast(ctx, in, out);
         }
      };
      this.outboundMsgMatcher = TypeParameterMatcher.find(this, ByteToMessageCodec.class, "I");
      this.encoder = new ByteToMessageCodec.Encoder(preferDirect);
   }

   protected ByteToMessageCodec(Class outboundMessageType, boolean preferDirect) {
      this.decoder = new ByteToMessageDecoder() {
         public void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
            ByteToMessageCodec.this.decode(ctx, in, out);
         }

         protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
            ByteToMessageCodec.this.decodeLast(ctx, in, out);
         }
      };
      this.checkForSharableAnnotation();
      this.outboundMsgMatcher = TypeParameterMatcher.get(outboundMessageType);
      this.encoder = new ByteToMessageCodec.Encoder(preferDirect);
   }

   private void checkForSharableAnnotation() {
      if(this.isSharable()) {
         throw new IllegalStateException("@Sharable annotation is not allowed");
      }
   }

   public boolean acceptOutboundMessage(Object msg) throws Exception {
      return this.outboundMsgMatcher.match(msg);
   }

   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      this.decoder.channelRead(ctx, msg);
   }

   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      this.encoder.write(ctx, msg, promise);
   }

   protected abstract void encode(ChannelHandlerContext var1, Object var2, ByteBuf var3) throws Exception;

   protected abstract void decode(ChannelHandlerContext var1, ByteBuf var2, List var3) throws Exception;

   protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
      this.decode(ctx, in, out);
   }

   private final class Encoder extends MessageToByteEncoder {
      Encoder(boolean preferDirect) {
         super(preferDirect);
      }

      public boolean acceptOutboundMessage(Object msg) throws Exception {
         return ByteToMessageCodec.this.acceptOutboundMessage(msg);
      }

      protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
         ByteToMessageCodec.this.encode(ctx, msg, out);
      }
   }
}
