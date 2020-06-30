package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Cut;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.RangeSet;
import com.google.common.collect.RegularImmutableSortedMap;
import com.google.common.collect.RegularImmutableSortedSet;
import com.google.common.collect.SortedLists;
import com.google.common.collect.TreeRangeMap;
import com.google.common.collect.TreeRangeSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible("NavigableMap")
public class ImmutableRangeMap implements RangeMap {
   private static final ImmutableRangeMap EMPTY = new ImmutableRangeMap(ImmutableList.of(), ImmutableList.of());
   private final ImmutableList ranges;
   private final ImmutableList values;

   public static ImmutableRangeMap of() {
      return EMPTY;
   }

   public static ImmutableRangeMap of(Range range, Object value) {
      return new ImmutableRangeMap(ImmutableList.of(range), ImmutableList.of(value));
   }

   public static ImmutableRangeMap copyOf(RangeMap rangeMap) {
      if(rangeMap instanceof ImmutableRangeMap) {
         return (ImmutableRangeMap)rangeMap;
      } else {
         Map<Range<K>, ? extends V> map = rangeMap.asMapOfRanges();
         ImmutableList.Builder<Range<K>> rangesBuilder = new ImmutableList.Builder(map.size());
         ImmutableList.Builder<V> valuesBuilder = new ImmutableList.Builder(map.size());

         for(Entry<Range<K>, ? extends V> entry : map.entrySet()) {
            rangesBuilder.add(entry.getKey());
            valuesBuilder.add(entry.getValue());
         }

         return new ImmutableRangeMap(rangesBuilder.build(), valuesBuilder.build());
      }
   }

   public static ImmutableRangeMap.Builder builder() {
      return new ImmutableRangeMap.Builder();
   }

   ImmutableRangeMap(ImmutableList ranges, ImmutableList values) {
      this.ranges = ranges;
      this.values = values;
   }

   @Nullable
   public Object get(Comparable key) {
      int index = SortedLists.binarySearch(this.ranges, (Function)Range.lowerBoundFn(), (Comparable)Cut.belowValue(key), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
      if(index == -1) {
         return null;
      } else {
         Range<K> range = (Range)this.ranges.get(index);
         return range.contains(key)?this.values.get(index):null;
      }
   }

   @Nullable
   public Entry getEntry(Comparable key) {
      int index = SortedLists.binarySearch(this.ranges, (Function)Range.lowerBoundFn(), (Comparable)Cut.belowValue(key), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
      if(index == -1) {
         return null;
      } else {
         Range<K> range = (Range)this.ranges.get(index);
         return range.contains(key)?Maps.immutableEntry(range, this.values.get(index)):null;
      }
   }

   public Range span() {
      if(this.ranges.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         Range<K> firstRange = (Range)this.ranges.get(0);
         Range<K> lastRange = (Range)this.ranges.get(this.ranges.size() - 1);
         return Range.create(firstRange.lowerBound, lastRange.upperBound);
      }
   }

   public void put(Range range, Object value) {
      throw new UnsupportedOperationException();
   }

   public void putAll(RangeMap rangeMap) {
      throw new UnsupportedOperationException();
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public void remove(Range range) {
      throw new UnsupportedOperationException();
   }

   public ImmutableMap asMapOfRanges() {
      if(this.ranges.isEmpty()) {
         return ImmutableMap.of();
      } else {
         RegularImmutableSortedSet<Range<K>> rangeSet = new RegularImmutableSortedSet(this.ranges, Range.RANGE_LEX_ORDERING);
         return new RegularImmutableSortedMap(rangeSet, this.values);
      }
   }

   public ImmutableRangeMap subRangeMap(final Range range) {
      if(((Range)Preconditions.checkNotNull(range)).isEmpty()) {
         return of();
      } else if(!this.ranges.isEmpty() && !range.encloses(this.span())) {
         final int lowerIndex = SortedLists.binarySearch(this.ranges, (Function)Range.upperBoundFn(), (Comparable)range.lowerBound, SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
         int upperIndex = SortedLists.binarySearch(this.ranges, (Function)Range.lowerBoundFn(), (Comparable)range.upperBound, SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
         if(lowerIndex >= upperIndex) {
            return of();
         } else {
            final int len = upperIndex - lowerIndex;
            final ImmutableList<Range<K>> subRanges = new ImmutableList() {
               public int size() {
                  return len;
               }

               public Range get(int index) {
                  Preconditions.checkElementIndex(index, len);
                  return index != 0 && index != len - 1?(Range)ImmutableRangeMap.this.ranges.get(index + lowerIndex):((Range)ImmutableRangeMap.this.ranges.get(index + lowerIndex)).intersection(range);
               }

               boolean isPartialView() {
                  return true;
               }
            };
            return new ImmutableRangeMap(subRanges, this.values.subList(lowerIndex, upperIndex)) {
               public ImmutableRangeMap subRangeMap(Range subRange) {
                  return range.isConnected(subRange)?ImmutableRangeMap.this.subRangeMap(subRange.intersection(range)):ImmutableRangeMap.EMPTY;
               }
            };
         }
      } else {
         return this;
      }
   }

   public int hashCode() {
      return this.asMapOfRanges().hashCode();
   }

   public boolean equals(@Nullable Object o) {
      if(o instanceof RangeMap) {
         RangeMap<?, ?> rangeMap = (RangeMap)o;
         return this.asMapOfRanges().equals(rangeMap.asMapOfRanges());
      } else {
         return false;
      }
   }

   public String toString() {
      return this.asMapOfRanges().toString();
   }

   public static final class Builder {
      private final RangeSet keyRanges = TreeRangeSet.create();
      private final RangeMap rangeMap = TreeRangeMap.create();

      public ImmutableRangeMap.Builder put(Range range, Object value) {
         Preconditions.checkNotNull(range);
         Preconditions.checkNotNull(value);
         Preconditions.checkArgument(!range.isEmpty(), "Range must not be empty, but was %s", new Object[]{range});
         if(!this.keyRanges.complement().encloses(range)) {
            for(Entry<Range<K>, V> entry : this.rangeMap.asMapOfRanges().entrySet()) {
               Range<K> key = (Range)entry.getKey();
               if(key.isConnected(range) && !key.intersection(range).isEmpty()) {
                  throw new IllegalArgumentException("Overlapping ranges: range " + range + " overlaps with entry " + entry);
               }
            }
         }

         this.keyRanges.add(range);
         this.rangeMap.put(range, value);
         return this;
      }

      public ImmutableRangeMap.Builder putAll(RangeMap rangeMap) {
         for(Entry<Range<K>, ? extends V> entry : rangeMap.asMapOfRanges().entrySet()) {
            this.put((Range)entry.getKey(), entry.getValue());
         }

         return this;
      }

      public ImmutableRangeMap build() {
         Map<Range<K>, V> map = this.rangeMap.asMapOfRanges();
         ImmutableList.Builder<Range<K>> rangesBuilder = new ImmutableList.Builder(map.size());
         ImmutableList.Builder<V> valuesBuilder = new ImmutableList.Builder(map.size());

         for(Entry<Range<K>, V> entry : map.entrySet()) {
            rangesBuilder.add(entry.getKey());
            valuesBuilder.add(entry.getValue());
         }

         return new ImmutableRangeMap(rangesBuilder.build(), valuesBuilder.build());
      }
   }
}
