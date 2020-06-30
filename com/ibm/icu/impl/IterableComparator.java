package com.ibm.icu.impl;

import java.util.Comparator;
import java.util.Iterator;

public class IterableComparator implements Comparator {
   private final Comparator comparator;
   private final int shorterFirst;
   private static final IterableComparator NOCOMPARATOR = new IterableComparator();

   public IterableComparator() {
      this((Comparator)null, true);
   }

   public IterableComparator(Comparator comparator) {
      this(comparator, true);
   }

   public IterableComparator(Comparator comparator, boolean shorterFirst) {
      this.comparator = comparator;
      this.shorterFirst = shorterFirst?1:-1;
   }

   public int compare(Iterable a, Iterable b) {
      if(a == null) {
         return b == null?0:-this.shorterFirst;
      } else if(b == null) {
         return this.shorterFirst;
      } else {
         Iterator<T> ai = a.iterator();
         Iterator<T> bi = b.iterator();

         while(ai.hasNext()) {
            if(!bi.hasNext()) {
               return this.shorterFirst;
            }

            T aItem = ai.next();
            T bItem = bi.next();
            int result = this.comparator != null?this.comparator.compare(aItem, bItem):((Comparable)aItem).compareTo(bItem);
            if(result != 0) {
               return result;
            }
         }

         return bi.hasNext()?-this.shorterFirst:0;
      }
   }

   public static int compareIterables(Iterable a, Iterable b) {
      return NOCOMPARATOR.compare(a, b);
   }
}
