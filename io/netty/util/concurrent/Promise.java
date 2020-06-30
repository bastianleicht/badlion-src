package io.netty.util.concurrent;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public interface Promise extends Future {
   Promise setSuccess(Object var1);

   boolean trySuccess(Object var1);

   Promise setFailure(Throwable var1);

   boolean tryFailure(Throwable var1);

   boolean setUncancellable();

   Promise addListener(GenericFutureListener var1);

   Promise addListeners(GenericFutureListener... var1);

   Promise removeListener(GenericFutureListener var1);

   Promise removeListeners(GenericFutureListener... var1);

   Promise await() throws InterruptedException;

   Promise awaitUninterruptibly();

   Promise sync() throws InterruptedException;

   Promise syncUninterruptibly();
}
