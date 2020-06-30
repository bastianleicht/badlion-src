package org.apache.commons.lang3.tuple;

import org.apache.commons.lang3.tuple.Triple;

public class MutableTriple extends Triple {
   private static final long serialVersionUID = 1L;
   public Object left;
   public Object middle;
   public Object right;

   public static MutableTriple of(Object left, Object middle, Object right) {
      return new MutableTriple(left, middle, right);
   }

   public MutableTriple() {
   }

   public MutableTriple(Object left, Object middle, Object right) {
      this.left = left;
      this.middle = middle;
      this.right = right;
   }

   public Object getLeft() {
      return this.left;
   }

   public void setLeft(Object left) {
      this.left = left;
   }

   public Object getMiddle() {
      return this.middle;
   }

   public void setMiddle(Object middle) {
      this.middle = middle;
   }

   public Object getRight() {
      return this.right;
   }

   public void setRight(Object right) {
      this.right = right;
   }
}
