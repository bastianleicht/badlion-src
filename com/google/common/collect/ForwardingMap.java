package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.collect.ForwardingObject;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingMap extends ForwardingObject implements Map {
   protected abstract Map delegate();

   public int size() {
      return this.delegate().size();
   }

   public boolean isEmpty() {
      return this.delegate().isEmpty();
   }

   public Object remove(Object object) {
      return this.delegate().remove(object);
   }

   public void clear() {
      this.delegate().clear();
   }

   public boolean containsKey(@Nullable Object key) {
      return this.delegate().containsKey(key);
   }

   public boolean containsValue(@Nullable Object value) {
      return this.delegate().containsValue(value);
   }

   public Object get(@Nullable Object key) {
      return this.delegate().get(key);
   }

   public Object put(Object key, Object value) {
      return this.delegate().put(key, value);
   }

   public void putAll(Map map) {
      this.delegate().putAll(map);
   }

   public Set keySet() {
      return this.delegate().keySet();
   }

   public Collection values() {
      return this.delegate().values();
   }

   public Set entrySet() {
      return this.delegate().entrySet();
   }

   public boolean equals(@Nullable Object object) {
      return object == this || this.delegate().equals(object);
   }

   public int hashCode() {
      return this.delegate().hashCode();
   }

   protected void standardPutAll(Map map) {
      Maps.putAllImpl(this, map);
   }

   @Beta
   protected Object standardRemove(@Nullable Object key) {
      Iterator<Entry<K, V>> entryIterator = this.entrySet().iterator();

      while(entryIterator.hasNext()) {
         Entry<K, V> entry = (Entry)entryIterator.next();
         if(Objects.equal(entry.getKey(), key)) {
            V value = entry.getValue();
            entryIterator.remove();
            return value;
         }
      }

      return null;
   }

   protected void standardClear() {
      Iterators.clear(this.entrySet().iterator());
   }

   @Beta
   protected boolean standardContainsKey(@Nullable Object key) {
      return Maps.containsKeyImpl(this, key);
   }

   protected boolean standardContainsValue(@Nullable Object value) {
      return Maps.containsValueImpl(this, value);
   }

   protected boolean standardIsEmpty() {
      return !this.entrySet().iterator().hasNext();
   }

   protected boolean standardEquals(@Nullable Object object) {
      return Maps.equalsImpl(this, object);
   }

   protected int standardHashCode() {
      return Sets.hashCodeImpl(this.entrySet());
   }

   protected String standardToString() {
      return Maps.toStringImpl(this);
   }

   @Beta
   protected abstract class StandardEntrySet extends Maps.EntrySet {
      Map map() {
         return ForwardingMap.this;
      }
   }

   @Beta
   protected class StandardKeySet extends Maps.KeySet {
      public StandardKeySet() {
         super(ForwardingMap.this);
      }
   }

   @Beta
   protected class StandardValues extends Maps.Values {
      public StandardValues() {
         super(ForwardingMap.this);
      }
   }
}
