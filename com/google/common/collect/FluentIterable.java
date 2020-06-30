package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public abstract class FluentIterable implements Iterable {
   private final Iterable iterable;

   protected FluentIterable() {
      this.iterable = this;
   }

   FluentIterable(Iterable iterable) {
      this.iterable = (Iterable)Preconditions.checkNotNull(iterable);
   }

   public static FluentIterable from(final Iterable iterable) {
      return iterable instanceof FluentIterable?(FluentIterable)iterable:new FluentIterable(iterable) {
         public Iterator iterator() {
            return iterable.iterator();
         }
      };
   }

   /** @deprecated */
   @Deprecated
   public static FluentIterable from(FluentIterable iterable) {
      return (FluentIterable)Preconditions.checkNotNull(iterable);
   }

   public String toString() {
      return Iterables.toString(this.iterable);
   }

   public final int size() {
      return Iterables.size(this.iterable);
   }

   public final boolean contains(@Nullable Object element) {
      return Iterables.contains(this.iterable, element);
   }

   @CheckReturnValue
   public final FluentIterable cycle() {
      return from(Iterables.cycle(this.iterable));
   }

   @CheckReturnValue
   public final FluentIterable filter(Predicate predicate) {
      return from(Iterables.filter(this.iterable, predicate));
   }

   @CheckReturnValue
   @GwtIncompatible("Class.isInstance")
   public final FluentIterable filter(Class type) {
      return from(Iterables.filter(this.iterable, type));
   }

   public final boolean anyMatch(Predicate predicate) {
      return Iterables.any(this.iterable, predicate);
   }

   public final boolean allMatch(Predicate predicate) {
      return Iterables.all(this.iterable, predicate);
   }

   public final Optional firstMatch(Predicate predicate) {
      return Iterables.tryFind(this.iterable, predicate);
   }

   public final FluentIterable transform(Function function) {
      return from(Iterables.transform(this.iterable, function));
   }

   public FluentIterable transformAndConcat(Function function) {
      return from(Iterables.concat((Iterable)this.transform(function)));
   }

   public final Optional first() {
      Iterator<E> iterator = this.iterable.iterator();
      return iterator.hasNext()?Optional.of(iterator.next()):Optional.absent();
   }

   public final Optional last() {
      if(this.iterable instanceof List) {
         List<E> list = (List)this.iterable;
         return list.isEmpty()?Optional.absent():Optional.of(list.get(list.size() - 1));
      } else {
         Iterator<E> iterator = this.iterable.iterator();
         if(!iterator.hasNext()) {
            return Optional.absent();
         } else if(this.iterable instanceof SortedSet) {
            SortedSet<E> sortedSet = (SortedSet)this.iterable;
            return Optional.of(sortedSet.last());
         } else {
            E current;
            while(true) {
               current = iterator.next();
               if(!iterator.hasNext()) {
                  break;
               }
            }

            return Optional.of(current);
         }
      }
   }

   @CheckReturnValue
   public final FluentIterable skip(int numberToSkip) {
      return from(Iterables.skip(this.iterable, numberToSkip));
   }

   @CheckReturnValue
   public final FluentIterable limit(int size) {
      return from(Iterables.limit(this.iterable, size));
   }

   public final boolean isEmpty() {
      return !this.iterable.iterator().hasNext();
   }

   public final ImmutableList toList() {
      return ImmutableList.copyOf(this.iterable);
   }

   @Beta
   public final ImmutableList toSortedList(Comparator comparator) {
      return Ordering.from(comparator).immutableSortedCopy(this.iterable);
   }

   public final ImmutableSet toSet() {
      return ImmutableSet.copyOf(this.iterable);
   }

   public final ImmutableSortedSet toSortedSet(Comparator comparator) {
      return ImmutableSortedSet.copyOf(comparator, this.iterable);
   }

   public final ImmutableMap toMap(Function valueFunction) {
      return Maps.toMap(this.iterable, valueFunction);
   }

   public final ImmutableListMultimap index(Function keyFunction) {
      return Multimaps.index(this.iterable, keyFunction);
   }

   public final ImmutableMap uniqueIndex(Function keyFunction) {
      return Maps.uniqueIndex(this.iterable, keyFunction);
   }

   @GwtIncompatible("Array.newArray(Class, int)")
   public final Object[] toArray(Class type) {
      return Iterables.toArray(this.iterable, type);
   }

   public final Collection copyInto(Collection collection) {
      Preconditions.checkNotNull(collection);
      if(this.iterable instanceof Collection) {
         collection.addAll(Collections2.cast(this.iterable));
      } else {
         for(E item : this.iterable) {
            collection.add(item);
         }
      }

      return collection;
   }

   public final Object get(int position) {
      return Iterables.get(this.iterable, position);
   }

   private static class FromIterableFunction implements Function {
      public FluentIterable apply(Iterable fromObject) {
         return FluentIterable.from(fromObject);
      }
   }
}
