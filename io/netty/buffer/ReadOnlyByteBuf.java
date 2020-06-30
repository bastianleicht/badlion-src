package io.netty.buffer;

import io.netty.buffer.AbstractDerivedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.DuplicatedByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class ReadOnlyByteBuf extends AbstractDerivedByteBuf {
   private final ByteBuf buffer;

   public ReadOnlyByteBuf(ByteBuf buffer) {
      super(buffer.maxCapacity());
      if(!(buffer instanceof ReadOnlyByteBuf) && !(buffer instanceof DuplicatedByteBuf)) {
         this.buffer = buffer;
      } else {
         this.buffer = buffer.unwrap();
      }

      this.setIndex(buffer.readerIndex(), buffer.writerIndex());
   }

   public boolean isWritable() {
      return false;
   }

   public boolean isWritable(int numBytes) {
      return false;
   }

   public ByteBuf unwrap() {
      return this.buffer;
   }

   public ByteBufAllocator alloc() {
      return this.buffer.alloc();
   }

   public ByteOrder order() {
      return this.buffer.order();
   }

   public boolean isDirect() {
      return this.buffer.isDirect();
   }

   public boolean hasArray() {
      return false;
   }

   public byte[] array() {
      throw new ReadOnlyBufferException();
   }

   public int arrayOffset() {
      throw new ReadOnlyBufferException();
   }

   public boolean hasMemoryAddress() {
      return false;
   }

   public long memoryAddress() {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf discardReadBytes() {
      throw new ReadOnlyBufferException();
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

   public ByteBuf setByte(int index, int value) {
      throw new ReadOnlyBufferException();
   }

   protected void _setByte(int index, int value) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setShort(int index, int value) {
      throw new ReadOnlyBufferException();
   }

   protected void _setShort(int index, int value) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setMedium(int index, int value) {
      throw new ReadOnlyBufferException();
   }

   protected void _setMedium(int index, int value) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setInt(int index, int value) {
      throw new ReadOnlyBufferException();
   }

   protected void _setInt(int index, int value) {
      throw new ReadOnlyBufferException();
   }

   public ByteBuf setLong(int index, long value) {
      throw new ReadOnlyBufferException();
   }

   protected void _setLong(int index, long value) {
      throw new ReadOnlyBufferException();
   }

   public int setBytes(int index, InputStream in, int length) {
      throw new ReadOnlyBufferException();
   }

   public int setBytes(int index, ScatteringByteChannel in, int length) {
      throw new ReadOnlyBufferException();
   }

   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
      return this.buffer.getBytes(index, out, length);
   }

   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
      this.buffer.getBytes(index, out, length);
      return this;
   }

   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
      this.buffer.getBytes(index, dst, dstIndex, length);
      return this;
   }

   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
      this.buffer.getBytes(index, dst, dstIndex, length);
      return this;
   }

   public ByteBuf getBytes(int index, ByteBuffer dst) {
      this.buffer.getBytes(index, dst);
      return this;
   }

   public ByteBuf duplicate() {
      return new ReadOnlyByteBuf(this);
   }

   public ByteBuf copy(int index, int length) {
      return this.buffer.copy(index, length);
   }

   public ByteBuf slice(int index, int length) {
      return Unpooled.unmodifiableBuffer(this.buffer.slice(index, length));
   }

   public byte getByte(int index) {
      return this._getByte(index);
   }

   protected byte _getByte(int index) {
      return this.buffer.getByte(index);
   }

   public short getShort(int index) {
      return this._getShort(index);
   }

   protected short _getShort(int index) {
      return this.buffer.getShort(index);
   }

   public int getUnsignedMedium(int index) {
      return this._getUnsignedMedium(index);
   }

   protected int _getUnsignedMedium(int index) {
      return this.buffer.getUnsignedMedium(index);
   }

   public int getInt(int index) {
      return this._getInt(index);
   }

   protected int _getInt(int index) {
      return this.buffer.getInt(index);
   }

   public long getLong(int index) {
      return this._getLong(index);
   }

   protected long _getLong(int index) {
      return this.buffer.getLong(index);
   }

   public int nioBufferCount() {
      return this.buffer.nioBufferCount();
   }

   public ByteBuffer nioBuffer(int index, int length) {
      return this.buffer.nioBuffer(index, length).asReadOnlyBuffer();
   }

   public ByteBuffer[] nioBuffers(int index, int length) {
      return this.buffer.nioBuffers(index, length);
   }

   public ByteBuffer internalNioBuffer(int index, int length) {
      return this.nioBuffer(index, length);
   }

   public int forEachByte(int index, int length, ByteBufProcessor processor) {
      return this.buffer.forEachByte(index, length, processor);
   }

   public int forEachByteDesc(int index, int length, ByteBufProcessor processor) {
      return this.buffer.forEachByteDesc(index, length, processor);
   }

   public int capacity() {
      return this.buffer.capacity();
   }

   public ByteBuf capacity(int newCapacity) {
      throw new ReadOnlyBufferException();
   }
}
