package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface Table {
   boolean contains(@Nullable Object var1, @Nullable Object var2);

   boolean containsRow(@Nullable Object var1);

   boolean containsColumn(@Nullable Object var1);

   boolean containsValue(@Nullable Object var1);

   Object get(@Nullable Object var1, @Nullable Object var2);

   boolean isEmpty();

   int size();

   boolean equals(@Nullable Object var1);

   int hashCode();

   void clear();

   Object put(Object var1, Object var2, Object var3);

   void putAll(Table var1);

   Object remove(@Nullable Object var1, @Nullable Object var2);

   Map row(Object var1);

   Map column(Object var1);

   Set cellSet();

   Set rowKeySet();

   Set columnKeySet();

   Collection values();

   Map rowMap();

   Map columnMap();

   public interface Cell {
      Object getRowKey();

      Object getColumnKey();

      Object getValue();

      boolean equals(@Nullable Object var1);

      int hashCode();
   }
}
