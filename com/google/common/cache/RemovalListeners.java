package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import java.util.concurrent.Executor;

@Beta
public final class RemovalListeners {
   public static RemovalListener asynchronous(final RemovalListener listener, final Executor executor) {
      Preconditions.checkNotNull(listener);
      Preconditions.checkNotNull(executor);
      return new RemovalListener() {
         public void onRemoval(final RemovalNotification notification) {
            executor.execute(new Runnable() {
               public void run() {
                  listener.onRemoval(notification);
               }
            });
         }
      };
   }
}
