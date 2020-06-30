package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
final class AllEqualOrdering extends Ordering implements Serializable {
   static final AllEqualOrdering INSTANCE = new AllEqualOrdering();
   private static final long serialVersionUID = 0L;

   public int compare(@Nullable Object left, @Nullable Object right) {
      return 0;
   }

   public List sortedCopy(Iterable iterable) {
      return Lists.newArrayList(iterable);
   }

   public ImmutableList immutableSortedCopy(Iterable iterable) {
      return ImmutableList.copyOf(iterable);
   }

   public Ordering reverse() {
      return this;
   }

   private Object readResolve() {
      return INSTANCE;
   }

   public String toString() {
      return "Ordering.allEqual()";
   }
}
