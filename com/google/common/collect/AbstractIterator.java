package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;
import java.util.NoSuchElementException;

@GwtCompatible
public abstract class AbstractIterator extends UnmodifiableIterator {
   private AbstractIterator.State state = AbstractIterator.State.NOT_READY;
   private Object next;

   protected abstract Object computeNext();

   protected final Object endOfData() {
      this.state = AbstractIterator.State.DONE;
      return null;
   }

   public final boolean hasNext() {
      Preconditions.checkState(this.state != AbstractIterator.State.FAILED);
      switch(this.state) {
      case DONE:
         return false;
      case READY:
         return true;
      default:
         return this.tryToComputeNext();
      }
   }

   private boolean tryToComputeNext() {
      this.state = AbstractIterator.State.FAILED;
      this.next = this.computeNext();
      if(this.state != AbstractIterator.State.DONE) {
         this.state = AbstractIterator.State.READY;
         return true;
      } else {
         return false;
      }
   }

   public final Object next() {
      if(!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         this.state = AbstractIterator.State.NOT_READY;
         T result = this.next;
         this.next = null;
         return result;
      }
   }

   public final Object peek() {
      if(!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.next;
      }
   }

   private static enum State {
      READY,
      NOT_READY,
      DONE,
      FAILED;
   }
}
