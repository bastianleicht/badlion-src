package io.netty.buffer;

import io.netty.buffer.PoolChunk;
import io.netty.buffer.PoolChunkList;
import io.netty.buffer.PoolSubpage;
import io.netty.buffer.PoolThreadCache;
import io.netty.buffer.PooledByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.PooledDirectByteBuf;
import io.netty.buffer.PooledHeapByteBuf;
import io.netty.buffer.PooledUnsafeDirectByteBuf;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.nio.ByteBuffer;

abstract class PoolArena {
   static final int numTinySubpagePools = 32;
   final PooledByteBufAllocator parent;
   private final int maxOrder;
   final int pageSize;
   final int pageShifts;
   final int chunkSize;
   final int subpageOverflowMask;
   final int numSmallSubpagePools;
   private final PoolSubpage[] tinySubpagePools;
   private final PoolSubpage[] smallSubpagePools;
   private final PoolChunkList q050;
   private final PoolChunkList q025;
   private final PoolChunkList q000;
   private final PoolChunkList qInit;
   private final PoolChunkList q075;
   private final PoolChunkList q100;

   protected PoolArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize) {
      this.parent = parent;
      this.pageSize = pageSize;
      this.maxOrder = maxOrder;
      this.pageShifts = pageShifts;
      this.chunkSize = chunkSize;
      this.subpageOverflowMask = ~(pageSize - 1);
      this.tinySubpagePools = this.newSubpagePoolArray(32);

      for(int i = 0; i < this.tinySubpagePools.length; ++i) {
         this.tinySubpagePools[i] = this.newSubpagePoolHead(pageSize);
      }

      this.numSmallSubpagePools = pageShifts - 9;
      this.smallSubpagePools = this.newSubpagePoolArray(this.numSmallSubpagePools);

      for(int i = 0; i < this.smallSubpagePools.length; ++i) {
         this.smallSubpagePools[i] = this.newSubpagePoolHead(pageSize);
      }

      this.q100 = new PoolChunkList(this, (PoolChunkList)null, 100, Integer.MAX_VALUE);
      this.q075 = new PoolChunkList(this, this.q100, 75, 100);
      this.q050 = new PoolChunkList(this, this.q075, 50, 100);
      this.q025 = new PoolChunkList(this, this.q050, 25, 75);
      this.q000 = new PoolChunkList(this, this.q025, 1, 50);
      this.qInit = new PoolChunkList(this, this.q000, Integer.MIN_VALUE, 25);
      this.q100.prevList = this.q075;
      this.q075.prevList = this.q050;
      this.q050.prevList = this.q025;
      this.q025.prevList = this.q000;
      this.q000.prevList = null;
      this.qInit.prevList = this.qInit;
   }

   private PoolSubpage newSubpagePoolHead(int pageSize) {
      PoolSubpage<T> head = new PoolSubpage(pageSize);
      head.prev = head;
      head.next = head;
      return head;
   }

   private PoolSubpage[] newSubpagePoolArray(int size) {
      return new PoolSubpage[size];
   }

   abstract boolean isDirect();

   PooledByteBuf allocate(PoolThreadCache cache, int reqCapacity, int maxCapacity) {
      PooledByteBuf<T> buf = this.newByteBuf(maxCapacity);
      this.allocate(cache, buf, reqCapacity);
      return buf;
   }

   static int tinyIdx(int normCapacity) {
      return normCapacity >>> 4;
   }

   static int smallIdx(int normCapacity) {
      int tableIdx = 0;

      for(int i = normCapacity >>> 10; i != 0; ++tableIdx) {
         i >>>= 1;
      }

      return tableIdx;
   }

   boolean isTinyOrSmall(int normCapacity) {
      return (normCapacity & this.subpageOverflowMask) == 0;
   }

   static boolean isTiny(int normCapacity) {
      return (normCapacity & -512) == 0;
   }

   private void allocate(PoolThreadCache cache, PooledByteBuf buf, int reqCapacity) {
      int normCapacity = this.normalizeCapacity(reqCapacity);
      if(this.isTinyOrSmall(normCapacity)) {
         int tableIdx;
         PoolSubpage<T>[] table;
         if(isTiny(normCapacity)) {
            if(cache.allocateTiny(this, buf, reqCapacity, normCapacity)) {
               return;
            }

            tableIdx = tinyIdx(normCapacity);
            table = this.tinySubpagePools;
         } else {
            if(cache.allocateSmall(this, buf, reqCapacity, normCapacity)) {
               return;
            }

            tableIdx = smallIdx(normCapacity);
            table = this.smallSubpagePools;
         }

         synchronized(this) {
            PoolSubpage<T> head = table[tableIdx];
            PoolSubpage<T> s = head.next;
            if(s != head) {
               if($assertionsDisabled || s.doNotDestroy && s.elemSize == normCapacity) {
                  long handle = s.allocate();

                  assert handle >= 0L;

                  s.chunk.initBufWithSubpage(buf, handle, reqCapacity);
                  return;
               }

               throw new AssertionError();
            }
         }
      } else {
         if(normCapacity > this.chunkSize) {
            this.allocateHuge(buf, reqCapacity);
            return;
         }

         if(cache.allocateNormal(this, buf, reqCapacity, normCapacity)) {
            return;
         }
      }

      this.allocateNormal(buf, reqCapacity, normCapacity);
   }

   private synchronized void allocateNormal(PooledByteBuf buf, int reqCapacity, int normCapacity) {
      if(!this.q050.allocate(buf, reqCapacity, normCapacity) && !this.q025.allocate(buf, reqCapacity, normCapacity) && !this.q000.allocate(buf, reqCapacity, normCapacity) && !this.qInit.allocate(buf, reqCapacity, normCapacity) && !this.q075.allocate(buf, reqCapacity, normCapacity) && !this.q100.allocate(buf, reqCapacity, normCapacity)) {
         PoolChunk<T> c = this.newChunk(this.pageSize, this.maxOrder, this.pageShifts, this.chunkSize);
         long handle = c.allocate(normCapacity);

         assert handle > 0L;

         c.initBuf(buf, handle, reqCapacity);
         this.qInit.add(c);
      }
   }

   private void allocateHuge(PooledByteBuf buf, int reqCapacity) {
      buf.initUnpooled(this.newUnpooledChunk(reqCapacity), reqCapacity);
   }

   void free(PoolChunk chunk, long handle, int normCapacity) {
      if(chunk.unpooled) {
         this.destroyChunk(chunk);
      } else {
         PoolThreadCache cache = (PoolThreadCache)this.parent.threadCache.get();
         if(cache.add(this, chunk, handle, normCapacity)) {
            return;
         }

         synchronized(this) {
            chunk.parent.free(chunk, handle);
         }
      }

   }

   PoolSubpage findSubpagePoolHead(int elemSize) {
      int tableIdx;
      PoolSubpage<T>[] table;
      if(isTiny(elemSize)) {
         tableIdx = elemSize >>> 4;
         table = this.tinySubpagePools;
      } else {
         tableIdx = 0;

         for(elemSize = elemSize >>> 10; elemSize != 0; ++tableIdx) {
            elemSize >>>= 1;
         }

         table = this.smallSubpagePools;
      }

      return table[tableIdx];
   }

   int normalizeCapacity(int reqCapacity) {
      if(reqCapacity < 0) {
         throw new IllegalArgumentException("capacity: " + reqCapacity + " (expected: 0+)");
      } else if(reqCapacity >= this.chunkSize) {
         return reqCapacity;
      } else if(!isTiny(reqCapacity)) {
         int normalizedCapacity = reqCapacity - 1;
         normalizedCapacity = normalizedCapacity | normalizedCapacity >>> 1;
         normalizedCapacity = normalizedCapacity | normalizedCapacity >>> 2;
         normalizedCapacity = normalizedCapacity | normalizedCapacity >>> 4;
         normalizedCapacity = normalizedCapacity | normalizedCapacity >>> 8;
         normalizedCapacity = normalizedCapacity | normalizedCapacity >>> 16;
         ++normalizedCapacity;
         if(normalizedCapacity < 0) {
            normalizedCapacity >>>= 1;
         }

         return normalizedCapacity;
      } else {
         return (reqCapacity & 15) == 0?reqCapacity:(reqCapacity & -16) + 16;
      }
   }

   void reallocate(PooledByteBuf buf, int newCapacity, boolean freeOldMemory) {
      if(newCapacity >= 0 && newCapacity <= buf.maxCapacity()) {
         int oldCapacity = buf.length;
         if(oldCapacity != newCapacity) {
            PoolChunk<T> oldChunk = buf.chunk;
            long oldHandle = buf.handle;
            T oldMemory = buf.memory;
            int oldOffset = buf.offset;
            int oldMaxLength = buf.maxLength;
            int readerIndex = buf.readerIndex();
            int writerIndex = buf.writerIndex();
            this.allocate((PoolThreadCache)this.parent.threadCache.get(), buf, newCapacity);
            if(newCapacity > oldCapacity) {
               this.memoryCopy(oldMemory, oldOffset, buf.memory, buf.offset, oldCapacity);
            } else if(newCapacity < oldCapacity) {
               if(readerIndex < newCapacity) {
                  if(writerIndex > newCapacity) {
                     writerIndex = newCapacity;
                  }

                  this.memoryCopy(oldMemory, oldOffset + readerIndex, buf.memory, buf.offset + readerIndex, writerIndex - readerIndex);
               } else {
                  writerIndex = newCapacity;
                  readerIndex = newCapacity;
               }
            }

            buf.setIndex(readerIndex, writerIndex);
            if(freeOldMemory) {
               this.free(oldChunk, oldHandle, oldMaxLength);
            }

         }
      } else {
         throw new IllegalArgumentException("newCapacity: " + newCapacity);
      }
   }

   protected abstract PoolChunk newChunk(int var1, int var2, int var3, int var4);

   protected abstract PoolChunk newUnpooledChunk(int var1);

   protected abstract PooledByteBuf newByteBuf(int var1);

   protected abstract void memoryCopy(Object var1, int var2, Object var3, int var4, int var5);

   protected abstract void destroyChunk(PoolChunk var1);

   public synchronized String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("Chunk(s) at 0~25%:");
      buf.append(StringUtil.NEWLINE);
      buf.append(this.qInit);
      buf.append(StringUtil.NEWLINE);
      buf.append("Chunk(s) at 0~50%:");
      buf.append(StringUtil.NEWLINE);
      buf.append(this.q000);
      buf.append(StringUtil.NEWLINE);
      buf.append("Chunk(s) at 25~75%:");
      buf.append(StringUtil.NEWLINE);
      buf.append(this.q025);
      buf.append(StringUtil.NEWLINE);
      buf.append("Chunk(s) at 50~100%:");
      buf.append(StringUtil.NEWLINE);
      buf.append(this.q050);
      buf.append(StringUtil.NEWLINE);
      buf.append("Chunk(s) at 75~100%:");
      buf.append(StringUtil.NEWLINE);
      buf.append(this.q075);
      buf.append(StringUtil.NEWLINE);
      buf.append("Chunk(s) at 100%:");
      buf.append(StringUtil.NEWLINE);
      buf.append(this.q100);
      buf.append(StringUtil.NEWLINE);
      buf.append("tiny subpages:");

      for(int i = 1; i < this.tinySubpagePools.length; ++i) {
         PoolSubpage<T> head = this.tinySubpagePools[i];
         if(head.next != head) {
            buf.append(StringUtil.NEWLINE);
            buf.append(i);
            buf.append(": ");
            PoolSubpage<T> s = head.next;

            while(true) {
               buf.append(s);
               s = s.next;
               if(s == head) {
                  break;
               }
            }
         }
      }

      buf.append(StringUtil.NEWLINE);
      buf.append("small subpages:");

      for(int i = 1; i < this.smallSubpagePools.length; ++i) {
         PoolSubpage<T> head = this.smallSubpagePools[i];
         if(head.next != head) {
            buf.append(StringUtil.NEWLINE);
            buf.append(i);
            buf.append(": ");
            PoolSubpage<T> s = head.next;

            while(true) {
               buf.append(s);
               s = s.next;
               if(s == head) {
                  break;
               }
            }
         }
      }

      buf.append(StringUtil.NEWLINE);
      return buf.toString();
   }

   static final class DirectArena extends PoolArena {
      private static final boolean HAS_UNSAFE = PlatformDependent.hasUnsafe();

      DirectArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize) {
         super(parent, pageSize, maxOrder, pageShifts, chunkSize);
      }

      boolean isDirect() {
         return true;
      }

      protected PoolChunk newChunk(int pageSize, int maxOrder, int pageShifts, int chunkSize) {
         return new PoolChunk(this, ByteBuffer.allocateDirect(chunkSize), pageSize, maxOrder, pageShifts, chunkSize);
      }

      protected PoolChunk newUnpooledChunk(int capacity) {
         return new PoolChunk(this, ByteBuffer.allocateDirect(capacity), capacity);
      }

      protected void destroyChunk(PoolChunk chunk) {
         PlatformDependent.freeDirectBuffer((ByteBuffer)chunk.memory);
      }

      protected PooledByteBuf newByteBuf(int maxCapacity) {
         return (PooledByteBuf)(HAS_UNSAFE?PooledUnsafeDirectByteBuf.newInstance(maxCapacity):PooledDirectByteBuf.newInstance(maxCapacity));
      }

      protected void memoryCopy(ByteBuffer src, int srcOffset, ByteBuffer dst, int dstOffset, int length) {
         if(length != 0) {
            if(HAS_UNSAFE) {
               PlatformDependent.copyMemory(PlatformDependent.directBufferAddress(src) + (long)srcOffset, PlatformDependent.directBufferAddress(dst) + (long)dstOffset, (long)length);
            } else {
               src = src.duplicate();
               dst = dst.duplicate();
               src.position(srcOffset).limit(srcOffset + length);
               dst.position(dstOffset);
               dst.put(src);
            }

         }
      }
   }

   static final class HeapArena extends PoolArena {
      HeapArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize) {
         super(parent, pageSize, maxOrder, pageShifts, chunkSize);
      }

      boolean isDirect() {
         return false;
      }

      protected PoolChunk newChunk(int pageSize, int maxOrder, int pageShifts, int chunkSize) {
         return new PoolChunk(this, new byte[chunkSize], pageSize, maxOrder, pageShifts, chunkSize);
      }

      protected PoolChunk newUnpooledChunk(int capacity) {
         return new PoolChunk(this, new byte[capacity], capacity);
      }

      protected void destroyChunk(PoolChunk chunk) {
      }

      protected PooledByteBuf newByteBuf(int maxCapacity) {
         return PooledHeapByteBuf.newInstance(maxCapacity);
      }

      protected void memoryCopy(byte[] src, int srcOffset, byte[] dst, int dstOffset, int length) {
         if(length != 0) {
            System.arraycopy(src, srcOffset, dst, dstOffset, length);
         }
      }
   }
}
