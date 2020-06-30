package io.netty.buffer;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class UnpooledHeapByteBuf extends AbstractReferenceCountedByteBuf {
   private final ByteBufAllocator alloc;
   private byte[] array;
   private ByteBuffer tmpNioBuf;

   protected UnpooledHeapByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
      this(alloc, new byte[initialCapacity], 0, 0, maxCapacity);
   }

   protected UnpooledHeapByteBuf(ByteBufAllocator alloc, byte[] initialArray, int maxCapacity) {
      this(alloc, initialArray, 0, initialArray.length, maxCapacity);
   }

   private UnpooledHeapByteBuf(ByteBufAllocator alloc, byte[] initialArray, int readerIndex, int writerIndex, int maxCapacity) {
      super(maxCapacity);
      if(alloc == null) {
         throw new NullPointerException("alloc");
      } else if(initialArray == null) {
         throw new NullPointerException("initialArray");
      } else if(initialArray.length > maxCapacity) {
         throw new IllegalArgumentException(String.format("initialCapacity(%d) > maxCapacity(%d)", new Object[]{Integer.valueOf(initialArray.length), Integer.valueOf(maxCapacity)}));
      } else {
         this.alloc = alloc;
         this.setArray(initialArray);
         this.setIndex(readerIndex, writerIndex);
      }
   }

   private void setArray(byte[] initialArray) {
      this.array = initialArray;
      this.tmpNioBuf = null;
   }

   public ByteBufAllocator alloc() {
      return this.alloc;
   }

   public ByteOrder order() {
      return ByteOrder.BIG_ENDIAN;
   }

   public boolean isDirect() {
      return false;
   }

   public int capacity() {
      this.ensureAccessible();
      return this.array.length;
   }

   public ByteBuf capacity(int newCapacity) {
      this.ensureAccessible();
      if(newCapacity >= 0 && newCapacity <= this.maxCapacity()) {
         int oldCapacity = this.array.length;
         if(newCapacity > oldCapacity) {
            byte[] newArray = new byte[newCapacity];
            System.arraycopy(this.array, 0, newArray, 0, this.array.length);
            this.setArray(newArray);
         } else if(newCapacity < oldCapacity) {
            byte[] newArray = new byte[newCapacity];
            int readerIndex = this.readerIndex();
            if(readerIndex < newCapacity) {
               int writerIndex = this.writerIndex();
               if(writerIndex > newCapacity) {
                  writerIndex = newCapacity;
                  this.writerIndex(newCapacity);
               }

               System.arraycopy(this.array, readerIndex, newArray, readerIndex, writerIndex - readerIndex);
            } else {
               this.setIndex(newCapacity, newCapacity);
            }

            this.setArray(newArray);
         }

         return this;
      } else {
         throw new IllegalArgumentException("newCapacity: " + newCapacity);
      }
   }

   public boolean hasArray() {
      return true;
   }

   public byte[] array() {
      this.ensureAccessible();
      return this.array;
   }

   public int arrayOffset() {
      return 0;
   }

   public boolean hasMemoryAddress() {
      return false;
   }

   public long memoryAddress() {
      throw new UnsupportedOperationException();
   }

   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
      this.checkDstIndex(index, length, dstIndex, dst.capacity());
      if(dst.hasMemoryAddress()) {
         PlatformDependent.copyMemory(this.array, index, dst.memoryAddress() + (long)dstIndex, (long)length);
      } else if(dst.hasArray()) {
         this.getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
      } else {
         dst.setBytes(dstIndex, this.array, index, length);
      }

      return this;
   }

   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
      this.checkDstIndex(index, length, dstIndex, dst.length);
      System.arraycopy(this.array, index, dst, dstIndex, length);
      return this;
   }

   public ByteBuf getBytes(int index, ByteBuffer dst) {
      this.ensureAccessible();
      dst.put(this.array, index, Math.min(this.capacity() - index, dst.remaining()));
      return this;
   }

   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
      this.ensureAccessible();
      out.write(this.array, index, length);
      return this;
   }

   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
      this.ensureAccessible();
      return this.getBytes(index, out, length, false);
   }

   private int getBytes(int index, GatheringByteChannel out, int length, boolean internal) throws IOException {
      this.ensureAccessible();
      ByteBuffer tmpBuf;
      if(internal) {
         tmpBuf = this.internalNioBuffer();
      } else {
         tmpBuf = ByteBuffer.wrap(this.array);
      }

      return out.write((ByteBuffer)tmpBuf.clear().position(index).limit(index + length));
   }

   public int readBytes(GatheringByteChannel out, int length) throws IOException {
      this.checkReadableBytes(length);
      int readBytes = this.getBytes(this.readerIndex, out, length, true);
      this.readerIndex += readBytes;
      return readBytes;
   }

   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
      this.checkSrcIndex(index, length, srcIndex, src.capacity());
      if(src.hasMemoryAddress()) {
         PlatformDependent.copyMemory(src.memoryAddress() + (long)srcIndex, this.array, index, (long)length);
      } else if(src.hasArray()) {
         this.setBytes(index, src.array(), src.arrayOffset() + srcIndex, length);
      } else {
         src.getBytes(srcIndex, this.array, index, length);
      }

      return this;
   }

   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
      this.checkSrcIndex(index, length, srcIndex, src.length);
      System.arraycopy(src, srcIndex, this.array, index, length);
      return this;
   }

   public ByteBuf setBytes(int index, ByteBuffer src) {
      this.ensureAccessible();
      src.get(this.array, index, src.remaining());
      return this;
   }

   public int setBytes(int index, InputStream in, int length) throws IOException {
      this.ensureAccessible();
      return in.read(this.array, index, length);
   }

   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
      this.ensureAccessible();

      try {
         return in.read((ByteBuffer)this.internalNioBuffer().clear().position(index).limit(index + length));
      } catch (ClosedChannelException var5) {
         return -1;
      }
   }

   public int nioBufferCount() {
      return 1;
   }

   public ByteBuffer nioBuffer(int index, int length) {
      this.ensureAccessible();
      return ByteBuffer.wrap(this.array, index, length).slice();
   }

   public ByteBuffer[] nioBuffers(int index, int length) {
      return new ByteBuffer[]{this.nioBuffer(index, length)};
   }

   public ByteBuffer internalNioBuffer(int index, int length) {
      this.checkIndex(index, length);
      return (ByteBuffer)this.internalNioBuffer().clear().position(index).limit(index + length);
   }

   public byte getByte(int index) {
      this.ensureAccessible();
      return this._getByte(index);
   }

   protected byte _getByte(int index) {
      return this.array[index];
   }

   public short getShort(int index) {
      this.ensureAccessible();
      return this._getShort(index);
   }

   protected short _getShort(int index) {
      return (short)(this.array[index] << 8 | this.array[index + 1] & 255);
   }

   public int getUnsignedMedium(int index) {
      this.ensureAccessible();
      return this._getUnsignedMedium(index);
   }

   protected int _getUnsignedMedium(int index) {
      return (this.array[index] & 255) << 16 | (this.array[index + 1] & 255) << 8 | this.array[index + 2] & 255;
   }

   public int getInt(int index) {
      this.ensureAccessible();
      return this._getInt(index);
   }

   protected int _getInt(int index) {
      return (this.array[index] & 255) << 24 | (this.array[index + 1] & 255) << 16 | (this.array[index + 2] & 255) << 8 | this.array[index + 3] & 255;
   }

   public long getLong(int index) {
      this.ensureAccessible();
      return this._getLong(index);
   }

   protected long _getLong(int index) {
      return ((long)this.array[index] & 255L) << 56 | ((long)this.array[index + 1] & 255L) << 48 | ((long)this.array[index + 2] & 255L) << 40 | ((long)this.array[index + 3] & 255L) << 32 | ((long)this.array[index + 4] & 255L) << 24 | ((long)this.array[index + 5] & 255L) << 16 | ((long)this.array[index + 6] & 255L) << 8 | (long)this.array[index + 7] & 255L;
   }

   public ByteBuf setByte(int index, int value) {
      this.ensureAccessible();
      this._setByte(index, value);
      return this;
   }

   protected void _setByte(int index, int value) {
      this.array[index] = (byte)value;
   }

   public ByteBuf setShort(int index, int value) {
      this.ensureAccessible();
      this._setShort(index, value);
      return this;
   }

   protected void _setShort(int index, int value) {
      this.array[index] = (byte)(value >>> 8);
      this.array[index + 1] = (byte)value;
   }

   public ByteBuf setMedium(int index, int value) {
      this.ensureAccessible();
      this._setMedium(index, value);
      return this;
   }

   protected void _setMedium(int index, int value) {
      this.array[index] = (byte)(value >>> 16);
      this.array[index + 1] = (byte)(value >>> 8);
      this.array[index + 2] = (byte)value;
   }

   public ByteBuf setInt(int index, int value) {
      this.ensureAccessible();
      this._setInt(index, value);
      return this;
   }

   protected void _setInt(int index, int value) {
      this.array[index] = (byte)(value >>> 24);
      this.array[index + 1] = (byte)(value >>> 16);
      this.array[index + 2] = (byte)(value >>> 8);
      this.array[index + 3] = (byte)value;
   }

   public ByteBuf setLong(int index, long value) {
      this.ensureAccessible();
      this._setLong(index, value);
      return this;
   }

   protected void _setLong(int index, long value) {
      this.array[index] = (byte)((int)(value >>> 56));
      this.array[index + 1] = (byte)((int)(value >>> 48));
      this.array[index + 2] = (byte)((int)(value >>> 40));
      this.array[index + 3] = (byte)((int)(value >>> 32));
      this.array[index + 4] = (byte)((int)(value >>> 24));
      this.array[index + 5] = (byte)((int)(value >>> 16));
      this.array[index + 6] = (byte)((int)(value >>> 8));
      this.array[index + 7] = (byte)((int)value);
   }

   public ByteBuf copy(int index, int length) {
      this.checkIndex(index, length);
      byte[] copiedArray = new byte[length];
      System.arraycopy(this.array, index, copiedArray, 0, length);
      return new UnpooledHeapByteBuf(this.alloc(), copiedArray, this.maxCapacity());
   }

   private ByteBuffer internalNioBuffer() {
      ByteBuffer tmpNioBuf = this.tmpNioBuf;
      if(tmpNioBuf == null) {
         this.tmpNioBuf = tmpNioBuf = ByteBuffer.wrap(this.array);
      }

      return tmpNioBuf;
   }

   protected void deallocate() {
      this.array = null;
   }

   public ByteBuf unwrap() {
      return null;
   }
}
