package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Beta
public interface ListeningScheduledExecutorService extends ScheduledExecutorService, ListeningExecutorService {
   ListenableScheduledFuture schedule(Runnable var1, long var2, TimeUnit var4);

   ListenableScheduledFuture schedule(Callable var1, long var2, TimeUnit var4);

   ListenableScheduledFuture scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6);

   ListenableScheduledFuture scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6);
}
