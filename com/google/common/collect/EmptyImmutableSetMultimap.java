package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import java.util.Comparator;

@GwtCompatible(
   serializable = true
)
class EmptyImmutableSetMultimap extends ImmutableSetMultimap {
   static final EmptyImmutableSetMultimap INSTANCE = new EmptyImmutableSetMultimap();
   private static final long serialVersionUID = 0L;

   private EmptyImmutableSetMultimap() {
      super(ImmutableMap.of(), 0, (Comparator)null);
   }

   private Object readResolve() {
      return INSTANCE;
   }
}
