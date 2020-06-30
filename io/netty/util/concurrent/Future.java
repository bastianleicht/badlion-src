package io.netty.util.concurrent;

import io.netty.util.concurrent.GenericFutureListener;
import java.util.concurrent.TimeUnit;

public interface Future extends java.util.concurrent.Future {
   boolean isSuccess();

   boolean isCancellable();

   Throwable cause();

   Future addListener(GenericFutureListener var1);

   Future addListeners(GenericFutureListener... var1);

   Future removeListener(GenericFutureListener var1);

   Future removeListeners(GenericFutureListener... var1);

   Future sync() throws InterruptedException;

   Future syncUninterruptibly();

   Future await() throws InterruptedException;

   Future awaitUninterruptibly();

   boolean await(long var1, TimeUnit var3) throws InterruptedException;

   boolean await(long var1) throws InterruptedException;

   boolean awaitUninterruptibly(long var1, TimeUnit var3);

   boolean awaitUninterruptibly(long var1);

   Object getNow();

   boolean cancel(boolean var1);
}
