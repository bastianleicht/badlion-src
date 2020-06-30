package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingCollection;
import java.util.NoSuchElementException;
import java.util.Queue;

@GwtCompatible
public abstract class ForwardingQueue extends ForwardingCollection implements Queue {
   protected abstract Queue delegate();

   public boolean offer(Object o) {
      return this.delegate().offer(o);
   }

   public Object poll() {
      return this.delegate().poll();
   }

   public Object remove() {
      return this.delegate().remove();
   }

   public Object peek() {
      return this.delegate().peek();
   }

   public Object element() {
      return this.delegate().element();
   }

   protected boolean standardOffer(Object e) {
      try {
         return this.add(e);
      } catch (IllegalStateException var3) {
         return false;
      }
   }

   protected Object standardPeek() {
      try {
         return this.element();
      } catch (NoSuchElementException var2) {
         return null;
      }
   }

   protected Object standardPoll() {
      try {
         return this.remove();
      } catch (NoSuchElementException var2) {
         return null;
      }
   }
}
