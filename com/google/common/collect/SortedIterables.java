package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedIterable;
import java.util.Comparator;
import java.util.SortedSet;

@GwtCompatible
final class SortedIterables {
   public static boolean hasSameComparator(Comparator comparator, Iterable elements) {
      Preconditions.checkNotNull(comparator);
      Preconditions.checkNotNull(elements);
      Comparator<?> comparator2;
      if(elements instanceof SortedSet) {
         comparator2 = comparator((SortedSet)elements);
      } else {
         if(!(elements instanceof SortedIterable)) {
            return false;
         }

         comparator2 = ((SortedIterable)elements).comparator();
      }

      return comparator.equals(comparator2);
   }

   public static Comparator comparator(SortedSet sortedSet) {
      Comparator<? super E> result = sortedSet.comparator();
      if(result == null) {
         result = Ordering.natural();
      }

      return result;
   }
}
