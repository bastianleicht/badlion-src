package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
public interface Function {
   @Nullable
   Object apply(@Nullable Object var1);

   boolean equals(@Nullable Object var1);
}
