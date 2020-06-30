package io.netty.util.concurrent;

import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.MultithreadEventExecutorGroup;
import java.util.concurrent.ThreadFactory;

public class DefaultEventExecutorGroup extends MultithreadEventExecutorGroup {
   public DefaultEventExecutorGroup(int nThreads) {
      this(nThreads, (ThreadFactory)null);
   }

   public DefaultEventExecutorGroup(int nThreads, ThreadFactory threadFactory) {
      super(nThreads, threadFactory, new Object[0]);
   }

   protected EventExecutor newChild(ThreadFactory threadFactory, Object... args) throws Exception {
      return new DefaultEventExecutor(this, threadFactory);
   }
}
