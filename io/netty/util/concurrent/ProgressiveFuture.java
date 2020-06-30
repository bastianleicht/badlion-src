package io.netty.util.concurrent;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public interface ProgressiveFuture extends Future {
   ProgressiveFuture addListener(GenericFutureListener var1);

   ProgressiveFuture addListeners(GenericFutureListener... var1);

   ProgressiveFuture removeListener(GenericFutureListener var1);

   ProgressiveFuture removeListeners(GenericFutureListener... var1);

   ProgressiveFuture sync() throws InterruptedException;

   ProgressiveFuture syncUninterruptibly();

   ProgressiveFuture await() throws InterruptedException;

   ProgressiveFuture awaitUninterruptibly();
}
