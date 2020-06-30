package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.SetMultimap;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public interface SortedSetMultimap extends SetMultimap {
   SortedSet get(@Nullable Object var1);

   SortedSet removeAll(@Nullable Object var1);

   SortedSet replaceValues(Object var1, Iterable var2);

   Map asMap();

   Comparator valueComparator();
}
