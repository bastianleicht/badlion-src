package com.google.common.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public interface ListenableFuture extends Future {
   void addListener(Runnable var1, Executor var2);
}
