package io.netty.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FailedChannelFuture;
import io.netty.channel.ThreadPerChannelEventLoop;
import io.netty.util.concurrent.AbstractEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReadOnlyIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ThreadPerChannelEventLoopGroup extends AbstractEventExecutorGroup implements EventLoopGroup {
   private final Object[] childArgs;
   private final int maxChannels;
   final ThreadFactory threadFactory;
   final Set activeChildren;
   final Queue idleChildren;
   private final ChannelException tooManyChannels;
   private volatile boolean shuttingDown;
   private final Promise terminationFuture;
   private final FutureListener childTerminationListener;

   protected ThreadPerChannelEventLoopGroup() {
      this(0);
   }

   protected ThreadPerChannelEventLoopGroup(int maxChannels) {
      this(maxChannels, Executors.defaultThreadFactory(), new Object[0]);
   }

   protected ThreadPerChannelEventLoopGroup(int maxChannels, ThreadFactory threadFactory, Object... args) {
      this.activeChildren = Collections.newSetFromMap(PlatformDependent.newConcurrentHashMap());
      this.idleChildren = new ConcurrentLinkedQueue();
      this.terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
      this.childTerminationListener = new FutureListener() {
         public void operationComplete(Future future) throws Exception {
            if(ThreadPerChannelEventLoopGroup.this.isTerminated()) {
               ThreadPerChannelEventLoopGroup.this.terminationFuture.trySuccess((Object)null);
            }

         }
      };
      if(maxChannels < 0) {
         throw new IllegalArgumentException(String.format("maxChannels: %d (expected: >= 0)", new Object[]{Integer.valueOf(maxChannels)}));
      } else if(threadFactory == null) {
         throw new NullPointerException("threadFactory");
      } else {
         if(args == null) {
            this.childArgs = EmptyArrays.EMPTY_OBJECTS;
         } else {
            this.childArgs = (Object[])args.clone();
         }

         this.maxChannels = maxChannels;
         this.threadFactory = threadFactory;
         this.tooManyChannels = new ChannelException("too many channels (max: " + maxChannels + ')');
         this.tooManyChannels.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
      }
   }

   protected ThreadPerChannelEventLoop newChild(Object... args) throws Exception {
      return new ThreadPerChannelEventLoop(this);
   }

   public Iterator iterator() {
      return new ReadOnlyIterator(this.activeChildren.iterator());
   }

   public EventLoop next() {
      throw new UnsupportedOperationException();
   }

   public Future shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
      this.shuttingDown = true;

      for(EventLoop l : this.activeChildren) {
         l.shutdownGracefully(quietPeriod, timeout, unit);
      }

      for(EventLoop l : this.idleChildren) {
         l.shutdownGracefully(quietPeriod, timeout, unit);
      }

      if(this.isTerminated()) {
         this.terminationFuture.trySuccess((Object)null);
      }

      return this.terminationFuture();
   }

   public Future terminationFuture() {
      return this.terminationFuture;
   }

   /** @deprecated */
   @Deprecated
   public void shutdown() {
      this.shuttingDown = true;

      for(EventLoop l : this.activeChildren) {
         l.shutdown();
      }

      for(EventLoop l : this.idleChildren) {
         l.shutdown();
      }

      if(this.isTerminated()) {
         this.terminationFuture.trySuccess((Object)null);
      }

   }

   public boolean isShuttingDown() {
      for(EventLoop l : this.activeChildren) {
         if(!l.isShuttingDown()) {
            return false;
         }
      }

      for(EventLoop l : this.idleChildren) {
         if(!l.isShuttingDown()) {
            return false;
         }
      }

      return true;
   }

   public boolean isShutdown() {
      for(EventLoop l : this.activeChildren) {
         if(!l.isShutdown()) {
            return false;
         }
      }

      for(EventLoop l : this.idleChildren) {
         if(!l.isShutdown()) {
            return false;
         }
      }

      return true;
   }

   public boolean isTerminated() {
      for(EventLoop l : this.activeChildren) {
         if(!l.isTerminated()) {
            return false;
         }
      }

      for(EventLoop l : this.idleChildren) {
         if(!l.isTerminated()) {
            return false;
         }
      }

      return true;
   }

   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      long deadline = System.nanoTime() + unit.toNanos(timeout);

      for(EventLoop l : this.activeChildren) {
         while(true) {
            long timeLeft = deadline - System.nanoTime();
            if(timeLeft <= 0L) {
               return this.isTerminated();
            }

            if(l.awaitTermination(timeLeft, TimeUnit.NANOSECONDS)) {
               break;
            }
         }
      }

      for(EventLoop l : this.idleChildren) {
         while(true) {
            long timeLeft = deadline - System.nanoTime();
            if(timeLeft <= 0L) {
               return this.isTerminated();
            }

            if(l.awaitTermination(timeLeft, TimeUnit.NANOSECONDS)) {
               break;
            }
         }
      }

      return this.isTerminated();
   }

   public ChannelFuture register(Channel channel) {
      if(channel == null) {
         throw new NullPointerException("channel");
      } else {
         try {
            EventLoop l = this.nextChild();
            return l.register(channel, new DefaultChannelPromise(channel, l));
         } catch (Throwable var3) {
            return new FailedChannelFuture(channel, GlobalEventExecutor.INSTANCE, var3);
         }
      }
   }

   public ChannelFuture register(Channel channel, ChannelPromise promise) {
      if(channel == null) {
         throw new NullPointerException("channel");
      } else {
         try {
            return this.nextChild().register(channel, promise);
         } catch (Throwable var4) {
            promise.setFailure(var4);
            return promise;
         }
      }
   }

   private EventLoop nextChild() throws Exception {
      if(this.shuttingDown) {
         throw new RejectedExecutionException("shutting down");
      } else {
         ThreadPerChannelEventLoop loop = (ThreadPerChannelEventLoop)this.idleChildren.poll();
         if(loop == null) {
            if(this.maxChannels > 0 && this.activeChildren.size() >= this.maxChannels) {
               throw this.tooManyChannels;
            }

            loop = this.newChild(this.childArgs);
            loop.terminationFuture().addListener(this.childTerminationListener);
         }

         this.activeChildren.add(loop);
         return loop;
      }
   }
}
