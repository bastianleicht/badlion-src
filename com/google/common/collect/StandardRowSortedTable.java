package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.RowSortedTable;
import com.google.common.collect.StandardTable;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible
class StandardRowSortedTable extends StandardTable implements RowSortedTable {
   private static final long serialVersionUID = 0L;

   StandardRowSortedTable(SortedMap backingMap, Supplier factory) {
      super(backingMap, factory);
   }

   private SortedMap sortedBackingMap() {
      return (SortedMap)this.backingMap;
   }

   public SortedSet rowKeySet() {
      return (SortedSet)this.rowMap().keySet();
   }

   public SortedMap rowMap() {
      return (SortedMap)super.rowMap();
   }

   SortedMap createRowMap() {
      return new StandardRowSortedTable.RowSortedMap();
   }

   private class RowSortedMap extends StandardTable.RowMap implements SortedMap {
      private RowSortedMap() {
         super();
      }

      public SortedSet keySet() {
         return (SortedSet)super.keySet();
      }

      SortedSet createKeySet() {
         return new Maps.SortedKeySet(this);
      }

      public Comparator comparator() {
         return StandardRowSortedTable.this.sortedBackingMap().comparator();
      }

      public Object firstKey() {
         return StandardRowSortedTable.this.sortedBackingMap().firstKey();
      }

      public Object lastKey() {
         return StandardRowSortedTable.this.sortedBackingMap().lastKey();
      }

      public SortedMap headMap(Object toKey) {
         Preconditions.checkNotNull(toKey);
         return (new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().headMap(toKey), StandardRowSortedTable.this.factory)).rowMap();
      }

      public SortedMap subMap(Object fromKey, Object toKey) {
         Preconditions.checkNotNull(fromKey);
         Preconditions.checkNotNull(toKey);
         return (new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().subMap(fromKey, toKey), StandardRowSortedTable.this.factory)).rowMap();
      }

      public SortedMap tailMap(Object fromKey) {
         Preconditions.checkNotNull(fromKey);
         return (new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().tailMap(fromKey), StandardRowSortedTable.this.factory)).rowMap();
      }
   }
}
