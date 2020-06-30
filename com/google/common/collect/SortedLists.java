package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import javax.annotation.Nullable;

@GwtCompatible
@Beta
final class SortedLists {
   public static int binarySearch(List list, Comparable e, SortedLists.KeyPresentBehavior presentBehavior, SortedLists.KeyAbsentBehavior absentBehavior) {
      Preconditions.checkNotNull(e);
      return binarySearch(list, (Object)Preconditions.checkNotNull(e), (Comparator)Ordering.natural(), presentBehavior, absentBehavior);
   }

   public static int binarySearch(List list, Function keyFunction, @Nullable Comparable key, SortedLists.KeyPresentBehavior presentBehavior, SortedLists.KeyAbsentBehavior absentBehavior) {
      return binarySearch(list, keyFunction, key, Ordering.natural(), presentBehavior, absentBehavior);
   }

   public static int binarySearch(List list, Function keyFunction, @Nullable Object key, Comparator keyComparator, SortedLists.KeyPresentBehavior presentBehavior, SortedLists.KeyAbsentBehavior absentBehavior) {
      return binarySearch(Lists.transform(list, keyFunction), key, keyComparator, presentBehavior, absentBehavior);
   }

   public static int binarySearch(List list, @Nullable Object key, Comparator comparator, SortedLists.KeyPresentBehavior presentBehavior, SortedLists.KeyAbsentBehavior absentBehavior) {
      Preconditions.checkNotNull(comparator);
      Preconditions.checkNotNull(list);
      Preconditions.checkNotNull(presentBehavior);
      Preconditions.checkNotNull(absentBehavior);
      if(!(list instanceof RandomAccess)) {
         list = Lists.newArrayList((Iterable)list);
      }

      int lower = 0;
      int upper = ((List)list).size() - 1;

      while(lower <= upper) {
         int middle = lower + upper >>> 1;
         int c = comparator.compare(key, ((List)list).get(middle));
         if(c < 0) {
            upper = middle - 1;
         } else {
            if(c <= 0) {
               return lower + presentBehavior.resultIndex(comparator, key, ((List)list).subList(lower, upper + 1), middle - lower);
            }

            lower = middle + 1;
         }
      }

      return absentBehavior.resultIndex(lower);
   }

   public static enum KeyAbsentBehavior {
      NEXT_LOWER {
         int resultIndex(int higherIndex) {
            return higherIndex - 1;
         }
      },
      NEXT_HIGHER {
         public int resultIndex(int higherIndex) {
            return higherIndex;
         }
      },
      INVERTED_INSERTION_INDEX {
         public int resultIndex(int higherIndex) {
            return ~higherIndex;
         }
      };

      private KeyAbsentBehavior() {
      }

      abstract int resultIndex(int var1);
   }

   public static enum KeyPresentBehavior {
      ANY_PRESENT {
         int resultIndex(Comparator comparator, Object key, List list, int foundIndex) {
            return foundIndex;
         }
      },
      LAST_PRESENT {
         int resultIndex(Comparator comparator, Object key, List list, int foundIndex) {
            int lower = foundIndex;
            int upper = list.size() - 1;

            while(lower < upper) {
               int middle = lower + upper + 1 >>> 1;
               int c = comparator.compare(list.get(middle), key);
               if(c > 0) {
                  upper = middle - 1;
               } else {
                  lower = middle;
               }
            }

            return lower;
         }
      },
      FIRST_PRESENT {
         int resultIndex(Comparator comparator, Object key, List list, int foundIndex) {
            int lower = 0;
            int upper = foundIndex;

            while(lower < upper) {
               int middle = lower + upper >>> 1;
               int c = comparator.compare(list.get(middle), key);
               if(c < 0) {
                  lower = middle + 1;
               } else {
                  upper = middle;
               }
            }

            return lower;
         }
      },
      FIRST_AFTER {
         public int resultIndex(Comparator comparator, Object key, List list, int foundIndex) {
            return LAST_PRESENT.resultIndex(comparator, key, list, foundIndex) + 1;
         }
      },
      LAST_BEFORE {
         public int resultIndex(Comparator comparator, Object key, List list, int foundIndex) {
            return FIRST_PRESENT.resultIndex(comparator, key, list, foundIndex) - 1;
         }
      };

      private KeyPresentBehavior() {
      }

      abstract int resultIndex(Comparator var1, Object var2, List var3, int var4);
   }
}
