package io.netty.buffer;

import io.netty.buffer.AbstractDerivedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class DuplicatedByteBuf extends AbstractDerivedByteBuf {
   private final ByteBuf buffer;

   public DuplicatedByteBuf(ByteBuf buffer) {
      super(buffer.maxCapacity());
      if(buffer instanceof DuplicatedByteBuf) {
         this.buffer = ((DuplicatedByteBuf)buffer).buffer;
      } else {
         this.buffer = buffer;
      }

      this.setIndex(buffer.readerIndex(), buffer.writerIndex());
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

   public int capacity() {
      return this.buffer.capacity();
   }

   public ByteBuf capacity(int newCapacity) {
      this.buffer.capacity(newCapacity);
      return this;
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
      return this.buffer.hasMemoryAddress();
   }

   public long memoryAddress() {
      return this.buffer.memoryAddress();
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

   public ByteBuf copy(int index, int length) {
      return this.buffer.copy(index, length);
   }

   public ByteBuf slice(int index, int length) {
      return this.buffer.slice(index, length);
   }

   public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
      this.buffer.getBytes(index, dst, dstIndex, length);
      return this;
   }

   public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
      this.buffer.getBytes(index, dst, dstIndex, length);
      return this;
   }

   public ByteBuf getBytes(int index, ByteBuffer dst) {
      this.buffer.getBytes(index, dst);
      return this;
   }

   public ByteBuf setByte(int index, int value) {
      this._setByte(index, value);
      return this;
   }

   protected void _setByte(int index, int value) {
      this.buffer.setByte(index, value);
   }

   public ByteBuf setShort(int index, int value) {
      this._setShort(index, value);
      return this;
   }

   protected void _setShort(int index, int value) {
      this.buffer.setShort(index, value);
   }

   public ByteBuf setMedium(int index, int value) {
      this._setMedium(index, value);
      return this;
   }

   protected void _setMedium(int index, int value) {
      this.buffer.setMedium(index, value);
   }

   public ByteBuf setInt(int index, int value) {
      this._setInt(index, value);
      return this;
   }

   protected void _setInt(int index, int value) {
      this.buffer.setInt(index, value);
   }

   public ByteBuf setLong(int index, long value) {
      this._setLong(index, value);
      return this;
   }

   protected void _setLong(int index, long value) {
      this.buffer.setLong(index, value);
   }

   public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
      this.buffer.setBytes(index, src, srcIndex, length);
      return this;
   }

   public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
      this.buffer.setBytes(index, src, srcIndex, length);
      return this;
   }

   public ByteBuf setBytes(int index, ByteBuffer src) {
      this.buffer.setBytes(index, src);
      return this;
   }

   public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
      this.buffer.getBytes(index, out, length);
      return this;
   }

   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
      return this.buffer.getBytes(index, out, length);
   }

   public int setBytes(int index, InputStream in, int length) throws IOException {
      return this.buffer.setBytes(index, in, length);
   }

   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
      return this.buffer.setBytes(index, in, length);
   }

   public int nioBufferCount() {
      return this.buffer.nioBufferCount();
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
}
