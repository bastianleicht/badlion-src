package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

@Beta
public abstract class AbstractListeningExecutorService extends AbstractExecutorService implements ListeningExecutorService {
   protected final ListenableFutureTask newTaskFor(Runnable runnable, Object value) {
      return ListenableFutureTask.create(runnable, value);
   }

   protected final ListenableFutureTask newTaskFor(Callable callable) {
      return ListenableFutureTask.create(callable);
   }

   public ListenableFuture submit(Runnable task) {
      return (ListenableFuture)super.submit(task);
   }

   public ListenableFuture submit(Runnable task, @Nullable Object result) {
      return (ListenableFuture)super.submit(task, result);
   }

   public ListenableFuture submit(Callable task) {
      return (ListenableFuture)super.submit(task);
   }
}
