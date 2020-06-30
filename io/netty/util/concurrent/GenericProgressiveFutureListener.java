package io.netty.util.concurrent;

import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ProgressiveFuture;

public interface GenericProgressiveFutureListener extends GenericFutureListener {
   void operationProgressed(ProgressiveFuture var1, long var2, long var4) throws Exception;
}
