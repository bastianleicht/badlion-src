package org.apache.commons.lang3.tuple;

import java.io.Serializable;
import java.util.Map.Entry;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;

public abstract class Pair implements Entry, Comparable, Serializable {
   private static final long serialVersionUID = 4954918890077093841L;

   public static Pair of(Object left, Object right) {
      return new ImmutablePair(left, right);
   }

   public abstract Object getLeft();

   public abstract Object getRight();

   public final Object getKey() {
      return this.getLeft();
   }

   public Object getValue() {
      return this.getRight();
   }

   public int compareTo(Pair other) {
      return (new CompareToBuilder()).append(this.getLeft(), other.getLeft()).append(this.getRight(), other.getRight()).toComparison();
   }

   public boolean equals(Object obj) {
      if(obj == this) {
         return true;
      } else if(!(obj instanceof Entry)) {
         return false;
      } else {
         Entry<?, ?> other = (Entry)obj;
         return ObjectUtils.equals(this.getKey(), other.getKey()) && ObjectUtils.equals(this.getValue(), other.getValue());
      }
   }

   public int hashCode() {
      return (this.getKey() == null?0:this.getKey().hashCode()) ^ (this.getValue() == null?0:this.getValue().hashCode());
   }

   public String toString() {
      return "" + '(' + this.getLeft() + ',' + this.getRight() + ')';
   }

   public String toString(String format) {
      return String.format(format, new Object[]{this.getLeft(), this.getRight()});
   }
}
