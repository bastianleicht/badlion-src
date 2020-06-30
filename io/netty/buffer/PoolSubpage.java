package io.netty.buffer;

import io.netty.buffer.PoolChunk;

final class PoolSubpage {
   final PoolChunk chunk;
   private final int memoryMapIdx;
   private final int runOffset;
   private final int pageSize;
   private final long[] bitmap;
   PoolSubpage prev;
   PoolSubpage next;
   boolean doNotDestroy;
   int elemSize;
   private int maxNumElems;
   private int bitmapLength;
   private int nextAvail;
   private int numAvail;

   PoolSubpage(int pageSize) {
      this.chunk = null;
      this.memoryMapIdx = -1;
      this.runOffset = -1;
      this.elemSize = -1;
      this.pageSize = pageSize;
      this.bitmap = null;
   }

   PoolSubpage(PoolChunk chunk, int memoryMapIdx, int runOffset, int pageSize, int elemSize) {
      this.chunk = chunk;
      this.memoryMapIdx = memoryMapIdx;
      this.runOffset = runOffset;
      this.pageSize = pageSize;
      this.bitmap = new long[pageSize >>> 10];
      this.init(elemSize);
   }

   void init(int elemSize) {
      this.doNotDestroy = true;
      this.elemSize = elemSize;
      if(elemSize != 0) {
         this.maxNumElems = this.numAvail = this.pageSize / elemSize;
         this.nextAvail = 0;
         this.bitmapLength = this.maxNumElems >>> 6;
         if((this.maxNumElems & 63) != 0) {
            ++this.bitmapLength;
         }

         for(int i = 0; i < this.bitmapLength; ++i) {
            this.bitmap[i] = 0L;
         }
      }

      this.addToPool();
   }

   long allocate() {
      if(this.elemSize == 0) {
         return this.toHandle(0);
      } else if(this.numAvail != 0 && this.doNotDestroy) {
         int bitmapIdx = this.getNextAvail();
         int q = bitmapIdx >>> 6;
         int r = bitmapIdx & 63;

         assert (this.bitmap[q] >>> r & 1L) == 0L;

         this.bitmap[q] |= 1L << r;
         if(--this.numAvail == 0) {
            this.removeFromPool();
         }

         return this.toHandle(bitmapIdx);
      } else {
         return -1L;
      }
   }

   boolean free(int bitmapIdx) {
      if(this.elemSize == 0) {
         return true;
      } else {
         int q = bitmapIdx >>> 6;
         int r = bitmapIdx & 63;

         assert (this.bitmap[q] >>> r & 1L) != 0L;

         this.bitmap[q] ^= 1L << r;
         this.setNextAvail(bitmapIdx);
         if(this.numAvail++ == 0) {
            this.addToPool();
            return true;
         } else if(this.numAvail != this.maxNumElems) {
            return true;
         } else if(this.prev == this.next) {
            return true;
         } else {
            this.doNotDestroy = false;
            this.removeFromPool();
            return false;
         }
      }
   }

   private void addToPool() {
      PoolSubpage<T> head = this.chunk.arena.findSubpagePoolHead(this.elemSize);
      if($assertionsDisabled || this.prev == null && this.next == null) {
         this.prev = head;
         this.next = head.next;
         this.next.prev = this;
         head.next = this;
      } else {
         throw new AssertionError();
      }
   }

   private void removeFromPool() {
      if($assertionsDisabled || this.prev != null && this.next != null) {
         this.prev.next = this.next;
         this.next.prev = this.prev;
         this.next = null;
         this.prev = null;
      } else {
         throw new AssertionError();
      }
   }

   private void setNextAvail(int bitmapIdx) {
      this.nextAvail = bitmapIdx;
   }

   private int getNextAvail() {
      int nextAvail = this.nextAvail;
      if(nextAvail >= 0) {
         this.nextAvail = -1;
         return nextAvail;
      } else {
         return this.findNextAvail();
      }
   }

   private int findNextAvail() {
      long[] bitmap = this.bitmap;
      int bitmapLength = this.bitmapLength;

      for(int i = 0; i < bitmapLength; ++i) {
         long bits = bitmap[i];
         if(~bits != 0L) {
            return this.findNextAvail0(i, bits);
         }
      }

      return -1;
   }

   private int findNextAvail0(int i, long bits) {
      int maxNumElems = this.maxNumElems;
      int baseVal = i << 6;

      for(int j = 0; j < 64; ++j) {
         if((bits & 1L) == 0L) {
            int val = baseVal | j;
            if(val < maxNumElems) {
               return val;
            }
            break;
         }

         bits >>>= 1;
      }

      return -1;
   }

   private long toHandle(int bitmapIdx) {
      return 4611686018427387904L | (long)bitmapIdx << 32 | (long)this.memoryMapIdx;
   }

   public String toString() {
      return !this.doNotDestroy?"(" + this.memoryMapIdx + ": not in use)":String.valueOf('(') + this.memoryMapIdx + ": " + (this.maxNumElems - this.numAvail) + '/' + this.maxNumElems + ", offset: " + this.runOffset + ", length: " + this.pageSize + ", elemSize: " + this.elemSize + ')';
   }
}
