package org.apache.commons.lang3.tuple;

import org.apache.commons.lang3.tuple.Pair;

public final class ImmutablePair extends Pair {
   private static final long serialVersionUID = 4954918890077093841L;
   public final Object left;
   public final Object right;

   public static ImmutablePair of(Object left, Object right) {
      return new ImmutablePair(left, right);
   }

   public ImmutablePair(Object left, Object right) {
      this.left = left;
      this.right = right;
   }

   public Object getLeft() {
      return this.left;
   }

   public Object getRight() {
      return this.right;
   }

   public Object setValue(Object value) {
      throw new UnsupportedOperationException();
   }
}
