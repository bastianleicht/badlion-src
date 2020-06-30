package io.netty.channel.epoll;

import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.util.concurrent.EventExecutor;
import java.util.concurrent.ThreadFactory;

public final class EpollEventLoopGroup extends MultithreadEventLoopGroup {
   public EpollEventLoopGroup() {
      this(0);
   }

   public EpollEventLoopGroup(int nThreads) {
      this(nThreads, (ThreadFactory)null);
   }

   public EpollEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
      this(nThreads, threadFactory, 128);
   }

   public EpollEventLoopGroup(int nThreads, ThreadFactory threadFactory, int maxEventsAtOnce) {
      super(nThreads, threadFactory, new Object[]{Integer.valueOf(maxEventsAtOnce)});
   }

   public void setIoRatio(int ioRatio) {
      for(EventExecutor e : this.children()) {
         ((EpollEventLoop)e).setIoRatio(ioRatio);
      }

   }

   protected EventExecutor newChild(ThreadFactory threadFactory, Object... args) throws Exception {
      return new EpollEventLoop(this, threadFactory, ((Integer)args[0]).intValue());
   }
}
