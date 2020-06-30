package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;

@GwtCompatible
interface FilteredMultimap extends Multimap {
   Multimap unfiltered();

   Predicate entryPredicate();
}
