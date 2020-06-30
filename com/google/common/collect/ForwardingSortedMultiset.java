package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.BoundType;
import com.google.common.collect.DescendingMultiset;
import com.google.common.collect.ForwardingMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.SortedMultisets;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;

@Beta
@GwtCompatible(
   emulated = true
)
public abstract class ForwardingSortedMultiset extends ForwardingMultiset implements SortedMultiset {
   protected abstract SortedMultiset delegate();

   public NavigableSet elementSet() {
      return (NavigableSet)super.elementSet();
   }

   public Comparator comparator() {
      return this.delegate().comparator();
   }

   public SortedMultiset descendingMultiset() {
      return this.delegate().descendingMultiset();
   }

   public Multiset.Entry firstEntry() {
      return this.delegate().firstEntry();
   }

   protected Multiset.Entry standardFirstEntry() {
      Iterator<Multiset.Entry<E>> entryIterator = this.entrySet().iterator();
      if(!entryIterator.hasNext()) {
         return null;
      } else {
         Multiset.Entry<E> entry = (Multiset.Entry)entryIterator.next();
         return Multisets.immutableEntry(entry.getElement(), entry.getCount());
      }
   }

   public Multiset.Entry lastEntry() {
      return this.delegate().lastEntry();
   }

   protected Multiset.Entry standardLastEntry() {
      Iterator<Multiset.Entry<E>> entryIterator = this.descendingMultiset().entrySet().iterator();
      if(!entryIterator.hasNext()) {
         return null;
      } else {
         Multiset.Entry<E> entry = (Multiset.Entry)entryIterator.next();
         return Multisets.immutableEntry(entry.getElement(), entry.getCount());
      }
   }

   public Multiset.Entry pollFirstEntry() {
      return this.delegate().pollFirstEntry();
   }

   protected Multiset.Entry standardPollFirstEntry() {
      Iterator<Multiset.Entry<E>> entryIterator = this.entrySet().iterator();
      if(!entryIterator.hasNext()) {
         return null;
      } else {
         Multiset.Entry<E> entry = (Multiset.Entry)entryIterator.next();
         entry = Multisets.immutableEntry(entry.getElement(), entry.getCount());
         entryIterator.remove();
         return entry;
      }
   }

   public Multiset.Entry pollLastEntry() {
      return this.delegate().pollLastEntry();
   }

   protected Multiset.Entry standardPollLastEntry() {
      Iterator<Multiset.Entry<E>> entryIterator = this.descendingMultiset().entrySet().iterator();
      if(!entryIterator.hasNext()) {
         return null;
      } else {
         Multiset.Entry<E> entry = (Multiset.Entry)entryIterator.next();
         entry = Multisets.immutableEntry(entry.getElement(), entry.getCount());
         entryIterator.remove();
         return entry;
      }
   }

   public SortedMultiset headMultiset(Object upperBound, BoundType boundType) {
      return this.delegate().headMultiset(upperBound, boundType);
   }

   public SortedMultiset subMultiset(Object lowerBound, BoundType lowerBoundType, Object upperBound, BoundType upperBoundType) {
      return this.delegate().subMultiset(lowerBound, lowerBoundType, upperBound, upperBoundType);
   }

   protected SortedMultiset standardSubMultiset(Object lowerBound, BoundType lowerBoundType, Object upperBound, BoundType upperBoundType) {
      return this.tailMultiset(lowerBound, lowerBoundType).headMultiset(upperBound, upperBoundType);
   }

   public SortedMultiset tailMultiset(Object lowerBound, BoundType boundType) {
      return this.delegate().tailMultiset(lowerBound, boundType);
   }

   protected abstract class StandardDescendingMultiset extends DescendingMultiset {
      SortedMultiset forwardMultiset() {
         return ForwardingSortedMultiset.this;
      }
   }

   protected class StandardElementSet extends SortedMultisets.NavigableElementSet {
      public StandardElementSet() {
         super(ForwardingSortedMultiset.this);
      }
   }
}
