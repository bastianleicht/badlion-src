package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
final class ExplicitOrdering extends Ordering implements Serializable {
   final ImmutableMap rankMap;
   private static final long serialVersionUID = 0L;

   ExplicitOrdering(List valuesInOrder) {
      this(buildRankMap(valuesInOrder));
   }

   ExplicitOrdering(ImmutableMap rankMap) {
      this.rankMap = rankMap;
   }

   public int compare(Object left, Object right) {
      return this.rank(left) - this.rank(right);
   }

   private int rank(Object value) {
      Integer rank = (Integer)this.rankMap.get(value);
      if(rank == null) {
         throw new Ordering.IncomparableValueException(value);
      } else {
         return rank.intValue();
      }
   }

   private static ImmutableMap buildRankMap(List valuesInOrder) {
      ImmutableMap.Builder<T, Integer> builder = ImmutableMap.builder();
      int rank = 0;

      for(T value : valuesInOrder) {
         builder.put(value, Integer.valueOf(rank++));
      }

      return builder.build();
   }

   public boolean equals(@Nullable Object object) {
      if(object instanceof ExplicitOrdering) {
         ExplicitOrdering<?> that = (ExplicitOrdering)object;
         return this.rankMap.equals(that.rankMap);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.rankMap.hashCode();
   }

   public String toString() {
      return "Ordering.explicit(" + this.rankMap.keySet() + ")";
   }
}
