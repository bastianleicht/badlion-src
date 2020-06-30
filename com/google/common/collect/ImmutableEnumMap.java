package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMapEntrySet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
final class ImmutableEnumMap extends ImmutableMap {
   private final transient EnumMap delegate;

   static ImmutableMap asImmutable(EnumMap map) {
      switch(map.size()) {
      case 0:
         return ImmutableMap.of();
      case 1:
         Entry<K, V> entry = (Entry)Iterables.getOnlyElement(map.entrySet());
         return ImmutableMap.of(entry.getKey(), entry.getValue());
      default:
         return new ImmutableEnumMap(map);
      }
   }

   private ImmutableEnumMap(EnumMap delegate) {
      this.delegate = delegate;
      Preconditions.checkArgument(!delegate.isEmpty());
   }

   ImmutableSet createKeySet() {
      return new ImmutableSet() {
         public boolean contains(Object object) {
            return ImmutableEnumMap.this.delegate.containsKey(object);
         }

         public int size() {
            return ImmutableEnumMap.this.size();
         }

         public UnmodifiableIterator iterator() {
            return Iterators.unmodifiableIterator(ImmutableEnumMap.this.delegate.keySet().iterator());
         }

         boolean isPartialView() {
            return true;
         }
      };
   }

   public int size() {
      return this.delegate.size();
   }

   public boolean containsKey(@Nullable Object key) {
      return this.delegate.containsKey(key);
   }

   public Object get(Object key) {
      return this.delegate.get(key);
   }

   ImmutableSet createEntrySet() {
      return new ImmutableMapEntrySet() {
         ImmutableMap map() {
            return ImmutableEnumMap.this;
         }

         public UnmodifiableIterator iterator() {
            return new UnmodifiableIterator() {
               private final Iterator backingIterator;

               {
                  this.backingIterator = ImmutableEnumMap.this.delegate.entrySet().iterator();
               }

               public boolean hasNext() {
                  return this.backingIterator.hasNext();
               }

               public Entry next() {
                  Entry<K, V> entry = (Entry)this.backingIterator.next();
                  return Maps.immutableEntry(entry.getKey(), entry.getValue());
               }
            };
         }
      };
   }

   boolean isPartialView() {
      return false;
   }

   Object writeReplace() {
      return new ImmutableEnumMap.EnumSerializedForm(this.delegate);
   }

   private static class EnumSerializedForm implements Serializable {
      final EnumMap delegate;
      private static final long serialVersionUID = 0L;

      EnumSerializedForm(EnumMap delegate) {
         this.delegate = delegate;
      }

      Object readResolve() {
         return new ImmutableEnumMap(this.delegate);
      }
   }
}
