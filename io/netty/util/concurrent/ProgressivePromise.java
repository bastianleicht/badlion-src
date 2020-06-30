package io.netty.util.concurrent;

import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ProgressiveFuture;
import io.netty.util.concurrent.Promise;

public interface ProgressivePromise extends Promise, ProgressiveFuture {
   ProgressivePromise setProgress(long var1, long var3);

   boolean tryProgress(long var1, long var3);

   ProgressivePromise setSuccess(Object var1);

   ProgressivePromise setFailure(Throwable var1);

   ProgressivePromise addListener(GenericFutureListener var1);

   ProgressivePromise addListeners(GenericFutureListener... var1);

   ProgressivePromise removeListener(GenericFutureListener var1);

   ProgressivePromise removeListeners(GenericFutureListener... var1);

   ProgressivePromise await() throws InterruptedException;

   ProgressivePromise awaitUninterruptibly();

   ProgressivePromise sync() throws InterruptedException;

   ProgressivePromise syncUninterruptibly();
}
