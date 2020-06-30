package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableAsList;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class ImmutableMapKeySet extends ImmutableSet {
   private final ImmutableMap map;

   ImmutableMapKeySet(ImmutableMap map) {
      this.map = map;
   }

   public int size() {
      return this.map.size();
   }

   public UnmodifiableIterator iterator() {
      return this.asList().iterator();
   }

   public boolean contains(@Nullable Object object) {
      return this.map.containsKey(object);
   }

   ImmutableList createAsList() {
      final ImmutableList<Entry<K, V>> entryList = this.map.entrySet().asList();
      return new ImmutableAsList() {
         public Object get(int index) {
            return ((Entry)entryList.get(index)).getKey();
         }

         ImmutableCollection delegateCollection() {
            return ImmutableMapKeySet.this;
         }
      };
   }

   boolean isPartialView() {
      return true;
   }

   @GwtIncompatible("serialization")
   Object writeReplace() {
      return new ImmutableMapKeySet.KeySetSerializedForm(this.map);
   }

   @GwtIncompatible("serialization")
   private static class KeySetSerializedForm implements Serializable {
      final ImmutableMap map;
      private static final long serialVersionUID = 0L;

      KeySetSerializedForm(ImmutableMap map) {
         this.map = map;
      }

      Object readResolve() {
         return this.map.keySet();
      }
   }
}
