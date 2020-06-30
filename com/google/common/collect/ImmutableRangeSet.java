package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.AbstractRangeSet;
import com.google.common.collect.BoundType;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.Cut;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.RegularImmutableSortedSet;
import com.google.common.collect.SortedLists;
import com.google.common.collect.TreeRangeSet;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.primitives.Ints;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

@Beta
public final class ImmutableRangeSet extends AbstractRangeSet implements Serializable {
   private static final ImmutableRangeSet EMPTY = new ImmutableRangeSet(ImmutableList.of());
   private static final ImmutableRangeSet ALL = new ImmutableRangeSet(ImmutableList.of(Range.all()));
   private final transient ImmutableList ranges;
   private transient ImmutableRangeSet complement;

   public static ImmutableRangeSet of() {
      return EMPTY;
   }

   static ImmutableRangeSet all() {
      return ALL;
   }

   public static ImmutableRangeSet of(Range range) {
      Preconditions.checkNotNull(range);
      return range.isEmpty()?of():(range.equals(Range.all())?all():new ImmutableRangeSet(ImmutableList.of(range)));
   }

   public static ImmutableRangeSet copyOf(RangeSet rangeSet) {
      Preconditions.checkNotNull(rangeSet);
      if(rangeSet.isEmpty()) {
         return of();
      } else if(rangeSet.encloses(Range.all())) {
         return all();
      } else {
         if(rangeSet instanceof ImmutableRangeSet) {
            ImmutableRangeSet<C> immutableRangeSet = (ImmutableRangeSet)rangeSet;
            if(!immutableRangeSet.isPartialView()) {
               return immutableRangeSet;
            }
         }

         return new ImmutableRangeSet(ImmutableList.copyOf((Collection)rangeSet.asRanges()));
      }
   }

   ImmutableRangeSet(ImmutableList ranges) {
      this.ranges = ranges;
   }

   private ImmutableRangeSet(ImmutableList ranges, ImmutableRangeSet complement) {
      this.ranges = ranges;
      this.complement = complement;
   }

   public boolean encloses(Range otherRange) {
      int index = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), otherRange.lowerBound, Ordering.natural(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
      return index != -1 && ((Range)this.ranges.get(index)).encloses(otherRange);
   }

   public Range rangeContaining(Comparable value) {
      int index = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), Cut.belowValue(value), Ordering.natural(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
      if(index != -1) {
         Range<C> range = (Range)this.ranges.get(index);
         return range.contains(value)?range:null;
      } else {
         return null;
      }
   }

   public Range span() {
      if(this.ranges.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return Range.create(((Range)this.ranges.get(0)).lowerBound, ((Range)this.ranges.get(this.ranges.size() - 1)).upperBound);
      }
   }

   public boolean isEmpty() {
      return this.ranges.isEmpty();
   }

   public void add(Range range) {
      throw new UnsupportedOperationException();
   }

   public void addAll(RangeSet other) {
      throw new UnsupportedOperationException();
   }

   public void remove(Range range) {
      throw new UnsupportedOperationException();
   }

   public void removeAll(RangeSet other) {
      throw new UnsupportedOperationException();
   }

   public ImmutableSet asRanges() {
      return (ImmutableSet)(this.ranges.isEmpty()?ImmutableSet.of():new RegularImmutableSortedSet(this.ranges, Range.RANGE_LEX_ORDERING));
   }

   public ImmutableRangeSet complement() {
      ImmutableRangeSet<C> result = this.complement;
      if(result != null) {
         return result;
      } else if(this.ranges.isEmpty()) {
         return this.complement = all();
      } else if(this.ranges.size() == 1 && ((Range)this.ranges.get(0)).equals(Range.all())) {
         return this.complement = of();
      } else {
         ImmutableList<Range<C>> complementRanges = new ImmutableRangeSet.ComplementRanges();
         result = this.complement = new ImmutableRangeSet(complementRanges, this);
         return result;
      }
   }

   private ImmutableList intersectRanges(final Range range) {
      if(!this.ranges.isEmpty() && !range.isEmpty()) {
         if(range.encloses(this.span())) {
            return this.ranges;
         } else {
            final int fromIndex;
            if(range.hasLowerBound()) {
               fromIndex = SortedLists.binarySearch(this.ranges, (Function)Range.upperBoundFn(), (Comparable)range.lowerBound, SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
            } else {
               fromIndex = 0;
            }

            int toIndex;
            if(range.hasUpperBound()) {
               toIndex = SortedLists.binarySearch(this.ranges, (Function)Range.lowerBoundFn(), (Comparable)range.upperBound, SortedLists.KeyPresentBehavior.FIRST_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
            } else {
               toIndex = this.ranges.size();
            }

            final int length = toIndex - fromIndex;
            return length == 0?ImmutableList.of():new ImmutableList() {
               public int size() {
                  return length;
               }

               public Range get(int index) {
                  Preconditions.checkElementIndex(index, length);
                  return index != 0 && index != length - 1?(Range)ImmutableRangeSet.this.ranges.get(index + fromIndex):((Range)ImmutableRangeSet.this.ranges.get(index + fromIndex)).intersection(range);
               }

               boolean isPartialView() {
                  return true;
               }
            };
         }
      } else {
         return ImmutableList.of();
      }
   }

   public ImmutableRangeSet subRangeSet(Range range) {
      if(!this.isEmpty()) {
         Range<C> span = this.span();
         if(range.encloses(span)) {
            return this;
         }

         if(range.isConnected(span)) {
            return new ImmutableRangeSet(this.intersectRanges(range));
         }
      }

      return of();
   }

   public ImmutableSortedSet asSet(DiscreteDomain domain) {
      Preconditions.checkNotNull(domain);
      if(this.isEmpty()) {
         return ImmutableSortedSet.of();
      } else {
         Range<C> span = this.span().canonical(domain);
         if(!span.hasLowerBound()) {
            throw new IllegalArgumentException("Neither the DiscreteDomain nor this range set are bounded below");
         } else {
            if(!span.hasUpperBound()) {
               try {
                  domain.maxValue();
               } catch (NoSuchElementException var4) {
                  throw new IllegalArgumentException("Neither the DiscreteDomain nor this range set are bounded above");
               }
            }

            return new ImmutableRangeSet.AsSet(domain);
         }
      }
   }

   boolean isPartialView() {
      return this.ranges.isPartialView();
   }

   public static ImmutableRangeSet.Builder builder() {
      return new ImmutableRangeSet.Builder();
   }

   Object writeReplace() {
      return new ImmutableRangeSet.SerializedForm(this.ranges);
   }

   private final class AsSet extends ImmutableSortedSet {
      private final DiscreteDomain domain;
      private transient Integer size;

      AsSet(DiscreteDomain domain) {
         super(Ordering.natural());
         this.domain = domain;
      }

      public int size() {
         Integer result = this.size;
         if(result == null) {
            long total = 0L;

            for(Range<C> range : ImmutableRangeSet.this.ranges) {
               total += (long)ContiguousSet.create(range, this.domain).size();
               if(total >= 2147483647L) {
                  break;
               }
            }

            result = this.size = Integer.valueOf(Ints.saturatedCast(total));
         }

         return result.intValue();
      }

      public UnmodifiableIterator iterator() {
         return new AbstractIterator() {
            final Iterator rangeItr;
            Iterator elemItr;

            {
               this.rangeItr = ImmutableRangeSet.this.ranges.iterator();
               this.elemItr = Iterators.emptyIterator();
            }

            protected Comparable computeNext() {
               while(true) {
                  if(!this.elemItr.hasNext()) {
                     if(this.rangeItr.hasNext()) {
                        this.elemItr = ContiguousSet.create((Range)this.rangeItr.next(), AsSet.this.domain).iterator();
                        continue;
                     }

                     return (Comparable)this.endOfData();
                  }

                  return (Comparable)this.elemItr.next();
               }
            }
         };
      }

      @GwtIncompatible("NavigableSet")
      public UnmodifiableIterator descendingIterator() {
         return new AbstractIterator() {
            final Iterator rangeItr;
            Iterator elemItr;

            {
               this.rangeItr = ImmutableRangeSet.this.ranges.reverse().iterator();
               this.elemItr = Iterators.emptyIterator();
            }

            protected Comparable computeNext() {
               while(true) {
                  if(!this.elemItr.hasNext()) {
                     if(this.rangeItr.hasNext()) {
                        this.elemItr = ContiguousSet.create((Range)this.rangeItr.next(), AsSet.this.domain).descendingIterator();
                        continue;
                     }

                     return (Comparable)this.endOfData();
                  }

                  return (Comparable)this.elemItr.next();
               }
            }
         };
      }

      ImmutableSortedSet subSet(Range range) {
         return ImmutableRangeSet.this.subRangeSet(range).asSet(this.domain);
      }

      ImmutableSortedSet headSetImpl(Comparable toElement, boolean inclusive) {
         return this.subSet(Range.upTo(toElement, BoundType.forBoolean(inclusive)));
      }

      ImmutableSortedSet subSetImpl(Comparable fromElement, boolean fromInclusive, Comparable toElement, boolean toInclusive) {
         return !fromInclusive && !toInclusive && Range.compareOrThrow(fromElement, toElement) == 0?ImmutableSortedSet.of():this.subSet(Range.range(fromElement, BoundType.forBoolean(fromInclusive), toElement, BoundType.forBoolean(toInclusive)));
      }

      ImmutableSortedSet tailSetImpl(Comparable fromElement, boolean inclusive) {
         return this.subSet(Range.downTo(fromElement, BoundType.forBoolean(inclusive)));
      }

      public boolean contains(@Nullable Object o) {
         if(o == null) {
            return false;
         } else {
            try {
               C c = (Comparable)o;
               return ImmutableRangeSet.this.contains(c);
            } catch (ClassCastException var3) {
               return false;
            }
         }
      }

      int indexOf(Object target) {
         if(this.contains(target)) {
            C c = (Comparable)target;
            long total = 0L;

            for(Range<C> range : ImmutableRangeSet.this.ranges) {
               if(range.contains(c)) {
                  return Ints.saturatedCast(total + (long)ContiguousSet.create(range, this.domain).indexOf(c));
               }

               total += (long)ContiguousSet.create(range, this.domain).size();
            }

            throw new AssertionError("impossible");
         } else {
            return -1;
         }
      }

      boolean isPartialView() {
         return ImmutableRangeSet.this.ranges.isPartialView();
      }

      public String toString() {
         return ImmutableRangeSet.this.ranges.toString();
      }

      Object writeReplace() {
         return new ImmutableRangeSet.AsSetSerializedForm(ImmutableRangeSet.this.ranges, this.domain);
      }
   }

   private static class AsSetSerializedForm implements Serializable {
      private final ImmutableList ranges;
      private final DiscreteDomain domain;

      AsSetSerializedForm(ImmutableList ranges, DiscreteDomain domain) {
         this.ranges = ranges;
         this.domain = domain;
      }

      Object readResolve() {
         return (new ImmutableRangeSet(this.ranges)).asSet(this.domain);
      }
   }

   public static class Builder {
      private final RangeSet rangeSet = TreeRangeSet.create();

      public ImmutableRangeSet.Builder add(Range range) {
         if(range.isEmpty()) {
            throw new IllegalArgumentException("range must not be empty, but was " + range);
         } else if(this.rangeSet.complement().encloses(range)) {
            this.rangeSet.add(range);
            return this;
         } else {
            for(Range<C> currentRange : this.rangeSet.asRanges()) {
               Preconditions.checkArgument(!currentRange.isConnected(range) || currentRange.intersection(range).isEmpty(), "Ranges may not overlap, but received %s and %s", new Object[]{currentRange, range});
            }

            throw new AssertionError("should have thrown an IAE above");
         }
      }

      public ImmutableRangeSet.Builder addAll(RangeSet ranges) {
         for(Range<C> range : ranges.asRanges()) {
            this.add(range);
         }

         return this;
      }

      public ImmutableRangeSet build() {
         return ImmutableRangeSet.copyOf(this.rangeSet);
      }
   }

   private final class ComplementRanges extends ImmutableList {
      private final boolean positiveBoundedBelow;
      private final boolean positiveBoundedAbove;
      private final int size;

      ComplementRanges() {
         this.positiveBoundedBelow = ((Range)ImmutableRangeSet.this.ranges.get(0)).hasLowerBound();
         this.positiveBoundedAbove = ((Range)Iterables.getLast(ImmutableRangeSet.this.ranges)).hasUpperBound();
         int size = ImmutableRangeSet.this.ranges.size() - 1;
         if(this.positiveBoundedBelow) {
            ++size;
         }

         if(this.positiveBoundedAbove) {
            ++size;
         }

         this.size = size;
      }

      public int size() {
         return this.size;
      }

      public Range get(int index) {
         Preconditions.checkElementIndex(index, this.size);
         Cut<C> lowerBound;
         if(this.positiveBoundedBelow) {
            lowerBound = index == 0?Cut.belowAll():((Range)ImmutableRangeSet.this.ranges.get(index - 1)).upperBound;
         } else {
            lowerBound = ((Range)ImmutableRangeSet.this.ranges.get(index)).upperBound;
         }

         Cut<C> upperBound;
         if(this.positiveBoundedAbove && index == this.size - 1) {
            upperBound = Cut.aboveAll();
         } else {
            upperBound = ((Range)ImmutableRangeSet.this.ranges.get(index + (this.positiveBoundedBelow?0:1))).lowerBound;
         }

         return Range.create(lowerBound, upperBound);
      }

      boolean isPartialView() {
         return true;
      }
   }

   private static final class SerializedForm implements Serializable {
      private final ImmutableList ranges;

      SerializedForm(ImmutableList ranges) {
         this.ranges = ranges;
      }

      Object readResolve() {
         return this.ranges.isEmpty()?ImmutableRangeSet.EMPTY:(this.ranges.equals(ImmutableList.of(Range.all()))?ImmutableRangeSet.ALL:new ImmutableRangeSet(this.ranges));
      }
   }
}
