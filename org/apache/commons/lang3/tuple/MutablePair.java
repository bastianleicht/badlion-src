package org.apache.commons.lang3.tuple;

import org.apache.commons.lang3.tuple.Pair;

public class MutablePair extends Pair {
   private static final long serialVersionUID = 4954918890077093841L;
   public Object left;
   public Object right;

   public static MutablePair of(Object left, Object right) {
      return new MutablePair(left, right);
   }

   public MutablePair() {
   }

   public MutablePair(Object left, Object right) {
      this.left = left;
      this.right = right;
   }

   public Object getLeft() {
      return this.left;
   }

   public void setLeft(Object left) {
      this.left = left;
   }

   public Object getRight() {
      return this.right;
   }

   public void setRight(Object right) {
      this.right = right;
   }

   public Object setValue(Object value) {
      R result = this.getRight();
      this.setRight(value);
      return result;
   }
}
