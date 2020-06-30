package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible(
   emulated = true
)
final class Platform {
   static Object[] newArray(Object[] reference, int length) {
      Class<?> type = reference.getClass().getComponentType();
      T[] result = (Object[])((Object[])Array.newInstance(type, length));
      return result;
   }

   static Set newSetFromMap(Map map) {
      return Collections.newSetFromMap(map);
   }

   static MapMaker tryWeakKeys(MapMaker mapMaker) {
      return mapMaker.weakKeys();
   }

   static SortedMap mapsTransformEntriesSortedMap(SortedMap fromMap, Maps.EntryTransformer transformer) {
      return (SortedMap)(fromMap instanceof NavigableMap?Maps.transformEntries((NavigableMap)fromMap, transformer):Maps.transformEntriesIgnoreNavigable(fromMap, transformer));
   }

   static SortedMap mapsAsMapSortedSet(SortedSet set, Function function) {
      return (SortedMap)(set instanceof NavigableSet?Maps.asMap((NavigableSet)set, function):Maps.asMapSortedIgnoreNavigable(set, function));
   }

   static SortedSet setsFilterSortedSet(SortedSet set, Predicate predicate) {
      return (SortedSet)(set instanceof NavigableSet?Sets.filter((NavigableSet)set, predicate):Sets.filterSortedIgnoreNavigable(set, predicate));
   }

   static SortedMap mapsFilterSortedMap(SortedMap map, Predicate predicate) {
      return (SortedMap)(map instanceof NavigableMap?Maps.filterEntries((NavigableMap)map, predicate):Maps.filterSortedIgnoreNavigable(map, predicate));
   }
}
