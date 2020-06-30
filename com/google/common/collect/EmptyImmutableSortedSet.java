package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
class EmptyImmutableSortedSet extends ImmutableSortedSet {
   EmptyImmutableSortedSet(Comparator comparator) {
      super(comparator);
   }

   public int size() {
      return 0;
   }

   public boolean isEmpty() {
      return true;
   }

   public boolean contains(@Nullable Object target) {
      return false;
   }

   public boolean containsAll(Collection targets) {
      return targets.isEmpty();
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

   public ImmutableList asList() {
      return ImmutableList.of();
   }

   int copyIntoArray(Object[] dst, int offset) {
      return offset;
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

   public String toString() {
      return "[]";
   }

   public Object first() {
      throw new NoSuchElementException();
   }

   public Object last() {
      throw new NoSuchElementException();
   }

   ImmutableSortedSet headSetImpl(Object toElement, boolean inclusive) {
      return this;
   }

   ImmutableSortedSet subSetImpl(Object fromElement, boolean fromInclusive, Object toElement, boolean toInclusive) {
      return this;
   }

   ImmutableSortedSet tailSetImpl(Object fromElement, boolean inclusive) {
      return this;
   }

   int indexOf(@Nullable Object target) {
      return -1;
   }

   ImmutableSortedSet createDescendingSet() {
      return new EmptyImmutableSortedSet(Ordering.from(this.comparator).reverse());
   }
}
