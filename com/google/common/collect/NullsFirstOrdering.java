package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
final class NullsFirstOrdering extends Ordering implements Serializable {
   final Ordering ordering;
   private static final long serialVersionUID = 0L;

   NullsFirstOrdering(Ordering ordering) {
      this.ordering = ordering;
   }

   public int compare(@Nullable Object left, @Nullable Object right) {
      return left == right?0:(left == null?-1:(right == null?1:this.ordering.compare(left, right)));
   }

   public Ordering reverse() {
      return this.ordering.reverse().nullsLast();
   }

   public Ordering nullsFirst() {
      return this;
   }

   public Ordering nullsLast() {
      return this.ordering.nullsLast();
   }

   public boolean equals(@Nullable Object object) {
      if(object == this) {
         return true;
      } else if(object instanceof NullsFirstOrdering) {
         NullsFirstOrdering<?> that = (NullsFirstOrdering)object;
         return this.ordering.equals(that.ordering);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.ordering.hashCode() ^ 957692532;
   }

   public String toString() {
      return this.ordering + ".nullsFirst()";
   }
}
