package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
final class LexicographicalOrdering extends Ordering implements Serializable {
   final Ordering elementOrder;
   private static final long serialVersionUID = 0L;

   LexicographicalOrdering(Ordering elementOrder) {
      this.elementOrder = elementOrder;
   }

   public int compare(Iterable leftIterable, Iterable rightIterable) {
      Iterator<T> left = leftIterable.iterator();
      Iterator<T> right = rightIterable.iterator();

      while(left.hasNext()) {
         if(!right.hasNext()) {
            return 1;
         }

         int result = this.elementOrder.compare(left.next(), right.next());
         if(result != 0) {
            return result;
         }
      }

      if(right.hasNext()) {
         return -1;
      } else {
         return 0;
      }
   }

   public boolean equals(@Nullable Object object) {
      if(object == this) {
         return true;
      } else if(object instanceof LexicographicalOrdering) {
         LexicographicalOrdering<?> that = (LexicographicalOrdering)object;
         return this.elementOrder.equals(that.elementOrder);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.elementOrder.hashCode() ^ 2075626741;
   }

   public String toString() {
      return this.elementOrder + ".lexicographical()";
   }
}
