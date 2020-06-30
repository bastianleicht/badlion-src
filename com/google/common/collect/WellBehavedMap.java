package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.AbstractMapEntry;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import com.google.common.collect.TransformedIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

@GwtCompatible
final class WellBehavedMap extends ForwardingMap {
   private final Map delegate;
   private Set entrySet;

   private WellBehavedMap(Map delegate) {
      this.delegate = delegate;
   }

   static WellBehavedMap wrap(Map delegate) {
      return new WellBehavedMap(delegate);
   }

   protected Map delegate() {
      return this.delegate;
   }

   public Set entrySet() {
      Set<Entry<K, V>> es = this.entrySet;
      return es != null?es:(this.entrySet = new WellBehavedMap.EntrySet());
   }

   private final class EntrySet extends Maps.EntrySet {
      private EntrySet() {
      }

      Map map() {
         return WellBehavedMap.this;
      }

      public Iterator iterator() {
         return new TransformedIterator(WellBehavedMap.this.keySet().iterator()) {
            Entry transform(final Object key) {
               return new AbstractMapEntry() {
                  public Object getKey() {
                     return key;
                  }

                  public Object getValue() {
                     return WellBehavedMap.this.get(key);
                  }

                  public Object setValue(Object value) {
                     return WellBehavedMap.this.put(key, value);
                  }
               };
            }
         };
      }
   }
}
