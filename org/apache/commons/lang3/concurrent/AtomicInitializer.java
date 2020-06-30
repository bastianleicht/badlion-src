package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.ConcurrentInitializer;

public abstract class AtomicInitializer implements ConcurrentInitializer {
   private final AtomicReference reference = new AtomicReference();

   public Object get() throws ConcurrentException {
      T result = this.reference.get();
      if(result == null) {
         result = this.initialize();
         if(!this.reference.compareAndSet((Object)null, result)) {
            result = this.reference.get();
         }
      }

      return result;
   }

   protected abstract Object initialize() throws ConcurrentException;
}
