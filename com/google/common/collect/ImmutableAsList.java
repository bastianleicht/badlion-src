package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
abstract class ImmutableAsList extends ImmutableList {
   abstract ImmutableCollection delegateCollection();

   public boolean contains(Object target) {
      return this.delegateCollection().contains(target);
   }

   public int size() {
      return this.delegateCollection().size();
   }

   public boolean isEmpty() {
      return this.delegateCollection().isEmpty();
   }

   boolean isPartialView() {
      return this.delegateCollection().isPartialView();
   }

   @GwtIncompatible("serialization")
   private void readObject(ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Use SerializedForm");
   }

   @GwtIncompatible("serialization")
   Object writeReplace() {
      return new ImmutableAsList.SerializedForm(this.delegateCollection());
   }

   @GwtIncompatible("serialization")
   static class SerializedForm implements Serializable {
      final ImmutableCollection collection;
      private static final long serialVersionUID = 0L;

      SerializedForm(ImmutableCollection collection) {
         this.collection = collection;
      }

      Object readResolve() {
         return this.collection.asList();
      }
   }
}
