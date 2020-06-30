package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Supplier;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.StandardTable;
import com.google.common.collect.Table;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
public class HashBasedTable extends StandardTable {
   private static final long serialVersionUID = 0L;

   public static HashBasedTable create() {
      return new HashBasedTable(new HashMap(), new HashBasedTable.Factory(0));
   }

   public static HashBasedTable create(int expectedRows, int expectedCellsPerRow) {
      CollectPreconditions.checkNonnegative(expectedCellsPerRow, "expectedCellsPerRow");
      Map<R, Map<C, V>> backingMap = Maps.newHashMapWithExpectedSize(expectedRows);
      return new HashBasedTable(backingMap, new HashBasedTable.Factory(expectedCellsPerRow));
   }

   public static HashBasedTable create(Table table) {
      HashBasedTable<R, C, V> result = create();
      result.putAll(table);
      return result;
   }

   HashBasedTable(Map backingMap, HashBasedTable.Factory factory) {
      super(backingMap, factory);
   }

   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
      return super.contains(rowKey, columnKey);
   }

   public boolean containsColumn(@Nullable Object columnKey) {
      return super.containsColumn(columnKey);
   }

   public boolean containsRow(@Nullable Object rowKey) {
      return super.containsRow(rowKey);
   }

   public boolean containsValue(@Nullable Object value) {
      return super.containsValue(value);
   }

   public Object get(@Nullable Object rowKey, @Nullable Object columnKey) {
      return super.get(rowKey, columnKey);
   }

   public boolean equals(@Nullable Object obj) {
      return super.equals(obj);
   }

   public Object remove(@Nullable Object rowKey, @Nullable Object columnKey) {
      return super.remove(rowKey, columnKey);
   }

   private static class Factory implements Supplier, Serializable {
      final int expectedSize;
      private static final long serialVersionUID = 0L;

      Factory(int expectedSize) {
         this.expectedSize = expectedSize;
      }

      public Map get() {
         return Maps.newHashMapWithExpectedSize(this.expectedSize);
      }
   }
}
