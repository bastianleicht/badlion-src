package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.BoundType;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.EmptyImmutableSortedSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class EmptyContiguousSet extends ContiguousSet {
   EmptyContiguousSet(DiscreteDomain domain) {
      super(domain);
   }

   public Comparable first() {
      throw new NoSuchElementException();
   }

   public Comparable last() {
      throw new NoSuchElementException();
   }

   public int size() {
      return 0;
   }

   public ContiguousSet intersection(ContiguousSet other) {
      return this;
   }

   public Range range() {
      throw new NoSuchElementException();
   }

   public Range range(BoundType lowerBoundType, BoundType upperBoundType) {
      throw new NoSuchElementException();
   }

   ContiguousSet headSetImpl(Comparable toElement, boolean inclusive) {
      return this;
   }

   ContiguousSet subSetImpl(Comparable fromElement, boolean fromInclusive, Comparable toElement, boolean toInclusive) {
      return this;
   }

   ContiguousSet tailSetImpl(Comparable fromElement, boolean fromInclusive) {
      return this;
   }

   @GwtIncompatible("not used by GWT emulation")
   int indexOf(Object target) {
      return -1;
   }

   public UnmodifiableIterator iterator() {
      return Iterators.emptyIterator();
   }

   @GwtIncompatible("NavigableSet")
   public UnmodifiableIterator descendingIterator() {
      return Iterators.emptyIterator();
   }

   boolean isPartialView() {
      return false;
   }

   public boolean isEmpty() {
      return true;
   }

   public ImmutableList asList() {
      return ImmutableList.of();
   }

   public String toString() {
      return "[]";
   }

   public boolean equals(@Nullable Object object) {
      if(object instanceof Set) {
         Set<?> that = (Set)object;
         return that.isEmpty();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 0;
   }

   @GwtIncompatible("serialization")
   Object writeReplace() {
      return new EmptyContiguousSet.SerializedForm(this.domain);
   }

   @GwtIncompatible("NavigableSet")
   ImmutableSortedSet createDescendingSet() {
      return new EmptyImmutableSortedSet(Ordering.natural().reverse());
   }

   @GwtIncompatible("serialization")
   private static final class SerializedForm implements Serializable {
      private final DiscreteDomain domain;
      private static final long serialVersionUID = 0L;

      private SerializedForm(DiscreteDomain domain) {
         this.domain = domain;
      }

      private Object readResolve() {
         return new EmptyContiguousSet(this.domain);
      }
   }
}
