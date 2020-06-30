package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.RegularImmutableTable;
import com.google.common.collect.Table;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.concurrent.Immutable;

@GwtCompatible
@Immutable
final class SparseImmutableTable extends RegularImmutableTable {
   private final ImmutableMap rowMap;
   private final ImmutableMap columnMap;
   private final int[] iterationOrderRow;
   private final int[] iterationOrderColumn;

   SparseImmutableTable(ImmutableList cellList, ImmutableSet rowSpace, ImmutableSet columnSpace) {
      Map<R, Integer> rowIndex = Maps.newHashMap();
      Map<R, Map<C, V>> rows = Maps.newLinkedHashMap();

      for(R row : rowSpace) {
         rowIndex.put(row, Integer.valueOf(rows.size()));
         rows.put(row, new LinkedHashMap());
      }

      Map<C, Map<R, V>> columns = Maps.newLinkedHashMap();

      for(C col : columnSpace) {
         columns.put(col, new LinkedHashMap());
      }

      int[] iterationOrderRow = new int[cellList.size()];
      int[] iterationOrderColumn = new int[cellList.size()];

      for(int i = 0; i < cellList.size(); ++i) {
         Table.Cell<R, C, V> cell = (Table.Cell)cellList.get(i);
         R rowKey = cell.getRowKey();
         C columnKey = cell.getColumnKey();
         V value = cell.getValue();
         iterationOrderRow[i] = ((Integer)rowIndex.get(rowKey)).intValue();
         Map<C, V> thisRow = (Map)rows.get(rowKey);
         iterationOrderColumn[i] = thisRow.size();
         V oldValue = thisRow.put(columnKey, value);
         if(oldValue != null) {
            throw new IllegalArgumentException("Duplicate value for row=" + rowKey + ", column=" + columnKey + ": " + value + ", " + oldValue);
         }

         ((Map)columns.get(columnKey)).put(rowKey, value);
      }

      this.iterationOrderRow = iterationOrderRow;
      this.iterationOrderColumn = iterationOrderColumn;
      ImmutableMap.Builder<R, Map<C, V>> rowBuilder = ImmutableMap.builder();

      for(Entry<R, Map<C, V>> row : rows.entrySet()) {
         rowBuilder.put(row.getKey(), ImmutableMap.copyOf((Map)row.getValue()));
      }

      this.rowMap = rowBuilder.build();
      ImmutableMap.Builder<C, Map<R, V>> columnBuilder = ImmutableMap.builder();

      for(Entry<C, Map<R, V>> col : columns.entrySet()) {
         columnBuilder.put(col.getKey(), ImmutableMap.copyOf((Map)col.getValue()));
      }

      this.columnMap = columnBuilder.build();
   }

   public ImmutableMap columnMap() {
      return this.columnMap;
   }

   public ImmutableMap rowMap() {
      return this.rowMap;
   }

   public int size() {
      return this.iterationOrderRow.length;
   }

   Table.Cell getCell(int index) {
      int rowIndex = this.iterationOrderRow[index];
      Entry<R, Map<C, V>> rowEntry = (Entry)this.rowMap.entrySet().asList().get(rowIndex);
      ImmutableMap<C, V> row = (ImmutableMap)rowEntry.getValue();
      int columnIndex = this.iterationOrderColumn[index];
      Entry<C, V> colEntry = (Entry)row.entrySet().asList().get(columnIndex);
      return cellOf(rowEntry.getKey(), colEntry.getKey(), colEntry.getValue());
   }

   Object getValue(int index) {
      int rowIndex = this.iterationOrderRow[index];
      ImmutableMap<C, V> row = (ImmutableMap)this.rowMap.values().asList().get(rowIndex);
      int columnIndex = this.iterationOrderColumn[index];
      return row.values().asList().get(columnIndex);
   }
}
