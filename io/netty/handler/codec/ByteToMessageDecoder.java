package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import io.netty.util.internal.RecyclableArrayList;
import io.netty.util.internal.StringUtil;
import java.util.List;

public abstract class ByteToMessageDecoder extends ChannelInboundHandlerAdapter {
   ByteBuf cumulation;
   private boolean singleDecode;
   private boolean decodeWasNull;
   private boolean first;

   protected ByteToMessageDecoder() {
      if(this.isSharable()) {
         throw new IllegalStateException("@Sharable annotation is not allowed");
      }
   }

   public void setSingleDecode(boolean singleDecode) {
      this.singleDecode = singleDecode;
   }

   public boolean isSingleDecode() {
      return this.singleDecode;
   }

   protected int actualReadableBytes() {
      return this.internalBuffer().readableBytes();
   }

   protected ByteBuf internalBuffer() {
      return this.cumulation != null?this.cumulation:Unpooled.EMPTY_BUFFER;
   }

   public final void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      ByteBuf buf = this.internalBuffer();
      int readable = buf.readableBytes();
      if(buf.isReadable()) {
         ByteBuf bytes = buf.readBytes(readable);
         buf.release();
         ctx.fireChannelRead(bytes);
      } else {
         buf.release();
      }

      this.cumulation = null;
      ctx.fireChannelReadComplete();
      this.handlerRemoved0(ctx);
   }

   protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
   }

   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if(msg instanceof ByteBuf) {
         RecyclableArrayList out = RecyclableArrayList.newInstance();
         boolean var12 = false;

         try {
            var12 = true;
            ByteBuf data = (ByteBuf)msg;
            this.first = this.cumulation == null;
            if(this.first) {
               this.cumulation = data;
            } else {
               if(this.cumulation.writerIndex() > this.cumulation.maxCapacity() - data.readableBytes() || this.cumulation.refCnt() > 1) {
                  this.expandCumulation(ctx, data.readableBytes());
               }

               this.cumulation.writeBytes(data);
               data.release();
            }

            this.callDecode(ctx, this.cumulation, out);
            var12 = false;
         } catch (DecoderException var13) {
            throw var13;
         } catch (Throwable var14) {
            throw new DecoderException(var14);
         } finally {
            if(var12) {
               if(this.cumulation != null && !this.cumulation.isReadable()) {
                  this.cumulation.release();
                  this.cumulation = null;
               }

               int size = out.size();
               this.decodeWasNull = size == 0;

               for(int i = 0; i < size; ++i) {
                  ctx.fireChannelRead(out.get(i));
               }

               out.recycle();
            }
         }

         if(this.cumulation != null && !this.cumulation.isReadable()) {
            this.cumulation.release();
            this.cumulation = null;
         }

         int size = out.size();
         this.decodeWasNull = size == 0;

         for(int i = 0; i < size; ++i) {
            ctx.fireChannelRead(out.get(i));
         }

         out.recycle();
      } else {
         ctx.fireChannelRead(msg);
      }

   }

   private void expandCumulation(ChannelHandlerContext ctx, int readable) {
      ByteBuf oldCumulation = this.cumulation;
      this.cumulation = ctx.alloc().buffer(oldCumulation.readableBytes() + readable);
      this.cumulation.writeBytes(oldCumulation);
      oldCumulation.release();
   }

   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      if(this.cumulation != null && !this.first && this.cumulation.refCnt() == 1) {
         this.cumulation.discardSomeReadBytes();
      }

      if(this.decodeWasNull) {
         this.decodeWasNull = false;
         if(!ctx.channel().config().isAutoRead()) {
            ctx.read();
         }
      }

      ctx.fireChannelReadComplete();
   }

   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      RecyclableArrayList out = RecyclableArrayList.newInstance();
      boolean var25 = false;

      try {
         var25 = true;
         if(this.cumulation != null) {
            this.callDecode(ctx, this.cumulation, out);
            this.decodeLast(ctx, this.cumulation, out);
            var25 = false;
         } else {
            this.decodeLast(ctx, Unpooled.EMPTY_BUFFER, out);
            var25 = false;
         }
      } catch (DecoderException var26) {
         throw var26;
      } catch (Exception var27) {
         throw new DecoderException(var27);
      } finally {
         if(var25) {
            try {
               if(this.cumulation != null) {
                  this.cumulation.release();
                  this.cumulation = null;
               }

               int size = out.size();

               for(int i = 0; i < size; ++i) {
                  ctx.fireChannelRead(out.get(i));
               }

               if(size > 0) {
                  ctx.fireChannelReadComplete();
               }

               ctx.fireChannelInactive();
            } finally {
               out.recycle();
            }
         }
      }

      try {
         if(this.cumulation != null) {
            this.cumulation.release();
            this.cumulation = null;
         }

         int size = out.size();

         for(int i = 0; i < size; ++i) {
            ctx.fireChannelRead(out.get(i));
         }

         if(size > 0) {
            ctx.fireChannelReadComplete();
         }

         ctx.fireChannelInactive();
      } finally {
         out.recycle();
      }

   }

   protected void callDecode(ChannelHandlerContext ctx, ByteBuf in, List out) {
      try {
         while(true) {
            if(in.isReadable()) {
               int outSize = out.size();
               int oldInputLength = in.readableBytes();
               this.decode(ctx, in, out);
               if(!ctx.isRemoved()) {
                  if(outSize == out.size()) {
                     if(oldInputLength != in.readableBytes()) {
                        continue;
                     }
                  } else {
                     if(oldInputLength == in.readableBytes()) {
                        throw new DecoderException(StringUtil.simpleClassName(this.getClass()) + ".decode() did not read anything but decoded a message.");
                     }

                     if(!this.isSingleDecode()) {
                        continue;
                     }
                  }
               }
            }

            return;
         }
      } catch (DecoderException var6) {
         throw var6;
      } catch (Throwable var7) {
         throw new DecoderException(var7);
      }
   }

   protected abstract void decode(ChannelHandlerContext var1, ByteBuf var2, List var3) throws Exception;

   protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
      this.decode(ctx, in, out);
   }
}
