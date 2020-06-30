package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.ConcurrentInitializer;

public abstract class AtomicSafeInitializer implements ConcurrentInitializer {
   private final AtomicReference factory = new AtomicReference();
   private final AtomicReference reference = new AtomicReference();

   public final Object get() throws ConcurrentException {
      T result;
      while((result = this.reference.get()) == null) {
         if(this.factory.compareAndSet((Object)null, this)) {
            this.reference.set(this.initialize());
         }
      }

      return result;
   }

   protected abstract Object initialize() throws ConcurrentException;
}
