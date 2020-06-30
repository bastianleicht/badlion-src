package io.netty.util.concurrent;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public interface EventExecutorGroup extends ScheduledExecutorService, Iterable {
   boolean isShuttingDown();

   Future shutdownGracefully();

   Future shutdownGracefully(long var1, long var3, TimeUnit var5);

   Future terminationFuture();

   /** @deprecated */
   @Deprecated
   void shutdown();

   /** @deprecated */
   @Deprecated
   List shutdownNow();

   EventExecutor next();

   Iterator iterator();

   Future submit(Runnable var1);

   Future submit(Runnable var1, Object var2);

   Future submit(Callable var1);

   ScheduledFuture schedule(Runnable var1, long var2, TimeUnit var4);

   ScheduledFuture schedule(Callable var1, long var2, TimeUnit var4);

   ScheduledFuture scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6);

   ScheduledFuture scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6);
}
