package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableAsList;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMapEntrySet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.RegularImmutableSortedSet;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class RegularImmutableSortedMap extends ImmutableSortedMap {
   private final transient RegularImmutableSortedSet keySet;
   private final transient ImmutableList valueList;

   RegularImmutableSortedMap(RegularImmutableSortedSet keySet, ImmutableList valueList) {
      this.keySet = keySet;
      this.valueList = valueList;
   }

   RegularImmutableSortedMap(RegularImmutableSortedSet keySet, ImmutableList valueList, ImmutableSortedMap descendingMap) {
      super(descendingMap);
      this.keySet = keySet;
      this.valueList = valueList;
   }

   ImmutableSet createEntrySet() {
      return new RegularImmutableSortedMap.EntrySet();
   }

   public ImmutableSortedSet keySet() {
      return this.keySet;
   }

   public ImmutableCollection values() {
      return this.valueList;
   }

   public Object get(@Nullable Object key) {
      int index = this.keySet.indexOf(key);
      return index == -1?null:this.valueList.get(index);
   }

   private ImmutableSortedMap getSubMap(int fromIndex, int toIndex) {
      return (ImmutableSortedMap)(fromIndex == 0 && toIndex == this.size()?this:(fromIndex == toIndex?emptyMap(this.comparator()):from(this.keySet.getSubSet(fromIndex, toIndex), this.valueList.subList(fromIndex, toIndex))));
   }

   public ImmutableSortedMap headMap(Object toKey, boolean inclusive) {
      return this.getSubMap(0, this.keySet.headIndex(Preconditions.checkNotNull(toKey), inclusive));
   }

   public ImmutableSortedMap tailMap(Object fromKey, boolean inclusive) {
      return this.getSubMap(this.keySet.tailIndex(Preconditions.checkNotNull(fromKey), inclusive), this.size());
   }

   ImmutableSortedMap createDescendingMap() {
      return new RegularImmutableSortedMap((RegularImmutableSortedSet)this.keySet.descendingSet(), this.valueList.reverse(), this);
   }

   private class EntrySet extends ImmutableMapEntrySet {
      private EntrySet() {
      }

      public UnmodifiableIterator iterator() {
         return this.asList().iterator();
      }

      ImmutableList createAsList() {
         return new ImmutableAsList() {
            private final ImmutableList keyList = RegularImmutableSortedMap.this.keySet().asList();

            public Entry get(int index) {
               return Maps.immutableEntry(this.keyList.get(index), RegularImmutableSortedMap.this.valueList.get(index));
            }

            ImmutableCollection delegateCollection() {
               return EntrySet.this;
            }
         };
      }

      ImmutableMap map() {
         return RegularImmutableSortedMap.this;
      }
   }
}
