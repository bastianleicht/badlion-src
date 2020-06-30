package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractMapEntry;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableEnumMap;
import com.google.common.collect.ImmutableMapEntry;
import com.google.common.collect.ImmutableMapEntrySet;
import com.google.common.collect.ImmutableMapKeySet;
import com.google.common.collect.ImmutableMapValues;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.RegularImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableMap implements Map, Serializable {
   private static final Entry[] EMPTY_ENTRY_ARRAY = new Entry[0];
   private transient ImmutableSet entrySet;
   private transient ImmutableSet keySet;
   private transient ImmutableCollection values;
   private transient ImmutableSetMultimap multimapView;

   public static ImmutableMap of() {
      return ImmutableBiMap.of();
   }

   public static ImmutableMap of(Object k1, Object v1) {
      return ImmutableBiMap.of(k1, v1);
   }

   public static ImmutableMap of(Object k1, Object v1, Object k2, Object v2) {
      return new RegularImmutableMap(new ImmutableMapEntry.TerminalEntry[]{entryOf(k1, v1), entryOf(k2, v2)});
   }

   public static ImmutableMap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3) {
      return new RegularImmutableMap(new ImmutableMapEntry.TerminalEntry[]{entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3)});
   }

   public static ImmutableMap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3, Object k4, Object v4) {
      return new RegularImmutableMap(new ImmutableMapEntry.TerminalEntry[]{entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4)});
   }

   public static ImmutableMap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3, Object k4, Object v4, Object k5, Object v5) {
      return new RegularImmutableMap(new ImmutableMapEntry.TerminalEntry[]{entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4), entryOf(k5, v5)});
   }

   static ImmutableMapEntry.TerminalEntry entryOf(Object key, Object value) {
      CollectPreconditions.checkEntryNotNull(key, value);
      return new ImmutableMapEntry.TerminalEntry(key, value);
   }

   public static ImmutableMap.Builder builder() {
      return new ImmutableMap.Builder();
   }

   static void checkNoConflict(boolean safe, String conflictDescription, Entry entry1, Entry entry2) {
      if(!safe) {
         throw new IllegalArgumentException("Multiple entries with same " + conflictDescription + ": " + entry1 + " and " + entry2);
      }
   }

   public static ImmutableMap copyOf(Map map) {
      if(map instanceof ImmutableMap && !(map instanceof ImmutableSortedMap)) {
         ImmutableMap<K, V> kvMap = (ImmutableMap)map;
         if(!kvMap.isPartialView()) {
            return kvMap;
         }
      } else if(map instanceof EnumMap) {
         return copyOfEnumMapUnsafe(map);
      }

      Entry<?, ?>[] entries = (Entry[])map.entrySet().toArray(EMPTY_ENTRY_ARRAY);
      switch(entries.length) {
      case 0:
         return of();
      case 1:
         Entry<K, V> onlyEntry = entries[0];
         return of(onlyEntry.getKey(), onlyEntry.getValue());
      default:
         return new RegularImmutableMap(entries);
      }
   }

   private static ImmutableMap copyOfEnumMapUnsafe(Map map) {
      return copyOfEnumMap((EnumMap)map);
   }

   private static ImmutableMap copyOfEnumMap(Map original) {
      EnumMap<K, V> copy = new EnumMap(original);

      for(Entry<?, ?> entry : copy.entrySet()) {
         CollectPreconditions.checkEntryNotNull(entry.getKey(), entry.getValue());
      }

      return ImmutableEnumMap.asImmutable(copy);
   }

   /** @deprecated */
   @Deprecated
   public final Object put(Object k, Object v) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final Object remove(Object o) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final void putAll(Map map) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public final void clear() {
      throw new UnsupportedOperationException();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public boolean containsKey(@Nullable Object key) {
      return this.get(key) != null;
   }

   public boolean containsValue(@Nullable Object value) {
      return this.values().contains(value);
   }

   public abstract Object get(@Nullable Object var1);

   public ImmutableSet entrySet() {
      ImmutableSet<Entry<K, V>> result = this.entrySet;
      return result == null?(this.entrySet = this.createEntrySet()):result;
   }

   abstract ImmutableSet createEntrySet();

   public ImmutableSet keySet() {
      ImmutableSet<K> result = this.keySet;
      return result == null?(this.keySet = this.createKeySet()):result;
   }

   ImmutableSet createKeySet() {
      return new ImmutableMapKeySet(this);
   }

   public ImmutableCollection values() {
      ImmutableCollection<V> result = this.values;
      return result == null?(this.values = new ImmutableMapValues(this)):result;
   }

   @Beta
   public ImmutableSetMultimap asMultimap() {
      ImmutableSetMultimap<K, V> result = this.multimapView;
      return result == null?(this.multimapView = this.createMultimapView()):result;
   }

   private ImmutableSetMultimap createMultimapView() {
      ImmutableMap<K, ImmutableSet<V>> map = this.viewMapValuesAsSingletonSets();
      return new ImmutableSetMultimap(map, map.size(), (Comparator)null);
   }

   private ImmutableMap viewMapValuesAsSingletonSets() {
      return new ImmutableMap.MapViewOfValuesAsSingletonSets(this);
   }

   public boolean equals(@Nullable Object object) {
      return Maps.equalsImpl(this, object);
   }

   abstract boolean isPartialView();

   public int hashCode() {
      return this.entrySet().hashCode();
   }

   public String toString() {
      return Maps.toStringImpl(this);
   }

   Object writeReplace() {
      return new ImmutableMap.SerializedForm(this);
   }

   public static class Builder {
      ImmutableMapEntry.TerminalEntry[] entries;
      int size;

      public Builder() {
         this(4);
      }

      Builder(int initialCapacity) {
         this.entries = new ImmutableMapEntry.TerminalEntry[initialCapacity];
         this.size = 0;
      }

      private void ensureCapacity(int minCapacity) {
         if(minCapacity > this.entries.length) {
            this.entries = (ImmutableMapEntry.TerminalEntry[])ObjectArrays.arraysCopyOf(this.entries, ImmutableCollection.Builder.expandedCapacity(this.entries.length, minCapacity));
         }

      }

      public ImmutableMap.Builder put(Object key, Object value) {
         this.ensureCapacity(this.size + 1);
         ImmutableMapEntry.TerminalEntry<K, V> entry = ImmutableMap.entryOf(key, value);
         this.entries[this.size++] = entry;
         return this;
      }

      public ImmutableMap.Builder put(Entry entry) {
         return this.put(entry.getKey(), entry.getValue());
      }

      public ImmutableMap.Builder putAll(Map map) {
         this.ensureCapacity(this.size + map.size());

         for(Entry<? extends K, ? extends V> entry : map.entrySet()) {
            this.put(entry);
         }

         return this;
      }

      public ImmutableMap build() {
         switch(this.size) {
         case 0:
            return ImmutableBiMap.of();
         case 1:
            return ImmutableBiMap.of(this.entries[0].getKey(), this.entries[0].getValue());
         default:
            return new RegularImmutableMap(this.size, this.entries);
         }
      }
   }

   private static final class MapViewOfValuesAsSingletonSets extends ImmutableMap {
      private final ImmutableMap delegate;

      MapViewOfValuesAsSingletonSets(ImmutableMap delegate) {
         this.delegate = (ImmutableMap)Preconditions.checkNotNull(delegate);
      }

      public int size() {
         return this.delegate.size();
      }

      public boolean containsKey(@Nullable Object key) {
         return this.delegate.containsKey(key);
      }

      public ImmutableSet get(@Nullable Object key) {
         V outerValue = this.delegate.get(key);
         return outerValue == null?null:ImmutableSet.of(outerValue);
      }

      boolean isPartialView() {
         return false;
      }

      ImmutableSet createEntrySet() {
         return new ImmutableMapEntrySet() {
            ImmutableMap map() {
               return MapViewOfValuesAsSingletonSets.this;
            }

            public UnmodifiableIterator iterator() {
               final Iterator<Entry<K, V>> backingIterator = MapViewOfValuesAsSingletonSets.this.delegate.entrySet().iterator();
               return new UnmodifiableIterator() {
                  public boolean hasNext() {
                     return backingIterator.hasNext();
                  }

                  public Entry next() {
                     final Entry<K, V> backingEntry = (Entry)backingIterator.next();
                     return new AbstractMapEntry() {
                        public Object getKey() {
                           return backingEntry.getKey();
                        }

                        public ImmutableSet getValue() {
                           return ImmutableSet.of(backingEntry.getValue());
                        }
                     };
                  }
               };
            }
         };
      }
   }

   static class SerializedForm implements Serializable {
      private final Object[] keys;
      private final Object[] values;
      private static final long serialVersionUID = 0L;

      SerializedForm(ImmutableMap map) {
         this.keys = new Object[map.size()];
         this.values = new Object[map.size()];
         int i = 0;

         for(Entry<?, ?> entry : map.entrySet()) {
            this.keys[i] = entry.getKey();
            this.values[i] = entry.getValue();
            ++i;
         }

      }

      Object readResolve() {
         ImmutableMap.Builder<Object, Object> builder = new ImmutableMap.Builder();
         return this.createMap(builder);
      }

      Object createMap(ImmutableMap.Builder builder) {
         for(int i = 0; i < this.keys.length; ++i) {
            builder.put(this.keys[i], this.values[i]);
         }

         return builder.build();
      }
   }
}
