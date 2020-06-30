package io.netty.util.concurrent;

import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ProgressivePromise;
import io.netty.util.concurrent.Promise;

public interface EventExecutor extends EventExecutorGroup {
   EventExecutor next();

   EventExecutorGroup parent();

   boolean inEventLoop();

   boolean inEventLoop(Thread var1);

   Promise newPromise();

   ProgressivePromise newProgressivePromise();

   Future newSucceededFuture(Object var1);

   Future newFailedFuture(Throwable var1);
}
