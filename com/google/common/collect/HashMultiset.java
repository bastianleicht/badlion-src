package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.AbstractMapBasedMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multisets;
import com.google.common.collect.Serialization;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public final class HashMultiset extends AbstractMapBasedMultiset {
   @GwtIncompatible("Not needed in emulated source.")
   private static final long serialVersionUID = 0L;

   public static HashMultiset create() {
      return new HashMultiset();
   }

   public static HashMultiset create(int distinctElements) {
      return new HashMultiset(distinctElements);
   }

   public static HashMultiset create(Iterable elements) {
      HashMultiset<E> multiset = create(Multisets.inferDistinctElements(elements));
      Iterables.addAll(multiset, elements);
      return multiset;
   }

   private HashMultiset() {
      super(new HashMap());
   }

   private HashMultiset(int distinctElements) {
      super(Maps.newHashMapWithExpectedSize(distinctElements));
   }

   @GwtIncompatible("java.io.ObjectOutputStream")
   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      Serialization.writeMultiset(this, stream);
   }

   @GwtIncompatible("java.io.ObjectInputStream")
   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      int distinctElements = Serialization.readCount(stream);
      this.setBackingMap(Maps.newHashMapWithExpectedSize(distinctElements));
      Serialization.populateMultiset(this, stream, distinctElements);
   }
}
