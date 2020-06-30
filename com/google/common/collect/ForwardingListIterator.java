package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingIterator;
import java.util.ListIterator;

@GwtCompatible
public abstract class ForwardingListIterator extends ForwardingIterator implements ListIterator {
   protected abstract ListIterator delegate();

   public void add(Object element) {
      this.delegate().add(element);
   }

   public boolean hasPrevious() {
      return this.delegate().hasPrevious();
   }

   public int nextIndex() {
      return this.delegate().nextIndex();
   }

   public Object previous() {
      return this.delegate().previous();
   }

   public int previousIndex() {
      return this.delegate().previousIndex();
   }

   public void set(Object element) {
      this.delegate().set(element);
   }
}
