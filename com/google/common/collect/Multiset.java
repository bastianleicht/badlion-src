package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface Multiset extends Collection {
   int count(@Nullable Object var1);

   int add(@Nullable Object var1, int var2);

   int remove(@Nullable Object var1, int var2);

   int setCount(Object var1, int var2);

   boolean setCount(Object var1, int var2, int var3);

   Set elementSet();

   Set entrySet();

   boolean equals(@Nullable Object var1);

   int hashCode();

   String toString();

   Iterator iterator();

   boolean contains(@Nullable Object var1);

   boolean containsAll(Collection var1);

   boolean add(Object var1);

   boolean remove(@Nullable Object var1);

   boolean removeAll(Collection var1);

   boolean retainAll(Collection var1);

   public interface Entry {
      Object getElement();

      int getCount();

      boolean equals(Object var1);

      int hashCode();

      String toString();
   }
}
