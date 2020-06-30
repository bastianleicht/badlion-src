package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.BoundType;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.Cut;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public final class Range implements Predicate, Serializable {
   private static final Function LOWER_BOUND_FN = new Function() {
      public Cut apply(Range range) {
         return range.lowerBound;
      }
   };
   private static final Function UPPER_BOUND_FN = new Function() {
      public Cut apply(Range range) {
         return range.upperBound;
      }
   };
   static final Ordering RANGE_LEX_ORDERING = new Ordering() {
      public int compare(Range left, Range right) {
         return ComparisonChain.start().compare(left.lowerBound, right.lowerBound).compare(left.upperBound, right.upperBound).result();
      }
   };
   private static final Range ALL = new Range(Cut.belowAll(), Cut.aboveAll());
   final Cut lowerBound;
   final Cut upperBound;
   private static final long serialVersionUID = 0L;

   static Function lowerBoundFn() {
      return LOWER_BOUND_FN;
   }

   static Function upperBoundFn() {
      return UPPER_BOUND_FN;
   }

   static Range create(Cut lowerBound, Cut upperBound) {
      return new Range(lowerBound, upperBound);
   }

   public static Range open(Comparable lower, Comparable upper) {
      return create(Cut.aboveValue(lower), Cut.belowValue(upper));
   }

   public static Range closed(Comparable lower, Comparable upper) {
      return create(Cut.belowValue(lower), Cut.aboveValue(upper));
   }

   public static Range closedOpen(Comparable lower, Comparable upper) {
      return create(Cut.belowValue(lower), Cut.belowValue(upper));
   }

   public static Range openClosed(Comparable lower, Comparable upper) {
      return create(Cut.aboveValue(lower), Cut.aboveValue(upper));
   }

   public static Range range(Comparable lower, BoundType lowerType, Comparable upper, BoundType upperType) {
      Preconditions.checkNotNull(lowerType);
      Preconditions.checkNotNull(upperType);
      Cut<C> lowerBound = lowerType == BoundType.OPEN?Cut.aboveValue(lower):Cut.belowValue(lower);
      Cut<C> upperBound = upperType == BoundType.OPEN?Cut.belowValue(upper):Cut.aboveValue(upper);
      return create(lowerBound, upperBound);
   }

   public static Range lessThan(Comparable endpoint) {
      return create(Cut.belowAll(), Cut.belowValue(endpoint));
   }

   public static Range atMost(Comparable endpoint) {
      return create(Cut.belowAll(), Cut.aboveValue(endpoint));
   }

   public static Range upTo(Comparable endpoint, BoundType boundType) {
      switch(boundType) {
      case OPEN:
         return lessThan(endpoint);
      case CLOSED:
         return atMost(endpoint);
      default:
         throw new AssertionError();
      }
   }

   public static Range greaterThan(Comparable endpoint) {
      return create(Cut.aboveValue(endpoint), Cut.aboveAll());
   }

   public static Range atLeast(Comparable endpoint) {
      return create(Cut.belowValue(endpoint), Cut.aboveAll());
   }

   public static Range downTo(Comparable endpoint, BoundType boundType) {
      switch(boundType) {
      case OPEN:
         return greaterThan(endpoint);
      case CLOSED:
         return atLeast(endpoint);
      default:
         throw new AssertionError();
      }
   }

   public static Range all() {
      return ALL;
   }

   public static Range singleton(Comparable value) {
      return closed(value, value);
   }

   public static Range encloseAll(Iterable values) {
      Preconditions.checkNotNull(values);
      if(values instanceof ContiguousSet) {
         return ((ContiguousSet)values).range();
      } else {
         Iterator<C> valueIterator = values.iterator();
         C min = (Comparable)Preconditions.checkNotNull(valueIterator.next());

         C max;
         C value;
         for(max = min; valueIterator.hasNext(); max = (Comparable)Ordering.natural().max(max, value)) {
            value = (Comparable)Preconditions.checkNotNull(valueIterator.next());
            min = (Comparable)Ordering.natural().min(min, value);
         }

         return closed(min, max);
      }
   }

   private Range(Cut lowerBound, Cut upperBound) {
      if(lowerBound.compareTo(upperBound) <= 0 && lowerBound != Cut.aboveAll() && upperBound != Cut.belowAll()) {
         this.lowerBound = (Cut)Preconditions.checkNotNull(lowerBound);
         this.upperBound = (Cut)Preconditions.checkNotNull(upperBound);
      } else {
         throw new IllegalArgumentException("Invalid range: " + toString(lowerBound, upperBound));
      }
   }

   public boolean hasLowerBound() {
      return this.lowerBound != Cut.belowAll();
   }

   public Comparable lowerEndpoint() {
      return this.lowerBound.endpoint();
   }

   public BoundType lowerBoundType() {
      return this.lowerBound.typeAsLowerBound();
   }

   public boolean hasUpperBound() {
      return this.upperBound != Cut.aboveAll();
   }

   public Comparable upperEndpoint() {
      return this.upperBound.endpoint();
   }

   public BoundType upperBoundType() {
      return this.upperBound.typeAsUpperBound();
   }

   public boolean isEmpty() {
      return this.lowerBound.equals(this.upperBound);
   }

   public boolean contains(Comparable value) {
      Preconditions.checkNotNull(value);
      return this.lowerBound.isLessThan(value) && !this.upperBound.isLessThan(value);
   }

   /** @deprecated */
   @Deprecated
   public boolean apply(Comparable input) {
      return this.contains(input);
   }

   public boolean containsAll(Iterable values) {
      if(Iterables.isEmpty(values)) {
         return true;
      } else {
         if(values instanceof SortedSet) {
            SortedSet<? extends C> set = cast(values);
            Comparator<?> comparator = set.comparator();
            if(Ordering.natural().equals(comparator) || comparator == null) {
               return this.contains((Comparable)set.first()) && this.contains((Comparable)set.last());
            }
         }

         for(C value : values) {
            if(!this.contains(value)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean encloses(Range other) {
      return this.lowerBound.compareTo(other.lowerBound) <= 0 && this.upperBound.compareTo(other.upperBound) >= 0;
   }

   public boolean isConnected(Range other) {
      return this.lowerBound.compareTo(other.upperBound) <= 0 && other.lowerBound.compareTo(this.upperBound) <= 0;
   }

   public Range intersection(Range connectedRange) {
      int lowerCmp = this.lowerBound.compareTo(connectedRange.lowerBound);
      int upperCmp = this.upperBound.compareTo(connectedRange.upperBound);
      if(lowerCmp >= 0 && upperCmp <= 0) {
         return this;
      } else if(lowerCmp <= 0 && upperCmp >= 0) {
         return connectedRange;
      } else {
         Cut<C> newLower = lowerCmp >= 0?this.lowerBound:connectedRange.lowerBound;
         Cut<C> newUpper = upperCmp <= 0?this.upperBound:connectedRange.upperBound;
         return create(newLower, newUpper);
      }
   }

   public Range span(Range other) {
      int lowerCmp = this.lowerBound.compareTo(other.lowerBound);
      int upperCmp = this.upperBound.compareTo(other.upperBound);
      if(lowerCmp <= 0 && upperCmp >= 0) {
         return this;
      } else if(lowerCmp >= 0 && upperCmp <= 0) {
         return other;
      } else {
         Cut<C> newLower = lowerCmp <= 0?this.lowerBound:other.lowerBound;
         Cut<C> newUpper = upperCmp >= 0?this.upperBound:other.upperBound;
         return create(newLower, newUpper);
      }
   }

   public Range canonical(DiscreteDomain domain) {
      Preconditions.checkNotNull(domain);
      Cut<C> lower = this.lowerBound.canonical(domain);
      Cut<C> upper = this.upperBound.canonical(domain);
      return lower == this.lowerBound && upper == this.upperBound?this:create(lower, upper);
   }

   public boolean equals(@Nullable Object object) {
      if(!(object instanceof Range)) {
         return false;
      } else {
         Range<?> other = (Range)object;
         return this.lowerBound.equals(other.lowerBound) && this.upperBound.equals(other.upperBound);
      }
   }

   public int hashCode() {
      return this.lowerBound.hashCode() * 31 + this.upperBound.hashCode();
   }

   public String toString() {
      return toString(this.lowerBound, this.upperBound);
   }

   private static String toString(Cut lowerBound, Cut upperBound) {
      StringBuilder sb = new StringBuilder(16);
      lowerBound.describeAsLowerBound(sb);
      sb.append('‥');
      upperBound.describeAsUpperBound(sb);
      return sb.toString();
   }

   private static SortedSet cast(Iterable iterable) {
      return (SortedSet)iterable;
   }

   Object readResolve() {
      return this.equals(ALL)?all():this;
   }

   static int compareOrThrow(Comparable left, Comparable right) {
      return left.compareTo(right);
   }
}
