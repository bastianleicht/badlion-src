package com.google.common.util.concurrent;

import com.google.common.util.concurrent.ForwardingExecutorService;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.concurrent.Callable;

public abstract class ForwardingListeningExecutorService extends ForwardingExecutorService implements ListeningExecutorService {
   protected abstract ListeningExecutorService delegate();

   public ListenableFuture submit(Callable task) {
      return this.delegate().submit(task);
   }

   public ListenableFuture submit(Runnable task) {
      return this.delegate().submit(task);
   }

   public ListenableFuture submit(Runnable task, Object result) {
      return this.delegate().submit(task, result);
   }
}
