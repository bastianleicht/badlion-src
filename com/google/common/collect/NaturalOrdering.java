package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import com.google.common.collect.ReverseNaturalOrdering;
import java.io.Serializable;

@GwtCompatible(
   serializable = true
)
final class NaturalOrdering extends Ordering implements Serializable {
   static final NaturalOrdering INSTANCE = new NaturalOrdering();
   private static final long serialVersionUID = 0L;

   public int compare(Comparable left, Comparable right) {
      Preconditions.checkNotNull(left);
      Preconditions.checkNotNull(right);
      return left.compareTo(right);
   }

   public Ordering reverse() {
      return ReverseNaturalOrdering.INSTANCE;
   }

   private Object readResolve() {
      return INSTANCE;
   }

   public String toString() {
      return "Ordering.natural()";
   }
}
