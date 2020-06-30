package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.AllEqualOrdering;
import com.google.common.collect.ByFunctionOrdering;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ComparatorOrdering;
import com.google.common.collect.CompoundOrdering;
import com.google.common.collect.ExplicitOrdering;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.LexicographicalOrdering;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.NaturalOrdering;
import com.google.common.collect.NullsFirstOrdering;
import com.google.common.collect.NullsLastOrdering;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Platform;
import com.google.common.collect.ReverseOrdering;
import com.google.common.collect.UsingToStringOrdering;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class Ordering implements Comparator {
   static final int LEFT_IS_GREATER = 1;
   static final int RIGHT_IS_GREATER = -1;

   @GwtCompatible(
      serializable = true
   )
   public static Ordering natural() {
      return NaturalOrdering.INSTANCE;
   }

   @GwtCompatible(
      serializable = true
   )
   public static Ordering from(Comparator comparator) {
      return (Ordering)(comparator instanceof Ordering?(Ordering)comparator:new ComparatorOrdering(comparator));
   }

   /** @deprecated */
   @Deprecated
   @GwtCompatible(
      serializable = true
   )
   public static Ordering from(Ordering ordering) {
      return (Ordering)Preconditions.checkNotNull(ordering);
   }

   @GwtCompatible(
      serializable = true
   )
   public static Ordering explicit(List valuesInOrder) {
      return new ExplicitOrdering(valuesInOrder);
   }

   @GwtCompatible(
      serializable = true
   )
   public static Ordering explicit(Object leastValue, Object... remainingValuesInOrder) {
      return explicit(Lists.asList(leastValue, remainingValuesInOrder));
   }

   @GwtCompatible(
      serializable = true
   )
   public static Ordering allEqual() {
      return AllEqualOrdering.INSTANCE;
   }

   @GwtCompatible(
      serializable = true
   )
   public static Ordering usingToString() {
      return UsingToStringOrdering.INSTANCE;
   }

   public static Ordering arbitrary() {
      return Ordering.ArbitraryOrderingHolder.ARBITRARY_ORDERING;
   }

   @GwtCompatible(
      serializable = true
   )
   public Ordering reverse() {
      return new ReverseOrdering(this);
   }

   @GwtCompatible(
      serializable = true
   )
   public Ordering nullsFirst() {
      return new NullsFirstOrdering(this);
   }

   @GwtCompatible(
      serializable = true
   )
   public Ordering nullsLast() {
      return new NullsLastOrdering(this);
   }

   @GwtCompatible(
      serializable = true
   )
   public Ordering onResultOf(Function function) {
      return new ByFunctionOrdering(function, this);
   }

   Ordering onKeys() {
      return this.onResultOf(Maps.keyFunction());
   }

   @GwtCompatible(
      serializable = true
   )
   public Ordering compound(Comparator secondaryComparator) {
      return new CompoundOrdering(this, (Comparator)Preconditions.checkNotNull(secondaryComparator));
   }

   @GwtCompatible(
      serializable = true
   )
   public static Ordering compound(Iterable comparators) {
      return new CompoundOrdering(comparators);
   }

   @GwtCompatible(
      serializable = true
   )
   public Ordering lexicographical() {
      return new LexicographicalOrdering(this);
   }

   public abstract int compare(@Nullable Object var1, @Nullable Object var2);

   public Object min(Iterator iterator) {
      E minSoFar;
      for(minSoFar = iterator.next(); iterator.hasNext(); minSoFar = this.min(minSoFar, iterator.next())) {
         ;
      }

      return minSoFar;
   }

   public Object min(Iterable iterable) {
      return this.min(iterable.iterator());
   }

   public Object min(@Nullable Object a, @Nullable Object b) {
      return this.compare(a, b) <= 0?a:b;
   }

   public Object min(@Nullable Object a, @Nullable Object b, @Nullable Object c, Object... rest) {
      E minSoFar = this.min(this.min(a, b), c);

      for(E r : rest) {
         minSoFar = this.min(minSoFar, r);
      }

      return minSoFar;
   }

   public Object max(Iterator iterator) {
      E maxSoFar;
      for(maxSoFar = iterator.next(); iterator.hasNext(); maxSoFar = this.max(maxSoFar, iterator.next())) {
         ;
      }

      return maxSoFar;
   }

   public Object max(Iterable iterable) {
      return this.max(iterable.iterator());
   }

   public Object max(@Nullable Object a, @Nullable Object b) {
      return this.compare(a, b) >= 0?a:b;
   }

   public Object max(@Nullable Object a, @Nullable Object b, @Nullable Object c, Object... rest) {
      E maxSoFar = this.max(this.max(a, b), c);

      for(E r : rest) {
         maxSoFar = this.max(maxSoFar, r);
      }

      return maxSoFar;
   }

   public List leastOf(Iterable iterable, int k) {
      if(iterable instanceof Collection) {
         Collection<E> collection = (Collection)iterable;
         if((long)collection.size() <= 2L * (long)k) {
            E[] array = (Object[])collection.toArray();
            Arrays.sort(array, this);
            if(array.length > k) {
               array = ObjectArrays.arraysCopyOf(array, k);
            }

            return Collections.unmodifiableList(Arrays.asList(array));
         }
      }

      return this.leastOf(iterable.iterator(), k);
   }

   public List leastOf(Iterator elements, int k) {
      Preconditions.checkNotNull(elements);
      CollectPreconditions.checkNonnegative(k, "k");
      if(k != 0 && elements.hasNext()) {
         if(k >= 1073741823) {
            ArrayList<E> list = Lists.newArrayList(elements);
            Collections.sort(list, this);
            if(list.size() > k) {
               list.subList(k, list.size()).clear();
            }

            list.trimToSize();
            return Collections.unmodifiableList(list);
         } else {
            int bufferCap = k * 2;
            E[] buffer = (Object[])(new Object[bufferCap]);
            E threshold = elements.next();
            buffer[0] = threshold;

            int bufferSize;
            E e;
            for(bufferSize = 1; bufferSize < k && elements.hasNext(); threshold = this.max(threshold, e)) {
               e = elements.next();
               buffer[bufferSize++] = e;
            }

            while(elements.hasNext()) {
               e = elements.next();
               if(this.compare(e, threshold) < 0) {
                  buffer[bufferSize++] = e;
                  if(bufferSize == bufferCap) {
                     int left = 0;
                     int right = bufferCap - 1;
                     int minThresholdPosition = 0;

                     while(left < right) {
                        int pivotIndex = left + right + 1 >>> 1;
                        int pivotNewIndex = this.partition(buffer, left, right, pivotIndex);
                        if(pivotNewIndex > k) {
                           right = pivotNewIndex - 1;
                        } else {
                           if(pivotNewIndex >= k) {
                              break;
                           }

                           left = Math.max(pivotNewIndex, left + 1);
                           minThresholdPosition = pivotNewIndex;
                        }
                     }

                     bufferSize = k;
                     threshold = buffer[minThresholdPosition];

                     for(int i = minThresholdPosition + 1; i < bufferSize; ++i) {
                        threshold = this.max(threshold, buffer[i]);
                     }
                  }
               }
            }

            Arrays.sort(buffer, 0, bufferSize, this);
            bufferSize = Math.min(bufferSize, k);
            return Collections.unmodifiableList(Arrays.asList(ObjectArrays.arraysCopyOf(buffer, bufferSize)));
         }
      } else {
         return ImmutableList.of();
      }
   }

   private int partition(Object[] values, int left, int right, int pivotIndex) {
      E pivotValue = values[pivotIndex];
      values[pivotIndex] = values[right];
      values[right] = pivotValue;
      int storeIndex = left;

      for(int i = left; i < right; ++i) {
         if(this.compare(values[i], pivotValue) < 0) {
            ObjectArrays.swap(values, storeIndex, i);
            ++storeIndex;
         }
      }

      ObjectArrays.swap(values, right, storeIndex);
      return storeIndex;
   }

   public List greatestOf(Iterable iterable, int k) {
      return this.reverse().leastOf(iterable, k);
   }

   public List greatestOf(Iterator iterator, int k) {
      return this.reverse().leastOf(iterator, k);
   }

   public List sortedCopy(Iterable elements) {
      E[] array = (Object[])Iterables.toArray(elements);
      Arrays.sort(array, this);
      return Lists.newArrayList((Iterable)Arrays.asList(array));
   }

   public ImmutableList immutableSortedCopy(Iterable elements) {
      E[] array = (Object[])Iterables.toArray(elements);

      for(E e : array) {
         Preconditions.checkNotNull(e);
      }

      Arrays.sort(array, this);
      return ImmutableList.asImmutableList(array);
   }

   public boolean isOrdered(Iterable iterable) {
      Iterator<? extends T> it = iterable.iterator();
      T next;
      if(it.hasNext()) {
         for(T prev = it.next(); it.hasNext(); prev = next) {
            next = it.next();
            if(this.compare(prev, next) > 0) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean isStrictlyOrdered(Iterable iterable) {
      Iterator<? extends T> it = iterable.iterator();
      T next;
      if(it.hasNext()) {
         for(T prev = it.next(); it.hasNext(); prev = next) {
            next = it.next();
            if(this.compare(prev, next) >= 0) {
               return false;
            }
         }
      }

      return true;
   }

   public int binarySearch(List sortedList, @Nullable Object key) {
      return Collections.binarySearch(sortedList, key, this);
   }

   @VisibleForTesting
   static class ArbitraryOrdering extends Ordering {
      private Map uids = Platform.tryWeakKeys(new MapMaker()).makeComputingMap(new Function() {
         final AtomicInteger counter = new AtomicInteger(0);

         public Integer apply(Object from) {
            return Integer.valueOf(this.counter.getAndIncrement());
         }
      });

      public int compare(Object left, Object right) {
         if(left == right) {
            return 0;
         } else if(left == null) {
            return -1;
         } else if(right == null) {
            return 1;
         } else {
            int leftCode = this.identityHashCode(left);
            int rightCode = this.identityHashCode(right);
            if(leftCode != rightCode) {
               return leftCode < rightCode?-1:1;
            } else {
               int result = ((Integer)this.uids.get(left)).compareTo((Integer)this.uids.get(right));
               if(result == 0) {
                  throw new AssertionError();
               } else {
                  return result;
               }
            }
         }
      }

      public String toString() {
         return "Ordering.arbitrary()";
      }

      int identityHashCode(Object object) {
         return System.identityHashCode(object);
      }
   }

   private static class ArbitraryOrderingHolder {
      static final Ordering ARBITRARY_ORDERING = new Ordering.ArbitraryOrdering();
   }

   @VisibleForTesting
   static class IncomparableValueException extends ClassCastException {
      final Object value;
      private static final long serialVersionUID = 0L;

      IncomparableValueException(Object value) {
         super("Cannot compare value: " + value);
         this.value = value;
      }
   }
}
