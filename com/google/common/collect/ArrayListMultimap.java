package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.AbstractListMultimap;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Serialization;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public final class ArrayListMultimap extends AbstractListMultimap {
   private static final int DEFAULT_VALUES_PER_KEY = 3;
   @VisibleForTesting
   transient int expectedValuesPerKey;
   @GwtIncompatible("Not needed in emulated source.")
   private static final long serialVersionUID = 0L;

   public static ArrayListMultimap create() {
      return new ArrayListMultimap();
   }

   public static ArrayListMultimap create(int expectedKeys, int expectedValuesPerKey) {
      return new ArrayListMultimap(expectedKeys, expectedValuesPerKey);
   }

   public static ArrayListMultimap create(Multimap multimap) {
      return new ArrayListMultimap(multimap);
   }

   private ArrayListMultimap() {
      super(new HashMap());
      this.expectedValuesPerKey = 3;
   }

   private ArrayListMultimap(int expectedKeys, int expectedValuesPerKey) {
      super(Maps.newHashMapWithExpectedSize(expectedKeys));
      CollectPreconditions.checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
      this.expectedValuesPerKey = expectedValuesPerKey;
   }

   private ArrayListMultimap(Multimap multimap) {
      this(multimap.keySet().size(), multimap instanceof ArrayListMultimap?((ArrayListMultimap)multimap).expectedValuesPerKey:3);
      this.putAll(multimap);
   }

   List createCollection() {
      return new ArrayList(this.expectedValuesPerKey);
   }

   public void trimToSize() {
      for(Collection<V> collection : this.backingMap().values()) {
         ArrayList<V> arrayList = (ArrayList)collection;
         arrayList.trimToSize();
      }

   }

   @GwtIncompatible("java.io.ObjectOutputStream")
   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      stream.writeInt(this.expectedValuesPerKey);
      Serialization.writeMultimap(this, stream);
   }

   @GwtIncompatible("java.io.ObjectOutputStream")
   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      this.expectedValuesPerKey = stream.readInt();
      int distinctKeys = Serialization.readCount(stream);
      Map<K, Collection<V>> map = Maps.newHashMapWithExpectedSize(distinctKeys);
      this.setMap(map);
      Serialization.populateMultimap(this, stream, distinctKeys);
   }
}
