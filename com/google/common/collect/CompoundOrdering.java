package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.util.Comparator;

@GwtCompatible(
   serializable = true
)
final class CompoundOrdering extends Ordering implements Serializable {
   final ImmutableList comparators;
   private static final long serialVersionUID = 0L;

   CompoundOrdering(Comparator primary, Comparator secondary) {
      this.comparators = ImmutableList.of(primary, secondary);
   }

   CompoundOrdering(Iterable comparators) {
      this.comparators = ImmutableList.copyOf(comparators);
   }

   public int compare(Object left, Object right) {
      int size = this.comparators.size();

      for(int i = 0; i < size; ++i) {
         int result = ((Comparator)this.comparators.get(i)).compare(left, right);
         if(result != 0) {
            return result;
         }
      }

      return 0;
   }

   public boolean equals(Object object) {
      if(object == this) {
         return true;
      } else if(object instanceof CompoundOrdering) {
         CompoundOrdering<?> that = (CompoundOrdering)object;
         return this.comparators.equals(that.comparators);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.comparators.hashCode();
   }

   public String toString() {
      return "Ordering.compound(" + this.comparators + ")";
   }
}
