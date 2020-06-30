package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.AbstractMapBasedMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractListMultimap extends AbstractMapBasedMultimap implements ListMultimap {
   private static final long serialVersionUID = 6588350623831699109L;

   protected AbstractListMultimap(Map map) {
      super(map);
   }

   abstract List createCollection();

   List createUnmodifiableEmptyCollection() {
      return ImmutableList.of();
   }

   public List get(@Nullable Object key) {
      return (List)super.get(key);
   }

   public List removeAll(@Nullable Object key) {
      return (List)super.removeAll(key);
   }

   public List replaceValues(@Nullable Object key, Iterable values) {
      return (List)super.replaceValues(key, values);
   }

   public boolean put(@Nullable Object key, @Nullable Object value) {
      return super.put(key, value);
   }

   public Map asMap() {
      return super.asMap();
   }

   public boolean equals(@Nullable Object object) {
      return super.equals(object);
   }
}
