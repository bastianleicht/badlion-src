package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.TransformedIterator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class Iterables {
   public static Iterable unmodifiableIterable(Iterable iterable) {
      Preconditions.checkNotNull(iterable);
      return (Iterable)(!(iterable instanceof Iterables.UnmodifiableIterable) && !(iterable instanceof ImmutableCollection)?new Iterables.UnmodifiableIterable(iterable):iterable);
   }

   /** @deprecated */
   @Deprecated
   public static Iterable unmodifiableIterable(ImmutableCollection iterable) {
      return (Iterable)Preconditions.checkNotNull(iterable);
   }

   public static int size(Iterable iterable) {
      return iterable instanceof Collection?((Collection)iterable).size():Iterators.size(iterable.iterator());
   }

   public static boolean contains(Iterable iterable, @Nullable Object element) {
      if(iterable instanceof Collection) {
         Collection<?> collection = (Collection)iterable;
         return Collections2.safeContains(collection, element);
      } else {
         return Iterators.contains(iterable.iterator(), element);
      }
   }

   public static boolean removeAll(Iterable removeFrom, Collection elementsToRemove) {
      return removeFrom instanceof Collection?((Collection)removeFrom).removeAll((Collection)Preconditions.checkNotNull(elementsToRemove)):Iterators.removeAll(removeFrom.iterator(), elementsToRemove);
   }

   public static boolean retainAll(Iterable removeFrom, Collection elementsToRetain) {
      return removeFrom instanceof Collection?((Collection)removeFrom).retainAll((Collection)Preconditions.checkNotNull(elementsToRetain)):Iterators.retainAll(removeFrom.iterator(), elementsToRetain);
   }

   public static boolean removeIf(Iterable removeFrom, Predicate predicate) {
      return removeFrom instanceof RandomAccess && removeFrom instanceof List?removeIfFromRandomAccessList((List)removeFrom, (Predicate)Preconditions.checkNotNull(predicate)):Iterators.removeIf(removeFrom.iterator(), predicate);
   }

   private static boolean removeIfFromRandomAccessList(List list, Predicate predicate) {
      int from = 0;

      int to;
      for(to = 0; from < list.size(); ++from) {
         T element = list.get(from);
         if(!predicate.apply(element)) {
            if(from > to) {
               try {
                  list.set(to, element);
               } catch (UnsupportedOperationException var6) {
                  slowRemoveIfForRemainingElements(list, predicate, to, from);
                  return true;
               }
            }

            ++to;
         }
      }

      list.subList(to, list.size()).clear();
      return from != to;
   }

   private static void slowRemoveIfForRemainingElements(List list, Predicate predicate, int to, int from) {
      for(int n = list.size() - 1; n > from; --n) {
         if(predicate.apply(list.get(n))) {
            list.remove(n);
         }
      }

      for(int n = from - 1; n >= to; --n) {
         list.remove(n);
      }

   }

   @Nullable
   static Object removeFirstMatching(Iterable removeFrom, Predicate predicate) {
      Preconditions.checkNotNull(predicate);
      Iterator<T> iterator = removeFrom.iterator();

      while(iterator.hasNext()) {
         T next = iterator.next();
         if(predicate.apply(next)) {
            iterator.remove();
            return next;
         }
      }

      return null;
   }

   public static boolean elementsEqual(Iterable iterable1, Iterable iterable2) {
      if(iterable1 instanceof Collection && iterable2 instanceof Collection) {
         Collection<?> collection1 = (Collection)iterable1;
         Collection<?> collection2 = (Collection)iterable2;
         if(collection1.size() != collection2.size()) {
            return false;
         }
      }

      return Iterators.elementsEqual(iterable1.iterator(), iterable2.iterator());
   }

   public static String toString(Iterable iterable) {
      return Iterators.toString(iterable.iterator());
   }

   public static Object getOnlyElement(Iterable iterable) {
      return Iterators.getOnlyElement(iterable.iterator());
   }

   @Nullable
   public static Object getOnlyElement(Iterable iterable, @Nullable Object defaultValue) {
      return Iterators.getOnlyElement(iterable.iterator(), defaultValue);
   }

   @GwtIncompatible("Array.newInstance(Class, int)")
   public static Object[] toArray(Iterable iterable, Class type) {
      Collection<? extends T> collection = toCollection(iterable);
      T[] array = ObjectArrays.newArray(type, collection.size());
      return collection.toArray(array);
   }

   static Object[] toArray(Iterable iterable) {
      return toCollection(iterable).toArray();
   }

   private static Collection toCollection(Iterable iterable) {
      return (Collection)(iterable instanceof Collection?(Collection)iterable:Lists.newArrayList(iterable.iterator()));
   }

   public static boolean addAll(Collection addTo, Iterable elementsToAdd) {
      if(elementsToAdd instanceof Collection) {
         Collection<? extends T> c = Collections2.cast(elementsToAdd);
         return addTo.addAll(c);
      } else {
         return Iterators.addAll(addTo, ((Iterable)Preconditions.checkNotNull(elementsToAdd)).iterator());
      }
   }

   public static int frequency(Iterable iterable, @Nullable Object element) {
      return iterable instanceof Multiset?((Multiset)iterable).count(element):(iterable instanceof Set?(((Set)iterable).contains(element)?1:0):Iterators.frequency(iterable.iterator(), element));
   }

   public static Iterable cycle(final Iterable iterable) {
      Preconditions.checkNotNull(iterable);
      return new FluentIterable() {
         public Iterator iterator() {
            return Iterators.cycle(iterable);
         }

         public String toString() {
            return iterable.toString() + " (cycled)";
         }
      };
   }

   public static Iterable cycle(Object... elements) {
      return cycle((Iterable)Lists.newArrayList(elements));
   }

   public static Iterable concat(Iterable a, Iterable b) {
      return concat((Iterable)ImmutableList.of(a, b));
   }

   public static Iterable concat(Iterable a, Iterable b, Iterable c) {
      return concat((Iterable)ImmutableList.of(a, b, c));
   }

   public static Iterable concat(Iterable a, Iterable b, Iterable c, Iterable d) {
      return concat((Iterable)ImmutableList.of(a, b, c, d));
   }

   public static Iterable concat(Iterable... inputs) {
      return concat((Iterable)ImmutableList.copyOf((Object[])inputs));
   }

   public static Iterable concat(final Iterable inputs) {
      Preconditions.checkNotNull(inputs);
      return new FluentIterable() {
         public Iterator iterator() {
            return Iterators.concat(Iterables.iterators(inputs));
         }
      };
   }

   private static Iterator iterators(Iterable iterables) {
      return new TransformedIterator(iterables.iterator()) {
         Iterator transform(Iterable from) {
            return from.iterator();
         }
      };
   }

   public static Iterable partition(final Iterable iterable, final int size) {
      Preconditions.checkNotNull(iterable);
      Preconditions.checkArgument(size > 0);
      return new FluentIterable() {
         public Iterator iterator() {
            return Iterators.partition(iterable.iterator(), size);
         }
      };
   }

   public static Iterable paddedPartition(final Iterable iterable, final int size) {
      Preconditions.checkNotNull(iterable);
      Preconditions.checkArgument(size > 0);
      return new FluentIterable() {
         public Iterator iterator() {
            return Iterators.paddedPartition(iterable.iterator(), size);
         }
      };
   }

   public static Iterable filter(final Iterable unfiltered, final Predicate predicate) {
      Preconditions.checkNotNull(unfiltered);
      Preconditions.checkNotNull(predicate);
      return new FluentIterable() {
         public Iterator iterator() {
            return Iterators.filter(unfiltered.iterator(), predicate);
         }
      };
   }

   @GwtIncompatible("Class.isInstance")
   public static Iterable filter(final Iterable unfiltered, final Class type) {
      Preconditions.checkNotNull(unfiltered);
      Preconditions.checkNotNull(type);
      return new FluentIterable() {
         public Iterator iterator() {
            return Iterators.filter(unfiltered.iterator(), type);
         }
      };
   }

   public static boolean any(Iterable iterable, Predicate predicate) {
      return Iterators.any(iterable.iterator(), predicate);
   }

   public static boolean all(Iterable iterable, Predicate predicate) {
      return Iterators.all(iterable.iterator(), predicate);
   }

   public static Object find(Iterable iterable, Predicate predicate) {
      return Iterators.find(iterable.iterator(), predicate);
   }

   @Nullable
   public static Object find(Iterable iterable, Predicate predicate, @Nullable Object defaultValue) {
      return Iterators.find(iterable.iterator(), predicate, defaultValue);
   }

   public static Optional tryFind(Iterable iterable, Predicate predicate) {
      return Iterators.tryFind(iterable.iterator(), predicate);
   }

   public static int indexOf(Iterable iterable, Predicate predicate) {
      return Iterators.indexOf(iterable.iterator(), predicate);
   }

   public static Iterable transform(final Iterable fromIterable, final Function function) {
      Preconditions.checkNotNull(fromIterable);
      Preconditions.checkNotNull(function);
      return new FluentIterable() {
         public Iterator iterator() {
            return Iterators.transform(fromIterable.iterator(), function);
         }
      };
   }

   public static Object get(Iterable iterable, int position) {
      Preconditions.checkNotNull(iterable);
      return iterable instanceof List?((List)iterable).get(position):Iterators.get(iterable.iterator(), position);
   }

   @Nullable
   public static Object get(Iterable iterable, int position, @Nullable Object defaultValue) {
      Preconditions.checkNotNull(iterable);
      Iterators.checkNonnegative(position);
      if(iterable instanceof List) {
         List<? extends T> list = Lists.cast(iterable);
         return position < list.size()?list.get(position):defaultValue;
      } else {
         Iterator<? extends T> iterator = iterable.iterator();
         Iterators.advance(iterator, position);
         return Iterators.getNext(iterator, defaultValue);
      }
   }

   @Nullable
   public static Object getFirst(Iterable iterable, @Nullable Object defaultValue) {
      return Iterators.getNext(iterable.iterator(), defaultValue);
   }

   public static Object getLast(Iterable iterable) {
      if(iterable instanceof List) {
         List<T> list = (List)iterable;
         if(list.isEmpty()) {
            throw new NoSuchElementException();
         } else {
            return getLastInNonemptyList(list);
         }
      } else {
         return Iterators.getLast(iterable.iterator());
      }
   }

   @Nullable
   public static Object getLast(Iterable iterable, @Nullable Object defaultValue) {
      if(iterable instanceof Collection) {
         Collection<? extends T> c = Collections2.cast(iterable);
         if(c.isEmpty()) {
            return defaultValue;
         }

         if(iterable instanceof List) {
            return getLastInNonemptyList(Lists.cast(iterable));
         }
      }

      return Iterators.getLast(iterable.iterator(), defaultValue);
   }

   private static Object getLastInNonemptyList(List list) {
      return list.get(list.size() - 1);
   }

   public static Iterable skip(final Iterable iterable, final int numberToSkip) {
      Preconditions.checkNotNull(iterable);
      Preconditions.checkArgument(numberToSkip >= 0, "number to skip cannot be negative");
      if(iterable instanceof List) {
         final List<T> list = (List)iterable;
         return new FluentIterable() {
            public Iterator iterator() {
               int toSkip = Math.min(list.size(), numberToSkip);
               return list.subList(toSkip, list.size()).iterator();
            }
         };
      } else {
         return new FluentIterable() {
            public Iterator iterator() {
               final Iterator<T> iterator = iterable.iterator();
               Iterators.advance(iterator, numberToSkip);
               return new Iterator() {
                  boolean atStart = true;

                  public boolean hasNext() {
                     return iterator.hasNext();
                  }

                  public Object next() {
                     T result = iterator.next();
                     this.atStart = false;
                     return result;
                  }

                  public void remove() {
                     CollectPreconditions.checkRemove(!this.atStart);
                     iterator.remove();
                  }
               };
            }
         };
      }
   }

   public static Iterable limit(final Iterable iterable, final int limitSize) {
      Preconditions.checkNotNull(iterable);
      Preconditions.checkArgument(limitSize >= 0, "limit is negative");
      return new FluentIterable() {
         public Iterator iterator() {
            return Iterators.limit(iterable.iterator(), limitSize);
         }
      };
   }

   public static Iterable consumingIterable(final Iterable iterable) {
      if(iterable instanceof Queue) {
         return new FluentIterable() {
            public Iterator iterator() {
               return new Iterables.ConsumingQueueIterator((Queue)iterable);
            }

            public String toString() {
               return "Iterables.consumingIterable(...)";
            }
         };
      } else {
         Preconditions.checkNotNull(iterable);
         return new FluentIterable() {
            public Iterator iterator() {
               return Iterators.consumingIterator(iterable.iterator());
            }

            public String toString() {
               return "Iterables.consumingIterable(...)";
            }
         };
      }
   }

   public static boolean isEmpty(Iterable iterable) {
      return iterable instanceof Collection?((Collection)iterable).isEmpty():!iterable.iterator().hasNext();
   }

   @Beta
   public static Iterable mergeSorted(final Iterable iterables, final Comparator comparator) {
      Preconditions.checkNotNull(iterables, "iterables");
      Preconditions.checkNotNull(comparator, "comparator");
      Iterable<T> iterable = new FluentIterable() {
         public Iterator iterator() {
            return Iterators.mergeSorted(Iterables.transform(iterables, Iterables.toIterator()), comparator);
         }
      };
      return new Iterables.UnmodifiableIterable(iterable);
   }

   private static Function toIterator() {
      return new Function() {
         public Iterator apply(Iterable iterable) {
            return iterable.iterator();
         }
      };
   }

   private static class ConsumingQueueIterator extends AbstractIterator {
      private final Queue queue;

      private ConsumingQueueIterator(Queue queue) {
         this.queue = queue;
      }

      public Object computeNext() {
         try {
            return this.queue.remove();
         } catch (NoSuchElementException var2) {
            return this.endOfData();
         }
      }
   }

   private static final class UnmodifiableIterable extends FluentIterable {
      private final Iterable iterable;

      private UnmodifiableIterable(Iterable iterable) {
         this.iterable = iterable;
      }

      public Iterator iterator() {
         return Iterators.unmodifiableIterator(this.iterable.iterator());
      }

      public String toString() {
         return this.iterable.toString();
      }
   }
}
