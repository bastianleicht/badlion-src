package com.google.common.util.concurrent;

import javax.annotation.Nullable;

public interface FutureCallback {
   void onSuccess(@Nullable Object var1);

   void onFailure(Throwable var1);
}
