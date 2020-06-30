package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.EmptyImmutableSortedMap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMapFauxverideShim;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.RegularImmutableSortedMap;
import com.google.common.collect.RegularImmutableSortedSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableSortedMap extends ImmutableSortedMapFauxverideShim implements NavigableMap {
   private static final Comparator NATURAL_ORDER = Ordering.natural();
   private static final ImmutableSortedMap NATURAL_EMPTY_MAP = new EmptyImmutableSortedMap(NATURAL_ORDER);
   private transient ImmutableSortedMap descendingMap;
   private static final long serialVersionUID = 0L;

   static ImmutableSortedMap emptyMap(Comparator comparator) {
      return (ImmutableSortedMap)(Ordering.natural().equals(comparator)?of():new EmptyImmutableSortedMap(comparator));
   }

   static ImmutableSortedMap fromSortedEntries(Comparator comparator, int size, Entry[] entries) {
      if(size == 0) {
         return emptyMap(comparator);
      } else {
         ImmutableList.Builder<K> keyBuilder = ImmutableList.builder();
         ImmutableList.Builder<V> valueBuilder = ImmutableList.builder();

         for(int i = 0; i < size; ++i) {
            Entry<K, V> entry = entries[i];
            keyBuilder.add(entry.getKey());
            valueBuilder.add(entry.getValue());
         }

         return new RegularImmutableSortedMap(new RegularImmutableSortedSet(keyBuilder.build(), comparator), valueBuilder.build());
      }
   }

   static ImmutableSortedMap from(ImmutableSortedSet keySet, ImmutableList valueList) {
      return (ImmutableSortedMap)(keySet.isEmpty()?emptyMap(keySet.comparator()):new RegularImmutableSortedMap((RegularImmutableSortedSet)keySet, valueList));
   }

   public static ImmutableSortedMap of() {
      return NATURAL_EMPTY_MAP;
   }

   public static ImmutableSortedMap of(Comparable k1, Object v1) {
      return from(ImmutableSortedSet.of(k1), ImmutableList.of(v1));
   }

   public static ImmutableSortedMap of(Comparable k1, Object v1, Comparable k2, Object v2) {
      return fromEntries(Ordering.natural(), false, 2, new Entry[]{entryOf(k1, v1), entryOf(k2, v2)});
   }

   public static ImmutableSortedMap of(Comparable k1, Object v1, Comparable k2, Object v2, Comparable k3, Object v3) {
      return fromEntries(Ordering.natural(), false, 3, new Entry[]{entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3)});
   }

   public static ImmutableSortedMap of(Comparable k1, Object v1, Comparable k2, Object v2, Comparable k3, Object v3, Comparable k4, Object v4) {
      return fromEntries(Ordering.natural(), false, 4, new Entry[]{entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4)});
   }

   public static ImmutableSortedMap of(Comparable k1, Object v1, Comparable k2, Object v2, Comparable k3, Object v3, Comparable k4, Object v4, Comparable k5, Object v5) {
      return fromEntries(Ordering.natural(), false, 5, new Entry[]{entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4), entryOf(k5, v5)});
   }

   public static ImmutableSortedMap copyOf(Map map) {
      Ordering<K> naturalOrder = Ordering.natural();
      return copyOfInternal(map, naturalOrder);
   }

   public static ImmutableSortedMap copyOf(Map map, Comparator comparator) {
      return copyOfInternal(map, (Comparator)Preconditions.checkNotNull(comparator));
   }

   public static ImmutableSortedMap copyOfSorted(SortedMap map) {
      Comparator<? super K> comparator = map.comparator();
      if(comparator == null) {
         comparator = NATURAL_ORDER;
      }

      return copyOfInternal(map, comparator);
   }

   private static ImmutableSortedMap copyOfInternal(Map map, Comparator comparator) {
      boolean sameComparator = false;
      if(map instanceof SortedMap) {
         SortedMap<?, ?> sortedMap = (SortedMap)map;
         Comparator<?> comparator2 = sortedMap.comparator();
         sameComparator = comparator2 == null?comparator == NATURAL_ORDER:comparator.equals(comparator2);
      }

      if(sameComparator && map instanceof ImmutableSortedMap) {
         ImmutableSortedMap<K, V> kvMap = (ImmutableSortedMap)map;
         if(!kvMap.isPartialView()) {
            return kvMap;
         }
      }

      Entry<K, V>[] entries = (Entry[])map.entrySet().toArray(new Entry[0]);
      return fromEntries(comparator, sameComparator, entries.length, entries);
   }

   static ImmutableSortedMap fromEntries(Comparator comparator, boolean sameComparator, int size, Entry... entries) {
      for(int i = 0; i < size; ++i) {
         Entry<K, V> entry = entries[i];
         entries[i] = entryOf(entry.getKey(), entry.getValue());
      }

      if(!sameComparator) {
         sortEntries(comparator, size, entries);
         validateEntries(size, entries, comparator);
      }

      return fromSortedEntries(comparator, size, entries);
   }

   private static void sortEntries(Comparator comparator, int size, Entry[] entries) {
      Arrays.sort(entries, 0, size, Ordering.from(comparator).onKeys());
   }

   private static void validateEntries(int size, Entry[] entries, Comparator comparator) {
      for(int i = 1; i < size; ++i) {
         checkNoConflict(comparator.compare(entries[i - 1].getKey(), entries[i].getKey()) != 0, "key", entries[i - 1], entries[i]);
      }

   }

   public static ImmutableSortedMap.Builder naturalOrder() {
      return new ImmutableSortedMap.Builder(Ordering.natural());
   }

   public static ImmutableSortedMap.Builder orderedBy(Comparator comparator) {
      return new ImmutableSortedMap.Builder(comparator);
   }

   public static ImmutableSortedMap.Builder reverseOrder() {
      return new ImmutableSortedMap.Builder(Ordering.natural().reverse());
   }

   ImmutableSortedMap() {
   }

   ImmutableSortedMap(ImmutableSortedMap descendingMap) {
      this.descendingMap = descendingMap;
   }

   public int size() {
      return this.values().size();
   }

   public boolean containsValue(@Nullable Object value) {
      return this.values().contains(value);
   }

   boolean isPartialView() {
      return this.keySet().isPartialView() || this.values().isPartialView();
   }

   public ImmutableSet entrySet() {
      return super.entrySet();
   }

   public abstract ImmutableSortedSet keySet();

   public abstract ImmutableCollection values();

   public Comparator comparator() {
      return this.keySet().comparator();
   }

   public Object firstKey() {
      return this.keySet().first();
   }

   public Object lastKey() {
      return this.keySet().last();
   }

   public ImmutableSortedMap headMap(Object toKey) {
      return this.headMap(toKey, false);
   }

   public abstract ImmutableSortedMap headMap(Object var1, boolean var2);

   public ImmutableSortedMap subMap(Object fromKey, Object toKey) {
      return this.subMap(fromKey, true, toKey, false);
   }

   public ImmutableSortedMap subMap(Object fromKey, boolean fromInclusive, Object toKey, boolean toInclusive) {
      Preconditions.checkNotNull(fromKey);
      Preconditions.checkNotNull(toKey);
      Preconditions.checkArgument(this.comparator().compare(fromKey, toKey) <= 0, "expected fromKey <= toKey but %s > %s", new Object[]{fromKey, toKey});
      return this.headMap(toKey, toInclusive).tailMap(fromKey, fromInclusive);
   }

   public ImmutableSortedMap tailMap(Object fromKey) {
      return this.tailMap(fromKey, true);
   }

   public abstract ImmutableSortedMap tailMap(Object var1, boolean var2);

   public Entry lowerEntry(Object key) {
      return this.headMap(key, false).lastEntry();
   }

   public Object lowerKey(Object key) {
      return Maps.keyOrNull(this.lowerEntry(key));
   }

   public Entry floorEntry(Object key) {
      return this.headMap(key, true).lastEntry();
   }

   public Object floorKey(Object key) {
      return Maps.keyOrNull(this.floorEntry(key));
   }

   public Entry ceilingEntry(Object key) {
      return this.tailMap(key, true).firstEntry();
   }

   public Object ceilingKey(Object key) {
      return Maps.keyOrNull(this.ceilingEntry(key));
   }

   public Entry higherEntry(Object key) {
      return this.tailMap(key, false).firstEntry();
   }

   public Object higherKey(Object key) {
      return Maps.keyOrNull(this.higherEntry(key));
   }

   public Entry firstEntry() {
      return this.isEmpty()?null:(Entry)this.entrySet().asList().get(0);
   }

   public Entry lastEntry() {
      return this.isEmpty()?null:(Entry)this.entrySet().asList().get(this.size() - 1);
   }

   /** @deprecated */
   @Deprecated
   public final Entry pollFirstEntry() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final Entry pollLastEntry() {
      throw new UnsupportedOperationException();
   }

   public ImmutableSortedMap descendingMap() {
      ImmutableSortedMap<K, V> result = this.descendingMap;
      if(result == null) {
         result = this.descendingMap = this.createDescendingMap();
      }

      return result;
   }

   abstract ImmutableSortedMap createDescendingMap();

   public ImmutableSortedSet navigableKeySet() {
      return this.keySet();
   }

   public ImmutableSortedSet descendingKeySet() {
      return this.keySet().descendingSet();
   }

   Object writeReplace() {
      return new ImmutableSortedMap.SerializedForm(this);
   }

   public static class Builder extends ImmutableMap.Builder {
      private final Comparator comparator;

      public Builder(Comparator comparator) {
         this.comparator = (Comparator)Preconditions.checkNotNull(comparator);
      }

      public ImmutableSortedMap.Builder put(Object key, Object value) {
         super.put(key, value);
         return this;
      }

      public ImmutableSortedMap.Builder put(Entry entry) {
         super.put(entry);
         return this;
      }

      public ImmutableSortedMap.Builder putAll(Map map) {
         super.putAll(map);
         return this;
      }

      public ImmutableSortedMap build() {
         return ImmutableSortedMap.fromEntries(this.comparator, false, this.size, this.entries);
      }
   }

   private static class SerializedForm extends ImmutableMap.SerializedForm {
      private final Comparator comparator;
      private static final long serialVersionUID = 0L;

      SerializedForm(ImmutableSortedMap sortedMap) {
         super(sortedMap);
         this.comparator = sortedMap.comparator();
      }

      Object readResolve() {
         ImmutableSortedMap.Builder<Object, Object> builder = new ImmutableSortedMap.Builder(this.comparator);
         return this.createMap(builder);
      }
   }
}
