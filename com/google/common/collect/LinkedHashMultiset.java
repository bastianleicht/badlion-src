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
import java.util.LinkedHashMap;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public final class LinkedHashMultiset extends AbstractMapBasedMultiset {
   @GwtIncompatible("not needed in emulated source")
   private static final long serialVersionUID = 0L;

   public static LinkedHashMultiset create() {
      return new LinkedHashMultiset();
   }

   public static LinkedHashMultiset create(int distinctElements) {
      return new LinkedHashMultiset(distinctElements);
   }

   public static LinkedHashMultiset create(Iterable elements) {
      LinkedHashMultiset<E> multiset = create(Multisets.inferDistinctElements(elements));
      Iterables.addAll(multiset, elements);
      return multiset;
   }

   private LinkedHashMultiset() {
      super(new LinkedHashMap());
   }

   private LinkedHashMultiset(int distinctElements) {
      super(new LinkedHashMap(Maps.capacity(distinctElements)));
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
      this.setBackingMap(new LinkedHashMap(Maps.capacity(distinctElements)));
      Serialization.populateMultiset(this, stream, distinctElements);
   }
}
