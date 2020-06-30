package io.netty.util.concurrent;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GenericProgressiveFutureListener;
import java.util.Arrays;

final class DefaultFutureListeners {
   private GenericFutureListener[] listeners = new GenericFutureListener[2];
   private int size;
   private int progressiveSize;

   DefaultFutureListeners(GenericFutureListener first, GenericFutureListener second) {
      this.listeners[0] = first;
      this.listeners[1] = second;
      this.size = 2;
      if(first instanceof GenericProgressiveFutureListener) {
         ++this.progressiveSize;
      }

      if(second instanceof GenericProgressiveFutureListener) {
         ++this.progressiveSize;
      }

   }

   public void add(GenericFutureListener l) {
      GenericFutureListener<? extends Future<?>>[] listeners = this.listeners;
      int size = this.size;
      if(size == listeners.length) {
         this.listeners = listeners = (GenericFutureListener[])Arrays.copyOf(listeners, size << 1);
      }

      listeners[size] = l;
      this.size = size + 1;
      if(l instanceof GenericProgressiveFutureListener) {
         ++this.progressiveSize;
      }

   }

   public void remove(GenericFutureListener l) {
      GenericFutureListener<? extends Future<?>>[] listeners = this.listeners;
      int size = this.size;

      for(int i = 0; i < size; ++i) {
         if(listeners[i] == l) {
            int listenersToMove = size - i - 1;
            if(listenersToMove > 0) {
               System.arraycopy(listeners, i + 1, listeners, i, listenersToMove);
            }

            --size;
            listeners[size] = null;
            this.size = size;
            if(l instanceof GenericProgressiveFutureListener) {
               --this.progressiveSize;
            }

            return;
         }
      }

   }

   public GenericFutureListener[] listeners() {
      return this.listeners;
   }

   public int size() {
      return this.size;
   }

   public int progressiveSize() {
      return this.progressiveSize;
   }
}
