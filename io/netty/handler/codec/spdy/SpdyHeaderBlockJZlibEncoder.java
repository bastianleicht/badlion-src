package io.netty.handler.codec.spdy;

import com.jcraft.jzlib.Deflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.compression.CompressionException;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyHeaderBlockRawEncoder;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyVersion;

class SpdyHeaderBlockJZlibEncoder extends SpdyHeaderBlockRawEncoder {
   private final Deflater z = new Deflater();
   private boolean finished;

   SpdyHeaderBlockJZlibEncoder(SpdyVersion version, int compressionLevel, int windowBits, int memLevel) {
      super(version);
      if(compressionLevel >= 0 && compressionLevel <= 9) {
         if(windowBits >= 9 && windowBits <= 15) {
            if(memLevel >= 1 && memLevel <= 9) {
               int resultCode = this.z.deflateInit(compressionLevel, windowBits, memLevel, JZlib.W_ZLIB);
               if(resultCode != 0) {
                  throw new CompressionException("failed to initialize an SPDY header block deflater: " + resultCode);
               } else {
                  resultCode = this.z.deflateSetDictionary(SpdyCodecUtil.SPDY_DICT, SpdyCodecUtil.SPDY_DICT.length);
                  if(resultCode != 0) {
                     throw new CompressionException("failed to set the SPDY dictionary: " + resultCode);
                  }
               }
            } else {
               throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
            }
         } else {
            throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
         }
      } else {
         throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
      }
   }

   private void setInput(ByteBuf decompressed) {
      byte[] in = new byte[decompressed.readableBytes()];
      decompressed.readBytes(in);
      this.z.next_in = in;
      this.z.next_in_index = 0;
      this.z.avail_in = in.length;
   }

   private void encode(ByteBuf compressed) {
      try {
         byte[] out = new byte[(int)Math.ceil((double)this.z.next_in.length * 1.001D) + 12];
         this.z.next_out = out;
         this.z.next_out_index = 0;
         this.z.avail_out = out.length;
         int resultCode = this.z.deflate(2);
         if(resultCode != 0) {
            throw new CompressionException("compression failure: " + resultCode);
         }

         if(this.z.next_out_index != 0) {
            compressed.writeBytes((byte[])out, 0, this.z.next_out_index);
         }
      } finally {
         this.z.next_in = null;
         this.z.next_out = null;
      }

   }

   public ByteBuf encode(SpdyHeadersFrame frame) throws Exception {
      if(frame == null) {
         throw new IllegalArgumentException("frame");
      } else if(this.finished) {
         return Unpooled.EMPTY_BUFFER;
      } else {
         ByteBuf decompressed = super.encode(frame);
         if(decompressed.readableBytes() == 0) {
            return Unpooled.EMPTY_BUFFER;
         } else {
            ByteBuf compressed = decompressed.alloc().buffer();
            this.setInput(decompressed);
            this.encode(compressed);
            return compressed;
         }
      }
   }

   public void end() {
      if(!this.finished) {
         this.finished = true;
         this.z.deflateEnd();
         this.z.next_in = null;
         this.z.next_out = null;
      }
   }
}
