package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.FileRegion;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public final class ChannelOutboundBuffer {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelOutboundBuffer.class);
   private static final FastThreadLocal NIO_BUFFERS = new FastThreadLocal() {
      protected ByteBuffer[] initialValue() throws Exception {
         return new ByteBuffer[1024];
      }
   };
   private final Channel channel;
   private ChannelOutboundBuffer.Entry flushedEntry;
   private ChannelOutboundBuffer.Entry unflushedEntry;
   private ChannelOutboundBuffer.Entry tailEntry;
   private int flushed;
   private int nioBufferCount;
   private long nioBufferSize;
   private boolean inFail;
   private static final AtomicLongFieldUpdater TOTAL_PENDING_SIZE_UPDATER;
   private volatile long totalPendingSize;
   private static final AtomicIntegerFieldUpdater WRITABLE_UPDATER;
   private volatile int writable = 1;

   ChannelOutboundBuffer(AbstractChannel channel) {
      this.channel = channel;
   }

   public void addMessage(Object msg, int size, ChannelPromise promise) {
      ChannelOutboundBuffer.Entry entry = ChannelOutboundBuffer.Entry.newInstance(msg, size, total(msg), promise);
      if(this.tailEntry == null) {
         this.flushedEntry = null;
         this.tailEntry = entry;
      } else {
         ChannelOutboundBuffer.Entry tail = this.tailEntry;
         tail.next = entry;
         this.tailEntry = entry;
      }

      if(this.unflushedEntry == null) {
         this.unflushedEntry = entry;
      }

      this.incrementPendingOutboundBytes((long)size);
   }

   public void addFlush() {
      ChannelOutboundBuffer.Entry entry = this.unflushedEntry;
      if(entry != null) {
         if(this.flushedEntry == null) {
            this.flushedEntry = entry;
         }

         while(true) {
            ++this.flushed;
            if(!entry.promise.setUncancellable()) {
               int pending = entry.cancel();
               this.decrementPendingOutboundBytes((long)pending);
            }

            entry = entry.next;
            if(entry == null) {
               break;
            }
         }

         this.unflushedEntry = null;
      }

   }

   void incrementPendingOutboundBytes(long size) {
      if(size != 0L) {
         long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, size);
         if(newWriteBufferSize > (long)this.channel.config().getWriteBufferHighWaterMark() && WRITABLE_UPDATER.compareAndSet(this, 1, 0)) {
            this.channel.pipeline().fireChannelWritabilityChanged();
         }

      }
   }

   void decrementPendingOutboundBytes(long size) {
      if(size != 0L) {
         long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -size);
         if((newWriteBufferSize == 0L || newWriteBufferSize < (long)this.channel.config().getWriteBufferLowWaterMark()) && WRITABLE_UPDATER.compareAndSet(this, 0, 1)) {
            this.channel.pipeline().fireChannelWritabilityChanged();
         }

      }
   }

   private static long total(Object msg) {
      return msg instanceof ByteBuf?(long)((ByteBuf)msg).readableBytes():(msg instanceof FileRegion?((FileRegion)msg).count():(msg instanceof ByteBufHolder?(long)((ByteBufHolder)msg).content().readableBytes():-1L));
   }

   public Object current() {
      ChannelOutboundBuffer.Entry entry = this.flushedEntry;
      return entry == null?null:entry.msg;
   }

   public void progress(long amount) {
      ChannelOutboundBuffer.Entry e = this.flushedEntry;

      assert e != null;

      ChannelPromise p = e.promise;
      if(p instanceof ChannelProgressivePromise) {
         long progress = e.progress + amount;
         e.progress = progress;
         ((ChannelProgressivePromise)p).tryProgress(progress, e.total);
      }

   }

   public boolean remove() {
      ChannelOutboundBuffer.Entry e = this.flushedEntry;
      if(e == null) {
         return false;
      } else {
         Object msg = e.msg;
         ChannelPromise promise = e.promise;
         int size = e.pendingSize;
         this.removeEntry(e);
         if(!e.cancelled) {
            ReferenceCountUtil.safeRelease(msg);
            safeSuccess(promise);
            this.decrementPendingOutboundBytes((long)size);
         }

         e.recycle();
         return true;
      }
   }

   public boolean remove(Throwable cause) {
      ChannelOutboundBuffer.Entry e = this.flushedEntry;
      if(e == null) {
         return false;
      } else {
         Object msg = e.msg;
         ChannelPromise promise = e.promise;
         int size = e.pendingSize;
         this.removeEntry(e);
         if(!e.cancelled) {
            ReferenceCountUtil.safeRelease(msg);
            safeFail(promise, cause);
            this.decrementPendingOutboundBytes((long)size);
         }

         e.recycle();
         return true;
      }
   }

   private void removeEntry(ChannelOutboundBuffer.Entry e) {
      if(--this.flushed == 0) {
         this.flushedEntry = null;
         if(e == this.tailEntry) {
            this.tailEntry = null;
            this.unflushedEntry = null;
         }
      } else {
         this.flushedEntry = e.next;
      }

   }

   public void removeBytes(long writtenBytes) {
      while(true) {
         Object msg = this.current();
         if(!(msg instanceof ByteBuf)) {
            assert writtenBytes == 0L;
         } else {
            ByteBuf buf = (ByteBuf)msg;
            int readerIndex = buf.readerIndex();
            int readableBytes = buf.writerIndex() - readerIndex;
            if((long)readableBytes <= writtenBytes) {
               if(writtenBytes != 0L) {
                  this.progress((long)readableBytes);
                  writtenBytes -= (long)readableBytes;
               }

               this.remove();
               continue;
            }

            if(writtenBytes != 0L) {
               buf.readerIndex(readerIndex + (int)writtenBytes);
               this.progress(writtenBytes);
            }
         }

         return;
      }
   }

   public ByteBuffer[] nioBuffers() {
      long nioBufferSize = 0L;
      int nioBufferCount = 0;
      InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
      ByteBuffer[] nioBuffers = (ByteBuffer[])NIO_BUFFERS.get(threadLocalMap);

      for(ChannelOutboundBuffer.Entry entry = this.flushedEntry; this.isFlushedEntry(entry) && entry.msg instanceof ByteBuf; entry = entry.next) {
         if(!entry.cancelled) {
            ByteBuf buf = (ByteBuf)entry.msg;
            int readerIndex = buf.readerIndex();
            int readableBytes = buf.writerIndex() - readerIndex;
            if(readableBytes > 0) {
               nioBufferSize += (long)readableBytes;
               int count = entry.count;
               if(count == -1) {
                  entry.count = count = buf.nioBufferCount();
               }

               int neededSpace = nioBufferCount + count;
               if(neededSpace > nioBuffers.length) {
                  nioBuffers = expandNioBufferArray(nioBuffers, neededSpace, nioBufferCount);
                  NIO_BUFFERS.set(threadLocalMap, nioBuffers);
               }

               if(count == 1) {
                  ByteBuffer nioBuf = entry.buf;
                  if(nioBuf == null) {
                     entry.buf = nioBuf = buf.internalNioBuffer(readerIndex, readableBytes);
                  }

                  nioBuffers[nioBufferCount++] = nioBuf;
               } else {
                  ByteBuffer[] nioBufs = entry.bufs;
                  if(nioBufs == null) {
                     entry.bufs = nioBufs = buf.nioBuffers();
                  }

                  nioBufferCount = fillBufferArray(nioBufs, nioBuffers, nioBufferCount);
               }
            }
         }
      }

      this.nioBufferCount = nioBufferCount;
      this.nioBufferSize = nioBufferSize;
      return nioBuffers;
   }

   private static int fillBufferArray(ByteBuffer[] nioBufs, ByteBuffer[] nioBuffers, int nioBufferCount) {
      for(ByteBuffer nioBuf : nioBufs) {
         if(nioBuf == null) {
            break;
         }

         nioBuffers[nioBufferCount++] = nioBuf;
      }

      return nioBufferCount;
   }

   private static ByteBuffer[] expandNioBufferArray(ByteBuffer[] array, int neededSpace, int size) {
      int newCapacity = array.length;

      while(true) {
         newCapacity <<= 1;
         if(newCapacity < 0) {
            throw new IllegalStateException();
         }

         if(neededSpace <= newCapacity) {
            break;
         }
      }

      ByteBuffer[] newArray = new ByteBuffer[newCapacity];
      System.arraycopy(array, 0, newArray, 0, size);
      return newArray;
   }

   public int nioBufferCount() {
      return this.nioBufferCount;
   }

   public long nioBufferSize() {
      return this.nioBufferSize;
   }

   boolean isWritable() {
      return this.writable != 0;
   }

   public int size() {
      return this.flushed;
   }

   public boolean isEmpty() {
      return this.flushed == 0;
   }

   void failFlushed(Throwable cause) {
      if(!this.inFail) {
         try {
            this.inFail = true;

            while(this.remove(cause)) {
               ;
            }
         } finally {
            this.inFail = false;
         }

      }
   }

   void close(final ClosedChannelException cause) {
      if(this.inFail) {
         this.channel.eventLoop().execute(new Runnable() {
            public void run() {
               ChannelOutboundBuffer.this.close(cause);
            }
         });
      } else {
         this.inFail = true;
         if(this.channel.isOpen()) {
            throw new IllegalStateException("close() must be invoked after the channel is closed.");
         } else if(!this.isEmpty()) {
            throw new IllegalStateException("close() must be invoked after all flushed writes are handled.");
         } else {
            try {
               for(ChannelOutboundBuffer.Entry e = this.unflushedEntry; e != null; e = e.recycleAndGetNext()) {
                  int size = e.pendingSize;
                  TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, (long)(-size));
                  if(!e.cancelled) {
                     ReferenceCountUtil.safeRelease(e.msg);
                     safeFail(e.promise, cause);
                  }
               }
            } finally {
               this.inFail = false;
            }

         }
      }
   }

   private static void safeSuccess(ChannelPromise promise) {
      if(!(promise instanceof VoidChannelPromise) && !promise.trySuccess()) {
         logger.warn("Failed to mark a promise as success because it is done already: {}", (Object)promise);
      }

   }

   private static void safeFail(ChannelPromise promise, Throwable cause) {
      if(!(promise instanceof VoidChannelPromise) && !promise.tryFailure(cause)) {
         logger.warn("Failed to mark a promise as failure because it\'s done already: {}", promise, cause);
      }

   }

   /** @deprecated */
   @Deprecated
   public void recycle() {
   }

   public long totalPendingWriteBytes() {
      return this.totalPendingSize;
   }

   public void forEachFlushedMessage(ChannelOutboundBuffer.MessageProcessor processor) throws Exception {
      if(processor == null) {
         throw new NullPointerException("processor");
      } else {
         ChannelOutboundBuffer.Entry entry = this.flushedEntry;
         if(entry != null) {
            while(entry.cancelled || processor.processMessage(entry.msg)) {
               entry = entry.next;
               if(!this.isFlushedEntry(entry)) {
                  return;
               }
            }

         }
      }
   }

   private boolean isFlushedEntry(ChannelOutboundBuffer.Entry e) {
      return e != null && e != this.unflushedEntry;
   }

   static {
      AtomicIntegerFieldUpdater<ChannelOutboundBuffer> writableUpdater = PlatformDependent.newAtomicIntegerFieldUpdater(ChannelOutboundBuffer.class, "writable");
      if(writableUpdater == null) {
         writableUpdater = AtomicIntegerFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "writable");
      }

      WRITABLE_UPDATER = writableUpdater;
      AtomicLongFieldUpdater<ChannelOutboundBuffer> pendingSizeUpdater = PlatformDependent.newAtomicLongFieldUpdater(ChannelOutboundBuffer.class, "totalPendingSize");
      if(pendingSizeUpdater == null) {
         pendingSizeUpdater = AtomicLongFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "totalPendingSize");
      }

      TOTAL_PENDING_SIZE_UPDATER = pendingSizeUpdater;
   }

   static final class Entry {
      private static final Recycler RECYCLER = new Recycler() {
         protected ChannelOutboundBuffer.Entry newObject(Recycler.Handle handle) {
            return new ChannelOutboundBuffer.Entry(handle);
         }
      };
      private final Recycler.Handle handle;
      ChannelOutboundBuffer.Entry next;
      Object msg;
      ByteBuffer[] bufs;
      ByteBuffer buf;
      ChannelPromise promise;
      long progress;
      long total;
      int pendingSize;
      int count;
      boolean cancelled;

      private Entry(Recycler.Handle handle) {
         this.count = -1;
         this.handle = handle;
      }

      static ChannelOutboundBuffer.Entry newInstance(Object msg, int size, long total, ChannelPromise promise) {
         ChannelOutboundBuffer.Entry entry = (ChannelOutboundBuffer.Entry)RECYCLER.get();
         entry.msg = msg;
         entry.pendingSize = size;
         entry.total = total;
         entry.promise = promise;
         return entry;
      }

      int cancel() {
         if(!this.cancelled) {
            this.cancelled = true;
            int pSize = this.pendingSize;
            ReferenceCountUtil.safeRelease(this.msg);
            this.msg = Unpooled.EMPTY_BUFFER;
            this.pendingSize = 0;
            this.total = 0L;
            this.progress = 0L;
            this.bufs = null;
            this.buf = null;
            return pSize;
         } else {
            return 0;
         }
      }

      void recycle() {
         this.next = null;
         this.bufs = null;
         this.buf = null;
         this.msg = null;
         this.promise = null;
         this.progress = 0L;
         this.total = 0L;
         this.pendingSize = 0;
         this.count = -1;
         this.cancelled = false;
         RECYCLER.recycle(this, this.handle);
      }

      ChannelOutboundBuffer.Entry recycleAndGetNext() {
         ChannelOutboundBuffer.Entry next = this.next;
         this.recycle();
         return next;
      }
   }

   public interface MessageProcessor {
      boolean processMessage(Object var1) throws Exception;
   }
}
