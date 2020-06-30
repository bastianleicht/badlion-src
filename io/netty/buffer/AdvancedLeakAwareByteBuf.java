package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.WrappedByteBuf;
import io.netty.util.ResourceLeak;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;

final class AdvancedLeakAwareByteBuf extends WrappedByteBuf {
   private final ResourceLeak leak;

   AdvancedLeakAwareByteBuf(ByteBuf buf, ResourceLeak leak) {
      super(buf);
      this.leak = leak;
   }

   public boolean release() {
      boolean deallocated = super.release();
      if(deallocated) {
         this.leak.close();
      } else {
         this.leak.record();
      }

      return deallocated;
   }

   public boolean release(int decrement) {
      boolean deallocated = super.release(decrement);
      if(deallocated) {
         this.leak.close();
      } else {
         this.leak.record();
      }

      return deallocated;
   }

   public ByteBuf order(ByteOrder endianness) {
      this.leak.record();
      return this.order() == endianness?this:new AdvancedLeakAwareByteBuf(super.order(endianness), this.leak);
   }

   public ByteBuf slice() {
      this.leak.record();
      return new AdvancedLeakAwareByteBuf(super.slice(), this.leak);
   }

   public ByteBuf slice(int index, int length) {
      this.leak.record();
      return new AdvancedLeakAwareByteBuf(super.slice(index, length), this.leak);
   }

   public ByteBuf duplicate() {
      this.leak.record();
      return new AdvancedLeakAwareByteBuf(super.duplicate(), this.leak);
   }

   public ByteBuf readSlice(int length) {
      this.leak.record();
      return new AdvancedLeakAwareByteBuf(super.readSlice(length), this.leak);
   }

   public ByteBuf discardReadBytes() {
      this.leak.record();
      return super.discardReadBytes();
   }

   public ByteBuf discardSomeReadBytes() {
      this.leak.record();
      return super.discardSomeReadBytes();
   }

   public ByteBuf ensureWritable(int minWritableBytes) {
      this.leak.record();
      return super.ensureWritable(minWritableBytes);
   }

   public int ensureWritable(int minWritableBytes, boolean force) {
      this.leak.record();
      return super.ensureWritable(minWritableBytes, force);
   }

   public boolean getBoolean(int index) {
      this.leak.record();
      return super.getBoolean(index);
   }

   public byte getByte(int index) {
      this.leak.record();
      return super.getByte(index);
   }

   public short getUnsignedByte(int index) {
      this.leak.record();
      return super.getUnsignedByte(index);
   }

   public short getShort(int index) {
      this.leak.record();
      return super.getShort(index);
   }

   public int getUnsignedShort(int index) {
      this.leak.record();
      return super.getUnsignedShort(index);
   }

   public int getMedium(int index) {
      this.leak.record();
      return super.getMedium(index);
   }

   public int getUnsignedMedium(int index) {
      this.leak.record();
      return super.getUnsignedMedium(index);
   }

   public int getInt(int index) {
      this.leak.record();
      return super.getInt(index);
   }

   public long getUnsignedInt(int index) {
      this.leak.record();
      return super.getUnsignedInt(index);
   }

   public long getLong(int index) {
      this.leak.record();
      return super.getLong(index);
   }

   public char getChar(int index) {
      this.leak.record();
      return super.getChar(index);
   }

   public float getFloat(int index) {
      this.leak.record();
      return super.getFloat(index);
   }

   public double getDouble(int index) {
      this.leak.record();
      return super.getDouble(index);
   }

   public ByteBuf getBytes(int index, ByteBuf dst) {
      this.leak.record();
      return super.getBytes(index, dst);
   }

   public ByteBuf getBytes(int index, ByteBuf dst, int length) {
      this.leak.record();
      return super.getBytes(index, dst, length);
   }

   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
      this.leak.record();
      return super.getBytes(index, dst, dstIndex, length);
   }

   public ByteBuf getBytes(int index, byte[] dst) {
      this.leak.record();
      return super.getBytes(index, dst);
   }

   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
      this.leak.record();
      return super.getBytes(index, dst, dstIndex, length);
   }

   public ByteBuf getBytes(int index, ByteBuffer dst) {
      this.leak.record();
      return super.getBytes(index, dst);
   }

   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
      this.leak.record();
      return super.getBytes(index, out, length);
   }

   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
      this.leak.record();
      return super.getBytes(index, out, length);
   }

   public ByteBuf setBoolean(int index, boolean value) {
      this.leak.record();
      return super.setBoolean(index, value);
   }

   public ByteBuf setByte(int index, int value) {
      this.leak.record();
      return super.setByte(index, value);
   }

   public ByteBuf setShort(int index, int value) {
      this.leak.record();
      return super.setShort(index, value);
   }

   public ByteBuf setMedium(int index, int value) {
      this.leak.record();
      return super.setMedium(index, value);
   }

   public ByteBuf setInt(int index, int value) {
      this.leak.record();
      return super.setInt(index, value);
   }

   public ByteBuf setLong(int index, long value) {
      this.leak.record();
      return super.setLong(index, value);
   }

   public ByteBuf setChar(int index, int value) {
      this.leak.record();
      return super.setChar(index, value);
   }

   public ByteBuf setFloat(int index, float value) {
      this.leak.record();
      return super.setFloat(index, value);
   }

   public ByteBuf setDouble(int index, double value) {
      this.leak.record();
      return super.setDouble(index, value);
   }

   public ByteBuf setBytes(int index, ByteBuf src) {
      this.leak.record();
      return super.setBytes(index, src);
   }

   public ByteBuf setBytes(int index, ByteBuf src, int length) {
      this.leak.record();
      return super.setBytes(index, src, length);
   }

   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
      this.leak.record();
      return super.setBytes(index, src, srcIndex, length);
   }

   public ByteBuf setBytes(int index, byte[] src) {
      this.leak.record();
      return super.setBytes(index, src);
   }

   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
      this.leak.record();
      return super.setBytes(index, src, srcIndex, length);
   }

   public ByteBuf setBytes(int index, ByteBuffer src) {
      this.leak.record();
      return super.setBytes(index, src);
   }

   public int setBytes(int index, InputStream in, int length) throws IOException {
      this.leak.record();
      return super.setBytes(index, in, length);
   }

   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
      this.leak.record();
      return super.setBytes(index, in, length);
   }

   public ByteBuf setZero(int index, int length) {
      this.leak.record();
      return super.setZero(index, length);
   }

   public boolean readBoolean() {
      this.leak.record();
      return super.readBoolean();
   }

   public byte readByte() {
      this.leak.record();
      return super.readByte();
   }

   public short readUnsignedByte() {
      this.leak.record();
      return super.readUnsignedByte();
   }

   public short readShort() {
      this.leak.record();
      return super.readShort();
   }

   public int readUnsignedShort() {
      this.leak.record();
      return super.readUnsignedShort();
   }

   public int readMedium() {
      this.leak.record();
      return super.readMedium();
   }

   public int readUnsignedMedium() {
      this.leak.record();
      return super.readUnsignedMedium();
   }

   public int readInt() {
      this.leak.record();
      return super.readInt();
   }

   public long readUnsignedInt() {
      this.leak.record();
      return super.readUnsignedInt();
   }

   public long readLong() {
      this.leak.record();
      return super.readLong();
   }

   public char readChar() {
      this.leak.record();
      return super.readChar();
   }

   public float readFloat() {
      this.leak.record();
      return super.readFloat();
   }

   public double readDouble() {
      this.leak.record();
      return super.readDouble();
   }

   public ByteBuf readBytes(int length) {
      this.leak.record();
      return super.readBytes(length);
   }

   public ByteBuf readBytes(ByteBuf dst) {
      this.leak.record();
      return super.readBytes(dst);
   }

   public ByteBuf readBytes(ByteBuf dst, int length) {
      this.leak.record();
      return super.readBytes(dst, length);
   }

   public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
      this.leak.record();
      return super.readBytes(dst, dstIndex, length);
   }

   public ByteBuf readBytes(byte[] dst) {
      this.leak.record();
      return super.readBytes(dst);
   }

   public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
      this.leak.record();
      return super.readBytes(dst, dstIndex, length);
   }

   public ByteBuf readBytes(ByteBuffer dst) {
      this.leak.record();
      return super.readBytes(dst);
   }

   public ByteBuf readBytes(OutputStream out, int length) throws IOException {
      this.leak.record();
      return super.readBytes(out, length);
   }

   public int readBytes(GatheringByteChannel out, int length) throws IOException {
      this.leak.record();
      return super.readBytes(out, length);
   }

   public ByteBuf skipBytes(int length) {
      this.leak.record();
      return super.skipBytes(length);
   }

   public ByteBuf writeBoolean(boolean value) {
      this.leak.record();
      return super.writeBoolean(value);
   }

   public ByteBuf writeByte(int value) {
      this.leak.record();
      return super.writeByte(value);
   }

   public ByteBuf writeShort(int value) {
      this.leak.record();
      return super.writeShort(value);
   }

   public ByteBuf writeMedium(int value) {
      this.leak.record();
      return super.writeMedium(value);
   }

   public ByteBuf writeInt(int value) {
      this.leak.record();
      return super.writeInt(value);
   }

   public ByteBuf writeLong(long value) {
      this.leak.record();
      return super.writeLong(value);
   }

   public ByteBuf writeChar(int value) {
      this.leak.record();
      return super.writeChar(value);
   }

   public ByteBuf writeFloat(float value) {
      this.leak.record();
      return super.writeFloat(value);
   }

   public ByteBuf writeDouble(double value) {
      this.leak.record();
      return super.writeDouble(value);
   }

   public ByteBuf writeBytes(ByteBuf src) {
      this.leak.record();
      return super.writeBytes(src);
   }

   public ByteBuf writeBytes(ByteBuf src, int length) {
      this.leak.record();
      return super.writeBytes(src, length);
   }

   public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
      this.leak.record();
      return super.writeBytes(src, srcIndex, length);
   }

   public ByteBuf writeBytes(byte[] src) {
      this.leak.record();
      return super.writeBytes(src);
   }

   public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
      this.leak.record();
      return super.writeBytes(src, srcIndex, length);
   }

   public ByteBuf writeBytes(ByteBuffer src) {
      this.leak.record();
      return super.writeBytes(src);
   }

   public int writeBytes(InputStream in, int length) throws IOException {
      this.leak.record();
      return super.writeBytes(in, length);
   }

   public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
      this.leak.record();
      return super.writeBytes(in, length);
   }

   public ByteBuf writeZero(int length) {
      this.leak.record();
      return super.writeZero(length);
   }

   public int indexOf(int fromIndex, int toIndex, byte value) {
      this.leak.record();
      return super.indexOf(fromIndex, toIndex, value);
   }

   public int bytesBefore(byte value) {
      this.leak.record();
      return super.bytesBefore(value);
   }

   public int bytesBefore(int length, byte value) {
      this.leak.record();
      return super.bytesBefore(length, value);
   }

   public int bytesBefore(int index, int length, byte value) {
      this.leak.record();
      return super.bytesBefore(index, length, value);
   }

   public int forEachByte(ByteBufProcessor processor) {
      this.leak.record();
      return super.forEachByte(processor);
   }

   public int forEachByte(int index, int length, ByteBufProcessor processor) {
      this.leak.record();
      return super.forEachByte(index, length, processor);
   }

   public int forEachByteDesc(ByteBufProcessor processor) {
      this.leak.record();
      return super.forEachByteDesc(processor);
   }

   public int forEachByteDesc(int index, int length, ByteBufProcessor processor) {
      this.leak.record();
      return super.forEachByteDesc(index, length, processor);
   }

   public ByteBuf copy() {
      this.leak.record();
      return super.copy();
   }

   public ByteBuf copy(int index, int length) {
      this.leak.record();
      return super.copy(index, length);
   }

   public int nioBufferCount() {
      this.leak.record();
      return super.nioBufferCount();
   }

   public ByteBuffer nioBuffer() {
      this.leak.record();
      return super.nioBuffer();
   }

   public ByteBuffer nioBuffer(int index, int length) {
      this.leak.record();
      return super.nioBuffer(index, length);
   }

   public ByteBuffer[] nioBuffers() {
      this.leak.record();
      return super.nioBuffers();
   }

   public ByteBuffer[] nioBuffers(int index, int length) {
      this.leak.record();
      return super.nioBuffers(index, length);
   }

   public ByteBuffer internalNioBuffer(int index, int length) {
      this.leak.record();
      return super.internalNioBuffer(index, length);
   }

   public String toString(Charset charset) {
      this.leak.record();
      return super.toString(charset);
   }

   public String toString(int index, int length, Charset charset) {
      this.leak.record();
      return super.toString(index, length, charset);
   }

   public ByteBuf retain() {
      this.leak.record();
      return super.retain();
   }

   public ByteBuf retain(int increment) {
      this.leak.record();
      return super.retain(increment);
   }

   public ByteBuf capacity(int newCapacity) {
      this.leak.record();
      return super.capacity(newCapacity);
   }
}
