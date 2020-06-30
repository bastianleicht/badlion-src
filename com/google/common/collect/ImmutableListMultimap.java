package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.EmptyImmutableListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Serialization;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public class ImmutableListMultimap extends ImmutableMultimap implements ListMultimap {
   private transient ImmutableListMultimap inverse;
   @GwtIncompatible("Not needed in emulated source")
   private static final long serialVersionUID = 0L;

   public static ImmutableListMultimap of() {
      return EmptyImmutableListMultimap.INSTANCE;
   }

   public static ImmutableListMultimap of(Object k1, Object v1) {
      ImmutableListMultimap.Builder<K, V> builder = builder();
      builder.put(k1, v1);
      return builder.build();
   }

   public static ImmutableListMultimap of(Object k1, Object v1, Object k2, Object v2) {
      ImmutableListMultimap.Builder<K, V> builder = builder();
      builder.put(k1, v1);
      builder.put(k2, v2);
      return builder.build();
   }

   public static ImmutableListMultimap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3) {
      ImmutableListMultimap.Builder<K, V> builder = builder();
      builder.put(k1, v1);
      builder.put(k2, v2);
      builder.put(k3, v3);
      return builder.build();
   }

   public static ImmutableListMultimap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3, Object k4, Object v4) {
      ImmutableListMultimap.Builder<K, V> builder = builder();
      builder.put(k1, v1);
      builder.put(k2, v2);
      builder.put(k3, v3);
      builder.put(k4, v4);
      return builder.build();
   }

   public static ImmutableListMultimap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3, Object k4, Object v4, Object k5, Object v5) {
      ImmutableListMultimap.Builder<K, V> builder = builder();
      builder.put(k1, v1);
      builder.put(k2, v2);
      builder.put(k3, v3);
      builder.put(k4, v4);
      builder.put(k5, v5);
      return builder.build();
   }

   public static ImmutableListMultimap.Builder builder() {
      return new ImmutableListMultimap.Builder();
   }

   public static ImmutableListMultimap copyOf(Multimap multimap) {
      if(multimap.isEmpty()) {
         return of();
      } else {
         if(multimap instanceof ImmutableListMultimap) {
            ImmutableListMultimap<K, V> kvMultimap = (ImmutableListMultimap)multimap;
            if(!kvMultimap.isPartialView()) {
               return kvMultimap;
            }
         }

         ImmutableMap.Builder<K, ImmutableList<V>> builder = ImmutableMap.builder();
         int size = 0;

         for(Entry<? extends K, ? extends Collection<? extends V>> entry : multimap.asMap().entrySet()) {
            ImmutableList<V> list = ImmutableList.copyOf((Collection)entry.getValue());
            if(!list.isEmpty()) {
               builder.put(entry.getKey(), list);
               size += list.size();
            }
         }

         return new ImmutableListMultimap(builder.build(), size);
      }
   }

   ImmutableListMultimap(ImmutableMap map, int size) {
      super(map, size);
   }

   public ImmutableList get(@Nullable Object key) {
      ImmutableList<V> list = (ImmutableList)this.map.get(key);
      return list == null?ImmutableList.of():list;
   }

   public ImmutableListMultimap inverse() {
      ImmutableListMultimap<V, K> result = this.inverse;
      return result == null?(this.inverse = this.invert()):result;
   }

   private ImmutableListMultimap invert() {
      ImmutableListMultimap.Builder<V, K> builder = builder();

      for(Entry<K, V> entry : this.entries()) {
         builder.put(entry.getValue(), entry.getKey());
      }

      ImmutableListMultimap<V, K> invertedMultimap = builder.build();
      invertedMultimap.inverse = this;
      return invertedMultimap;
   }

   /** @deprecated */
   @Deprecated
   public ImmutableList removeAll(Object key) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public ImmutableList replaceValues(Object key, Iterable values) {
      throw new UnsupportedOperationException();
   }

   @GwtIncompatible("java.io.ObjectOutputStream")
   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      Serialization.writeMultimap(this, stream);
   }

   @GwtIncompatible("java.io.ObjectInputStream")
   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      int keyCount = stream.readInt();
      if(keyCount < 0) {
         throw new InvalidObjectException("Invalid key count " + keyCount);
      } else {
         ImmutableMap.Builder<Object, ImmutableList<Object>> builder = ImmutableMap.builder();
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

            builder.put(key, ImmutableList.copyOf(array));
            tmpSize += valueCount;
         }

         ImmutableMap<Object, ImmutableList<Object>> tmpMap;
         try {
            tmpMap = builder.build();
         } catch (IllegalArgumentException var10) {
            throw (InvalidObjectException)(new InvalidObjectException(var10.getMessage())).initCause(var10);
         }

         ImmutableMultimap.FieldSettersHolder.MAP_FIELD_SETTER.set(this, tmpMap);
         ImmutableMultimap.FieldSettersHolder.SIZE_FIELD_SETTER.set(this, tmpSize);
      }
   }

   public static final class Builder extends ImmutableMultimap.Builder {
      public ImmutableListMultimap.Builder put(Object key, Object value) {
         super.put(key, value);
         return this;
      }

      public ImmutableListMultimap.Builder put(Entry entry) {
         super.put(entry);
         return this;
      }

      public ImmutableListMultimap.Builder putAll(Object key, Iterable values) {
         super.putAll(key, values);
         return this;
      }

      public ImmutableListMultimap.Builder putAll(Object key, Object... values) {
         super.putAll(key, values);
         return this;
      }

      public ImmutableListMultimap.Builder putAll(Multimap multimap) {
         super.putAll(multimap);
         return this;
      }

      public ImmutableListMultimap.Builder orderKeysBy(Comparator keyComparator) {
         super.orderKeysBy(keyComparator);
         return this;
      }

      public ImmutableListMultimap.Builder orderValuesBy(Comparator valueComparator) {
         super.orderValuesBy(valueComparator);
         return this;
      }

      public ImmutableListMultimap build() {
         return (ImmutableListMultimap)super.build();
      }
   }
}
