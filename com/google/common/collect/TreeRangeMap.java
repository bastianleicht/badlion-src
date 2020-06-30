package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.AbstractMapEntry;
import com.google.common.collect.Cut;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible("NavigableMap")
public final class TreeRangeMap implements RangeMap {
   private final NavigableMap entriesByLowerBound = Maps.newTreeMap();
   private static final RangeMap EMPTY_SUB_RANGE_MAP = new RangeMap() {
      @Nullable
      public Object get(Comparable key) {
         return null;
      }

      @Nullable
      public Entry getEntry(Comparable key) {
         return null;
      }

      public Range span() {
         throw new NoSuchElementException();
      }

      public void put(Range range, Object value) {
         Preconditions.checkNotNull(range);
         throw new IllegalArgumentException("Cannot insert range " + range + " into an empty subRangeMap");
      }

      public void putAll(RangeMap rangeMap) {
         if(!rangeMap.asMapOfRanges().isEmpty()) {
            throw new IllegalArgumentException("Cannot putAll(nonEmptyRangeMap) into an empty subRangeMap");
         }
      }

      public void clear() {
      }

      public void remove(Range range) {
         Preconditions.checkNotNull(range);
      }

      public Map asMapOfRanges() {
         return Collections.emptyMap();
      }

      public RangeMap subRangeMap(Range range) {
         Preconditions.checkNotNull(range);
         return this;
      }
   };

   public static TreeRangeMap create() {
      return new TreeRangeMap();
   }

   @Nullable
   public Object get(Comparable key) {
      Entry<Range<K>, V> entry = this.getEntry(key);
      return entry == null?null:entry.getValue();
   }

   @Nullable
   public Entry getEntry(Comparable key) {
      Entry<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> mapEntry = this.entriesByLowerBound.floorEntry(Cut.belowValue(key));
      return mapEntry != null && ((TreeRangeMap.RangeMapEntry)mapEntry.getValue()).contains(key)?(Entry)mapEntry.getValue():null;
   }

   public void put(Range range, Object value) {
      if(!range.isEmpty()) {
         Preconditions.checkNotNull(value);
         this.remove(range);
         this.entriesByLowerBound.put(range.lowerBound, new TreeRangeMap.RangeMapEntry(range, value));
      }

   }

   public void putAll(RangeMap rangeMap) {
      for(Entry<Range<K>, V> entry : rangeMap.asMapOfRanges().entrySet()) {
         this.put((Range)entry.getKey(), entry.getValue());
      }

   }

   public void clear() {
      this.entriesByLowerBound.clear();
   }

   public Range span() {
      Entry<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> firstEntry = this.entriesByLowerBound.firstEntry();
      Entry<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> lastEntry = this.entriesByLowerBound.lastEntry();
      if(firstEntry == null) {
         throw new NoSuchElementException();
      } else {
         return Range.create(((TreeRangeMap.RangeMapEntry)firstEntry.getValue()).getKey().lowerBound, ((TreeRangeMap.RangeMapEntry)lastEntry.getValue()).getKey().upperBound);
      }
   }

   private void putRangeMapEntry(Cut lowerBound, Cut upperBound, Object value) {
      this.entriesByLowerBound.put(lowerBound, new TreeRangeMap.RangeMapEntry(lowerBound, upperBound, value));
   }

   public void remove(Range rangeToRemove) {
      if(!rangeToRemove.isEmpty()) {
         Entry<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> mapEntryBelowToTruncate = this.entriesByLowerBound.lowerEntry(rangeToRemove.lowerBound);
         if(mapEntryBelowToTruncate != null) {
            TreeRangeMap.RangeMapEntry<K, V> rangeMapEntry = (TreeRangeMap.RangeMapEntry)mapEntryBelowToTruncate.getValue();
            if(rangeMapEntry.getUpperBound().compareTo(rangeToRemove.lowerBound) > 0) {
               if(rangeMapEntry.getUpperBound().compareTo(rangeToRemove.upperBound) > 0) {
                  this.putRangeMapEntry(rangeToRemove.upperBound, rangeMapEntry.getUpperBound(), ((TreeRangeMap.RangeMapEntry)mapEntryBelowToTruncate.getValue()).getValue());
               }

               this.putRangeMapEntry(rangeMapEntry.getLowerBound(), rangeToRemove.lowerBound, ((TreeRangeMap.RangeMapEntry)mapEntryBelowToTruncate.getValue()).getValue());
            }
         }

         Entry<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> mapEntryAboveToTruncate = this.entriesByLowerBound.lowerEntry(rangeToRemove.upperBound);
         if(mapEntryAboveToTruncate != null) {
            TreeRangeMap.RangeMapEntry<K, V> rangeMapEntry = (TreeRangeMap.RangeMapEntry)mapEntryAboveToTruncate.getValue();
            if(rangeMapEntry.getUpperBound().compareTo(rangeToRemove.upperBound) > 0) {
               this.putRangeMapEntry(rangeToRemove.upperBound, rangeMapEntry.getUpperBound(), ((TreeRangeMap.RangeMapEntry)mapEntryAboveToTruncate.getValue()).getValue());
               this.entriesByLowerBound.remove(rangeToRemove.lowerBound);
            }
         }

         this.entriesByLowerBound.subMap(rangeToRemove.lowerBound, rangeToRemove.upperBound).clear();
      }
   }

   public Map asMapOfRanges() {
      return new TreeRangeMap.AsMapOfRanges();
   }

   public RangeMap subRangeMap(Range subRange) {
      return (RangeMap)(subRange.equals(Range.all())?this:new TreeRangeMap.SubRangeMap(subRange));
   }

   private RangeMap emptySubRangeMap() {
      return EMPTY_SUB_RANGE_MAP;
   }

   public boolean equals(@Nullable Object o) {
      if(o instanceof RangeMap) {
         RangeMap<?, ?> rangeMap = (RangeMap)o;
         return this.asMapOfRanges().equals(rangeMap.asMapOfRanges());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.asMapOfRanges().hashCode();
   }

   public String toString() {
      return this.entriesByLowerBound.values().toString();
   }

   private final class AsMapOfRanges extends AbstractMap {
      private AsMapOfRanges() {
      }

      public boolean containsKey(@Nullable Object key) {
         return this.get(key) != null;
      }

      public Object get(@Nullable Object key) {
         if(key instanceof Range) {
            Range<?> range = (Range)key;
            TreeRangeMap.RangeMapEntry<K, V> rangeMapEntry = (TreeRangeMap.RangeMapEntry)TreeRangeMap.this.entriesByLowerBound.get(range.lowerBound);
            if(rangeMapEntry != null && rangeMapEntry.getKey().equals(range)) {
               return rangeMapEntry.getValue();
            }
         }

         return null;
      }

      public Set entrySet() {
         return new AbstractSet() {
            public Iterator iterator() {
               return TreeRangeMap.this.entriesByLowerBound.values().iterator();
            }

            public int size() {
               return TreeRangeMap.this.entriesByLowerBound.size();
            }
         };
      }
   }

   private static final class RangeMapEntry extends AbstractMapEntry {
      private final Range range;
      private final Object value;

      RangeMapEntry(Cut lowerBound, Cut upperBound, Object value) {
         this(Range.create(lowerBound, upperBound), value);
      }

      RangeMapEntry(Range range, Object value) {
         this.range = range;
         this.value = value;
      }

      public Range getKey() {
         return this.range;
      }

      public Object getValue() {
         return this.value;
      }

      public boolean contains(Comparable value) {
         return this.range.contains(value);
      }

      Cut getLowerBound() {
         return this.range.lowerBound;
      }

      Cut getUpperBound() {
         return this.range.upperBound;
      }
   }

   private class SubRangeMap implements RangeMap {
      private final Range subRange;

      SubRangeMap(Range subRange) {
         this.subRange = subRange;
      }

      @Nullable
      public Object get(Comparable key) {
         return this.subRange.contains(key)?TreeRangeMap.this.get(key):null;
      }

      @Nullable
      public Entry getEntry(Comparable key) {
         if(this.subRange.contains(key)) {
            Entry<Range<K>, V> entry = TreeRangeMap.this.getEntry(key);
            if(entry != null) {
               return Maps.immutableEntry(((Range)entry.getKey()).intersection(this.subRange), entry.getValue());
            }
         }

         return null;
      }

      public Range span() {
         Entry<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> lowerEntry = TreeRangeMap.this.entriesByLowerBound.floorEntry(this.subRange.lowerBound);
         Cut<K> lowerBound;
         if(lowerEntry != null && ((TreeRangeMap.RangeMapEntry)lowerEntry.getValue()).getUpperBound().compareTo(this.subRange.lowerBound) > 0) {
            lowerBound = this.subRange.lowerBound;
         } else {
            lowerBound = (Cut)TreeRangeMap.this.entriesByLowerBound.ceilingKey(this.subRange.lowerBound);
            if(lowerBound == null || lowerBound.compareTo(this.subRange.upperBound) >= 0) {
               throw new NoSuchElementException();
            }
         }

         Entry<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> upperEntry = TreeRangeMap.this.entriesByLowerBound.lowerEntry(this.subRange.upperBound);
         if(upperEntry == null) {
            throw new NoSuchElementException();
         } else {
            Cut<K> upperBound;
            if(((TreeRangeMap.RangeMapEntry)upperEntry.getValue()).getUpperBound().compareTo(this.subRange.upperBound) >= 0) {
               upperBound = this.subRange.upperBound;
            } else {
               upperBound = ((TreeRangeMap.RangeMapEntry)upperEntry.getValue()).getUpperBound();
            }

            return Range.create(lowerBound, upperBound);
         }
      }

      public void put(Range range, Object value) {
         Preconditions.checkArgument(this.subRange.encloses(range), "Cannot put range %s into a subRangeMap(%s)", new Object[]{range, this.subRange});
         TreeRangeMap.this.put(range, value);
      }

      public void putAll(RangeMap rangeMap) {
         if(!rangeMap.asMapOfRanges().isEmpty()) {
            Range<K> span = rangeMap.span();
            Preconditions.checkArgument(this.subRange.encloses(span), "Cannot putAll rangeMap with span %s into a subRangeMap(%s)", new Object[]{span, this.subRange});
            TreeRangeMap.this.putAll(rangeMap);
         }
      }

      public void clear() {
         TreeRangeMap.this.remove(this.subRange);
      }

      public void remove(Range range) {
         if(range.isConnected(this.subRange)) {
            TreeRangeMap.this.remove(range.intersection(this.subRange));
         }

      }

      public RangeMap subRangeMap(Range range) {
         return !range.isConnected(this.subRange)?TreeRangeMap.this.emptySubRangeMap():TreeRangeMap.this.subRangeMap(range.intersection(this.subRange));
      }

      public Map asMapOfRanges() {
         return new TreeRangeMap.SubRangeMap.SubRangeMapAsMap();
      }

      public boolean equals(@Nullable Object o) {
         if(o instanceof RangeMap) {
            RangeMap<?, ?> rangeMap = (RangeMap)o;
            return this.asMapOfRanges().equals(rangeMap.asMapOfRanges());
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.asMapOfRanges().hashCode();
      }

      public String toString() {
         return this.asMapOfRanges().toString();
      }

      class SubRangeMapAsMap extends AbstractMap {
         public boolean containsKey(Object key) {
            return this.get(key) != null;
         }

         public Object get(Object key) {
            try {
               if(key instanceof Range) {
                  Range<K> r = (Range)key;
                  if(!SubRangeMap.this.subRange.encloses(r) || r.isEmpty()) {
                     return null;
                  }

                  TreeRangeMap.RangeMapEntry<K, V> candidate = null;
                  if(r.lowerBound.compareTo(SubRangeMap.this.subRange.lowerBound) == 0) {
                     Entry<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> entry = TreeRangeMap.this.entriesByLowerBound.floorEntry(r.lowerBound);
                     if(entry != null) {
                        candidate = (TreeRangeMap.RangeMapEntry)entry.getValue();
                     }
                  } else {
                     candidate = (TreeRangeMap.RangeMapEntry)TreeRangeMap.this.entriesByLowerBound.get(r.lowerBound);
                  }

                  if(candidate != null && candidate.getKey().isConnected(SubRangeMap.this.subRange) && candidate.getKey().intersection(SubRangeMap.this.subRange).equals(r)) {
                     return candidate.getValue();
                  }
               }

               return null;
            } catch (ClassCastException var5) {
               return null;
            }
         }

         public Object remove(Object key) {
            V value = this.get(key);
            if(value != null) {
               Range<K> range = (Range)key;
               TreeRangeMap.this.remove(range);
               return value;
            } else {
               return null;
            }
         }

         public void clear() {
            SubRangeMap.this.clear();
         }

         private boolean removeEntryIf(Predicate predicate) {
            List<Range<K>> toRemove = Lists.newArrayList();

            for(Entry<Range<K>, V> entry : this.entrySet()) {
               if(predicate.apply(entry)) {
                  toRemove.add(entry.getKey());
               }
            }

            for(Range<K> range : toRemove) {
               TreeRangeMap.this.remove(range);
            }

            return !toRemove.isEmpty();
         }

         public Set keySet() {
            return new Maps.KeySet(this) {
               public boolean remove(@Nullable Object o) {
                  return SubRangeMapAsMap.this.remove(o) != null;
               }

               public boolean retainAll(Collection c) {
                  return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.not(Predicates.in(c)), Maps.keyFunction()));
               }
            };
         }

         public Set entrySet() {
            return new Maps.EntrySet() {
               Map map() {
                  return SubRangeMapAsMap.this;
               }

               public Iterator iterator() {
                  if(SubRangeMap.this.subRange.isEmpty()) {
                     return Iterators.emptyIterator();
                  } else {
                     Cut<K> cutToStart = (Cut)Objects.firstNonNull(TreeRangeMap.this.entriesByLowerBound.floorKey(SubRangeMap.this.subRange.lowerBound), SubRangeMap.this.subRange.lowerBound);
                     final Iterator<TreeRangeMap.RangeMapEntry<K, V>> backingItr = TreeRangeMap.this.entriesByLowerBound.tailMap(cutToStart, true).values().iterator();
                     return new AbstractIterator() {
                        protected Entry computeNext() {
                           while(true) {
                              if(backingItr.hasNext()) {
                                 TreeRangeMap.RangeMapEntry<K, V> entry = (TreeRangeMap.RangeMapEntry)backingItr.next();
                                 if(entry.getLowerBound().compareTo(SubRangeMap.this.subRange.upperBound) < 0) {
                                    if(entry.getUpperBound().compareTo(SubRangeMap.this.subRange.lowerBound) <= 0) {
                                       continue;
                                    }

                                    return Maps.immutableEntry(entry.getKey().intersection(SubRangeMap.this.subRange), entry.getValue());
                                 }
                              }

                              return (Entry)this.endOfData();
                           }
                        }
                     };
                  }
               }

               public boolean retainAll(Collection c) {
                  return SubRangeMapAsMap.this.removeEntryIf(Predicates.not(Predicates.in(c)));
               }

               public int size() {
                  return Iterators.size(this.iterator());
               }

               public boolean isEmpty() {
                  return !this.iterator().hasNext();
               }
            };
         }

         public Collection values() {
            return new Maps.Values(this) {
               public boolean removeAll(Collection c) {
                  return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.in(c), Maps.valueFunction()));
               }

               public boolean retainAll(Collection c) {
                  return SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.not(Predicates.in(c)), Maps.valueFunction()));
               }
            };
         }
      }
   }
}
