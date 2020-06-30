package io.netty.handler.codec.serialization;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

abstract class ReferenceMap implements Map {
   private final Map delegate;

   protected ReferenceMap(Map delegate) {
      this.delegate = delegate;
   }

   abstract Reference fold(Object var1);

   private Object unfold(Reference ref) {
      return ref == null?null:ref.get();
   }

   public int size() {
      return this.delegate.size();
   }

   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   public boolean containsKey(Object key) {
      return this.delegate.containsKey(key);
   }

   public boolean containsValue(Object value) {
      throw new UnsupportedOperationException();
   }

   public Object get(Object key) {
      return this.unfold((Reference)this.delegate.get(key));
   }

   public Object put(Object key, Object value) {
      return this.unfold((Reference)this.delegate.put(key, this.fold(value)));
   }

   public Object remove(Object key) {
      return this.unfold((Reference)this.delegate.remove(key));
   }

   public void putAll(Map m) {
      for(Entry<? extends K, ? extends V> entry : m.entrySet()) {
         this.delegate.put(entry.getKey(), this.fold(entry.getValue()));
      }

   }

   public void clear() {
      this.delegate.clear();
   }

   public Set keySet() {
      return this.delegate.keySet();
   }

   public Collection values() {
      throw new UnsupportedOperationException();
   }

   public Set entrySet() {
      throw new UnsupportedOperationException();
   }
}
