package com.google.common.collect;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSortedMultiset;

abstract class ImmutableSortedMultisetFauxverideShim extends ImmutableMultiset {
   /** @deprecated */
   @Deprecated
   public static ImmutableSortedMultiset.Builder builder() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedMultiset of(Object element) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedMultiset of(Object e1, Object e2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedMultiset of(Object e1, Object e2, Object e3) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedMultiset of(Object e1, Object e2, Object e3, Object e4) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedMultiset of(Object e1, Object e2, Object e3, Object e4, Object e5) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedMultiset of(Object e1, Object e2, Object e3, Object e4, Object e5, Object e6, Object... remaining) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedMultiset copyOf(Object[] elements) {
      throw new UnsupportedOperationException();
   }
}
