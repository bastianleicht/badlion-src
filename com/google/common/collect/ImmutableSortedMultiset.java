package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.DescendingImmutableSortedMultiset;
import com.google.common.collect.EmptyImmutableSortedMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSortedMultisetFauxverideShim;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.collect.RegularImmutableSortedMultiset;
import com.google.common.collect.RegularImmutableSortedSet;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Beta
@GwtIncompatible("hasn\'t been tested yet")
public abstract class ImmutableSortedMultiset extends ImmutableSortedMultisetFauxverideShim implements SortedMultiset {
   private static final Comparator NATURAL_ORDER = Ordering.natural();
   private static final ImmutableSortedMultiset NATURAL_EMPTY_MULTISET = new EmptyImmutableSortedMultiset(NATURAL_ORDER);
   transient ImmutableSortedMultiset descendingMultiset;

   public static ImmutableSortedMultiset of() {
      return NATURAL_EMPTY_MULTISET;
   }

   public static ImmutableSortedMultiset of(Comparable element) {
      RegularImmutableSortedSet<E> elementSet = (RegularImmutableSortedSet)ImmutableSortedSet.of(element);
      int[] counts = new int[]{1};
      long[] cumulativeCounts = new long[]{0L, 1L};
      return new RegularImmutableSortedMultiset(elementSet, counts, cumulativeCounts, 0, 1);
   }

   public static ImmutableSortedMultiset of(Comparable e1, Comparable e2) {
      return copyOf(Ordering.natural(), (Iterable)Arrays.asList(new Comparable[]{e1, e2}));
   }

   public static ImmutableSortedMultiset of(Comparable e1, Comparable e2, Comparable e3) {
      return copyOf(Ordering.natural(), (Iterable)Arrays.asList(new Comparable[]{e1, e2, e3}));
   }

   public static ImmutableSortedMultiset of(Comparable e1, Comparable e2, Comparable e3, Comparable e4) {
      return copyOf(Ordering.natural(), (Iterable)Arrays.asList(new Comparable[]{e1, e2, e3, e4}));
   }

   public static ImmutableSortedMultiset of(Comparable e1, Comparable e2, Comparable e3, Comparable e4, Comparable e5) {
      return copyOf(Ordering.natural(), (Iterable)Arrays.asList(new Comparable[]{e1, e2, e3, e4, e5}));
   }

   public static ImmutableSortedMultiset of(Comparable e1, Comparable e2, Comparable e3, Comparable e4, Comparable e5, Comparable e6, Comparable... remaining) {
      int size = remaining.length + 6;
      List<E> all = Lists.newArrayListWithCapacity(size);
      Collections.addAll(all, new Comparable[]{e1, e2, e3, e4, e5, e6});
      Collections.addAll(all, remaining);
      return copyOf(Ordering.natural(), (Iterable)all);
   }

   public static ImmutableSortedMultiset copyOf(Comparable[] elements) {
      return copyOf(Ordering.natural(), (Iterable)Arrays.asList(elements));
   }

   public static ImmutableSortedMultiset copyOf(Iterable elements) {
      Ordering<E> naturalOrder = Ordering.natural();
      return copyOf(naturalOrder, (Iterable)elements);
   }

   public static ImmutableSortedMultiset copyOf(Iterator elements) {
      Ordering<E> naturalOrder = Ordering.natural();
      return copyOf(naturalOrder, (Iterator)elements);
   }

   public static ImmutableSortedMultiset copyOf(Comparator comparator, Iterator elements) {
      Preconditions.checkNotNull(comparator);
      return (new ImmutableSortedMultiset.Builder(comparator)).addAll(elements).build();
   }

   public static ImmutableSortedMultiset copyOf(Comparator comparator, Iterable elements) {
      if(elements instanceof ImmutableSortedMultiset) {
         ImmutableSortedMultiset<E> multiset = (ImmutableSortedMultiset)elements;
         if(comparator.equals(multiset.comparator())) {
            if(multiset.isPartialView()) {
               return copyOfSortedEntries(comparator, multiset.entrySet().asList());
            }

            return multiset;
         }
      }

      ArrayList var3 = Lists.newArrayList(elements);
      TreeMultiset<E> sortedCopy = TreeMultiset.create((Comparator)Preconditions.checkNotNull(comparator));
      Iterables.addAll(sortedCopy, var3);
      return copyOfSortedEntries(comparator, sortedCopy.entrySet());
   }

   public static ImmutableSortedMultiset copyOfSorted(SortedMultiset sortedMultiset) {
      return copyOfSortedEntries(sortedMultiset.comparator(), Lists.newArrayList((Iterable)sortedMultiset.entrySet()));
   }

   private static ImmutableSortedMultiset copyOfSortedEntries(Comparator comparator, Collection entries) {
      if(entries.isEmpty()) {
         return emptyMultiset(comparator);
      } else {
         ImmutableList.Builder<E> elementsBuilder = new ImmutableList.Builder(entries.size());
         int[] counts = new int[entries.size()];
         long[] cumulativeCounts = new long[entries.size() + 1];
         int i = 0;

         for(Multiset.Entry<E> entry : entries) {
            elementsBuilder.add(entry.getElement());
            counts[i] = entry.getCount();
            cumulativeCounts[i + 1] = cumulativeCounts[i] + (long)counts[i];
            ++i;
         }

         return new RegularImmutableSortedMultiset(new RegularImmutableSortedSet(elementsBuilder.build(), comparator), counts, cumulativeCounts, 0, entries.size());
      }
   }

   static ImmutableSortedMultiset emptyMultiset(Comparator comparator) {
      return (ImmutableSortedMultiset)(NATURAL_ORDER.equals(comparator)?NATURAL_EMPTY_MULTISET:new EmptyImmutableSortedMultiset(comparator));
   }

   public final Comparator comparator() {
      return this.elementSet().comparator();
   }

   public abstract ImmutableSortedSet elementSet();

   public ImmutableSortedMultiset descendingMultiset() {
      ImmutableSortedMultiset<E> result = this.descendingMultiset;
      return result == null?(this.descendingMultiset = new DescendingImmutableSortedMultiset(this)):result;
   }

   /** @deprecated */
   @Deprecated
   public final Multiset.Entry pollFirstEntry() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final Multiset.Entry pollLastEntry() {
      throw new UnsupportedOperationException();
   }

   public abstract ImmutableSortedMultiset headMultiset(Object var1, BoundType var2);

   public ImmutableSortedMultiset subMultiset(Object lowerBound, BoundType lowerBoundType, Object upperBound, BoundType upperBoundType) {
      Preconditions.checkArgument(this.comparator().compare(lowerBound, upperBound) <= 0, "Expected lowerBound <= upperBound but %s > %s", new Object[]{lowerBound, upperBound});
      return this.tailMultiset(lowerBound, lowerBoundType).headMultiset(upperBound, upperBoundType);
   }

   public abstract ImmutableSortedMultiset tailMultiset(Object var1, BoundType var2);

   public static ImmutableSortedMultiset.Builder orderedBy(Comparator comparator) {
      return new ImmutableSortedMultiset.Builder(comparator);
   }

   public static ImmutableSortedMultiset.Builder reverseOrder() {
      return new ImmutableSortedMultiset.Builder(Ordering.natural().reverse());
   }

   public static ImmutableSortedMultiset.Builder naturalOrder() {
      return new ImmutableSortedMultiset.Builder(Ordering.natural());
   }

   Object writeReplace() {
      return new ImmutableSortedMultiset.SerializedForm(this);
   }

   public static class Builder extends ImmutableMultiset.Builder {
      public Builder(Comparator comparator) {
         super(TreeMultiset.create((Comparator)Preconditions.checkNotNull(comparator)));
      }

      public ImmutableSortedMultiset.Builder add(Object element) {
         super.add(element);
         return this;
      }

      public ImmutableSortedMultiset.Builder addCopies(Object element, int occurrences) {
         super.addCopies(element, occurrences);
         return this;
      }

      public ImmutableSortedMultiset.Builder setCount(Object element, int count) {
         super.setCount(element, count);
         return this;
      }

      public ImmutableSortedMultiset.Builder add(Object... elements) {
         super.add(elements);
         return this;
      }

      public ImmutableSortedMultiset.Builder addAll(Iterable elements) {
         super.addAll(elements);
         return this;
      }

      public ImmutableSortedMultiset.Builder addAll(Iterator elements) {
         super.addAll(elements);
         return this;
      }

      public ImmutableSortedMultiset build() {
         return ImmutableSortedMultiset.copyOfSorted((SortedMultiset)this.contents);
      }
   }

   private static final class SerializedForm implements Serializable {
      Comparator comparator;
      Object[] elements;
      int[] counts;

      SerializedForm(SortedMultiset multiset) {
         this.comparator = multiset.comparator();
         int n = multiset.entrySet().size();
         this.elements = (Object[])(new Object[n]);
         this.counts = new int[n];
         int i = 0;

         for(Multiset.Entry<E> entry : multiset.entrySet()) {
            this.elements[i] = entry.getElement();
            this.counts[i] = entry.getCount();
            ++i;
         }

      }

      Object readResolve() {
         int n = this.elements.length;
         ImmutableSortedMultiset.Builder<E> builder = new ImmutableSortedMultiset.Builder(this.comparator);

         for(int i = 0; i < n; ++i) {
            builder.addCopies(this.elements[i], this.counts[i]);
         }

         return builder.build();
      }
   }
}
