package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractMapBasedMultimap;
import com.google.common.collect.EmptyImmutableSetMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Serialization;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public class ImmutableSetMultimap extends ImmutableMultimap implements SetMultimap {
   private final transient ImmutableSet emptySet;
   private transient ImmutableSetMultimap inverse;
   private transient ImmutableSet entries;
   @GwtIncompatible("not needed in emulated source.")
   private static final long serialVersionUID = 0L;

   public static ImmutableSetMultimap of() {
      return EmptyImmutableSetMultimap.INSTANCE;
   }

   public static ImmutableSetMultimap of(Object k1, Object v1) {
      ImmutableSetMultimap.Builder<K, V> builder = builder();
      builder.put(k1, v1);
      return builder.build();
   }

   public static ImmutableSetMultimap of(Object k1, Object v1, Object k2, Object v2) {
      ImmutableSetMultimap.Builder<K, V> builder = builder();
      builder.put(k1, v1);
      builder.put(k2, v2);
      return builder.build();
   }

   public static ImmutableSetMultimap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3) {
      ImmutableSetMultimap.Builder<K, V> builder = builder();
      builder.put(k1, v1);
      builder.put(k2, v2);
      builder.put(k3, v3);
      return builder.build();
   }

   public static ImmutableSetMultimap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3, Object k4, Object v4) {
      ImmutableSetMultimap.Builder<K, V> builder = builder();
      builder.put(k1, v1);
      builder.put(k2, v2);
      builder.put(k3, v3);
      builder.put(k4, v4);
      return builder.build();
   }

   public static ImmutableSetMultimap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3, Object k4, Object v4, Object k5, Object v5) {
      ImmutableSetMultimap.Builder<K, V> builder = builder();
      builder.put(k1, v1);
      builder.put(k2, v2);
      builder.put(k3, v3);
      builder.put(k4, v4);
      builder.put(k5, v5);
      return builder.build();
   }

   public static ImmutableSetMultimap.Builder builder() {
      return new ImmutableSetMultimap.Builder();
   }

   public static ImmutableSetMultimap copyOf(Multimap multimap) {
      return copyOf(multimap, (Comparator)null);
   }

   private static ImmutableSetMultimap copyOf(Multimap multimap, Comparator valueComparator) {
      Preconditions.checkNotNull(multimap);
      if(multimap.isEmpty() && valueComparator == null) {
         return of();
      } else {
         if(multimap instanceof ImmutableSetMultimap) {
            ImmutableSetMultimap<K, V> kvMultimap = (ImmutableSetMultimap)multimap;
            if(!kvMultimap.isPartialView()) {
               return kvMultimap;
            }
         }

         ImmutableMap.Builder<K, ImmutableSet<V>> builder = ImmutableMap.builder();
         int size = 0;

         for(Entry<? extends K, ? extends Collection<? extends V>> entry : multimap.asMap().entrySet()) {
            K key = entry.getKey();
            Collection<? extends V> values = (Collection)entry.getValue();
            ImmutableSet<V> set = valueSet(valueComparator, values);
            if(!set.isEmpty()) {
               builder.put(key, set);
               size += set.size();
            }
         }

         return new ImmutableSetMultimap(builder.build(), size, valueComparator);
      }
   }

   ImmutableSetMultimap(ImmutableMap map, int size, @Nullable Comparator valueComparator) {
      super(map, size);
      this.emptySet = emptySet(valueComparator);
   }

   public ImmutableSet get(@Nullable Object key) {
      ImmutableSet<V> set = (ImmutableSet)this.map.get(key);
      return (ImmutableSet)Objects.firstNonNull(set, this.emptySet);
   }

   public ImmutableSetMultimap inverse() {
      ImmutableSetMultimap<V, K> result = this.inverse;
      return result == null?(this.inverse = this.invert()):result;
   }

   private ImmutableSetMultimap invert() {
      ImmutableSetMultimap.Builder<V, K> builder = builder();

      for(Entry<K, V> entry : this.entries()) {
         builder.put(entry.getValue(), entry.getKey());
      }

      ImmutableSetMultimap<V, K> invertedMultimap = builder.build();
      invertedMultimap.inverse = this;
      return invertedMultimap;
   }

   /** @deprecated */
   @Deprecated
   public ImmutableSet removeAll(Object key) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public ImmutableSet replaceValues(Object key, Iterable values) {
      throw new UnsupportedOperationException();
   }

   public ImmutableSet entries() {
      ImmutableSet<Entry<K, V>> result = this.entries;
      return result == null?(this.entries = new ImmutableSetMultimap.EntrySet(this)):result;
   }

   private static ImmutableSet valueSet(@Nullable Comparator valueComparator, Collection values) {
      return (ImmutableSet)(valueComparator == null?ImmutableSet.copyOf(values):ImmutableSortedSet.copyOf(valueComparator, values));
   }

   private static ImmutableSet emptySet(@Nullable Comparator valueComparator) {
      return (ImmutableSet)(valueComparator == null?ImmutableSet.of():ImmutableSortedSet.emptySet(valueComparator));
   }

   @GwtIncompatible("java.io.ObjectOutputStream")
   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      stream.writeObject(this.valueComparator());
      Serialization.writeMultimap(this, stream);
   }

   @Nullable
   Comparator valueComparator() {
      return this.emptySet instanceof ImmutableSortedSet?((ImmutableSortedSet)this.emptySet).comparator():null;
   }

   @GwtIncompatible("java.io.ObjectInputStream")
   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      Comparator<Object> valueComparator = (Comparator)stream.readObject();
      int keyCount = stream.readInt();
      if(keyCount < 0) {
         throw new InvalidObjectException("Invalid key count " + keyCount);
      } else {
         ImmutableMap.Builder<Object, ImmutableSet<Object>> builder = ImmutableMap.builder();
         int tmpSize = 0;

         for(int i = 0; i < keyCount; ++i) {
            Object key = stream.readObject();
            int valueCount = stream.readInt();
            if(valueCount <= 0) {
               throw new InvalidObjectException("Invalid value count " + valueCount);
            }

            Object[] array = new Object[valueCount];

            for(int j = 0; j < valueCount; ++j) {
               array[j] = stream.readObject();
            }

            ImmutableSet<Object> valueSet = valueSet(valueComparator, Arrays.asList(array));
            if(valueSet.size() != array.length) {
               throw new InvalidObjectException("Duplicate key-value pairs exist for key " + key);
            }

            builder.put(key, valueSet);
            tmpSize += valueCount;
         }

         ImmutableMap<Object, ImmutableSet<Object>> tmpMap;
         try {
            tmpMap = builder.build();
         } catch (IllegalArgumentException var11) {
            throw (InvalidObjectException)(new InvalidObjectException(var11.getMessage())).initCause(var11);
         }

         ImmutableMultimap.FieldSettersHolder.MAP_FIELD_SETTER.set(this, tmpMap);
         ImmutableMultimap.FieldSettersHolder.SIZE_FIELD_SETTER.set(this, tmpSize);
         ImmutableMultimap.FieldSettersHolder.EMPTY_SET_FIELD_SETTER.set(this, emptySet(valueComparator));
      }
   }

   public static final class Builder extends ImmutableMultimap.Builder {
      public Builder() {
         this.builderMultimap = new ImmutableSetMultimap.BuilderMultimap();
      }

      public ImmutableSetMultimap.Builder put(Object key, Object value) {
         this.builderMultimap.put(Preconditions.checkNotNull(key), Preconditions.checkNotNull(value));
         return this;
      }

      public ImmutableSetMultimap.Builder put(Entry entry) {
         this.builderMultimap.put(Preconditions.checkNotNull(entry.getKey()), Preconditions.checkNotNull(entry.getValue()));
         return this;
      }

      public ImmutableSetMultimap.Builder putAll(Object key, Iterable values) {
         Collection<V> collection = this.builderMultimap.get(Preconditions.checkNotNull(key));

         for(V value : values) {
            collection.add(Preconditions.checkNotNull(value));
         }

         return this;
      }

      public ImmutableSetMultimap.Builder putAll(Object key, Object... values) {
         return this.putAll(key, (Iterable)Arrays.asList(values));
      }

      public ImmutableSetMultimap.Builder putAll(Multimap multimap) {
         for(Entry<? extends K, ? extends Collection<? extends V>> entry : multimap.asMap().entrySet()) {
            this.putAll(entry.getKey(), (Iterable)entry.getValue());
         }

         return this;
      }

      public ImmutableSetMultimap.Builder orderKeysBy(Comparator keyComparator) {
         this.keyComparator = (Comparator)Preconditions.checkNotNull(keyComparator);
         return this;
      }

      public ImmutableSetMultimap.Builder orderValuesBy(Comparator valueComparator) {
         super.orderValuesBy(valueComparator);
         return this;
      }

      public ImmutableSetMultimap build() {
         if(this.keyComparator != null) {
            Multimap<K, V> sortedCopy = new ImmutableSetMultimap.BuilderMultimap();
            List<Entry<K, Collection<V>>> entries = Lists.newArrayList((Iterable)this.builderMultimap.asMap().entrySet());
            Collections.sort(entries, Ordering.from(this.keyComparator).onKeys());

            for(Entry<K, Collection<V>> entry : entries) {
               sortedCopy.putAll(entry.getKey(), (Iterable)entry.getValue());
            }

            this.builderMultimap = sortedCopy;
         }

         return ImmutableSetMultimap.copyOf(this.builderMultimap, this.valueComparator);
      }
   }

   private static class BuilderMultimap extends AbstractMapBasedMultimap {
      private static final long serialVersionUID = 0L;

      BuilderMultimap() {
         super(new LinkedHashMap());
      }

      Collection createCollection() {
         return Sets.newLinkedHashSet();
      }
   }

   private static final class EntrySet extends ImmutableSet {
      private final transient ImmutableSetMultimap multimap;

      EntrySet(ImmutableSetMultimap multimap) {
         this.multimap = multimap;
      }

      public boolean contains(@Nullable Object object) {
         if(object instanceof Entry) {
            Entry<?, ?> entry = (Entry)object;
            return this.multimap.containsEntry(entry.getKey(), entry.getValue());
         } else {
            return false;
         }
      }

      public int size() {
         return this.multimap.size();
      }

      public UnmodifiableIterator iterator() {
         return this.multimap.entryIterator();
      }

      boolean isPartialView() {
         return false;
      }
   }
}
