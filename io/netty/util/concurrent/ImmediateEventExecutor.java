package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.DefaultProgressivePromise;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.ProgressivePromise;
import io.netty.util.concurrent.Promise;
import java.util.concurrent.TimeUnit;

public final class ImmediateEventExecutor extends AbstractEventExecutor {
   public static final ImmediateEventExecutor INSTANCE = new ImmediateEventExecutor();
   private final Future terminationFuture = new FailedFuture(GlobalEventExecutor.INSTANCE, new UnsupportedOperationException());

   public EventExecutorGroup parent() {
      return null;
   }

   public boolean inEventLoop() {
      return true;
   }

   public boolean inEventLoop(Thread thread) {
      return true;
   }

   public Future shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
      return this.terminationFuture();
   }

   public Future terminationFuture() {
      return this.terminationFuture;
   }

   /** @deprecated */
   @Deprecated
   public void shutdown() {
   }

   public boolean isShuttingDown() {
      return false;
   }

   public boolean isShutdown() {
      return false;
   }

   public boolean isTerminated() {
      return false;
   }

   public boolean awaitTermination(long timeout, TimeUnit unit) {
      return false;
   }

   public void execute(Runnable command) {
      if(command == null) {
         throw new NullPointerException("command");
      } else {
         command.run();
      }
   }

   public Promise newPromise() {
      return new ImmediateEventExecutor.ImmediatePromise(this);
   }

   public ProgressivePromise newProgressivePromise() {
      return new ImmediateEventExecutor.ImmediateProgressivePromise(this);
   }

   static class ImmediateProgressivePromise extends DefaultProgressivePromise {
      ImmediateProgressivePromise(EventExecutor executor) {
         super(executor);
      }

      protected void checkDeadLock() {
      }
   }

   static class ImmediatePromise extends DefaultPromise {
      ImmediatePromise(EventExecutor executor) {
         super(executor);
      }

      protected void checkDeadLock() {
      }
   }
}
