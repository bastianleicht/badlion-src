package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractTable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.RegularImmutableTable;
import com.google.common.collect.SingletonImmutableTable;
import com.google.common.collect.SparseImmutableTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ImmutableTable extends AbstractTable {
   private static final ImmutableTable EMPTY = new SparseImmutableTable(ImmutableList.of(), ImmutableSet.of(), ImmutableSet.of());

   public static ImmutableTable of() {
      return EMPTY;
   }

   public static ImmutableTable of(Object rowKey, Object columnKey, Object value) {
      return new SingletonImmutableTable(rowKey, columnKey, value);
   }

   public static ImmutableTable copyOf(Table table) {
      if(table instanceof ImmutableTable) {
         ImmutableTable<R, C, V> parameterizedTable = (ImmutableTable)table;
         return parameterizedTable;
      } else {
         int size = table.size();
         switch(size) {
         case 0:
            return of();
         case 1:
            Table.Cell<? extends R, ? extends C, ? extends V> onlyCell = (Table.Cell)Iterables.getOnlyElement(table.cellSet());
            return of(onlyCell.getRowKey(), onlyCell.getColumnKey(), onlyCell.getValue());
         default:
            ImmutableSet.Builder<Table.Cell<R, C, V>> cellSetBuilder = ImmutableSet.builder();

            for(Table.Cell<? extends R, ? extends C, ? extends V> cell : table.cellSet()) {
               cellSetBuilder.add((Object)cellOf(cell.getRowKey(), cell.getColumnKey(), cell.getValue()));
            }

            return RegularImmutableTable.forCells(cellSetBuilder.build());
         }
      }
   }

   public static ImmutableTable.Builder builder() {
      return new ImmutableTable.Builder();
   }

   static Table.Cell cellOf(Object rowKey, Object columnKey, Object value) {
      return Tables.immutableCell(Preconditions.checkNotNull(rowKey), Preconditions.checkNotNull(columnKey), Preconditions.checkNotNull(value));
   }

   public ImmutableSet cellSet() {
      return (ImmutableSet)super.cellSet();
   }

   abstract ImmutableSet createCellSet();

   final UnmodifiableIterator cellIterator() {
      throw new AssertionError("should never be called");
   }

   public ImmutableCollection values() {
      return (ImmutableCollection)super.values();
   }

   abstract ImmutableCollection createValues();

   final Iterator valuesIterator() {
      throw new AssertionError("should never be called");
   }

   public ImmutableMap column(Object columnKey) {
      Preconditions.checkNotNull(columnKey);
      return (ImmutableMap)Objects.firstNonNull((ImmutableMap)this.columnMap().get(columnKey), ImmutableMap.of());
   }

   public ImmutableSet columnKeySet() {
      return this.columnMap().keySet();
   }

   public abstract ImmutableMap columnMap();

   public ImmutableMap row(Object rowKey) {
      Preconditions.checkNotNull(rowKey);
      return (ImmutableMap)Objects.firstNonNull((ImmutableMap)this.rowMap().get(rowKey), ImmutableMap.of());
   }

   public ImmutableSet rowKeySet() {
      return this.rowMap().keySet();
   }

   public abstract ImmutableMap rowMap();

   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
      return this.get(rowKey, columnKey) != null;
   }

   public boolean containsValue(@Nullable Object value) {
      return this.values().contains(value);
   }

   /** @deprecated */
   @Deprecated
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final Object put(Object rowKey, Object columnKey, Object value) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final void putAll(Table table) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final Object remove(Object rowKey, Object columnKey) {
      throw new UnsupportedOperationException();
   }

   public static final class Builder {
      private final List cells = Lists.newArrayList();
      private Comparator rowComparator;
      private Comparator columnComparator;

      public ImmutableTable.Builder orderRowsBy(Comparator rowComparator) {
         this.rowComparator = (Comparator)Preconditions.checkNotNull(rowComparator);
         return this;
      }

      public ImmutableTable.Builder orderColumnsBy(Comparator columnComparator) {
         this.columnComparator = (Comparator)Preconditions.checkNotNull(columnComparator);
         return this;
      }

      public ImmutableTable.Builder put(Object rowKey, Object columnKey, Object value) {
         this.cells.add(ImmutableTable.cellOf(rowKey, columnKey, value));
         return this;
      }

      public ImmutableTable.Builder put(Table.Cell cell) {
         if(cell instanceof Tables.ImmutableCell) {
            Preconditions.checkNotNull(cell.getRowKey());
            Preconditions.checkNotNull(cell.getColumnKey());
            Preconditions.checkNotNull(cell.getValue());
            this.cells.add(cell);
         } else {
            this.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
         }

         return this;
      }

      public ImmutableTable.Builder putAll(Table table) {
         for(Table.Cell<? extends R, ? extends C, ? extends V> cell : table.cellSet()) {
            this.put(cell);
         }

         return this;
      }

      public ImmutableTable build() {
         int size = this.cells.size();
         switch(size) {
         case 0:
            return ImmutableTable.EMPTY;
         case 1:
            return new SingletonImmutableTable((Table.Cell)Iterables.getOnlyElement(this.cells));
         default:
            return RegularImmutableTable.forCells(this.cells, this.rowComparator, this.columnComparator);
         }
      }
   }
}
