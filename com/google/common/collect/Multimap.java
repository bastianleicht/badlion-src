package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Multiset;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface Multimap {
   int size();

   boolean isEmpty();

   boolean containsKey(@Nullable Object var1);

   boolean containsValue(@Nullable Object var1);

   boolean containsEntry(@Nullable Object var1, @Nullable Object var2);

   boolean put(@Nullable Object var1, @Nullable Object var2);

   boolean remove(@Nullable Object var1, @Nullable Object var2);

   boolean putAll(@Nullable Object var1, Iterable var2);

   boolean putAll(Multimap var1);

   Collection replaceValues(@Nullable Object var1, Iterable var2);

   Collection removeAll(@Nullable Object var1);

   void clear();

   Collection get(@Nullable Object var1);

   Set keySet();

   Multiset keys();

   Collection values();

   Collection entries();

   Map asMap();

   boolean equals(@Nullable Object var1);

   int hashCode();
}
