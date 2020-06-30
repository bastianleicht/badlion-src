package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedAsList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.SortedIterables;
import com.google.common.collect.SortedLists;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
final class RegularImmutableSortedSet extends ImmutableSortedSet {
   private final transient ImmutableList elements;

   RegularImmutableSortedSet(ImmutableList elements, Comparator comparator) {
      super(comparator);
      this.elements = elements;
      Preconditions.checkArgument(!elements.isEmpty());
   }

   public UnmodifiableIterator iterator() {
      return this.elements.iterator();
   }

   @GwtIncompatible("NavigableSet")
   public UnmodifiableIterator descendingIterator() {
      return this.elements.reverse().iterator();
   }

   public boolean isEmpty() {
      return false;
   }

   public int size() {
      return this.elements.size();
   }

   public boolean contains(Object o) {
      try {
         return o != null && this.unsafeBinarySearch(o) >= 0;
      } catch (ClassCastException var3) {
         return false;
      }
   }

   public boolean containsAll(Collection targets) {
      if(targets instanceof Multiset) {
         targets = ((Multiset)targets).elementSet();
      }

      if(SortedIterables.hasSameComparator(this.comparator(), (Iterable)targets) && ((Collection)targets).size() > 1) {
         PeekingIterator<E> thisIterator = Iterators.peekingIterator((Iterator)this.iterator());
         Iterator<?> thatIterator = ((Collection)targets).iterator();
         Object target = thatIterator.next();

         try {
            while(thisIterator.hasNext()) {
               int cmp = this.unsafeCompare(thisIterator.peek(), target);
               if(cmp < 0) {
                  thisIterator.next();
               } else if(cmp == 0) {
                  if(!thatIterator.hasNext()) {
                     return true;
                  }

                  target = thatIterator.next();
               } else if(cmp > 0) {
                  return false;
               }
            }

            return false;
         } catch (NullPointerException var6) {
            return false;
         } catch (ClassCastException var7) {
            return false;
         }
      } else {
         return super.containsAll((Collection)targets);
      }
   }

   private int unsafeBinarySearch(Object key) throws ClassCastException {
      return Collections.binarySearch(this.elements, key, this.unsafeComparator());
   }

   boolean isPartialView() {
      return this.elements.isPartialView();
   }

   int copyIntoArray(Object[] dst, int offset) {
      return this.elements.copyIntoArray(dst, offset);
   }

   public boolean equals(@Nullable Object object) {
      if(object == this) {
         return true;
      } else if(!(object instanceof Set)) {
         return false;
      } else {
         Set<?> that = (Set)object;
         if(this.size() != that.size()) {
            return false;
         } else if(SortedIterables.hasSameComparator(this.comparator, that)) {
            Iterator<?> otherIterator = that.iterator();

            try {
               Iterator<E> iterator = this.iterator();

               while(((Iterator)iterator).hasNext()) {
                  Object element = iterator.next();
                  Object otherElement = otherIterator.next();
                  if(otherElement == null || this.unsafeCompare(element, otherElement) != 0) {
                     return false;
                  }
               }

               return true;
            } catch (ClassCastException var7) {
               return false;
            } catch (NoSuchElementException var8) {
               return false;
            }
         } else {
            return this.containsAll(that);
         }
      }
   }

   public Object first() {
      return this.elements.get(0);
   }

   public Object last() {
      return this.elements.get(this.size() - 1);
   }

   public Object lower(Object element) {
      int index = this.headIndex(element, false) - 1;
      return index == -1?null:this.elements.get(index);
   }

   public Object floor(Object element) {
      int index = this.headIndex(element, true) - 1;
      return index == -1?null:this.elements.get(index);
   }

   public Object ceiling(Object element) {
      int index = this.tailIndex(element, true);
      return index == this.size()?null:this.elements.get(index);
   }

   public Object higher(Object element) {
      int index = this.tailIndex(element, false);
      return index == this.size()?null:this.elements.get(index);
   }

   ImmutableSortedSet headSetImpl(Object toElement, boolean inclusive) {
      return this.getSubSet(0, this.headIndex(toElement, inclusive));
   }

   int headIndex(Object toElement, boolean inclusive) {
      return SortedLists.binarySearch(this.elements, (Object)Preconditions.checkNotNull(toElement), (Comparator)this.comparator(), inclusive?SortedLists.KeyPresentBehavior.FIRST_AFTER:SortedLists.KeyPresentBehavior.FIRST_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
   }

   ImmutableSortedSet subSetImpl(Object fromElement, boolean fromInclusive, Object toElement, boolean toInclusive) {
      return this.tailSetImpl(fromElement, fromInclusive).headSetImpl(toElement, toInclusive);
   }

   ImmutableSortedSet tailSetImpl(Object fromElement, boolean inclusive) {
      return this.getSubSet(this.tailIndex(fromElement, inclusive), this.size());
   }

   int tailIndex(Object fromElement, boolean inclusive) {
      return SortedLists.binarySearch(this.elements, (Object)Preconditions.checkNotNull(fromElement), (Comparator)this.comparator(), inclusive?SortedLists.KeyPresentBehavior.FIRST_PRESENT:SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
   }

   Comparator unsafeComparator() {
      return this.comparator;
   }

   ImmutableSortedSet getSubSet(int newFromIndex, int newToIndex) {
      return (ImmutableSortedSet)(newFromIndex == 0 && newToIndex == this.size()?this:(newFromIndex < newToIndex?new RegularImmutableSortedSet(this.elements.subList(newFromIndex, newToIndex), this.comparator):emptySet(this.comparator)));
   }

   int indexOf(@Nullable Object target) {
      if(target == null) {
         return -1;
      } else {
         int position;
         try {
            position = SortedLists.binarySearch(this.elements, (Object)target, (Comparator)this.unsafeComparator(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.INVERTED_INSERTION_INDEX);
         } catch (ClassCastException var4) {
            return -1;
         }

         return position >= 0?position:-1;
      }
   }

   ImmutableList createAsList() {
      return new ImmutableSortedAsList(this, this.elements);
   }

   ImmutableSortedSet createDescendingSet() {
      return new RegularImmutableSortedSet(this.elements.reverse(), Ordering.from(this.comparator).reverse());
   }
}
