package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.WrappedByteBuf;
import io.netty.util.ResourceLeak;
import java.nio.ByteOrder;

final class SimpleLeakAwareByteBuf extends WrappedByteBuf {
   private final ResourceLeak leak;

   SimpleLeakAwareByteBuf(ByteBuf buf, ResourceLeak leak) {
      super(buf);
      this.leak = leak;
   }

   public boolean release() {
      boolean deallocated = super.release();
      if(deallocated) {
         this.leak.close();
      }

      return deallocated;
   }

   public boolean release(int decrement) {
      boolean deallocated = super.release(decrement);
      if(deallocated) {
         this.leak.close();
      }

      return deallocated;
   }

   public ByteBuf order(ByteOrder endianness) {
      this.leak.record();
      return this.order() == endianness?this:new SimpleLeakAwareByteBuf(super.order(endianness), this.leak);
   }

   public ByteBuf slice() {
      return new SimpleLeakAwareByteBuf(super.slice(), this.leak);
   }

   public ByteBuf slice(int index, int length) {
      return new SimpleLeakAwareByteBuf(super.slice(index, length), this.leak);
   }

   public ByteBuf duplicate() {
      return new SimpleLeakAwareByteBuf(super.duplicate(), this.leak);
   }

   public ByteBuf readSlice(int length) {
      return new SimpleLeakAwareByteBuf(super.readSlice(length), this.leak);
   }
}
