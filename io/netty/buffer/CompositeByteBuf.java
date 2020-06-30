package io.netty.buffer;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.ResourceLeak;
import io.netty.util.internal.EmptyArrays;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CompositeByteBuf extends AbstractReferenceCountedByteBuf {
   private final ResourceLeak leak;
   private final ByteBufAllocator alloc;
   private final boolean direct;
   private final List components = new ArrayList();
   private final int maxNumComponents;
   private static final ByteBuffer FULL_BYTEBUFFER = (ByteBuffer)ByteBuffer.allocate(1).position(1);
   private boolean freed;

   public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents) {
      super(Integer.MAX_VALUE);
      if(alloc == null) {
         throw new NullPointerException("alloc");
      } else {
         this.alloc = alloc;
         this.direct = direct;
         this.maxNumComponents = maxNumComponents;
         this.leak = leakDetector.open(this);
      }
   }

   public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, ByteBuf... buffers) {
      super(Integer.MAX_VALUE);
      if(alloc == null) {
         throw new NullPointerException("alloc");
      } else if(maxNumComponents < 2) {
         throw new IllegalArgumentException("maxNumComponents: " + maxNumComponents + " (expected: >= 2)");
      } else {
         this.alloc = alloc;
         this.direct = direct;
         this.maxNumComponents = maxNumComponents;
         this.addComponents0(0, (ByteBuf[])buffers);
         this.consolidateIfNeeded();
         this.setIndex(0, this.capacity());
         this.leak = leakDetector.open(this);
      }
   }

   public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, Iterable buffers) {
      super(Integer.MAX_VALUE);
      if(alloc == null) {
         throw new NullPointerException("alloc");
      } else if(maxNumComponents < 2) {
         throw new IllegalArgumentException("maxNumComponents: " + maxNumComponents + " (expected: >= 2)");
      } else {
         this.alloc = alloc;
         this.direct = direct;
         this.maxNumComponents = maxNumComponents;
         this.addComponents0(0, (Iterable)buffers);
         this.consolidateIfNeeded();
         this.setIndex(0, this.capacity());
         this.leak = leakDetector.open(this);
      }
   }

   public CompositeByteBuf addComponent(ByteBuf buffer) {
      this.addComponent0(this.components.size(), buffer);
      this.consolidateIfNeeded();
      return this;
   }

   public CompositeByteBuf addComponents(ByteBuf... buffers) {
      this.addComponents0(this.components.size(), buffers);
      this.consolidateIfNeeded();
      return this;
   }

   public CompositeByteBuf addComponents(Iterable buffers) {
      this.addComponents0(this.components.size(), buffers);
      this.consolidateIfNeeded();
      return this;
   }

   public CompositeByteBuf addComponent(int cIndex, ByteBuf buffer) {
      this.addComponent0(cIndex, buffer);
      this.consolidateIfNeeded();
      return this;
   }

   private int addComponent0(int cIndex, ByteBuf buffer) {
      this.checkComponentIndex(cIndex);
      if(buffer == null) {
         throw new NullPointerException("buffer");
      } else {
         int readableBytes = buffer.readableBytes();
         if(readableBytes == 0) {
            return cIndex;
         } else {
            CompositeByteBuf.Component c = new CompositeByteBuf.Component(buffer.order(ByteOrder.BIG_ENDIAN).slice());
            if(cIndex == this.components.size()) {
               this.components.add(c);
               if(cIndex == 0) {
                  c.endOffset = readableBytes;
               } else {
                  CompositeByteBuf.Component prev = (CompositeByteBuf.Component)this.components.get(cIndex - 1);
                  c.offset = prev.endOffset;
                  c.endOffset = c.offset + readableBytes;
               }
            } else {
               this.components.add(cIndex, c);
               this.updateComponentOffsets(cIndex);
            }

            return cIndex;
         }
      }
   }

   public CompositeByteBuf addComponents(int cIndex, ByteBuf... buffers) {
      this.addComponents0(cIndex, buffers);
      this.consolidateIfNeeded();
      return this;
   }

   private int addComponents0(int cIndex, ByteBuf... buffers) {
      this.checkComponentIndex(cIndex);
      if(buffers == null) {
         throw new NullPointerException("buffers");
      } else {
         int readableBytes = 0;

         for(ByteBuf b : buffers) {
            if(b == null) {
               break;
            }

            readableBytes += b.readableBytes();
         }

         if(readableBytes == 0) {
            return cIndex;
         } else {
            for(ByteBuf b : buffers) {
               if(b == null) {
                  break;
               }

               if(b.isReadable()) {
                  cIndex = this.addComponent0(cIndex, b) + 1;
                  int size = this.components.size();
                  if(cIndex > size) {
                     cIndex = size;
                  }
               } else {
                  b.release();
               }
            }

            return cIndex;
         }
      }
   }

   public CompositeByteBuf addComponents(int cIndex, Iterable buffers) {
      this.addComponents0(cIndex, buffers);
      this.consolidateIfNeeded();
      return this;
   }

   private int addComponents0(int cIndex, Iterable buffers) {
      if(buffers == null) {
         throw new NullPointerException("buffers");
      } else if(buffers instanceof ByteBuf) {
         return this.addComponent0(cIndex, (ByteBuf)buffers);
      } else {
         if(!(buffers instanceof Collection)) {
            List<ByteBuf> list = new ArrayList();

            for(ByteBuf b : buffers) {
               list.add(b);
            }

            buffers = list;
         }

         Collection<ByteBuf> col = (Collection)buffers;
         return this.addComponents0(cIndex, (ByteBuf[])col.toArray(new ByteBuf[col.size()]));
      }
   }

   private void consolidateIfNeeded() {
      int numComponents = this.components.size();
      if(numComponents > this.maxNumComponents) {
         int capacity = ((CompositeByteBuf.Component)this.components.get(numComponents - 1)).endOffset;
         ByteBuf consolidated = this.allocBuffer(capacity);

         for(int i = 0; i < numComponents; ++i) {
            CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
            ByteBuf b = c.buf;
            consolidated.writeBytes(b);
            c.freeIfNecessary();
         }

         CompositeByteBuf.Component c = new CompositeByteBuf.Component(consolidated);
         c.endOffset = c.length;
         this.components.clear();
         this.components.add(c);
      }

   }

   private void checkComponentIndex(int cIndex) {
      this.ensureAccessible();
      if(cIndex < 0 || cIndex > this.components.size()) {
         throw new IndexOutOfBoundsException(String.format("cIndex: %d (expected: >= 0 && <= numComponents(%d))", new Object[]{Integer.valueOf(cIndex), Integer.valueOf(this.components.size())}));
      }
   }

   private void checkComponentIndex(int cIndex, int numComponents) {
      this.ensureAccessible();
      if(cIndex < 0 || cIndex + numComponents > this.components.size()) {
         throw new IndexOutOfBoundsException(String.format("cIndex: %d, numComponents: %d (expected: cIndex >= 0 && cIndex + numComponents <= totalNumComponents(%d))", new Object[]{Integer.valueOf(cIndex), Integer.valueOf(numComponents), Integer.valueOf(this.components.size())}));
      }
   }

   private void updateComponentOffsets(int cIndex) {
      int size = this.components.size();
      if(size > cIndex) {
         CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(cIndex);
         if(cIndex == 0) {
            c.offset = 0;
            c.endOffset = c.length;
            ++cIndex;
         }

         for(int i = cIndex; i < size; ++i) {
            CompositeByteBuf.Component prev = (CompositeByteBuf.Component)this.components.get(i - 1);
            CompositeByteBuf.Component cur = (CompositeByteBuf.Component)this.components.get(i);
            cur.offset = prev.endOffset;
            cur.endOffset = cur.offset + cur.length;
         }

      }
   }

   public CompositeByteBuf removeComponent(int cIndex) {
      this.checkComponentIndex(cIndex);
      ((CompositeByteBuf.Component)this.components.remove(cIndex)).freeIfNecessary();
      this.updateComponentOffsets(cIndex);
      return this;
   }

   public CompositeByteBuf removeComponents(int cIndex, int numComponents) {
      this.checkComponentIndex(cIndex, numComponents);
      List<CompositeByteBuf.Component> toRemove = this.components.subList(cIndex, cIndex + numComponents);

      for(CompositeByteBuf.Component c : toRemove) {
         c.freeIfNecessary();
      }

      toRemove.clear();
      this.updateComponentOffsets(cIndex);
      return this;
   }

   public Iterator iterator() {
      this.ensureAccessible();
      List<ByteBuf> list = new ArrayList(this.components.size());

      for(CompositeByteBuf.Component c : this.components) {
         list.add(c.buf);
      }

      return list.iterator();
   }

   public List decompose(int offset, int length) {
      this.checkIndex(offset, length);
      if(length == 0) {
         return Collections.emptyList();
      } else {
         int componentId = this.toComponentIndex(offset);
         List<ByteBuf> slice = new ArrayList(this.components.size());
         CompositeByteBuf.Component firstC = (CompositeByteBuf.Component)this.components.get(componentId);
         ByteBuf first = firstC.buf.duplicate();
         first.readerIndex(offset - firstC.offset);
         ByteBuf buf = first;
         int bytesToSlice = length;

         while(true) {
            int readableBytes = buf.readableBytes();
            if(bytesToSlice <= readableBytes) {
               buf.writerIndex(buf.readerIndex() + bytesToSlice);
               slice.add(buf);
               break;
            }

            slice.add(buf);
            bytesToSlice -= readableBytes;
            ++componentId;
            buf = ((CompositeByteBuf.Component)this.components.get(componentId)).buf.duplicate();
            if(bytesToSlice <= 0) {
               break;
            }
         }

         for(int i = 0; i < ((List)slice).size(); ++i) {
            slice.set(i, ((ByteBuf)slice.get(i)).slice());
         }

         return slice;
      }
   }

   public boolean isDirect() {
      int size = this.components.size();
      if(size == 0) {
         return false;
      } else {
         for(int i = 0; i < size; ++i) {
            if(!((CompositeByteBuf.Component)this.components.get(i)).buf.isDirect()) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean hasArray() {
      return this.components.size() == 1?((CompositeByteBuf.Component)this.components.get(0)).buf.hasArray():false;
   }

   public byte[] array() {
      if(this.components.size() == 1) {
         return ((CompositeByteBuf.Component)this.components.get(0)).buf.array();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public int arrayOffset() {
      if(this.components.size() == 1) {
         return ((CompositeByteBuf.Component)this.components.get(0)).buf.arrayOffset();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public boolean hasMemoryAddress() {
      return this.components.size() == 1?((CompositeByteBuf.Component)this.components.get(0)).buf.hasMemoryAddress():false;
   }

   public long memoryAddress() {
      if(this.components.size() == 1) {
         return ((CompositeByteBuf.Component)this.components.get(0)).buf.memoryAddress();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public int capacity() {
      return this.components.isEmpty()?0:((CompositeByteBuf.Component)this.components.get(this.components.size() - 1)).endOffset;
   }

   public CompositeByteBuf capacity(int newCapacity) {
      this.ensureAccessible();
      if(newCapacity >= 0 && newCapacity <= this.maxCapacity()) {
         int oldCapacity = this.capacity();
         if(newCapacity > oldCapacity) {
            int paddingLength = newCapacity - oldCapacity;
            int nComponents = this.components.size();
            if(nComponents < this.maxNumComponents) {
               ByteBuf padding = this.allocBuffer(paddingLength);
               padding.setIndex(0, paddingLength);
               this.addComponent0(this.components.size(), padding);
            } else {
               ByteBuf padding = this.allocBuffer(paddingLength);
               padding.setIndex(0, paddingLength);
               this.addComponent0(this.components.size(), padding);
               this.consolidateIfNeeded();
            }
         } else if(newCapacity < oldCapacity) {
            int bytesToTrim = oldCapacity - newCapacity;
            ListIterator<CompositeByteBuf.Component> i = this.components.listIterator(this.components.size());

            while(i.hasPrevious()) {
               CompositeByteBuf.Component c = (CompositeByteBuf.Component)i.previous();
               if(bytesToTrim < c.length) {
                  CompositeByteBuf.Component newC = new CompositeByteBuf.Component(c.buf.slice(0, c.length - bytesToTrim));
                  newC.offset = c.offset;
                  newC.endOffset = newC.offset + newC.length;
                  i.set(newC);
                  break;
               }

               bytesToTrim -= c.length;
               i.remove();
            }

            if(this.readerIndex() > newCapacity) {
               this.setIndex(newCapacity, newCapacity);
            } else if(this.writerIndex() > newCapacity) {
               this.writerIndex(newCapacity);
            }
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

   public int numComponents() {
      return this.components.size();
   }

   public int maxNumComponents() {
      return this.maxNumComponents;
   }

   public int toComponentIndex(int offset) {
      this.checkIndex(offset);
      int low = 0;
      int high = this.components.size();

      while(low <= high) {
         int mid = low + high >>> 1;
         CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(mid);
         if(offset >= c.endOffset) {
            low = mid + 1;
         } else {
            if(offset >= c.offset) {
               return mid;
            }

            high = mid - 1;
         }
      }

      throw new Error("should not reach here");
   }

   public int toByteIndex(int cIndex) {
      this.checkComponentIndex(cIndex);
      return ((CompositeByteBuf.Component)this.components.get(cIndex)).offset;
   }

   public byte getByte(int index) {
      return this._getByte(index);
   }

   protected byte _getByte(int index) {
      CompositeByteBuf.Component c = this.findComponent(index);
      return c.buf.getByte(index - c.offset);
   }

   protected short _getShort(int index) {
      CompositeByteBuf.Component c = this.findComponent(index);
      return index + 2 <= c.endOffset?c.buf.getShort(index - c.offset):(this.order() == ByteOrder.BIG_ENDIAN?(short)((this._getByte(index) & 255) << 8 | this._getByte(index + 1) & 255):(short)(this._getByte(index) & 255 | (this._getByte(index + 1) & 255) << 8));
   }

   protected int _getUnsignedMedium(int index) {
      CompositeByteBuf.Component c = this.findComponent(index);
      return index + 3 <= c.endOffset?c.buf.getUnsignedMedium(index - c.offset):(this.order() == ByteOrder.BIG_ENDIAN?(this._getShort(index) & '\uffff') << 8 | this._getByte(index + 2) & 255:this._getShort(index) & '\uffff' | (this._getByte(index + 2) & 255) << 16);
   }

   protected int _getInt(int index) {
      CompositeByteBuf.Component c = this.findComponent(index);
      return index + 4 <= c.endOffset?c.buf.getInt(index - c.offset):(this.order() == ByteOrder.BIG_ENDIAN?(this._getShort(index) & '\uffff') << 16 | this._getShort(index + 2) & '\uffff':this._getShort(index) & '\uffff' | (this._getShort(index + 2) & '\uffff') << 16);
   }

   protected long _getLong(int index) {
      CompositeByteBuf.Component c = this.findComponent(index);
      return index + 8 <= c.endOffset?c.buf.getLong(index - c.offset):(this.order() == ByteOrder.BIG_ENDIAN?((long)this._getInt(index) & 4294967295L) << 32 | (long)this._getInt(index + 4) & 4294967295L:(long)this._getInt(index) & 4294967295L | ((long)this._getInt(index + 4) & 4294967295L) << 32);
   }

   public CompositeByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
      this.checkDstIndex(index, length, dstIndex, dst.length);
      if(length == 0) {
         return this;
      } else {
         for(int i = this.toComponentIndex(index); length > 0; ++i) {
            CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
            ByteBuf s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.getBytes(index - adjustment, dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
            length -= localLength;
         }

         return this;
      }
   }

   public CompositeByteBuf getBytes(int index, ByteBuffer dst) {
      int limit = dst.limit();
      int length = dst.remaining();
      this.checkIndex(index, length);
      if(length == 0) {
         return this;
      } else {
         int i = this.toComponentIndex(index);

         try {
            while(length > 0) {
               CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
               ByteBuf s = c.buf;
               int adjustment = c.offset;
               int localLength = Math.min(length, s.capacity() - (index - adjustment));
               dst.limit(dst.position() + localLength);
               s.getBytes(index - adjustment, dst);
               index += localLength;
               length -= localLength;
               ++i;
            }
         } finally {
            dst.limit(limit);
         }

         return this;
      }
   }

   public CompositeByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
      this.checkDstIndex(index, length, dstIndex, dst.capacity());
      if(length == 0) {
         return this;
      } else {
         for(int i = this.toComponentIndex(index); length > 0; ++i) {
            CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
            ByteBuf s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.getBytes(index - adjustment, dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
            length -= localLength;
         }

         return this;
      }
   }

   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
      int count = this.nioBufferCount();
      if(count == 1) {
         return out.write(this.internalNioBuffer(index, length));
      } else {
         long writtenBytes = out.write(this.nioBuffers(index, length));
         return writtenBytes > 2147483647L?Integer.MAX_VALUE:(int)writtenBytes;
      }
   }

   public CompositeByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
      this.checkIndex(index, length);
      if(length == 0) {
         return this;
      } else {
         for(int i = this.toComponentIndex(index); length > 0; ++i) {
            CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
            ByteBuf s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.getBytes(index - adjustment, out, localLength);
            index += localLength;
            length -= localLength;
         }

         return this;
      }
   }

   public CompositeByteBuf setByte(int index, int value) {
      CompositeByteBuf.Component c = this.findComponent(index);
      c.buf.setByte(index - c.offset, value);
      return this;
   }

   protected void _setByte(int index, int value) {
      this.setByte(index, value);
   }

   public CompositeByteBuf setShort(int index, int value) {
      return (CompositeByteBuf)super.setShort(index, value);
   }

   protected void _setShort(int index, int value) {
      CompositeByteBuf.Component c = this.findComponent(index);
      if(index + 2 <= c.endOffset) {
         c.buf.setShort(index - c.offset, value);
      } else if(this.order() == ByteOrder.BIG_ENDIAN) {
         this._setByte(index, (byte)(value >>> 8));
         this._setByte(index + 1, (byte)value);
      } else {
         this._setByte(index, (byte)value);
         this._setByte(index + 1, (byte)(value >>> 8));
      }

   }

   public CompositeByteBuf setMedium(int index, int value) {
      return (CompositeByteBuf)super.setMedium(index, value);
   }

   protected void _setMedium(int index, int value) {
      CompositeByteBuf.Component c = this.findComponent(index);
      if(index + 3 <= c.endOffset) {
         c.buf.setMedium(index - c.offset, value);
      } else if(this.order() == ByteOrder.BIG_ENDIAN) {
         this._setShort(index, (short)(value >> 8));
         this._setByte(index + 2, (byte)value);
      } else {
         this._setShort(index, (short)value);
         this._setByte(index + 2, (byte)(value >>> 16));
      }

   }

   public CompositeByteBuf setInt(int index, int value) {
      return (CompositeByteBuf)super.setInt(index, value);
   }

   protected void _setInt(int index, int value) {
      CompositeByteBuf.Component c = this.findComponent(index);
      if(index + 4 <= c.endOffset) {
         c.buf.setInt(index - c.offset, value);
      } else if(this.order() == ByteOrder.BIG_ENDIAN) {
         this._setShort(index, (short)(value >>> 16));
         this._setShort(index + 2, (short)value);
      } else {
         this._setShort(index, (short)value);
         this._setShort(index + 2, (short)(value >>> 16));
      }

   }

   public CompositeByteBuf setLong(int index, long value) {
      return (CompositeByteBuf)super.setLong(index, value);
   }

   protected void _setLong(int index, long value) {
      CompositeByteBuf.Component c = this.findComponent(index);
      if(index + 8 <= c.endOffset) {
         c.buf.setLong(index - c.offset, value);
      } else if(this.order() == ByteOrder.BIG_ENDIAN) {
         this._setInt(index, (int)(value >>> 32));
         this._setInt(index + 4, (int)value);
      } else {
         this._setInt(index, (int)value);
         this._setInt(index + 4, (int)(value >>> 32));
      }

   }

   public CompositeByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
      this.checkSrcIndex(index, length, srcIndex, src.length);
      if(length == 0) {
         return this;
      } else {
         for(int i = this.toComponentIndex(index); length > 0; ++i) {
            CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
            ByteBuf s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.setBytes(index - adjustment, src, srcIndex, localLength);
            index += localLength;
            srcIndex += localLength;
            length -= localLength;
         }

         return this;
      }
   }

   public CompositeByteBuf setBytes(int index, ByteBuffer src) {
      int limit = src.limit();
      int length = src.remaining();
      this.checkIndex(index, length);
      if(length == 0) {
         return this;
      } else {
         int i = this.toComponentIndex(index);

         try {
            while(length > 0) {
               CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
               ByteBuf s = c.buf;
               int adjustment = c.offset;
               int localLength = Math.min(length, s.capacity() - (index - adjustment));
               src.limit(src.position() + localLength);
               s.setBytes(index - adjustment, src);
               index += localLength;
               length -= localLength;
               ++i;
            }
         } finally {
            src.limit(limit);
         }

         return this;
      }
   }

   public CompositeByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
      this.checkSrcIndex(index, length, srcIndex, src.capacity());
      if(length == 0) {
         return this;
      } else {
         for(int i = this.toComponentIndex(index); length > 0; ++i) {
            CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
            ByteBuf s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.setBytes(index - adjustment, src, srcIndex, localLength);
            index += localLength;
            srcIndex += localLength;
            length -= localLength;
         }

         return this;
      }
   }

   public int setBytes(int index, InputStream in, int length) throws IOException {
      this.checkIndex(index, length);
      if(length == 0) {
         return in.read(EmptyArrays.EMPTY_BYTES);
      } else {
         int i = this.toComponentIndex(index);
         int readBytes = 0;

         while(true) {
            CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
            ByteBuf s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            int localReadBytes = s.setBytes(index - adjustment, in, localLength);
            if(localReadBytes < 0) {
               if(readBytes == 0) {
                  return -1;
               }
               break;
            }

            if(localReadBytes == localLength) {
               index += localLength;
               length -= localLength;
               readBytes += localLength;
               ++i;
            } else {
               index += localReadBytes;
               length -= localReadBytes;
               readBytes += localReadBytes;
            }

            if(length <= 0) {
               break;
            }
         }

         return readBytes;
      }
   }

   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
      this.checkIndex(index, length);
      if(length == 0) {
         return in.read(FULL_BYTEBUFFER);
      } else {
         int i = this.toComponentIndex(index);
         int readBytes = 0;

         while(true) {
            CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
            ByteBuf s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            int localReadBytes = s.setBytes(index - adjustment, in, localLength);
            if(localReadBytes == 0) {
               break;
            }

            if(localReadBytes < 0) {
               if(readBytes == 0) {
                  return -1;
               }
               break;
            }

            if(localReadBytes == localLength) {
               index += localLength;
               length -= localLength;
               readBytes += localLength;
               ++i;
            } else {
               index += localReadBytes;
               length -= localReadBytes;
               readBytes += localReadBytes;
            }

            if(length <= 0) {
               break;
            }
         }

         return readBytes;
      }
   }

   public ByteBuf copy(int index, int length) {
      this.checkIndex(index, length);
      ByteBuf dst = Unpooled.buffer(length);
      if(length != 0) {
         this.copyTo(index, length, this.toComponentIndex(index), dst);
      }

      return dst;
   }

   private void copyTo(int index, int length, int componentId, ByteBuf dst) {
      int dstIndex = 0;

      for(int i = componentId; length > 0; ++i) {
         CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
         ByteBuf s = c.buf;
         int adjustment = c.offset;
         int localLength = Math.min(length, s.capacity() - (index - adjustment));
         s.getBytes(index - adjustment, dst, dstIndex, localLength);
         index += localLength;
         dstIndex += localLength;
         length -= localLength;
      }

      dst.writerIndex(dst.capacity());
   }

   public ByteBuf component(int cIndex) {
      return this.internalComponent(cIndex).duplicate();
   }

   public ByteBuf componentAtOffset(int offset) {
      return this.internalComponentAtOffset(offset).duplicate();
   }

   public ByteBuf internalComponent(int cIndex) {
      this.checkComponentIndex(cIndex);
      return ((CompositeByteBuf.Component)this.components.get(cIndex)).buf;
   }

   public ByteBuf internalComponentAtOffset(int offset) {
      return this.findComponent(offset).buf;
   }

   private CompositeByteBuf.Component findComponent(int offset) {
      this.checkIndex(offset);
      int low = 0;
      int high = this.components.size();

      while(low <= high) {
         int mid = low + high >>> 1;
         CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(mid);
         if(offset >= c.endOffset) {
            low = mid + 1;
         } else {
            if(offset >= c.offset) {
               return c;
            }

            high = mid - 1;
         }
      }

      throw new Error("should not reach here");
   }

   public int nioBufferCount() {
      if(this.components.size() == 1) {
         return ((CompositeByteBuf.Component)this.components.get(0)).buf.nioBufferCount();
      } else {
         int count = 0;
         int componentsCount = this.components.size();

         for(int i = 0; i < componentsCount; ++i) {
            CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
            count += c.buf.nioBufferCount();
         }

         return count;
      }
   }

   public ByteBuffer internalNioBuffer(int index, int length) {
      if(this.components.size() == 1) {
         return ((CompositeByteBuf.Component)this.components.get(0)).buf.internalNioBuffer(index, length);
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public ByteBuffer nioBuffer(int index, int length) {
      if(this.components.size() == 1) {
         ByteBuf buf = ((CompositeByteBuf.Component)this.components.get(0)).buf;
         if(buf.nioBufferCount() == 1) {
            return ((CompositeByteBuf.Component)this.components.get(0)).buf.nioBuffer(index, length);
         }
      }

      ByteBuffer merged = ByteBuffer.allocate(length).order(this.order());
      ByteBuffer[] buffers = this.nioBuffers(index, length);

      for(int i = 0; i < buffers.length; ++i) {
         merged.put(buffers[i]);
      }

      merged.flip();
      return merged;
   }

   public ByteBuffer[] nioBuffers(int index, int length) {
      this.checkIndex(index, length);
      if(length == 0) {
         return EmptyArrays.EMPTY_BYTE_BUFFERS;
      } else {
         List<ByteBuffer> buffers = new ArrayList(this.components.size());

         for(int i = this.toComponentIndex(index); length > 0; ++i) {
            CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
            ByteBuf s = c.buf;
            int adjustment = c.offset;
            int localLength = Math.min(length, s.capacity() - (index - adjustment));
            switch(s.nioBufferCount()) {
            case 0:
               throw new UnsupportedOperationException();
            case 1:
               buffers.add(s.nioBuffer(index - adjustment, localLength));
               break;
            default:
               Collections.addAll(buffers, s.nioBuffers(index - adjustment, localLength));
            }

            index += localLength;
            length -= localLength;
         }

         return (ByteBuffer[])buffers.toArray(new ByteBuffer[buffers.size()]);
      }
   }

   public CompositeByteBuf consolidate() {
      this.ensureAccessible();
      int numComponents = this.numComponents();
      if(numComponents <= 1) {
         return this;
      } else {
         CompositeByteBuf.Component last = (CompositeByteBuf.Component)this.components.get(numComponents - 1);
         int capacity = last.endOffset;
         ByteBuf consolidated = this.allocBuffer(capacity);

         for(int i = 0; i < numComponents; ++i) {
            CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
            ByteBuf b = c.buf;
            consolidated.writeBytes(b);
            c.freeIfNecessary();
         }

         this.components.clear();
         this.components.add(new CompositeByteBuf.Component(consolidated));
         this.updateComponentOffsets(0);
         return this;
      }
   }

   public CompositeByteBuf consolidate(int cIndex, int numComponents) {
      this.checkComponentIndex(cIndex, numComponents);
      if(numComponents <= 1) {
         return this;
      } else {
         int endCIndex = cIndex + numComponents;
         CompositeByteBuf.Component last = (CompositeByteBuf.Component)this.components.get(endCIndex - 1);
         int capacity = last.endOffset - ((CompositeByteBuf.Component)this.components.get(cIndex)).offset;
         ByteBuf consolidated = this.allocBuffer(capacity);

         for(int i = cIndex; i < endCIndex; ++i) {
            CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(i);
            ByteBuf b = c.buf;
            consolidated.writeBytes(b);
            c.freeIfNecessary();
         }

         this.components.subList(cIndex + 1, endCIndex).clear();
         this.components.set(cIndex, new CompositeByteBuf.Component(consolidated));
         this.updateComponentOffsets(cIndex);
         return this;
      }
   }

   public CompositeByteBuf discardReadComponents() {
      this.ensureAccessible();
      int readerIndex = this.readerIndex();
      if(readerIndex == 0) {
         return this;
      } else {
         int writerIndex = this.writerIndex();
         if(readerIndex == writerIndex && writerIndex == this.capacity()) {
            for(CompositeByteBuf.Component c : this.components) {
               c.freeIfNecessary();
            }

            this.components.clear();
            this.setIndex(0, 0);
            this.adjustMarkers(readerIndex);
            return this;
         } else {
            int firstComponentId = this.toComponentIndex(readerIndex);

            for(int i = 0; i < firstComponentId; ++i) {
               ((CompositeByteBuf.Component)this.components.get(i)).freeIfNecessary();
            }

            this.components.subList(0, firstComponentId).clear();
            CompositeByteBuf.Component first = (CompositeByteBuf.Component)this.components.get(0);
            int offset = first.offset;
            this.updateComponentOffsets(0);
            this.setIndex(readerIndex - offset, writerIndex - offset);
            this.adjustMarkers(offset);
            return this;
         }
      }
   }

   public CompositeByteBuf discardReadBytes() {
      this.ensureAccessible();
      int readerIndex = this.readerIndex();
      if(readerIndex == 0) {
         return this;
      } else {
         int writerIndex = this.writerIndex();
         if(readerIndex == writerIndex && writerIndex == this.capacity()) {
            for(CompositeByteBuf.Component c : this.components) {
               c.freeIfNecessary();
            }

            this.components.clear();
            this.setIndex(0, 0);
            this.adjustMarkers(readerIndex);
            return this;
         } else {
            int firstComponentId = this.toComponentIndex(readerIndex);

            for(int i = 0; i < firstComponentId; ++i) {
               ((CompositeByteBuf.Component)this.components.get(i)).freeIfNecessary();
            }

            this.components.subList(0, firstComponentId).clear();
            CompositeByteBuf.Component c = (CompositeByteBuf.Component)this.components.get(0);
            int adjustment = readerIndex - c.offset;
            if(adjustment == c.length) {
               this.components.remove(0);
            } else {
               CompositeByteBuf.Component newC = new CompositeByteBuf.Component(c.buf.slice(adjustment, c.length - adjustment));
               this.components.set(0, newC);
            }

            this.updateComponentOffsets(0);
            this.setIndex(0, writerIndex - readerIndex);
            this.adjustMarkers(readerIndex);
            return this;
         }
      }
   }

   private ByteBuf allocBuffer(int capacity) {
      return this.direct?this.alloc().directBuffer(capacity):this.alloc().heapBuffer(capacity);
   }

   public String toString() {
      String result = super.toString();
      result = result.substring(0, result.length() - 1);
      return result + ", components=" + this.components.size() + ')';
   }

   public CompositeByteBuf readerIndex(int readerIndex) {
      return (CompositeByteBuf)super.readerIndex(readerIndex);
   }

   public CompositeByteBuf writerIndex(int writerIndex) {
      return (CompositeByteBuf)super.writerIndex(writerIndex);
   }

   public CompositeByteBuf setIndex(int readerIndex, int writerIndex) {
      return (CompositeByteBuf)super.setIndex(readerIndex, writerIndex);
   }

   public CompositeByteBuf clear() {
      return (CompositeByteBuf)super.clear();
   }

   public CompositeByteBuf markReaderIndex() {
      return (CompositeByteBuf)super.markReaderIndex();
   }

   public CompositeByteBuf resetReaderIndex() {
      return (CompositeByteBuf)super.resetReaderIndex();
   }

   public CompositeByteBuf markWriterIndex() {
      return (CompositeByteBuf)super.markWriterIndex();
   }

   public CompositeByteBuf resetWriterIndex() {
      return (CompositeByteBuf)super.resetWriterIndex();
   }

   public CompositeByteBuf ensureWritable(int minWritableBytes) {
      return (CompositeByteBuf)super.ensureWritable(minWritableBytes);
   }

   public CompositeByteBuf getBytes(int index, ByteBuf dst) {
      return (CompositeByteBuf)super.getBytes(index, dst);
   }

   public CompositeByteBuf getBytes(int index, ByteBuf dst, int length) {
      return (CompositeByteBuf)super.getBytes(index, dst, length);
   }

   public CompositeByteBuf getBytes(int index, byte[] dst) {
      return (CompositeByteBuf)super.getBytes(index, dst);
   }

   public CompositeByteBuf setBoolean(int index, boolean value) {
      return (CompositeByteBuf)super.setBoolean(index, value);
   }

   public CompositeByteBuf setChar(int index, int value) {
      return (CompositeByteBuf)super.setChar(index, value);
   }

   public CompositeByteBuf setFloat(int index, float value) {
      return (CompositeByteBuf)super.setFloat(index, value);
   }

   public CompositeByteBuf setDouble(int index, double value) {
      return (CompositeByteBuf)super.setDouble(index, value);
   }

   public CompositeByteBuf setBytes(int index, ByteBuf src) {
      return (CompositeByteBuf)super.setBytes(index, src);
   }

   public CompositeByteBuf setBytes(int index, ByteBuf src, int length) {
      return (CompositeByteBuf)super.setBytes(index, src, length);
   }

   public CompositeByteBuf setBytes(int index, byte[] src) {
      return (CompositeByteBuf)super.setBytes(index, src);
   }

   public CompositeByteBuf setZero(int index, int length) {
      return (CompositeByteBuf)super.setZero(index, length);
   }

   public CompositeByteBuf readBytes(ByteBuf dst) {
      return (CompositeByteBuf)super.readBytes(dst);
   }

   public CompositeByteBuf readBytes(ByteBuf dst, int length) {
      return (CompositeByteBuf)super.readBytes(dst, length);
   }

   public CompositeByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
      return (CompositeByteBuf)super.readBytes(dst, dstIndex, length);
   }

   public CompositeByteBuf readBytes(byte[] dst) {
      return (CompositeByteBuf)super.readBytes(dst);
   }

   public CompositeByteBuf readBytes(byte[] dst, int dstIndex, int length) {
      return (CompositeByteBuf)super.readBytes(dst, dstIndex, length);
   }

   public CompositeByteBuf readBytes(ByteBuffer dst) {
      return (CompositeByteBuf)super.readBytes(dst);
   }

   public CompositeByteBuf readBytes(OutputStream out, int length) throws IOException {
      return (CompositeByteBuf)super.readBytes(out, length);
   }

   public CompositeByteBuf skipBytes(int length) {
      return (CompositeByteBuf)super.skipBytes(length);
   }

   public CompositeByteBuf writeBoolean(boolean value) {
      return (CompositeByteBuf)super.writeBoolean(value);
   }

   public CompositeByteBuf writeByte(int value) {
      return (CompositeByteBuf)super.writeByte(value);
   }

   public CompositeByteBuf writeShort(int value) {
      return (CompositeByteBuf)super.writeShort(value);
   }

   public CompositeByteBuf writeMedium(int value) {
      return (CompositeByteBuf)super.writeMedium(value);
   }

   public CompositeByteBuf writeInt(int value) {
      return (CompositeByteBuf)super.writeInt(value);
   }

   public CompositeByteBuf writeLong(long value) {
      return (CompositeByteBuf)super.writeLong(value);
   }

   public CompositeByteBuf writeChar(int value) {
      return (CompositeByteBuf)super.writeChar(value);
   }

   public CompositeByteBuf writeFloat(float value) {
      return (CompositeByteBuf)super.writeFloat(value);
   }

   public CompositeByteBuf writeDouble(double value) {
      return (CompositeByteBuf)super.writeDouble(value);
   }

   public CompositeByteBuf writeBytes(ByteBuf src) {
      return (CompositeByteBuf)super.writeBytes(src);
   }

   public CompositeByteBuf writeBytes(ByteBuf src, int length) {
      return (CompositeByteBuf)super.writeBytes(src, length);
   }

   public CompositeByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
      return (CompositeByteBuf)super.writeBytes(src, srcIndex, length);
   }

   public CompositeByteBuf writeBytes(byte[] src) {
      return (CompositeByteBuf)super.writeBytes(src);
   }

   public CompositeByteBuf writeBytes(byte[] src, int srcIndex, int length) {
      return (CompositeByteBuf)super.writeBytes(src, srcIndex, length);
   }

   public CompositeByteBuf writeBytes(ByteBuffer src) {
      return (CompositeByteBuf)super.writeBytes(src);
   }

   public CompositeByteBuf writeZero(int length) {
      return (CompositeByteBuf)super.writeZero(length);
   }

   public CompositeByteBuf retain(int increment) {
      return (CompositeByteBuf)super.retain(increment);
   }

   public CompositeByteBuf retain() {
      return (CompositeByteBuf)super.retain();
   }

   public ByteBuffer[] nioBuffers() {
      return this.nioBuffers(this.readerIndex(), this.readableBytes());
   }

   public CompositeByteBuf discardSomeReadBytes() {
      return this.discardReadComponents();
   }

   protected void deallocate() {
      if(!this.freed) {
         this.freed = true;
         int size = this.components.size();

         for(int i = 0; i < size; ++i) {
            ((CompositeByteBuf.Component)this.components.get(i)).freeIfNecessary();
         }

         if(this.leak != null) {
            this.leak.close();
         }

      }
   }

   public ByteBuf unwrap() {
      return null;
   }

   private static final class Component {
      final ByteBuf buf;
      final int length;
      int offset;
      int endOffset;

      Component(ByteBuf buf) {
         this.buf = buf;
         this.length = buf.readableBytes();
      }

      void freeIfNecessary() {
         this.buf.release();
      }
   }
}
