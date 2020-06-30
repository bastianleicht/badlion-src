package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Iterators;
import com.google.common.collect.TransformedIterator;
import java.util.ListIterator;

@GwtCompatible
abstract class TransformedListIterator extends TransformedIterator implements ListIterator {
   TransformedListIterator(ListIterator backingIterator) {
      super(backingIterator);
   }

   private ListIterator backingIterator() {
      return Iterators.cast(this.backingIterator);
   }

   public final boolean hasPrevious() {
      return this.backingIterator().hasPrevious();
   }

   public final Object previous() {
      return this.transform(this.backingIterator().previous());
   }

   public final int nextIndex() {
      return this.backingIterator().nextIndex();
   }

   public final int previousIndex() {
      return this.backingIterator().previousIndex();
   }

   public void set(Object element) {
      throw new UnsupportedOperationException();
   }

   public void add(Object element) {
      throw new UnsupportedOperationException();
   }
}
