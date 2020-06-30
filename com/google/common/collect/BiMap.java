package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface BiMap extends Map {
   Object put(@Nullable Object var1, @Nullable Object var2);

   Object forcePut(@Nullable Object var1, @Nullable Object var2);

   void putAll(Map var1);

   Set values();

   BiMap inverse();
}
