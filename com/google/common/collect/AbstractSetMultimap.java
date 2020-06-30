package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.AbstractMapBasedMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractSetMultimap extends AbstractMapBasedMultimap implements SetMultimap {
   private static final long serialVersionUID = 7431625294878419160L;

   protected AbstractSetMultimap(Map map) {
      super(map);
   }

   abstract Set createCollection();

   Set createUnmodifiableEmptyCollection() {
      return ImmutableSet.of();
   }

   public Set get(@Nullable Object key) {
      return (Set)super.get(key);
   }

   public Set entries() {
      return (Set)super.entries();
   }

   public Set removeAll(@Nullable Object key) {
      return (Set)super.removeAll(key);
   }

   public Set replaceValues(@Nullable Object key, Iterable values) {
      return (Set)super.replaceValues(key, values);
   }

   public Map asMap() {
      return super.asMap();
   }

   public boolean put(@Nullable Object key, @Nullable Object value) {
      return super.put(key, value);
   }

   public boolean equals(@Nullable Object object) {
      return super.equals(object);
   }
}
