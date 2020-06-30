package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.compression.CompressionException;
import io.netty.handler.codec.compression.Snappy;

public class SnappyFramedEncoder extends MessageToByteEncoder {
   private static final int MIN_COMPRESSIBLE_LENGTH = 18;
   private static final byte[] STREAM_START = new byte[]{(byte)-1, (byte)6, (byte)0, (byte)0, (byte)115, (byte)78, (byte)97, (byte)80, (byte)112, (byte)89};
   private final Snappy snappy = new Snappy();
   private boolean started;

   protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
      if(in.isReadable()) {
         if(!this.started) {
            this.started = true;
            out.writeBytes(STREAM_START);
         }

         int dataLength = in.readableBytes();
         if(dataLength <= 18) {
            writeUnencodedChunk(in, out, dataLength);
         } else {
            while(true) {
               int lengthIdx = out.writerIndex() + 1;
               if(dataLength < 18) {
                  ByteBuf slice = in.readSlice(dataLength);
                  writeUnencodedChunk(slice, out, dataLength);
                  break;
               }

               out.writeInt(0);
               if(dataLength <= 32767) {
                  ByteBuf slice = in.readSlice(dataLength);
                  calculateAndWriteChecksum(slice, out);
                  this.snappy.encode(slice, out, dataLength);
                  setChunkLength(out, lengthIdx);
                  break;
               }

               ByteBuf slice = in.readSlice(32767);
               calculateAndWriteChecksum(slice, out);
               this.snappy.encode(slice, out, 32767);
               setChunkLength(out, lengthIdx);
               dataLength -= 32767;
            }
         }

      }
   }

   private static void writeUnencodedChunk(ByteBuf in, ByteBuf out, int dataLength) {
      out.writeByte(1);
      writeChunkLength(out, dataLength + 4);
      calculateAndWriteChecksum(in, out);
      out.writeBytes(in, dataLength);
   }

   private static void setChunkLength(ByteBuf out, int lengthIdx) {
      int chunkLength = out.writerIndex() - lengthIdx - 3;
      if(chunkLength >>> 24 != 0) {
         throw new CompressionException("compressed data too large: " + chunkLength);
      } else {
         out.setMedium(lengthIdx, ByteBufUtil.swapMedium(chunkLength));
      }
   }

   private static void writeChunkLength(ByteBuf out, int chunkLength) {
      out.writeMedium(ByteBufUtil.swapMedium(chunkLength));
   }

   private static void calculateAndWriteChecksum(ByteBuf slice, ByteBuf out) {
      out.writeInt(ByteBufUtil.swapInt(Snappy.calculateChecksum(slice)));
   }
}
