package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.AbstractNavigableMap;
import com.google.common.collect.AbstractRangeSet;
import com.google.common.collect.BoundType;
import com.google.common.collect.Cut;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible("uses NavigableMap")
public class TreeRangeSet extends AbstractRangeSet {
   @VisibleForTesting
   final NavigableMap rangesByLowerBound;
   private transient Set asRanges;
   private transient RangeSet complement;

   public static TreeRangeSet create() {
      return new TreeRangeSet(new TreeMap());
   }

   public static TreeRangeSet create(RangeSet rangeSet) {
      TreeRangeSet<C> result = create();
      result.addAll(rangeSet);
      return result;
   }

   private TreeRangeSet(NavigableMap rangesByLowerCut) {
      this.rangesByLowerBound = rangesByLowerCut;
   }

   public Set asRanges() {
      Set<Range<C>> result = this.asRanges;
      return result == null?(this.asRanges = new TreeRangeSet.AsRanges()):result;
   }

   @Nullable
   public Range rangeContaining(Comparable value) {
      Preconditions.checkNotNull(value);
      Entry<Cut<C>, Range<C>> floorEntry = this.rangesByLowerBound.floorEntry(Cut.belowValue(value));
      return floorEntry != null && ((Range)floorEntry.getValue()).contains(value)?(Range)floorEntry.getValue():null;
   }

   public boolean encloses(Range range) {
      Preconditions.checkNotNull(range);
      Entry<Cut<C>, Range<C>> floorEntry = this.rangesByLowerBound.floorEntry(range.lowerBound);
      return floorEntry != null && ((Range)floorEntry.getValue()).encloses(range);
   }

   @Nullable
   private Range rangeEnclosing(Range range) {
      Preconditions.checkNotNull(range);
      Entry<Cut<C>, Range<C>> floorEntry = this.rangesByLowerBound.floorEntry(range.lowerBound);
      return floorEntry != null && ((Range)floorEntry.getValue()).encloses(range)?(Range)floorEntry.getValue():null;
   }

   public Range span() {
      Entry<Cut<C>, Range<C>> firstEntry = this.rangesByLowerBound.firstEntry();
      Entry<Cut<C>, Range<C>> lastEntry = this.rangesByLowerBound.lastEntry();
      if(firstEntry == null) {
         throw new NoSuchElementException();
      } else {
         return Range.create(((Range)firstEntry.getValue()).lowerBound, ((Range)lastEntry.getValue()).upperBound);
      }
   }

   public void add(Range rangeToAdd) {
      Preconditions.checkNotNull(rangeToAdd);
      if(!rangeToAdd.isEmpty()) {
         Cut<C> lbToAdd = rangeToAdd.lowerBound;
         Cut<C> ubToAdd = rangeToAdd.upperBound;
         Entry<Cut<C>, Range<C>> entryBelowLB = this.rangesByLowerBound.lowerEntry(lbToAdd);
         if(entryBelowLB != null) {
            Range<C> rangeBelowLB = (Range)entryBelowLB.getValue();
            if(rangeBelowLB.upperBound.compareTo(lbToAdd) >= 0) {
               if(rangeBelowLB.upperBound.compareTo(ubToAdd) >= 0) {
                  ubToAdd = rangeBelowLB.upperBound;
               }

               lbToAdd = rangeBelowLB.lowerBound;
            }
         }

         Entry<Cut<C>, Range<C>> entryBelowUB = this.rangesByLowerBound.floorEntry(ubToAdd);
         if(entryBelowUB != null) {
            Range<C> rangeBelowUB = (Range)entryBelowUB.getValue();
            if(rangeBelowUB.upperBound.compareTo(ubToAdd) >= 0) {
               ubToAdd = rangeBelowUB.upperBound;
            }
         }

         this.rangesByLowerBound.subMap(lbToAdd, ubToAdd).clear();
         this.replaceRangeWithSameLowerBound(Range.create(lbToAdd, ubToAdd));
      }
   }

   public void remove(Range rangeToRemove) {
      Preconditions.checkNotNull(rangeToRemove);
      if(!rangeToRemove.isEmpty()) {
         Entry<Cut<C>, Range<C>> entryBelowLB = this.rangesByLowerBound.lowerEntry(rangeToRemove.lowerBound);
         if(entryBelowLB != null) {
            Range<C> rangeBelowLB = (Range)entryBelowLB.getValue();
            if(rangeBelowLB.upperBound.compareTo(rangeToRemove.lowerBound) >= 0) {
               if(rangeToRemove.hasUpperBound() && rangeBelowLB.upperBound.compareTo(rangeToRemove.upperBound) >= 0) {
                  this.replaceRangeWithSameLowerBound(Range.create(rangeToRemove.upperBound, rangeBelowLB.upperBound));
               }

               this.replaceRangeWithSameLowerBound(Range.create(rangeBelowLB.lowerBound, rangeToRemove.lowerBound));
            }
         }

         Entry<Cut<C>, Range<C>> entryBelowUB = this.rangesByLowerBound.floorEntry(rangeToRemove.upperBound);
         if(entryBelowUB != null) {
            Range<C> rangeBelowUB = (Range)entryBelowUB.getValue();
            if(rangeToRemove.hasUpperBound() && rangeBelowUB.upperBound.compareTo(rangeToRemove.upperBound) >= 0) {
               this.replaceRangeWithSameLowerBound(Range.create(rangeToRemove.upperBound, rangeBelowUB.upperBound));
            }
         }

         this.rangesByLowerBound.subMap(rangeToRemove.lowerBound, rangeToRemove.upperBound).clear();
      }
   }

   private void replaceRangeWithSameLowerBound(Range range) {
      if(range.isEmpty()) {
         this.rangesByLowerBound.remove(range.lowerBound);
      } else {
         this.rangesByLowerBound.put(range.lowerBound, range);
      }

   }

   public RangeSet complement() {
      RangeSet<C> result = this.complement;
      return result == null?(this.complement = new TreeRangeSet.Complement()):result;
   }

   public RangeSet subRangeSet(Range view) {
      return (RangeSet)(view.equals(Range.all())?this:new TreeRangeSet.SubRangeSet(view));
   }

   final class AsRanges extends ForwardingCollection implements Set {
      protected Collection delegate() {
         return TreeRangeSet.this.rangesByLowerBound.values();
      }

      public int hashCode() {
         return Sets.hashCodeImpl(this);
      }

      public boolean equals(@Nullable Object o) {
         return Sets.equalsImpl(this, o);
      }
   }

   private final class Complement extends TreeRangeSet {
      Complement() {
         super(new TreeRangeSet.ComplementRangesByLowerBound(TreeRangeSet.this.rangesByLowerBound), null);
      }

      public void add(Range rangeToAdd) {
         TreeRangeSet.this.remove(rangeToAdd);
      }

      public void remove(Range rangeToRemove) {
         TreeRangeSet.this.add(rangeToRemove);
      }

      public boolean contains(Comparable value) {
         return !TreeRangeSet.this.contains(value);
      }

      public RangeSet complement() {
         return TreeRangeSet.this;
      }
   }

   private static final class ComplementRangesByLowerBound extends AbstractNavigableMap {
      private final NavigableMap positiveRangesByLowerBound;
      private final NavigableMap positiveRangesByUpperBound;
      private final Range complementLowerBoundWindow;

      ComplementRangesByLowerBound(NavigableMap positiveRangesByLowerBound) {
         this(positiveRangesByLowerBound, Range.all());
      }

      private ComplementRangesByLowerBound(NavigableMap positiveRangesByLowerBound, Range window) {
         this.positiveRangesByLowerBound = positiveRangesByLowerBound;
         this.positiveRangesByUpperBound = new TreeRangeSet.RangesByUpperBound(positiveRangesByLowerBound);
         this.complementLowerBoundWindow = window;
      }

      private NavigableMap subMap(Range subWindow) {
         if(!this.complementLowerBoundWindow.isConnected(subWindow)) {
            return ImmutableSortedMap.of();
         } else {
            subWindow = subWindow.intersection(this.complementLowerBoundWindow);
            return new TreeRangeSet.ComplementRangesByLowerBound(this.positiveRangesByLowerBound, subWindow);
         }
      }

      public NavigableMap subMap(Cut fromKey, boolean fromInclusive, Cut toKey, boolean toInclusive) {
         return this.subMap(Range.range(fromKey, BoundType.forBoolean(fromInclusive), toKey, BoundType.forBoolean(toInclusive)));
      }

      public NavigableMap headMap(Cut toKey, boolean inclusive) {
         return this.subMap(Range.upTo(toKey, BoundType.forBoolean(inclusive)));
      }

      public NavigableMap tailMap(Cut fromKey, boolean inclusive) {
         return this.subMap(Range.downTo(fromKey, BoundType.forBoolean(inclusive)));
      }

      public Comparator comparator() {
         return Ordering.natural();
      }

      Iterator entryIterator() {
         Collection<Range<C>> positiveRanges;
         if(this.complementLowerBoundWindow.hasLowerBound()) {
            positiveRanges = this.positiveRangesByUpperBound.tailMap(this.complementLowerBoundWindow.lowerEndpoint(), this.complementLowerBoundWindow.lowerBoundType() == BoundType.CLOSED).values();
         } else {
            positiveRanges = this.positiveRangesByUpperBound.values();
         }

         final PeekingIterator<Range<C>> positiveItr = Iterators.peekingIterator(positiveRanges.iterator());
         final Cut<C> firstComplementRangeLowerBound;
         if(!this.complementLowerBoundWindow.contains(Cut.belowAll()) || positiveItr.hasNext() && ((Range)positiveItr.peek()).lowerBound == Cut.belowAll()) {
            if(!positiveItr.hasNext()) {
               return Iterators.emptyIterator();
            }

            firstComplementRangeLowerBound = ((Range)positiveItr.next()).upperBound;
         } else {
            firstComplementRangeLowerBound = Cut.belowAll();
         }

         return new AbstractIterator() {
            Cut nextComplementRangeLowerBound = firstComplementRangeLowerBound;

            protected Entry computeNext() {
               if(!ComplementRangesByLowerBound.this.complementLowerBoundWindow.upperBound.isLessThan(this.nextComplementRangeLowerBound) && this.nextComplementRangeLowerBound != Cut.aboveAll()) {
                  Range<C> negativeRange;
                  if(positiveItr.hasNext()) {
                     Range<C> positiveRange = (Range)positiveItr.next();
                     negativeRange = Range.create(this.nextComplementRangeLowerBound, positiveRange.lowerBound);
                     this.nextComplementRangeLowerBound = positiveRange.upperBound;
                  } else {
                     negativeRange = Range.create(this.nextComplementRangeLowerBound, Cut.aboveAll());
                     this.nextComplementRangeLowerBound = Cut.aboveAll();
                  }

                  return Maps.immutableEntry(negativeRange.lowerBound, negativeRange);
               } else {
                  return (Entry)this.endOfData();
               }
            }
         };
      }

      Iterator descendingEntryIterator() {
         Cut<C> startingPoint = this.complementLowerBoundWindow.hasUpperBound()?(Cut)this.complementLowerBoundWindow.upperEndpoint():Cut.aboveAll();
         boolean inclusive = this.complementLowerBoundWindow.hasUpperBound() && this.complementLowerBoundWindow.upperBoundType() == BoundType.CLOSED;
         final PeekingIterator<Range<C>> positiveItr = Iterators.peekingIterator(this.positiveRangesByUpperBound.headMap(startingPoint, inclusive).descendingMap().values().iterator());
         Cut<C> cut;
         if(positiveItr.hasNext()) {
            cut = ((Range)positiveItr.peek()).upperBound == Cut.aboveAll()?((Range)positiveItr.next()).lowerBound:(Cut)this.positiveRangesByLowerBound.higherKey(((Range)positiveItr.peek()).upperBound);
         } else {
            if(!this.complementLowerBoundWindow.contains(Cut.belowAll()) || this.positiveRangesByLowerBound.containsKey(Cut.belowAll())) {
               return Iterators.emptyIterator();
            }

            cut = (Cut)this.positiveRangesByLowerBound.higherKey(Cut.belowAll());
         }

         final Cut<C> firstComplementRangeUpperBound = (Cut)Objects.firstNonNull(cut, Cut.aboveAll());
         return new AbstractIterator() {
            Cut nextComplementRangeUpperBound = firstComplementRangeUpperBound;

            protected Entry computeNext() {
               if(this.nextComplementRangeUpperBound == Cut.belowAll()) {
                  return (Entry)this.endOfData();
               } else {
                  if(positiveItr.hasNext()) {
                     Range<C> positiveRange = (Range)positiveItr.next();
                     Range<C> negativeRange = Range.create(positiveRange.upperBound, this.nextComplementRangeUpperBound);
                     this.nextComplementRangeUpperBound = positiveRange.lowerBound;
                     if(ComplementRangesByLowerBound.this.complementLowerBoundWindow.lowerBound.isLessThan(negativeRange.lowerBound)) {
                        return Maps.immutableEntry(negativeRange.lowerBound, negativeRange);
                     }
                  } else if(ComplementRangesByLowerBound.this.complementLowerBoundWindow.lowerBound.isLessThan(Cut.belowAll())) {
                     Range<C> negativeRange = Range.create(Cut.belowAll(), this.nextComplementRangeUpperBound);
                     this.nextComplementRangeUpperBound = Cut.belowAll();
                     return Maps.immutableEntry(Cut.belowAll(), negativeRange);
                  }

                  return (Entry)this.endOfData();
               }
            }
         };
      }

      public int size() {
         return Iterators.size(this.entryIterator());
      }

      @Nullable
      public Range get(Object key) {
         if(key instanceof Cut) {
            try {
               Cut<C> cut = (Cut)key;
               Entry<Cut<C>, Range<C>> firstEntry = this.tailMap(cut, true).firstEntry();
               if(firstEntry != null && ((Cut)firstEntry.getKey()).equals(cut)) {
                  return (Range)firstEntry.getValue();
               }
            } catch (ClassCastException var4) {
               return null;
            }
         }

         return null;
      }

      public boolean containsKey(Object key) {
         return this.get(key) != null;
      }
   }

   @VisibleForTesting
   static final class RangesByUpperBound extends AbstractNavigableMap {
      private final NavigableMap rangesByLowerBound;
      private final Range upperBoundWindow;

      RangesByUpperBound(NavigableMap rangesByLowerBound) {
         this.rangesByLowerBound = rangesByLowerBound;
         this.upperBoundWindow = Range.all();
      }

      private RangesByUpperBound(NavigableMap rangesByLowerBound, Range upperBoundWindow) {
         this.rangesByLowerBound = rangesByLowerBound;
         this.upperBoundWindow = upperBoundWindow;
      }

      private NavigableMap subMap(Range window) {
         return (NavigableMap)(window.isConnected(this.upperBoundWindow)?new TreeRangeSet.RangesByUpperBound(this.rangesByLowerBound, window.intersection(this.upperBoundWindow)):ImmutableSortedMap.of());
      }

      public NavigableMap subMap(Cut fromKey, boolean fromInclusive, Cut toKey, boolean toInclusive) {
         return this.subMap(Range.range(fromKey, BoundType.forBoolean(fromInclusive), toKey, BoundType.forBoolean(toInclusive)));
      }

      public NavigableMap headMap(Cut toKey, boolean inclusive) {
         return this.subMap(Range.upTo(toKey, BoundType.forBoolean(inclusive)));
      }

      public NavigableMap tailMap(Cut fromKey, boolean inclusive) {
         return this.subMap(Range.downTo(fromKey, BoundType.forBoolean(inclusive)));
      }

      public Comparator comparator() {
         return Ordering.natural();
      }

      public boolean containsKey(@Nullable Object key) {
         return this.get(key) != null;
      }

      public Range get(@Nullable Object key) {
         if(key instanceof Cut) {
            try {
               Cut<C> cut = (Cut)key;
               if(!this.upperBoundWindow.contains(cut)) {
                  return null;
               }

               Entry<Cut<C>, Range<C>> candidate = this.rangesByLowerBound.lowerEntry(cut);
               if(candidate != null && ((Range)candidate.getValue()).upperBound.equals(cut)) {
                  return (Range)candidate.getValue();
               }
            } catch (ClassCastException var4) {
               return null;
            }
         }

         return null;
      }

      Iterator entryIterator() {
         final Iterator<Range<C>> backingItr;
         if(!this.upperBoundWindow.hasLowerBound()) {
            backingItr = this.rangesByLowerBound.values().iterator();
         } else {
            Entry<Cut<C>, Range<C>> lowerEntry = this.rangesByLowerBound.lowerEntry(this.upperBoundWindow.lowerEndpoint());
            if(lowerEntry == null) {
               backingItr = this.rangesByLowerBound.values().iterator();
            } else if(this.upperBoundWindow.lowerBound.isLessThan(((Range)lowerEntry.getValue()).upperBound)) {
               backingItr = this.rangesByLowerBound.tailMap(lowerEntry.getKey(), true).values().iterator();
            } else {
               backingItr = this.rangesByLowerBound.tailMap(this.upperBoundWindow.lowerEndpoint(), true).values().iterator();
            }
         }

         return new AbstractIterator() {
            protected Entry computeNext() {
               if(!backingItr.hasNext()) {
                  return (Entry)this.endOfData();
               } else {
                  Range<C> range = (Range)backingItr.next();
                  return RangesByUpperBound.this.upperBoundWindow.upperBound.isLessThan(range.upperBound)?(Entry)this.endOfData():Maps.immutableEntry(range.upperBound, range);
               }
            }
         };
      }

      Iterator descendingEntryIterator() {
         Collection<Range<C>> candidates;
         if(this.upperBoundWindow.hasUpperBound()) {
            candidates = this.rangesByLowerBound.headMap(this.upperBoundWindow.upperEndpoint(), false).descendingMap().values();
         } else {
            candidates = this.rangesByLowerBound.descendingMap().values();
         }

         final PeekingIterator<Range<C>> backingItr = Iterators.peekingIterator(candidates.iterator());
         if(backingItr.hasNext() && this.upperBoundWindow.upperBound.isLessThan(((Range)backingItr.peek()).upperBound)) {
            backingItr.next();
         }

         return new AbstractIterator() {
            protected Entry computeNext() {
               if(!backingItr.hasNext()) {
                  return (Entry)this.endOfData();
               } else {
                  Range<C> range = (Range)backingItr.next();
                  return RangesByUpperBound.this.upperBoundWindow.lowerBound.isLessThan(range.upperBound)?Maps.immutableEntry(range.upperBound, range):(Entry)this.endOfData();
               }
            }
         };
      }

      public int size() {
         return this.upperBoundWindow.equals(Range.all())?this.rangesByLowerBound.size():Iterators.size(this.entryIterator());
      }

      public boolean isEmpty() {
         return this.upperBoundWindow.equals(Range.all())?this.rangesByLowerBound.isEmpty():!this.entryIterator().hasNext();
      }
   }

   private final class SubRangeSet extends TreeRangeSet {
      private final Range restriction;

      SubRangeSet(Range restriction) {
         super(new TreeRangeSet.SubRangeSetRangesByLowerBound(Range.all(), restriction, TreeRangeSet.this.rangesByLowerBound), null);
         this.restriction = restriction;
      }

      public boolean encloses(Range range) {
         if(!this.restriction.isEmpty() && this.restriction.encloses(range)) {
            Range<C> enclosing = TreeRangeSet.this.rangeEnclosing(range);
            return enclosing != null && !enclosing.intersection(this.restriction).isEmpty();
         } else {
            return false;
         }
      }

      @Nullable
      public Range rangeContaining(Comparable value) {
         if(!this.restriction.contains(value)) {
            return null;
         } else {
            Range<C> result = TreeRangeSet.this.rangeContaining(value);
            return result == null?null:result.intersection(this.restriction);
         }
      }

      public void add(Range rangeToAdd) {
         Preconditions.checkArgument(this.restriction.encloses(rangeToAdd), "Cannot add range %s to subRangeSet(%s)", new Object[]{rangeToAdd, this.restriction});
         super.add(rangeToAdd);
      }

      public void remove(Range rangeToRemove) {
         if(rangeToRemove.isConnected(this.restriction)) {
            TreeRangeSet.this.remove(rangeToRemove.intersection(this.restriction));
         }

      }

      public boolean contains(Comparable value) {
         return this.restriction.contains(value) && TreeRangeSet.this.contains(value);
      }

      public void clear() {
         TreeRangeSet.this.remove(this.restriction);
      }

      public RangeSet subRangeSet(Range view) {
         return (RangeSet)(view.encloses(this.restriction)?this:(view.isConnected(this.restriction)?new TreeRangeSet.SubRangeSet(this.restriction.intersection(view)):ImmutableRangeSet.of()));
      }
   }

   private static final class SubRangeSetRangesByLowerBound extends AbstractNavigableMap {
      private final Range lowerBoundWindow;
      private final Range restriction;
      private final NavigableMap rangesByLowerBound;
      private final NavigableMap rangesByUpperBound;

      private SubRangeSetRangesByLowerBound(Range lowerBoundWindow, Range restriction, NavigableMap rangesByLowerBound) {
         this.lowerBoundWindow = (Range)Preconditions.checkNotNull(lowerBoundWindow);
         this.restriction = (Range)Preconditions.checkNotNull(restriction);
         this.rangesByLowerBound = (NavigableMap)Preconditions.checkNotNull(rangesByLowerBound);
         this.rangesByUpperBound = new TreeRangeSet.RangesByUpperBound(rangesByLowerBound);
      }

      private NavigableMap subMap(Range window) {
         return (NavigableMap)(!window.isConnected(this.lowerBoundWindow)?ImmutableSortedMap.of():new TreeRangeSet.SubRangeSetRangesByLowerBound(this.lowerBoundWindow.intersection(window), this.restriction, this.rangesByLowerBound));
      }

      public NavigableMap subMap(Cut fromKey, boolean fromInclusive, Cut toKey, boolean toInclusive) {
         return this.subMap(Range.range(fromKey, BoundType.forBoolean(fromInclusive), toKey, BoundType.forBoolean(toInclusive)));
      }

      public NavigableMap headMap(Cut toKey, boolean inclusive) {
         return this.subMap(Range.upTo(toKey, BoundType.forBoolean(inclusive)));
      }

      public NavigableMap tailMap(Cut fromKey, boolean inclusive) {
         return this.subMap(Range.downTo(fromKey, BoundType.forBoolean(inclusive)));
      }

      public Comparator comparator() {
         return Ordering.natural();
      }

      public boolean containsKey(@Nullable Object key) {
         return this.get(key) != null;
      }

      @Nullable
      public Range get(@Nullable Object key) {
         if(key instanceof Cut) {
            try {
               Cut<C> cut = (Cut)key;
               if(!this.lowerBoundWindow.contains(cut) || cut.compareTo(this.restriction.lowerBound) < 0 || cut.compareTo(this.restriction.upperBound) >= 0) {
                  return null;
               }

               if(cut.equals(this.restriction.lowerBound)) {
                  Range<C> candidate = (Range)Maps.valueOrNull(this.rangesByLowerBound.floorEntry(cut));
                  if(candidate != null && candidate.upperBound.compareTo(this.restriction.lowerBound) > 0) {
                     return candidate.intersection(this.restriction);
                  }
               } else {
                  Range<C> result = (Range)this.rangesByLowerBound.get(cut);
                  if(result != null) {
                     return result.intersection(this.restriction);
                  }
               }
            } catch (ClassCastException var4) {
               return null;
            }
         }

         return null;
      }

      Iterator entryIterator() {
         if(this.restriction.isEmpty()) {
            return Iterators.emptyIterator();
         } else if(this.lowerBoundWindow.upperBound.isLessThan(this.restriction.lowerBound)) {
            return Iterators.emptyIterator();
         } else {
            final Iterator<Range<C>> completeRangeItr;
            if(this.lowerBoundWindow.lowerBound.isLessThan(this.restriction.lowerBound)) {
               completeRangeItr = this.rangesByUpperBound.tailMap(this.restriction.lowerBound, false).values().iterator();
            } else {
               completeRangeItr = this.rangesByLowerBound.tailMap(this.lowerBoundWindow.lowerBound.endpoint(), this.lowerBoundWindow.lowerBoundType() == BoundType.CLOSED).values().iterator();
            }

            final Cut<Cut<C>> upperBoundOnLowerBounds = (Cut)Ordering.natural().min(this.lowerBoundWindow.upperBound, Cut.belowValue(this.restriction.upperBound));
            return new AbstractIterator() {
               protected Entry computeNext() {
                  if(!completeRangeItr.hasNext()) {
                     return (Entry)this.endOfData();
                  } else {
                     Range<C> nextRange = (Range)completeRangeItr.next();
                     if(upperBoundOnLowerBounds.isLessThan(nextRange.lowerBound)) {
                        return (Entry)this.endOfData();
                     } else {
                        nextRange = nextRange.intersection(SubRangeSetRangesByLowerBound.this.restriction);
                        return Maps.immutableEntry(nextRange.lowerBound, nextRange);
                     }
                  }
               }
            };
         }
      }

      Iterator descendingEntryIterator() {
         if(this.restriction.isEmpty()) {
            return Iterators.emptyIterator();
         } else {
            Cut<Cut<C>> upperBoundOnLowerBounds = (Cut)Ordering.natural().min(this.lowerBoundWindow.upperBound, Cut.belowValue(this.restriction.upperBound));
            final Iterator<Range<C>> completeRangeItr = this.rangesByLowerBound.headMap(upperBoundOnLowerBounds.endpoint(), upperBoundOnLowerBounds.typeAsUpperBound() == BoundType.CLOSED).descendingMap().values().iterator();
            return new AbstractIterator() {
               protected Entry computeNext() {
                  if(!completeRangeItr.hasNext()) {
                     return (Entry)this.endOfData();
                  } else {
                     Range<C> nextRange = (Range)completeRangeItr.next();
                     if(SubRangeSetRangesByLowerBound.this.restriction.lowerBound.compareTo(nextRange.upperBound) >= 0) {
                        return (Entry)this.endOfData();
                     } else {
                        nextRange = nextRange.intersection(SubRangeSetRangesByLowerBound.this.restriction);
                        return SubRangeSetRangesByLowerBound.this.lowerBoundWindow.contains(nextRange.lowerBound)?Maps.immutableEntry(nextRange.lowerBound, nextRange):(Entry)this.endOfData();
                     }
                  }
               }
            };
         }
      }

      public int size() {
         return Iterators.size(this.entryIterator());
      }
   }
}
