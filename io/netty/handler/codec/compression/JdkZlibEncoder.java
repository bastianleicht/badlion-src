package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPromiseNotifier;
import io.netty.handler.codec.compression.ZlibEncoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

public class JdkZlibEncoder extends ZlibEncoder {
   private final ZlibWrapper wrapper;
   private final Deflater deflater;
   private volatile boolean finished;
   private volatile ChannelHandlerContext ctx;
   private final CRC32 crc;
   private static final byte[] gzipHeader = new byte[]{(byte)31, (byte)-117, (byte)8, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0};
   private boolean writeHeader;

   public JdkZlibEncoder() {
      this(6);
   }

   public JdkZlibEncoder(int compressionLevel) {
      this(ZlibWrapper.ZLIB, compressionLevel);
   }

   public JdkZlibEncoder(ZlibWrapper wrapper) {
      this(wrapper, 6);
   }

   public JdkZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
      this.crc = new CRC32();
      this.writeHeader = true;
      if(compressionLevel >= 0 && compressionLevel <= 9) {
         if(wrapper == null) {
            throw new NullPointerException("wrapper");
         } else if(wrapper == ZlibWrapper.ZLIB_OR_NONE) {
            throw new IllegalArgumentException("wrapper \'" + ZlibWrapper.ZLIB_OR_NONE + "\' is not " + "allowed for compression.");
         } else {
            this.wrapper = wrapper;
            this.deflater = new Deflater(compressionLevel, wrapper != ZlibWrapper.ZLIB);
         }
      } else {
         throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
      }
   }

   public JdkZlibEncoder(byte[] dictionary) {
      this(6, dictionary);
   }

   public JdkZlibEncoder(int compressionLevel, byte[] dictionary) {
      this.crc = new CRC32();
      this.writeHeader = true;
      if(compressionLevel >= 0 && compressionLevel <= 9) {
         if(dictionary == null) {
            throw new NullPointerException("dictionary");
         } else {
            this.wrapper = ZlibWrapper.ZLIB;
            this.deflater = new Deflater(compressionLevel);
            this.deflater.setDictionary(dictionary);
         }
      } else {
         throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
      }
   }

   public ChannelFuture close() {
      return this.close(this.ctx().newPromise());
   }

   public ChannelFuture close(final ChannelPromise promise) {
      ChannelHandlerContext ctx = this.ctx();
      EventExecutor executor = ctx.executor();
      if(executor.inEventLoop()) {
         return this.finishEncode(ctx, promise);
      } else {
         final ChannelPromise p = ctx.newPromise();
         executor.execute(new Runnable() {
            public void run() {
               ChannelFuture f = JdkZlibEncoder.this.finishEncode(JdkZlibEncoder.this.ctx(), p);
               f.addListener(new ChannelPromiseNotifier(new ChannelPromise[]{promise}));
            }
         });
         return p;
      }
   }

   private ChannelHandlerContext ctx() {
      ChannelHandlerContext ctx = this.ctx;
      if(ctx == null) {
         throw new IllegalStateException("not added to a pipeline");
      } else {
         return ctx;
      }
   }

   public boolean isClosed() {
      return this.finished;
   }

   protected void encode(ChannelHandlerContext ctx, ByteBuf uncompressed, ByteBuf out) throws Exception {
      if(this.finished) {
         out.writeBytes(uncompressed);
      } else {
         int len = uncompressed.readableBytes();
         int offset;
         byte[] inAry;
         if(uncompressed.hasArray()) {
            inAry = uncompressed.array();
            offset = uncompressed.arrayOffset() + uncompressed.readerIndex();
            uncompressed.skipBytes(len);
         } else {
            inAry = new byte[len];
            uncompressed.readBytes(inAry);
            offset = 0;
         }

         if(this.writeHeader) {
            this.writeHeader = false;
            if(this.wrapper == ZlibWrapper.GZIP) {
               out.writeBytes(gzipHeader);
            }
         }

         if(this.wrapper == ZlibWrapper.GZIP) {
            this.crc.update(inAry, offset, len);
         }

         this.deflater.setInput(inAry, offset, len);

         while(!this.deflater.needsInput()) {
            this.deflate(out);
         }

      }
   }

   protected final ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
      int sizeEstimate = (int)Math.ceil((double)msg.readableBytes() * 1.001D) + 12;
      if(this.writeHeader) {
         switch(this.wrapper) {
         case GZIP:
            sizeEstimate += gzipHeader.length;
            break;
         case ZLIB:
            sizeEstimate += 2;
         }
      }

      return ctx.alloc().heapBuffer(sizeEstimate);
   }

   public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
      ChannelFuture f = this.finishEncode(ctx, ctx.newPromise());
      f.addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture f) throws Exception {
            ctx.close(promise);
         }
      });
      if(!f.isDone()) {
         ctx.executor().schedule(new Runnable() {
            public void run() {
               ctx.close(promise);
            }
         }, 10L, TimeUnit.SECONDS);
      }

   }

   private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) {
      if(this.finished) {
         promise.setSuccess();
         return promise;
      } else {
         this.finished = true;
         ByteBuf footer = ctx.alloc().heapBuffer();
         if(this.writeHeader && this.wrapper == ZlibWrapper.GZIP) {
            this.writeHeader = false;
            footer.writeBytes(gzipHeader);
         }

         this.deflater.finish();

         while(!this.deflater.finished()) {
            this.deflate(footer);
            if(!footer.isWritable()) {
               ctx.write(footer);
               footer = ctx.alloc().heapBuffer();
            }
         }

         if(this.wrapper == ZlibWrapper.GZIP) {
            int crcValue = (int)this.crc.getValue();
            int uncBytes = this.deflater.getTotalIn();
            footer.writeByte(crcValue);
            footer.writeByte(crcValue >>> 8);
            footer.writeByte(crcValue >>> 16);
            footer.writeByte(crcValue >>> 24);
            footer.writeByte(uncBytes);
            footer.writeByte(uncBytes >>> 8);
            footer.writeByte(uncBytes >>> 16);
            footer.writeByte(uncBytes >>> 24);
         }

         this.deflater.end();
         return ctx.writeAndFlush(footer, promise);
      }
   }

   private void deflate(ByteBuf out) {
      while(true) {
         int writerIndex = out.writerIndex();
         int numBytes = this.deflater.deflate(out.array(), out.arrayOffset() + writerIndex, out.writableBytes(), 2);
         out.writerIndex(writerIndex + numBytes);
         if(numBytes <= 0) {
            break;
         }
      }

   }

   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      this.ctx = ctx;
   }
}
