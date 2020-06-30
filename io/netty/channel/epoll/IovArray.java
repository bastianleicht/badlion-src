package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.epoll.Native;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.PlatformDependent;

final class IovArray implements ChannelOutboundBuffer.MessageProcessor {
   private static final int ADDRESS_SIZE = PlatformDependent.addressSize();
   private static final int IOV_SIZE = 2 * ADDRESS_SIZE;
   private static final int CAPACITY = Native.IOV_MAX * IOV_SIZE;
   private static final FastThreadLocal ARRAY = new FastThreadLocal() {
      protected IovArray initialValue() throws Exception {
         return new IovArray();
      }

      protected void onRemoval(IovArray value) throws Exception {
         PlatformDependent.freeMemory(value.memoryAddress);
      }
   };
   private final long memoryAddress;
   private int count;
   private long size;

   private IovArray() {
      this.memoryAddress = PlatformDependent.allocateMemory((long)CAPACITY);
   }

   private boolean add(ByteBuf buf) {
      if(this.count == Native.IOV_MAX) {
         return false;
      } else {
         int len = buf.readableBytes();
         if(len == 0) {
            return true;
         } else {
            long addr = buf.memoryAddress();
            int offset = buf.readerIndex();
            long baseOffset = this.memoryAddress(this.count++);
            long lengthOffset = baseOffset + (long)ADDRESS_SIZE;
            if(ADDRESS_SIZE == 8) {
               PlatformDependent.putLong(baseOffset, addr + (long)offset);
               PlatformDependent.putLong(lengthOffset, (long)len);
            } else {
               assert ADDRESS_SIZE == 4;

               PlatformDependent.putInt(baseOffset, (int)addr + offset);
               PlatformDependent.putInt(lengthOffset, len);
            }

            this.size += (long)len;
            return true;
         }
      }
   }

   long processWritten(int index, long written) {
      long baseOffset = this.memoryAddress(index);
      long lengthOffset = baseOffset + (long)ADDRESS_SIZE;
      if(ADDRESS_SIZE == 8) {
         long len = PlatformDependent.getLong(lengthOffset);
         if(len > written) {
            long offset = PlatformDependent.getLong(baseOffset);
            PlatformDependent.putLong(baseOffset, offset + written);
            PlatformDependent.putLong(lengthOffset, len - written);
            return -1L;
         } else {
            return len;
         }
      } else {
         assert ADDRESS_SIZE == 4;

         long len = (long)PlatformDependent.getInt(lengthOffset);
         if(len > written) {
            int offset = PlatformDependent.getInt(baseOffset);
            PlatformDependent.putInt(baseOffset, (int)((long)offset + written));
            PlatformDependent.putInt(lengthOffset, (int)(len - written));
            return -1L;
         } else {
            return len;
         }
      }
   }

   int count() {
      return this.count;
   }

   long size() {
      return this.size;
   }

   long memoryAddress(int offset) {
      return this.memoryAddress + (long)(IOV_SIZE * offset);
   }

   public boolean processMessage(Object msg) throws Exception {
      return msg instanceof ByteBuf && this.add((ByteBuf)msg);
   }

   static IovArray get(ChannelOutboundBuffer buffer) throws Exception {
      IovArray array = (IovArray)ARRAY.get();
      array.size = 0L;
      array.count = 0;
      buffer.forEachFlushedMessage(array);
      return array;
   }
}
