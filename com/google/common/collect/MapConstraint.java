package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
@Beta
public interface MapConstraint {
   void checkKeyValue(@Nullable Object var1, @Nullable Object var2);

   String toString();
}
