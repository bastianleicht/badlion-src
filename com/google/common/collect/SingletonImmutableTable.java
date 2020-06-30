package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

@GwtCompatible
class SingletonImmutableTable extends ImmutableTable {
   final Object singleRowKey;
   final Object singleColumnKey;
   final Object singleValue;

   SingletonImmutableTable(Object rowKey, Object columnKey, Object value) {
      this.singleRowKey = Preconditions.checkNotNull(rowKey);
      this.singleColumnKey = Preconditions.checkNotNull(columnKey);
      this.singleValue = Preconditions.checkNotNull(value);
   }

   SingletonImmutableTable(Table.Cell cell) {
      this(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
   }

   public ImmutableMap column(Object columnKey) {
      Preconditions.checkNotNull(columnKey);
      return this.containsColumn(columnKey)?ImmutableMap.of(this.singleRowKey, this.singleValue):ImmutableMap.of();
   }

   public ImmutableMap columnMap() {
      return ImmutableMap.of(this.singleColumnKey, ImmutableMap.of(this.singleRowKey, this.singleValue));
   }

   public ImmutableMap rowMap() {
      return ImmutableMap.of(this.singleRowKey, ImmutableMap.of(this.singleColumnKey, this.singleValue));
   }

   public int size() {
      return 1;
   }

   ImmutableSet createCellSet() {
      return ImmutableSet.of(cellOf(this.singleRowKey, this.singleColumnKey, this.singleValue));
   }

   ImmutableCollection createValues() {
      return ImmutableSet.of(this.singleValue);
   }
}
