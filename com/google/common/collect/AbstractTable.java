package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.TransformedIterator;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractTable implements Table {
   private transient Set cellSet;
   private transient Collection values;

   public boolean containsRow(@Nullable Object rowKey) {
      return Maps.safeContainsKey(this.rowMap(), rowKey);
   }

   public boolean containsColumn(@Nullable Object columnKey) {
      return Maps.safeContainsKey(this.columnMap(), columnKey);
   }

   public Set rowKeySet() {
      return this.rowMap().keySet();
   }

   public Set columnKeySet() {
      return this.columnMap().keySet();
   }

   public boolean containsValue(@Nullable Object value) {
      for(Map<C, V> row : this.rowMap().values()) {
         if(row.containsValue(value)) {
            return true;
         }
      }

      return false;
   }

   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
      Map<C, V> row = (Map)Maps.safeGet(this.rowMap(), rowKey);
      return row != null && Maps.safeContainsKey(row, columnKey);
   }

   public Object get(@Nullable Object rowKey, @Nullable Object columnKey) {
      Map<C, V> row = (Map)Maps.safeGet(this.rowMap(), rowKey);
      return row == null?null:Maps.safeGet(row, columnKey);
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public void clear() {
      Iterators.clear(this.cellSet().iterator());
   }

   public Object remove(@Nullable Object rowKey, @Nullable Object columnKey) {
      Map<C, V> row = (Map)Maps.safeGet(this.rowMap(), rowKey);
      return row == null?null:Maps.safeRemove(row, columnKey);
   }

   public Object put(Object rowKey, Object columnKey, Object value) {
      return this.row(rowKey).put(columnKey, value);
   }

   public void putAll(Table table) {
      for(Table.Cell<? extends R, ? extends C, ? extends V> cell : table.cellSet()) {
         this.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
      }

   }

   public Set cellSet() {
      Set<Table.Cell<R, C, V>> result = this.cellSet;
      return result == null?(this.cellSet = this.createCellSet()):result;
   }

   Set createCellSet() {
      return new AbstractTable.CellSet();
   }

   abstract Iterator cellIterator();

   public Collection values() {
      Collection<V> result = this.values;
      return result == null?(this.values = this.createValues()):result;
   }

   Collection createValues() {
      return new AbstractTable.Values();
   }

   Iterator valuesIterator() {
      return new TransformedIterator(this.cellSet().iterator()) {
         Object transform(Table.Cell cell) {
            return cell.getValue();
         }
      };
   }

   public boolean equals(@Nullable Object obj) {
      return Tables.equalsImpl(this, obj);
   }

   public int hashCode() {
      return this.cellSet().hashCode();
   }

   public String toString() {
      return this.rowMap().toString();
   }

   class CellSet extends AbstractSet {
      public boolean contains(Object o) {
         if(!(o instanceof Table.Cell)) {
            return false;
         } else {
            Table.Cell<?, ?, ?> cell = (Table.Cell)o;
            Map<C, V> row = (Map)Maps.safeGet(AbstractTable.this.rowMap(), cell.getRowKey());
            return row != null && Collections2.safeContains(row.entrySet(), Maps.immutableEntry(cell.getColumnKey(), cell.getValue()));
         }
      }

      public boolean remove(@Nullable Object o) {
         if(!(o instanceof Table.Cell)) {
            return false;
         } else {
            Table.Cell<?, ?, ?> cell = (Table.Cell)o;
            Map<C, V> row = (Map)Maps.safeGet(AbstractTable.this.rowMap(), cell.getRowKey());
            return row != null && Collections2.safeRemove(row.entrySet(), Maps.immutableEntry(cell.getColumnKey(), cell.getValue()));
         }
      }

      public void clear() {
         AbstractTable.this.clear();
      }

      public Iterator iterator() {
         return AbstractTable.this.cellIterator();
      }

      public int size() {
         return AbstractTable.this.size();
      }
   }

   class Values extends AbstractCollection {
      public Iterator iterator() {
         return AbstractTable.this.valuesIterator();
      }

      public boolean contains(Object o) {
         return AbstractTable.this.containsValue(o);
      }

      public void clear() {
         AbstractTable.this.clear();
      }

      public int size() {
         return AbstractTable.this.size();
      }
   }
}
