package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.DenseImmutableTable;
import com.google.common.collect.ImmutableAsList;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.SparseImmutableTable;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
abstract class RegularImmutableTable extends ImmutableTable {
   abstract Table.Cell getCell(int var1);

   final ImmutableSet createCellSet() {
      return (ImmutableSet)(this.isEmpty()?ImmutableSet.of():new RegularImmutableTable.CellSet());
   }

   abstract Object getValue(int var1);

   final ImmutableCollection createValues() {
      return (ImmutableCollection)(this.isEmpty()?ImmutableList.of():new RegularImmutableTable.Values());
   }

   static RegularImmutableTable forCells(List cells, @Nullable final Comparator rowComparator, @Nullable final Comparator columnComparator) {
      Preconditions.checkNotNull(cells);
      if(rowComparator != null || columnComparator != null) {
         Comparator<Table.Cell<R, C, V>> comparator = new Comparator() {
            public int compare(Table.Cell cell1, Table.Cell cell2) {
               int rowCompare = rowComparator == null?0:rowComparator.compare(cell1.getRowKey(), cell2.getRowKey());
               return rowCompare != 0?rowCompare:(columnComparator == null?0:columnComparator.compare(cell1.getColumnKey(), cell2.getColumnKey()));
            }
         };
         Collections.sort(cells, comparator);
      }

      return forCellsInternal(cells, rowComparator, columnComparator);
   }

   static RegularImmutableTable forCells(Iterable cells) {
      return forCellsInternal(cells, (Comparator)null, (Comparator)null);
   }

   private static final RegularImmutableTable forCellsInternal(Iterable cells, @Nullable Comparator rowComparator, @Nullable Comparator columnComparator) {
      ImmutableSet.Builder<R> rowSpaceBuilder = ImmutableSet.builder();
      ImmutableSet.Builder<C> columnSpaceBuilder = ImmutableSet.builder();
      ImmutableList<Table.Cell<R, C, V>> cellList = ImmutableList.copyOf(cells);

      for(Table.Cell<R, C, V> cell : cellList) {
         rowSpaceBuilder.add(cell.getRowKey());
         columnSpaceBuilder.add(cell.getColumnKey());
      }

      ImmutableSet<R> rowSpace = rowSpaceBuilder.build();
      if(rowComparator != null) {
         List<R> rowList = Lists.newArrayList((Iterable)rowSpace);
         Collections.sort(rowList, rowComparator);
         rowSpace = ImmutableSet.copyOf((Collection)rowList);
      }

      ImmutableSet<C> columnSpace = columnSpaceBuilder.build();
      if(columnComparator != null) {
         List<C> columnList = Lists.newArrayList((Iterable)columnSpace);
         Collections.sort(columnList, columnComparator);
         columnSpace = ImmutableSet.copyOf((Collection)columnList);
      }

      return (RegularImmutableTable)((long)cellList.size() > (long)rowSpace.size() * (long)columnSpace.size() / 2L?new DenseImmutableTable(cellList, rowSpace, columnSpace):new SparseImmutableTable(cellList, rowSpace, columnSpace));
   }

   private final class CellSet extends ImmutableSet {
      private CellSet() {
      }

      public int size() {
         return RegularImmutableTable.this.size();
      }

      public UnmodifiableIterator iterator() {
         return this.asList().iterator();
      }

      ImmutableList createAsList() {
         return new ImmutableAsList() {
            public Table.Cell get(int index) {
               return RegularImmutableTable.this.getCell(index);
            }

            ImmutableCollection delegateCollection() {
               return CellSet.this;
            }
         };
      }

      public boolean contains(@Nullable Object object) {
         if(!(object instanceof Table.Cell)) {
            return false;
         } else {
            Table.Cell<?, ?, ?> cell = (Table.Cell)object;
            Object value = RegularImmutableTable.this.get(cell.getRowKey(), cell.getColumnKey());
            return value != null && value.equals(cell.getValue());
         }
      }

      boolean isPartialView() {
         return false;
      }
   }

   private final class Values extends ImmutableList {
      private Values() {
      }

      public int size() {
         return RegularImmutableTable.this.size();
      }

      public Object get(int index) {
         return RegularImmutableTable.this.getValue(index);
      }

      boolean isPartialView() {
         return true;
      }
   }
}
