package io.netty.channel.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.EventLoopException;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.nio.NioTask;
import io.netty.channel.nio.SelectedSelectionKeySet;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class NioEventLoop extends SingleThreadEventLoop {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioEventLoop.class);
   private static final int CLEANUP_INTERVAL = 256;
   private static final boolean DISABLE_KEYSET_OPTIMIZATION = SystemPropertyUtil.getBoolean("io.netty.noKeySetOptimization", false);
   private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;
   private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;
   Selector selector;
   private SelectedSelectionKeySet selectedKeys;
   private final SelectorProvider provider;
   private final AtomicBoolean wakenUp = new AtomicBoolean();
   private volatile int ioRatio = 50;
   private int cancelledKeys;
   private boolean needsToSelectAgain;

   NioEventLoop(NioEventLoopGroup parent, ThreadFactory threadFactory, SelectorProvider selectorProvider) {
      super(parent, threadFactory, false);
      if(selectorProvider == null) {
         throw new NullPointerException("selectorProvider");
      } else {
         this.provider = selectorProvider;
         this.selector = this.openSelector();
      }
   }

   private Selector openSelector() {
      Selector selector;
      try {
         selector = this.provider.openSelector();
      } catch (IOException var7) {
         throw new ChannelException("failed to open a new selector", var7);
      }

      if(DISABLE_KEYSET_OPTIMIZATION) {
         return selector;
      } else {
         try {
            SelectedSelectionKeySet selectedKeySet = new SelectedSelectionKeySet();
            Class<?> selectorImplClass = Class.forName("sun.nio.ch.SelectorImpl", false, PlatformDependent.getSystemClassLoader());
            if(!selectorImplClass.isAssignableFrom(selector.getClass())) {
               return selector;
            }

            Field selectedKeysField = selectorImplClass.getDeclaredField("selectedKeys");
            Field publicSelectedKeysField = selectorImplClass.getDeclaredField("publicSelectedKeys");
            selectedKeysField.setAccessible(true);
            publicSelectedKeysField.setAccessible(true);
            selectedKeysField.set(selector, selectedKeySet);
            publicSelectedKeysField.set(selector, selectedKeySet);
            this.selectedKeys = selectedKeySet;
            logger.trace("Instrumented an optimized java.util.Set into: {}", (Object)selector);
         } catch (Throwable var6) {
            this.selectedKeys = null;
            logger.trace("Failed to instrument an optimized java.util.Set into: {}", selector, var6);
         }

         return selector;
      }
   }

   protected Queue newTaskQueue() {
      return PlatformDependent.newMpscQueue();
   }

   public void register(SelectableChannel ch, int interestOps, NioTask task) {
      if(ch == null) {
         throw new NullPointerException("ch");
      } else if(interestOps == 0) {
         throw new IllegalArgumentException("interestOps must be non-zero.");
      } else if((interestOps & ~ch.validOps()) != 0) {
         throw new IllegalArgumentException("invalid interestOps: " + interestOps + "(validOps: " + ch.validOps() + ')');
      } else if(task == null) {
         throw new NullPointerException("task");
      } else if(this.isShutdown()) {
         throw new IllegalStateException("event loop shut down");
      } else {
         try {
            ch.register(this.selector, interestOps, task);
         } catch (Exception var5) {
            throw new EventLoopException("failed to register a channel", var5);
         }
      }
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

   public void rebuildSelector() {
      if(!this.inEventLoop()) {
         this.execute(new Runnable() {
            public void run() {
               NioEventLoop.this.rebuildSelector();
            }
         });
      } else {
         Selector oldSelector = this.selector;
         if(oldSelector != null) {
            Selector newSelector;
            try {
               newSelector = this.openSelector();
            } catch (Exception var9) {
               logger.warn("Failed to create a new Selector.", (Throwable)var9);
               return;
            }

            int nChannels = 0;

            label53:
            while(true) {
               try {
                  Iterator i$ = oldSelector.keys().iterator();

                  while(true) {
                     if(!i$.hasNext()) {
                        break label53;
                     }

                     SelectionKey key = (SelectionKey)i$.next();
                     Object a = key.attachment();

                     try {
                        if(key.isValid() && key.channel().keyFor(newSelector) == null) {
                           int interestOps = key.interestOps();
                           key.cancel();
                           SelectionKey newKey = key.channel().register(newSelector, interestOps, a);
                           if(a instanceof AbstractNioChannel) {
                              ((AbstractNioChannel)a).selectionKey = newKey;
                           }

                           ++nChannels;
                        }
                     } catch (Exception var11) {
                        logger.warn("Failed to re-register a Channel to the new Selector.", (Throwable)var11);
                        if(a instanceof AbstractNioChannel) {
                           AbstractNioChannel ch = (AbstractNioChannel)a;
                           ch.unsafe().close(ch.unsafe().voidPromise());
                        } else {
                           NioTask<SelectableChannel> task = (NioTask)a;
                           invokeChannelUnregistered(task, key, var11);
                        }
                     }
                  }
               } catch (ConcurrentModificationException var12) {
                  ;
               }
            }

            this.selector = newSelector;

            try {
               oldSelector.close();
            } catch (Throwable var10) {
               if(logger.isWarnEnabled()) {
                  logger.warn("Failed to close the old Selector.", var10);
               }
            }

            logger.info("Migrated " + nChannels + " channel(s) to the new Selector.");
         }
      }
   }

   protected void run() {
      while(true) {
         boolean oldWakenUp = this.wakenUp.getAndSet(false);

         try {
            if(this.hasTasks()) {
               this.selectNow();
            } else {
               this.select(oldWakenUp);
               if(this.wakenUp.get()) {
                  this.selector.wakeup();
               }
            }

            this.cancelledKeys = 0;
            this.needsToSelectAgain = false;
            int ioRatio = this.ioRatio;
            if(ioRatio == 100) {
               this.processSelectedKeys();
               this.runAllTasks();
            } else {
               long ioStartTime = System.nanoTime();
               this.processSelectedKeys();
               long ioTime = System.nanoTime() - ioStartTime;
               this.runAllTasks(ioTime * (long)(100 - ioRatio) / (long)ioRatio);
            }

            if(this.isShuttingDown()) {
               this.closeAll();
               if(this.confirmShutdown()) {
                  return;
               }
            }
         } catch (Throwable var8) {
            logger.warn("Unexpected exception in the selector loop.", var8);

            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var7) {
               ;
            }
         }
      }
   }

   private void processSelectedKeys() {
      if(this.selectedKeys != null) {
         this.processSelectedKeysOptimized(this.selectedKeys.flip());
      } else {
         this.processSelectedKeysPlain(this.selector.selectedKeys());
      }

   }

   protected void cleanup() {
      try {
         this.selector.close();
      } catch (IOException var2) {
         logger.warn("Failed to close a selector.", (Throwable)var2);
      }

   }

   void cancel(SelectionKey key) {
      key.cancel();
      ++this.cancelledKeys;
      if(this.cancelledKeys >= 256) {
         this.cancelledKeys = 0;
         this.needsToSelectAgain = true;
      }

   }

   protected Runnable pollTask() {
      Runnable task = super.pollTask();
      if(this.needsToSelectAgain) {
         this.selectAgain();
      }

      return task;
   }

   private void processSelectedKeysPlain(Set selectedKeys) {
      if(!selectedKeys.isEmpty()) {
         Iterator<SelectionKey> i = selectedKeys.iterator();

         while(true) {
            SelectionKey k = (SelectionKey)i.next();
            Object a = k.attachment();
            i.remove();
            if(a instanceof AbstractNioChannel) {
               processSelectedKey(k, (AbstractNioChannel)a);
            } else {
               NioTask<SelectableChannel> task = (NioTask)a;
               processSelectedKey(k, task);
            }

            if(!i.hasNext()) {
               break;
            }

            if(this.needsToSelectAgain) {
               this.selectAgain();
               selectedKeys = this.selector.selectedKeys();
               if(selectedKeys.isEmpty()) {
                  break;
               }

               i = selectedKeys.iterator();
            }
         }

      }
   }

   private void processSelectedKeysOptimized(SelectionKey[] selectedKeys) {
      int i = 0;

      while(true) {
         SelectionKey k = selectedKeys[i];
         if(k == null) {
            return;
         }

         selectedKeys[i] = null;
         Object a = k.attachment();
         if(a instanceof AbstractNioChannel) {
            processSelectedKey(k, (AbstractNioChannel)a);
         } else {
            NioTask<SelectableChannel> task = (NioTask)a;
            processSelectedKey(k, task);
         }

         if(this.needsToSelectAgain) {
            while(selectedKeys[i] != null) {
               selectedKeys[i] = null;
               ++i;
            }

            this.selectAgain();
            selectedKeys = this.selectedKeys.flip();
            i = -1;
         }

         ++i;
      }
   }

   private static void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
      AbstractNioChannel.NioUnsafe unsafe = ch.unsafe();
      if(!k.isValid()) {
         unsafe.close(unsafe.voidPromise());
      } else {
         try {
            int readyOps = k.readyOps();
            if((readyOps & 17) != 0 || readyOps == 0) {
               unsafe.read();
               if(!ch.isOpen()) {
                  return;
               }
            }

            if((readyOps & 4) != 0) {
               ch.unsafe().forceFlush();
            }

            if((readyOps & 8) != 0) {
               int ops = k.interestOps();
               ops = ops & -9;
               k.interestOps(ops);
               unsafe.finishConnect();
            }
         } catch (CancelledKeyException var5) {
            unsafe.close(unsafe.voidPromise());
         }

      }
   }

   private static void processSelectedKey(SelectionKey k, NioTask task) {
      // $FF: Couldn't be decompiled
   }

   private void closeAll() {
      this.selectAgain();
      Set<SelectionKey> keys = this.selector.keys();
      Collection<AbstractNioChannel> channels = new ArrayList(keys.size());

      for(SelectionKey k : keys) {
         Object a = k.attachment();
         if(a instanceof AbstractNioChannel) {
            channels.add((AbstractNioChannel)a);
         } else {
            k.cancel();
            NioTask<SelectableChannel> task = (NioTask)a;
            invokeChannelUnregistered(task, k, (Throwable)null);
         }
      }

      for(AbstractNioChannel ch : channels) {
         ch.unsafe().close(ch.unsafe().voidPromise());
      }

   }

   private static void invokeChannelUnregistered(NioTask task, SelectionKey k, Throwable cause) {
      try {
         task.channelUnregistered(k.channel(), cause);
      } catch (Exception var4) {
         logger.warn("Unexpected exception while running NioTask.channelUnregistered()", (Throwable)var4);
      }

   }

   protected void wakeup(boolean inEventLoop) {
      if(!inEventLoop && this.wakenUp.compareAndSet(false, true)) {
         this.selector.wakeup();
      }

   }

   void selectNow() throws IOException {
      try {
         this.selector.selectNow();
      } finally {
         if(this.wakenUp.get()) {
            this.selector.wakeup();
         }

      }

   }

   private void select(boolean oldWakenUp) throws IOException {
      Selector selector = this.selector;

      try {
         int selectCnt = 0;
         long currentTimeNanos = System.nanoTime();
         long selectDeadLineNanos = currentTimeNanos + this.delayNanos(currentTimeNanos);

         while(true) {
            long timeoutMillis = (selectDeadLineNanos - currentTimeNanos + 500000L) / 1000000L;
            if(timeoutMillis <= 0L) {
               if(selectCnt == 0) {
                  selector.selectNow();
                  selectCnt = 1;
               }
               break;
            }

            int selectedKeys = selector.select(timeoutMillis);
            ++selectCnt;
            if(selectedKeys != 0 || oldWakenUp || this.wakenUp.get() || this.hasTasks() || this.hasScheduledTasks()) {
               break;
            }

            if(Thread.interrupted()) {
               if(logger.isDebugEnabled()) {
                  logger.debug("Selector.select() returned prematurely because Thread.currentThread().interrupt() was called. Use NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop.");
               }

               selectCnt = 1;
               break;
            }

            long time = System.nanoTime();
            if(time - TimeUnit.MILLISECONDS.toNanos(timeoutMillis) >= currentTimeNanos) {
               selectCnt = 1;
            } else if(SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
               logger.warn("Selector.select() returned prematurely {} times in a row; rebuilding selector.", (Object)Integer.valueOf(selectCnt));
               this.rebuildSelector();
               selector = this.selector;
               selector.selectNow();
               selectCnt = 1;
               break;
            }

            currentTimeNanos = time;
         }

         if(selectCnt > 3 && logger.isDebugEnabled()) {
            logger.debug("Selector.select() returned prematurely {} times in a row.", (Object)Integer.valueOf(selectCnt - 1));
         }
      } catch (CancelledKeyException var13) {
         if(logger.isDebugEnabled()) {
            logger.debug(CancelledKeyException.class.getSimpleName() + " raised by a Selector - JDK bug?", (Throwable)var13);
         }
      }

   }

   private void selectAgain() {
      this.needsToSelectAgain = false;

      try {
         this.selector.selectNow();
      } catch (Throwable var2) {
         logger.warn("Failed to update SelectionKeys.", var2);
      }

   }

   static {
      String key = "sun.nio.ch.bugLevel";

      try {
         String buglevel = SystemPropertyUtil.get(key);
         if(buglevel == null) {
            System.setProperty(key, "");
         }
      } catch (SecurityException var2) {
         if(logger.isDebugEnabled()) {
            logger.debug("Unable to get/set System Property: {}", key, var2);
         }
      }

      int selectorAutoRebuildThreshold = SystemPropertyUtil.getInt("io.netty.selectorAutoRebuildThreshold", 512);
      if(selectorAutoRebuildThreshold < 3) {
         selectorAutoRebuildThreshold = 0;
      }

      SELECTOR_AUTO_REBUILD_THRESHOLD = selectorAutoRebuildThreshold;
      if(logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.noKeySetOptimization: {}", (Object)Boolean.valueOf(DISABLE_KEYSET_OPTIMIZATION));
         logger.debug("-Dio.netty.selectorAutoRebuildThreshold: {}", (Object)Integer.valueOf(SELECTOR_AUTO_REBUILD_THRESHOLD));
      }

   }
}
