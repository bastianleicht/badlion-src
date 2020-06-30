package com.google.common.util.concurrent;

import com.google.common.util.concurrent.AbstractFuture;
import javax.annotation.Nullable;

public final class SettableFuture extends AbstractFuture {
   public static SettableFuture create() {
      return new SettableFuture();
   }

   public boolean set(@Nullable Object value) {
      return super.set(value);
   }

   public boolean setException(Throwable throwable) {
      return super.setException(throwable);
   }
}
