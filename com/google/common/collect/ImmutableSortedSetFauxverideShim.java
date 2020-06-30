package com.google.common.collect;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

abstract class ImmutableSortedSetFauxverideShim extends ImmutableSet {
   /** @deprecated */
   @Deprecated
   public static ImmutableSortedSet.Builder builder() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedSet of(Object element) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedSet of(Object e1, Object e2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedSet of(Object e1, Object e2, Object e3) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedSet of(Object e1, Object e2, Object e3, Object e4) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedSet of(Object e1, Object e2, Object e3, Object e4, Object e5) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedSet of(Object e1, Object e2, Object e3, Object e4, Object e5, Object e6, Object... remaining) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedSet copyOf(Object[] elements) {
      throw new UnsupportedOperationException();
   }
}
