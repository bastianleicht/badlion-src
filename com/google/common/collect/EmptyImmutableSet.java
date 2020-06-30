package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
final class EmptyImmutableSet extends ImmutableSet {
   static final EmptyImmutableSet INSTANCE = new EmptyImmutableSet();
   private static final long serialVersionUID = 0L;

   public int size() {
      return 0;
   }

   public boolean isEmpty() {
      return true;
   }

   public boolean contains(@Nullable Object target) {
      return false;
   }

   public boolean containsAll(Collection targets) {
      return targets.isEmpty();
   }

   public UnmodifiableIterator iterator() {
      return Iterators.emptyIterator();
   }

   boolean isPartialView() {
      return false;
   }

   int copyIntoArray(Object[] dst, int offset) {
      return offset;
   }

   public ImmutableList asList() {
      return ImmutableList.of();
   }

   public boolean equals(@Nullable Object object) {
      if(object instanceof Set) {
         Set<?> that = (Set)object;
         return that.isEmpty();
      } else {
         return false;
      }
   }

   public final int hashCode() {
      return 0;
   }

   boolean isHashCodeFast() {
      return true;
   }

   public String toString() {
      return "[]";
   }

   Object readResolve() {
      return INSTANCE;
   }
}
