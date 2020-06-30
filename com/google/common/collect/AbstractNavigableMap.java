package com.google.common.collect;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;
import javax.annotation.Nullable;

abstract class AbstractNavigableMap extends AbstractMap implements NavigableMap {
   @Nullable
   public abstract Object get(@Nullable Object var1);

   @Nullable
   public Entry firstEntry() {
      return (Entry)Iterators.getNext(this.entryIterator(), (Object)null);
   }

   @Nullable
   public Entry lastEntry() {
      return (Entry)Iterators.getNext(this.descendingEntryIterator(), (Object)null);
   }

   @Nullable
   public Entry pollFirstEntry() {
      return (Entry)Iterators.pollNext(this.entryIterator());
   }

   @Nullable
   public Entry pollLastEntry() {
      return (Entry)Iterators.pollNext(this.descendingEntryIterator());
   }

   public Object firstKey() {
      Entry<K, V> entry = this.firstEntry();
      if(entry == null) {
         throw new NoSuchElementException();
      } else {
         return entry.getKey();
      }
   }

   public Object lastKey() {
      Entry<K, V> entry = this.lastEntry();
      if(entry == null) {
         throw new NoSuchElementException();
      } else {
         return entry.getKey();
      }
   }

   @Nullable
   public Entry lowerEntry(Object key) {
      return this.headMap(key, false).lastEntry();
   }

   @Nullable
   public Entry floorEntry(Object key) {
      return this.headMap(key, true).lastEntry();
   }

   @Nullable
   public Entry ceilingEntry(Object key) {
      return this.tailMap(key, true).firstEntry();
   }

   @Nullable
   public Entry higherEntry(Object key) {
      return this.tailMap(key, false).firstEntry();
   }

   public Object lowerKey(Object key) {
      return Maps.keyOrNull(this.lowerEntry(key));
   }

   public Object floorKey(Object key) {
      return Maps.keyOrNull(this.floorEntry(key));
   }

   public Object ceilingKey(Object key) {
      return Maps.keyOrNull(this.ceilingEntry(key));
   }

   public Object higherKey(Object key) {
      return Maps.keyOrNull(this.higherEntry(key));
   }

   abstract Iterator entryIterator();

   abstract Iterator descendingEntryIterator();

   public SortedMap subMap(Object fromKey, Object toKey) {
      return this.subMap(fromKey, true, toKey, false);
   }

   public SortedMap headMap(Object toKey) {
      return this.headMap(toKey, false);
   }

   public SortedMap tailMap(Object fromKey) {
      return this.tailMap(fromKey, true);
   }

   public NavigableSet navigableKeySet() {
      return new Maps.NavigableKeySet(this);
   }

   public Set keySet() {
      return this.navigableKeySet();
   }

   public abstract int size();

   public Set entrySet() {
      return new Maps.EntrySet() {
         Map map() {
            return AbstractNavigableMap.this;
         }

         public Iterator iterator() {
            return AbstractNavigableMap.this.entryIterator();
         }
      };
   }

   public NavigableSet descendingKeySet() {
      return this.descendingMap().navigableKeySet();
   }

   public NavigableMap descendingMap() {
      return new AbstractNavigableMap.DescendingMap();
   }

   private final class DescendingMap extends Maps.DescendingMap {
      private DescendingMap() {
      }

      NavigableMap forward() {
         return AbstractNavigableMap.this;
      }

      Iterator entryIterator() {
         return AbstractNavigableMap.this.descendingEntryIterator();
      }
   }
}
