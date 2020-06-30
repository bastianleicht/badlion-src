package io.netty.buffer;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.UnsafeDirectSwappedByteBuf;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class UnpooledUnsafeDirectByteBuf extends AbstractReferenceCountedByteBuf {
   private static final boolean NATIVE_ORDER = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
   private final ByteBufAllocator alloc;
   private long memoryAddress;
   private ByteBuffer buffer;
   private ByteBuffer tmpNioBuf;
   private int capacity;
   private boolean doNotFree;

   protected UnpooledUnsafeDirectByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
      super(maxCapacity);
      if(alloc == null) {
         throw new NullPointerException("alloc");
      } else if(initialCapacity < 0) {
         throw new IllegalArgumentException("initialCapacity: " + initialCapacity);
      } else if(maxCapacity < 0) {
         throw new IllegalArgumentException("maxCapacity: " + maxCapacity);
      } else if(initialCapacity > maxCapacity) {
         throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", new Object[]{Integer.valueOf(initialCapacity), Integer.valueOf(maxCapacity)}));
      } else {
         this.alloc = alloc;
         this.setByteBuffer(this.allocateDirect(initialCapacity));
      }
   }

   protected UnpooledUnsafeDirectByteBuf(ByteBufAllocator alloc, ByteBuffer initialBuffer, int maxCapacity) {
      super(maxCapacity);
      if(alloc == null) {
         throw new NullPointerException("alloc");
      } else if(initialBuffer == null) {
         throw new NullPointerException("initialBuffer");
      } else if(!initialBuffer.isDirect()) {
         throw new IllegalArgumentException("initialBuffer is not a direct buffer.");
      } else if(initialBuffer.isReadOnly()) {
         throw new IllegalArgumentException("initialBuffer is a read-only buffer.");
      } else {
         int initialCapacity = initialBuffer.remaining();
         if(initialCapacity > maxCapacity) {
            throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", new Object[]{Integer.valueOf(initialCapacity), Integer.valueOf(maxCapacity)}));
         } else {
            this.alloc = alloc;
            this.doNotFree = true;
            this.setByteBuffer(initialBuffer.slice().order(ByteOrder.BIG_ENDIAN));
            this.writerIndex(initialCapacity);
         }
      }
   }

   protected ByteBuffer allocateDirect(int initialCapacity) {
      return ByteBuffer.allocateDirect(initialCapacity);
   }

   protected void freeDirect(ByteBuffer buffer) {
      PlatformDependent.freeDirectBuffer(buffer);
   }

   private void setByteBuffer(ByteBuffer buffer) {
      ByteBuffer oldBuffer = this.buffer;
      if(oldBuffer != null) {
         if(this.doNotFree) {
            this.doNotFree = false;
         } else {
            this.freeDirect(oldBuffer);
         }
      }

      this.buffer = buffer;
      this.memoryAddress = PlatformDependent.directBufferAddress(buffer);
      this.tmpNioBuf = null;
      this.capacity = buffer.remaining();
   }

   public boolean isDirect() {
      return true;
   }

   public int capacity() {
      return this.capacity;
   }

   public ByteBuf capacity(int newCapacity) {
      this.ensureAccessible();
      if(newCapacity >= 0 && newCapacity <= this.maxCapacity()) {
         int readerIndex = this.readerIndex();
         int writerIndex = this.writerIndex();
         int oldCapacity = this.capacity;
         if(newCapacity > oldCapacity) {
            ByteBuffer oldBuffer = this.buffer;
            ByteBuffer newBuffer = this.allocateDirect(newCapacity);
            oldBuffer.position(0).limit(oldBuffer.capacity());
            newBuffer.position(0).limit(oldBuffer.capacity());
            newBuffer.put(oldBuffer);
            newBuffer.clear();
            this.setByteBuffer(newBuffer);
         } else if(newCapacity < oldCapacity) {
            ByteBuffer oldBuffer = this.buffer;
            ByteBuffer newBuffer = this.allocateDirect(newCapacity);
            if(readerIndex < newCapacity) {
               if(writerIndex > newCapacity) {
                  writerIndex = newCapacity;
                  this.writerIndex(newCapacity);
               }

               oldBuffer.position(readerIndex).limit(writerIndex);
               newBuffer.position(readerIndex).limit(writerIndex);
               newBuffer.put(oldBuffer);
               newBuffer.clear();
            } else {
               this.setIndex(newCapacity, newCapacity);
            }

            this.setByteBuffer(newBuffer);
         }

         return this;
      } else {
         throw new IllegalArgumentException("newCapacity: " + newCapacity);
      }
   }

   public ByteBufAllocator alloc() {
      return this.alloc;
   }

   public ByteOrder order() {
      return ByteOrder.BIG_ENDIAN;
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
         if(dst.hasMemoryAddress()) {
            PlatformDependent.copyMemory(this.addr(index), dst.memoryAddress() + (long)dstIndex, (long)length);
         } else if(dst.hasArray()) {
            PlatformDependent.copyMemory(this.addr(index), dst.array(), dst.arrayOffset() + dstIndex, (long)length);
         } else {
            dst.setBytes(dstIndex, (ByteBuf)this, index, length);
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
         throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", new Object[]{Integer.valueOf(dstIndex), Integer.valueOf(length), Integer.valueOf(dst.length)}));
      }
   }

   public ByteBuf getBytes(int index, ByteBuffer dst) {
      this.getBytes(index, dst, false);
      return this;
   }

   private void getBytes(int index, ByteBuffer dst, boolean internal) {
      this.checkIndex(index);
      if(dst == null) {
         throw new NullPointerException("dst");
      } else {
         int bytesToCopy = Math.min(this.capacity() - index, dst.remaining());
         ByteBuffer tmpBuf;
         if(internal) {
            tmpBuf = this.internalNioBuffer();
         } else {
            tmpBuf = this.buffer.duplicate();
         }

         tmpBuf.clear().position(index).limit(index + bytesToCopy);
         dst.put(tmpBuf);
      }
   }

   public ByteBuf readBytes(ByteBuffer dst) {
      int length = dst.remaining();
      this.checkReadableBytes(length);
      this.getBytes(this.readerIndex, dst, true);
      this.readerIndex += length;
      return this;
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
      this.ensureAccessible();
      ByteBuffer tmpBuf = this.internalNioBuffer();
      if(src == tmpBuf) {
         src = src.duplicate();
      }

      tmpBuf.clear().position(index).limit(index + src.remaining());
      tmpBuf.put(src);
      return this;
   }

   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
      this.ensureAccessible();
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
      this.ensureAccessible();
      if(length == 0) {
         return 0;
      } else {
         ByteBuffer tmpBuf;
         if(internal) {
            tmpBuf = this.internalNioBuffer();
         } else {
            tmpBuf = this.buffer.duplicate();
         }

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
      this.ensureAccessible();
      ByteBuffer tmpBuf = this.internalNioBuffer();
      tmpBuf.clear().position(index).limit(index + length);

      try {
         return in.read(tmpBuf);
      } catch (ClosedChannelException var6) {
         return -1;
      }
   }

   public int nioBufferCount() {
      return 1;
   }

   public ByteBuffer[] nioBuffers(int index, int length) {
      return new ByteBuffer[]{this.nioBuffer(index, length)};
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

   public ByteBuffer internalNioBuffer(int index, int length) {
      this.checkIndex(index, length);
      return (ByteBuffer)this.internalNioBuffer().clear().position(index).limit(index + length);
   }

   private ByteBuffer internalNioBuffer() {
      ByteBuffer tmpNioBuf = this.tmpNioBuf;
      if(tmpNioBuf == null) {
         this.tmpNioBuf = tmpNioBuf = this.buffer.duplicate();
      }

      return tmpNioBuf;
   }

   public ByteBuffer nioBuffer(int index, int length) {
      this.checkIndex(index, length);
      return ((ByteBuffer)this.buffer.duplicate().position(index).limit(index + length)).slice();
   }

   protected void deallocate() {
      ByteBuffer buffer = this.buffer;
      if(buffer != null) {
         this.buffer = null;
         if(!this.doNotFree) {
            this.freeDirect(buffer);
         }

      }
   }

   public ByteBuf unwrap() {
      return null;
   }

   long addr(int index) {
      return this.memoryAddress + (long)index;
   }

   protected SwappedByteBuf newSwappedByteBuf() {
      return new UnsafeDirectSwappedByteBuf(this);
   }
}
