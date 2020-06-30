package io.netty.channel.epoll;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.Native;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

final class EpollEventLoop extends SingleThreadEventLoop {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollEventLoop.class);
   private static final AtomicIntegerFieldUpdater WAKEN_UP_UPDATER;
   private final int epollFd;
   private final int eventFd;
   private final IntObjectMap ids = new IntObjectHashMap();
   private final long[] events;
   private int id;
   private boolean overflown;
   private volatile int wakenUp;
   private volatile int ioRatio = 50;

   EpollEventLoop(EventLoopGroup parent, ThreadFactory threadFactory, int maxEvents) {
      super(parent, threadFactory, false);
      this.events = new long[maxEvents];
      boolean success = false;
      int epollFd = -1;
      int eventFd = -1;

      try {
         this.epollFd = epollFd = Native.epollCreate();
         this.eventFd = eventFd = Native.eventFd();
         Native.epollCtlAdd(epollFd, eventFd, 1, 0);
         success = true;
      } finally {
         if(!success) {
            if(epollFd != -1) {
               try {
                  Native.close(epollFd);
               } catch (Exception var16) {
                  ;
               }
            }

            if(eventFd != -1) {
               try {
                  Native.close(eventFd);
               } catch (Exception var15) {
                  ;
               }
            }
         }

      }

   }

   private int nextId() {
      int id = this.id;
      if(id == Integer.MAX_VALUE) {
         this.overflown = true;
         id = 0;
      }

      if(this.overflown) {
         while(true) {
            ++id;
            if(!this.ids.containsKey(id)) {
               this.id = id;
               break;
            }
         }
      } else {
         ++id;
         this.id = id;
      }

      return id;
   }

   protected void wakeup(boolean inEventLoop) {
      if(!inEventLoop && WAKEN_UP_UPDATER.compareAndSet(this, 0, 1)) {
         Native.eventFdWrite(this.eventFd, 1L);
      }

   }

   void add(AbstractEpollChannel ch) {
      assert this.inEventLoop();

      int id = this.nextId();
      Native.epollCtlAdd(this.epollFd, ch.fd, ch.flags, id);
      ch.id = id;
      this.ids.put(id, ch);
   }

   void modify(AbstractEpollChannel ch) {
      assert this.inEventLoop();

      Native.epollCtlMod(this.epollFd, ch.fd, ch.flags, ch.id);
   }

   void remove(AbstractEpollChannel ch) {
      assert this.inEventLoop();

      if(this.ids.remove(ch.id) != null && ch.isOpen()) {
         Native.epollCtlDel(this.epollFd, ch.fd);
      }

   }

   protected Queue newTaskQueue() {
      return PlatformDependent.newMpscQueue();
   }

   public int getIoRatio() {
      return this.ioRatio;
   }

   public void setIoRatio(int ioRatio) {
      if(ioRatio > 0 && ioRatio <= 100) {
         this.ioRatio = ioRatio;
      } else {
         throw new IllegalArgumentException("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)");
      }
   }

   private int epollWait(boolean oldWakenUp) {
      int selectCnt = 0;
      long currentTimeNanos = System.nanoTime();
      long selectDeadLineNanos = currentTimeNanos + this.delayNanos(currentTimeNanos);

      while(true) {
         long timeoutMillis = (selectDeadLineNanos - currentTimeNanos + 500000L) / 1000000L;
         if(timeoutMillis <= 0L) {
            if(selectCnt == 0) {
               int ready = Native.epollWait(this.epollFd, this.events, 0);
               if(ready > 0) {
                  return ready;
               }
            }

            return 0;
         }

         int selectedKeys = Native.epollWait(this.epollFd, this.events, (int)timeoutMillis);
         ++selectCnt;
         if(selectedKeys != 0 || oldWakenUp || this.wakenUp == 1 || this.hasTasks() || this.hasScheduledTasks()) {
            return selectedKeys;
         }

         currentTimeNanos = System.nanoTime();
      }
   }

   protected void run() {
      while(true) {
         boolean oldWakenUp = WAKEN_UP_UPDATER.getAndSet(this, 0) == 1;

         try {
            int ready;
            if(this.hasTasks()) {
               ready = Native.epollWait(this.epollFd, this.events, 0);
            } else {
               ready = this.epollWait(oldWakenUp);
               if(this.wakenUp == 1) {
                  Native.eventFdWrite(this.eventFd, 1L);
               }
            }

            int ioRatio = this.ioRatio;
            if(ioRatio == 100) {
               if(ready > 0) {
                  this.processReady(this.events, ready);
               }

               this.runAllTasks();
            } else {
               long ioStartTime = System.nanoTime();
               if(ready > 0) {
                  this.processReady(this.events, ready);
               }

               long ioTime = System.nanoTime() - ioStartTime;
               this.runAllTasks(ioTime * (long)(100 - ioRatio) / (long)ioRatio);
            }

            if(this.isShuttingDown()) {
               this.closeAll();
               if(this.confirmShutdown()) {
                  return;
               }
            }
         } catch (Throwable var9) {
            logger.warn("Unexpected exception in the selector loop.", var9);

            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var8) {
               ;
            }
         }
      }
   }

   private void closeAll() {
      Native.epollWait(this.epollFd, this.events, 0);
      Collection<AbstractEpollChannel> channels = new ArrayList(this.ids.size());

      for(IntObjectMap.Entry<AbstractEpollChannel> entry : this.ids.entries()) {
         channels.add(entry.value());
      }

      for(AbstractEpollChannel ch : channels) {
         ch.unsafe().close(ch.unsafe().voidPromise());
      }

   }

   private void processReady(long[] events, int ready) {
      for(int i = 0; i < ready; ++i) {
         long ev = events[i];
         int id = (int)(ev >> 32);
         if(id == 0) {
            Native.eventFdRead(this.eventFd);
         } else {
            boolean read = (ev & 1L) != 0L;
            boolean write = (ev & 2L) != 0L;
            boolean close = (ev & 8L) != 0L;
            AbstractEpollChannel ch = (AbstractEpollChannel)this.ids.get(id);
            if(ch != null) {
               AbstractEpollChannel.AbstractEpollUnsafe unsafe = (AbstractEpollChannel.AbstractEpollUnsafe)ch.unsafe();
               if(write && ch.isOpen()) {
                  unsafe.epollOutReady();
               }

               if(read && ch.isOpen()) {
                  unsafe.epollInReady();
               }

               if(close && ch.isOpen()) {
                  unsafe.epollRdHupReady();
               }
            }
         }
      }

   }

   protected void cleanup() {
      try {
         Native.close(this.epollFd);
      } catch (IOException var3) {
         logger.warn("Failed to close the epoll fd.", (Throwable)var3);
      }

      try {
         Native.close(this.eventFd);
      } catch (IOException var2) {
         logger.warn("Failed to close the event fd.", (Throwable)var2);
      }

   }

   static {
      AtomicIntegerFieldUpdater<EpollEventLoop> updater = PlatformDependent.newAtomicIntegerFieldUpdater(EpollEventLoop.class, "wakenUp");
      if(updater == null) {
         updater = AtomicIntegerFieldUpdater.newUpdater(EpollEventLoop.class, "wakenUp");
      }

      WAKEN_UP_UPDATER = updater;
   }
}
