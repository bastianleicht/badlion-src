package com.google.common.util.concurrent;

import com.google.common.util.concurrent.ListenableFuture;

public interface AsyncFunction {
   ListenableFuture apply(Object var1) throws Exception;
}
