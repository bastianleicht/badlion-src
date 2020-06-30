package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;

public interface ByteBufAllocator {
   ByteBufAllocator DEFAULT = ByteBufUtil.DEFAULT_ALLOCATOR;

   ByteBuf buffer();

   ByteBuf buffer(int var1);

   ByteBuf buffer(int var1, int var2);

   ByteBuf ioBuffer();

   ByteBuf ioBuffer(int var1);

   ByteBuf ioBuffer(int var1, int var2);

   ByteBuf heapBuffer();

   ByteBuf heapBuffer(int var1);

   ByteBuf heapBuffer(int var1, int var2);

   ByteBuf directBuffer();

   ByteBuf directBuffer(int var1);

   ByteBuf directBuffer(int var1, int var2);

   CompositeByteBuf compositeBuffer();

   CompositeByteBuf compositeBuffer(int var1);

   CompositeByteBuf compositeHeapBuffer();

   CompositeByteBuf compositeHeapBuffer(int var1);

   CompositeByteBuf compositeDirectBuffer();

   CompositeByteBuf compositeDirectBuffer(int var1);

   boolean isDirectBufferPooled();
}
