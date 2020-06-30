package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractTable;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingTable;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.RowSortedTable;
import com.google.common.collect.StandardTable;
import com.google.common.collect.Table;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public final class Tables {
   private static final Function UNMODIFIABLE_WRAPPER = new Function() {
      public Map apply(Map input) {
         return Collections.unmodifiableMap(input);
      }
   };

   public static Table.Cell immutableCell(@Nullable Object rowKey, @Nullable Object columnKey, @Nullable Object value) {
      return new Tables.ImmutableCell(rowKey, columnKey, value);
   }

   public static Table transpose(Table table) {
      return (Table)(table instanceof Tables.TransposeTable?((Tables.TransposeTable)table).original:new Tables.TransposeTable(table));
   }

   @Beta
   public static Table newCustomTable(Map backingMap, Supplier factory) {
      Preconditions.checkArgument(backingMap.isEmpty());
      Preconditions.checkNotNull(factory);
      return new StandardTable(backingMap, factory);
   }

   @Beta
   public static Table transformValues(Table fromTable, Function function) {
      return new Tables.TransformedTable(fromTable, function);
   }

   public static Table unmodifiableTable(Table table) {
      return new Tables.UnmodifiableTable(table);
   }

   @Beta
   public static RowSortedTable unmodifiableRowSortedTable(RowSortedTable table) {
      return new Tables.UnmodifiableRowSortedMap(table);
   }

   private static Function unmodifiableWrapper() {
      return UNMODIFIABLE_WRAPPER;
   }

   static boolean equalsImpl(Table table, @Nullable Object obj) {
      if(obj == table) {
         return true;
      } else if(obj instanceof Table) {
         Table<?, ?, ?> that = (Table)obj;
         return table.cellSet().equals(that.cellSet());
      } else {
         return false;
      }
   }

   abstract static class AbstractCell implements Table.Cell {
      public boolean equals(Object obj) {
         if(obj == this) {
            return true;
         } else if(!(obj instanceof Table.Cell)) {
            return false;
         } else {
            Table.Cell<?, ?, ?> other = (Table.Cell)obj;
            return Objects.equal(this.getRowKey(), other.getRowKey()) && Objects.equal(this.getColumnKey(), other.getColumnKey()) && Objects.equal(this.getValue(), other.getValue());
         }
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.getRowKey(), this.getColumnKey(), this.getValue()});
      }

      public String toString() {
         return "(" + this.getRowKey() + "," + this.getColumnKey() + ")=" + this.getValue();
      }
   }

   static final class ImmutableCell extends Tables.AbstractCell implements Serializable {
      private final Object rowKey;
      private final Object columnKey;
      private final Object value;
      private static final long serialVersionUID = 0L;

      ImmutableCell(@Nullable Object rowKey, @Nullable Object columnKey, @Nullable Object value) {
         this.rowKey = rowKey;
         this.columnKey = columnKey;
         this.value = value;
      }

      public Object getRowKey() {
         return this.rowKey;
      }

      public Object getColumnKey() {
         return this.columnKey;
      }

      public Object getValue() {
         return this.value;
      }
   }

   private static class TransformedTable extends AbstractTable {
      final Table fromTable;
      final Function function;

      TransformedTable(Table fromTable, Function function) {
         this.fromTable = (Table)Preconditions.checkNotNull(fromTable);
         this.function = (Function)Preconditions.checkNotNull(function);
      }

      public boolean contains(Object rowKey, Object columnKey) {
         return this.fromTable.contains(rowKey, columnKey);
      }

      public Object get(Object rowKey, Object columnKey) {
         return this.contains(rowKey, columnKey)?this.function.apply(this.fromTable.get(rowKey, columnKey)):null;
      }

      public int size() {
         return this.fromTable.size();
      }

      public void clear() {
         this.fromTable.clear();
      }

      public Object put(Object rowKey, Object columnKey, Object value) {
         throw new UnsupportedOperationException();
      }

      public void putAll(Table table) {
         throw new UnsupportedOperationException();
      }

      public Object remove(Object rowKey, Object columnKey) {
         return this.contains(rowKey, columnKey)?this.function.apply(this.fromTable.remove(rowKey, columnKey)):null;
      }

      public Map row(Object rowKey) {
         return Maps.transformValues(this.fromTable.row(rowKey), this.function);
      }

      public Map column(Object columnKey) {
         return Maps.transformValues(this.fromTable.column(columnKey), this.function);
      }

      Function cellFunction() {
         return new Function() {
            public Table.Cell apply(Table.Cell cell) {
               return Tables.immutableCell(cell.getRowKey(), cell.getColumnKey(), TransformedTable.this.function.apply(cell.getValue()));
            }
         };
      }

      Iterator cellIterator() {
         return Iterators.transform(this.fromTable.cellSet().iterator(), this.cellFunction());
      }

      public Set rowKeySet() {
         return this.fromTable.rowKeySet();
      }

      public Set columnKeySet() {
         return this.fromTable.columnKeySet();
      }

      Collection createValues() {
         return Collections2.transform(this.fromTable.values(), this.function);
      }

      public Map rowMap() {
         Function<Map<C, V1>, Map<C, V2>> rowFunction = new Function() {
            public Map apply(Map row) {
               return Maps.transformValues(row, TransformedTable.this.function);
            }
         };
         return Maps.transformValues(this.fromTable.rowMap(), rowFunction);
      }

      public Map columnMap() {
         Function<Map<R, V1>, Map<R, V2>> columnFunction = new Function() {
            public Map apply(Map column) {
               return Maps.transformValues(column, TransformedTable.this.function);
            }
         };
         return Maps.transformValues(this.fromTable.columnMap(), columnFunction);
      }
   }

   private static class TransposeTable extends AbstractTable {
      final Table original;
      private static final Function TRANSPOSE_CELL = new Function() {
         public Table.Cell apply(Table.Cell cell) {
            return Tables.immutableCell(cell.getColumnKey(), cell.getRowKey(), cell.getValue());
         }
      };

      TransposeTable(Table original) {
         this.original = (Table)Preconditions.checkNotNull(original);
      }

      public void clear() {
         this.original.clear();
      }

      public Map column(Object columnKey) {
         return this.original.row(columnKey);
      }

      public Set columnKeySet() {
         return this.original.rowKeySet();
      }

      public Map columnMap() {
         return this.original.rowMap();
      }

      public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
         return this.original.contains(columnKey, rowKey);
      }

      public boolean containsColumn(@Nullable Object columnKey) {
         return this.original.containsRow(columnKey);
      }

      public boolean containsRow(@Nullable Object rowKey) {
         return this.original.containsColumn(rowKey);
      }

      public boolean containsValue(@Nullable Object value) {
         return this.original.containsValue(value);
      }

      public Object get(@Nullable Object rowKey, @Nullable Object columnKey) {
         return this.original.get(columnKey, rowKey);
      }

      public Object put(Object rowKey, Object columnKey, Object value) {
         return this.original.put(columnKey, rowKey, value);
      }

      public void putAll(Table table) {
         this.original.putAll(Tables.transpose(table));
      }

      public Object remove(@Nullable Object rowKey, @Nullable Object columnKey) {
         return this.original.remove(columnKey, rowKey);
      }

      public Map row(Object rowKey) {
         return this.original.column(rowKey);
      }

      public Set rowKeySet() {
         return this.original.columnKeySet();
      }

      public Map rowMap() {
         return this.original.columnMap();
      }

      public int size() {
         return this.original.size();
      }

      public Collection values() {
         return this.original.values();
      }

      Iterator cellIterator() {
         return Iterators.transform(this.original.cellSet().iterator(), TRANSPOSE_CELL);
      }
   }

   static final class UnmodifiableRowSortedMap extends Tables.UnmodifiableTable implements RowSortedTable {
      private static final long serialVersionUID = 0L;

      public UnmodifiableRowSortedMap(RowSortedTable delegate) {
         super(delegate);
      }

      protected RowSortedTable delegate() {
         return (RowSortedTable)super.delegate();
      }

      public SortedMap rowMap() {
         Function<Map<C, V>, Map<C, V>> wrapper = Tables.UNMODIFIABLE_WRAPPER;
         return Collections.unmodifiableSortedMap(Maps.transformValues(this.delegate().rowMap(), wrapper));
      }

      public SortedSet rowKeySet() {
         return Collections.unmodifiableSortedSet(this.delegate().rowKeySet());
      }
   }

   private static class UnmodifiableTable extends ForwardingTable implements Serializable {
      final Table delegate;
      private static final long serialVersionUID = 0L;

      UnmodifiableTable(Table delegate) {
         this.delegate = (Table)Preconditions.checkNotNull(delegate);
      }

      protected Table delegate() {
         return this.delegate;
      }

      public Set cellSet() {
         return Collections.unmodifiableSet(super.cellSet());
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public Map column(@Nullable Object columnKey) {
         return Collections.unmodifiableMap(super.column(columnKey));
      }

      public Set columnKeySet() {
         return Collections.unmodifiableSet(super.columnKeySet());
      }

      public Map columnMap() {
         Function<Map<R, V>, Map<R, V>> wrapper = Tables.UNMODIFIABLE_WRAPPER;
         return Collections.unmodifiableMap(Maps.transformValues(super.columnMap(), wrapper));
      }

      public Object put(@Nullable Object rowKey, @Nullable Object columnKey, @Nullable Object value) {
         throw new UnsupportedOperationException();
      }

      public void putAll(Table table) {
         throw new UnsupportedOperationException();
      }

      public Object remove(@Nullable Object rowKey, @Nullable Object columnKey) {
         throw new UnsupportedOperationException();
      }

      public Map row(@Nullable Object rowKey) {
         return Collections.unmodifiableMap(super.row(rowKey));
      }

      public Set rowKeySet() {
         return Collections.unmodifiableSet(super.rowKeySet());
      }

      public Map rowMap() {
         Function<Map<C, V>, Map<C, V>> wrapper = Tables.UNMODIFIABLE_WRAPPER;
         return Collections.unmodifiableMap(Maps.transformValues(super.rowMap(), wrapper));
      }

      public Collection values() {
         return Collections.unmodifiableCollection(super.values());
      }
   }
}
