package io.netty.util.internal;

import java.util.Iterator;

public final class ReadOnlyIterator implements Iterator {
   private final Iterator iterator;

   public ReadOnlyIterator(Iterator iterator) {
      if(iterator == null) {
         throw new NullPointerException("iterator");
      } else {
         this.iterator = iterator;
      }
   }

   public boolean hasNext() {
      return this.iterator.hasNext();
   }

   public Object next() {
      return this.iterator.next();
   }

   public void remove() {
      throw new UnsupportedOperationException("read-only");
   }
}
