package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.AbstractSortedSetMultimap;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible
abstract class AbstractSortedKeySortedSetMultimap extends AbstractSortedSetMultimap {
   AbstractSortedKeySortedSetMultimap(SortedMap map) {
      super(map);
   }

   public SortedMap asMap() {
      return (SortedMap)super.asMap();
   }

   SortedMap backingMap() {
      return (SortedMap)super.backingMap();
   }

   public SortedSet keySet() {
      return (SortedSet)super.keySet();
   }
}
