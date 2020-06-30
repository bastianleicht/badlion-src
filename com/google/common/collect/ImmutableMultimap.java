package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractMapBasedMultimap;
import com.google.common.collect.AbstractMultimap;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.Serialization;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public abstract class ImmutableMultimap extends AbstractMultimap implements Serializable {
   final transient ImmutableMap map;
   final transient int size;
   private static final long serialVersionUID = 0L;

   public static ImmutableMultimap of() {
      return ImmutableListMultimap.of();
   }

   public static ImmutableMultimap of(Object k1, Object v1) {
      return ImmutableListMultimap.of(k1, v1);
   }

   public static ImmutableMultimap of(Object k1, Object v1, Object k2, Object v2) {
      return ImmutableListMultimap.of(k1, v1, k2, v2);
   }

   public static ImmutableMultimap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3) {
      return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3);
   }

   public static ImmutableMultimap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3, Object k4, Object v4) {
      return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3, k4, v4);
   }

   public static ImmutableMultimap of(Object k1, Object v1, Object k2, Object v2, Object k3, Object v3, Object k4, Object v4, Object k5, Object v5) {
      return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
   }

   public static ImmutableMultimap.Builder builder() {
      return new ImmutableMultimap.Builder();
   }

   public static ImmutableMultimap copyOf(Multimap multimap) {
      if(multimap instanceof ImmutableMultimap) {
         ImmutableMultimap<K, V> kvMultimap = (ImmutableMultimap)multimap;
         if(!kvMultimap.isPartialView()) {
            return kvMultimap;
         }
      }

      return ImmutableListMultimap.copyOf(multimap);
   }

   ImmutableMultimap(ImmutableMap map, int size) {
      this.map = map;
      this.size = size;
   }

   /** @deprecated */
   @Deprecated
   public ImmutableCollection removeAll(Object key) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public ImmutableCollection replaceValues(Object key, Iterable values) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public void clear() {
      throw new UnsupportedOperationException();
   }

   public abstract ImmutableCollection get(Object var1);

   public abstract ImmutableMultimap inverse();

   /** @deprecated */
   @Deprecated
   public boolean put(Object key, Object value) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public boolean putAll(Object key, Iterable values) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public boolean putAll(Multimap multimap) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public boolean remove(Object key, Object value) {
      throw new UnsupportedOperationException();
   }

   boolean isPartialView() {
      return this.map.isPartialView();
   }

   public boolean containsKey(@Nullable Object key) {
      return this.map.containsKey(key);
   }

   public boolean containsValue(@Nullable Object value) {
      return value != null && super.containsValue(value);
   }

   public int size() {
      return this.size;
   }

   public ImmutableSet keySet() {
      return this.map.keySet();
   }

   public ImmutableMap asMap() {
      return this.map;
   }

   Map createAsMap() {
      throw new AssertionError("should never be called");
   }

   public ImmutableCollection entries() {
      return (ImmutableCollection)super.entries();
   }

   ImmutableCollection createEntries() {
      return new ImmutableMultimap.EntryCollection(this);
   }

   UnmodifiableIterator entryIterator() {
      return new ImmutableMultimap.Itr(null) {
         Entry output(Object key, Object value) {
            return Maps.immutableEntry(key, value);
         }
      };
   }

   public ImmutableMultiset keys() {
      return (ImmutableMultiset)super.keys();
   }

   ImmutableMultiset createKeys() {
      return new ImmutableMultimap.Keys();
   }

   public ImmutableCollection values() {
      return (ImmutableCollection)super.values();
   }

   ImmutableCollection createValues() {
      return new ImmutableMultimap.Values(this);
   }

   UnmodifiableIterator valueIterator() {
      return new ImmutableMultimap.Itr(null) {
         Object output(Object key, Object value) {
            return value;
         }
      };
   }

   public static class Builder {
      Multimap builderMultimap = new ImmutableMultimap.BuilderMultimap();
      Comparator keyComparator;
      Comparator valueComparator;

      public ImmutableMultimap.Builder put(Object key, Object value) {
         CollectPreconditions.checkEntryNotNull(key, value);
         this.builderMultimap.put(key, value);
         return this;
      }

      public ImmutableMultimap.Builder put(Entry entry) {
         return this.put(entry.getKey(), entry.getValue());
      }

      public ImmutableMultimap.Builder putAll(Object key, Iterable values) {
         if(key == null) {
            throw new NullPointerException("null key in entry: null=" + Iterables.toString(values));
         } else {
            Collection<V> valueList = this.builderMultimap.get(key);

            for(V value : values) {
               CollectPreconditions.checkEntryNotNull(key, value);
               valueList.add(value);
            }

            return this;
         }
      }

      public ImmutableMultimap.Builder putAll(Object key, Object... values) {
         return this.putAll(key, (Iterable)Arrays.asList(values));
      }

      public ImmutableMultimap.Builder putAll(Multimap multimap) {
         for(Entry<? extends K, ? extends Collection<? extends V>> entry : multimap.asMap().entrySet()) {
            this.putAll(entry.getKey(), (Iterable)entry.getValue());
         }

         return this;
      }

      public ImmutableMultimap.Builder orderKeysBy(Comparator keyComparator) {
         this.keyComparator = (Comparator)Preconditions.checkNotNull(keyComparator);
         return this;
      }

      public ImmutableMultimap.Builder orderValuesBy(Comparator valueComparator) {
         this.valueComparator = (Comparator)Preconditions.checkNotNull(valueComparator);
         return this;
      }

      public ImmutableMultimap build() {
         if(this.valueComparator != null) {
            for(Collection<V> values : this.builderMultimap.asMap().values()) {
               List<V> list = (List)values;
               Collections.sort(list, this.valueComparator);
            }
         }

         if(this.keyComparator != null) {
            Multimap<K, V> sortedCopy = new ImmutableMultimap.BuilderMultimap();
            List<Entry<K, Collection<V>>> entries = Lists.newArrayList((Iterable)this.builderMultimap.asMap().entrySet());
            Collections.sort(entries, Ordering.from(this.keyComparator).onKeys());

            for(Entry<K, Collection<V>> entry : entries) {
               sortedCopy.putAll(entry.getKey(), (Iterable)entry.getValue());
            }

            this.builderMultimap = sortedCopy;
         }

         return ImmutableMultimap.copyOf(this.builderMultimap);
      }
   }

   private static class BuilderMultimap extends AbstractMapBasedMultimap {
      private static final long serialVersionUID = 0L;

      BuilderMultimap() {
         super(new LinkedHashMap());
      }

      Collection createCollection() {
         return Lists.newArrayList();
      }
   }

   private static class EntryCollection extends ImmutableCollection {
      final ImmutableMultimap multimap;
      private static final long serialVersionUID = 0L;

      EntryCollection(ImmutableMultimap multimap) {
         this.multimap = multimap;
      }

      public UnmodifiableIterator iterator() {
         return this.multimap.entryIterator();
      }

      boolean isPartialView() {
         return this.multimap.isPartialView();
      }

      public int size() {
         return this.multimap.size();
      }

      public boolean contains(Object object) {
         if(object instanceof Entry) {
            Entry<?, ?> entry = (Entry)object;
            return this.multimap.containsEntry(entry.getKey(), entry.getValue());
         } else {
            return false;
         }
      }
   }

   @GwtIncompatible("java serialization is not supported")
   static class FieldSettersHolder {
      static final Serialization.FieldSetter MAP_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "map");
      static final Serialization.FieldSetter SIZE_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "size");
      static final Serialization.FieldSetter EMPTY_SET_FIELD_SETTER = Serialization.getFieldSetter(ImmutableSetMultimap.class, "emptySet");
   }

   private abstract class Itr extends UnmodifiableIterator {
      final Iterator mapIterator;
      Object key;
      Iterator valueIterator;

      private Itr() {
         this.mapIterator = ImmutableMultimap.this.asMap().entrySet().iterator();
         this.key = null;
         this.valueIterator = Iterators.emptyIterator();
      }

      abstract Object output(Object var1, Object var2);

      public boolean hasNext() {
         return this.mapIterator.hasNext() || this.valueIterator.hasNext();
      }

      public Object next() {
         if(!this.valueIterator.hasNext()) {
            Entry<K, Collection<V>> mapEntry = (Entry)this.mapIterator.next();
            this.key = mapEntry.getKey();
            this.valueIterator = ((Collection)mapEntry.getValue()).iterator();
         }

         return this.output(this.key, this.valueIterator.next());
      }
   }

   class Keys extends ImmutableMultiset {
      public boolean contains(@Nullable Object object) {
         return ImmutableMultimap.this.containsKey(object);
      }

      public int count(@Nullable Object element) {
         Collection<V> values = (Collection)ImmutableMultimap.this.map.get(element);
         return values == null?0:values.size();
      }

      public Set elementSet() {
         return ImmutableMultimap.this.keySet();
      }

      public int size() {
         return ImmutableMultimap.this.size();
      }

      Multiset.Entry getEntry(int index) {
         Entry<K, ? extends Collection<V>> entry = (Entry)ImmutableMultimap.this.map.entrySet().asList().get(index);
         return Multisets.immutableEntry(entry.getKey(), ((Collection)entry.getValue()).size());
      }

      boolean isPartialView() {
         return true;
      }
   }

   private static final class Values extends ImmutableCollection {
      private final transient ImmutableMultimap multimap;
      private static final long serialVersionUID = 0L;

      Values(ImmutableMultimap multimap) {
         this.multimap = multimap;
      }

      public boolean contains(@Nullable Object object) {
         return this.multimap.containsValue(object);
      }

      public UnmodifiableIterator iterator() {
         return this.multimap.valueIterator();
      }

      @GwtIncompatible("not present in emulated superclass")
      int copyIntoArray(Object[] dst, int offset) {
         for(ImmutableCollection<V> valueCollection : this.multimap.map.values()) {
            offset = valueCollection.copyIntoArray(dst, offset);
         }

         return offset;
      }

      public int size() {
         return this.multimap.size();
      }

      boolean isPartialView() {
         return true;
      }
   }
}
