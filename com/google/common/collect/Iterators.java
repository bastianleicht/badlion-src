package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIndexedListIterator;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.TransformedIterator;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.UnmodifiableListIterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class Iterators {
   static final UnmodifiableListIterator EMPTY_LIST_ITERATOR = new UnmodifiableListIterator() {
      public boolean hasNext() {
         return false;
      }

      public Object next() {
         throw new NoSuchElementException();
      }

      public boolean hasPrevious() {
         return false;
      }

      public Object previous() {
         throw new NoSuchElementException();
      }

      public int nextIndex() {
         return 0;
      }

      public int previousIndex() {
         return -1;
      }
   };
   private static final Iterator EMPTY_MODIFIABLE_ITERATOR = new Iterator() {
      public boolean hasNext() {
         return false;
      }

      public Object next() {
         throw new NoSuchElementException();
      }

      public void remove() {
         CollectPreconditions.checkRemove(false);
      }
   };

   public static UnmodifiableIterator emptyIterator() {
      return emptyListIterator();
   }

   static UnmodifiableListIterator emptyListIterator() {
      return EMPTY_LIST_ITERATOR;
   }

   static Iterator emptyModifiableIterator() {
      return EMPTY_MODIFIABLE_ITERATOR;
   }

   public static UnmodifiableIterator unmodifiableIterator(final Iterator iterator) {
      Preconditions.checkNotNull(iterator);
      return iterator instanceof UnmodifiableIterator?(UnmodifiableIterator)iterator:new UnmodifiableIterator() {
         public boolean hasNext() {
            return iterator.hasNext();
         }

         public Object next() {
            return iterator.next();
         }
      };
   }

   /** @deprecated */
   @Deprecated
   public static UnmodifiableIterator unmodifiableIterator(UnmodifiableIterator iterator) {
      return (UnmodifiableIterator)Preconditions.checkNotNull(iterator);
   }

   public static int size(Iterator iterator) {
      int count;
      for(count = 0; iterator.hasNext(); ++count) {
         iterator.next();
      }

      return count;
   }

   public static boolean contains(Iterator iterator, @Nullable Object element) {
      return any(iterator, Predicates.equalTo(element));
   }

   public static boolean removeAll(Iterator removeFrom, Collection elementsToRemove) {
      return removeIf(removeFrom, Predicates.in(elementsToRemove));
   }

   public static boolean removeIf(Iterator removeFrom, Predicate predicate) {
      Preconditions.checkNotNull(predicate);
      boolean modified = false;

      while(removeFrom.hasNext()) {
         if(predicate.apply(removeFrom.next())) {
            removeFrom.remove();
            modified = true;
         }
      }

      return modified;
   }

   public static boolean retainAll(Iterator removeFrom, Collection elementsToRetain) {
      return removeIf(removeFrom, Predicates.not(Predicates.in(elementsToRetain)));
   }

   public static boolean elementsEqual(Iterator iterator1, Iterator iterator2) {
      while(true) {
         if(iterator1.hasNext()) {
            if(!iterator2.hasNext()) {
               return false;
            }

            Object o1 = iterator1.next();
            Object o2 = iterator2.next();
            if(Objects.equal(o1, o2)) {
               continue;
            }

            return false;
         }

         return !iterator2.hasNext();
      }
   }

   public static String toString(Iterator iterator) {
      return Collections2.STANDARD_JOINER.appendTo((new StringBuilder()).append('['), iterator).append(']').toString();
   }

   public static Object getOnlyElement(Iterator iterator) {
      T first = iterator.next();
      if(!iterator.hasNext()) {
         return first;
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append("expected one element but was: <" + first);

         for(int i = 0; i < 4 && iterator.hasNext(); ++i) {
            sb.append(", " + iterator.next());
         }

         if(iterator.hasNext()) {
            sb.append(", ...");
         }

         sb.append('>');
         throw new IllegalArgumentException(sb.toString());
      }
   }

   @Nullable
   public static Object getOnlyElement(Iterator iterator, @Nullable Object defaultValue) {
      return iterator.hasNext()?getOnlyElement(iterator):defaultValue;
   }

   @GwtIncompatible("Array.newInstance(Class, int)")
   public static Object[] toArray(Iterator iterator, Class type) {
      List<T> list = Lists.newArrayList(iterator);
      return Iterables.toArray(list, type);
   }

   public static boolean addAll(Collection addTo, Iterator iterator) {
      Preconditions.checkNotNull(addTo);
      Preconditions.checkNotNull(iterator);

      boolean wasModified;
      for(wasModified = false; iterator.hasNext(); wasModified |= addTo.add(iterator.next())) {
         ;
      }

      return wasModified;
   }

   public static int frequency(Iterator iterator, @Nullable Object element) {
      return size(filter(iterator, Predicates.equalTo(element)));
   }

   public static Iterator cycle(final Iterable iterable) {
      Preconditions.checkNotNull(iterable);
      return new Iterator() {
         Iterator iterator = Iterators.EMPTY_LIST_ITERATOR;
         Iterator removeFrom;

         public boolean hasNext() {
            if(!this.iterator.hasNext()) {
               this.iterator = iterable.iterator();
            }

            return this.iterator.hasNext();
         }

         public Object next() {
            if(!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               this.removeFrom = this.iterator;
               return this.iterator.next();
            }
         }

         public void remove() {
            CollectPreconditions.checkRemove(this.removeFrom != null);
            this.removeFrom.remove();
            this.removeFrom = null;
         }
      };
   }

   public static Iterator cycle(Object... elements) {
      return cycle((Iterable)Lists.newArrayList(elements));
   }

   public static Iterator concat(Iterator a, Iterator b) {
      return concat((Iterator)ImmutableList.of(a, b).iterator());
   }

   public static Iterator concat(Iterator a, Iterator b, Iterator c) {
      return concat((Iterator)ImmutableList.of(a, b, c).iterator());
   }

   public static Iterator concat(Iterator a, Iterator b, Iterator c, Iterator d) {
      return concat((Iterator)ImmutableList.of(a, b, c, d).iterator());
   }

   public static Iterator concat(Iterator... inputs) {
      return concat((Iterator)ImmutableList.copyOf((Object[])inputs).iterator());
   }

   public static Iterator concat(final Iterator inputs) {
      Preconditions.checkNotNull(inputs);
      return new Iterator() {
         Iterator current = Iterators.EMPTY_LIST_ITERATOR;
         Iterator removeFrom;

         public boolean hasNext() {
            boolean currentHasNext;
            while(!(currentHasNext = ((Iterator)Preconditions.checkNotNull(this.current)).hasNext()) && inputs.hasNext()) {
               this.current = (Iterator)inputs.next();
            }

            return currentHasNext;
         }

         public Object next() {
            if(!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               this.removeFrom = this.current;
               return this.current.next();
            }
         }

         public void remove() {
            CollectPreconditions.checkRemove(this.removeFrom != null);
            this.removeFrom.remove();
            this.removeFrom = null;
         }
      };
   }

   public static UnmodifiableIterator partition(Iterator iterator, int size) {
      return partitionImpl(iterator, size, false);
   }

   public static UnmodifiableIterator paddedPartition(Iterator iterator, int size) {
      return partitionImpl(iterator, size, true);
   }

   private static UnmodifiableIterator partitionImpl(final Iterator iterator, final int size, final boolean pad) {
      Preconditions.checkNotNull(iterator);
      Preconditions.checkArgument(size > 0);
      return new UnmodifiableIterator() {
         public boolean hasNext() {
            return iterator.hasNext();
         }

         public List next() {
            if(!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Object[] array = new Object[size];

               int count;
               for(count = 0; count < size && iterator.hasNext(); ++count) {
                  array[count] = iterator.next();
               }

               for(int i = count; i < size; ++i) {
                  array[i] = null;
               }

               List<T> list = Collections.unmodifiableList(Arrays.asList(array));
               return !pad && count != size?list.subList(0, count):list;
            }
         }
      };
   }

   public static UnmodifiableIterator filter(final Iterator unfiltered, final Predicate predicate) {
      Preconditions.checkNotNull(unfiltered);
      Preconditions.checkNotNull(predicate);
      return new AbstractIterator() {
         protected Object computeNext() {
            while(true) {
               if(unfiltered.hasNext()) {
                  T element = unfiltered.next();
                  if(!predicate.apply(element)) {
                     continue;
                  }

                  return element;
               }

               return this.endOfData();
            }
         }
      };
   }

   @GwtIncompatible("Class.isInstance")
   public static UnmodifiableIterator filter(Iterator unfiltered, Class type) {
      return filter(unfiltered, Predicates.instanceOf(type));
   }

   public static boolean any(Iterator iterator, Predicate predicate) {
      return indexOf(iterator, predicate) != -1;
   }

   public static boolean all(Iterator iterator, Predicate predicate) {
      Preconditions.checkNotNull(predicate);

      while(iterator.hasNext()) {
         T element = iterator.next();
         if(!predicate.apply(element)) {
            return false;
         }
      }

      return true;
   }

   public static Object find(Iterator iterator, Predicate predicate) {
      return filter(iterator, predicate).next();
   }

   @Nullable
   public static Object find(Iterator iterator, Predicate predicate, @Nullable Object defaultValue) {
      return getNext(filter(iterator, predicate), defaultValue);
   }

   public static Optional tryFind(Iterator iterator, Predicate predicate) {
      UnmodifiableIterator<T> filteredIterator = filter(iterator, predicate);
      return filteredIterator.hasNext()?Optional.of(filteredIterator.next()):Optional.absent();
   }

   public static int indexOf(Iterator iterator, Predicate predicate) {
      Preconditions.checkNotNull(predicate, "predicate");

      for(int i = 0; iterator.hasNext(); ++i) {
         T current = iterator.next();
         if(predicate.apply(current)) {
            return i;
         }
      }

      return -1;
   }

   public static Iterator transform(final Iterator fromIterator, final Function function) {
      Preconditions.checkNotNull(function);
      return new TransformedIterator(fromIterator) {
         Object transform(Object from) {
            return function.apply(from);
         }
      };
   }

   public static Object get(Iterator iterator, int position) {
      checkNonnegative(position);
      int skipped = advance(iterator, position);
      if(!iterator.hasNext()) {
         throw new IndexOutOfBoundsException("position (" + position + ") must be less than the number of elements that remained (" + skipped + ")");
      } else {
         return iterator.next();
      }
   }

   static void checkNonnegative(int position) {
      if(position < 0) {
         throw new IndexOutOfBoundsException("position (" + position + ") must not be negative");
      }
   }

   @Nullable
   public static Object get(Iterator iterator, int position, @Nullable Object defaultValue) {
      checkNonnegative(position);
      advance(iterator, position);
      return getNext(iterator, defaultValue);
   }

   @Nullable
   public static Object getNext(Iterator iterator, @Nullable Object defaultValue) {
      return iterator.hasNext()?iterator.next():defaultValue;
   }

   public static Object getLast(Iterator iterator) {
      T current;
      while(true) {
         current = iterator.next();
         if(!iterator.hasNext()) {
            break;
         }
      }

      return current;
   }

   @Nullable
   public static Object getLast(Iterator iterator, @Nullable Object defaultValue) {
      return iterator.hasNext()?getLast(iterator):defaultValue;
   }

   public static int advance(Iterator iterator, int numberToAdvance) {
      Preconditions.checkNotNull(iterator);
      Preconditions.checkArgument(numberToAdvance >= 0, "numberToAdvance must be nonnegative");

      int i;
      for(i = 0; i < numberToAdvance && iterator.hasNext(); ++i) {
         iterator.next();
      }

      return i;
   }

   public static Iterator limit(final Iterator iterator, final int limitSize) {
      Preconditions.checkNotNull(iterator);
      Preconditions.checkArgument(limitSize >= 0, "limit is negative");
      return new Iterator() {
         private int count;

         public boolean hasNext() {
            return this.count < limitSize && iterator.hasNext();
         }

         public Object next() {
            if(!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               ++this.count;
               return iterator.next();
            }
         }

         public void remove() {
            iterator.remove();
         }
      };
   }

   public static Iterator consumingIterator(final Iterator iterator) {
      Preconditions.checkNotNull(iterator);
      return new UnmodifiableIterator() {
         public boolean hasNext() {
            return iterator.hasNext();
         }

         public Object next() {
            T next = iterator.next();
            iterator.remove();
            return next;
         }

         public String toString() {
            return "Iterators.consumingIterator(...)";
         }
      };
   }

   @Nullable
   static Object pollNext(Iterator iterator) {
      if(iterator.hasNext()) {
         T result = iterator.next();
         iterator.remove();
         return result;
      } else {
         return null;
      }
   }

   static void clear(Iterator iterator) {
      Preconditions.checkNotNull(iterator);

      while(iterator.hasNext()) {
         iterator.next();
         iterator.remove();
      }

   }

   public static UnmodifiableIterator forArray(Object... array) {
      return forArray(array, 0, array.length, 0);
   }

   static UnmodifiableListIterator forArray(final Object[] array, final int offset, final int length, final int index) {
      Preconditions.checkArgument(length >= 0);
      int end = offset + length;
      Preconditions.checkPositionIndexes(offset, end, array.length);
      Preconditions.checkPositionIndex(index, length);
      return (UnmodifiableListIterator)(length == 0?emptyListIterator():new AbstractIndexedListIterator(length, index) {
         protected Object get(int index) {
            return array[offset + index];
         }
      });
   }

   public static UnmodifiableIterator singletonIterator(@Nullable final Object value) {
      return new UnmodifiableIterator() {
         boolean done;

         public boolean hasNext() {
            return !this.done;
         }

         public Object next() {
            if(this.done) {
               throw new NoSuchElementException();
            } else {
               this.done = true;
               return value;
            }
         }
      };
   }

   public static UnmodifiableIterator forEnumeration(final Enumeration enumeration) {
      Preconditions.checkNotNull(enumeration);
      return new UnmodifiableIterator() {
         public boolean hasNext() {
            return enumeration.hasMoreElements();
         }

         public Object next() {
            return enumeration.nextElement();
         }
      };
   }

   public static Enumeration asEnumeration(final Iterator iterator) {
      Preconditions.checkNotNull(iterator);
      return new Enumeration() {
         public boolean hasMoreElements() {
            return iterator.hasNext();
         }

         public Object nextElement() {
            return iterator.next();
         }
      };
   }

   public static PeekingIterator peekingIterator(Iterator iterator) {
      if(iterator instanceof Iterators.PeekingImpl) {
         Iterators.PeekingImpl<T> peeking = (Iterators.PeekingImpl)iterator;
         return peeking;
      } else {
         return new Iterators.PeekingImpl(iterator);
      }
   }

   /** @deprecated */
   @Deprecated
   public static PeekingIterator peekingIterator(PeekingIterator iterator) {
      return (PeekingIterator)Preconditions.checkNotNull(iterator);
   }

   @Beta
   public static UnmodifiableIterator mergeSorted(Iterable iterators, Comparator comparator) {
      Preconditions.checkNotNull(iterators, "iterators");
      Preconditions.checkNotNull(comparator, "comparator");
      return new Iterators.MergingIterator(iterators, comparator);
   }

   static ListIterator cast(Iterator iterator) {
      return (ListIterator)iterator;
   }

   private static class MergingIterator extends UnmodifiableIterator {
      final Queue queue;

      public MergingIterator(Iterable iterators, final Comparator itemComparator) {
         Comparator<PeekingIterator<T>> heapComparator = new Comparator() {
            public int compare(PeekingIterator o1, PeekingIterator o2) {
               return itemComparator.compare(o1.peek(), o2.peek());
            }
         };
         this.queue = new PriorityQueue(2, heapComparator);

         for(Iterator<? extends T> iterator : iterators) {
            if(iterator.hasNext()) {
               this.queue.add(Iterators.peekingIterator(iterator));
            }
         }

      }

      public boolean hasNext() {
         return !this.queue.isEmpty();
      }

      public Object next() {
         PeekingIterator<T> nextIter = (PeekingIterator)this.queue.remove();
         T next = nextIter.next();
         if(nextIter.hasNext()) {
            this.queue.add(nextIter);
         }

         return next;
      }
   }

   private static class PeekingImpl implements PeekingIterator {
      private final Iterator iterator;
      private boolean hasPeeked;
      private Object peekedElement;

      public PeekingImpl(Iterator iterator) {
         this.iterator = (Iterator)Preconditions.checkNotNull(iterator);
      }

      public boolean hasNext() {
         return this.hasPeeked || this.iterator.hasNext();
      }

      public Object next() {
         if(!this.hasPeeked) {
            return this.iterator.next();
         } else {
            E result = this.peekedElement;
            this.hasPeeked = false;
            this.peekedElement = null;
            return result;
         }
      }

      public void remove() {
         Preconditions.checkState(!this.hasPeeked, "Can\'t remove after you\'ve peeked at next");
         this.iterator.remove();
      }

      public Object peek() {
         if(!this.hasPeeked) {
            this.peekedElement = this.iterator.next();
            this.hasPeeked = true;
         }

         return this.peekedElement;
      }
   }
}
