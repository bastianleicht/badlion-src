package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.handler.codec.compression.Snappy;
import java.util.Arrays;
import java.util.List;

public class SnappyFramedDecoder extends ByteToMessageDecoder {
   private static final byte[] SNAPPY = new byte[]{(byte)115, (byte)78, (byte)97, (byte)80, (byte)112, (byte)89};
   private static final int MAX_UNCOMPRESSED_DATA_SIZE = 65540;
   private final Snappy snappy;
   private final boolean validateChecksums;
   private boolean started;
   private boolean corrupted;

   public SnappyFramedDecoder() {
      this(false);
   }

   public SnappyFramedDecoder(boolean validateChecksums) {
      this.snappy = new Snappy();
      this.validateChecksums = validateChecksums;
   }

   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
      if(this.corrupted) {
         in.skipBytes(in.readableBytes());
      } else {
         try {
            int idx = in.readerIndex();
            int inSize = in.readableBytes();
            if(inSize >= 4) {
               int chunkTypeVal = in.getUnsignedByte(idx);
               SnappyFramedDecoder.ChunkType chunkType = mapChunkType((byte)chunkTypeVal);
               int chunkLength = ByteBufUtil.swapMedium(in.getUnsignedMedium(idx + 1));
               switch(chunkType) {
               case STREAM_IDENTIFIER:
                  if(chunkLength != SNAPPY.length) {
                     throw new DecompressionException("Unexpected length of stream identifier: " + chunkLength);
                  }

                  if(inSize >= 4 + SNAPPY.length) {
                     byte[] identifier = new byte[chunkLength];
                     in.skipBytes(4).readBytes(identifier);
                     if(!Arrays.equals(identifier, SNAPPY)) {
                        throw new DecompressionException("Unexpected stream identifier contents. Mismatched snappy protocol version?");
                     }

                     this.started = true;
                  }
                  break;
               case RESERVED_SKIPPABLE:
                  if(!this.started) {
                     throw new DecompressionException("Received RESERVED_SKIPPABLE tag before STREAM_IDENTIFIER");
                  }

                  if(inSize < 4 + chunkLength) {
                     return;
                  }

                  in.skipBytes(4 + chunkLength);
                  break;
               case RESERVED_UNSKIPPABLE:
                  throw new DecompressionException("Found reserved unskippable chunk type: 0x" + Integer.toHexString(chunkTypeVal));
               case UNCOMPRESSED_DATA:
                  if(!this.started) {
                     throw new DecompressionException("Received UNCOMPRESSED_DATA tag before STREAM_IDENTIFIER");
                  }

                  if(chunkLength > 65540) {
                     throw new DecompressionException("Received UNCOMPRESSED_DATA larger than 65540 bytes");
                  }

                  if(inSize < 4 + chunkLength) {
                     return;
                  }

                  in.skipBytes(4);
                  if(this.validateChecksums) {
                     int checksum = ByteBufUtil.swapInt(in.readInt());
                     Snappy.validateChecksum(checksum, in, in.readerIndex(), chunkLength - 4);
                  } else {
                     in.skipBytes(4);
                  }

                  out.add(in.readSlice(chunkLength - 4).retain());
                  break;
               case COMPRESSED_DATA:
                  if(!this.started) {
                     throw new DecompressionException("Received COMPRESSED_DATA tag before STREAM_IDENTIFIER");
                  }

                  if(inSize < 4 + chunkLength) {
                     return;
                  }

                  in.skipBytes(4);
                  int checksum = ByteBufUtil.swapInt(in.readInt());
                  ByteBuf uncompressed = ctx.alloc().buffer(0);
                  if(this.validateChecksums) {
                     int oldWriterIndex = in.writerIndex();

                     try {
                        in.writerIndex(in.readerIndex() + chunkLength - 4);
                        this.snappy.decode(in, uncompressed);
                     } finally {
                        in.writerIndex(oldWriterIndex);
                     }

                     Snappy.validateChecksum(checksum, uncompressed, 0, uncompressed.writerIndex());
                  } else {
                     this.snappy.decode(in.readSlice(chunkLength - 4), uncompressed);
                  }

                  out.add(uncompressed);
                  this.snappy.reset();
               }

            }
         } catch (Exception var17) {
            this.corrupted = true;
            throw var17;
         }
      }
   }

   private static SnappyFramedDecoder.ChunkType mapChunkType(byte type) {
      return type == 0?SnappyFramedDecoder.ChunkType.COMPRESSED_DATA:(type == 1?SnappyFramedDecoder.ChunkType.UNCOMPRESSED_DATA:(type == -1?SnappyFramedDecoder.ChunkType.STREAM_IDENTIFIER:((type & 128) == 128?SnappyFramedDecoder.ChunkType.RESERVED_SKIPPABLE:SnappyFramedDecoder.ChunkType.RESERVED_UNSKIPPABLE)));
   }

   private static enum ChunkType {
      STREAM_IDENTIFIER,
      COMPRESSED_DATA,
      UNCOMPRESSED_DATA,
      RESERVED_UNSKIPPABLE,
      RESERVED_SKIPPABLE;
   }
}
