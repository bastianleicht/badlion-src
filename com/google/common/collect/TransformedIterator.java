package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Iterator;

@GwtCompatible
abstract class TransformedIterator implements Iterator {
   final Iterator backingIterator;

   TransformedIterator(Iterator backingIterator) {
      this.backingIterator = (Iterator)Preconditions.checkNotNull(backingIterator);
   }

   abstract Object transform(Object var1);

   public final boolean hasNext() {
      return this.backingIterator.hasNext();
   }

   public final Object next() {
      return this.transform(this.backingIterator.next());
   }

   public final void remove() {
      this.backingIterator.remove();
   }
}
