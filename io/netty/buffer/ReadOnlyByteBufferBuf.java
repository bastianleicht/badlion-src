package io.netty.buffer;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

class ReadOnlyByteBufferBuf extends AbstractReferenceCountedByteBuf {
   protected final ByteBuffer buffer;
   private final ByteBufAllocator allocator;
   private ByteBuffer tmpNioBuf;

   ReadOnlyByteBufferBuf(ByteBufAllocator allocator, ByteBuffer buffer) {
      super(buffer.remaining());
      if(!buffer.isReadOnly()) {
         throw new IllegalArgumentException("must be a readonly buffer: " + StringUtil.simpleClassName((Object)buffer));
      } else {
         this.allocator = allocator;
         this.buffer = buffer.slice().order(ByteOrder.BIG_ENDIAN);
         this.writerIndex(this.buffer.limit());
      }
   }

   protected void deallocate() {
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
      this.checkDstIndex(index, length, dstIndex, dst.length);
      if(dstIndex >= 0 && dstIndex <= dst.length - length) {
         ByteBuffer tmpBuf = this.internalNioBuffer();
         tmpBuf.clear().position(index).limit(index + length);
         tmpBuf.get(dst, dstIndex, length);
         return this;
      } else {
         throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", new Object[]{Integer.valueOf(dstIndex), Integer.valueOf(length), Integer.valueOf(dst.length)}));
      }
   }

   public ByteBuf getBytes(int index, ByteBuffer dst) {
      this.checkIndex(index);
      if(dst == null) {
         throw new NullPointerException("dst");
      } else {
         int bytesToCopy = Math.min(this.capacity() - index, dst.remaining());
         ByteBuffer tmpBuf = this.internalNioBuffer();
         tmpBuf.clear().position(index).limit(index + bytesToCopy);
         dst.put(tmpBuf);
         return this;
      }
   }

   protected void _setByte(int index, int value) {
      throw new ReadOnlyBufferException();
   }

   protected void _setShort(int index, int value) {
      throw new ReadOnlyBufferException();
   }

   protected void _setMedium(int index, int value) {
      throw new ReadOnlyBufferException();
   }

   protected void _setInt(int index, int value) {
      throw new ReadOnlyBufferException();
   }

   protected void _setLong(int index, long value) {
      throw new ReadOnlyBufferException();
   }

   public int capacity() {
      return this.maxCapacity();
   }

   public ByteBuf capacity(int newCapacity) {
      throw new ReadOnlyBufferException();
   }

   public ByteBufAllocator alloc() {
      return this.allocator;
   }

   public ByteOrder order() {
      return ByteOrder.BIG_ENDIAN;
   }

   public ByteBuf unwrap() {
      return null;
   }

   public boolean isDirect() {
      return this.buffer.isDirect();
   }

   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
      this.ensureAccessible();
      if(length == 0) {
         return this;
      } else {
         if(this.buffer.hasArray()) {
            out.write(this.buffer.array(), index + this.buffer.arrayOffset(), length);
         } else {
            byte[] tmp = new byte[length];
            ByteBuffer tmpBuf = this.internalNioBuffer();
            tmpBuf.clear().position(index);
            tmpBuf.get(tmp);
            out.write(tmp);
         }

         return this;
      }
   }

   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
      this.ensureAccessible();
      if(length == 0) {
         return 0;
      } else {
         ByteBuffer tmpBuf = this.internalNioBuffer();
         tmpBuf.clear().position(index).limit(index + length);
         return out.write(tmpBuf);
      }
   }

   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setBytes(int index, ByteBuffer src) {
      throw new ReadOnlyBufferException();
   }

   public int setBytes(int index, InputStream in, int length) throws IOException {
      throw new ReadOnlyBufferException();
   }

   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
      throw new ReadOnlyBufferException();
   }

   protected final ByteBuffer internalNioBuffer() {
      ByteBuffer tmpNioBuf = this.tmpNioBuf;
      if(tmpNioBuf == null) {
         this.tmpNioBuf = tmpNioBuf = this.buffer.duplicate();
      }

      return tmpNioBuf;
   }

   public ByteBuf copy(int index, int length) {
      this.ensureAccessible();

      ByteBuffer src;
      try {
         src = (ByteBuffer)this.internalNioBuffer().clear().position(index).limit(index + length);
      } catch (IllegalArgumentException var5) {
         throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (index + length));
      }

      ByteBuffer dst = ByteBuffer.allocateDirect(length);
      dst.put(src);
      dst.order(this.order());
      dst.clear();
      return new UnpooledDirectByteBuf(this.alloc(), dst, this.maxCapacity());
   }

   public int nioBufferCount() {
      return 1;
   }

   public ByteBuffer[] nioBuffers(int index, int length) {
      return new ByteBuffer[]{this.nioBuffer(index, length)};
   }

   public ByteBuffer nioBuffer(int index, int length) {
      return (ByteBuffer)this.buffer.duplicate().position(index).limit(index + length);
   }

   public ByteBuffer internalNioBuffer(int index, int length) {
      this.ensureAccessible();
      return (ByteBuffer)this.internalNioBuffer().clear().position(index).limit(index + length);
   }

   public boolean hasArray() {
      return this.buffer.hasArray();
   }

   public byte[] array() {
      return this.buffer.array();
   }

   public int arrayOffset() {
      return this.buffer.arrayOffset();
   }

   public boolean hasMemoryAddress() {
      return false;
   }

   public long memoryAddress() {
      throw new UnsupportedOperationException();
   }
}
