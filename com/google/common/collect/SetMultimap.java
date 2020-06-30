package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface SetMultimap extends Multimap {
   Set get(@Nullable Object var1);

   Set removeAll(@Nullable Object var1);

   Set replaceValues(Object var1, Iterable var2);

   Set entries();

   Map asMap();

   boolean equals(@Nullable Object var1);
}
