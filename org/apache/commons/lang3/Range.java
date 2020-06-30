package org.apache.commons.lang3;

import java.io.Serializable;
import java.util.Comparator;

public final class Range implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Comparator comparator;
   private final Object minimum;
   private final Object maximum;
   private transient int hashCode;
   private transient String toString;

   public static Range is(Comparable element) {
      return between(element, element, (Comparator)null);
   }

   public static Range is(Object element, Comparator comparator) {
      return between(element, element, comparator);
   }

   public static Range between(Comparable fromInclusive, Comparable toInclusive) {
      return between(fromInclusive, toInclusive, (Comparator)null);
   }

   public static Range between(Object fromInclusive, Object toInclusive, Comparator comparator) {
      return new Range(fromInclusive, toInclusive, comparator);
   }

   private Range(Object element1, Object element2, Comparator comp) {
      if(element1 != null && element2 != null) {
         if(comp == null) {
            this.comparator = Range.ComparableComparator.INSTANCE;
         } else {
            this.comparator = comp;
         }

         if(this.comparator.compare(element1, element2) < 1) {
            this.minimum = element1;
            this.maximum = element2;
         } else {
            this.minimum = element2;
            this.maximum = element1;
         }

      } else {
         throw new IllegalArgumentException("Elements in a range must not be null: element1=" + element1 + ", element2=" + element2);
      }
   }

   public Object getMinimum() {
      return this.minimum;
   }

   public Object getMaximum() {
      return this.maximum;
   }

   public Comparator getComparator() {
      return this.comparator;
   }

   public boolean isNaturalOrdering() {
      return this.comparator == Range.ComparableComparator.INSTANCE;
   }

   public boolean contains(Object element) {
      return element == null?false:this.comparator.compare(element, this.minimum) > -1 && this.comparator.compare(element, this.maximum) < 1;
   }

   public boolean isAfter(Object element) {
      return element == null?false:this.comparator.compare(element, this.minimum) < 0;
   }

   public boolean isStartedBy(Object element) {
      return element == null?false:this.comparator.compare(element, this.minimum) == 0;
   }

   public boolean isEndedBy(Object element) {
      return element == null?false:this.comparator.compare(element, this.maximum) == 0;
   }

   public boolean isBefore(Object element) {
      return element == null?false:this.comparator.compare(element, this.maximum) > 0;
   }

   public int elementCompareTo(Object element) {
      if(element == null) {
         throw new NullPointerException("Element is null");
      } else {
         return this.isAfter(element)?-1:(this.isBefore(element)?1:0);
      }
   }

   public boolean containsRange(Range otherRange) {
      return otherRange == null?false:this.contains(otherRange.minimum) && this.contains(otherRange.maximum);
   }

   public boolean isAfterRange(Range otherRange) {
      return otherRange == null?false:this.isAfter(otherRange.maximum);
   }

   public boolean isOverlappedBy(Range otherRange) {
      return otherRange == null?false:otherRange.contains(this.minimum) || otherRange.contains(this.maximum) || this.contains(otherRange.minimum);
   }

   public boolean isBeforeRange(Range otherRange) {
      return otherRange == null?false:this.isBefore(otherRange.minimum);
   }

   public Range intersectionWith(Range other) {
      if(!this.isOverlappedBy(other)) {
         throw new IllegalArgumentException(String.format("Cannot calculate intersection with non-overlapping range %s", new Object[]{other}));
      } else if(this.equals(other)) {
         return this;
      } else {
         T min = this.getComparator().compare(this.minimum, other.minimum) < 0?other.minimum:this.minimum;
         T max = this.getComparator().compare(this.maximum, other.maximum) < 0?this.maximum:other.maximum;
         return between(min, max, this.getComparator());
      }
   }

   public boolean equals(Object obj) {
      if(obj == this) {
         return true;
      } else if(obj != null && obj.getClass() == this.getClass()) {
         Range<T> range = (Range)obj;
         return this.minimum.equals(range.minimum) && this.maximum.equals(range.maximum);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.hashCode;
      if(this.hashCode == 0) {
         result = 17;
         result = 37 * result + this.getClass().hashCode();
         result = 37 * result + this.minimum.hashCode();
         result = 37 * result + this.maximum.hashCode();
         this.hashCode = result;
      }

      return result;
   }

   public String toString() {
      String result = this.toString;
      if(result == null) {
         StringBuilder buf = new StringBuilder(32);
         buf.append('[');
         buf.append(this.minimum);
         buf.append("..");
         buf.append(this.maximum);
         buf.append(']');
         result = buf.toString();
         this.toString = result;
      }

      return result;
   }

   public String toString(String format) {
      return String.format(format, new Object[]{this.minimum, this.maximum, this.comparator});
   }

   private static enum ComparableComparator implements Comparator {
      INSTANCE;

      public int compare(Object obj1, Object obj2) {
         return ((Comparable)obj1).compareTo(obj2);
      }
   }
}
