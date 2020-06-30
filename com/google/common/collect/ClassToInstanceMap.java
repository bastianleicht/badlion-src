package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public interface ClassToInstanceMap extends Map {
   Object getInstance(Class var1);

   Object putInstance(Class var1, @Nullable Object var2);
}
