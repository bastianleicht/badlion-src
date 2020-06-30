package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

class WrappedByteBuf extends ByteBuf {
   protected final ByteBuf buf;

   protected WrappedByteBuf(ByteBuf buf) {
      if(buf == null) {
         throw new NullPointerException("buf");
      } else {
         this.buf = buf;
      }
   }

   public boolean hasMemoryAddress() {
      return this.buf.hasMemoryAddress();
   }

   public long memoryAddress() {
      return this.buf.memoryAddress();
   }

   public int capacity() {
      return this.buf.capacity();
   }

   public ByteBuf capacity(int newCapacity) {
      this.buf.capacity(newCapacity);
      return this;
   }

   public int maxCapacity() {
      return this.buf.maxCapacity();
   }

   public ByteBufAllocator alloc() {
      return this.buf.alloc();
   }

   public ByteOrder order() {
      return this.buf.order();
   }

   public ByteBuf order(ByteOrder endianness) {
      return this.buf.order(endianness);
   }

   public ByteBuf unwrap() {
      return this.buf;
   }

   public boolean isDirect() {
      return this.buf.isDirect();
   }

   public int readerIndex() {
      return this.buf.readerIndex();
   }

   public ByteBuf readerIndex(int readerIndex) {
      this.buf.readerIndex(readerIndex);
      return this;
   }

   public int writerIndex() {
      return this.buf.writerIndex();
   }

   public ByteBuf writerIndex(int writerIndex) {
      this.buf.writerIndex(writerIndex);
      return this;
   }

   public ByteBuf setIndex(int readerIndex, int writerIndex) {
      this.buf.setIndex(readerIndex, writerIndex);
      return this;
   }

   public int readableBytes() {
      return this.buf.readableBytes();
   }

   public int writableBytes() {
      return this.buf.writableBytes();
   }

   public int maxWritableBytes() {
      return this.buf.maxWritableBytes();
   }

   public boolean isReadable() {
      return this.buf.isReadable();
   }

   public boolean isWritable() {
      return this.buf.isWritable();
   }

   public ByteBuf clear() {
      this.buf.clear();
      return this;
   }

   public ByteBuf markReaderIndex() {
      this.buf.markReaderIndex();
      return this;
   }

   public ByteBuf resetReaderIndex() {
      this.buf.resetReaderIndex();
      return this;
   }

   public ByteBuf markWriterIndex() {
      this.buf.markWriterIndex();
      return this;
   }

   public ByteBuf resetWriterIndex() {
      this.buf.resetWriterIndex();
      return this;
   }

   public ByteBuf discardReadBytes() {
      this.buf.discardReadBytes();
      return this;
   }

   public ByteBuf discardSomeReadBytes() {
      this.buf.discardSomeReadBytes();
      return this;
   }

   public ByteBuf ensureWritable(int minWritableBytes) {
      this.buf.ensureWritable(minWritableBytes);
      return this;
   }

   public int ensureWritable(int minWritableBytes, boolean force) {
      return this.buf.ensureWritable(minWritableBytes, force);
   }

   public boolean getBoolean(int index) {
      return this.buf.getBoolean(index);
   }

   public byte getByte(int index) {
      return this.buf.getByte(index);
   }

   public short getUnsignedByte(int index) {
      return this.buf.getUnsignedByte(index);
   }

   public short getShort(int index) {
      return this.buf.getShort(index);
   }

   public int getUnsignedShort(int index) {
      return this.buf.getUnsignedShort(index);
   }

   public int getMedium(int index) {
      return this.buf.getMedium(index);
   }

   public int getUnsignedMedium(int index) {
      return this.buf.getUnsignedMedium(index);
   }

   public int getInt(int index) {
      return this.buf.getInt(index);
   }

   public long getUnsignedInt(int index) {
      return this.buf.getUnsignedInt(index);
   }

   public long getLong(int index) {
      return this.buf.getLong(index);
   }

   public char getChar(int index) {
      return this.buf.getChar(index);
   }

   public float getFloat(int index) {
      return this.buf.getFloat(index);
   }

   public double getDouble(int index) {
      return this.buf.getDouble(index);
   }

   public ByteBuf getBytes(int index, ByteBuf dst) {
      this.buf.getBytes(index, dst);
      return this;
   }

   public ByteBuf getBytes(int index, ByteBuf dst, int length) {
      this.buf.getBytes(index, dst, length);
      return this;
   }

   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
      this.buf.getBytes(index, dst, dstIndex, length);
      return this;
   }

   public ByteBuf getBytes(int index, byte[] dst) {
      this.buf.getBytes(index, dst);
      return this;
   }

   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
      this.buf.getBytes(index, dst, dstIndex, length);
      return this;
   }

   public ByteBuf getBytes(int index, ByteBuffer dst) {
      this.buf.getBytes(index, dst);
      return this;
   }

   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
      this.buf.getBytes(index, out, length);
      return this;
   }

   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
      return this.buf.getBytes(index, out, length);
   }

   public ByteBuf setBoolean(int index, boolean value) {
      this.buf.setBoolean(index, value);
      return this;
   }

   public ByteBuf setByte(int index, int value) {
      this.buf.setByte(index, value);
      return this;
   }

   public ByteBuf setShort(int index, int value) {
      this.buf.setShort(index, value);
      return this;
   }

   public ByteBuf setMedium(int index, int value) {
      this.buf.setMedium(index, value);
      return this;
   }

   public ByteBuf setInt(int index, int value) {
      this.buf.setInt(index, value);
      return this;
   }

   public ByteBuf setLong(int index, long value) {
      this.buf.setLong(index, value);
      return this;
   }

   public ByteBuf setChar(int index, int value) {
      this.buf.setChar(index, value);
      return this;
   }

   public ByteBuf setFloat(int index, float value) {
      this.buf.setFloat(index, value);
      return this;
   }

   public ByteBuf setDouble(int index, double value) {
      this.buf.setDouble(index, value);
      return this;
   }

   public ByteBuf setBytes(int index, ByteBuf src) {
      this.buf.setBytes(index, src);
      return this;
   }

   public ByteBuf setBytes(int index, ByteBuf src, int length) {
      this.buf.setBytes(index, src, length);
      return this;
   }

   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
      this.buf.setBytes(index, src, srcIndex, length);
      return this;
   }

   public ByteBuf setBytes(int index, byte[] src) {
      this.buf.setBytes(index, src);
      return this;
   }

   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
      this.buf.setBytes(index, src, srcIndex, length);
      return this;
   }

   public ByteBuf setBytes(int index, ByteBuffer src) {
      this.buf.setBytes(index, src);
      return this;
   }

   public int setBytes(int index, InputStream in, int length) throws IOException {
      return this.buf.setBytes(index, in, length);
   }

   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
      return this.buf.setBytes(index, in, length);
   }

   public ByteBuf setZero(int index, int length) {
      this.buf.setZero(index, length);
      return this;
   }

   public boolean readBoolean() {
      return this.buf.readBoolean();
   }

   public byte readByte() {
      return this.buf.readByte();
   }

   public short readUnsignedByte() {
      return this.buf.readUnsignedByte();
   }

   public short readShort() {
      return this.buf.readShort();
   }

   public int readUnsignedShort() {
      return this.buf.readUnsignedShort();
   }

   public int readMedium() {
      return this.buf.readMedium();
   }

   public int readUnsignedMedium() {
      return this.buf.readUnsignedMedium();
   }

   public int readInt() {
      return this.buf.readInt();
   }

   public long readUnsignedInt() {
      return this.buf.readUnsignedInt();
   }

   public long readLong() {
      return this.buf.readLong();
   }

   public char readChar() {
      return this.buf.readChar();
   }

   public float readFloat() {
      return this.buf.readFloat();
   }

   public double readDouble() {
      return this.buf.readDouble();
   }

   public ByteBuf readBytes(int length) {
      return this.buf.readBytes(length);
   }

   public ByteBuf readSlice(int length) {
      return this.buf.readSlice(length);
   }

   public ByteBuf readBytes(ByteBuf dst) {
      this.buf.readBytes(dst);
      return this;
   }

   public ByteBuf readBytes(ByteBuf dst, int length) {
      this.buf.readBytes(dst, length);
      return this;
   }

   public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
      this.buf.readBytes(dst, dstIndex, length);
      return this;
   }

   public ByteBuf readBytes(byte[] dst) {
      this.buf.readBytes(dst);
      return this;
   }

   public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
      this.buf.readBytes(dst, dstIndex, length);
      return this;
   }

   public ByteBuf readBytes(ByteBuffer dst) {
      this.buf.readBytes(dst);
      return this;
   }

   public ByteBuf readBytes(OutputStream out, int length) throws IOException {
      this.buf.readBytes(out, length);
      return this;
   }

   public int readBytes(GatheringByteChannel out, int length) throws IOException {
      return this.buf.readBytes(out, length);
   }

   public ByteBuf skipBytes(int length) {
      this.buf.skipBytes(length);
      return this;
   }

   public ByteBuf writeBoolean(boolean value) {
      this.buf.writeBoolean(value);
      return this;
   }

   public ByteBuf writeByte(int value) {
      this.buf.writeByte(value);
      return this;
   }

   public ByteBuf writeShort(int value) {
      this.buf.writeShort(value);
      return this;
   }

   public ByteBuf writeMedium(int value) {
      this.buf.writeMedium(value);
      return this;
   }

   public ByteBuf writeInt(int value) {
      this.buf.writeInt(value);
      return this;
   }

   public ByteBuf writeLong(long value) {
      this.buf.writeLong(value);
      return this;
   }

   public ByteBuf writeChar(int value) {
      this.buf.writeChar(value);
      return this;
   }

   public ByteBuf writeFloat(float value) {
      this.buf.writeFloat(value);
      return this;
   }

   public ByteBuf writeDouble(double value) {
      this.buf.writeDouble(value);
      return this;
   }

   public ByteBuf writeBytes(ByteBuf src) {
      this.buf.writeBytes(src);
      return this;
   }

   public ByteBuf writeBytes(ByteBuf src, int length) {
      this.buf.writeBytes(src, length);
      return this;
   }

   public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
      this.buf.writeBytes(src, srcIndex, length);
      return this;
   }

   public ByteBuf writeBytes(byte[] src) {
      this.buf.writeBytes(src);
      return this;
   }

   public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
      this.buf.writeBytes(src, srcIndex, length);
      return this;
   }

   public ByteBuf writeBytes(ByteBuffer src) {
      this.buf.writeBytes(src);
      return this;
   }

   public int writeBytes(InputStream in, int length) throws IOException {
      return this.buf.writeBytes(in, length);
   }

   public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
      return this.buf.writeBytes(in, length);
   }

   public ByteBuf writeZero(int length) {
      this.buf.writeZero(length);
      return this;
   }

   public int indexOf(int fromIndex, int toIndex, byte value) {
      return this.buf.indexOf(fromIndex, toIndex, value);
   }

   public int bytesBefore(byte value) {
      return this.buf.bytesBefore(value);
   }

   public int bytesBefore(int length, byte value) {
      return this.buf.bytesBefore(length, value);
   }

   public int bytesBefore(int index, int length, byte value) {
      return this.buf.bytesBefore(index, length, value);
   }

   public int forEachByte(ByteBufProcessor processor) {
      return this.buf.forEachByte(processor);
   }

   public int forEachByte(int index, int length, ByteBufProcessor processor) {
      return this.buf.forEachByte(index, length, processor);
   }

   public int forEachByteDesc(ByteBufProcessor processor) {
      return this.buf.forEachByteDesc(processor);
   }

   public int forEachByteDesc(int index, int length, ByteBufProcessor processor) {
      return this.buf.forEachByteDesc(index, length, processor);
   }

   public ByteBuf copy() {
      return this.buf.copy();
   }

   public ByteBuf copy(int index, int length) {
      return this.buf.copy(index, length);
   }

   public ByteBuf slice() {
      return this.buf.slice();
   }

   public ByteBuf slice(int index, int length) {
      return this.buf.slice(index, length);
   }

   public ByteBuf duplicate() {
      return this.buf.duplicate();
   }

   public int nioBufferCount() {
      return this.buf.nioBufferCount();
   }

   public ByteBuffer nioBuffer() {
      return this.buf.nioBuffer();
   }

   public ByteBuffer nioBuffer(int index, int length) {
      return this.buf.nioBuffer(index, length);
   }

   public ByteBuffer[] nioBuffers() {
      return this.buf.nioBuffers();
   }

   public ByteBuffer[] nioBuffers(int index, int length) {
      return this.buf.nioBuffers(index, length);
   }

   public ByteBuffer internalNioBuffer(int index, int length) {
      return this.buf.internalNioBuffer(index, length);
   }

   public boolean hasArray() {
      return this.buf.hasArray();
   }

   public byte[] array() {
      return this.buf.array();
   }

   public int arrayOffset() {
      return this.buf.arrayOffset();
   }

   public String toString(Charset charset) {
      return this.buf.toString(charset);
   }

   public String toString(int index, int length, Charset charset) {
      return this.buf.toString(index, length, charset);
   }

   public int hashCode() {
      return this.buf.hashCode();
   }

   public boolean equals(Object obj) {
      return this.buf.equals(obj);
   }

   public int compareTo(ByteBuf buffer) {
      return this.buf.compareTo(buffer);
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '(' + this.buf.toString() + ')';
   }

   public ByteBuf retain(int increment) {
      this.buf.retain(increment);
      return this;
   }

   public ByteBuf retain() {
      this.buf.retain();
      return this;
   }

   public boolean isReadable(int size) {
      return this.buf.isReadable(size);
   }

   public boolean isWritable(int size) {
      return this.buf.isWritable(size);
   }

   public int refCnt() {
      return this.buf.refCnt();
   }

   public boolean release() {
      return this.buf.release();
   }

   public boolean release(int decrement) {
      return this.buf.release(decrement);
   }
}
