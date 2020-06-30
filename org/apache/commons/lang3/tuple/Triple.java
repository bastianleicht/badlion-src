package org.apache.commons.lang3.tuple;

import java.io.Serializable;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.tuple.ImmutableTriple;

public abstract class Triple implements Comparable, Serializable {
   private static final long serialVersionUID = 1L;

   public static Triple of(Object left, Object middle, Object right) {
      return new ImmutableTriple(left, middle, right);
   }

   public abstract Object getLeft();

   public abstract Object getMiddle();

   public abstract Object getRight();

   public int compareTo(Triple other) {
      return (new CompareToBuilder()).append(this.getLeft(), other.getLeft()).append(this.getMiddle(), other.getMiddle()).append(this.getRight(), other.getRight()).toComparison();
   }

   public boolean equals(Object obj) {
      if(obj == this) {
         return true;
      } else if(!(obj instanceof Triple)) {
         return false;
      } else {
         Triple<?, ?, ?> other = (Triple)obj;
         return ObjectUtils.equals(this.getLeft(), other.getLeft()) && ObjectUtils.equals(this.getMiddle(), other.getMiddle()) && ObjectUtils.equals(this.getRight(), other.getRight());
      }
   }

   public int hashCode() {
      return (this.getLeft() == null?0:this.getLeft().hashCode()) ^ (this.getMiddle() == null?0:this.getMiddle().hashCode()) ^ (this.getRight() == null?0:this.getRight().hashCode());
   }

   public String toString() {
      return "" + '(' + this.getLeft() + ',' + this.getMiddle() + ',' + this.getRight() + ')';
   }

   public String toString(String format) {
      return String.format(format, new Object[]{this.getLeft(), this.getMiddle(), this.getRight()});
   }
}
