package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractMapBasedMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Serialization;
import com.google.common.collect.WellBehavedMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.Iterator;

@GwtCompatible(
   emulated = true
)
public final class EnumMultiset extends AbstractMapBasedMultiset {
   private transient Class type;
   @GwtIncompatible("Not needed in emulated source")
   private static final long serialVersionUID = 0L;

   public static EnumMultiset create(Class type) {
      return new EnumMultiset(type);
   }

   public static EnumMultiset create(Iterable elements) {
      Iterator<E> iterator = elements.iterator();
      Preconditions.checkArgument(iterator.hasNext(), "EnumMultiset constructor passed empty Iterable");
      EnumMultiset<E> multiset = new EnumMultiset(((Enum)iterator.next()).getDeclaringClass());
      Iterables.addAll(multiset, elements);
      return multiset;
   }

   public static EnumMultiset create(Iterable elements, Class type) {
      EnumMultiset<E> result = create(type);
      Iterables.addAll(result, elements);
      return result;
   }

   private EnumMultiset(Class type) {
      super(WellBehavedMap.wrap(new EnumMap(type)));
      this.type = type;
   }

   @GwtIncompatible("java.io.ObjectOutputStream")
   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      stream.writeObject(this.type);
      Serialization.writeMultiset(this, stream);
   }

   @GwtIncompatible("java.io.ObjectInputStream")
   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      Class<E> localType = (Class)stream.readObject();
      this.type = localType;
      this.setBackingMap(WellBehavedMap.wrap(new EnumMap(this.type)));
      Serialization.populateMultiset(this, stream);
   }
}
