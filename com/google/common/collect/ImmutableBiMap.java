package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.BiMap;
import com.google.common.collect.EmptyImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMapEntry;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.RegularImmutableBiMap;
import com.google.common.collect.SingletonImmutableBiMap;
import java.util.Map;
import java.util.Map.Entry;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableBiMap extends ImmutableMap implements BiMap {
   private static final Entry[] EMPTY_ENTRY_ARRAY = new Entry[0];

   public static ImmutableBiMap of() {
      return EmptyImmutableBiMap.INSTANCE;
   }

   public static ImmutableBiMap of(Object k1, Object v1) {
      return new SingletonImmutableBiMap(k1, v1);
   }

   public static ImmutableBiMap of(Object k1, Object v1, Object k2, Object v2) {
      return new RegularImmutableBiMap(new ImmutableMapEntry.TerminalEntry[]{entryOf(k1, v1), entryOf(k2, v2)});
   }

   public static ImmutableBiMap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3) {
      return new RegularImmutableBiMap(new ImmutableMapEntry.TerminalEntry[]{entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3)});
   }

   public static ImmutableBiMap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3, Object k4, Object v4) {
      return new RegularImmutableBiMap(new ImmutableMapEntry.TerminalEntry[]{entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4)});
   }

   public static ImmutableBiMap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3, Object k4, Object v4, Object k5, Object v5) {
      return new RegularImmutableBiMap(new ImmutableMapEntry.TerminalEntry[]{entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4), entryOf(k5, v5)});
   }

   public static ImmutableBiMap.Builder builder() {
      return new ImmutableBiMap.Builder();
   }

   public static ImmutableBiMap copyOf(Map map) {
      if(map instanceof ImmutableBiMap) {
         ImmutableBiMap<K, V> bimap = (ImmutableBiMap)map;
         if(!bimap.isPartialView()) {
            return bimap;
         }
      }

      Entry<?, ?>[] entries = (Entry[])map.entrySet().toArray(EMPTY_ENTRY_ARRAY);
      switch(entries.length) {
      case 0:
         return of();
      case 1:
         Entry<K, V> entry = entries[0];
         return of(entry.getKey(), entry.getValue());
      default:
         return new RegularImmutableBiMap(entries);
      }
   }

   public abstract ImmutableBiMap inverse();

   public ImmutableSet values() {
      return this.inverse().keySet();
   }

   /** @deprecated */
   @Deprecated
   public Object forcePut(Object key, Object value) {
      throw new UnsupportedOperationException();
   }

   Object writeReplace() {
      return new ImmutableBiMap.SerializedForm(this);
   }

   public static final class Builder extends ImmutableMap.Builder {
      public ImmutableBiMap.Builder put(Object key, Object value) {
         super.put(key, value);
         return this;
      }

      public ImmutableBiMap.Builder putAll(Map map) {
         super.putAll(map);
         return this;
      }

      public ImmutableBiMap build() {
         switch(this.size) {
         case 0:
            return ImmutableBiMap.of();
         case 1:
            return ImmutableBiMap.of(this.entries[0].getKey(), this.entries[0].getValue());
         default:
            return new RegularImmutableBiMap(this.size, this.entries);
         }
      }
   }

   private static class SerializedForm extends ImmutableMap.SerializedForm {
      private static final long serialVersionUID = 0L;

      SerializedForm(ImmutableBiMap bimap) {
         super(bimap);
      }

      Object readResolve() {
         ImmutableBiMap.Builder<Object, Object> builder = new ImmutableBiMap.Builder();
         return this.createMap(builder);
      }
   }
}
