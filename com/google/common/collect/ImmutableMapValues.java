package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableAsList;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class ImmutableMapValues extends ImmutableCollection {
   private final ImmutableMap map;

   ImmutableMapValues(ImmutableMap map) {
      this.map = map;
   }

   public int size() {
      return this.map.size();
   }

   public UnmodifiableIterator iterator() {
      return Maps.valueIterator(this.map.entrySet().iterator());
   }

   public boolean contains(@Nullable Object object) {
      return object != null && Iterators.contains(this.iterator(), object);
   }

   boolean isPartialView() {
      return true;
   }

   ImmutableList createAsList() {
      final ImmutableList<Entry<K, V>> entryList = this.map.entrySet().asList();
      return new ImmutableAsList() {
         public Object get(int index) {
            return ((Entry)entryList.get(index)).getValue();
         }

         ImmutableCollection delegateCollection() {
            return ImmutableMapValues.this;
         }
      };
   }

   @GwtIncompatible("serialization")
   Object writeReplace() {
      return new ImmutableMapValues.SerializedForm(this.map);
   }

   @GwtIncompatible("serialization")
   private static class SerializedForm implements Serializable {
      final ImmutableMap map;
      private static final long serialVersionUID = 0L;

      SerializedForm(ImmutableMap map) {
         this.map = map;
      }

      Object readResolve() {
         return this.map.values();
      }
   }
}
