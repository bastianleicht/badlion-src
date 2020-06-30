package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToInstanceMap;
import com.google.common.reflect.TypeToken;
import java.util.Map;

@Beta
public final class ImmutableTypeToInstanceMap extends ForwardingMap implements TypeToInstanceMap {
   private final ImmutableMap delegate;

   public static ImmutableTypeToInstanceMap of() {
      return new ImmutableTypeToInstanceMap(ImmutableMap.of());
   }

   public static ImmutableTypeToInstanceMap.Builder builder() {
      return new ImmutableTypeToInstanceMap.Builder();
   }

   private ImmutableTypeToInstanceMap(ImmutableMap delegate) {
      this.delegate = delegate;
   }

   public Object getInstance(TypeToken type) {
      return this.trustedGet(type.rejectTypeVariables());
   }

   public Object putInstance(TypeToken type, Object value) {
      throw new UnsupportedOperationException();
   }

   public Object getInstance(Class type) {
      return this.trustedGet(TypeToken.of(type));
   }

   public Object putInstance(Class type, Object value) {
      throw new UnsupportedOperationException();
   }

   protected Map delegate() {
      return this.delegate;
   }

   private Object trustedGet(TypeToken type) {
      return this.delegate.get(type);
   }

   @Beta
   public static final class Builder {
      private final ImmutableMap.Builder mapBuilder;

      private Builder() {
         this.mapBuilder = ImmutableMap.builder();
      }

      public ImmutableTypeToInstanceMap.Builder put(Class key, Object value) {
         this.mapBuilder.put(TypeToken.of(key), value);
         return this;
      }

      public ImmutableTypeToInstanceMap.Builder put(TypeToken key, Object value) {
         this.mapBuilder.put(key.rejectTypeVariables(), value);
         return this;
      }

      public ImmutableTypeToInstanceMap build() {
         return new ImmutableTypeToInstanceMap(this.mapBuilder.build());
      }
   }
}
