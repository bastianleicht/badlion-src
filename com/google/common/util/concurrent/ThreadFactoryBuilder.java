package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public final class ThreadFactoryBuilder {
   private String nameFormat = null;
   private Boolean daemon = null;
   private Integer priority = null;
   private UncaughtExceptionHandler uncaughtExceptionHandler = null;
   private ThreadFactory backingThreadFactory = null;

   public ThreadFactoryBuilder setNameFormat(String nameFormat) {
      String.format(nameFormat, new Object[]{Integer.valueOf(0)});
      this.nameFormat = nameFormat;
      return this;
   }

   public ThreadFactoryBuilder setDaemon(boolean daemon) {
      this.daemon = Boolean.valueOf(daemon);
      return this;
   }

   public ThreadFactoryBuilder setPriority(int priority) {
      Preconditions.checkArgument(priority >= 1, "Thread priority (%s) must be >= %s", new Object[]{Integer.valueOf(priority), Integer.valueOf(1)});
      Preconditions.checkArgument(priority <= 10, "Thread priority (%s) must be <= %s", new Object[]{Integer.valueOf(priority), Integer.valueOf(10)});
      this.priority = Integer.valueOf(priority);
      return this;
   }

   public ThreadFactoryBuilder setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler) {
      this.uncaughtExceptionHandler = (UncaughtExceptionHandler)Preconditions.checkNotNull(uncaughtExceptionHandler);
      return this;
   }

   public ThreadFactoryBuilder setThreadFactory(ThreadFactory backingThreadFactory) {
      this.backingThreadFactory = (ThreadFactory)Preconditions.checkNotNull(backingThreadFactory);
      return this;
   }

   public ThreadFactory build() {
      return build(this);
   }

   private static ThreadFactory build(ThreadFactoryBuilder builder) {
      final String nameFormat = builder.nameFormat;
      final Boolean daemon = builder.daemon;
      final Integer priority = builder.priority;
      final UncaughtExceptionHandler uncaughtExceptionHandler = builder.uncaughtExceptionHandler;
      final ThreadFactory backingThreadFactory = builder.backingThreadFactory != null?builder.backingThreadFactory:Executors.defaultThreadFactory();
      final AtomicLong count = nameFormat != null?new AtomicLong(0L):null;
      return new ThreadFactory() {
         public Thread newThread(Runnable runnable) {
            Thread thread = backingThreadFactory.newThread(runnable);
            if(nameFormat != null) {
               thread.setName(String.format(nameFormat, new Object[]{Long.valueOf(count.getAndIncrement())}));
            }

            if(daemon != null) {
               thread.setDaemon(daemon.booleanValue());
            }

            if(priority != null) {
               thread.setPriority(priority.intValue());
            }

            if(uncaughtExceptionHandler != null) {
               thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            }

            return thread;
         }
      };
   }
}
