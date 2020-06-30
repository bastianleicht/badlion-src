package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
final class SingletonImmutableBiMap extends ImmutableBiMap {
   final transient Object singleKey;
   final transient Object singleValue;
   transient ImmutableBiMap inverse;

   SingletonImmutableBiMap(Object singleKey, Object singleValue) {
      CollectPreconditions.checkEntryNotNull(singleKey, singleValue);
      this.singleKey = singleKey;
      this.singleValue = singleValue;
   }

   private SingletonImmutableBiMap(Object singleKey, Object singleValue, ImmutableBiMap inverse) {
      this.singleKey = singleKey;
      this.singleValue = singleValue;
      this.inverse = inverse;
   }

   SingletonImmutableBiMap(Entry entry) {
      this(entry.getKey(), entry.getValue());
   }

   public Object get(@Nullable Object key) {
      return this.singleKey.equals(key)?this.singleValue:null;
   }

   public int size() {
      return 1;
   }

   public boolean containsKey(@Nullable Object key) {
      return this.singleKey.equals(key);
   }

   public boolean containsValue(@Nullable Object value) {
      return this.singleValue.equals(value);
   }

   boolean isPartialView() {
      return false;
   }

   ImmutableSet createEntrySet() {
      return ImmutableSet.of(Maps.immutableEntry(this.singleKey, this.singleValue));
   }

   ImmutableSet createKeySet() {
      return ImmutableSet.of(this.singleKey);
   }

   public ImmutableBiMap inverse() {
      ImmutableBiMap<V, K> result = this.inverse;
      return result == null?(this.inverse = new SingletonImmutableBiMap(this.singleValue, this.singleKey, this)):result;
   }
}
