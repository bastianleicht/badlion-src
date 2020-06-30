package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingMultiset extends ForwardingCollection implements Multiset {
   protected abstract Multiset delegate();

   public int count(Object element) {
      return this.delegate().count(element);
   }

   public int add(Object element, int occurrences) {
      return this.delegate().add(element, occurrences);
   }

   public int remove(Object element, int occurrences) {
      return this.delegate().remove(element, occurrences);
   }

   public Set elementSet() {
      return this.delegate().elementSet();
   }

   public Set entrySet() {
      return this.delegate().entrySet();
   }

   public boolean equals(@Nullable Object object) {
      return object == this || this.delegate().equals(object);
   }

   public int hashCode() {
      return this.delegate().hashCode();
   }

   public int setCount(Object element, int count) {
      return this.delegate().setCount(element, count);
   }

   public boolean setCount(Object element, int oldCount, int newCount) {
      return this.delegate().setCount(element, oldCount, newCount);
   }

   protected boolean standardContains(@Nullable Object object) {
      return this.count(object) > 0;
   }

   protected void standardClear() {
      Iterators.clear(this.entrySet().iterator());
   }

   @Beta
   protected int standardCount(@Nullable Object object) {
      for(Multiset.Entry<?> entry : this.entrySet()) {
         if(Objects.equal(entry.getElement(), object)) {
            return entry.getCount();
         }
      }

      return 0;
   }

   protected boolean standardAdd(Object element) {
      this.add(element, 1);
      return true;
   }

   @Beta
   protected boolean standardAddAll(Collection elementsToAdd) {
      return Multisets.addAllImpl(this, elementsToAdd);
   }

   protected boolean standardRemove(Object element) {
      return this.remove(element, 1) > 0;
   }

   protected boolean standardRemoveAll(Collection elementsToRemove) {
      return Multisets.removeAllImpl(this, elementsToRemove);
   }

   protected boolean standardRetainAll(Collection elementsToRetain) {
      return Multisets.retainAllImpl(this, elementsToRetain);
   }

   protected int standardSetCount(Object element, int count) {
      return Multisets.setCountImpl(this, element, count);
   }

   protected boolean standardSetCount(Object element, int oldCount, int newCount) {
      return Multisets.setCountImpl(this, element, oldCount, newCount);
   }

   protected Iterator standardIterator() {
      return Multisets.iteratorImpl(this);
   }

   protected int standardSize() {
      return Multisets.sizeImpl(this);
   }

   protected boolean standardEquals(@Nullable Object object) {
      return Multisets.equalsImpl(this, object);
   }

   protected int standardHashCode() {
      return this.entrySet().hashCode();
   }

   protected String standardToString() {
      return this.entrySet().toString();
   }

   @Beta
   protected class StandardElementSet extends Multisets.ElementSet {
      Multiset multiset() {
         return ForwardingMultiset.this;
      }
   }
}
