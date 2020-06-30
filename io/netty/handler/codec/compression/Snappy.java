package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.compression.Crc32c;
import io.netty.handler.codec.compression.DecompressionException;

class Snappy {
   private static final int MAX_HT_SIZE = 16384;
   private static final int MIN_COMPRESSIBLE_BYTES = 15;
   private static final int PREAMBLE_NOT_FULL = -1;
   private static final int NOT_ENOUGH_INPUT = -1;
   private static final int LITERAL = 0;
   private static final int COPY_1_BYTE_OFFSET = 1;
   private static final int COPY_2_BYTE_OFFSET = 2;
   private static final int COPY_4_BYTE_OFFSET = 3;
   private Snappy.State state = Snappy.State.READY;
   private byte tag;
   private int written;

   public void reset() {
      this.state = Snappy.State.READY;
      this.tag = 0;
      this.written = 0;
   }

   public void encode(ByteBuf in, ByteBuf out, int length) {
      int i = 0;

      while(true) {
         int b = length >>> i * 7;
         if((b & -128) == 0) {
            out.writeByte(b);
            i = in.readerIndex();
            b = i;
            short[] table = getHashTable(length);
            int shift = 32 - (int)Math.floor(Math.log((double)table.length) / Math.log(2.0D));
            int nextEmit = i;
            if(length - i >= 15) {
               ++i;
               int nextHash = hash(in, i, shift);

               label118:
               while(true) {
                  int skip = 32;
                  int nextIndex = i;

                  int candidate;
                  while(true) {
                     i = nextIndex;
                     int hash = nextHash;
                     int bytesBetweenHashLookups = skip++ >> 5;
                     nextIndex += bytesBetweenHashLookups;
                     if(nextIndex > length - 4) {
                        break label118;
                     }

                     nextHash = hash(in, nextIndex, shift);
                     candidate = b + table[hash];
                     table[hash] = (short)(i - b);
                     if(in.getInt(i) == in.getInt(candidate)) {
                        break;
                     }
                  }

                  encodeLiteral(in, out, i - nextEmit);

                  int insertTail;
                  while(true) {
                     int base = i;
                     int matched = 4 + findMatchingLength(in, candidate + 4, i + 4, length);
                     i += matched;
                     int offset = base - candidate;
                     encodeCopy(out, offset, matched);
                     in.readerIndex(in.readerIndex() + matched);
                     insertTail = i - 1;
                     nextEmit = i;
                     if(i >= length - 4) {
                        break label118;
                     }

                     int prevHash = hash(in, insertTail, shift);
                     table[prevHash] = (short)(i - b - 1);
                     int currentHash = hash(in, insertTail + 1, shift);
                     candidate = b + table[currentHash];
                     table[currentHash] = (short)(i - b);
                     if(in.getInt(insertTail + 1) != in.getInt(candidate)) {
                        break;
                     }
                  }

                  nextHash = hash(in, insertTail + 2, shift);
                  ++i;
               }
            }

            if(nextEmit < length) {
               encodeLiteral(in, out, length - nextEmit);
            }

            return;
         }

         out.writeByte(b & 127 | 128);
         ++i;
      }
   }

   private static int hash(ByteBuf in, int index, int shift) {
      return in.getInt(index) + 506832829 >>> shift;
   }

   private static short[] getHashTable(int inputSize) {
      int htSize;
      for(htSize = 256; htSize < 16384 && htSize < inputSize; htSize <<= 1) {
         ;
      }

      short[] table;
      if(htSize <= 256) {
         table = new short[256];
      } else {
         table = new short[16384];
      }

      return table;
   }

   private static int findMatchingLength(ByteBuf in, int minIndex, int inIndex, int maxIndex) {
      int matched;
      for(matched = 0; inIndex <= maxIndex - 4 && in.getInt(inIndex) == in.getInt(minIndex + matched); matched += 4) {
         inIndex += 4;
      }

      while(inIndex < maxIndex && in.getByte(minIndex + matched) == in.getByte(inIndex)) {
         ++inIndex;
         ++matched;
      }

      return matched;
   }

   private static int bitsToEncode(int value) {
      int highestOneBit = Integer.highestOneBit(value);

      int bitLength;
      for(bitLength = 0; (highestOneBit >>= 1) != 0; ++bitLength) {
         ;
      }

      return bitLength;
   }

   private static void encodeLiteral(ByteBuf in, ByteBuf out, int length) {
      if(length < 61) {
         out.writeByte(length - 1 << 2);
      } else {
         int bitLength = bitsToEncode(length - 1);
         int bytesToEncode = 1 + bitLength / 8;
         out.writeByte(59 + bytesToEncode << 2);

         for(int i = 0; i < bytesToEncode; ++i) {
            out.writeByte(length - 1 >> i * 8 & 255);
         }
      }

      out.writeBytes(in, length);
   }

   private static void encodeCopyWithOffset(ByteBuf out, int offset, int length) {
      if(length < 12 && offset < 2048) {
         out.writeByte(1 | length - 4 << 2 | offset >> 8 << 5);
         out.writeByte(offset & 255);
      } else {
         out.writeByte(2 | length - 1 << 2);
         out.writeByte(offset & 255);
         out.writeByte(offset >> 8 & 255);
      }

   }

   private static void encodeCopy(ByteBuf out, int offset, int length) {
      while(length >= 68) {
         encodeCopyWithOffset(out, offset, 64);
         length -= 64;
      }

      if(length > 64) {
         encodeCopyWithOffset(out, offset, 60);
         length -= 60;
      }

      encodeCopyWithOffset(out, offset, length);
   }

   public void decode(ByteBuf in, ByteBuf out) {
      while(in.isReadable()) {
         switch(this.state) {
         case READY:
            this.state = Snappy.State.READING_PREAMBLE;
         case READING_PREAMBLE:
            int uncompressedLength = readPreamble(in);
            if(uncompressedLength == -1) {
               return;
            }

            if(uncompressedLength == 0) {
               this.state = Snappy.State.READY;
               return;
            }

            out.ensureWritable(uncompressedLength);
            this.state = Snappy.State.READING_TAG;
         case READING_TAG:
            if(!in.isReadable()) {
               return;
            }

            this.tag = in.readByte();
            switch(this.tag & 3) {
            case 0:
               this.state = Snappy.State.READING_LITERAL;
               continue;
            case 1:
            case 2:
            case 3:
               this.state = Snappy.State.READING_COPY;
            default:
               continue;
            }
         case READING_LITERAL:
            int literalWritten = decodeLiteral(this.tag, in, out);
            if(literalWritten != -1) {
               this.state = Snappy.State.READING_TAG;
               this.written += literalWritten;
               break;
            }

            return;
         case READING_COPY:
            switch(this.tag & 3) {
            case 1:
               int decodeWritten = decodeCopyWith1ByteOffset(this.tag, in, out, this.written);
               if(decodeWritten != -1) {
                  this.state = Snappy.State.READING_TAG;
                  this.written += decodeWritten;
                  break;
               }

               return;
            case 2:
               int var6 = decodeCopyWith2ByteOffset(this.tag, in, out, this.written);
               if(var6 != -1) {
                  this.state = Snappy.State.READING_TAG;
                  this.written += var6;
                  break;
               }

               return;
            case 3:
               int decodeWritten = decodeCopyWith4ByteOffset(this.tag, in, out, this.written);
               if(decodeWritten == -1) {
                  return;
               }

               this.state = Snappy.State.READING_TAG;
               this.written += decodeWritten;
            }
         }
      }

   }

   private static int readPreamble(ByteBuf in) {
      int length = 0;
      int byteIndex = 0;

      while(in.isReadable()) {
         int current = in.readUnsignedByte();
         length |= (current & 127) << byteIndex++ * 7;
         if((current & 128) == 0) {
            return length;
         }

         if(byteIndex >= 4) {
            throw new DecompressionException("Preamble is greater than 4 bytes");
         }
      }

      return 0;
   }

   private static int decodeLiteral(byte tag, ByteBuf in, ByteBuf out) {
      in.markReaderIndex();
      int length;
      switch(tag >> 2 & 63) {
      case 60:
         if(!in.isReadable()) {
            return -1;
         }

         length = in.readUnsignedByte();
         break;
      case 61:
         if(in.readableBytes() < 2) {
            return -1;
         }

         length = ByteBufUtil.swapShort(in.readShort());
         break;
      case 62:
         if(in.readableBytes() < 3) {
            return -1;
         }

         length = ByteBufUtil.swapMedium(in.readUnsignedMedium());
         break;
      case 63:
      default:
         length = tag >> 2 & 63;
         break;
      case 64:
         if(in.readableBytes() < 4) {
            return -1;
         }

         length = ByteBufUtil.swapInt(in.readInt());
      }

      ++length;
      if(in.readableBytes() < length) {
         in.resetReaderIndex();
         return -1;
      } else {
         out.writeBytes(in, length);
         return length;
      }
   }

   private static int decodeCopyWith1ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
      if(!in.isReadable()) {
         return -1;
      } else {
         int initialIndex = out.writerIndex();
         int length = 4 + ((tag & 28) >> 2);
         int offset = (tag & 224) << 8 >> 5 | in.readUnsignedByte();
         validateOffset(offset, writtenSoFar);
         out.markReaderIndex();
         if(offset < length) {
            for(int copies = length / offset; copies > 0; --copies) {
               out.readerIndex(initialIndex - offset);
               out.readBytes(out, offset);
            }

            if(length % offset != 0) {
               out.readerIndex(initialIndex - offset);
               out.readBytes(out, length % offset);
            }
         } else {
            out.readerIndex(initialIndex - offset);
            out.readBytes(out, length);
         }

         out.resetReaderIndex();
         return length;
      }
   }

   private static int decodeCopyWith2ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
      if(in.readableBytes() < 2) {
         return -1;
      } else {
         int initialIndex = out.writerIndex();
         int length = 1 + (tag >> 2 & 63);
         int offset = ByteBufUtil.swapShort(in.readShort());
         validateOffset(offset, writtenSoFar);
         out.markReaderIndex();
         if(offset < length) {
            for(int copies = length / offset; copies > 0; --copies) {
               out.readerIndex(initialIndex - offset);
               out.readBytes(out, offset);
            }

            if(length % offset != 0) {
               out.readerIndex(initialIndex - offset);
               out.readBytes(out, length % offset);
            }
         } else {
            out.readerIndex(initialIndex - offset);
            out.readBytes(out, length);
         }

         out.resetReaderIndex();
         return length;
      }
   }

   private static int decodeCopyWith4ByteOffset(byte tag, ByteBuf in, ByteBuf out, int writtenSoFar) {
      if(in.readableBytes() < 4) {
         return -1;
      } else {
         int initialIndex = out.writerIndex();
         int length = 1 + (tag >> 2 & 63);
         int offset = ByteBufUtil.swapInt(in.readInt());
         validateOffset(offset, writtenSoFar);
         out.markReaderIndex();
         if(offset < length) {
            for(int copies = length / offset; copies > 0; --copies) {
               out.readerIndex(initialIndex - offset);
               out.readBytes(out, offset);
            }

            if(length % offset != 0) {
               out.readerIndex(initialIndex - offset);
               out.readBytes(out, length % offset);
            }
         } else {
            out.readerIndex(initialIndex - offset);
            out.readBytes(out, length);
         }

         out.resetReaderIndex();
         return length;
      }
   }

   private static void validateOffset(int offset, int chunkSizeSoFar) {
      if(offset > 32767) {
         throw new DecompressionException("Offset exceeds maximum permissible value");
      } else if(offset <= 0) {
         throw new DecompressionException("Offset is less than minimum permissible value");
      } else if(offset > chunkSizeSoFar) {
         throw new DecompressionException("Offset exceeds size of chunk");
      }
   }

   public static int calculateChecksum(ByteBuf data) {
      return calculateChecksum(data, data.readerIndex(), data.readableBytes());
   }

   public static int calculateChecksum(ByteBuf data, int offset, int length) {
      Crc32c crc32 = new Crc32c();

      int var8;
      try {
         if(data.hasArray()) {
            crc32.update(data.array(), data.arrayOffset() + offset, length);
         } else {
            byte[] array = new byte[length];
            data.getBytes(offset, array);
            crc32.update(array, 0, length);
         }

         var8 = maskChecksum((int)crc32.getValue());
      } finally {
         crc32.reset();
      }

      return var8;
   }

   static void validateChecksum(int expectedChecksum, ByteBuf data) {
      validateChecksum(expectedChecksum, data, data.readerIndex(), data.readableBytes());
   }

   static void validateChecksum(int expectedChecksum, ByteBuf data, int offset, int length) {
      int actualChecksum = calculateChecksum(data, offset, length);
      if(actualChecksum != expectedChecksum) {
         throw new DecompressionException("mismatching checksum: " + Integer.toHexString(actualChecksum) + " (expected: " + Integer.toHexString(expectedChecksum) + ')');
      }
   }

   static int maskChecksum(int checksum) {
      return (checksum >> 15 | checksum << 17) + -1568478504;
   }

   private static enum State {
      READY,
      READING_PREAMBLE,
      READING_TAG,
      READING_LITERAL,
      READING_COPY;
   }
}
