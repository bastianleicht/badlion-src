package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multiset;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Comparator;
import javax.annotation.Nullable;

final class EmptyImmutableSortedMultiset extends ImmutableSortedMultiset {
   private final ImmutableSortedSet elementSet;

   EmptyImmutableSortedMultiset(Comparator comparator) {
      this.elementSet = ImmutableSortedSet.emptySet(comparator);
   }

   public Multiset.Entry firstEntry() {
      return null;
   }

   public Multiset.Entry lastEntry() {
      return null;
   }

   public int count(@Nullable Object element) {
      return 0;
   }

   public boolean containsAll(Collection targets) {
      return targets.isEmpty();
   }

   public int size() {
      return 0;
   }

   public ImmutableSortedSet elementSet() {
      return this.elementSet;
   }

   Multiset.Entry getEntry(int index) {
      throw new AssertionError("should never be called");
   }

   public ImmutableSortedMultiset headMultiset(Object upperBound, BoundType boundType) {
      Preconditions.checkNotNull(upperBound);
      Preconditions.checkNotNull(boundType);
      return this;
   }

   public ImmutableSortedMultiset tailMultiset(Object lowerBound, BoundType boundType) {
      Preconditions.checkNotNull(lowerBound);
      Preconditions.checkNotNull(boundType);
      return this;
   }

   public UnmodifiableIterator iterator() {
      return Iterators.emptyIterator();
   }

   public boolean equals(@Nullable Object object) {
      if(object instanceof Multiset) {
         Multiset<?> other = (Multiset)object;
         return other.isEmpty();
      } else {
         return false;
      }
   }

   boolean isPartialView() {
      return false;
   }

   int copyIntoArray(Object[] dst, int offset) {
      return offset;
   }

   public ImmutableList asList() {
      return ImmutableList.of();
   }
}
