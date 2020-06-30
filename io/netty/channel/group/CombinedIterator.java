package io.netty.channel.group;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class CombinedIterator implements Iterator {
   private final Iterator i1;
   private final Iterator i2;
   private Iterator currentIterator;

   CombinedIterator(Iterator i1, Iterator i2) {
      if(i1 == null) {
         throw new NullPointerException("i1");
      } else if(i2 == null) {
         throw new NullPointerException("i2");
      } else {
         this.i1 = i1;
         this.i2 = i2;
         this.currentIterator = i1;
      }
   }

   public boolean hasNext() {
      while(!this.currentIterator.hasNext()) {
         if(this.currentIterator != this.i1) {
            return false;
         }

         this.currentIterator = this.i2;
      }

      return true;
   }

   public Object next() {
      while(true) {
         try {
            return this.currentIterator.next();
         } catch (NoSuchElementException var2) {
            if(this.currentIterator != this.i1) {
               throw var2;
            }

            this.currentIterator = this.i2;
         }
      }
   }

   public void remove() {
      this.currentIterator.remove();
   }
}
