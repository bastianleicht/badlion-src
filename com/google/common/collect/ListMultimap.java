package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public interface ListMultimap extends Multimap {
   List get(@Nullable Object var1);

   List removeAll(@Nullable Object var1);

   List replaceValues(Object var1, Iterable var2);

   Map asMap();

   boolean equals(@Nullable Object var1);
}
