package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.FilteredMultimap;
import com.google.common.collect.SetMultimap;

@GwtCompatible
interface FilteredSetMultimap extends FilteredMultimap, SetMultimap {
   SetMultimap unfiltered();
}
