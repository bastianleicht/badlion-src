package org.apache.commons.lang3.concurrent;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.ConcurrentInitializer;

public abstract class LazyInitializer implements ConcurrentInitializer {
   private volatile Object object;

   public Object get() throws ConcurrentException {
      T result = this.object;
      if(result == null) {
         synchronized(this) {
            result = this.object;
            if(result == null) {
               this.object = result = this.initialize();
            }
         }
      }

      return result;
   }

   protected abstract Object initialize() throws ConcurrentException;
}
