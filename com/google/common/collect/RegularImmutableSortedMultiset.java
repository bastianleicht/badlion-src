package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.RegularImmutableSortedSet;
import com.google.common.primitives.Ints;
import javax.annotation.Nullable;

final class RegularImmutableSortedMultiset extends ImmutableSortedMultiset {
   private final transient RegularImmutableSortedSet elementSet;
   private final transient int[] counts;
   private final transient long[] cumulativeCounts;
   private final transient int offset;
   private final transient int length;

   RegularImmutableSortedMultiset(RegularImmutableSortedSet elementSet, int[] counts, long[] cumulativeCounts, int offset, int length) {
      this.elementSet = elementSet;
      this.counts = counts;
      this.cumulativeCounts = cumulativeCounts;
      this.offset = offset;
      this.length = length;
   }

   Multiset.Entry getEntry(int index) {
      return Multisets.immutableEntry(this.elementSet.asList().get(index), this.counts[this.offset + index]);
   }

   public Multiset.Entry firstEntry() {
      return this.getEntry(0);
   }

   public Multiset.Entry lastEntry() {
      return this.getEntry(this.length - 1);
   }

   public int count(@Nullable Object element) {
      int index = this.elementSet.indexOf(element);
      return index == -1?0:this.counts[index + this.offset];
   }

   public int size() {
      long size = this.cumulativeCounts[this.offset + this.length] - this.cumulativeCounts[this.offset];
      return Ints.saturatedCast(size);
   }

   public ImmutableSortedSet elementSet() {
      return this.elementSet;
   }

   public ImmutableSortedMultiset headMultiset(Object upperBound, BoundType boundType) {
      return this.getSubMultiset(0, this.elementSet.headIndex(upperBound, Preconditions.checkNotNull(boundType) == BoundType.CLOSED));
   }

   public ImmutableSortedMultiset tailMultiset(Object lowerBound, BoundType boundType) {
      return this.getSubMultiset(this.elementSet.tailIndex(lowerBound, Preconditions.checkNotNull(boundType) == BoundType.CLOSED), this.length);
   }

   ImmutableSortedMultiset getSubMultiset(int from, int to) {
      Preconditions.checkPositionIndexes(from, to, this.length);
      if(from == to) {
         return emptyMultiset(this.comparator());
      } else if(from == 0 && to == this.length) {
         return this;
      } else {
         RegularImmutableSortedSet<E> subElementSet = (RegularImmutableSortedSet)this.elementSet.getSubSet(from, to);
         return new RegularImmutableSortedMultiset(subElementSet, this.counts, this.cumulativeCounts, this.offset + from, to - from);
      }
   }

   boolean isPartialView() {
      return this.offset > 0 || this.length < this.counts.length;
   }
}
