package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
final class ReverseOrdering extends Ordering implements Serializable {
   final Ordering forwardOrder;
   private static final long serialVersionUID = 0L;

   ReverseOrdering(Ordering forwardOrder) {
      this.forwardOrder = (Ordering)Preconditions.checkNotNull(forwardOrder);
   }

   public int compare(Object a, Object b) {
      return this.forwardOrder.compare(b, a);
   }

   public Ordering reverse() {
      return this.forwardOrder;
   }

   public Object min(Object a, Object b) {
      return this.forwardOrder.max(a, b);
   }

   public Object min(Object a, Object b, Object c, Object... rest) {
      return this.forwardOrder.max(a, b, c, rest);
   }

   public Object min(Iterator iterator) {
      return this.forwardOrder.max(iterator);
   }

   public Object min(Iterable iterable) {
      return this.forwardOrder.max(iterable);
   }

   public Object max(Object a, Object b) {
      return this.forwardOrder.min(a, b);
   }

   public Object max(Object a, Object b, Object c, Object... rest) {
      return this.forwardOrder.min(a, b, c, rest);
   }

   public Object max(Iterator iterator) {
      return this.forwardOrder.min(iterator);
   }

   public Object max(Iterable iterable) {
      return this.forwardOrder.min(iterable);
   }

   public int hashCode() {
      return -this.forwardOrder.hashCode();
   }

   public boolean equals(@Nullable Object object) {
      if(object == this) {
         return true;
      } else if(object instanceof ReverseOrdering) {
         ReverseOrdering<?> that = (ReverseOrdering)object;
         return this.forwardOrder.equals(that.forwardOrder);
      } else {
         return false;
      }
   }

   public String toString() {
      return this.forwardOrder + ".reverse()";
   }
}
