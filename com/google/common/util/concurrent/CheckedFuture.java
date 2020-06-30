package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public interface CheckedFuture extends ListenableFuture {
   Object checkedGet() throws Exception;

   Object checkedGet(long var1, TimeUnit var3) throws TimeoutException, Exception;
}
