package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.UnmodifiableIterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class AbstractSequentialIterator extends UnmodifiableIterator {
   private Object nextOrNull;

   protected AbstractSequentialIterator(@Nullable Object firstOrNull) {
      this.nextOrNull = firstOrNull;
   }

   protected abstract Object computeNext(Object var1);

   public final boolean hasNext() {
      return this.nextOrNull != null;
   }

   public final Object next() {
      if(!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         Object var1;
         try {
            var1 = this.nextOrNull;
         } finally {
            this.nextOrNull = this.computeNext(this.nextOrNull);
         }

         return var1;
      }
   }
}
