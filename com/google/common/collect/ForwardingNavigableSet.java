package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.collect.ForwardingSortedSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;

public abstract class ForwardingNavigableSet extends ForwardingSortedSet implements NavigableSet {
   protected abstract NavigableSet delegate();

   public Object lower(Object e) {
      return this.delegate().lower(e);
   }

   protected Object standardLower(Object e) {
      return Iterators.getNext(this.headSet(e, false).descendingIterator(), (Object)null);
   }

   public Object floor(Object e) {
      return this.delegate().floor(e);
   }

   protected Object standardFloor(Object e) {
      return Iterators.getNext(this.headSet(e, true).descendingIterator(), (Object)null);
   }

   public Object ceiling(Object e) {
      return this.delegate().ceiling(e);
   }

   protected Object standardCeiling(Object e) {
      return Iterators.getNext(this.tailSet(e, true).iterator(), (Object)null);
   }

   public Object higher(Object e) {
      return this.delegate().higher(e);
   }

   protected Object standardHigher(Object e) {
      return Iterators.getNext(this.tailSet(e, false).iterator(), (Object)null);
   }

   public Object pollFirst() {
      return this.delegate().pollFirst();
   }

   protected Object standardPollFirst() {
      return Iterators.pollNext(this.iterator());
   }

   public Object pollLast() {
      return this.delegate().pollLast();
   }

   protected Object standardPollLast() {
      return Iterators.pollNext(this.descendingIterator());
   }

   protected Object standardFirst() {
      return this.iterator().next();
   }

   protected Object standardLast() {
      return this.descendingIterator().next();
   }

   public NavigableSet descendingSet() {
      return this.delegate().descendingSet();
   }

   public Iterator descendingIterator() {
      return this.delegate().descendingIterator();
   }

   public NavigableSet subSet(Object fromElement, boolean fromInclusive, Object toElement, boolean toInclusive) {
      return this.delegate().subSet(fromElement, fromInclusive, toElement, toInclusive);
   }

   @Beta
   protected NavigableSet standardSubSet(Object fromElement, boolean fromInclusive, Object toElement, boolean toInclusive) {
      return this.tailSet(fromElement, fromInclusive).headSet(toElement, toInclusive);
   }

   protected SortedSet standardSubSet(Object fromElement, Object toElement) {
      return this.subSet(fromElement, true, toElement, false);
   }

   public NavigableSet headSet(Object toElement, boolean inclusive) {
      return this.delegate().headSet(toElement, inclusive);
   }

   protected SortedSet standardHeadSet(Object toElement) {
      return this.headSet(toElement, false);
   }

   public NavigableSet tailSet(Object fromElement, boolean inclusive) {
      return this.delegate().tailSet(fromElement, inclusive);
   }

   protected SortedSet standardTailSet(Object fromElement) {
      return this.tailSet(fromElement, true);
   }

   @Beta
   protected class StandardDescendingSet extends Sets.DescendingSet {
      public StandardDescendingSet() {
         super(ForwardingNavigableSet.this);
      }
   }
}
