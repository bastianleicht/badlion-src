package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.spdy.SpdyCodecUtil;
import io.netty.handler.codec.spdy.SpdyHeaderBlockDecoder;
import io.netty.handler.codec.spdy.SpdyHeadersFrame;
import io.netty.handler.codec.spdy.SpdyVersion;

public class SpdyHeaderBlockRawDecoder extends SpdyHeaderBlockDecoder {
   private static final int LENGTH_FIELD_SIZE = 4;
   private final int maxHeaderSize;
   private SpdyHeaderBlockRawDecoder.State state;
   private ByteBuf cumulation;
   private int headerSize;
   private int numHeaders;
   private int length;
   private String name;

   public SpdyHeaderBlockRawDecoder(SpdyVersion spdyVersion, int maxHeaderSize) {
      if(spdyVersion == null) {
         throw new NullPointerException("spdyVersion");
      } else {
         this.maxHeaderSize = maxHeaderSize;
         this.state = SpdyHeaderBlockRawDecoder.State.READ_NUM_HEADERS;
      }
   }

   private static int readLengthField(ByteBuf buffer) {
      int length = SpdyCodecUtil.getSignedInt(buffer, buffer.readerIndex());
      buffer.skipBytes(4);
      return length;
   }

   void decode(ByteBuf headerBlock, SpdyHeadersFrame frame) throws Exception {
      if(headerBlock == null) {
         throw new NullPointerException("headerBlock");
      } else if(frame == null) {
         throw new NullPointerException("frame");
      } else {
         if(this.cumulation == null) {
            this.decodeHeaderBlock(headerBlock, frame);
            if(headerBlock.isReadable()) {
               this.cumulation = headerBlock.alloc().buffer(headerBlock.readableBytes());
               this.cumulation.writeBytes(headerBlock);
            }
         } else {
            this.cumulation.writeBytes(headerBlock);
            this.decodeHeaderBlock(this.cumulation, frame);
            if(this.cumulation.isReadable()) {
               this.cumulation.discardReadBytes();
            } else {
               this.releaseBuffer();
            }
         }

      }
   }

   protected void decodeHeaderBlock(ByteBuf headerBlock, SpdyHeadersFrame frame) throws Exception {
      while(headerBlock.isReadable()) {
         switch(this.state) {
         case READ_NUM_HEADERS:
            if(headerBlock.readableBytes() < 4) {
               return;
            }

            this.numHeaders = readLengthField(headerBlock);
            if(this.numHeaders < 0) {
               this.state = SpdyHeaderBlockRawDecoder.State.ERROR;
               frame.setInvalid();
            } else {
               if(this.numHeaders == 0) {
                  this.state = SpdyHeaderBlockRawDecoder.State.END_HEADER_BLOCK;
                  break;
               }

               this.state = SpdyHeaderBlockRawDecoder.State.READ_NAME_LENGTH;
            }
            break;
         case READ_NAME_LENGTH:
            if(headerBlock.readableBytes() < 4) {
               return;
            }

            this.length = readLengthField(headerBlock);
            if(this.length <= 0) {
               this.state = SpdyHeaderBlockRawDecoder.State.ERROR;
               frame.setInvalid();
            } else {
               if(this.length <= this.maxHeaderSize && this.headerSize <= this.maxHeaderSize - this.length) {
                  this.headerSize += this.length;
                  this.state = SpdyHeaderBlockRawDecoder.State.READ_NAME;
                  break;
               }

               this.headerSize = this.maxHeaderSize + 1;
               this.state = SpdyHeaderBlockRawDecoder.State.SKIP_NAME;
               frame.setTruncated();
            }
            break;
         case READ_NAME:
            if(headerBlock.readableBytes() < this.length) {
               return;
            }

            byte[] nameBytes = new byte[this.length];
            headerBlock.readBytes(nameBytes);
            this.name = new String(nameBytes, "UTF-8");
            if(frame.headers().contains(this.name)) {
               this.state = SpdyHeaderBlockRawDecoder.State.ERROR;
               frame.setInvalid();
               break;
            }

            this.state = SpdyHeaderBlockRawDecoder.State.READ_VALUE_LENGTH;
            break;
         case SKIP_NAME:
            int skipLength = Math.min(headerBlock.readableBytes(), this.length);
            headerBlock.skipBytes(skipLength);
            this.length -= skipLength;
            if(this.length == 0) {
               this.state = SpdyHeaderBlockRawDecoder.State.READ_VALUE_LENGTH;
            }
            break;
         case READ_VALUE_LENGTH:
            if(headerBlock.readableBytes() < 4) {
               return;
            }

            this.length = readLengthField(headerBlock);
            if(this.length < 0) {
               this.state = SpdyHeaderBlockRawDecoder.State.ERROR;
               frame.setInvalid();
            } else if(this.length == 0) {
               if(!frame.isTruncated()) {
                  frame.headers().add(this.name, (Object)"");
               }

               this.name = null;
               if(--this.numHeaders == 0) {
                  this.state = SpdyHeaderBlockRawDecoder.State.END_HEADER_BLOCK;
                  break;
               }

               this.state = SpdyHeaderBlockRawDecoder.State.READ_NAME_LENGTH;
            } else {
               if(this.length <= this.maxHeaderSize && this.headerSize <= this.maxHeaderSize - this.length) {
                  this.headerSize += this.length;
                  this.state = SpdyHeaderBlockRawDecoder.State.READ_VALUE;
                  break;
               }

               this.headerSize = this.maxHeaderSize + 1;
               this.name = null;
               this.state = SpdyHeaderBlockRawDecoder.State.SKIP_VALUE;
               frame.setTruncated();
            }
            break;
         case READ_VALUE:
            if(headerBlock.readableBytes() < this.length) {
               return;
            }

            byte[] valueBytes = new byte[this.length];
            headerBlock.readBytes(valueBytes);
            int index = 0;
            int offset = 0;
            if(valueBytes[0] == 0) {
               this.state = SpdyHeaderBlockRawDecoder.State.ERROR;
               frame.setInvalid();
            } else {
               while(index < this.length) {
                  while(index < valueBytes.length && valueBytes[index] != 0) {
                     ++index;
                  }

                  if(index < valueBytes.length && (index + 1 == valueBytes.length || valueBytes[index + 1] == 0)) {
                     this.state = SpdyHeaderBlockRawDecoder.State.ERROR;
                     frame.setInvalid();
                     break;
                  }

                  String value = new String(valueBytes, offset, index - offset, "UTF-8");

                  try {
                     frame.headers().add(this.name, (Object)value);
                  } catch (IllegalArgumentException var10) {
                     this.state = SpdyHeaderBlockRawDecoder.State.ERROR;
                     frame.setInvalid();
                     break;
                  }

                  ++index;
                  offset = index;
               }

               this.name = null;
               if(this.state != SpdyHeaderBlockRawDecoder.State.ERROR) {
                  if(--this.numHeaders == 0) {
                     this.state = SpdyHeaderBlockRawDecoder.State.END_HEADER_BLOCK;
                  } else {
                     this.state = SpdyHeaderBlockRawDecoder.State.READ_NAME_LENGTH;
                  }
               }
            }
            break;
         case SKIP_VALUE:
            int skipLength = Math.min(headerBlock.readableBytes(), this.length);
            headerBlock.skipBytes(skipLength);
            this.length -= skipLength;
            if(this.length != 0) {
               break;
            }

            if(--this.numHeaders == 0) {
               this.state = SpdyHeaderBlockRawDecoder.State.END_HEADER_BLOCK;
               break;
            }

            this.state = SpdyHeaderBlockRawDecoder.State.READ_NAME_LENGTH;
            break;
         case END_HEADER_BLOCK:
            this.state = SpdyHeaderBlockRawDecoder.State.ERROR;
            frame.setInvalid();
            break;
         case ERROR:
            headerBlock.skipBytes(headerBlock.readableBytes());
            return;
         default:
            throw new Error("Shouldn\'t reach here.");
         }
      }

   }

   void endHeaderBlock(SpdyHeadersFrame frame) throws Exception {
      if(this.state != SpdyHeaderBlockRawDecoder.State.END_HEADER_BLOCK) {
         frame.setInvalid();
      }

      this.releaseBuffer();
      this.headerSize = 0;
      this.name = null;
      this.state = SpdyHeaderBlockRawDecoder.State.READ_NUM_HEADERS;
   }

   void end() {
      this.releaseBuffer();
   }

   private void releaseBuffer() {
      if(this.cumulation != null) {
         this.cumulation.release();
         this.cumulation = null;
      }

   }

   private static enum State {
      READ_NUM_HEADERS,
      READ_NAME_LENGTH,
      READ_NAME,
      SKIP_NAME,
      READ_VALUE_LENGTH,
      READ_VALUE,
      SKIP_VALUE,
      END_HEADER_BLOCK,
      ERROR;
   }
}
