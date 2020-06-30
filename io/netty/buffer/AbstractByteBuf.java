package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.DuplicatedByteBuf;
import io.netty.buffer.SlicedByteBuf;
import io.netty.buffer.SwappedByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

public abstract class AbstractByteBuf extends ByteBuf {
   static final ResourceLeakDetector leakDetector = new ResourceLeakDetector(ByteBuf.class);
   int readerIndex;
   int writerIndex;
   private int markedReaderIndex;
   private int markedWriterIndex;
   private int maxCapacity;
   private SwappedByteBuf swappedBuf;

   protected AbstractByteBuf(int maxCapacity) {
      if(maxCapacity < 0) {
         throw new IllegalArgumentException("maxCapacity: " + maxCapacity + " (expected: >= 0)");
      } else {
         this.maxCapacity = maxCapacity;
      }
   }

   public int maxCapacity() {
      return this.maxCapacity;
   }

   protected final void maxCapacity(int maxCapacity) {
      this.maxCapacity = maxCapacity;
   }

   public int readerIndex() {
      return this.readerIndex;
   }

   public ByteBuf readerIndex(int readerIndex) {
      if(readerIndex >= 0 && readerIndex <= this.writerIndex) {
         this.readerIndex = readerIndex;
         return this;
      } else {
         throw new IndexOutOfBoundsException(String.format("readerIndex: %d (expected: 0 <= readerIndex <= writerIndex(%d))", new Object[]{Integer.valueOf(readerIndex), Integer.valueOf(this.writerIndex)}));
      }
   }

   public int writerIndex() {
      return this.writerIndex;
   }

   public ByteBuf writerIndex(int writerIndex) {
      if(writerIndex >= this.readerIndex && writerIndex <= this.capacity()) {
         this.writerIndex = writerIndex;
         return this;
      } else {
         throw new IndexOutOfBoundsException(String.format("writerIndex: %d (expected: readerIndex(%d) <= writerIndex <= capacity(%d))", new Object[]{Integer.valueOf(writerIndex), Integer.valueOf(this.readerIndex), Integer.valueOf(this.capacity())}));
      }
   }

   public ByteBuf setIndex(int readerIndex, int writerIndex) {
      if(readerIndex >= 0 && readerIndex <= writerIndex && writerIndex <= this.capacity()) {
         this.readerIndex = readerIndex;
         this.writerIndex = writerIndex;
         return this;
      } else {
         throw new IndexOutOfBoundsException(String.format("readerIndex: %d, writerIndex: %d (expected: 0 <= readerIndex <= writerIndex <= capacity(%d))", new Object[]{Integer.valueOf(readerIndex), Integer.valueOf(writerIndex), Integer.valueOf(this.capacity())}));
      }
   }

   public ByteBuf clear() {
      this.readerIndex = this.writerIndex = 0;
      return this;
   }

   public boolean isReadable() {
      return this.writerIndex > this.readerIndex;
   }

   public boolean isReadable(int numBytes) {
      return this.writerIndex - this.readerIndex >= numBytes;
   }

   public boolean isWritable() {
      return this.capacity() > this.writerIndex;
   }

   public boolean isWritable(int numBytes) {
      return this.capacity() - this.writerIndex >= numBytes;
   }

   public int readableBytes() {
      return this.writerIndex - this.readerIndex;
   }

   public int writableBytes() {
      return this.capacity() - this.writerIndex;
   }

   public int maxWritableBytes() {
      return this.maxCapacity() - this.writerIndex;
   }

   public ByteBuf markReaderIndex() {
      this.markedReaderIndex = this.readerIndex;
      return this;
   }

   public ByteBuf resetReaderIndex() {
      this.readerIndex(this.markedReaderIndex);
      return this;
   }

   public ByteBuf markWriterIndex() {
      this.markedWriterIndex = this.writerIndex;
      return this;
   }

   public ByteBuf resetWriterIndex() {
      this.writerIndex = this.markedWriterIndex;
      return this;
   }

   public ByteBuf discardReadBytes() {
      this.ensureAccessible();
      if(this.readerIndex == 0) {
         return this;
      } else {
         if(this.readerIndex != this.writerIndex) {
            this.setBytes(0, this, this.readerIndex, this.writerIndex - this.readerIndex);
            this.writerIndex -= this.readerIndex;
            this.adjustMarkers(this.readerIndex);
            this.readerIndex = 0;
         } else {
            this.adjustMarkers(this.readerIndex);
            this.writerIndex = this.readerIndex = 0;
         }

         return this;
      }
   }

   public ByteBuf discardSomeReadBytes() {
      this.ensureAccessible();
      if(this.readerIndex == 0) {
         return this;
      } else if(this.readerIndex == this.writerIndex) {
         this.adjustMarkers(this.readerIndex);
         this.writerIndex = this.readerIndex = 0;
         return this;
      } else {
         if(this.readerIndex >= this.capacity() >>> 1) {
            this.setBytes(0, this, this.readerIndex, this.writerIndex - this.readerIndex);
            this.writerIndex -= this.readerIndex;
            this.adjustMarkers(this.readerIndex);
            this.readerIndex = 0;
         }

         return this;
      }
   }

   protected final void adjustMarkers(int decrement) {
      int markedReaderIndex = this.markedReaderIndex;
      if(markedReaderIndex <= decrement) {
         this.markedReaderIndex = 0;
         int markedWriterIndex = this.markedWriterIndex;
         if(markedWriterIndex <= decrement) {
            this.markedWriterIndex = 0;
         } else {
            this.markedWriterIndex = markedWriterIndex - decrement;
         }
      } else {
         this.markedReaderIndex = markedReaderIndex - decrement;
         this.markedWriterIndex -= decrement;
      }

   }

   public ByteBuf ensureWritable(int minWritableBytes) {
      if(minWritableBytes < 0) {
         throw new IllegalArgumentException(String.format("minWritableBytes: %d (expected: >= 0)", new Object[]{Integer.valueOf(minWritableBytes)}));
      } else if(minWritableBytes <= this.writableBytes()) {
         return this;
      } else if(minWritableBytes > this.maxCapacity - this.writerIndex) {
         throw new IndexOutOfBoundsException(String.format("writerIndex(%d) + minWritableBytes(%d) exceeds maxCapacity(%d): %s", new Object[]{Integer.valueOf(this.writerIndex), Integer.valueOf(minWritableBytes), Integer.valueOf(this.maxCapacity), this}));
      } else {
         int newCapacity = this.calculateNewCapacity(this.writerIndex + minWritableBytes);
         this.capacity(newCapacity);
         return this;
      }
   }

   public int ensureWritable(int minWritableBytes, boolean force) {
      if(minWritableBytes < 0) {
         throw new IllegalArgumentException(String.format("minWritableBytes: %d (expected: >= 0)", new Object[]{Integer.valueOf(minWritableBytes)}));
      } else if(minWritableBytes <= this.writableBytes()) {
         return 0;
      } else if(minWritableBytes > this.maxCapacity - this.writerIndex && force) {
         if(this.capacity() == this.maxCapacity()) {
            return 1;
         } else {
            this.capacity(this.maxCapacity());
            return 3;
         }
      } else {
         int newCapacity = this.calculateNewCapacity(this.writerIndex + minWritableBytes);
         this.capacity(newCapacity);
         return 2;
      }
   }

   private int calculateNewCapacity(int minNewCapacity) {
      int maxCapacity = this.maxCapacity;
      int threshold = 4194304;
      if(minNewCapacity == 4194304) {
         return 4194304;
      } else if(minNewCapacity > 4194304) {
         int newCapacity = minNewCapacity / 4194304 * 4194304;
         if(newCapacity > maxCapacity - 4194304) {
            newCapacity = maxCapacity;
         } else {
            newCapacity = newCapacity + 4194304;
         }

         return newCapacity;
      } else {
         int newCapacity;
         for(newCapacity = 64; newCapacity < minNewCapacity; newCapacity <<= 1) {
            ;
         }

         return Math.min(newCapacity, maxCapacity);
      }
   }

   public ByteBuf order(ByteOrder endianness) {
      if(endianness == null) {
         throw new NullPointerException("endianness");
      } else if(endianness == this.order()) {
         return this;
      } else {
         SwappedByteBuf swappedBuf = this.swappedBuf;
         if(swappedBuf == null) {
            this.swappedBuf = swappedBuf = this.newSwappedByteBuf();
         }

         return swappedBuf;
      }
   }

   protected SwappedByteBuf newSwappedByteBuf() {
      return new SwappedByteBuf(this);
   }

   public byte getByte(int index) {
      this.checkIndex(index);
      return this._getByte(index);
   }

   protected abstract byte _getByte(int var1);

   public boolean getBoolean(int index) {
      return this.getByte(index) != 0;
   }

   public short getUnsignedByte(int index) {
      return (short)(this.getByte(index) & 255);
   }

   public short getShort(int index) {
      this.checkIndex(index, 2);
      return this._getShort(index);
   }

   protected abstract short _getShort(int var1);

   public int getUnsignedShort(int index) {
      return this.getShort(index) & '\uffff';
   }

   public int getUnsignedMedium(int index) {
      this.checkIndex(index, 3);
      return this._getUnsignedMedium(index);
   }

   protected abstract int _getUnsignedMedium(int var1);

   public int getMedium(int index) {
      int value = this.getUnsignedMedium(index);
      if((value & 8388608) != 0) {
         value |= -16777216;
      }

      return value;
   }

   public int getInt(int index) {
      this.checkIndex(index, 4);
      return this._getInt(index);
   }

   protected abstract int _getInt(int var1);

   public long getUnsignedInt(int index) {
      return (long)this.getInt(index) & 4294967295L;
   }

   public long getLong(int index) {
      this.checkIndex(index, 8);
      return this._getLong(index);
   }

   protected abstract long _getLong(int var1);

   public char getChar(int index) {
      return (char)this.getShort(index);
   }

   public float getFloat(int index) {
      return Float.intBitsToFloat(this.getInt(index));
   }

   public double getDouble(int index) {
      return Double.longBitsToDouble(this.getLong(index));
   }

   public ByteBuf getBytes(int index, byte[] dst) {
      this.getBytes(index, dst, 0, dst.length);
      return this;
   }

   public ByteBuf getBytes(int index, ByteBuf dst) {
      this.getBytes(index, dst, dst.writableBytes());
      return this;
   }

   public ByteBuf getBytes(int index, ByteBuf dst, int length) {
      this.getBytes(index, dst, dst.writerIndex(), length);
      dst.writerIndex(dst.writerIndex() + length);
      return this;
   }

   public ByteBuf setByte(int index, int value) {
      this.checkIndex(index);
      this._setByte(index, value);
      return this;
   }

   protected abstract void _setByte(int var1, int var2);

   public ByteBuf setBoolean(int index, boolean value) {
      this.setByte(index, value?1:0);
      return this;
   }

   public ByteBuf setShort(int index, int value) {
      this.checkIndex(index, 2);
      this._setShort(index, value);
      return this;
   }

   protected abstract void _setShort(int var1, int var2);

   public ByteBuf setChar(int index, int value) {
      this.setShort(index, value);
      return this;
   }

   public ByteBuf setMedium(int index, int value) {
      this.checkIndex(index, 3);
      this._setMedium(index, value);
      return this;
   }

   protected abstract void _setMedium(int var1, int var2);

   public ByteBuf setInt(int index, int value) {
      this.checkIndex(index, 4);
      this._setInt(index, value);
      return this;
   }

   protected abstract void _setInt(int var1, int var2);

   public ByteBuf setFloat(int index, float value) {
      this.setInt(index, Float.floatToRawIntBits(value));
      return this;
   }

   public ByteBuf setLong(int index, long value) {
      this.checkIndex(index, 8);
      this._setLong(index, value);
      return this;
   }

   protected abstract void _setLong(int var1, long var2);

   public ByteBuf setDouble(int index, double value) {
      this.setLong(index, Double.doubleToRawLongBits(value));
      return this;
   }

   public ByteBuf setBytes(int index, byte[] src) {
      this.setBytes(index, src, 0, src.length);
      return this;
   }

   public ByteBuf setBytes(int index, ByteBuf src) {
      this.setBytes(index, src, src.readableBytes());
      return this;
   }

   public ByteBuf setBytes(int index, ByteBuf src, int length) {
      this.checkIndex(index, length);
      if(src == null) {
         throw new NullPointerException("src");
      } else if(length > src.readableBytes()) {
         throw new IndexOutOfBoundsException(String.format("length(%d) exceeds src.readableBytes(%d) where src is: %s", new Object[]{Integer.valueOf(length), Integer.valueOf(src.readableBytes()), src}));
      } else {
         this.setBytes(index, src, src.readerIndex(), length);
         src.readerIndex(src.readerIndex() + length);
         return this;
      }
   }

   public ByteBuf setZero(int index, int length) {
      if(length == 0) {
         return this;
      } else {
         this.checkIndex(index, length);
         int nLong = length >>> 3;
         int nBytes = length & 7;

         for(int i = nLong; i > 0; --i) {
            this.setLong(index, 0L);
            index += 8;
         }

         if(nBytes == 4) {
            this.setInt(index, 0);
         } else if(nBytes < 4) {
            for(int i = nBytes; i > 0; --i) {
               this.setByte(index, 0);
               ++index;
            }
         } else {
            this.setInt(index, 0);
            index = index + 4;

            for(int i = nBytes - 4; i > 0; --i) {
               this.setByte(index, 0);
               ++index;
            }
         }

         return this;
      }
   }

   public byte readByte() {
      this.checkReadableBytes(1);
      int i = this.readerIndex;
      byte b = this.getByte(i);
      this.readerIndex = i + 1;
      return b;
   }

   public boolean readBoolean() {
      return this.readByte() != 0;
   }

   public short readUnsignedByte() {
      return (short)(this.readByte() & 255);
   }

   public short readShort() {
      this.checkReadableBytes(2);
      short v = this._getShort(this.readerIndex);
      this.readerIndex += 2;
      return v;
   }

   public int readUnsignedShort() {
      return this.readShort() & '\uffff';
   }

   public int readMedium() {
      int value = this.readUnsignedMedium();
      if((value & 8388608) != 0) {
         value |= -16777216;
      }

      return value;
   }

   public int readUnsignedMedium() {
      this.checkReadableBytes(3);
      int v = this._getUnsignedMedium(this.readerIndex);
      this.readerIndex += 3;
      return v;
   }

   public int readInt() {
      this.checkReadableBytes(4);
      int v = this._getInt(this.readerIndex);
      this.readerIndex += 4;
      return v;
   }

   public long readUnsignedInt() {
      return (long)this.readInt() & 4294967295L;
   }

   public long readLong() {
      this.checkReadableBytes(8);
      long v = this._getLong(this.readerIndex);
      this.readerIndex += 8;
      return v;
   }

   public char readChar() {
      return (char)this.readShort();
   }

   public float readFloat() {
      return Float.intBitsToFloat(this.readInt());
   }

   public double readDouble() {
      return Double.longBitsToDouble(this.readLong());
   }

   public ByteBuf readBytes(int length) {
      this.checkReadableBytes(length);
      if(length == 0) {
         return Unpooled.EMPTY_BUFFER;
      } else {
         ByteBuf buf = Unpooled.buffer(length, this.maxCapacity);
         buf.writeBytes((ByteBuf)this, this.readerIndex, length);
         this.readerIndex += length;
         return buf;
      }
   }

   public ByteBuf readSlice(int length) {
      ByteBuf slice = this.slice(this.readerIndex, length);
      this.readerIndex += length;
      return slice;
   }

   public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
      this.checkReadableBytes(length);
      this.getBytes(this.readerIndex, dst, dstIndex, length);
      this.readerIndex += length;
      return this;
   }

   public ByteBuf readBytes(byte[] dst) {
      this.readBytes((byte[])dst, 0, dst.length);
      return this;
   }

   public ByteBuf readBytes(ByteBuf dst) {
      this.readBytes(dst, dst.writableBytes());
      return this;
   }

   public ByteBuf readBytes(ByteBuf dst, int length) {
      if(length > dst.writableBytes()) {
         throw new IndexOutOfBoundsException(String.format("length(%d) exceeds dst.writableBytes(%d) where dst is: %s", new Object[]{Integer.valueOf(length), Integer.valueOf(dst.writableBytes()), dst}));
      } else {
         this.readBytes(dst, dst.writerIndex(), length);
         dst.writerIndex(dst.writerIndex() + length);
         return this;
      }
   }

   public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
      this.checkReadableBytes(length);
      this.getBytes(this.readerIndex, dst, dstIndex, length);
      this.readerIndex += length;
      return this;
   }

   public ByteBuf readBytes(ByteBuffer dst) {
      int length = dst.remaining();
      this.checkReadableBytes(length);
      this.getBytes(this.readerIndex, (ByteBuffer)dst);
      this.readerIndex += length;
      return this;
   }

   public int readBytes(GatheringByteChannel out, int length) throws IOException {
      this.checkReadableBytes(length);
      int readBytes = this.getBytes(this.readerIndex, out, length);
      this.readerIndex += readBytes;
      return readBytes;
   }

   public ByteBuf readBytes(OutputStream out, int length) throws IOException {
      this.checkReadableBytes(length);
      this.getBytes(this.readerIndex, out, length);
      this.readerIndex += length;
      return this;
   }

   public ByteBuf skipBytes(int length) {
      this.checkReadableBytes(length);
      this.readerIndex += length;
      return this;
   }

   public ByteBuf writeBoolean(boolean value) {
      this.writeByte(value?1:0);
      return this;
   }

   public ByteBuf writeByte(int value) {
      this.ensureAccessible();
      this.ensureWritable(1);
      this._setByte(this.writerIndex++, value);
      return this;
   }

   public ByteBuf writeShort(int value) {
      this.ensureAccessible();
      this.ensureWritable(2);
      this._setShort(this.writerIndex, value);
      this.writerIndex += 2;
      return this;
   }

   public ByteBuf writeMedium(int value) {
      this.ensureAccessible();
      this.ensureWritable(3);
      this._setMedium(this.writerIndex, value);
      this.writerIndex += 3;
      return this;
   }

   public ByteBuf writeInt(int value) {
      this.ensureAccessible();
      this.ensureWritable(4);
      this._setInt(this.writerIndex, value);
      this.writerIndex += 4;
      return this;
   }

   public ByteBuf writeLong(long value) {
      this.ensureAccessible();
      this.ensureWritable(8);
      this._setLong(this.writerIndex, value);
      this.writerIndex += 8;
      return this;
   }

   public ByteBuf writeChar(int value) {
      this.writeShort(value);
      return this;
   }

   public ByteBuf writeFloat(float value) {
      this.writeInt(Float.floatToRawIntBits(value));
      return this;
   }

   public ByteBuf writeDouble(double value) {
      this.writeLong(Double.doubleToRawLongBits(value));
      return this;
   }

   public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
      this.ensureAccessible();
      this.ensureWritable(length);
      this.setBytes(this.writerIndex, src, srcIndex, length);
      this.writerIndex += length;
      return this;
   }

   public ByteBuf writeBytes(byte[] src) {
      this.writeBytes((byte[])src, 0, src.length);
      return this;
   }

   public ByteBuf writeBytes(ByteBuf src) {
      this.writeBytes(src, src.readableBytes());
      return this;
   }

   public ByteBuf writeBytes(ByteBuf src, int length) {
      if(length > src.readableBytes()) {
         throw new IndexOutOfBoundsException(String.format("length(%d) exceeds src.readableBytes(%d) where src is: %s", new Object[]{Integer.valueOf(length), Integer.valueOf(src.readableBytes()), src}));
      } else {
         this.writeBytes(src, src.readerIndex(), length);
         src.readerIndex(src.readerIndex() + length);
         return this;
      }
   }

   public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
      this.ensureAccessible();
      this.ensureWritable(length);
      this.setBytes(this.writerIndex, src, srcIndex, length);
      this.writerIndex += length;
      return this;
   }

   public ByteBuf writeBytes(ByteBuffer src) {
      this.ensureAccessible();
      int length = src.remaining();
      this.ensureWritable(length);
      this.setBytes(this.writerIndex, (ByteBuffer)src);
      this.writerIndex += length;
      return this;
   }

   public int writeBytes(InputStream in, int length) throws IOException {
      this.ensureAccessible();
      this.ensureWritable(length);
      int writtenBytes = this.setBytes(this.writerIndex, in, length);
      if(writtenBytes > 0) {
         this.writerIndex += writtenBytes;
      }

      return writtenBytes;
   }

   public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
      this.ensureAccessible();
      this.ensureWritable(length);
      int writtenBytes = this.setBytes(this.writerIndex, in, length);
      if(writtenBytes > 0) {
         this.writerIndex += writtenBytes;
      }

      return writtenBytes;
   }

   public ByteBuf writeZero(int length) {
      if(length == 0) {
         return this;
      } else {
         this.ensureWritable(length);
         this.checkIndex(this.writerIndex, length);
         int nLong = length >>> 3;
         int nBytes = length & 7;

         for(int i = nLong; i > 0; --i) {
            this.writeLong(0L);
         }

         if(nBytes == 4) {
            this.writeInt(0);
         } else if(nBytes < 4) {
            for(int i = nBytes; i > 0; --i) {
               this.writeByte(0);
            }
         } else {
            this.writeInt(0);

            for(int i = nBytes - 4; i > 0; --i) {
               this.writeByte(0);
            }
         }

         return this;
      }
   }

   public ByteBuf copy() {
      return this.copy(this.readerIndex, this.readableBytes());
   }

   public ByteBuf duplicate() {
      return new DuplicatedByteBuf(this);
   }

   public ByteBuf slice() {
      return this.slice(this.readerIndex, this.readableBytes());
   }

   public ByteBuf slice(int index, int length) {
      return (ByteBuf)(length == 0?Unpooled.EMPTY_BUFFER:new SlicedByteBuf(this, index, length));
   }

   public ByteBuffer nioBuffer() {
      return this.nioBuffer(this.readerIndex, this.readableBytes());
   }

   public ByteBuffer[] nioBuffers() {
      return this.nioBuffers(this.readerIndex, this.readableBytes());
   }

   public String toString(Charset charset) {
      return this.toString(this.readerIndex, this.readableBytes(), charset);
   }

   public String toString(int index, int length, Charset charset) {
      if(length == 0) {
         return "";
      } else {
         ByteBuffer nioBuffer;
         if(this.nioBufferCount() == 1) {
            nioBuffer = this.nioBuffer(index, length);
         } else {
            nioBuffer = ByteBuffer.allocate(length);
            this.getBytes(index, (ByteBuffer)nioBuffer);
            nioBuffer.flip();
         }

         return ByteBufUtil.decodeString(nioBuffer, charset);
      }
   }

   public int indexOf(int fromIndex, int toIndex, byte value) {
      return ByteBufUtil.indexOf(this, fromIndex, toIndex, value);
   }

   public int bytesBefore(byte value) {
      return this.bytesBefore(this.readerIndex(), this.readableBytes(), value);
   }

   public int bytesBefore(int length, byte value) {
      this.checkReadableBytes(length);
      return this.bytesBefore(this.readerIndex(), length, value);
   }

   public int bytesBefore(int index, int length, byte value) {
      int endIndex = this.indexOf(index, index + length, value);
      return endIndex < 0?-1:endIndex - index;
   }

   public int forEachByte(ByteBufProcessor processor) {
      int index = this.readerIndex;
      int length = this.writerIndex - index;
      this.ensureAccessible();
      return this.forEachByteAsc0(index, length, processor);
   }

   public int forEachByte(int index, int length, ByteBufProcessor processor) {
      this.checkIndex(index, length);
      return this.forEachByteAsc0(index, length, processor);
   }

   private int forEachByteAsc0(int index, int length, ByteBufProcessor processor) {
      if(processor == null) {
         throw new NullPointerException("processor");
      } else if(length == 0) {
         return -1;
      } else {
         int endIndex = index + length;
         int i = index;

         while(true) {
            try {
               if(!processor.process(this._getByte(i))) {
                  return i;
               }

               ++i;
               if(i < endIndex) {
                  continue;
               }
            } catch (Exception var7) {
               PlatformDependent.throwException(var7);
            }

            return -1;
         }
      }
   }

   public int forEachByteDesc(ByteBufProcessor processor) {
      int index = this.readerIndex;
      int length = this.writerIndex - index;
      this.ensureAccessible();
      return this.forEachByteDesc0(index, length, processor);
   }

   public int forEachByteDesc(int index, int length, ByteBufProcessor processor) {
      this.checkIndex(index, length);
      return this.forEachByteDesc0(index, length, processor);
   }

   private int forEachByteDesc0(int index, int length, ByteBufProcessor processor) {
      if(processor == null) {
         throw new NullPointerException("processor");
      } else if(length == 0) {
         return -1;
      } else {
         int i = index + length - 1;

         while(true) {
            try {
               if(!processor.process(this._getByte(i))) {
                  return i;
               }

               --i;
               if(i >= index) {
                  continue;
               }
            } catch (Exception var6) {
               PlatformDependent.throwException(var6);
            }

            return -1;
         }
      }
   }

   public int hashCode() {
      return ByteBufUtil.hashCode(this);
   }

   public boolean equals(Object o) {
      return this == o?true:(o instanceof ByteBuf?ByteBufUtil.equals(this, (ByteBuf)o):false);
   }

   public int compareTo(ByteBuf that) {
      return ByteBufUtil.compare(this, that);
   }

   public String toString() {
      if(this.refCnt() == 0) {
         return StringUtil.simpleClassName((Object)this) + "(freed)";
      } else {
         StringBuilder buf = new StringBuilder();
         buf.append(StringUtil.simpleClassName((Object)this));
         buf.append("(ridx: ");
         buf.append(this.readerIndex);
         buf.append(", widx: ");
         buf.append(this.writerIndex);
         buf.append(", cap: ");
         buf.append(this.capacity());
         if(this.maxCapacity != Integer.MAX_VALUE) {
            buf.append('/');
            buf.append(this.maxCapacity);
         }

         ByteBuf unwrapped = this.unwrap();
         if(unwrapped != null) {
            buf.append(", unwrapped: ");
            buf.append(unwrapped);
         }

         buf.append(')');
         return buf.toString();
      }
   }

   protected final void checkIndex(int index) {
      this.ensureAccessible();
      if(index < 0 || index >= this.capacity()) {
         throw new IndexOutOfBoundsException(String.format("index: %d (expected: range(0, %d))", new Object[]{Integer.valueOf(index), Integer.valueOf(this.capacity())}));
      }
   }

   protected final void checkIndex(int index, int fieldLength) {
      this.ensureAccessible();
      if(fieldLength < 0) {
         throw new IllegalArgumentException("length: " + fieldLength + " (expected: >= 0)");
      } else if(index < 0 || index > this.capacity() - fieldLength) {
         throw new IndexOutOfBoundsException(String.format("index: %d, length: %d (expected: range(0, %d))", new Object[]{Integer.valueOf(index), Integer.valueOf(fieldLength), Integer.valueOf(this.capacity())}));
      }
   }

   protected final void checkSrcIndex(int index, int length, int srcIndex, int srcCapacity) {
      this.checkIndex(index, length);
      if(srcIndex < 0 || srcIndex > srcCapacity - length) {
         throw new IndexOutOfBoundsException(String.format("srcIndex: %d, length: %d (expected: range(0, %d))", new Object[]{Integer.valueOf(srcIndex), Integer.valueOf(length), Integer.valueOf(srcCapacity)}));
      }
   }

   protected final void checkDstIndex(int index, int length, int dstIndex, int dstCapacity) {
      this.checkIndex(index, length);
      if(dstIndex < 0 || dstIndex > dstCapacity - length) {
         throw new IndexOutOfBoundsException(String.format("dstIndex: %d, length: %d (expected: range(0, %d))", new Object[]{Integer.valueOf(dstIndex), Integer.valueOf(length), Integer.valueOf(dstCapacity)}));
      }
   }

   protected final void checkReadableBytes(int minimumReadableBytes) {
      this.ensureAccessible();
      if(minimumReadableBytes < 0) {
         throw new IllegalArgumentException("minimumReadableBytes: " + minimumReadableBytes + " (expected: >= 0)");
      } else if(this.readerIndex > this.writerIndex - minimumReadableBytes) {
         throw new IndexOutOfBoundsException(String.format("readerIndex(%d) + length(%d) exceeds writerIndex(%d): %s", new Object[]{Integer.valueOf(this.readerIndex), Integer.valueOf(minimumReadableBytes), Integer.valueOf(this.writerIndex), this}));
      }
   }

   protected final void ensureAccessible() {
      if(this.refCnt() == 0) {
         throw new IllegalReferenceCountException(0);
      }
   }
}
