package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ForwardingSortedMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.Map.Entry;

public abstract class ForwardingNavigableMap extends ForwardingSortedMap implements NavigableMap {
   protected abstract NavigableMap delegate();

   public Entry lowerEntry(Object key) {
      return this.delegate().lowerEntry(key);
   }

   protected Entry standardLowerEntry(Object key) {
      return this.headMap(key, false).lastEntry();
   }

   public Object lowerKey(Object key) {
      return this.delegate().lowerKey(key);
   }

   protected Object standardLowerKey(Object key) {
      return Maps.keyOrNull(this.lowerEntry(key));
   }

   public Entry floorEntry(Object key) {
      return this.delegate().floorEntry(key);
   }

   protected Entry standardFloorEntry(Object key) {
      return this.headMap(key, true).lastEntry();
   }

   public Object floorKey(Object key) {
      return this.delegate().floorKey(key);
   }

   protected Object standardFloorKey(Object key) {
      return Maps.keyOrNull(this.floorEntry(key));
   }

   public Entry ceilingEntry(Object key) {
      return this.delegate().ceilingEntry(key);
   }

   protected Entry standardCeilingEntry(Object key) {
      return this.tailMap(key, true).firstEntry();
   }

   public Object ceilingKey(Object key) {
      return this.delegate().ceilingKey(key);
   }

   protected Object standardCeilingKey(Object key) {
      return Maps.keyOrNull(this.ceilingEntry(key));
   }

   public Entry higherEntry(Object key) {
      return this.delegate().higherEntry(key);
   }

   protected Entry standardHigherEntry(Object key) {
      return this.tailMap(key, false).firstEntry();
   }

   public Object higherKey(Object key) {
      return this.delegate().higherKey(key);
   }

   protected Object standardHigherKey(Object key) {
      return Maps.keyOrNull(this.higherEntry(key));
   }

   public Entry firstEntry() {
      return this.delegate().firstEntry();
   }

   protected Entry standardFirstEntry() {
      return (Entry)Iterables.getFirst(this.entrySet(), (Object)null);
   }

   protected Object standardFirstKey() {
      Entry<K, V> entry = this.firstEntry();
      if(entry == null) {
         throw new NoSuchElementException();
      } else {
         return entry.getKey();
      }
   }

   public Entry lastEntry() {
      return this.delegate().lastEntry();
   }

   protected Entry standardLastEntry() {
      return (Entry)Iterables.getFirst(this.descendingMap().entrySet(), (Object)null);
   }

   protected Object standardLastKey() {
      Entry<K, V> entry = this.lastEntry();
      if(entry == null) {
         throw new NoSuchElementException();
      } else {
         return entry.getKey();
      }
   }

   public Entry pollFirstEntry() {
      return this.delegate().pollFirstEntry();
   }

   protected Entry standardPollFirstEntry() {
      return (Entry)Iterators.pollNext(this.entrySet().iterator());
   }

   public Entry pollLastEntry() {
      return this.delegate().pollLastEntry();
   }

   protected Entry standardPollLastEntry() {
      return (Entry)Iterators.pollNext(this.descendingMap().entrySet().iterator());
   }

   public NavigableMap descendingMap() {
      return this.delegate().descendingMap();
   }

   public NavigableSet navigableKeySet() {
      return this.delegate().navigableKeySet();
   }

   public NavigableSet descendingKeySet() {
      return this.delegate().descendingKeySet();
   }

   @Beta
   protected NavigableSet standardDescendingKeySet() {
      return this.descendingMap().navigableKeySet();
   }

   protected SortedMap standardSubMap(Object fromKey, Object toKey) {
      return this.subMap(fromKey, true, toKey, false);
   }

   public NavigableMap subMap(Object fromKey, boolean fromInclusive, Object toKey, boolean toInclusive) {
      return this.delegate().subMap(fromKey, fromInclusive, toKey, toInclusive);
   }

   public NavigableMap headMap(Object toKey, boolean inclusive) {
      return this.delegate().headMap(toKey, inclusive);
   }

   public NavigableMap tailMap(Object fromKey, boolean inclusive) {
      return this.delegate().tailMap(fromKey, inclusive);
   }

   protected SortedMap standardHeadMap(Object toKey) {
      return this.headMap(toKey, false);
   }

   protected SortedMap standardTailMap(Object fromKey) {
      return this.tailMap(fromKey, true);
   }

   @Beta
   protected class StandardDescendingMap extends Maps.DescendingMap {
      NavigableMap forward() {
         return ForwardingNavigableMap.this;
      }

      protected Iterator entryIterator() {
         return new Iterator() {
            private Entry toRemove = null;
            private Entry nextOrNull = StandardDescendingMap.this.forward().lastEntry();

            public boolean hasNext() {
               return this.nextOrNull != null;
            }

            public Entry next() {
               if(!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  Entry var1;
                  try {
                     var1 = this.nextOrNull;
                  } finally {
                     this.toRemove = this.nextOrNull;
                     this.nextOrNull = StandardDescendingMap.this.forward().lowerEntry(this.nextOrNull.getKey());
                  }

                  return var1;
               }
            }

            public void remove() {
               CollectPreconditions.checkRemove(this.toRemove != null);
               StandardDescendingMap.this.forward().remove(this.toRemove.getKey());
               this.toRemove = null;
            }
         };
      }
   }

   @Beta
   protected class StandardNavigableKeySet extends Maps.NavigableKeySet {
      public StandardNavigableKeySet() {
         super(ForwardingNavigableMap.this);
      }
   }
}
