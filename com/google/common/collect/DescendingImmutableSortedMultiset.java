package com.google.common.collect;

import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multiset;
import javax.annotation.Nullable;

final class DescendingImmutableSortedMultiset extends ImmutableSortedMultiset {
   private final transient ImmutableSortedMultiset forward;

   DescendingImmutableSortedMultiset(ImmutableSortedMultiset forward) {
      this.forward = forward;
   }

   public int count(@Nullable Object element) {
      return this.forward.count(element);
   }

   public Multiset.Entry firstEntry() {
      return this.forward.lastEntry();
   }

   public Multiset.Entry lastEntry() {
      return this.forward.firstEntry();
   }

   public int size() {
      return this.forward.size();
   }

   public ImmutableSortedSet elementSet() {
      return this.forward.elementSet().descendingSet();
   }

   Multiset.Entry getEntry(int index) {
      return (Multiset.Entry)this.forward.entrySet().asList().reverse().get(index);
   }

   public ImmutableSortedMultiset descendingMultiset() {
      return this.forward;
   }

   public ImmutableSortedMultiset headMultiset(Object upperBound, BoundType boundType) {
      return this.forward.tailMultiset(upperBound, boundType).descendingMultiset();
   }

   public ImmutableSortedMultiset tailMultiset(Object lowerBound, BoundType boundType) {
      return this.forward.headMultiset(lowerBound, boundType).descendingMultiset();
   }

   boolean isPartialView() {
      return this.forward.isPartialView();
   }
}
