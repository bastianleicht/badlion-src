package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PooledByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.UnsafeDirectSwappedByteBuf;
import io.netty.util.Recycler;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

final class PooledUnsafeDirectByteBuf extends PooledByteBuf {
   private static final boolean NATIVE_ORDER = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
   private static final Recycler RECYCLER = new Recycler() {
      protected PooledUnsafeDirectByteBuf newObject(Recycler.Handle handle) {
         return new PooledUnsafeDirectByteBuf(handle, 0);
      }
   };
   private long memoryAddress;

   static PooledUnsafeDirectByteBuf newInstance(int maxCapacity) {
      PooledUnsafeDirectByteBuf buf = (PooledUnsafeDirectByteBuf)RECYCLER.get();
      buf.setRefCnt(1);
      buf.maxCapacity(maxCapacity);
      return buf;
   }

   private PooledUnsafeDirectByteBuf(Recycler.Handle recyclerHandle, int maxCapacity) {
      super(recyclerHandle, maxCapacity);
   }

   void init(PoolChunk chunk, long handle, int offset, int length, int maxLength) {
      super.init(chunk, handle, offset, length, maxLength);
      this.initMemoryAddress();
   }

   void initUnpooled(PoolChunk chunk, int length) {
      super.initUnpooled(chunk, length);
      this.initMemoryAddress();
   }

   private void initMemoryAddress() {
      this.memoryAddress = PlatformDependent.directBufferAddress((ByteBuffer)this.memory) + (long)this.offset;
   }

   protected ByteBuffer newInternalNioBuffer(ByteBuffer memory) {
      return memory.duplicate();
   }

   public boolean isDirect() {
      return true;
   }

   protected byte _getByte(int index) {
      return PlatformDependent.getByte(this.addr(index));
   }

   protected short _getShort(int index) {
      short v = PlatformDependent.getShort(this.addr(index));
      return NATIVE_ORDER?v:Short.reverseBytes(v);
   }

   protected int _getUnsignedMedium(int index) {
      long addr = this.addr(index);
      return (PlatformDependent.getByte(addr) & 255) << 16 | (PlatformDependent.getByte(addr + 1L) & 255) << 8 | PlatformDependent.getByte(addr + 2L) & 255;
   }

   protected int _getInt(int index) {
      int v = PlatformDependent.getInt(this.addr(index));
      return NATIVE_ORDER?v:Integer.reverseBytes(v);
   }

   protected long _getLong(int index) {
      long v = PlatformDependent.getLong(this.addr(index));
      return NATIVE_ORDER?v:Long.reverseBytes(v);
   }

   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
      this.checkIndex(index, length);
      if(dst == null) {
         throw new NullPointerException("dst");
      } else if(dstIndex >= 0 && dstIndex <= dst.capacity() - length) {
         if(length != 0) {
            if(dst.hasMemoryAddress()) {
               PlatformDependent.copyMemory(this.addr(index), dst.memoryAddress() + (long)dstIndex, (long)length);
            } else if(dst.hasArray()) {
               PlatformDependent.copyMemory(this.addr(index), dst.array(), dst.arrayOffset() + dstIndex, (long)length);
            } else {
               dst.setBytes(dstIndex, (ByteBuf)this, index, length);
            }
         }

         return this;
      } else {
         throw new IndexOutOfBoundsException("dstIndex: " + dstIndex);
      }
   }

   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
      this.checkIndex(index, length);
      if(dst == null) {
         throw new NullPointerException("dst");
      } else if(dstIndex >= 0 && dstIndex <= dst.length - length) {
         if(length != 0) {
            PlatformDependent.copyMemory(this.addr(index), dst, dstIndex, (long)length);
         }

         return this;
      } else {
         throw new IndexOutOfBoundsException("dstIndex: " + dstIndex);
      }
   }

   public ByteBuf getBytes(int index, ByteBuffer dst) {
      this.getBytes(index, dst, false);
      return this;
   }

   private void getBytes(int index, ByteBuffer dst, boolean internal) {
      this.checkIndex(index);
      int bytesToCopy = Math.min(this.capacity() - index, dst.remaining());
      ByteBuffer tmpBuf;
      if(internal) {
         tmpBuf = this.internalNioBuffer();
      } else {
         tmpBuf = ((ByteBuffer)this.memory).duplicate();
      }

      index = this.idx(index);
      tmpBuf.clear().position(index).limit(index + bytesToCopy);
      dst.put(tmpBuf);
   }

   public ByteBuf readBytes(ByteBuffer dst) {
      int length = dst.remaining();
      this.checkReadableBytes(length);
      this.getBytes(this.readerIndex, dst, true);
      this.readerIndex += length;
      return this;
   }

   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
      this.checkIndex(index, length);
      if(length != 0) {
         byte[] tmp = new byte[length];
         PlatformDependent.copyMemory(this.addr(index), tmp, 0, (long)length);
         out.write(tmp);
      }

      return this;
   }

   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
      return this.getBytes(index, out, length, false);
   }

   private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
      this.checkIndex(index, length);
      if(length == 0) {
         return 0;
      } else {
         ByteBuffer tmpBuf;
         if(internal) {
            tmpBuf = this.internalNioBuffer();
         } else {
            tmpBuf = ((ByteBuffer)this.memory).duplicate();
         }

         index = this.idx(index);
         tmpBuf.clear().position(index).limit(index + length);
         return out.write(tmpBuf);
      }
   }

   public int readBytes(GatheringByteChannel out, int length) throws IOException {
      this.checkReadableBytes(length);
      int readBytes = this.getBytes(this.readerIndex, out, length, true);
      this.readerIndex += readBytes;
      return readBytes;
   }

   protected void _setByte(int index, int value) {
      PlatformDependent.putByte(this.addr(index), (byte)value);
   }

   protected void _setShort(int index, int value) {
      PlatformDependent.putShort(this.addr(index), NATIVE_ORDER?(short)value:Short.reverseBytes((short)value));
   }

   protected void _setMedium(int index, int value) {
      long addr = this.addr(index);
      PlatformDependent.putByte(addr, (byte)(value >>> 16));
      PlatformDependent.putByte(addr + 1L, (byte)(value >>> 8));
      PlatformDependent.putByte(addr + 2L, (byte)value);
   }

   protected void _setInt(int index, int value) {
      PlatformDependent.putInt(this.addr(index), NATIVE_ORDER?value:Integer.reverseBytes(value));
   }

   protected void _setLong(int index, long value) {
      PlatformDependent.putLong(this.addr(index), NATIVE_ORDER?value:Long.reverseBytes(value));
   }

   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
      this.checkIndex(index, length);
      if(src == null) {
         throw new NullPointerException("src");
      } else if(srcIndex >= 0 && srcIndex <= src.capacity() - length) {
         if(length != 0) {
            if(src.hasMemoryAddress()) {
               PlatformDependent.copyMemory(src.memoryAddress() + (long)srcIndex, this.addr(index), (long)length);
            } else if(src.hasArray()) {
               PlatformDependent.copyMemory(src.array(), src.arrayOffset() + srcIndex, this.addr(index), (long)length);
            } else {
               src.getBytes(srcIndex, (ByteBuf)this, index, length);
            }
         }

         return this;
      } else {
         throw new IndexOutOfBoundsException("srcIndex: " + srcIndex);
      }
   }

   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
      this.checkIndex(index, length);
      if(length != 0) {
         PlatformDependent.copyMemory(src, srcIndex, this.addr(index), (long)length);
      }

      return this;
   }

   public ByteBuf setBytes(int index, ByteBuffer src) {
      this.checkIndex(index, src.remaining());
      ByteBuffer tmpBuf = this.internalNioBuffer();
      if(src == tmpBuf) {
         src = src.duplicate();
      }

      index = this.idx(index);
      tmpBuf.clear().position(index).limit(index + src.remaining());
      tmpBuf.put(src);
      return this;
   }

   public int setBytes(int index, InputStream in, int length) throws IOException {
      this.checkIndex(index, length);
      byte[] tmp = new byte[length];
      int readBytes = in.read(tmp);
      if(readBytes > 0) {
         PlatformDependent.copyMemory(tmp, 0, this.addr(index), (long)readBytes);
      }

      return readBytes;
   }

   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
      this.checkIndex(index, length);
      ByteBuffer tmpBuf = this.internalNioBuffer();
      index = this.idx(index);
      tmpBuf.clear().position(index).limit(index + length);

      try {
         return in.read(tmpBuf);
      } catch (ClosedChannelException var6) {
         return -1;
      }
   }

   public ByteBuf copy(int index, int length) {
      this.checkIndex(index, length);
      ByteBuf copy = this.alloc().directBuffer(length, this.maxCapacity());
      if(length != 0) {
         if(copy.hasMemoryAddress()) {
            PlatformDependent.copyMemory(this.addr(index), copy.memoryAddress(), (long)length);
            copy.setIndex(0, length);
         } else {
            copy.writeBytes((ByteBuf)this, index, length);
         }
      }

      return copy;
   }

   public int nioBufferCount() {
      return 1;
   }

   public ByteBuffer[] nioBuffers(int index, int length) {
      return new ByteBuffer[]{this.nioBuffer(index, length)};
   }

   public ByteBuffer nioBuffer(int index, int length) {
      this.checkIndex(index, length);
      index = this.idx(index);
      return ((ByteBuffer)((ByteBuffer)this.memory).duplicate().position(index).limit(index + length)).slice();
   }

   public ByteBuffer internalNioBuffer(int index, int length) {
      this.checkIndex(index, length);
      index = this.idx(index);
      return (ByteBuffer)this.internalNioBuffer().clear().position(index).limit(index + length);
   }

   public boolean hasArray() {
      return false;
   }

   public byte[] array() {
      throw new UnsupportedOperationException("direct buffer");
   }

   public int arrayOffset() {
      throw new UnsupportedOperationException("direct buffer");
   }

   public boolean hasMemoryAddress() {
      return true;
   }

   public long memoryAddress() {
      return this.memoryAddress;
   }

   private long addr(int index) {
      return this.memoryAddress + (long)index;
   }

   protected Recycler recycler() {
      return RECYCLER;
   }

   protected SwappedByteBuf newSwappedByteBuf() {
      return new UnsafeDirectSwappedByteBuf(this);
   }
}
