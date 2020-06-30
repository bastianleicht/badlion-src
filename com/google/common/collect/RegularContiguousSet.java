package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.BoundType;
import com.google.common.collect.Collections2;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.EmptyContiguousSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.util.Collection;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class RegularContiguousSet extends ContiguousSet {
   private final Range range;
   private static final long serialVersionUID = 0L;

   RegularContiguousSet(Range range, DiscreteDomain domain) {
      super(domain);
      this.range = range;
   }

   private ContiguousSet intersectionInCurrentDomain(Range other) {
      return (ContiguousSet)(this.range.isConnected(other)?ContiguousSet.create(this.range.intersection(other), this.domain):new EmptyContiguousSet(this.domain));
   }

   ContiguousSet headSetImpl(Comparable toElement, boolean inclusive) {
      return this.intersectionInCurrentDomain(Range.upTo(toElement, BoundType.forBoolean(inclusive)));
   }

   ContiguousSet subSetImpl(Comparable fromElement, boolean fromInclusive, Comparable toElement, boolean toInclusive) {
      return (ContiguousSet)(fromElement.compareTo(toElement) == 0 && !fromInclusive && !toInclusive?new EmptyContiguousSet(this.domain):this.intersectionInCurrentDomain(Range.range(fromElement, BoundType.forBoolean(fromInclusive), toElement, BoundType.forBoolean(toInclusive))));
   }

   ContiguousSet tailSetImpl(Comparable fromElement, boolean inclusive) {
      return this.intersectionInCurrentDomain(Range.downTo(fromElement, BoundType.forBoolean(inclusive)));
   }

   @GwtIncompatible("not used by GWT emulation")
   int indexOf(Object target) {
      return this.contains(target)?(int)this.domain.distance(this.first(), (Comparable)target):-1;
   }

   public UnmodifiableIterator iterator() {
      return new AbstractSequentialIterator(this.first()) {
         final Comparable last = RegularContiguousSet.this.last();

         protected Comparable computeNext(Comparable previous) {
            return RegularContiguousSet.equalsOrThrow(previous, this.last)?null:RegularContiguousSet.this.domain.next(previous);
         }
      };
   }

   @GwtIncompatible("NavigableSet")
   public UnmodifiableIterator descendingIterator() {
      return new AbstractSequentialIterator(this.last()) {
         final Comparable first = RegularContiguousSet.this.first();

         protected Comparable computeNext(Comparable previous) {
            return RegularContiguousSet.equalsOrThrow(previous, this.first)?null:RegularContiguousSet.this.domain.previous(previous);
         }
      };
   }

   private static boolean equalsOrThrow(Comparable left, @Nullable Comparable right) {
      return right != null && Range.compareOrThrow(left, right) == 0;
   }

   boolean isPartialView() {
      return false;
   }

   public Comparable first() {
      return this.range.lowerBound.leastValueAbove(this.domain);
   }

   public Comparable last() {
      return this.range.upperBound.greatestValueBelow(this.domain);
   }

   public int size() {
      long distance = this.domain.distance(this.first(), this.last());
      return distance >= 2147483647L?Integer.MAX_VALUE:(int)distance + 1;
   }

   public boolean contains(@Nullable Object object) {
      if(object == null) {
         return false;
      } else {
         try {
            return this.range.contains((Comparable)object);
         } catch (ClassCastException var3) {
            return false;
         }
      }
   }

   public boolean containsAll(Collection targets) {
      return Collections2.containsAllImpl(this, targets);
   }

   public boolean isEmpty() {
      return false;
   }

   public ContiguousSet intersection(ContiguousSet other) {
      Preconditions.checkNotNull(other);
      Preconditions.checkArgument(this.domain.equals(other.domain));
      if(other.isEmpty()) {
         return other;
      } else {
         C lowerEndpoint = (Comparable)Ordering.natural().max(this.first(), other.first());
         C upperEndpoint = (Comparable)Ordering.natural().min(this.last(), other.last());
         return (ContiguousSet)(lowerEndpoint.compareTo(upperEndpoint) < 0?ContiguousSet.create(Range.closed(lowerEndpoint, upperEndpoint), this.domain):new EmptyContiguousSet(this.domain));
      }
   }

   public Range range() {
      return this.range(BoundType.CLOSED, BoundType.CLOSED);
   }

   public Range range(BoundType lowerBoundType, BoundType upperBoundType) {
      return Range.create(this.range.lowerBound.withLowerBoundType(lowerBoundType, this.domain), this.range.upperBound.withUpperBoundType(upperBoundType, this.domain));
   }

   public boolean equals(@Nullable Object object) {
      if(object == this) {
         return true;
      } else {
         if(object instanceof RegularContiguousSet) {
            RegularContiguousSet<?> that = (RegularContiguousSet)object;
            if(this.domain.equals(that.domain)) {
               return this.first().equals(that.first()) && this.last().equals(that.last());
            }
         }

         return super.equals(object);
      }
   }

   public int hashCode() {
      return Sets.hashCodeImpl(this);
   }

   @GwtIncompatible("serialization")
   Object writeReplace() {
      return new RegularContiguousSet.SerializedForm(this.range, this.domain);
   }

   @GwtIncompatible("serialization")
   private static final class SerializedForm implements Serializable {
      final Range range;
      final DiscreteDomain domain;

      private SerializedForm(Range range, DiscreteDomain domain) {
         this.range = range;
         this.domain = domain;
      }

      private Object readResolve() {
         return new RegularContiguousSet(this.range, this.domain);
      }
   }
}
