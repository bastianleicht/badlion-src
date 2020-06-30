package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public interface MapDifference {
   boolean areEqual();

   Map entriesOnlyOnLeft();

   Map entriesOnlyOnRight();

   Map entriesInCommon();

   Map entriesDiffering();

   boolean equals(@Nullable Object var1);

   int hashCode();

   public interface ValueDifference {
      Object leftValue();

      Object rightValue();

      boolean equals(@Nullable Object var1);

      int hashCode();
   }
}
