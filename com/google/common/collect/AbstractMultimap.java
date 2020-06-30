package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractMultimap implements Multimap {
   private transient Collection entries;
   private transient Set keySet;
   private transient Multiset keys;
   private transient Collection values;
   private transient Map asMap;

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public boolean containsValue(@Nullable Object value) {
      for(Collection<V> collection : this.asMap().values()) {
         if(collection.contains(value)) {
            return true;
         }
      }

      return false;
   }

   public boolean containsEntry(@Nullable Object key, @Nullable Object value) {
      Collection<V> collection = (Collection)this.asMap().get(key);
      return collection != null && collection.contains(value);
   }

   public boolean remove(@Nullable Object key, @Nullable Object value) {
      Collection<V> collection = (Collection)this.asMap().get(key);
      return collection != null && collection.remove(value);
   }

   public boolean put(@Nullable Object key, @Nullable Object value) {
      return this.get(key).add(value);
   }

   public boolean putAll(@Nullable Object key, Iterable values) {
      Preconditions.checkNotNull(values);
      if(values instanceof Collection) {
         Collection<? extends V> valueCollection = (Collection)values;
         return !valueCollection.isEmpty() && this.get(key).addAll(valueCollection);
      } else {
         Iterator<? extends V> valueItr = values.iterator();
         return valueItr.hasNext() && Iterators.addAll(this.get(key), valueItr);
      }
   }

   public boolean putAll(Multimap multimap) {
      boolean changed = false;

      for(Entry<? extends K, ? extends V> entry : multimap.entries()) {
         changed |= this.put(entry.getKey(), entry.getValue());
      }

      return changed;
   }

   public Collection replaceValues(@Nullable Object key, Iterable values) {
      Preconditions.checkNotNull(values);
      Collection<V> result = this.removeAll(key);
      this.putAll(key, values);
      return result;
   }

   public Collection entries() {
      Collection<Entry<K, V>> result = this.entries;
      return result == null?(this.entries = this.createEntries()):result;
   }

   Collection createEntries() {
      return (Collection)(this instanceof SetMultimap?new AbstractMultimap.EntrySet():new AbstractMultimap.Entries());
   }

   abstract Iterator entryIterator();

   public Set keySet() {
      Set<K> result = this.keySet;
      return result == null?(this.keySet = this.createKeySet()):result;
   }

   Set createKeySet() {
      return new Maps.KeySet(this.asMap());
   }

   public Multiset keys() {
      Multiset<K> result = this.keys;
      return result == null?(this.keys = this.createKeys()):result;
   }

   Multiset createKeys() {
      return new Multimaps.Keys(this);
   }

   public Collection values() {
      Collection<V> result = this.values;
      return result == null?(this.values = this.createValues()):result;
   }

   Collection createValues() {
      return new AbstractMultimap.Values();
   }

   Iterator valueIterator() {
      return Maps.valueIterator(this.entries().iterator());
   }

   public Map asMap() {
      Map<K, Collection<V>> result = this.asMap;
      return result == null?(this.asMap = this.createAsMap()):result;
   }

   abstract Map createAsMap();

   public boolean equals(@Nullable Object object) {
      return Multimaps.equalsImpl(this, object);
   }

   public int hashCode() {
      return this.asMap().hashCode();
   }

   public String toString() {
      return this.asMap().toString();
   }

   private class Entries extends Multimaps.Entries {
      private Entries() {
      }

      Multimap multimap() {
         return AbstractMultimap.this;
      }

      public Iterator iterator() {
         return AbstractMultimap.this.entryIterator();
      }
   }

   private class EntrySet extends AbstractMultimap.Entries implements Set {
      private EntrySet() {
         super(null);
      }

      public int hashCode() {
         return Sets.hashCodeImpl(this);
      }

      public boolean equals(@Nullable Object obj) {
         return Sets.equalsImpl(this, obj);
      }
   }

   class Values extends AbstractCollection {
      public Iterator iterator() {
         return AbstractMultimap.this.valueIterator();
      }

      public int size() {
         return AbstractMultimap.this.size();
      }

      public boolean contains(@Nullable Object o) {
         return AbstractMultimap.this.containsValue(o);
      }

      public void clear() {
         AbstractMultimap.this.clear();
      }
   }
}
