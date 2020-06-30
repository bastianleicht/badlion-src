package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import java.util.Comparator;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class EmptyImmutableSortedMap extends ImmutableSortedMap {
   private final transient ImmutableSortedSet keySet;

   EmptyImmutableSortedMap(Comparator comparator) {
      this.keySet = ImmutableSortedSet.emptySet(comparator);
   }

   EmptyImmutableSortedMap(Comparator comparator, ImmutableSortedMap descendingMap) {
      super(descendingMap);
      this.keySet = ImmutableSortedSet.emptySet(comparator);
   }

   public Object get(@Nullable Object key) {
      return null;
   }

   public ImmutableSortedSet keySet() {
      return this.keySet;
   }

   public int size() {
      return 0;
   }

   public boolean isEmpty() {
      return true;
   }

   public ImmutableCollection values() {
      return ImmutableList.of();
   }

   public String toString() {
      return "{}";
   }

   boolean isPartialView() {
      return false;
   }

   public ImmutableSet entrySet() {
      return ImmutableSet.of();
   }

   ImmutableSet createEntrySet() {
      throw new AssertionError("should never be called");
   }

   public ImmutableSetMultimap asMultimap() {
      return ImmutableSetMultimap.of();
   }

   public ImmutableSortedMap headMap(Object toKey, boolean inclusive) {
      Preconditions.checkNotNull(toKey);
      return this;
   }

   public ImmutableSortedMap tailMap(Object fromKey, boolean inclusive) {
      Preconditions.checkNotNull(fromKey);
      return this;
   }

   ImmutableSortedMap createDescendingMap() {
      return new EmptyImmutableSortedMap(Ordering.from(this.comparator()).reverse(), this);
   }
}
