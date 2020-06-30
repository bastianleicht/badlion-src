package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.NaturalOrdering;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.util.Iterator;

@GwtCompatible(
   serializable = true
)
final class ReverseNaturalOrdering extends Ordering implements Serializable {
   static final ReverseNaturalOrdering INSTANCE = new ReverseNaturalOrdering();
   private static final long serialVersionUID = 0L;

   public int compare(Comparable left, Comparable right) {
      Preconditions.checkNotNull(left);
      return left == right?0:right.compareTo(left);
   }

   public Ordering reverse() {
      return Ordering.natural();
   }

   public Comparable min(Comparable a, Comparable b) {
      return (Comparable)NaturalOrdering.INSTANCE.max(a, b);
   }

   public Comparable min(Comparable a, Comparable b, Comparable c, Comparable... rest) {
      return (Comparable)NaturalOrdering.INSTANCE.max(a, b, c, rest);
   }

   public Comparable min(Iterator iterator) {
      return (Comparable)NaturalOrdering.INSTANCE.max(iterator);
   }

   public Comparable min(Iterable iterable) {
      return (Comparable)NaturalOrdering.INSTANCE.max(iterable);
   }

   public Comparable max(Comparable a, Comparable b) {
      return (Comparable)NaturalOrdering.INSTANCE.min(a, b);
   }

   public Comparable max(Comparable a, Comparable b, Comparable c, Comparable... rest) {
      return (Comparable)NaturalOrdering.INSTANCE.min(a, b, c, rest);
   }

   public Comparable max(Iterator iterator) {
      return (Comparable)NaturalOrdering.INSTANCE.min(iterator);
   }

   public Comparable max(Iterable iterable) {
      return (Comparable)NaturalOrdering.INSTANCE.min(iterable);
   }

   private Object readResolve() {
      return INSTANCE;
   }

   public String toString() {
      return "Ordering.natural().reverse()";
   }
}
