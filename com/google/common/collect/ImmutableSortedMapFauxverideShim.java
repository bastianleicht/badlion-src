package com.google.common.collect;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

abstract class ImmutableSortedMapFauxverideShim extends ImmutableMap {
   /** @deprecated */
   @Deprecated
   public static ImmutableSortedMap.Builder builder() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedMap of(Object k1, Object v1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedMap of(Object k1, Object v1, Object k2, Object v2) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedMap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedMap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3, Object k4, Object v4) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public static ImmutableSortedMap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3, Object k4, Object v4, Object k5, Object v5) {
      throw new UnsupportedOperationException();
   }
}
