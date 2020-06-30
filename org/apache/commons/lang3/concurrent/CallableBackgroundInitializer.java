package org.apache.commons.lang3.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang3.concurrent.BackgroundInitializer;

public class CallableBackgroundInitializer extends BackgroundInitializer {
   private final Callable callable;

   public CallableBackgroundInitializer(Callable call) {
      this.checkCallable(call);
      this.callable = call;
   }

   public CallableBackgroundInitializer(Callable call, ExecutorService exec) {
      super(exec);
      this.checkCallable(call);
      this.callable = call;
   }

   protected Object initialize() throws Exception {
      return this.callable.call();
   }

   private void checkCallable(Callable call) {
      if(call == null) {
         throw new IllegalArgumentException("Callable must not be null!");
      }
   }
}
