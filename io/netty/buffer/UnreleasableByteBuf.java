package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.WrappedByteBuf;
import java.nio.ByteOrder;

final class UnreleasableByteBuf extends WrappedByteBuf {
   private SwappedByteBuf swappedBuf;

   UnreleasableByteBuf(ByteBuf buf) {
      super(buf);
   }

   public ByteBuf order(ByteOrder endianness) {
      if(endianness == null) {
         throw new NullPointerException("endianness");
      } else if(endianness == this.order()) {
         return this;
      } else {
         SwappedByteBuf swappedBuf = this.swappedBuf;
         if(swappedBuf == null) {
            this.swappedBuf = swappedBuf = new SwappedByteBuf(this);
         }

         return swappedBuf;
      }
   }

   public ByteBuf readSlice(int length) {
      return new UnreleasableByteBuf(this.buf.readSlice(length));
   }

   public ByteBuf slice() {
      return new UnreleasableByteBuf(this.buf.slice());
   }

   public ByteBuf slice(int index, int length) {
      return new UnreleasableByteBuf(this.buf.slice(index, length));
   }

   public ByteBuf duplicate() {
      return new UnreleasableByteBuf(this.buf.duplicate());
   }

   public ByteBuf retain(int increment) {
      return this;
   }

   public ByteBuf retain() {
      return this;
   }

   public boolean release() {
      return false;
   }

   public boolean release(int decrement) {
      return false;
   }
}
