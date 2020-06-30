package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoderBuffer;
import io.netty.util.Signal;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import java.util.List;

public abstract class ReplayingDecoder extends ByteToMessageDecoder {
   static final Signal REPLAY = Signal.valueOf(ReplayingDecoder.class.getName() + ".REPLAY");
   private final ReplayingDecoderBuffer replayable;
   private Object state;
   private int checkpoint;

   protected ReplayingDecoder() {
      this((Object)null);
   }

   protected ReplayingDecoder(Object initialState) {
      this.replayable = new ReplayingDecoderBuffer();
      this.checkpoint = -1;
      this.state = initialState;
   }

   protected void checkpoint() {
      this.checkpoint = this.internalBuffer().readerIndex();
   }

   protected void checkpoint(Object state) {
      this.checkpoint();
      this.state(state);
   }

   protected Object state() {
      return this.state;
   }

   protected Object state(Object newState) {
      S oldState = this.state;
      this.state = newState;
      return oldState;
   }

   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      // $FF: Couldn't be decompiled
   }

   protected void callDecode(ChannelHandlerContext ctx, ByteBuf in, List out) {
      this.replayable.setCumulation(in);

      try {
         while(in.isReadable()) {
            int oldReaderIndex = this.checkpoint = in.readerIndex();
            int outSize = out.size();
            S oldState = this.state;
            int oldInputLength = in.readableBytes();

            try {
               this.decode(ctx, this.replayable, out);
               if(ctx.isRemoved()) {
                  break;
               }

               if(outSize == out.size()) {
                  if(oldInputLength == in.readableBytes() && oldState == this.state) {
                     throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() must consume the inbound " + "data or change its state if it did not decode anything.");
                  }
                  continue;
               }
            } catch (Signal var10) {
               var10.expect(REPLAY);
               if(!ctx.isRemoved()) {
                  int checkpoint = this.checkpoint;
                  if(checkpoint >= 0) {
                     in.readerIndex(checkpoint);
                  }
               }
               break;
            }

            if(oldReaderIndex == in.readerIndex() && oldState == this.state) {
               throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() method must consume the inbound data " + "or change its state if it decoded something.");
            }

            if(this.isSingleDecode()) {
               break;
            }
         }

      } catch (DecoderException var11) {
         throw var11;
      } catch (Throwable var12) {
         throw new DecoderException(var12);
      }
   }
}
