package io.netty.channel.local;

import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.local.LocalEventLoopGroup;
import java.util.concurrent.ThreadFactory;

final class LocalEventLoop extends SingleThreadEventLoop {
   LocalEventLoop(LocalEventLoopGroup parent, ThreadFactory threadFactory) {
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
