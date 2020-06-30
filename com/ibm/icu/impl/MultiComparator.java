package com.ibm.icu.impl;

import java.util.Comparator;

public class MultiComparator implements Comparator {
   private Comparator[] comparators;

   public MultiComparator(Comparator... comparators) {
      this.comparators = comparators;
   }

   public int compare(Object arg0, Object arg1) {
      for(int i = 0; i < this.comparators.length; ++i) {
         int result = this.comparators[i].compare(arg0, arg1);
         if(result != 0) {
            if(result > 0) {
               return i + 1;
            }

            return -(i + 1);
         }
      }

      return 0;
   }
}
