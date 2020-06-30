package com.ibm.icu.util;

public interface RangeValueIterator {
   boolean next(RangeValueIterator.Element var1);

   void reset();

   public static class Element {
      public int start;
      public int limit;
      public int value;
   }
}
