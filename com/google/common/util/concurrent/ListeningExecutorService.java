package com.google.common.util.concurrent;

import com.google.common.util.concurrent.ListenableFuture;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public interface ListeningExecutorService extends ExecutorService {
   ListenableFuture submit(Callable var1);

   ListenableFuture submit(Runnable var1);

   ListenableFuture submit(Runnable var1, Object var2);

   List invokeAll(Collection var1) throws InterruptedException;

   List invokeAll(Collection var1, long var2, TimeUnit var4) throws InterruptedException;
}
