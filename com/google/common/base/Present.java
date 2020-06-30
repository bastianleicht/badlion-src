package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
final class Present extends Optional {
   private final Object reference;
   private static final long serialVersionUID = 0L;

   Present(Object reference) {
      this.reference = reference;
   }

   public boolean isPresent() {
      return true;
   }

   public Object get() {
      return this.reference;
   }

   public Object or(Object defaultValue) {
      Preconditions.checkNotNull(defaultValue, "use Optional.orNull() instead of Optional.or(null)");
      return this.reference;
   }

   public Optional or(Optional secondChoice) {
      Preconditions.checkNotNull(secondChoice);
      return this;
   }

   public Object or(Supplier supplier) {
      Preconditions.checkNotNull(supplier);
      return this.reference;
   }

   public Object orNull() {
      return this.reference;
   }

   public Set asSet() {
      return Collections.singleton(this.reference);
   }

   public Optional transform(Function function) {
      return new Present(Preconditions.checkNotNull(function.apply(this.reference), "the Function passed to Optional.transform() must not return null."));
   }

   public boolean equals(@Nullable Object object) {
      if(object instanceof Present) {
         Present<?> other = (Present)object;
         return this.reference.equals(other.reference);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 1502476572 + this.reference.hashCode();
   }

   public String toString() {
      return "Optional.of(" + this.reference + ")";
   }
}
