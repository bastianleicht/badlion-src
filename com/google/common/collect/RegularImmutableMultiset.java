package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
class RegularImmutableMultiset extends ImmutableMultiset {
   private final transient ImmutableMap map;
   private final transient int size;

   RegularImmutableMultiset(ImmutableMap map, int size) {
      this.map = map;
      this.size = size;
   }

   boolean isPartialView() {
      return this.map.isPartialView();
   }

   public int count(@Nullable Object element) {
      Integer value = (Integer)this.map.get(element);
      return value == null?0:value.intValue();
   }

   public int size() {
      return this.size;
   }

   public boolean contains(@Nullable Object element) {
      return this.map.containsKey(element);
   }

   public ImmutableSet elementSet() {
      return this.map.keySet();
   }

   Multiset.Entry getEntry(int index) {
      Entry<E, Integer> mapEntry = (Entry)this.map.entrySet().asList().get(index);
      return Multisets.immutableEntry(mapEntry.getKey(), ((Integer)mapEntry.getValue()).intValue());
   }

   public int hashCode() {
      return this.map.hashCode();
   }
}
