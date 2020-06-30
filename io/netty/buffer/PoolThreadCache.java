package io.netty.buffer;

import io.netty.buffer.PoolArena;
import io.netty.buffer.PoolChunk;
import io.netty.buffer.PooledByteBuf;
import io.netty.util.ThreadDeathWatcher;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

final class PoolThreadCache {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PoolThreadCache.class);
   final PoolArena heapArena;
   final PoolArena directArena;
   private final PoolThreadCache.MemoryRegionCache[] tinySubPageHeapCaches;
   private final PoolThreadCache.MemoryRegionCache[] smallSubPageHeapCaches;
   private final PoolThreadCache.MemoryRegionCache[] tinySubPageDirectCaches;
   private final PoolThreadCache.MemoryRegionCache[] smallSubPageDirectCaches;
   private final PoolThreadCache.MemoryRegionCache[] normalHeapCaches;
   private final PoolThreadCache.MemoryRegionCache[] normalDirectCaches;
   private final int numShiftsNormalDirect;
   private final int numShiftsNormalHeap;
   private final int freeSweepAllocationThreshold;
   private int allocations;
   private final Thread thread = Thread.currentThread();
   private final Runnable freeTask = new Runnable() {
      public void run() {
         PoolThreadCache.this.free0();
      }
   };

   PoolThreadCache(PoolArena heapArena, PoolArena directArena, int tinyCacheSize, int smallCacheSize, int normalCacheSize, int maxCachedBufferCapacity, int freeSweepAllocationThreshold) {
      if(maxCachedBufferCapacity < 0) {
         throw new IllegalArgumentException("maxCachedBufferCapacity: " + maxCachedBufferCapacity + " (expected: >= 0)");
      } else if(freeSweepAllocationThreshold < 1) {
         throw new IllegalArgumentException("freeSweepAllocationThreshold: " + maxCachedBufferCapacity + " (expected: > 0)");
      } else {
         this.freeSweepAllocationThreshold = freeSweepAllocationThreshold;
         this.heapArena = heapArena;
         this.directArena = directArena;
         if(directArena != null) {
            this.tinySubPageDirectCaches = createSubPageCaches(tinyCacheSize, 32);
            this.smallSubPageDirectCaches = createSubPageCaches(smallCacheSize, directArena.numSmallSubpagePools);
            this.numShiftsNormalDirect = log2(directArena.pageSize);
            this.normalDirectCaches = createNormalCaches(normalCacheSize, maxCachedBufferCapacity, directArena);
         } else {
            this.tinySubPageDirectCaches = null;
            this.smallSubPageDirectCaches = null;
            this.normalDirectCaches = null;
            this.numShiftsNormalDirect = -1;
         }

         if(heapArena != null) {
            this.tinySubPageHeapCaches = createSubPageCaches(tinyCacheSize, 32);
            this.smallSubPageHeapCaches = createSubPageCaches(smallCacheSize, heapArena.numSmallSubpagePools);
            this.numShiftsNormalHeap = log2(heapArena.pageSize);
            this.normalHeapCaches = createNormalCaches(normalCacheSize, maxCachedBufferCapacity, heapArena);
         } else {
            this.tinySubPageHeapCaches = null;
            this.smallSubPageHeapCaches = null;
            this.normalHeapCaches = null;
            this.numShiftsNormalHeap = -1;
         }

         ThreadDeathWatcher.watch(this.thread, this.freeTask);
      }
   }

   private static PoolThreadCache.SubPageMemoryRegionCache[] createSubPageCaches(int cacheSize, int numCaches) {
      if(cacheSize <= 0) {
         return null;
      } else {
         PoolThreadCache.SubPageMemoryRegionCache<T>[] cache = new PoolThreadCache.SubPageMemoryRegionCache[numCaches];

         for(int i = 0; i < cache.length; ++i) {
            cache[i] = new PoolThreadCache.SubPageMemoryRegionCache(cacheSize);
         }

         return cache;
      }
   }

   private static PoolThreadCache.NormalMemoryRegionCache[] createNormalCaches(int cacheSize, int maxCachedBufferCapacity, PoolArena area) {
      if(cacheSize <= 0) {
         return null;
      } else {
         int max = Math.min(area.chunkSize, maxCachedBufferCapacity);
         int arraySize = Math.max(1, max / area.pageSize);
         PoolThreadCache.NormalMemoryRegionCache<T>[] cache = new PoolThreadCache.NormalMemoryRegionCache[arraySize];

         for(int i = 0; i < cache.length; ++i) {
            cache[i] = new PoolThreadCache.NormalMemoryRegionCache(cacheSize);
         }

         return cache;
      }
   }

   private static int log2(int val) {
      int res;
      for(res = 0; val > 1; ++res) {
         val >>= 1;
      }

      return res;
   }

   boolean allocateTiny(PoolArena area, PooledByteBuf buf, int reqCapacity, int normCapacity) {
      return this.allocate(this.cacheForTiny(area, normCapacity), buf, reqCapacity);
   }

   boolean allocateSmall(PoolArena area, PooledByteBuf buf, int reqCapacity, int normCapacity) {
      return this.allocate(this.cacheForSmall(area, normCapacity), buf, reqCapacity);
   }

   boolean allocateNormal(PoolArena area, PooledByteBuf buf, int reqCapacity, int normCapacity) {
      return this.allocate(this.cacheForNormal(area, normCapacity), buf, reqCapacity);
   }

   private boolean allocate(PoolThreadCache.MemoryRegionCache cache, PooledByteBuf buf, int reqCapacity) {
      if(cache == null) {
         return false;
      } else {
         boolean allocated = cache.allocate(buf, reqCapacity);
         if(++this.allocations >= this.freeSweepAllocationThreshold) {
            this.allocations = 0;
            this.trim();
         }

         return allocated;
      }
   }

   boolean add(PoolArena area, PoolChunk chunk, long handle, int normCapacity) {
      PoolThreadCache.MemoryRegionCache<?> cache;
      if(area.isTinyOrSmall(normCapacity)) {
         if(PoolArena.isTiny(normCapacity)) {
            cache = this.cacheForTiny(area, normCapacity);
         } else {
            cache = this.cacheForSmall(area, normCapacity);
         }
      } else {
         cache = this.cacheForNormal(area, normCapacity);
      }

      return cache == null?false:cache.add(chunk, handle);
   }

   void free() {
      ThreadDeathWatcher.unwatch(this.thread, this.freeTask);
      this.free0();
   }

   private void free0() {
      int numFreed = free(this.tinySubPageDirectCaches) + free(this.smallSubPageDirectCaches) + free(this.normalDirectCaches) + free(this.tinySubPageHeapCaches) + free(this.smallSubPageHeapCaches) + free(this.normalHeapCaches);
      if(numFreed > 0 && logger.isDebugEnabled()) {
         logger.debug("Freed {} thread-local buffer(s) from thread: {}", Integer.valueOf(numFreed), this.thread.getName());
      }

   }

   private static int free(PoolThreadCache.MemoryRegionCache[] caches) {
      if(caches == null) {
         return 0;
      } else {
         int numFreed = 0;

         for(PoolThreadCache.MemoryRegionCache<?> c : caches) {
            numFreed += free(c);
         }

         return numFreed;
      }
   }

   private static int free(PoolThreadCache.MemoryRegionCache cache) {
      return cache == null?0:cache.free();
   }

   void trim() {
      trim(this.tinySubPageDirectCaches);
      trim(this.smallSubPageDirectCaches);
      trim(this.normalDirectCaches);
      trim(this.tinySubPageHeapCaches);
      trim(this.smallSubPageHeapCaches);
      trim(this.normalHeapCaches);
   }

   private static void trim(PoolThreadCache.MemoryRegionCache[] caches) {
      if(caches != null) {
         for(PoolThreadCache.MemoryRegionCache<?> c : caches) {
            trim(c);
         }

      }
   }

   private static void trim(PoolThreadCache.MemoryRegionCache cache) {
      if(cache != null) {
         cache.trim();
      }
   }

   private PoolThreadCache.MemoryRegionCache cacheForTiny(PoolArena area, int normCapacity) {
      int idx = PoolArena.tinyIdx(normCapacity);
      return area.isDirect()?cache(this.tinySubPageDirectCaches, idx):cache(this.tinySubPageHeapCaches, idx);
   }

   private PoolThreadCache.MemoryRegionCache cacheForSmall(PoolArena area, int normCapacity) {
      int idx = PoolArena.smallIdx(normCapacity);
      return area.isDirect()?cache(this.smallSubPageDirectCaches, idx):cache(this.smallSubPageHeapCaches, idx);
   }

   private PoolThreadCache.MemoryRegionCache cacheForNormal(PoolArena area, int normCapacity) {
      if(area.isDirect()) {
         int idx = log2(normCapacity >> this.numShiftsNormalDirect);
         return cache(this.normalDirectCaches, idx);
      } else {
         int idx = log2(normCapacity >> this.numShiftsNormalHeap);
         return cache(this.normalHeapCaches, idx);
      }
   }

   private static PoolThreadCache.MemoryRegionCache cache(PoolThreadCache.MemoryRegionCache[] cache, int idx) {
      return cache != null && idx <= cache.length - 1?cache[idx]:null;
   }

   private abstract static class MemoryRegionCache {
      private final PoolThreadCache.MemoryRegionCache.Entry[] entries;
      private final int maxUnusedCached;
      private int head;
      private int tail;
      private int maxEntriesInUse;
      private int entriesInUse;

      MemoryRegionCache(int size) {
         this.entries = new PoolThreadCache.MemoryRegionCache.Entry[powerOfTwo(size)];

         for(int i = 0; i < this.entries.length; ++i) {
            this.entries[i] = new PoolThreadCache.MemoryRegionCache.Entry();
         }

         this.maxUnusedCached = size / 2;
      }

      private static int powerOfTwo(int res) {
         if(res <= 2) {
            return 2;
         } else {
            --res;
            res = res | res >> 1;
            res = res | res >> 2;
            res = res | res >> 4;
            res = res | res >> 8;
            res = res | res >> 16;
            ++res;
            return res;
         }
      }

      protected abstract void initBuf(PoolChunk var1, long var2, PooledByteBuf var4, int var5);

      public boolean add(PoolChunk chunk, long handle) {
         PoolThreadCache.MemoryRegionCache.Entry<T> entry = this.entries[this.tail];
         if(entry.chunk != null) {
            return false;
         } else {
            --this.entriesInUse;
            entry.chunk = chunk;
            entry.handle = handle;
            this.tail = this.nextIdx(this.tail);
            return true;
         }
      }

      public boolean allocate(PooledByteBuf buf, int reqCapacity) {
         PoolThreadCache.MemoryRegionCache.Entry<T> entry = this.entries[this.head];
         if(entry.chunk == null) {
            return false;
         } else {
            ++this.entriesInUse;
            if(this.maxEntriesInUse < this.entriesInUse) {
               this.maxEntriesInUse = this.entriesInUse;
            }

            this.initBuf(entry.chunk, entry.handle, buf, reqCapacity);
            entry.chunk = null;
            this.head = this.nextIdx(this.head);
            return true;
         }
      }

      public int free() {
         int numFreed = 0;
         this.entriesInUse = 0;
         this.maxEntriesInUse = 0;

         for(int i = this.head; freeEntry(this.entries[i]); i = this.nextIdx(i)) {
            ++numFreed;
         }

         return numFreed;
      }

      private void trim() {
         int free = this.size() - this.maxEntriesInUse;
         this.entriesInUse = 0;
         this.maxEntriesInUse = 0;
         if(free > this.maxUnusedCached) {
            for(int i = this.head; free > 0; --free) {
               if(!freeEntry(this.entries[i])) {
                  return;
               }

               i = this.nextIdx(i);
            }

         }
      }

      private static boolean freeEntry(PoolThreadCache.MemoryRegionCache.Entry entry) {
         PoolChunk chunk = entry.chunk;
         if(chunk == null) {
            return false;
         } else {
            synchronized(chunk.arena) {
               chunk.parent.free(chunk, entry.handle);
            }

            entry.chunk = null;
            return true;
         }
      }

      private int size() {
         return this.tail - this.head & this.entries.length - 1;
      }

      private int nextIdx(int index) {
         return index + 1 & this.entries.length - 1;
      }

      private static final class Entry {
         PoolChunk chunk;
         long handle;

         private Entry() {
         }
      }
   }

   private static final class NormalMemoryRegionCache extends PoolThreadCache.MemoryRegionCache {
      NormalMemoryRegionCache(int size) {
         super(size);
      }

      protected void initBuf(PoolChunk chunk, long handle, PooledByteBuf buf, int reqCapacity) {
         chunk.initBuf(buf, handle, reqCapacity);
      }
   }

   private static final class SubPageMemoryRegionCache extends PoolThreadCache.MemoryRegionCache {
      SubPageMemoryRegionCache(int size) {
         super(size);
      }

      protected void initBuf(PoolChunk chunk, long handle, PooledByteBuf buf, int reqCapacity) {
         chunk.initBufWithSubpage(buf, handle, reqCapacity);
      }
   }
}
