package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.AbstractSetMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.SortedSetMultimap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractSortedSetMultimap extends AbstractSetMultimap implements SortedSetMultimap {
   private static final long serialVersionUID = 430848587173315748L;

   protected AbstractSortedSetMultimap(Map map) {
      super(map);
   }

   abstract SortedSet createCollection();

   SortedSet createUnmodifiableEmptyCollection() {
      Comparator<? super V> comparator = this.valueComparator();
      return (SortedSet)(comparator == null?Collections.unmodifiableSortedSet(this.createCollection()):ImmutableSortedSet.emptySet(this.valueComparator()));
   }

   public SortedSet get(@Nullable Object key) {
      return (SortedSet)super.get(key);
   }

   public SortedSet removeAll(@Nullable Object key) {
      return (SortedSet)super.removeAll(key);
   }

   public SortedSet replaceValues(@Nullable Object key, Iterable values) {
      return (SortedSet)super.replaceValues(key, values);
   }

   public Map asMap() {
      return super.asMap();
   }

   public Collection values() {
      return super.values();
   }
}
