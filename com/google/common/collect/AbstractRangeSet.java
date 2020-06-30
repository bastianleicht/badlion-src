package com.google.common.collect;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import javax.annotation.Nullable;

abstract class AbstractRangeSet implements RangeSet {
   public boolean contains(Comparable value) {
      return this.rangeContaining(value) != null;
   }

   public abstract Range rangeContaining(Comparable var1);

   public boolean isEmpty() {
      return this.asRanges().isEmpty();
   }

   public void add(Range range) {
      throw new UnsupportedOperationException();
   }

   public void remove(Range range) {
      throw new UnsupportedOperationException();
   }

   public void clear() {
      this.remove(Range.all());
   }

   public boolean enclosesAll(RangeSet other) {
      for(Range<C> range : other.asRanges()) {
         if(!this.encloses(range)) {
            return false;
         }
      }

      return true;
   }

   public void addAll(RangeSet other) {
      for(Range<C> range : other.asRanges()) {
         this.add(range);
      }

   }

   public void removeAll(RangeSet other) {
      for(Range<C> range : other.asRanges()) {
         this.remove(range);
      }

   }

   public abstract boolean encloses(Range var1);

   public boolean equals(@Nullable Object obj) {
      if(obj == this) {
         return true;
      } else if(obj instanceof RangeSet) {
         RangeSet<?> other = (RangeSet)obj;
         return this.asRanges().equals(other.asRanges());
      } else {
         return false;
      }
   }

   public final int hashCode() {
      return this.asRanges().hashCode();
   }

   public final String toString() {
      return this.asRanges().toString();
   }
}
