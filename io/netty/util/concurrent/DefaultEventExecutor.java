package io.netty.util.concurrent;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import java.util.concurrent.ThreadFactory;

final class DefaultEventExecutor extends SingleThreadEventExecutor {
   DefaultEventExecutor(DefaultEventExecutorGroup parent, ThreadFactory threadFactory) {
      super(parent, threadFactory, true);
   }

   protected void run() {
      while(true) {
         Runnable task = this.takeTask();
         if(task != null) {
            task.run();
            this.updateLastExecutionTime();
         }

         if(this.confirmShutdown()) {
            break;
         }
      }

   }
}
