package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Primitives;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public final class ImmutableClassToInstanceMap extends ForwardingMap implements ClassToInstanceMap, Serializable {
   private final ImmutableMap delegate;

   public static ImmutableClassToInstanceMap.Builder builder() {
      return new ImmutableClassToInstanceMap.Builder();
   }

   public static ImmutableClassToInstanceMap copyOf(Map map) {
      if(map instanceof ImmutableClassToInstanceMap) {
         ImmutableClassToInstanceMap<B> cast = (ImmutableClassToInstanceMap)map;
         return cast;
      } else {
         return (new ImmutableClassToInstanceMap.Builder()).putAll(map).build();
      }
   }

   private ImmutableClassToInstanceMap(ImmutableMap delegate) {
      this.delegate = delegate;
   }

   protected Map delegate() {
      return this.delegate;
   }

   @Nullable
   public Object getInstance(Class type) {
      return this.delegate.get(Preconditions.checkNotNull(type));
   }

   /** @deprecated */
   @Deprecated
   public Object putInstance(Class type, Object value) {
      throw new UnsupportedOperationException();
   }

   public static final class Builder {
      private final ImmutableMap.Builder mapBuilder = ImmutableMap.builder();

      public ImmutableClassToInstanceMap.Builder put(Class key, Object value) {
         this.mapBuilder.put(key, value);
         return this;
      }

      public ImmutableClassToInstanceMap.Builder putAll(Map map) {
         for(Entry<? extends Class<? extends T>, ? extends T> entry : map.entrySet()) {
            Class<? extends T> type = (Class)entry.getKey();
            T value = entry.getValue();
            this.mapBuilder.put(type, cast(type, value));
         }

         return this;
      }

      private static Object cast(Class type, Object value) {
         return Primitives.wrap(type).cast(value);
      }

      public ImmutableClassToInstanceMap build() {
         return new ImmutableClassToInstanceMap(this.mapBuilder.build());
      }
   }
}
