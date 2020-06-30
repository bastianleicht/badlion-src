package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ForwardingSetMultimap;
import com.google.common.collect.SortedSetMultimap;
import java.util.Comparator;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSortedSetMultimap extends ForwardingSetMultimap implements SortedSetMultimap {
   protected abstract SortedSetMultimap delegate();

   public SortedSet get(@Nullable Object key) {
      return this.delegate().get(key);
   }

   public SortedSet removeAll(@Nullable Object key) {
      return this.delegate().removeAll(key);
   }

   public SortedSet replaceValues(Object key, Iterable values) {
      return this.delegate().replaceValues(key, values);
   }

   public Comparator valueComparator() {
      return this.delegate().valueComparator();
   }
}
