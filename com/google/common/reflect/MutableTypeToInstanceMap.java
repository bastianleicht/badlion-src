package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ForwardingMapEntry;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToInstanceMap;
import com.google.common.reflect.TypeToken;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@Beta
public final class MutableTypeToInstanceMap extends ForwardingMap implements TypeToInstanceMap {
   private final Map backingMap = Maps.newHashMap();

   @Nullable
   public Object getInstance(Class type) {
      return this.trustedGet(TypeToken.of(type));
   }

   @Nullable
   public Object putInstance(Class type, @Nullable Object value) {
      return this.trustedPut(TypeToken.of(type), value);
   }

   @Nullable
   public Object getInstance(TypeToken type) {
      return this.trustedGet(type.rejectTypeVariables());
   }

   @Nullable
   public Object putInstance(TypeToken type, @Nullable Object value) {
      return this.trustedPut(type.rejectTypeVariables(), value);
   }

   public Object put(TypeToken key, Object value) {
      throw new UnsupportedOperationException("Please use putInstance() instead.");
   }

   public void putAll(Map map) {
      throw new UnsupportedOperationException("Please use putInstance() instead.");
   }

   public Set entrySet() {
      return MutableTypeToInstanceMap.UnmodifiableEntry.transformEntries(super.entrySet());
   }

   protected Map delegate() {
      return this.backingMap;
   }

   @Nullable
   private Object trustedPut(TypeToken type, @Nullable Object value) {
      return this.backingMap.put(type, value);
   }

   @Nullable
   private Object trustedGet(TypeToken type) {
      return this.backingMap.get(type);
   }

   private static final class UnmodifiableEntry extends ForwardingMapEntry {
      private final Entry delegate;

      static Set transformEntries(final Set entries) {
         return new ForwardingSet() {
            protected Set delegate() {
               return entries;
            }

            public Iterator iterator() {
               return MutableTypeToInstanceMap.UnmodifiableEntry.transformEntries(super.iterator());
            }

            public Object[] toArray() {
               return this.standardToArray();
            }

            public Object[] toArray(Object[] array) {
               return this.standardToArray(array);
            }
         };
      }

      private static Iterator transformEntries(Iterator entries) {
         return Iterators.transform(entries, new Function() {
            public Entry apply(Entry entry) {
               return new MutableTypeToInstanceMap.UnmodifiableEntry(entry);
            }
         });
      }

      private UnmodifiableEntry(Entry delegate) {
         this.delegate = (Entry)Preconditions.checkNotNull(delegate);
      }

      protected Entry delegate() {
         return this.delegate;
      }

      public Object setValue(Object value) {
         throw new UnsupportedOperationException();
      }
   }
}
