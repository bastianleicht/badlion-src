package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.StandardRowSortedTable;
import com.google.common.collect.StandardTable;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
@Beta
public class TreeBasedTable extends StandardRowSortedTable {
   private final Comparator columnComparator;
   private static final long serialVersionUID = 0L;

   public static TreeBasedTable create() {
      return new TreeBasedTable(Ordering.natural(), Ordering.natural());
   }

   public static TreeBasedTable create(Comparator rowComparator, Comparator columnComparator) {
      Preconditions.checkNotNull(rowComparator);
      Preconditions.checkNotNull(columnComparator);
      return new TreeBasedTable(rowComparator, columnComparator);
   }

   public static TreeBasedTable create(TreeBasedTable table) {
      TreeBasedTable<R, C, V> result = new TreeBasedTable(table.rowComparator(), table.columnComparator());
      result.putAll(table);
      return result;
   }

   TreeBasedTable(Comparator rowComparator, Comparator columnComparator) {
      super(new TreeMap(rowComparator), new TreeBasedTable.Factory(columnComparator));
      this.columnComparator = columnComparator;
   }

   public Comparator rowComparator() {
      return this.rowKeySet().comparator();
   }

   public Comparator columnComparator() {
      return this.columnComparator;
   }

   public SortedMap row(Object rowKey) {
      return new TreeBasedTable.TreeRow(rowKey);
   }

   public SortedSet rowKeySet() {
      return super.rowKeySet();
   }

   public SortedMap rowMap() {
      return super.rowMap();
   }

   Iterator createColumnKeyIterator() {
      final Comparator<? super C> comparator = this.columnComparator();
      final Iterator<C> merged = Iterators.mergeSorted(Iterables.transform(this.backingMap.values(), new Function() {
         public Iterator apply(Map input) {
            return input.keySet().iterator();
         }
      }), comparator);
      return new AbstractIterator() {
         Object lastValue;

         protected Object computeNext() {
            while(merged.hasNext()) {
               C next = merged.next();
               boolean duplicate = this.lastValue != null && comparator.compare(next, this.lastValue) == 0;
               if(!duplicate) {
                  this.lastValue = next;
                  return this.lastValue;
               }
            }

            this.lastValue = null;
            return this.endOfData();
         }
      };
   }

   private static class Factory implements Supplier, Serializable {
      final Comparator comparator;
      private static final long serialVersionUID = 0L;

      Factory(Comparator comparator) {
         this.comparator = comparator;
      }

      public TreeMap get() {
         return new TreeMap(this.comparator);
      }
   }

   private class TreeRow extends StandardTable.Row implements SortedMap {
      @Nullable
      final Object lowerBound;
      @Nullable
      final Object upperBound;
      transient SortedMap wholeRow;

      TreeRow(Object rowKey) {
         this(rowKey, (Object)null, (Object)null);
      }

      TreeRow(Object rowKey, @Nullable Object lowerBound, @Nullable Object upperBound) {
         super(rowKey);
         this.lowerBound = lowerBound;
         this.upperBound = upperBound;
         Preconditions.checkArgument(lowerBound == null || upperBound == null || this.compare(lowerBound, upperBound) <= 0);
      }

      public SortedSet keySet() {
         return new Maps.SortedKeySet(this);
      }

      public Comparator comparator() {
         return TreeBasedTable.this.columnComparator();
      }

      int compare(Object a, Object b) {
         Comparator<Object> cmp = this.comparator();
         return cmp.compare(a, b);
      }

      boolean rangeContains(@Nullable Object o) {
         return o != null && (this.lowerBound == null || this.compare(this.lowerBound, o) <= 0) && (this.upperBound == null || this.compare(this.upperBound, o) > 0);
      }

      public SortedMap subMap(Object fromKey, Object toKey) {
         Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(fromKey)) && this.rangeContains(Preconditions.checkNotNull(toKey)));
         return TreeBasedTable.this.new TreeRow(this.rowKey, fromKey, toKey);
      }

      public SortedMap headMap(Object toKey) {
         Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(toKey)));
         return TreeBasedTable.this.new TreeRow(this.rowKey, this.lowerBound, toKey);
      }

      public SortedMap tailMap(Object fromKey) {
         Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(fromKey)));
         return TreeBasedTable.this.new TreeRow(this.rowKey, fromKey, this.upperBound);
      }

      public Object firstKey() {
         SortedMap<C, V> backing = this.backingRowMap();
         if(backing == null) {
            throw new NoSuchElementException();
         } else {
            return this.backingRowMap().firstKey();
         }
      }

      public Object lastKey() {
         SortedMap<C, V> backing = this.backingRowMap();
         if(backing == null) {
            throw new NoSuchElementException();
         } else {
            return this.backingRowMap().lastKey();
         }
      }

      SortedMap wholeRow() {
         if(this.wholeRow == null || this.wholeRow.isEmpty() && TreeBasedTable.this.backingMap.containsKey(this.rowKey)) {
            this.wholeRow = (SortedMap)TreeBasedTable.this.backingMap.get(this.rowKey);
         }

         return this.wholeRow;
      }

      SortedMap backingRowMap() {
         return (SortedMap)super.backingRowMap();
      }

      SortedMap computeBackingRowMap() {
         SortedMap<C, V> map = this.wholeRow();
         if(map != null) {
            if(this.lowerBound != null) {
               map = map.tailMap(this.lowerBound);
            }

            if(this.upperBound != null) {
               map = map.headMap(this.upperBound);
            }

            return map;
         } else {
            return null;
         }
      }

      void maintainEmptyInvariant() {
         if(this.wholeRow() != null && this.wholeRow.isEmpty()) {
            TreeBasedTable.this.backingMap.remove(this.rowKey);
            this.wholeRow = null;
            this.backingRowMap = null;
         }

      }

      public boolean containsKey(Object key) {
         return this.rangeContains(key) && super.containsKey(key);
      }

      public Object put(Object key, Object value) {
         Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(key)));
         return super.put(key, value);
      }
   }
}
