package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Converter;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractMapEntry;
import com.google.common.collect.AbstractNavigableMap;
import com.google.common.collect.BiMap;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ForwardingMapEntry;
import com.google.common.collect.ForwardingNavigableSet;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ForwardingSortedMap;
import com.google.common.collect.ForwardingSortedSet;
import com.google.common.collect.ImmutableEntry;
import com.google.common.collect.ImmutableEnumMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Ordering;
import com.google.common.collect.Platform;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedMapDifference;
import com.google.common.collect.Synchronized;
import com.google.common.collect.TransformedIterator;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class Maps {
   static final Joiner.MapJoiner STANDARD_JOINER = Collections2.STANDARD_JOINER.withKeyValueSeparator("=");

   static Function keyFunction() {
      return Maps.EntryFunction.KEY;
   }

   static Function valueFunction() {
      return Maps.EntryFunction.VALUE;
   }

   static Iterator keyIterator(Iterator entryIterator) {
      return Iterators.transform(entryIterator, keyFunction());
   }

   static Iterator valueIterator(Iterator entryIterator) {
      return Iterators.transform(entryIterator, valueFunction());
   }

   static UnmodifiableIterator valueIterator(final UnmodifiableIterator entryIterator) {
      return new UnmodifiableIterator() {
         public boolean hasNext() {
            return entryIterator.hasNext();
         }

         public Object next() {
            return ((Entry)entryIterator.next()).getValue();
         }
      };
   }

   @GwtCompatible(
      serializable = true
   )
   @Beta
   public static ImmutableMap immutableEnumMap(Map map) {
      if(map instanceof ImmutableEnumMap) {
         ImmutableEnumMap<K, V> result = (ImmutableEnumMap)map;
         return result;
      } else if(map.isEmpty()) {
         return ImmutableMap.of();
      } else {
         for(Entry<K, ? extends V> entry : map.entrySet()) {
            Preconditions.checkNotNull(entry.getKey());
            Preconditions.checkNotNull(entry.getValue());
         }

         return ImmutableEnumMap.asImmutable(new EnumMap(map));
      }
   }

   public static HashMap newHashMap() {
      return new HashMap();
   }

   public static HashMap newHashMapWithExpectedSize(int expectedSize) {
      return new HashMap(capacity(expectedSize));
   }

   static int capacity(int expectedSize) {
      if(expectedSize < 3) {
         CollectPreconditions.checkNonnegative(expectedSize, "expectedSize");
         return expectedSize + 1;
      } else {
         return expectedSize < 1073741824?expectedSize + expectedSize / 3:Integer.MAX_VALUE;
      }
   }

   public static HashMap newHashMap(Map map) {
      return new HashMap(map);
   }

   public static LinkedHashMap newLinkedHashMap() {
      return new LinkedHashMap();
   }

   public static LinkedHashMap newLinkedHashMap(Map map) {
      return new LinkedHashMap(map);
   }

   public static ConcurrentMap newConcurrentMap() {
      return (new MapMaker()).makeMap();
   }

   public static TreeMap newTreeMap() {
      return new TreeMap();
   }

   public static TreeMap newTreeMap(SortedMap map) {
      return new TreeMap(map);
   }

   public static TreeMap newTreeMap(@Nullable Comparator comparator) {
      return new TreeMap(comparator);
   }

   public static EnumMap newEnumMap(Class type) {
      return new EnumMap((Class)Preconditions.checkNotNull(type));
   }

   public static EnumMap newEnumMap(Map map) {
      return new EnumMap(map);
   }

   public static IdentityHashMap newIdentityHashMap() {
      return new IdentityHashMap();
   }

   public static MapDifference difference(Map left, Map right) {
      if(left instanceof SortedMap) {
         SortedMap<K, ? extends V> sortedLeft = (SortedMap)left;
         SortedMapDifference<K, V> result = difference(sortedLeft, right);
         return result;
      } else {
         return difference(left, right, Equivalence.equals());
      }
   }

   @Beta
   public static MapDifference difference(Map left, Map right, Equivalence valueEquivalence) {
      Preconditions.checkNotNull(valueEquivalence);
      Map<K, V> onlyOnLeft = newHashMap();
      Map<K, V> onlyOnRight = new HashMap(right);
      Map<K, V> onBoth = newHashMap();
      Map<K, MapDifference.ValueDifference<V>> differences = newHashMap();
      doDifference(left, right, valueEquivalence, onlyOnLeft, onlyOnRight, onBoth, differences);
      return new Maps.MapDifferenceImpl(onlyOnLeft, onlyOnRight, onBoth, differences);
   }

   private static void doDifference(Map left, Map right, Equivalence valueEquivalence, Map onlyOnLeft, Map onlyOnRight, Map onBoth, Map differences) {
      for(Entry<? extends K, ? extends V> entry : left.entrySet()) {
         K leftKey = entry.getKey();
         V leftValue = entry.getValue();
         if(right.containsKey(leftKey)) {
            V rightValue = onlyOnRight.remove(leftKey);
            if(valueEquivalence.equivalent(leftValue, rightValue)) {
               onBoth.put(leftKey, leftValue);
            } else {
               differences.put(leftKey, Maps.ValueDifferenceImpl.create(leftValue, rightValue));
            }
         } else {
            onlyOnLeft.put(leftKey, leftValue);
         }
      }

   }

   private static Map unmodifiableMap(Map map) {
      return (Map)(map instanceof SortedMap?Collections.unmodifiableSortedMap((SortedMap)map):Collections.unmodifiableMap(map));
   }

   public static SortedMapDifference difference(SortedMap left, Map right) {
      Preconditions.checkNotNull(left);
      Preconditions.checkNotNull(right);
      Comparator<? super K> comparator = orNaturalOrder(left.comparator());
      SortedMap<K, V> onlyOnLeft = newTreeMap(comparator);
      SortedMap<K, V> onlyOnRight = newTreeMap(comparator);
      onlyOnRight.putAll(right);
      SortedMap<K, V> onBoth = newTreeMap(comparator);
      SortedMap<K, MapDifference.ValueDifference<V>> differences = newTreeMap(comparator);
      doDifference(left, right, Equivalence.equals(), onlyOnLeft, onlyOnRight, onBoth, differences);
      return new Maps.SortedMapDifferenceImpl(onlyOnLeft, onlyOnRight, onBoth, differences);
   }

   static Comparator orNaturalOrder(@Nullable Comparator comparator) {
      return (Comparator)(comparator != null?comparator:Ordering.natural());
   }

   @Beta
   public static Map asMap(Set set, Function function) {
      return (Map)(set instanceof SortedSet?asMap((SortedSet)set, function):new Maps.AsMapView(set, function));
   }

   @Beta
   public static SortedMap asMap(SortedSet set, Function function) {
      return Platform.mapsAsMapSortedSet(set, function);
   }

   static SortedMap asMapSortedIgnoreNavigable(SortedSet set, Function function) {
      return new Maps.SortedAsMapView(set, function);
   }

   @Beta
   @GwtIncompatible("NavigableMap")
   public static NavigableMap asMap(NavigableSet set, Function function) {
      return new Maps.NavigableAsMapView(set, function);
   }

   static Iterator asMapEntryIterator(Set set, final Function function) {
      return new TransformedIterator(set.iterator()) {
         Entry transform(Object key) {
            return Maps.immutableEntry(key, function.apply(key));
         }
      };
   }

   private static Set removeOnlySet(final Set set) {
      return new ForwardingSet() {
         protected Set delegate() {
            return set;
         }

         public boolean add(Object element) {
            throw new UnsupportedOperationException();
         }

         public boolean addAll(Collection es) {
            throw new UnsupportedOperationException();
         }
      };
   }

   private static SortedSet removeOnlySortedSet(final SortedSet set) {
      return new ForwardingSortedSet() {
         protected SortedSet delegate() {
            return set;
         }

         public boolean add(Object element) {
            throw new UnsupportedOperationException();
         }

         public boolean addAll(Collection es) {
            throw new UnsupportedOperationException();
         }

         public SortedSet headSet(Object toElement) {
            return Maps.removeOnlySortedSet(super.headSet(toElement));
         }

         public SortedSet subSet(Object fromElement, Object toElement) {
            return Maps.removeOnlySortedSet(super.subSet(fromElement, toElement));
         }

         public SortedSet tailSet(Object fromElement) {
            return Maps.removeOnlySortedSet(super.tailSet(fromElement));
         }
      };
   }

   @GwtIncompatible("NavigableSet")
   private static NavigableSet removeOnlyNavigableSet(final NavigableSet set) {
      return new ForwardingNavigableSet() {
         protected NavigableSet delegate() {
            return set;
         }

         public boolean add(Object element) {
            throw new UnsupportedOperationException();
         }

         public boolean addAll(Collection es) {
            throw new UnsupportedOperationException();
         }

         public SortedSet headSet(Object toElement) {
            return Maps.removeOnlySortedSet(super.headSet(toElement));
         }

         public SortedSet subSet(Object fromElement, Object toElement) {
            return Maps.removeOnlySortedSet(super.subSet(fromElement, toElement));
         }

         public SortedSet tailSet(Object fromElement) {
            return Maps.removeOnlySortedSet(super.tailSet(fromElement));
         }

         public NavigableSet headSet(Object toElement, boolean inclusive) {
            return Maps.removeOnlyNavigableSet(super.headSet(toElement, inclusive));
         }

         public NavigableSet tailSet(Object fromElement, boolean inclusive) {
            return Maps.removeOnlyNavigableSet(super.tailSet(fromElement, inclusive));
         }

         public NavigableSet subSet(Object fromElement, boolean fromInclusive, Object toElement, boolean toInclusive) {
            return Maps.removeOnlyNavigableSet(super.subSet(fromElement, fromInclusive, toElement, toInclusive));
         }

         public NavigableSet descendingSet() {
            return Maps.removeOnlyNavigableSet(super.descendingSet());
         }
      };
   }

   @Beta
   public static ImmutableMap toMap(Iterable keys, Function valueFunction) {
      return toMap(keys.iterator(), valueFunction);
   }

   @Beta
   public static ImmutableMap toMap(Iterator keys, Function valueFunction) {
      Preconditions.checkNotNull(valueFunction);
      Map<K, V> builder = newLinkedHashMap();

      while(keys.hasNext()) {
         K key = keys.next();
         builder.put(key, valueFunction.apply(key));
      }

      return ImmutableMap.copyOf(builder);
   }

   public static ImmutableMap uniqueIndex(Iterable values, Function keyFunction) {
      return uniqueIndex(values.iterator(), keyFunction);
   }

   public static ImmutableMap uniqueIndex(Iterator values, Function keyFunction) {
      Preconditions.checkNotNull(keyFunction);
      ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();

      while(values.hasNext()) {
         V value = values.next();
         builder.put(keyFunction.apply(value), value);
      }

      return builder.build();
   }

   @GwtIncompatible("java.util.Properties")
   public static ImmutableMap fromProperties(Properties properties) {
      ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
      Enumeration<?> e = properties.propertyNames();

      while(e.hasMoreElements()) {
         String key = (String)e.nextElement();
         builder.put(key, properties.getProperty(key));
      }

      return builder.build();
   }

   @GwtCompatible(
      serializable = true
   )
   public static Entry immutableEntry(@Nullable Object key, @Nullable Object value) {
      return new ImmutableEntry(key, value);
   }

   static Set unmodifiableEntrySet(Set entrySet) {
      return new Maps.UnmodifiableEntrySet(Collections.unmodifiableSet(entrySet));
   }

   static Entry unmodifiableEntry(final Entry entry) {
      Preconditions.checkNotNull(entry);
      return new AbstractMapEntry() {
         public Object getKey() {
            return entry.getKey();
         }

         public Object getValue() {
            return entry.getValue();
         }
      };
   }

   @Beta
   public static Converter asConverter(BiMap bimap) {
      return new Maps.BiMapConverter(bimap);
   }

   public static BiMap synchronizedBiMap(BiMap bimap) {
      return Synchronized.biMap(bimap, (Object)null);
   }

   public static BiMap unmodifiableBiMap(BiMap bimap) {
      return new Maps.UnmodifiableBiMap(bimap, (BiMap)null);
   }

   public static Map transformValues(Map fromMap, Function function) {
      return transformEntries(fromMap, asEntryTransformer(function));
   }

   public static SortedMap transformValues(SortedMap fromMap, Function function) {
      return transformEntries(fromMap, asEntryTransformer(function));
   }

   @GwtIncompatible("NavigableMap")
   public static NavigableMap transformValues(NavigableMap fromMap, Function function) {
      return transformEntries(fromMap, asEntryTransformer(function));
   }

   public static Map transformEntries(Map fromMap, Maps.EntryTransformer transformer) {
      return (Map)(fromMap instanceof SortedMap?transformEntries((SortedMap)fromMap, transformer):new Maps.TransformedEntriesMap(fromMap, transformer));
   }

   public static SortedMap transformEntries(SortedMap fromMap, Maps.EntryTransformer transformer) {
      return Platform.mapsTransformEntriesSortedMap(fromMap, transformer);
   }

   @GwtIncompatible("NavigableMap")
   public static NavigableMap transformEntries(NavigableMap fromMap, Maps.EntryTransformer transformer) {
      return new Maps.TransformedEntriesNavigableMap(fromMap, transformer);
   }

   static SortedMap transformEntriesIgnoreNavigable(SortedMap fromMap, Maps.EntryTransformer transformer) {
      return new Maps.TransformedEntriesSortedMap(fromMap, transformer);
   }

   static Maps.EntryTransformer asEntryTransformer(final Function function) {
      Preconditions.checkNotNull(function);
      return new Maps.EntryTransformer() {
         public Object transformEntry(Object key, Object value) {
            return function.apply(value);
         }
      };
   }

   static Function asValueToValueFunction(final Maps.EntryTransformer transformer, final Object key) {
      Preconditions.checkNotNull(transformer);
      return new Function() {
         public Object apply(@Nullable Object v1) {
            return transformer.transformEntry(key, v1);
         }
      };
   }

   static Function asEntryToValueFunction(final Maps.EntryTransformer transformer) {
      Preconditions.checkNotNull(transformer);
      return new Function() {
         public Object apply(Entry entry) {
            return transformer.transformEntry(entry.getKey(), entry.getValue());
         }
      };
   }

   static Entry transformEntry(final Maps.EntryTransformer transformer, final Entry entry) {
      Preconditions.checkNotNull(transformer);
      Preconditions.checkNotNull(entry);
      return new AbstractMapEntry() {
         public Object getKey() {
            return entry.getKey();
         }

         public Object getValue() {
            return transformer.transformEntry(entry.getKey(), entry.getValue());
         }
      };
   }

   static Function asEntryToEntryFunction(final Maps.EntryTransformer transformer) {
      Preconditions.checkNotNull(transformer);
      return new Function() {
         public Entry apply(Entry entry) {
            return Maps.transformEntry(transformer, entry);
         }
      };
   }

   static Predicate keyPredicateOnEntries(Predicate keyPredicate) {
      return Predicates.compose(keyPredicate, keyFunction());
   }

   static Predicate valuePredicateOnEntries(Predicate valuePredicate) {
      return Predicates.compose(valuePredicate, valueFunction());
   }

   public static Map filterKeys(Map unfiltered, Predicate keyPredicate) {
      if(unfiltered instanceof SortedMap) {
         return filterKeys((SortedMap)unfiltered, keyPredicate);
      } else if(unfiltered instanceof BiMap) {
         return filterKeys((BiMap)unfiltered, keyPredicate);
      } else {
         Preconditions.checkNotNull(keyPredicate);
         Predicate<Entry<K, ?>> entryPredicate = keyPredicateOnEntries(keyPredicate);
         return (Map)(unfiltered instanceof Maps.AbstractFilteredMap?filterFiltered((Maps.AbstractFilteredMap)unfiltered, entryPredicate):new Maps.FilteredKeyMap((Map)Preconditions.checkNotNull(unfiltered), keyPredicate, entryPredicate));
      }
   }

   public static SortedMap filterKeys(SortedMap unfiltered, Predicate keyPredicate) {
      return filterEntries(unfiltered, keyPredicateOnEntries(keyPredicate));
   }

   @GwtIncompatible("NavigableMap")
   public static NavigableMap filterKeys(NavigableMap unfiltered, Predicate keyPredicate) {
      return filterEntries(unfiltered, keyPredicateOnEntries(keyPredicate));
   }

   public static BiMap filterKeys(BiMap unfiltered, Predicate keyPredicate) {
      Preconditions.checkNotNull(keyPredicate);
      return filterEntries(unfiltered, keyPredicateOnEntries(keyPredicate));
   }

   public static Map filterValues(Map unfiltered, Predicate valuePredicate) {
      return (Map)(unfiltered instanceof SortedMap?filterValues((SortedMap)unfiltered, valuePredicate):(unfiltered instanceof BiMap?filterValues((BiMap)unfiltered, valuePredicate):filterEntries(unfiltered, valuePredicateOnEntries(valuePredicate))));
   }

   public static SortedMap filterValues(SortedMap unfiltered, Predicate valuePredicate) {
      return filterEntries(unfiltered, valuePredicateOnEntries(valuePredicate));
   }

   @GwtIncompatible("NavigableMap")
   public static NavigableMap filterValues(NavigableMap unfiltered, Predicate valuePredicate) {
      return filterEntries(unfiltered, valuePredicateOnEntries(valuePredicate));
   }

   public static BiMap filterValues(BiMap unfiltered, Predicate valuePredicate) {
      return filterEntries(unfiltered, valuePredicateOnEntries(valuePredicate));
   }

   public static Map filterEntries(Map unfiltered, Predicate entryPredicate) {
      if(unfiltered instanceof SortedMap) {
         return filterEntries((SortedMap)unfiltered, entryPredicate);
      } else if(unfiltered instanceof BiMap) {
         return filterEntries((BiMap)unfiltered, entryPredicate);
      } else {
         Preconditions.checkNotNull(entryPredicate);
         return (Map)(unfiltered instanceof Maps.AbstractFilteredMap?filterFiltered((Maps.AbstractFilteredMap)unfiltered, entryPredicate):new Maps.FilteredEntryMap((Map)Preconditions.checkNotNull(unfiltered), entryPredicate));
      }
   }

   public static SortedMap filterEntries(SortedMap unfiltered, Predicate entryPredicate) {
      return Platform.mapsFilterSortedMap(unfiltered, entryPredicate);
   }

   static SortedMap filterSortedIgnoreNavigable(SortedMap unfiltered, Predicate entryPredicate) {
      Preconditions.checkNotNull(entryPredicate);
      return (SortedMap)(unfiltered instanceof Maps.FilteredEntrySortedMap?filterFiltered((Maps.FilteredEntrySortedMap)unfiltered, entryPredicate):new Maps.FilteredEntrySortedMap((SortedMap)Preconditions.checkNotNull(unfiltered), entryPredicate));
   }

   @GwtIncompatible("NavigableMap")
   public static NavigableMap filterEntries(NavigableMap unfiltered, Predicate entryPredicate) {
      Preconditions.checkNotNull(entryPredicate);
      return (NavigableMap)(unfiltered instanceof Maps.FilteredEntryNavigableMap?filterFiltered((Maps.FilteredEntryNavigableMap)unfiltered, entryPredicate):new Maps.FilteredEntryNavigableMap((NavigableMap)Preconditions.checkNotNull(unfiltered), entryPredicate));
   }

   public static BiMap filterEntries(BiMap unfiltered, Predicate entryPredicate) {
      Preconditions.checkNotNull(unfiltered);
      Preconditions.checkNotNull(entryPredicate);
      return (BiMap)(unfiltered instanceof Maps.FilteredEntryBiMap?filterFiltered((Maps.FilteredEntryBiMap)unfiltered, entryPredicate):new Maps.FilteredEntryBiMap(unfiltered, entryPredicate));
   }

   private static Map filterFiltered(Maps.AbstractFilteredMap map, Predicate entryPredicate) {
      return new Maps.FilteredEntryMap(map.unfiltered, Predicates.and(map.predicate, entryPredicate));
   }

   private static SortedMap filterFiltered(Maps.FilteredEntrySortedMap map, Predicate entryPredicate) {
      Predicate<Entry<K, V>> predicate = Predicates.and(map.predicate, entryPredicate);
      return new Maps.FilteredEntrySortedMap(map.sortedMap(), predicate);
   }

   @GwtIncompatible("NavigableMap")
   private static NavigableMap filterFiltered(Maps.FilteredEntryNavigableMap map, Predicate entryPredicate) {
      Predicate<Entry<K, V>> predicate = Predicates.and(map.entryPredicate, entryPredicate);
      return new Maps.FilteredEntryNavigableMap(map.unfiltered, predicate);
   }

   private static BiMap filterFiltered(Maps.FilteredEntryBiMap map, Predicate entryPredicate) {
      Predicate<Entry<K, V>> predicate = Predicates.and(map.predicate, entryPredicate);
      return new Maps.FilteredEntryBiMap(map.unfiltered(), predicate);
   }

   @GwtIncompatible("NavigableMap")
   public static NavigableMap unmodifiableNavigableMap(NavigableMap map) {
      Preconditions.checkNotNull(map);
      return (NavigableMap)(map instanceof Maps.UnmodifiableNavigableMap?map:new Maps.UnmodifiableNavigableMap(map));
   }

   @Nullable
   private static Entry unmodifiableOrNull(@Nullable Entry entry) {
      return entry == null?null:unmodifiableEntry(entry);
   }

   @GwtIncompatible("NavigableMap")
   public static NavigableMap synchronizedNavigableMap(NavigableMap navigableMap) {
      return Synchronized.navigableMap(navigableMap);
   }

   static Object safeGet(Map map, @Nullable Object key) {
      Preconditions.checkNotNull(map);

      try {
         return map.get(key);
      } catch (ClassCastException var3) {
         return null;
      } catch (NullPointerException var4) {
         return null;
      }
   }

   static boolean safeContainsKey(Map map, Object key) {
      Preconditions.checkNotNull(map);

      try {
         return map.containsKey(key);
      } catch (ClassCastException var3) {
         return false;
      } catch (NullPointerException var4) {
         return false;
      }
   }

   static Object safeRemove(Map map, Object key) {
      Preconditions.checkNotNull(map);

      try {
         return map.remove(key);
      } catch (ClassCastException var3) {
         return null;
      } catch (NullPointerException var4) {
         return null;
      }
   }

   static boolean containsKeyImpl(Map map, @Nullable Object key) {
      return Iterators.contains(keyIterator(map.entrySet().iterator()), key);
   }

   static boolean containsValueImpl(Map map, @Nullable Object value) {
      return Iterators.contains(valueIterator(map.entrySet().iterator()), value);
   }

   static boolean containsEntryImpl(Collection c, Object o) {
      return !(o instanceof Entry)?false:c.contains(unmodifiableEntry((Entry)o));
   }

   static boolean removeEntryImpl(Collection c, Object o) {
      return !(o instanceof Entry)?false:c.remove(unmodifiableEntry((Entry)o));
   }

   static boolean equalsImpl(Map map, Object object) {
      if(map == object) {
         return true;
      } else if(object instanceof Map) {
         Map<?, ?> o = (Map)object;
         return map.entrySet().equals(o.entrySet());
      } else {
         return false;
      }
   }

   static String toStringImpl(Map map) {
      StringBuilder sb = Collections2.newStringBuilderForCollection(map.size()).append('{');
      STANDARD_JOINER.appendTo(sb, map);
      return sb.append('}').toString();
   }

   static void putAllImpl(Map self, Map map) {
      for(Entry<? extends K, ? extends V> entry : map.entrySet()) {
         self.put(entry.getKey(), entry.getValue());
      }

   }

   @Nullable
   static Object keyOrNull(@Nullable Entry entry) {
      return entry == null?null:entry.getKey();
   }

   @Nullable
   static Object valueOrNull(@Nullable Entry entry) {
      return entry == null?null:entry.getValue();
   }

   private abstract static class AbstractFilteredMap extends Maps.ImprovedAbstractMap {
      final Map unfiltered;
      final Predicate predicate;

      AbstractFilteredMap(Map unfiltered, Predicate predicate) {
         this.unfiltered = unfiltered;
         this.predicate = predicate;
      }

      boolean apply(@Nullable Object key, @Nullable Object value) {
         return this.predicate.apply(Maps.immutableEntry(key, value));
      }

      public Object put(Object key, Object value) {
         Preconditions.checkArgument(this.apply(key, value));
         return this.unfiltered.put(key, value);
      }

      public void putAll(Map map) {
         for(Entry<? extends K, ? extends V> entry : map.entrySet()) {
            Preconditions.checkArgument(this.apply(entry.getKey(), entry.getValue()));
         }

         this.unfiltered.putAll(map);
      }

      public boolean containsKey(Object key) {
         return this.unfiltered.containsKey(key) && this.apply(key, this.unfiltered.get(key));
      }

      public Object get(Object key) {
         V value = this.unfiltered.get(key);
         return value != null && this.apply(key, value)?value:null;
      }

      public boolean isEmpty() {
         return this.entrySet().isEmpty();
      }

      public Object remove(Object key) {
         return this.containsKey(key)?this.unfiltered.remove(key):null;
      }

      Collection createValues() {
         return new Maps.FilteredMapValues(this, this.unfiltered, this.predicate);
      }
   }

   private static class AsMapView extends Maps.ImprovedAbstractMap {
      private final Set set;
      final Function function;

      Set backingSet() {
         return this.set;
      }

      AsMapView(Set set, Function function) {
         this.set = (Set)Preconditions.checkNotNull(set);
         this.function = (Function)Preconditions.checkNotNull(function);
      }

      public Set createKeySet() {
         return Maps.removeOnlySet(this.backingSet());
      }

      Collection createValues() {
         return Collections2.transform(this.set, this.function);
      }

      public int size() {
         return this.backingSet().size();
      }

      public boolean containsKey(@Nullable Object key) {
         return this.backingSet().contains(key);
      }

      public Object get(@Nullable Object key) {
         return Collections2.safeContains(this.backingSet(), key)?this.function.apply(key):null;
      }

      public Object remove(@Nullable Object key) {
         return this.backingSet().remove(key)?this.function.apply(key):null;
      }

      public void clear() {
         this.backingSet().clear();
      }

      protected Set createEntrySet() {
         return new Maps.EntrySet() {
            Map map() {
               return AsMapView.this;
            }

            public Iterator iterator() {
               return Maps.asMapEntryIterator(AsMapView.this.backingSet(), AsMapView.this.function);
            }
         };
      }
   }

   private static final class BiMapConverter extends Converter implements Serializable {
      private final BiMap bimap;
      private static final long serialVersionUID = 0L;

      BiMapConverter(BiMap bimap) {
         this.bimap = (BiMap)Preconditions.checkNotNull(bimap);
      }

      protected Object doForward(Object a) {
         return convert(this.bimap, a);
      }

      protected Object doBackward(Object b) {
         return convert(this.bimap.inverse(), b);
      }

      private static Object convert(BiMap bimap, Object input) {
         Y output = bimap.get(input);
         Preconditions.checkArgument(output != null, "No non-null mapping present for input: %s", new Object[]{input});
         return output;
      }

      public boolean equals(@Nullable Object object) {
         if(object instanceof Maps.BiMapConverter) {
            Maps.BiMapConverter<?, ?> that = (Maps.BiMapConverter)object;
            return this.bimap.equals(that.bimap);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.bimap.hashCode();
      }

      public String toString() {
         return "Maps.asConverter(" + this.bimap + ")";
      }
   }

   @GwtIncompatible("NavigableMap")
   abstract static class DescendingMap extends ForwardingMap implements NavigableMap {
      private transient Comparator comparator;
      private transient Set entrySet;
      private transient NavigableSet navigableKeySet;

      abstract NavigableMap forward();

      protected final Map delegate() {
         return this.forward();
      }

      public Comparator comparator() {
         Comparator<? super K> result = this.comparator;
         if(result == null) {
            Comparator<? super K> forwardCmp = this.forward().comparator();
            if(forwardCmp == null) {
               forwardCmp = Ordering.natural();
            }

            result = this.comparator = reverse(forwardCmp);
         }

         return result;
      }

      private static Ordering reverse(Comparator forward) {
         return Ordering.from(forward).reverse();
      }

      public Object firstKey() {
         return this.forward().lastKey();
      }

      public Object lastKey() {
         return this.forward().firstKey();
      }

      public Entry lowerEntry(Object key) {
         return this.forward().higherEntry(key);
      }

      public Object lowerKey(Object key) {
         return this.forward().higherKey(key);
      }

      public Entry floorEntry(Object key) {
         return this.forward().ceilingEntry(key);
      }

      public Object floorKey(Object key) {
         return this.forward().ceilingKey(key);
      }

      public Entry ceilingEntry(Object key) {
         return this.forward().floorEntry(key);
      }

      public Object ceilingKey(Object key) {
         return this.forward().floorKey(key);
      }

      public Entry higherEntry(Object key) {
         return this.forward().lowerEntry(key);
      }

      public Object higherKey(Object key) {
         return this.forward().lowerKey(key);
      }

      public Entry firstEntry() {
         return this.forward().lastEntry();
      }

      public Entry lastEntry() {
         return this.forward().firstEntry();
      }

      public Entry pollFirstEntry() {
         return this.forward().pollLastEntry();
      }

      public Entry pollLastEntry() {
         return this.forward().pollFirstEntry();
      }

      public NavigableMap descendingMap() {
         return this.forward();
      }

      public Set entrySet() {
         Set<Entry<K, V>> result = this.entrySet;
         return result == null?(this.entrySet = this.createEntrySet()):result;
      }

      abstract Iterator entryIterator();

      Set createEntrySet() {
         return new Maps.EntrySet() {
            Map map() {
               return DescendingMap.this;
            }

            public Iterator iterator() {
               return DescendingMap.this.entryIterator();
            }
         };
      }

      public Set keySet() {
         return this.navigableKeySet();
      }

      public NavigableSet navigableKeySet() {
         NavigableSet<K> result = this.navigableKeySet;
         return result == null?(this.navigableKeySet = new Maps.NavigableKeySet(this)):result;
      }

      public NavigableSet descendingKeySet() {
         return this.forward().navigableKeySet();
      }

      public NavigableMap subMap(Object fromKey, boolean fromInclusive, Object toKey, boolean toInclusive) {
         return this.forward().subMap(toKey, toInclusive, fromKey, fromInclusive).descendingMap();
      }

      public NavigableMap headMap(Object toKey, boolean inclusive) {
         return this.forward().tailMap(toKey, inclusive).descendingMap();
      }

      public NavigableMap tailMap(Object fromKey, boolean inclusive) {
         return this.forward().headMap(fromKey, inclusive).descendingMap();
      }

      public SortedMap subMap(Object fromKey, Object toKey) {
         return this.subMap(fromKey, true, toKey, false);
      }

      public SortedMap headMap(Object toKey) {
         return this.headMap(toKey, false);
      }

      public SortedMap tailMap(Object fromKey) {
         return this.tailMap(fromKey, true);
      }

      public Collection values() {
         return new Maps.Values(this);
      }

      public String toString() {
         return this.standardToString();
      }
   }

   private static enum EntryFunction implements Function {
      KEY {
         @Nullable
         public Object apply(Entry entry) {
            return entry.getKey();
         }
      },
      VALUE {
         @Nullable
         public Object apply(Entry entry) {
            return entry.getValue();
         }
      };

      private EntryFunction() {
      }
   }

   abstract static class EntrySet extends Sets.ImprovedAbstractSet {
      abstract Map map();

      public int size() {
         return this.map().size();
      }

      public void clear() {
         this.map().clear();
      }

      public boolean contains(Object o) {
         if(!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> entry = (Entry)o;
            Object key = entry.getKey();
            V value = Maps.safeGet(this.map(), key);
            return Objects.equal(value, entry.getValue()) && (value != null || this.map().containsKey(key));
         }
      }

      public boolean isEmpty() {
         return this.map().isEmpty();
      }

      public boolean remove(Object o) {
         if(this.contains(o)) {
            Entry<?, ?> entry = (Entry)o;
            return this.map().keySet().remove(entry.getKey());
         } else {
            return false;
         }
      }

      public boolean removeAll(Collection c) {
         try {
            return super.removeAll((Collection)Preconditions.checkNotNull(c));
         } catch (UnsupportedOperationException var3) {
            return Sets.removeAllImpl(this, (Iterator)c.iterator());
         }
      }

      public boolean retainAll(Collection c) {
         try {
            return super.retainAll((Collection)Preconditions.checkNotNull(c));
         } catch (UnsupportedOperationException var7) {
            Set<Object> keys = Sets.newHashSetWithExpectedSize(c.size());

            for(Object o : c) {
               if(this.contains(o)) {
                  Entry<?, ?> entry = (Entry)o;
                  keys.add(entry.getKey());
               }
            }

            return this.map().keySet().retainAll(keys);
         }
      }
   }

   public interface EntryTransformer {
      Object transformEntry(@Nullable Object var1, @Nullable Object var2);
   }

   static final class FilteredEntryBiMap extends Maps.FilteredEntryMap implements BiMap {
      private final BiMap inverse;

      private static Predicate inversePredicate(final Predicate forwardPredicate) {
         return new Predicate() {
            public boolean apply(Entry input) {
               return forwardPredicate.apply(Maps.immutableEntry(input.getValue(), input.getKey()));
            }
         };
      }

      FilteredEntryBiMap(BiMap delegate, Predicate predicate) {
         super(delegate, predicate);
         this.inverse = new Maps.FilteredEntryBiMap(delegate.inverse(), inversePredicate(predicate), this);
      }

      private FilteredEntryBiMap(BiMap delegate, Predicate predicate, BiMap inverse) {
         super(delegate, predicate);
         this.inverse = inverse;
      }

      BiMap unfiltered() {
         return (BiMap)this.unfiltered;
      }

      public Object forcePut(@Nullable Object key, @Nullable Object value) {
         Preconditions.checkArgument(this.apply(key, value));
         return this.unfiltered().forcePut(key, value);
      }

      public BiMap inverse() {
         return this.inverse;
      }

      public Set values() {
         return this.inverse.keySet();
      }
   }

   static class FilteredEntryMap extends Maps.AbstractFilteredMap {
      final Set filteredEntrySet;

      FilteredEntryMap(Map unfiltered, Predicate entryPredicate) {
         super(unfiltered, entryPredicate);
         this.filteredEntrySet = Sets.filter(unfiltered.entrySet(), this.predicate);
      }

      protected Set createEntrySet() {
         return new Maps.FilteredEntryMap.EntrySet();
      }

      Set createKeySet() {
         return new Maps.FilteredEntryMap.KeySet();
      }

      private class EntrySet extends ForwardingSet {
         private EntrySet() {
         }

         protected Set delegate() {
            return FilteredEntryMap.this.filteredEntrySet;
         }

         public Iterator iterator() {
            return new TransformedIterator(FilteredEntryMap.this.filteredEntrySet.iterator()) {
               Entry transform(final Entry entry) {
                  return new ForwardingMapEntry() {
                     protected Entry delegate() {
                        return entry;
                     }

                     public Object setValue(Object newValue) {
                        Preconditions.checkArgument(FilteredEntryMap.this.apply(this.getKey(), newValue));
                        return super.setValue(newValue);
                     }
                  };
               }
            };
         }
      }

      class KeySet extends Maps.KeySet {
         KeySet() {
            super(FilteredEntryMap.this);
         }

         public boolean remove(Object o) {
            if(FilteredEntryMap.this.containsKey(o)) {
               FilteredEntryMap.this.unfiltered.remove(o);
               return true;
            } else {
               return false;
            }
         }

         private boolean removeIf(Predicate keyPredicate) {
            return Iterables.removeIf(FilteredEntryMap.this.unfiltered.entrySet(), Predicates.and(FilteredEntryMap.this.predicate, Maps.keyPredicateOnEntries(keyPredicate)));
         }

         public boolean removeAll(Collection c) {
            return this.removeIf(Predicates.in(c));
         }

         public boolean retainAll(Collection c) {
            return this.removeIf(Predicates.not(Predicates.in(c)));
         }

         public Object[] toArray() {
            return Lists.newArrayList(this.iterator()).toArray();
         }

         public Object[] toArray(Object[] array) {
            return Lists.newArrayList(this.iterator()).toArray(array);
         }
      }
   }

   @GwtIncompatible("NavigableMap")
   private static class FilteredEntryNavigableMap extends AbstractNavigableMap {
      private final NavigableMap unfiltered;
      private final Predicate entryPredicate;
      private final Map filteredDelegate;

      FilteredEntryNavigableMap(NavigableMap unfiltered, Predicate entryPredicate) {
         this.unfiltered = (NavigableMap)Preconditions.checkNotNull(unfiltered);
         this.entryPredicate = entryPredicate;
         this.filteredDelegate = new Maps.FilteredEntryMap(unfiltered, entryPredicate);
      }

      public Comparator comparator() {
         return this.unfiltered.comparator();
      }

      public NavigableSet navigableKeySet() {
         return new Maps.NavigableKeySet(this) {
            public boolean removeAll(Collection c) {
               return Iterators.removeIf(FilteredEntryNavigableMap.this.unfiltered.entrySet().iterator(), Predicates.and(FilteredEntryNavigableMap.this.entryPredicate, Maps.keyPredicateOnEntries(Predicates.in(c))));
            }

            public boolean retainAll(Collection c) {
               return Iterators.removeIf(FilteredEntryNavigableMap.this.unfiltered.entrySet().iterator(), Predicates.and(FilteredEntryNavigableMap.this.entryPredicate, Maps.keyPredicateOnEntries(Predicates.not(Predicates.in(c)))));
            }
         };
      }

      public Collection values() {
         return new Maps.FilteredMapValues(this, this.unfiltered, this.entryPredicate);
      }

      Iterator entryIterator() {
         return Iterators.filter(this.unfiltered.entrySet().iterator(), this.entryPredicate);
      }

      Iterator descendingEntryIterator() {
         return Iterators.filter(this.unfiltered.descendingMap().entrySet().iterator(), this.entryPredicate);
      }

      public int size() {
         return this.filteredDelegate.size();
      }

      @Nullable
      public Object get(@Nullable Object key) {
         return this.filteredDelegate.get(key);
      }

      public boolean containsKey(@Nullable Object key) {
         return this.filteredDelegate.containsKey(key);
      }

      public Object put(Object key, Object value) {
         return this.filteredDelegate.put(key, value);
      }

      public Object remove(@Nullable Object key) {
         return this.filteredDelegate.remove(key);
      }

      public void putAll(Map m) {
         this.filteredDelegate.putAll(m);
      }

      public void clear() {
         this.filteredDelegate.clear();
      }

      public Set entrySet() {
         return this.filteredDelegate.entrySet();
      }

      public Entry pollFirstEntry() {
         return (Entry)Iterables.removeFirstMatching(this.unfiltered.entrySet(), this.entryPredicate);
      }

      public Entry pollLastEntry() {
         return (Entry)Iterables.removeFirstMatching(this.unfiltered.descendingMap().entrySet(), this.entryPredicate);
      }

      public NavigableMap descendingMap() {
         return Maps.filterEntries(this.unfiltered.descendingMap(), this.entryPredicate);
      }

      public NavigableMap subMap(Object fromKey, boolean fromInclusive, Object toKey, boolean toInclusive) {
         return Maps.filterEntries(this.unfiltered.subMap(fromKey, fromInclusive, toKey, toInclusive), this.entryPredicate);
      }

      public NavigableMap headMap(Object toKey, boolean inclusive) {
         return Maps.filterEntries(this.unfiltered.headMap(toKey, inclusive), this.entryPredicate);
      }

      public NavigableMap tailMap(Object fromKey, boolean inclusive) {
         return Maps.filterEntries(this.unfiltered.tailMap(fromKey, inclusive), this.entryPredicate);
      }
   }

   private static class FilteredEntrySortedMap extends Maps.FilteredEntryMap implements SortedMap {
      FilteredEntrySortedMap(SortedMap unfiltered, Predicate entryPredicate) {
         super(unfiltered, entryPredicate);
      }

      SortedMap sortedMap() {
         return (SortedMap)this.unfiltered;
      }

      public SortedSet keySet() {
         return (SortedSet)super.keySet();
      }

      SortedSet createKeySet() {
         return new Maps.FilteredEntrySortedMap.SortedKeySet();
      }

      public Comparator comparator() {
         return this.sortedMap().comparator();
      }

      public Object firstKey() {
         return this.keySet().iterator().next();
      }

      public Object lastKey() {
         SortedMap<K, V> headMap = this.sortedMap();

         while(true) {
            K key = headMap.lastKey();
            if(this.apply(key, this.unfiltered.get(key))) {
               return key;
            }

            headMap = this.sortedMap().headMap(key);
         }
      }

      public SortedMap headMap(Object toKey) {
         return new Maps.FilteredEntrySortedMap(this.sortedMap().headMap(toKey), this.predicate);
      }

      public SortedMap subMap(Object fromKey, Object toKey) {
         return new Maps.FilteredEntrySortedMap(this.sortedMap().subMap(fromKey, toKey), this.predicate);
      }

      public SortedMap tailMap(Object fromKey) {
         return new Maps.FilteredEntrySortedMap(this.sortedMap().tailMap(fromKey), this.predicate);
      }

      class SortedKeySet extends Maps.FilteredEntryMap.KeySet implements SortedSet {
         SortedKeySet() {
            super();
         }

         public Comparator comparator() {
            return FilteredEntrySortedMap.this.sortedMap().comparator();
         }

         public SortedSet subSet(Object fromElement, Object toElement) {
            return (SortedSet)FilteredEntrySortedMap.this.subMap(fromElement, toElement).keySet();
         }

         public SortedSet headSet(Object toElement) {
            return (SortedSet)FilteredEntrySortedMap.this.headMap(toElement).keySet();
         }

         public SortedSet tailSet(Object fromElement) {
            return (SortedSet)FilteredEntrySortedMap.this.tailMap(fromElement).keySet();
         }

         public Object first() {
            return FilteredEntrySortedMap.this.firstKey();
         }

         public Object last() {
            return FilteredEntrySortedMap.this.lastKey();
         }
      }
   }

   private static class FilteredKeyMap extends Maps.AbstractFilteredMap {
      Predicate keyPredicate;

      FilteredKeyMap(Map unfiltered, Predicate keyPredicate, Predicate entryPredicate) {
         super(unfiltered, entryPredicate);
         this.keyPredicate = keyPredicate;
      }

      protected Set createEntrySet() {
         return Sets.filter(this.unfiltered.entrySet(), this.predicate);
      }

      Set createKeySet() {
         return Sets.filter(this.unfiltered.keySet(), this.keyPredicate);
      }

      public boolean containsKey(Object key) {
         return this.unfiltered.containsKey(key) && this.keyPredicate.apply(key);
      }
   }

   private static final class FilteredMapValues extends Maps.Values {
      Map unfiltered;
      Predicate predicate;

      FilteredMapValues(Map filteredMap, Map unfiltered, Predicate predicate) {
         super(filteredMap);
         this.unfiltered = unfiltered;
         this.predicate = predicate;
      }

      public boolean remove(Object o) {
         return Iterables.removeFirstMatching(this.unfiltered.entrySet(), Predicates.and(this.predicate, Maps.valuePredicateOnEntries(Predicates.equalTo(o)))) != null;
      }

      private boolean removeIf(Predicate valuePredicate) {
         return Iterables.removeIf(this.unfiltered.entrySet(), Predicates.and(this.predicate, Maps.valuePredicateOnEntries(valuePredicate)));
      }

      public boolean removeAll(Collection collection) {
         return this.removeIf(Predicates.in(collection));
      }

      public boolean retainAll(Collection collection) {
         return this.removeIf(Predicates.not(Predicates.in(collection)));
      }

      public Object[] toArray() {
         return Lists.newArrayList(this.iterator()).toArray();
      }

      public Object[] toArray(Object[] array) {
         return Lists.newArrayList(this.iterator()).toArray(array);
      }
   }

   @GwtCompatible
   abstract static class ImprovedAbstractMap extends AbstractMap {
      private transient Set entrySet;
      private transient Set keySet;
      private transient Collection values;

      abstract Set createEntrySet();

      public Set entrySet() {
         Set<Entry<K, V>> result = this.entrySet;
         return result == null?(this.entrySet = this.createEntrySet()):result;
      }

      public Set keySet() {
         Set<K> result = this.keySet;
         return result == null?(this.keySet = this.createKeySet()):result;
      }

      Set createKeySet() {
         return new Maps.KeySet(this);
      }

      public Collection values() {
         Collection<V> result = this.values;
         return result == null?(this.values = this.createValues()):result;
      }

      Collection createValues() {
         return new Maps.Values(this);
      }
   }

   static class KeySet extends Sets.ImprovedAbstractSet {
      final Map map;

      KeySet(Map map) {
         this.map = (Map)Preconditions.checkNotNull(map);
      }

      Map map() {
         return this.map;
      }

      public Iterator iterator() {
         return Maps.keyIterator(this.map().entrySet().iterator());
      }

      public int size() {
         return this.map().size();
      }

      public boolean isEmpty() {
         return this.map().isEmpty();
      }

      public boolean contains(Object o) {
         return this.map().containsKey(o);
      }

      public boolean remove(Object o) {
         if(this.contains(o)) {
            this.map().remove(o);
            return true;
         } else {
            return false;
         }
      }

      public void clear() {
         this.map().clear();
      }
   }

   static class MapDifferenceImpl implements MapDifference {
      final Map onlyOnLeft;
      final Map onlyOnRight;
      final Map onBoth;
      final Map differences;

      MapDifferenceImpl(Map onlyOnLeft, Map onlyOnRight, Map onBoth, Map differences) {
         this.onlyOnLeft = Maps.unmodifiableMap(onlyOnLeft);
         this.onlyOnRight = Maps.unmodifiableMap(onlyOnRight);
         this.onBoth = Maps.unmodifiableMap(onBoth);
         this.differences = Maps.unmodifiableMap(differences);
      }

      public boolean areEqual() {
         return this.onlyOnLeft.isEmpty() && this.onlyOnRight.isEmpty() && this.differences.isEmpty();
      }

      public Map entriesOnlyOnLeft() {
         return this.onlyOnLeft;
      }

      public Map entriesOnlyOnRight() {
         return this.onlyOnRight;
      }

      public Map entriesInCommon() {
         return this.onBoth;
      }

      public Map entriesDiffering() {
         return this.differences;
      }

      public boolean equals(Object object) {
         if(object == this) {
            return true;
         } else if(!(object instanceof MapDifference)) {
            return false;
         } else {
            MapDifference<?, ?> other = (MapDifference)object;
            return this.entriesOnlyOnLeft().equals(other.entriesOnlyOnLeft()) && this.entriesOnlyOnRight().equals(other.entriesOnlyOnRight()) && this.entriesInCommon().equals(other.entriesInCommon()) && this.entriesDiffering().equals(other.entriesDiffering());
         }
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.entriesOnlyOnLeft(), this.entriesOnlyOnRight(), this.entriesInCommon(), this.entriesDiffering()});
      }

      public String toString() {
         if(this.areEqual()) {
            return "equal";
         } else {
            StringBuilder result = new StringBuilder("not equal");
            if(!this.onlyOnLeft.isEmpty()) {
               result.append(": only on left=").append(this.onlyOnLeft);
            }

            if(!this.onlyOnRight.isEmpty()) {
               result.append(": only on right=").append(this.onlyOnRight);
            }

            if(!this.differences.isEmpty()) {
               result.append(": value differences=").append(this.differences);
            }

            return result.toString();
         }
      }
   }

   @GwtIncompatible("NavigableMap")
   private static final class NavigableAsMapView extends AbstractNavigableMap {
      private final NavigableSet set;
      private final Function function;

      NavigableAsMapView(NavigableSet ks, Function vFunction) {
         this.set = (NavigableSet)Preconditions.checkNotNull(ks);
         this.function = (Function)Preconditions.checkNotNull(vFunction);
      }

      public NavigableMap subMap(Object fromKey, boolean fromInclusive, Object toKey, boolean toInclusive) {
         return Maps.asMap(this.set.subSet(fromKey, fromInclusive, toKey, toInclusive), this.function);
      }

      public NavigableMap headMap(Object toKey, boolean inclusive) {
         return Maps.asMap(this.set.headSet(toKey, inclusive), this.function);
      }

      public NavigableMap tailMap(Object fromKey, boolean inclusive) {
         return Maps.asMap(this.set.tailSet(fromKey, inclusive), this.function);
      }

      public Comparator comparator() {
         return this.set.comparator();
      }

      @Nullable
      public Object get(@Nullable Object key) {
         return Collections2.safeContains(this.set, key)?this.function.apply(key):null;
      }

      public void clear() {
         this.set.clear();
      }

      Iterator entryIterator() {
         return Maps.asMapEntryIterator(this.set, this.function);
      }

      Iterator descendingEntryIterator() {
         return this.descendingMap().entrySet().iterator();
      }

      public NavigableSet navigableKeySet() {
         return Maps.removeOnlyNavigableSet(this.set);
      }

      public int size() {
         return this.set.size();
      }

      public NavigableMap descendingMap() {
         return Maps.asMap(this.set.descendingSet(), this.function);
      }
   }

   @GwtIncompatible("NavigableMap")
   static class NavigableKeySet extends Maps.SortedKeySet implements NavigableSet {
      NavigableKeySet(NavigableMap map) {
         super(map);
      }

      NavigableMap map() {
         return (NavigableMap)this.map;
      }

      public Object lower(Object e) {
         return this.map().lowerKey(e);
      }

      public Object floor(Object e) {
         return this.map().floorKey(e);
      }

      public Object ceiling(Object e) {
         return this.map().ceilingKey(e);
      }

      public Object higher(Object e) {
         return this.map().higherKey(e);
      }

      public Object pollFirst() {
         return Maps.keyOrNull(this.map().pollFirstEntry());
      }

      public Object pollLast() {
         return Maps.keyOrNull(this.map().pollLastEntry());
      }

      public NavigableSet descendingSet() {
         return this.map().descendingKeySet();
      }

      public Iterator descendingIterator() {
         return this.descendingSet().iterator();
      }

      public NavigableSet subSet(Object fromElement, boolean fromInclusive, Object toElement, boolean toInclusive) {
         return this.map().subMap(fromElement, fromInclusive, toElement, toInclusive).navigableKeySet();
      }

      public NavigableSet headSet(Object toElement, boolean inclusive) {
         return this.map().headMap(toElement, inclusive).navigableKeySet();
      }

      public NavigableSet tailSet(Object fromElement, boolean inclusive) {
         return this.map().tailMap(fromElement, inclusive).navigableKeySet();
      }

      public SortedSet subSet(Object fromElement, Object toElement) {
         return this.subSet(fromElement, true, toElement, false);
      }

      public SortedSet headSet(Object toElement) {
         return this.headSet(toElement, false);
      }

      public SortedSet tailSet(Object fromElement) {
         return this.tailSet(fromElement, true);
      }
   }

   private static class SortedAsMapView extends Maps.AsMapView implements SortedMap {
      SortedAsMapView(SortedSet set, Function function) {
         super(set, function);
      }

      SortedSet backingSet() {
         return (SortedSet)super.backingSet();
      }

      public Comparator comparator() {
         return this.backingSet().comparator();
      }

      public Set keySet() {
         return Maps.removeOnlySortedSet(this.backingSet());
      }

      public SortedMap subMap(Object fromKey, Object toKey) {
         return Platform.mapsAsMapSortedSet(this.backingSet().subSet(fromKey, toKey), this.function);
      }

      public SortedMap headMap(Object toKey) {
         return Platform.mapsAsMapSortedSet(this.backingSet().headSet(toKey), this.function);
      }

      public SortedMap tailMap(Object fromKey) {
         return Platform.mapsAsMapSortedSet(this.backingSet().tailSet(fromKey), this.function);
      }

      public Object firstKey() {
         return this.backingSet().first();
      }

      public Object lastKey() {
         return this.backingSet().last();
      }
   }

   static class SortedKeySet extends Maps.KeySet implements SortedSet {
      SortedKeySet(SortedMap map) {
         super(map);
      }

      SortedMap map() {
         return (SortedMap)super.map();
      }

      public Comparator comparator() {
         return this.map().comparator();
      }

      public SortedSet subSet(Object fromElement, Object toElement) {
         return new Maps.SortedKeySet(this.map().subMap(fromElement, toElement));
      }

      public SortedSet headSet(Object toElement) {
         return new Maps.SortedKeySet(this.map().headMap(toElement));
      }

      public SortedSet tailSet(Object fromElement) {
         return new Maps.SortedKeySet(this.map().tailMap(fromElement));
      }

      public Object first() {
         return this.map().firstKey();
      }

      public Object last() {
         return this.map().lastKey();
      }
   }

   static class SortedMapDifferenceImpl extends Maps.MapDifferenceImpl implements SortedMapDifference {
      SortedMapDifferenceImpl(SortedMap onlyOnLeft, SortedMap onlyOnRight, SortedMap onBoth, SortedMap differences) {
         super(onlyOnLeft, onlyOnRight, onBoth, differences);
      }

      public SortedMap entriesDiffering() {
         return (SortedMap)super.entriesDiffering();
      }

      public SortedMap entriesInCommon() {
         return (SortedMap)super.entriesInCommon();
      }

      public SortedMap entriesOnlyOnLeft() {
         return (SortedMap)super.entriesOnlyOnLeft();
      }

      public SortedMap entriesOnlyOnRight() {
         return (SortedMap)super.entriesOnlyOnRight();
      }
   }

   static class TransformedEntriesMap extends Maps.ImprovedAbstractMap {
      final Map fromMap;
      final Maps.EntryTransformer transformer;

      TransformedEntriesMap(Map fromMap, Maps.EntryTransformer transformer) {
         this.fromMap = (Map)Preconditions.checkNotNull(fromMap);
         this.transformer = (Maps.EntryTransformer)Preconditions.checkNotNull(transformer);
      }

      public int size() {
         return this.fromMap.size();
      }

      public boolean containsKey(Object key) {
         return this.fromMap.containsKey(key);
      }

      public Object get(Object key) {
         V1 value = this.fromMap.get(key);
         return value == null && !this.fromMap.containsKey(key)?null:this.transformer.transformEntry(key, value);
      }

      public Object remove(Object key) {
         return this.fromMap.containsKey(key)?this.transformer.transformEntry(key, this.fromMap.remove(key)):null;
      }

      public void clear() {
         this.fromMap.clear();
      }

      public Set keySet() {
         return this.fromMap.keySet();
      }

      protected Set createEntrySet() {
         return new Maps.EntrySet() {
            Map map() {
               return TransformedEntriesMap.this;
            }

            public Iterator iterator() {
               return Iterators.transform(TransformedEntriesMap.this.fromMap.entrySet().iterator(), Maps.asEntryToEntryFunction(TransformedEntriesMap.this.transformer));
            }
         };
      }
   }

   @GwtIncompatible("NavigableMap")
   private static class TransformedEntriesNavigableMap extends Maps.TransformedEntriesSortedMap implements NavigableMap {
      TransformedEntriesNavigableMap(NavigableMap fromMap, Maps.EntryTransformer transformer) {
         super(fromMap, transformer);
      }

      public Entry ceilingEntry(Object key) {
         return this.transformEntry(this.fromMap().ceilingEntry(key));
      }

      public Object ceilingKey(Object key) {
         return this.fromMap().ceilingKey(key);
      }

      public NavigableSet descendingKeySet() {
         return this.fromMap().descendingKeySet();
      }

      public NavigableMap descendingMap() {
         return Maps.transformEntries(this.fromMap().descendingMap(), this.transformer);
      }

      public Entry firstEntry() {
         return this.transformEntry(this.fromMap().firstEntry());
      }

      public Entry floorEntry(Object key) {
         return this.transformEntry(this.fromMap().floorEntry(key));
      }

      public Object floorKey(Object key) {
         return this.fromMap().floorKey(key);
      }

      public NavigableMap headMap(Object toKey) {
         return this.headMap(toKey, false);
      }

      public NavigableMap headMap(Object toKey, boolean inclusive) {
         return Maps.transformEntries(this.fromMap().headMap(toKey, inclusive), this.transformer);
      }

      public Entry higherEntry(Object key) {
         return this.transformEntry(this.fromMap().higherEntry(key));
      }

      public Object higherKey(Object key) {
         return this.fromMap().higherKey(key);
      }

      public Entry lastEntry() {
         return this.transformEntry(this.fromMap().lastEntry());
      }

      public Entry lowerEntry(Object key) {
         return this.transformEntry(this.fromMap().lowerEntry(key));
      }

      public Object lowerKey(Object key) {
         return this.fromMap().lowerKey(key);
      }

      public NavigableSet navigableKeySet() {
         return this.fromMap().navigableKeySet();
      }

      public Entry pollFirstEntry() {
         return this.transformEntry(this.fromMap().pollFirstEntry());
      }

      public Entry pollLastEntry() {
         return this.transformEntry(this.fromMap().pollLastEntry());
      }

      public NavigableMap subMap(Object fromKey, boolean fromInclusive, Object toKey, boolean toInclusive) {
         return Maps.transformEntries(this.fromMap().subMap(fromKey, fromInclusive, toKey, toInclusive), this.transformer);
      }

      public NavigableMap subMap(Object fromKey, Object toKey) {
         return this.subMap(fromKey, true, toKey, false);
      }

      public NavigableMap tailMap(Object fromKey) {
         return this.tailMap(fromKey, true);
      }

      public NavigableMap tailMap(Object fromKey, boolean inclusive) {
         return Maps.transformEntries(this.fromMap().tailMap(fromKey, inclusive), this.transformer);
      }

      @Nullable
      private Entry transformEntry(@Nullable Entry entry) {
         return entry == null?null:Maps.transformEntry(this.transformer, entry);
      }

      protected NavigableMap fromMap() {
         return (NavigableMap)super.fromMap();
      }
   }

   static class TransformedEntriesSortedMap extends Maps.TransformedEntriesMap implements SortedMap {
      protected SortedMap fromMap() {
         return (SortedMap)this.fromMap;
      }

      TransformedEntriesSortedMap(SortedMap fromMap, Maps.EntryTransformer transformer) {
         super(fromMap, transformer);
      }

      public Comparator comparator() {
         return this.fromMap().comparator();
      }

      public Object firstKey() {
         return this.fromMap().firstKey();
      }

      public SortedMap headMap(Object toKey) {
         return Platform.mapsTransformEntriesSortedMap(this.fromMap().headMap(toKey), this.transformer);
      }

      public Object lastKey() {
         return this.fromMap().lastKey();
      }

      public SortedMap subMap(Object fromKey, Object toKey) {
         return Platform.mapsTransformEntriesSortedMap(this.fromMap().subMap(fromKey, toKey), this.transformer);
      }

      public SortedMap tailMap(Object fromKey) {
         return Platform.mapsTransformEntriesSortedMap(this.fromMap().tailMap(fromKey), this.transformer);
      }
   }

   private static class UnmodifiableBiMap extends ForwardingMap implements BiMap, Serializable {
      final Map unmodifiableMap;
      final BiMap delegate;
      BiMap inverse;
      transient Set values;
      private static final long serialVersionUID = 0L;

      UnmodifiableBiMap(BiMap delegate, @Nullable BiMap inverse) {
         this.unmodifiableMap = Collections.unmodifiableMap(delegate);
         this.delegate = delegate;
         this.inverse = inverse;
      }

      protected Map delegate() {
         return this.unmodifiableMap;
      }

      public Object forcePut(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      public BiMap inverse() {
         BiMap<V, K> result = this.inverse;
         return result == null?(this.inverse = new Maps.UnmodifiableBiMap(this.delegate.inverse(), this)):result;
      }

      public Set values() {
         Set<V> result = this.values;
         return result == null?(this.values = Collections.unmodifiableSet(this.delegate.values())):result;
      }
   }

   static class UnmodifiableEntries extends ForwardingCollection {
      private final Collection entries;

      UnmodifiableEntries(Collection entries) {
         this.entries = entries;
      }

      protected Collection delegate() {
         return this.entries;
      }

      public Iterator iterator() {
         final Iterator<Entry<K, V>> delegate = super.iterator();
         return new UnmodifiableIterator() {
            public boolean hasNext() {
               return delegate.hasNext();
            }

            public Entry next() {
               return Maps.unmodifiableEntry((Entry)delegate.next());
            }
         };
      }

      public Object[] toArray() {
         return this.standardToArray();
      }

      public Object[] toArray(Object[] array) {
         return this.standardToArray(array);
      }
   }

   static class UnmodifiableEntrySet extends Maps.UnmodifiableEntries implements Set {
      UnmodifiableEntrySet(Set entries) {
         super(entries);
      }

      public boolean equals(@Nullable Object object) {
         return Sets.equalsImpl(this, object);
      }

      public int hashCode() {
         return Sets.hashCodeImpl(this);
      }
   }

   @GwtIncompatible("NavigableMap")
   static class UnmodifiableNavigableMap extends ForwardingSortedMap implements NavigableMap, Serializable {
      private final NavigableMap delegate;
      private transient Maps.UnmodifiableNavigableMap descendingMap;

      UnmodifiableNavigableMap(NavigableMap delegate) {
         this.delegate = delegate;
      }

      UnmodifiableNavigableMap(NavigableMap delegate, Maps.UnmodifiableNavigableMap descendingMap) {
         this.delegate = delegate;
         this.descendingMap = descendingMap;
      }

      protected SortedMap delegate() {
         return Collections.unmodifiableSortedMap(this.delegate);
      }

      public Entry lowerEntry(Object key) {
         return Maps.unmodifiableOrNull(this.delegate.lowerEntry(key));
      }

      public Object lowerKey(Object key) {
         return this.delegate.lowerKey(key);
      }

      public Entry floorEntry(Object key) {
         return Maps.unmodifiableOrNull(this.delegate.floorEntry(key));
      }

      public Object floorKey(Object key) {
         return this.delegate.floorKey(key);
      }

      public Entry ceilingEntry(Object key) {
         return Maps.unmodifiableOrNull(this.delegate.ceilingEntry(key));
      }

      public Object ceilingKey(Object key) {
         return this.delegate.ceilingKey(key);
      }

      public Entry higherEntry(Object key) {
         return Maps.unmodifiableOrNull(this.delegate.higherEntry(key));
      }

      public Object higherKey(Object key) {
         return this.delegate.higherKey(key);
      }

      public Entry firstEntry() {
         return Maps.unmodifiableOrNull(this.delegate.firstEntry());
      }

      public Entry lastEntry() {
         return Maps.unmodifiableOrNull(this.delegate.lastEntry());
      }

      public final Entry pollFirstEntry() {
         throw new UnsupportedOperationException();
      }

      public final Entry pollLastEntry() {
         throw new UnsupportedOperationException();
      }

      public NavigableMap descendingMap() {
         Maps.UnmodifiableNavigableMap<K, V> result = this.descendingMap;
         return result == null?(this.descendingMap = new Maps.UnmodifiableNavigableMap(this.delegate.descendingMap(), this)):result;
      }

      public Set keySet() {
         return this.navigableKeySet();
      }

      public NavigableSet navigableKeySet() {
         return Sets.unmodifiableNavigableSet(this.delegate.navigableKeySet());
      }

      public NavigableSet descendingKeySet() {
         return Sets.unmodifiableNavigableSet(this.delegate.descendingKeySet());
      }

      public SortedMap subMap(Object fromKey, Object toKey) {
         return this.subMap(fromKey, true, toKey, false);
      }

      public SortedMap headMap(Object toKey) {
         return this.headMap(toKey, false);
      }

      public SortedMap tailMap(Object fromKey) {
         return this.tailMap(fromKey, true);
      }

      public NavigableMap subMap(Object fromKey, boolean fromInclusive, Object toKey, boolean toInclusive) {
         return Maps.unmodifiableNavigableMap(this.delegate.subMap(fromKey, fromInclusive, toKey, toInclusive));
      }

      public NavigableMap headMap(Object toKey, boolean inclusive) {
         return Maps.unmodifiableNavigableMap(this.delegate.headMap(toKey, inclusive));
      }

      public NavigableMap tailMap(Object fromKey, boolean inclusive) {
         return Maps.unmodifiableNavigableMap(this.delegate.tailMap(fromKey, inclusive));
      }
   }

   static class ValueDifferenceImpl implements MapDifference.ValueDifference {
      private final Object left;
      private final Object right;

      static MapDifference.ValueDifference create(@Nullable Object left, @Nullable Object right) {
         return new Maps.ValueDifferenceImpl(left, right);
      }

      private ValueDifferenceImpl(@Nullable Object left, @Nullable Object right) {
         this.left = left;
         this.right = right;
      }

      public Object leftValue() {
         return this.left;
      }

      public Object rightValue() {
         return this.right;
      }

      public boolean equals(@Nullable Object object) {
         if(!(object instanceof MapDifference.ValueDifference)) {
            return false;
         } else {
            MapDifference.ValueDifference<?> that = (MapDifference.ValueDifference)object;
            return Objects.equal(this.left, that.leftValue()) && Objects.equal(this.right, that.rightValue());
         }
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.left, this.right});
      }

      public String toString() {
         return "(" + this.left + ", " + this.right + ")";
      }
   }

   static class Values extends AbstractCollection {
      final Map map;

      Values(Map map) {
         this.map = (Map)Preconditions.checkNotNull(map);
      }

      final Map map() {
         return this.map;
      }

      public Iterator iterator() {
         return Maps.valueIterator(this.map().entrySet().iterator());
      }

      public boolean remove(Object o) {
         try {
            return super.remove(o);
         } catch (UnsupportedOperationException var5) {
            for(Entry<K, V> entry : this.map().entrySet()) {
               if(Objects.equal(o, entry.getValue())) {
                  this.map().remove(entry.getKey());
                  return true;
               }
            }

            return false;
         }
      }

      public boolean removeAll(Collection c) {
         try {
            return super.removeAll((Collection)Preconditions.checkNotNull(c));
         } catch (UnsupportedOperationException var6) {
            Set<K> toRemove = Sets.newHashSet();

            for(Entry<K, V> entry : this.map().entrySet()) {
               if(c.contains(entry.getValue())) {
                  toRemove.add(entry.getKey());
               }
            }

            return this.map().keySet().removeAll(toRemove);
         }
      }

      public boolean retainAll(Collection c) {
         try {
            return super.retainAll((Collection)Preconditions.checkNotNull(c));
         } catch (UnsupportedOperationException var6) {
            Set<K> toRetain = Sets.newHashSet();

            for(Entry<K, V> entry : this.map().entrySet()) {
               if(c.contains(entry.getValue())) {
                  toRetain.add(entry.getKey());
               }
            }

            return this.map().keySet().retainAll(toRetain);
         }
      }

      public int size() {
         return this.map().size();
      }

      public boolean isEmpty() {
         return this.map().isEmpty();
      }

      public boolean contains(@Nullable Object o) {
         return this.map().containsValue(o);
      }

      public void clear() {
         this.map().clear();
      }
   }
}
