package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import com.google.common.collect.FilteredKeyMultimap;
import com.google.common.collect.ListMultimap;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
final class FilteredKeyListMultimap extends FilteredKeyMultimap implements ListMultimap {
   FilteredKeyListMultimap(ListMultimap unfiltered, Predicate keyPredicate) {
      super(unfiltered, keyPredicate);
   }

   public ListMultimap unfiltered() {
      return (ListMultimap)super.unfiltered();
   }

   public List get(Object key) {
      return (List)super.get(key);
   }

   public List removeAll(@Nullable Object key) {
      return (List)super.removeAll(key);
   }

   public List replaceValues(Object key, Iterable values) {
      return (List)super.replaceValues(key, values);
   }
}
