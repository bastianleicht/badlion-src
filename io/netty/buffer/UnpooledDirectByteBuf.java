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

public class UnpooledDirectByteBuf extends AbstractReferenceCountedByteBuf {
   private final ByteBufAllocator alloc;
   private ByteBuffer buffer;
   private ByteBuffer tmpNioBuf;
   private int capacity;
   private boolean doNotFree;

   protected UnpooledDirectByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
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
         this.setByteBuffer(ByteBuffer.allocateDirect(initialCapacity));
      }
   }

   protected UnpooledDirectByteBuf(ByteBufAllocator alloc, ByteBuffer initialBuffer, int maxCapacity) {
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
      return false;
   }

   public long memoryAddress() {
      throw new UnsupportedOperationException();
   }

   public byte getByte(int index) {
      this.ensureAccessible();
      return this._getByte(index);
   }

   protected byte _getByte(int index) {
      return this.buffer.get(index);
   }

   public short getShort(int index) {
      this.ensureAccessible();
      return this._getShort(index);
   }

   protected short _getShort(int index) {
      return this.buffer.getShort(index);
   }

   public int getUnsignedMedium(int index) {
      this.ensureAccessible();
      return this._getUnsignedMedium(index);
   }

   protected int _getUnsignedMedium(int index) {
      return (this.getByte(index) & 255) << 16 | (this.getByte(index + 1) & 255) << 8 | this.getByte(index + 2) & 255;
   }

   public int getInt(int index) {
      this.ensureAccessible();
      return this._getInt(index);
   }

   protected int _getInt(int index) {
      return this.buffer.getInt(index);
   }

   public long getLong(int index) {
      this.ensureAccessible();
      return this._getLong(index);
   }

   protected long _getLong(int index) {
      return this.buffer.getLong(index);
   }

   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
      this.checkDstIndex(index, length, dstIndex, dst.capacity());
      if(dst.hasArray()) {
         this.getBytes(index, dst.array(), dst.arrayOffset() + dstIndex, length);
      } else if(dst.nioBufferCount() > 0) {
         for(ByteBuffer bb : dst.nioBuffers(dstIndex, length)) {
            int bbLen = bb.remaining();
            this.getBytes(index, bb);
            index += bbLen;
         }
      } else {
         dst.setBytes(dstIndex, (ByteBuf)this, index, length);
      }

      return this;
   }

   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
      this.getBytes(index, dst, dstIndex, length, false);
      return this;
   }

   private void getBytes(int index, byte[] dst, int dstIndex, int length, boolean internal) {
      this.checkDstIndex(index, length, dstIndex, dst.length);
      if(dstIndex >= 0 && dstIndex <= dst.length - length) {
         ByteBuffer tmpBuf;
         if(internal) {
            tmpBuf = this.internalNioBuffer();
         } else {
            tmpBuf = this.buffer.duplicate();
         }

         tmpBuf.clear().position(index).limit(index + length);
         tmpBuf.get(dst, dstIndex, length);
      } else {
         throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", new Object[]{Integer.valueOf(dstIndex), Integer.valueOf(length), Integer.valueOf(dst.length)}));
      }
   }

   public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
      this.checkReadableBytes(length);
      this.getBytes(this.readerIndex, dst, dstIndex, length, true);
      this.readerIndex += length;
      return this;
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

   public ByteBuf setByte(int index, int value) {
      this.ensureAccessible();
      this._setByte(index, value);
      return this;
   }

   protected void _setByte(int index, int value) {
      this.buffer.put(index, (byte)value);
   }

   public ByteBuf setShort(int index, int value) {
      this.ensureAccessible();
      this._setShort(index, value);
      return this;
   }

   protected void _setShort(int index, int value) {
      this.buffer.putShort(index, (short)value);
   }

   public ByteBuf setMedium(int index, int value) {
      this.ensureAccessible();
      this._setMedium(index, value);
      return this;
   }

   protected void _setMedium(int index, int value) {
      this.setByte(index, (byte)(value >>> 16));
      this.setByte(index + 1, (byte)(value >>> 8));
      this.setByte(index + 2, (byte)value);
   }

   public ByteBuf setInt(int index, int value) {
      this.ensureAccessible();
      this._setInt(index, value);
      return this;
   }

   protected void _setInt(int index, int value) {
      this.buffer.putInt(index, value);
   }

   public ByteBuf setLong(int index, long value) {
      this.ensureAccessible();
      this._setLong(index, value);
      return this;
   }

   protected void _setLong(int index, long value) {
      this.buffer.putLong(index, value);
   }

   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
      this.checkSrcIndex(index, length, srcIndex, src.capacity());
      if(src.nioBufferCount() > 0) {
         for(ByteBuffer bb : src.nioBuffers(srcIndex, length)) {
            int bbLen = bb.remaining();
            this.setBytes(index, bb);
            index += bbLen;
         }
      } else {
         src.getBytes(srcIndex, (ByteBuf)this, index, length);
      }

      return this;
   }

   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
      this.checkSrcIndex(index, length, srcIndex, src.length);
      ByteBuffer tmpBuf = this.internalNioBuffer();
      tmpBuf.clear().position(index).limit(index + length);
      tmpBuf.put(src, srcIndex, length);
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
      this.getBytes(index, out, length, false);
      return this;
   }

   private void getBytes(int index, OutputStream out, int length, boolean internal) throws IOException {
      this.ensureAccessible();
      if(length != 0) {
         if(this.buffer.hasArray()) {
            out.write(this.buffer.array(), index + this.buffer.arrayOffset(), length);
         } else {
            byte[] tmp = new byte[length];
            ByteBuffer tmpBuf;
            if(internal) {
               tmpBuf = this.internalNioBuffer();
            } else {
               tmpBuf = this.buffer.duplicate();
            }

            tmpBuf.clear().position(index);
            tmpBuf.get(tmp);
            out.write(tmp);
         }

      }
   }

   public ByteBuf readBytes(OutputStream out, int length) throws IOException {
      this.checkReadableBytes(length);
      this.getBytes(this.readerIndex, out, length, true);
      this.readerIndex += length;
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
      this.ensureAccessible();
      if(this.buffer.hasArray()) {
         return in.read(this.buffer.array(), this.buffer.arrayOffset() + index, length);
      } else {
         byte[] tmp = new byte[length];
         int readBytes = in.read(tmp);
         if(readBytes <= 0) {
            return readBytes;
         } else {
            ByteBuffer tmpBuf = this.internalNioBuffer();
            tmpBuf.clear().position(index);
            tmpBuf.put(tmp, 0, readBytes);
            return readBytes;
         }
      }
   }

   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
      this.ensureAccessible();
      ByteBuffer tmpBuf = this.internalNioBuffer();
      tmpBuf.clear().position(index).limit(index + length);

      try {
         return in.read(this.tmpNioBuf);
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
      this.ensureAccessible();

      ByteBuffer src;
      try {
         src = (ByteBuffer)this.buffer.duplicate().clear().position(index).limit(index + length);
      } catch (IllegalArgumentException var5) {
         throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (index + length));
      }

      return this.alloc().directBuffer(length, this.maxCapacity()).writeBytes(src);
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
}
