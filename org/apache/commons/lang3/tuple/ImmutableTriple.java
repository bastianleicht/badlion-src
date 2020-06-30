package org.apache.commons.lang3.tuple;

import org.apache.commons.lang3.tuple.Triple;

public final class ImmutableTriple extends Triple {
   private static final long serialVersionUID = 1L;
   public final Object left;
   public final Object middle;
   public final Object right;

   public static ImmutableTriple of(Object left, Object middle, Object right) {
      return new ImmutableTriple(left, middle, right);
   }

   public ImmutableTriple(Object left, Object middle, Object right) {
      this.left = left;
      this.middle = middle;
      this.right = right;
   }

   public Object getLeft() {
      return this.left;
   }

   public Object getMiddle() {
      return this.middle;
   }

   public Object getRight() {
      return this.right;
   }
}
