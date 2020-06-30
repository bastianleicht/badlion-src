package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.reflect.TypeToken;
import java.util.Map;
import javax.annotation.Nullable;

@Beta
public interface TypeToInstanceMap extends Map {
   @Nullable
   Object getInstance(Class var1);

   @Nullable
   Object putInstance(Class var1, @Nullable Object var2);

   @Nullable
   Object getInstance(TypeToken var1);

   @Nullable
   Object putInstance(TypeToken var1, @Nullable Object var2);
}
