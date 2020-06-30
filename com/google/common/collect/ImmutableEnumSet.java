package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;

@GwtCompatible(
   serializable = true,
   emulated = true
)
final class ImmutableEnumSet extends ImmutableSet {
   private final transient EnumSet delegate;
   private transient int hashCode;

   static ImmutableSet asImmutable(EnumSet set) {
      switch(set.size()) {
      case 0:
         return ImmutableSet.of();
      case 1:
         return ImmutableSet.of(Iterables.getOnlyElement(set));
      default:
         return new ImmutableEnumSet(set);
      }
   }

   private ImmutableEnumSet(EnumSet delegate) {
      this.delegate = delegate;
   }

   boolean isPartialView() {
      return false;
   }

   public UnmodifiableIterator iterator() {
      return Iterators.unmodifiableIterator(this.delegate.iterator());
   }

   public int size() {
      return this.delegate.size();
   }

   public boolean contains(Object object) {
      return this.delegate.contains(object);
   }

   public boolean containsAll(Collection collection) {
      return this.delegate.containsAll(collection);
   }

   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   public boolean equals(Object object) {
      return object == this || this.delegate.equals(object);
   }

   public int hashCode() {
      int result = this.hashCode;
      return result == 0?(this.hashCode = this.delegate.hashCode()):result;
   }

   public String toString() {
      return this.delegate.toString();
   }

   Object writeReplace() {
      return new ImmutableEnumSet.EnumSerializedForm(this.delegate);
   }

   private static class EnumSerializedForm implements Serializable {
      final EnumSet delegate;
      private static final long serialVersionUID = 0L;

      EnumSerializedForm(EnumSet delegate) {
         this.delegate = delegate;
      }

      Object readResolve() {
         return new ImmutableEnumSet(this.delegate.clone());
      }
   }
}
