package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractMultiset extends AbstractCollection implements Multiset {
   private transient Set elementSet;
   private transient Set entrySet;

   public int size() {
      return Multisets.sizeImpl(this);
   }

   public boolean isEmpty() {
      return this.entrySet().isEmpty();
   }

   public boolean contains(@Nullable Object element) {
      return this.count(element) > 0;
   }

   public Iterator iterator() {
      return Multisets.iteratorImpl(this);
   }

   public int count(@Nullable Object element) {
      for(Multiset.Entry<E> entry : this.entrySet()) {
         if(Objects.equal(entry.getElement(), element)) {
            return entry.getCount();
         }
      }

      return 0;
   }

   public boolean add(@Nullable Object element) {
      this.add(element, 1);
      return true;
   }

   public int add(@Nullable Object element, int occurrences) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(@Nullable Object element) {
      return this.remove(element, 1) > 0;
   }

   public int remove(@Nullable Object element, int occurrences) {
      throw new UnsupportedOperationException();
   }

   public int setCount(@Nullable Object element, int count) {
      return Multisets.setCountImpl(this, element, count);
   }

   public boolean setCount(@Nullable Object element, int oldCount, int newCount) {
      return Multisets.setCountImpl(this, element, oldCount, newCount);
   }

   public boolean addAll(Collection elementsToAdd) {
      return Multisets.addAllImpl(this, elementsToAdd);
   }

   public boolean removeAll(Collection elementsToRemove) {
      return Multisets.removeAllImpl(this, elementsToRemove);
   }

   public boolean retainAll(Collection elementsToRetain) {
      return Multisets.retainAllImpl(this, elementsToRetain);
   }

   public void clear() {
      Iterators.clear(this.entryIterator());
   }

   public Set elementSet() {
      Set<E> result = this.elementSet;
      if(result == null) {
         this.elementSet = result = this.createElementSet();
      }

      return result;
   }

   Set createElementSet() {
      return new AbstractMultiset.ElementSet();
   }

   abstract Iterator entryIterator();

   abstract int distinctElements();

   public Set entrySet() {
      Set<Multiset.Entry<E>> result = this.entrySet;
      return result == null?(this.entrySet = this.createEntrySet()):result;
   }

   Set createEntrySet() {
      return new AbstractMultiset.EntrySet();
   }

   public boolean equals(@Nullable Object object) {
      return Multisets.equalsImpl(this, object);
   }

   public int hashCode() {
      return this.entrySet().hashCode();
   }

   public String toString() {
      return this.entrySet().toString();
   }

   class ElementSet extends Multisets.ElementSet {
      Multiset multiset() {
         return AbstractMultiset.this;
      }
   }

   class EntrySet extends Multisets.EntrySet {
      Multiset multiset() {
         return AbstractMultiset.this;
      }

      public Iterator iterator() {
         return AbstractMultiset.this.entryIterator();
      }

      public int size() {
         return AbstractMultiset.this.distinctElements();
      }
   }
}
