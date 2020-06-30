package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ForwardingMapEntry;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
abstract class AbstractBiMap extends ForwardingMap implements BiMap, Serializable {
   private transient Map delegate;
   transient AbstractBiMap inverse;
   private transient Set keySet;
   private transient Set valueSet;
   private transient Set entrySet;
   @GwtIncompatible("Not needed in emulated source.")
   private static final long serialVersionUID = 0L;

   AbstractBiMap(Map forward, Map backward) {
      this.setDelegates(forward, backward);
   }

   private AbstractBiMap(Map backward, AbstractBiMap forward) {
      this.delegate = backward;
      this.inverse = forward;
   }

   protected Map delegate() {
      return this.delegate;
   }

   Object checkKey(@Nullable Object key) {
      return key;
   }

   Object checkValue(@Nullable Object value) {
      return value;
   }

   void setDelegates(Map forward, Map backward) {
      Preconditions.checkState(this.delegate == null);
      Preconditions.checkState(this.inverse == null);
      Preconditions.checkArgument(forward.isEmpty());
      Preconditions.checkArgument(backward.isEmpty());
      Preconditions.checkArgument(forward != backward);
      this.delegate = forward;
      this.inverse = new AbstractBiMap.Inverse(backward, this);
   }

   void setInverse(AbstractBiMap inverse) {
      this.inverse = inverse;
   }

   public boolean containsValue(@Nullable Object value) {
      return this.inverse.containsKey(value);
   }

   public Object put(@Nullable Object key, @Nullable Object value) {
      return this.putInBothMaps(key, value, false);
   }

   public Object forcePut(@Nullable Object key, @Nullable Object value) {
      return this.putInBothMaps(key, value, true);
   }

   private Object putInBothMaps(@Nullable Object key, @Nullable Object value, boolean force) {
      this.checkKey(key);
      this.checkValue(value);
      boolean containedKey = this.containsKey(key);
      if(containedKey && Objects.equal(value, this.get(key))) {
         return value;
      } else {
         if(force) {
            this.inverse().remove(value);
         } else {
            Preconditions.checkArgument(!this.containsValue(value), "value already present: %s", new Object[]{value});
         }

         V oldValue = this.delegate.put(key, value);
         this.updateInverseMap(key, containedKey, oldValue, value);
         return oldValue;
      }
   }

   private void updateInverseMap(Object key, boolean containedKey, Object oldValue, Object newValue) {
      if(containedKey) {
         this.removeFromInverseMap(oldValue);
      }

      this.inverse.delegate.put(newValue, key);
   }

   public Object remove(@Nullable Object key) {
      return this.containsKey(key)?this.removeFromBothMaps(key):null;
   }

   private Object removeFromBothMaps(Object key) {
      V oldValue = this.delegate.remove(key);
      this.removeFromInverseMap(oldValue);
      return oldValue;
   }

   private void removeFromInverseMap(Object oldValue) {
      this.inverse.delegate.remove(oldValue);
   }

   public void putAll(Map map) {
      for(Entry<? extends K, ? extends V> entry : map.entrySet()) {
         this.put(entry.getKey(), entry.getValue());
      }

   }

   public void clear() {
      this.delegate.clear();
      this.inverse.delegate.clear();
   }

   public BiMap inverse() {
      return this.inverse;
   }

   public Set keySet() {
      Set<K> result = this.keySet;
      return result == null?(this.keySet = new AbstractBiMap.KeySet()):result;
   }

   public Set values() {
      Set<V> result = this.valueSet;
      return result == null?(this.valueSet = new AbstractBiMap.ValueSet()):result;
   }

   public Set entrySet() {
      Set<Entry<K, V>> result = this.entrySet;
      return result == null?(this.entrySet = new AbstractBiMap.EntrySet()):result;
   }

   private class EntrySet extends ForwardingSet {
      final Set esDelegate;

      private EntrySet() {
         this.esDelegate = AbstractBiMap.this.delegate.entrySet();
      }

      protected Set delegate() {
         return this.esDelegate;
      }

      public void clear() {
         AbstractBiMap.this.clear();
      }

      public boolean remove(Object object) {
         if(!this.esDelegate.contains(object)) {
            return false;
         } else {
            Entry<?, ?> entry = (Entry)object;
            AbstractBiMap.this.inverse.delegate.remove(entry.getValue());
            this.esDelegate.remove(entry);
            return true;
         }
      }

      public Iterator iterator() {
         final Iterator<Entry<K, V>> iterator = this.esDelegate.iterator();
         return new Iterator() {
            Entry entry;

            public boolean hasNext() {
               return iterator.hasNext();
            }

            public Entry next() {
               this.entry = (Entry)iterator.next();
               final Entry<K, V> finalEntry = this.entry;
               return new ForwardingMapEntry() {
                  protected Entry delegate() {
                     return finalEntry;
                  }

                  public Object setValue(Object value) {
                     Preconditions.checkState(EntrySet.this.contains(this), "entry no longer in map");
                     if(Objects.equal(value, this.getValue())) {
                        return value;
                     } else {
                        Preconditions.checkArgument(!AbstractBiMap.this.containsValue(value), "value already present: %s", new Object[]{value});
                        V oldValue = finalEntry.setValue(value);
                        Preconditions.checkState(Objects.equal(value, AbstractBiMap.this.get(this.getKey())), "entry no longer in map");
                        AbstractBiMap.this.updateInverseMap(this.getKey(), true, oldValue, value);
                        return oldValue;
                     }
                  }
               };
            }

            public void remove() {
               CollectPreconditions.checkRemove(this.entry != null);
               V value = this.entry.getValue();
               iterator.remove();
               AbstractBiMap.this.removeFromInverseMap(value);
            }
         };
      }

      public Object[] toArray() {
         return this.standardToArray();
      }

      public Object[] toArray(Object[] array) {
         return this.standardToArray(array);
      }

      public boolean contains(Object o) {
         return Maps.containsEntryImpl(this.delegate(), o);
      }

      public boolean containsAll(Collection c) {
         return this.standardContainsAll(c);
      }

      public boolean removeAll(Collection c) {
         return this.standardRemoveAll(c);
      }

      public boolean retainAll(Collection c) {
         return this.standardRetainAll(c);
      }
   }

   private static class Inverse extends AbstractBiMap {
      @GwtIncompatible("Not needed in emulated source.")
      private static final long serialVersionUID = 0L;

      private Inverse(Map backward, AbstractBiMap forward) {
         super(backward, forward, null);
      }

      Object checkKey(Object key) {
         return this.inverse.checkValue(key);
      }

      Object checkValue(Object value) {
         return this.inverse.checkKey(value);
      }

      @GwtIncompatible("java.io.ObjectOuputStream")
      private void writeObject(ObjectOutputStream stream) throws IOException {
         stream.defaultWriteObject();
         stream.writeObject(this.inverse());
      }

      @GwtIncompatible("java.io.ObjectInputStream")
      private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
         stream.defaultReadObject();
         this.setInverse((AbstractBiMap)stream.readObject());
      }

      @GwtIncompatible("Not needed in the emulated source.")
      Object readResolve() {
         return this.inverse().inverse();
      }
   }

   private class KeySet extends ForwardingSet {
      private KeySet() {
      }

      protected Set delegate() {
         return AbstractBiMap.this.delegate.keySet();
      }

      public void clear() {
         AbstractBiMap.this.clear();
      }

      public boolean remove(Object key) {
         if(!this.contains(key)) {
            return false;
         } else {
            AbstractBiMap.this.removeFromBothMaps(key);
            return true;
         }
      }

      public boolean removeAll(Collection keysToRemove) {
         return this.standardRemoveAll(keysToRemove);
      }

      public boolean retainAll(Collection keysToRetain) {
         return this.standardRetainAll(keysToRetain);
      }

      public Iterator iterator() {
         return Maps.keyIterator(AbstractBiMap.this.entrySet().iterator());
      }
   }

   private class ValueSet extends ForwardingSet {
      final Set valuesDelegate;

      private ValueSet() {
         this.valuesDelegate = AbstractBiMap.this.inverse.keySet();
      }

      protected Set delegate() {
         return this.valuesDelegate;
      }

      public Iterator iterator() {
         return Maps.valueIterator(AbstractBiMap.this.entrySet().iterator());
      }

      public Object[] toArray() {
         return this.standardToArray();
      }

      public Object[] toArray(Object[] array) {
         return this.standardToArray(array);
      }

      public String toString() {
         return this.standardToString();
      }
   }
}
