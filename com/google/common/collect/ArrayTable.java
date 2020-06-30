package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIndexedListIterator;
import com.google.common.collect.AbstractMapEntry;
import com.google.common.collect.AbstractTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@Beta
@GwtCompatible(
   emulated = true
)
public final class ArrayTable extends AbstractTable implements Serializable {
   private final ImmutableList rowList;
   private final ImmutableList columnList;
   private final ImmutableMap rowKeyToIndex;
   private final ImmutableMap columnKeyToIndex;
   private final Object[][] array;
   private transient ArrayTable.ColumnMap columnMap;
   private transient ArrayTable.RowMap rowMap;
   private static final long serialVersionUID = 0L;

   public static ArrayTable create(Iterable rowKeys, Iterable columnKeys) {
      return new ArrayTable(rowKeys, columnKeys);
   }

   public static ArrayTable create(Table table) {
      return table instanceof ArrayTable?new ArrayTable((ArrayTable)table):new ArrayTable(table);
   }

   private ArrayTable(Iterable rowKeys, Iterable columnKeys) {
      this.rowList = ImmutableList.copyOf(rowKeys);
      this.columnList = ImmutableList.copyOf(columnKeys);
      Preconditions.checkArgument(!this.rowList.isEmpty());
      Preconditions.checkArgument(!this.columnList.isEmpty());
      this.rowKeyToIndex = index(this.rowList);
      this.columnKeyToIndex = index(this.columnList);
      V[][] tmpArray = (Object[][])(new Object[this.rowList.size()][this.columnList.size()]);
      this.array = tmpArray;
      this.eraseAll();
   }

   private static ImmutableMap index(List list) {
      ImmutableMap.Builder<E, Integer> columnBuilder = ImmutableMap.builder();

      for(int i = 0; i < list.size(); ++i) {
         columnBuilder.put(list.get(i), Integer.valueOf(i));
      }

      return columnBuilder.build();
   }

   private ArrayTable(Table table) {
      this(table.rowKeySet(), table.columnKeySet());
      this.putAll(table);
   }

   private ArrayTable(ArrayTable table) {
      this.rowList = table.rowList;
      this.columnList = table.columnList;
      this.rowKeyToIndex = table.rowKeyToIndex;
      this.columnKeyToIndex = table.columnKeyToIndex;
      V[][] copy = (Object[][])(new Object[this.rowList.size()][this.columnList.size()]);
      this.array = copy;
      this.eraseAll();

      for(int i = 0; i < this.rowList.size(); ++i) {
         System.arraycopy(table.array[i], 0, copy[i], 0, table.array[i].length);
      }

   }

   public ImmutableList rowKeyList() {
      return this.rowList;
   }

   public ImmutableList columnKeyList() {
      return this.columnList;
   }

   public Object at(int rowIndex, int columnIndex) {
      Preconditions.checkElementIndex(rowIndex, this.rowList.size());
      Preconditions.checkElementIndex(columnIndex, this.columnList.size());
      return this.array[rowIndex][columnIndex];
   }

   public Object set(int rowIndex, int columnIndex, @Nullable Object value) {
      Preconditions.checkElementIndex(rowIndex, this.rowList.size());
      Preconditions.checkElementIndex(columnIndex, this.columnList.size());
      V oldValue = this.array[rowIndex][columnIndex];
      this.array[rowIndex][columnIndex] = value;
      return oldValue;
   }

   @GwtIncompatible("reflection")
   public Object[][] toArray(Class valueClass) {
      V[][] copy = (Object[][])((Object[][])Array.newInstance(valueClass, new int[]{this.rowList.size(), this.columnList.size()}));

      for(int i = 0; i < this.rowList.size(); ++i) {
         System.arraycopy(this.array[i], 0, copy[i], 0, this.array[i].length);
      }

      return copy;
   }

   /** @deprecated */
   @Deprecated
   public void clear() {
      throw new UnsupportedOperationException();
   }

   public void eraseAll() {
      for(V[] row : this.array) {
         Arrays.fill(row, (Object)null);
      }

   }

   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
      return this.containsRow(rowKey) && this.containsColumn(columnKey);
   }

   public boolean containsColumn(@Nullable Object columnKey) {
      return this.columnKeyToIndex.containsKey(columnKey);
   }

   public boolean containsRow(@Nullable Object rowKey) {
      return this.rowKeyToIndex.containsKey(rowKey);
   }

   public boolean containsValue(@Nullable Object value) {
      for(V[] row : this.array) {
         for(V element : row) {
            if(Objects.equal(value, element)) {
               return true;
            }
         }
      }

      return false;
   }

   public Object get(@Nullable Object rowKey, @Nullable Object columnKey) {
      Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
      Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
      return rowIndex != null && columnIndex != null?this.at(rowIndex.intValue(), columnIndex.intValue()):null;
   }

   public boolean isEmpty() {
      return false;
   }

   public Object put(Object rowKey, Object columnKey, @Nullable Object value) {
      Preconditions.checkNotNull(rowKey);
      Preconditions.checkNotNull(columnKey);
      Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
      Preconditions.checkArgument(rowIndex != null, "Row %s not in %s", new Object[]{rowKey, this.rowList});
      Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
      Preconditions.checkArgument(columnIndex != null, "Column %s not in %s", new Object[]{columnKey, this.columnList});
      return this.set(rowIndex.intValue(), columnIndex.intValue(), value);
   }

   public void putAll(Table table) {
      super.putAll(table);
   }

   /** @deprecated */
   @Deprecated
   public Object remove(Object rowKey, Object columnKey) {
      throw new UnsupportedOperationException();
   }

   public Object erase(@Nullable Object rowKey, @Nullable Object columnKey) {
      Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
      Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
      return rowIndex != null && columnIndex != null?this.set(rowIndex.intValue(), columnIndex.intValue(), (Object)null):null;
   }

   public int size() {
      return this.rowList.size() * this.columnList.size();
   }

   public Set cellSet() {
      return super.cellSet();
   }

   Iterator cellIterator() {
      return new AbstractIndexedListIterator(this.size()) {
         protected Table.Cell get(final int index) {
            return new Tables.AbstractCell() {
               final int rowIndex;
               final int columnIndex;

               {
                  this.rowIndex = index / ArrayTable.this.columnList.size();
                  this.columnIndex = index % ArrayTable.this.columnList.size();
               }

               public Object getRowKey() {
                  return ArrayTable.this.rowList.get(this.rowIndex);
               }

               public Object getColumnKey() {
                  return ArrayTable.this.columnList.get(this.columnIndex);
               }

               public Object getValue() {
                  return ArrayTable.this.at(this.rowIndex, this.columnIndex);
               }
            };
         }
      };
   }

   public Map column(Object columnKey) {
      Preconditions.checkNotNull(columnKey);
      Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
      return (Map)(columnIndex == null?ImmutableMap.of():new ArrayTable.Column(columnIndex.intValue()));
   }

   public ImmutableSet columnKeySet() {
      return this.columnKeyToIndex.keySet();
   }

   public Map columnMap() {
      ArrayTable<R, C, V>.ColumnMap map = this.columnMap;
      return map == null?(this.columnMap = new ArrayTable.ColumnMap()):map;
   }

   public Map row(Object rowKey) {
      Preconditions.checkNotNull(rowKey);
      Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
      return (Map)(rowIndex == null?ImmutableMap.of():new ArrayTable.Row(rowIndex.intValue()));
   }

   public ImmutableSet rowKeySet() {
      return this.rowKeyToIndex.keySet();
   }

   public Map rowMap() {
      ArrayTable<R, C, V>.RowMap map = this.rowMap;
      return map == null?(this.rowMap = new ArrayTable.RowMap()):map;
   }

   public Collection values() {
      return super.values();
   }

   private abstract static class ArrayMap extends Maps.ImprovedAbstractMap {
      private final ImmutableMap keyIndex;

      private ArrayMap(ImmutableMap keyIndex) {
         this.keyIndex = keyIndex;
      }

      public Set keySet() {
         return this.keyIndex.keySet();
      }

      Object getKey(int index) {
         return this.keyIndex.keySet().asList().get(index);
      }

      abstract String getKeyRole();

      @Nullable
      abstract Object getValue(int var1);

      @Nullable
      abstract Object setValue(int var1, Object var2);

      public int size() {
         return this.keyIndex.size();
      }

      public boolean isEmpty() {
         return this.keyIndex.isEmpty();
      }

      protected Set createEntrySet() {
         return new Maps.EntrySet() {
            Map map() {
               return ArrayMap.this;
            }

            public Iterator iterator() {
               return new AbstractIndexedListIterator(this.size()) {
                  protected Entry get(final int index) {
                     return new AbstractMapEntry() {
                        public Object getKey() {
                           return ArrayMap.this.getKey(index);
                        }

                        public Object getValue() {
                           return ArrayMap.this.getValue(index);
                        }

                        public Object setValue(Object value) {
                           return ArrayMap.this.setValue(index, value);
                        }
                     };
                  }
               };
            }
         };
      }

      public boolean containsKey(@Nullable Object key) {
         return this.keyIndex.containsKey(key);
      }

      public Object get(@Nullable Object key) {
         Integer index = (Integer)this.keyIndex.get(key);
         return index == null?null:this.getValue(index.intValue());
      }

      public Object put(Object key, Object value) {
         Integer index = (Integer)this.keyIndex.get(key);
         if(index == null) {
            throw new IllegalArgumentException(this.getKeyRole() + " " + key + " not in " + this.keyIndex.keySet());
         } else {
            return this.setValue(index.intValue(), value);
         }
      }

      public Object remove(Object key) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }
   }

   private class Column extends ArrayTable.ArrayMap {
      final int columnIndex;

      Column(int columnIndex) {
         super(ArrayTable.this.rowKeyToIndex, null);
         this.columnIndex = columnIndex;
      }

      String getKeyRole() {
         return "Row";
      }

      Object getValue(int index) {
         return ArrayTable.this.at(index, this.columnIndex);
      }

      Object setValue(int index, Object newValue) {
         return ArrayTable.this.set(index, this.columnIndex, newValue);
      }
   }

   private class ColumnMap extends ArrayTable.ArrayMap {
      private ColumnMap() {
         super(ArrayTable.this.columnKeyToIndex, null);
      }

      String getKeyRole() {
         return "Column";
      }

      Map getValue(int index) {
         return ArrayTable.this.new Column(index);
      }

      Map setValue(int index, Map newValue) {
         throw new UnsupportedOperationException();
      }

      public Map put(Object key, Map value) {
         throw new UnsupportedOperationException();
      }
   }

   private class Row extends ArrayTable.ArrayMap {
      final int rowIndex;

      Row(int rowIndex) {
         super(ArrayTable.this.columnKeyToIndex, null);
         this.rowIndex = rowIndex;
      }

      String getKeyRole() {
         return "Column";
      }

      Object getValue(int index) {
         return ArrayTable.this.at(this.rowIndex, index);
      }

      Object setValue(int index, Object newValue) {
         return ArrayTable.this.set(this.rowIndex, index, newValue);
      }
   }

   private class RowMap extends ArrayTable.ArrayMap {
      private RowMap() {
         super(ArrayTable.this.rowKeyToIndex, null);
      }

      String getKeyRole() {
         return "Row";
      }

      Map getValue(int index) {
         return ArrayTable.this.new Row(index);
      }

      Map setValue(int index, Map newValue) {
         throw new UnsupportedOperationException();
      }

      public Map put(Object key, Map value) {
         throw new UnsupportedOperationException();
      }
   }
}
