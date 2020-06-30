package io.netty.handler.codec.marshalling;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.jboss.marshalling.ByteInput;

class ChannelBufferByteInput implements ByteInput {
   private final ByteBuf buffer;

   ChannelBufferByteInput(ByteBuf buffer) {
      this.buffer = buffer;
   }

   public void close() throws IOException {
   }

   public int available() throws IOException {
      return this.buffer.readableBytes();
   }

   public int read() throws IOException {
      return this.buffer.isReadable()?this.buffer.readByte() & 255:-1;
   }

   public int read(byte[] array) throws IOException {
      return this.read(array, 0, array.length);
   }

   public int read(byte[] dst, int dstIndex, int length) throws IOException {
      int available = this.available();
      if(available == 0) {
         return -1;
      } else {
         length = Math.min(available, length);
         this.buffer.readBytes(dst, dstIndex, length);
         return length;
      }
   }

   public long skip(long bytes) throws IOException {
      int readable = this.buffer.readableBytes();
      if((long)readable < bytes) {
         bytes = (long)readable;
      }

      this.buffer.readerIndex((int)((long)this.buffer.readerIndex() + bytes));
      return bytes;
   }
}
