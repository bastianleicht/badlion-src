package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.BoundType;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedMultiset;
import java.util.Comparator;
import java.util.NavigableSet;

@GwtCompatible(
   emulated = true
)
final class UnmodifiableSortedMultiset extends Multisets.UnmodifiableMultiset implements SortedMultiset {
   private transient UnmodifiableSortedMultiset descendingMultiset;
   private static final long serialVersionUID = 0L;

   UnmodifiableSortedMultiset(SortedMultiset delegate) {
      super(delegate);
   }

   protected SortedMultiset delegate() {
      return (SortedMultiset)super.delegate();
   }

   public Comparator comparator() {
      return this.delegate().comparator();
   }

   NavigableSet createElementSet() {
      return Sets.unmodifiableNavigableSet(this.delegate().elementSet());
   }

   public NavigableSet elementSet() {
      return (NavigableSet)super.elementSet();
   }

   public SortedMultiset descendingMultiset() {
      UnmodifiableSortedMultiset<E> result = this.descendingMultiset;
      if(result == null) {
         result = new UnmodifiableSortedMultiset(this.delegate().descendingMultiset());
         result.descendingMultiset = this;
         return this.descendingMultiset = result;
      } else {
         return result;
      }
   }

   public Multiset.Entry firstEntry() {
      return this.delegate().firstEntry();
   }

   public Multiset.Entry lastEntry() {
      return this.delegate().lastEntry();
   }

   public Multiset.Entry pollFirstEntry() {
      throw new UnsupportedOperationException();
   }

   public Multiset.Entry pollLastEntry() {
      throw new UnsupportedOperationException();
   }

   public SortedMultiset headMultiset(Object upperBound, BoundType boundType) {
      return Multisets.unmodifiableSortedMultiset(this.delegate().headMultiset(upperBound, boundType));
   }

   public SortedMultiset subMultiset(Object lowerBound, BoundType lowerBoundType, Object upperBound, BoundType upperBoundType) {
      return Multisets.unmodifiableSortedMultiset(this.delegate().subMultiset(lowerBound, lowerBoundType, upperBound, upperBoundType));
   }

   public SortedMultiset tailMultiset(Object lowerBound, BoundType boundType) {
      return Multisets.unmodifiableSortedMultiset(this.delegate().tailMultiset(lowerBound, boundType));
   }
}
