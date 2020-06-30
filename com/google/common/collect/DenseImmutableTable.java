package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMapEntrySet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.RegularImmutableTable;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@GwtCompatible
@Immutable
final class DenseImmutableTable extends RegularImmutableTable {
   private final ImmutableMap rowKeyToIndex;
   private final ImmutableMap columnKeyToIndex;
   private final ImmutableMap rowMap;
   private final ImmutableMap columnMap;
   private final int[] rowCounts;
   private final int[] columnCounts;
   private final Object[][] values;
   private final int[] iterationOrderRow;
   private final int[] iterationOrderColumn;

   private static ImmutableMap makeIndex(ImmutableSet set) {
      ImmutableMap.Builder<E, Integer> indexBuilder = ImmutableMap.builder();
      int i = 0;

      for(E key : set) {
         indexBuilder.put(key, Integer.valueOf(i));
         ++i;
      }

      return indexBuilder.build();
   }

   DenseImmutableTable(ImmutableList cellList, ImmutableSet rowSpace, ImmutableSet columnSpace) {
      V[][] array = (Object[][])(new Object[rowSpace.size()][columnSpace.size()]);
      this.values = array;
      this.rowKeyToIndex = makeIndex(rowSpace);
      this.columnKeyToIndex = makeIndex(columnSpace);
      this.rowCounts = new int[this.rowKeyToIndex.size()];
      this.columnCounts = new int[this.columnKeyToIndex.size()];
      int[] iterationOrderRow = new int[cellList.size()];
      int[] iterationOrderColumn = new int[cellList.size()];

      for(int i = 0; i < cellList.size(); ++i) {
         Table.Cell<R, C, V> cell = (Table.Cell)cellList.get(i);
         R rowKey = cell.getRowKey();
         C columnKey = cell.getColumnKey();
         int rowIndex = ((Integer)this.rowKeyToIndex.get(rowKey)).intValue();
         int columnIndex = ((Integer)this.columnKeyToIndex.get(columnKey)).intValue();
         V existingValue = this.values[rowIndex][columnIndex];
         Preconditions.checkArgument(existingValue == null, "duplicate key: (%s, %s)", new Object[]{rowKey, columnKey});
         this.values[rowIndex][columnIndex] = cell.getValue();
         ++this.rowCounts[rowIndex];
         ++this.columnCounts[columnIndex];
         iterationOrderRow[i] = rowIndex;
         iterationOrderColumn[i] = columnIndex;
      }

      this.iterationOrderRow = iterationOrderRow;
      this.iterationOrderColumn = iterationOrderColumn;
      this.rowMap = new DenseImmutableTable.RowMap();
      this.columnMap = new DenseImmutableTable.ColumnMap();
   }

   public ImmutableMap columnMap() {
      return this.columnMap;
   }

   public ImmutableMap rowMap() {
      return this.rowMap;
   }

   public Object get(@Nullable Object rowKey, @Nullable Object columnKey) {
      Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
      Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
      return rowIndex != null && columnIndex != null?this.values[rowIndex.intValue()][columnIndex.intValue()]:null;
   }

   public int size() {
      return this.iterationOrderRow.length;
   }

   Table.Cell getCell(int index) {
      int rowIndex = this.iterationOrderRow[index];
      int columnIndex = this.iterationOrderColumn[index];
      R rowKey = this.rowKeySet().asList().get(rowIndex);
      C columnKey = this.columnKeySet().asList().get(columnIndex);
      V value = this.values[rowIndex][columnIndex];
      return cellOf(rowKey, columnKey, value);
   }

   Object getValue(int index) {
      return this.values[this.iterationOrderRow[index]][this.iterationOrderColumn[index]];
   }

   private final class Column extends DenseImmutableTable.ImmutableArrayMap {
      private final int columnIndex;

      Column(int columnIndex) {
         super(DenseImmutableTable.this.columnCounts[columnIndex]);
         this.columnIndex = columnIndex;
      }

      ImmutableMap keyToIndex() {
         return DenseImmutableTable.this.rowKeyToIndex;
      }

      Object getValue(int keyIndex) {
         return DenseImmutableTable.this.values[keyIndex][this.columnIndex];
      }

      boolean isPartialView() {
         return true;
      }
   }

   private final class ColumnMap extends DenseImmutableTable.ImmutableArrayMap {
      private ColumnMap() {
         super(DenseImmutableTable.this.columnCounts.length);
      }

      ImmutableMap keyToIndex() {
         return DenseImmutableTable.this.columnKeyToIndex;
      }

      Map getValue(int keyIndex) {
         return DenseImmutableTable.this.new Column(keyIndex);
      }

      boolean isPartialView() {
         return false;
      }
   }

   private abstract static class ImmutableArrayMap extends ImmutableMap {
      private final int size;

      ImmutableArrayMap(int size) {
         this.size = size;
      }

      abstract ImmutableMap keyToIndex();

      private boolean isFull() {
         return this.size == this.keyToIndex().size();
      }

      Object getKey(int index) {
         return this.keyToIndex().keySet().asList().get(index);
      }

      @Nullable
      abstract Object getValue(int var1);

      ImmutableSet createKeySet() {
         return this.isFull()?this.keyToIndex().keySet():super.createKeySet();
      }

      public int size() {
         return this.size;
      }

      public Object get(@Nullable Object key) {
         Integer keyIndex = (Integer)this.keyToIndex().get(key);
         return keyIndex == null?null:this.getValue(keyIndex.intValue());
      }

      ImmutableSet createEntrySet() {
         return new ImmutableMapEntrySet() {
            ImmutableMap map() {
               return ImmutableArrayMap.this;
            }

            public UnmodifiableIterator iterator() {
               return new AbstractIterator() {
                  private int index = -1;
                  private final int maxIndex = ImmutableArrayMap.this.keyToIndex().size();

                  protected Entry computeNext() {
                     ++this.index;

                     while(this.index < this.maxIndex) {
                        V value = ImmutableArrayMap.this.getValue(this.index);
                        if(value != null) {
                           return Maps.immutableEntry(ImmutableArrayMap.this.getKey(this.index), value);
                        }

                        ++this.index;
                     }

                     return (Entry)this.endOfData();
                  }
               };
            }
         };
      }
   }

   private final class Row extends DenseImmutableTable.ImmutableArrayMap {
      private final int rowIndex;

      Row(int rowIndex) {
         super(DenseImmutableTable.this.rowCounts[rowIndex]);
         this.rowIndex = rowIndex;
      }

      ImmutableMap keyToIndex() {
         return DenseImmutableTable.this.columnKeyToIndex;
      }

      Object getValue(int keyIndex) {
         return DenseImmutableTable.this.values[this.rowIndex][keyIndex];
      }

      boolean isPartialView() {
         return true;
      }
   }

   private final class RowMap extends DenseImmutableTable.ImmutableArrayMap {
      private RowMap() {
         super(DenseImmutableTable.this.rowCounts.length);
      }

      ImmutableMap keyToIndex() {
         return DenseImmutableTable.this.rowKeyToIndex;
      }

      Map getValue(int keyIndex) {
         return DenseImmutableTable.this.new Row(keyIndex);
      }

      boolean isPartialView() {
         return false;
      }
   }
}
