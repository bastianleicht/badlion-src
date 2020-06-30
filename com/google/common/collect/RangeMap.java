package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.collect.Range;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@Beta
public interface RangeMap {
   @Nullable
   Object get(Comparable var1);

   @Nullable
   Entry getEntry(Comparable var1);

   Range span();

   void put(Range var1, Object var2);

   void putAll(RangeMap var1);

   void clear();

   void remove(Range var1);

   Map asMapOfRanges();

   RangeMap subRangeMap(Range var1);

   boolean equals(@Nullable Object var1);

   int hashCode();

   String toString();
}
