package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Constraint;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ForwardingListIterator;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ForwardingSortedSet;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedSet;

@GwtCompatible
final class Constraints {
   public static Collection constrainedCollection(Collection collection, Constraint constraint) {
      return new Constraints.ConstrainedCollection(collection, constraint);
   }

   public static Set constrainedSet(Set set, Constraint constraint) {
      return new Constraints.ConstrainedSet(set, constraint);
   }

   public static SortedSet constrainedSortedSet(SortedSet sortedSet, Constraint constraint) {
      return new Constraints.ConstrainedSortedSet(sortedSet, constraint);
   }

   public static List constrainedList(List list, Constraint constraint) {
      return (List)(list instanceof RandomAccess?new Constraints.ConstrainedRandomAccessList(list, constraint):new Constraints.ConstrainedList(list, constraint));
   }

   private static ListIterator constrainedListIterator(ListIterator listIterator, Constraint constraint) {
      return new Constraints.ConstrainedListIterator(listIterator, constraint);
   }

   static Collection constrainedTypePreservingCollection(Collection collection, Constraint constraint) {
      return (Collection)(collection instanceof SortedSet?constrainedSortedSet((SortedSet)collection, constraint):(collection instanceof Set?constrainedSet((Set)collection, constraint):(collection instanceof List?constrainedList((List)collection, constraint):constrainedCollection(collection, constraint))));
   }

   private static Collection checkElements(Collection elements, Constraint constraint) {
      Collection<E> copy = Lists.newArrayList((Iterable)elements);

      for(E element : copy) {
         constraint.checkElement(element);
      }

      return copy;
   }

   static class ConstrainedCollection extends ForwardingCollection {
      private final Collection delegate;
      private final Constraint constraint;

      public ConstrainedCollection(Collection delegate, Constraint constraint) {
         this.delegate = (Collection)Preconditions.checkNotNull(delegate);
         this.constraint = (Constraint)Preconditions.checkNotNull(constraint);
      }

      protected Collection delegate() {
         return this.delegate;
      }

      public boolean add(Object element) {
         this.constraint.checkElement(element);
         return this.delegate.add(element);
      }

      public boolean addAll(Collection elements) {
         return this.delegate.addAll(Constraints.checkElements(elements, this.constraint));
      }
   }

   @GwtCompatible
   private static class ConstrainedList extends ForwardingList {
      final List delegate;
      final Constraint constraint;

      ConstrainedList(List delegate, Constraint constraint) {
         this.delegate = (List)Preconditions.checkNotNull(delegate);
         this.constraint = (Constraint)Preconditions.checkNotNull(constraint);
      }

      protected List delegate() {
         return this.delegate;
      }

      public boolean add(Object element) {
         this.constraint.checkElement(element);
         return this.delegate.add(element);
      }

      public void add(int index, Object element) {
         this.constraint.checkElement(element);
         this.delegate.add(index, element);
      }

      public boolean addAll(Collection elements) {
         return this.delegate.addAll(Constraints.checkElements(elements, this.constraint));
      }

      public boolean addAll(int index, Collection elements) {
         return this.delegate.addAll(index, Constraints.checkElements(elements, this.constraint));
      }

      public ListIterator listIterator() {
         return Constraints.constrainedListIterator(this.delegate.listIterator(), this.constraint);
      }

      public ListIterator listIterator(int index) {
         return Constraints.constrainedListIterator(this.delegate.listIterator(index), this.constraint);
      }

      public Object set(int index, Object element) {
         this.constraint.checkElement(element);
         return this.delegate.set(index, element);
      }

      public List subList(int fromIndex, int toIndex) {
         return Constraints.constrainedList(this.delegate.subList(fromIndex, toIndex), this.constraint);
      }
   }

   static class ConstrainedListIterator extends ForwardingListIterator {
      private final ListIterator delegate;
      private final Constraint constraint;

      public ConstrainedListIterator(ListIterator delegate, Constraint constraint) {
         this.delegate = delegate;
         this.constraint = constraint;
      }

      protected ListIterator delegate() {
         return this.delegate;
      }

      public void add(Object element) {
         this.constraint.checkElement(element);
         this.delegate.add(element);
      }

      public void set(Object element) {
         this.constraint.checkElement(element);
         this.delegate.set(element);
      }
   }

   static class ConstrainedRandomAccessList extends Constraints.ConstrainedList implements RandomAccess {
      ConstrainedRandomAccessList(List delegate, Constraint constraint) {
         super(delegate, constraint);
      }
   }

   static class ConstrainedSet extends ForwardingSet {
      private final Set delegate;
      private final Constraint constraint;

      public ConstrainedSet(Set delegate, Constraint constraint) {
         this.delegate = (Set)Preconditions.checkNotNull(delegate);
         this.constraint = (Constraint)Preconditions.checkNotNull(constraint);
      }

      protected Set delegate() {
         return this.delegate;
      }

      public boolean add(Object element) {
         this.constraint.checkElement(element);
         return this.delegate.add(element);
      }

      public boolean addAll(Collection elements) {
         return this.delegate.addAll(Constraints.checkElements(elements, this.constraint));
      }
   }

   private static class ConstrainedSortedSet extends ForwardingSortedSet {
      final SortedSet delegate;
      final Constraint constraint;

      ConstrainedSortedSet(SortedSet delegate, Constraint constraint) {
         this.delegate = (SortedSet)Preconditions.checkNotNull(delegate);
         this.constraint = (Constraint)Preconditions.checkNotNull(constraint);
      }

      protected SortedSet delegate() {
         return this.delegate;
      }

      public SortedSet headSet(Object toElement) {
         return Constraints.constrainedSortedSet(this.delegate.headSet(toElement), this.constraint);
      }

      public SortedSet subSet(Object fromElement, Object toElement) {
         return Constraints.constrainedSortedSet(this.delegate.subSet(fromElement, toElement), this.constraint);
      }

      public SortedSet tailSet(Object fromElement) {
         return Constraints.constrainedSortedSet(this.delegate.tailSet(fromElement), this.constraint);
      }

      public boolean add(Object element) {
         this.constraint.checkElement(element);
         return this.delegate.add(element);
      }

      public boolean addAll(Collection elements) {
         return this.delegate.addAll(Constraints.checkElements(elements, this.constraint));
      }
   }
}
