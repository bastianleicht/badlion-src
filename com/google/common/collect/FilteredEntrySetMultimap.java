package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import com.google.common.collect.FilteredEntryMultimap;
import com.google.common.collect.FilteredSetMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.util.Set;

@GwtCompatible
final class FilteredEntrySetMultimap extends FilteredEntryMultimap implements FilteredSetMultimap {
   FilteredEntrySetMultimap(SetMultimap unfiltered, Predicate predicate) {
      super(unfiltered, predicate);
   }

   public SetMultimap unfiltered() {
      return (SetMultimap)this.unfiltered;
   }

   public Set get(Object key) {
      return (Set)super.get(key);
   }

   public Set removeAll(Object key) {
      return (Set)super.removeAll(key);
   }

   public Set replaceValues(Object key, Iterable values) {
      return (Set)super.replaceValues(key, values);
   }

   Set createEntries() {
      return Sets.filter(this.unfiltered().entries(), this.entryPredicate());
   }

   public Set entries() {
      return (Set)super.entries();
   }
}
